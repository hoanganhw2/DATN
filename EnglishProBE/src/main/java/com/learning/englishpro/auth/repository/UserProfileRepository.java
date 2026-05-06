package com.learning.englishpro.auth.repository;

import com.learning.englishpro.auth.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
