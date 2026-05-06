package com.learning.englishpro.flashcard.repository;

import com.learning.englishpro.flashcard.entity.UserDeckProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeckProgressRepository extends JpaRepository<UserDeckProgress, UserDeckProgress.UserDeckProgressId> {
}
