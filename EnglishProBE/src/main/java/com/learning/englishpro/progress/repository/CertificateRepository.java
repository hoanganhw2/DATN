package com.learning.englishpro.progress.repository;

import com.learning.englishpro.progress.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    /** Kiểm tra đã cấp chứng chỉ cho user + course chưa */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /** Lấy chứng chỉ theo user + course */
    Optional<Certificate> findByUserIdAndCourseId(Long userId, Long courseId);

    /** Lấy chứng chỉ theo mã tra cứu */
    Optional<Certificate> findByCertificateCode(String certificateCode);

    /** Lấy tất cả chứng chỉ của 1 user */
    @Query("SELECT c FROM Certificate c JOIN FETCH c.course WHERE c.user.id = :userId ORDER BY c.issuedAt DESC")
    List<Certificate> findAllByUserId(@Param("userId") Long userId);
}
