package com.learning.englishpro.coupon.repository;

import com.learning.englishpro.coupon.entity.Coupon;
import com.learning.englishpro.coupon.entity.CouponScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    /** Admin xem tất cả + tìm kiếm theo code + filter scope */
    @Query("""
        SELECT c FROM Coupon c
        WHERE (:keyword IS NULL OR UPPER(c.code) LIKE UPPER(CONCAT('%', :keyword, '%')))
          AND (:scope   IS NULL OR c.scope = :scope)
        """)
    Page<Coupon> searchAll(
            @Param("keyword") String keyword,
            @Param("scope")   CouponScope scope,
            Pageable pageable);

    /** Teacher xem coupon của mình + tìm kiếm theo code */
    @Query("""
        SELECT c FROM Coupon c
        WHERE c.createdBy.id = :createdById
          AND (:keyword IS NULL OR UPPER(c.code) LIKE UPPER(CONCAT('%', :keyword, '%')))
        """)
    Page<Coupon> searchByCreatedById(
            @Param("createdById") Long createdById,
            @Param("keyword")     String keyword,
            Pageable pageable);
}
