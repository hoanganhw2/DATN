package com.learning.englishpro.auth.service;

import com.learning.englishpro.auth.dto.request.LoginRequest;
import com.learning.englishpro.auth.dto.request.RefreshTokenRequest;
import com.learning.englishpro.auth.dto.request.RegisterRequest;
import com.learning.englishpro.auth.dto.response.AuthResponse;
import com.learning.englishpro.auth.dto.response.UserResponse;
import com.learning.englishpro.auth.entity.RefreshToken;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.jwt.JwtUtil;
import com.learning.englishpro.auth.repository.RefreshTokenRepository;
import com.learning.englishpro.auth.repository.UserProfileRepository;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository         userRepository;
    private final UserProfileRepository  userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;
    private final AuthenticationManager  authenticationManager;

    @Value("${jwt.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    // ── Register ────────────────────────────────────────────────────────────

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                // role = STUDENT, status = ACTIVE are set by @Builder.Default in User entity
                .build();

        userRepository.save(user);

        // Tạo UserProfile chứa thông tin cá nhân và liên kết với User
        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.computeFullName())
                .phone(request.phone())
                .gender(request.gender())
                .address(request.address())
                .build();
                
        userProfileRepository.save(profile);
        user.setProfile(profile);

        log.info("Người dùng mới đăng ký: {} (id={})", user.getEmail(), user.getId());

        return UserResponse.from(user);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        User user = userRepository.findByEmail(request.email())
                .or(() -> userRepository.findByUsername(request.email()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Update last login time
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Revoke all existing refresh tokens for this user (single-session policy)
        refreshTokenRepository.revokeAllByUser(user);

        return issueTokenPair(user);
    }

    // ── Refresh Token ────────────────────────────────────────────────────────

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        if (stored.isRevoked()) throw new AppException(ErrorCode.TOKEN_REVOKED);
        if (stored.isExpired()) throw new AppException(ErrorCode.TOKEN_EXPIRED);

        // Rotate: revoke old, issue new pair
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokenPair(stored.getUser());
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByToken(rawRefreshToken).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private AuthResponse issueTokenPair(User user) {
        String accessToken  = jwtUtil.generateAccessToken(user);
        String refreshToken = createAndSaveRefreshToken(user);

        String fullName = (user.getProfile() != null && user.getProfile().getFullName() != null) 
                            ? user.getProfile().getFullName() 
                            : "";
                            
        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessTokenExpirationMs / 1000,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                fullName,
                user.getRole()
        );
    }

    private String createAndSaveRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();
        refreshTokenRepository.save(rt);
        return token;
    }
}
