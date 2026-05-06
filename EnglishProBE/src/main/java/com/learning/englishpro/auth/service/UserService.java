package com.learning.englishpro.auth.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserGender;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.auth.dto.request.UpdateProfileRequest;
import com.learning.englishpro.auth.dto.request.UpdateUserAdminRequest;
import com.learning.englishpro.auth.dto.response.UserDetailResponse;
import com.learning.englishpro.auth.dto.response.UserResponse;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.learning.englishpro.common.service.FileStorageService;
import org.apache.tika.Tika;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // ── Current user helpers ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserDetailResponse getMe(UserDetails principal) {
        return UserDetailResponse.from(resolveUserWithProfile(principal));
    }

    public UserDetailResponse updateMe(UserDetails principal, UpdateProfileRequest request) {
        User user = resolveUserWithProfile(principal);
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
            user.setProfile(profile);
        }

        if (request.fullName() != null)
            profile.setFullName(request.fullName());
        if (request.phone() != null)
            profile.setPhone(request.phone());
        if (request.gender() != null) {
            try {
                profile.setGender(UserGender.valueOf(request.gender().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (request.address() != null)
            profile.setAddress(request.address());
        if (request.dateOfBirth() != null)
            profile.setDateOfBirth(request.dateOfBirth());
        if (request.country() != null)
            profile.setCountry(request.country());
        if (request.learningGoal() != null)
            profile.setLearningGoal(request.learningGoal());

        return UserDetailResponse.from(userRepository.save(user));
    }

    public String uploadAvatar(UserDetails principal, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType) && !"image/webp".equals(mimeType)) {
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }

            String ext = "";
            switch (mimeType) {
                case "image/jpeg":
                    ext = ".jpg";
                    break;
                case "image/png":
                    ext = ".png";
                    break;
                case "image/webp":
                    ext = ".webp";
                    break;
            }

            User user = resolveUserWithProfile(principal);
            UserProfile profile = user.getProfile();
            if (profile == null) {
                profile = UserProfile.builder().user(user).build();
                user.setProfile(profile);
            }

            String oldAvatar = profile.getAvatarUrl();

            String fileName = user.getId() + "_" + System.currentTimeMillis() + ext;
            String newAvatarUrl = fileStorageService.store(file, "avatars", fileName);

            profile.setAvatarUrl(newAvatarUrl);
            userRepository.save(user);

            if (oldAvatar != null && !oldAvatar.isBlank()) {
                fileStorageService.delete(oldAvatar);
            }
            return newAvatarUrl;
        } catch (java.io.IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // ── Admin CRUD ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.searchUsers(keyword.trim(), pageable).map(UserResponse::from);
        }
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public com.learning.englishpro.auth.dto.response.UserStatsResponse getUserStats() {
        long totalUsers = userRepository.count();
        long students = userRepository.countByRole(com.learning.englishpro.auth.entity.Role.STUDENT);
        long teachers = userRepository.countByRole(com.learning.englishpro.auth.entity.Role.TEACHER);
        long admins = userRepository.countByRole(com.learning.englishpro.auth.entity.Role.ADMIN);
        return com.learning.englishpro.auth.dto.response.UserStatsResponse.builder()
                .totalUsers(totalUsers)
                .students(students)
                .teachers(teachers)
                .admins(admins)
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findById(id));
    }

    public UserResponse adminUpdateUser(UserDetails principal, Long id, UpdateUserAdminRequest request) {
        User currentUser = resolveUser(principal);
        if (currentUser.getId().equals(id)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        User user = findById(id);
        user.setRole(request.role());
        user.setStatus(request.status());
        return UserResponse.from(userRepository.save(user));
    }

    public void deleteUser(UserDetails principal, Long id) {
        User currentUser = resolveUser(principal);
        if (currentUser.getId().equals(id)) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private User resolveUserWithProfile(UserDetails principal) {
        return userRepository.findByUsernameWithProfile(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
