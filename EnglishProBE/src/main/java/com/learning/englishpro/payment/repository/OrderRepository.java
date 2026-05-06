package com.learning.englishpro.payment.repository;

import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Checks whether a COMPLETED order exists for the given user and course.
     */
    boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, OrderStatus status);

    Optional<Order> findByOrderCode(String orderCode);

    org.springframework.data.domain.Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Order> findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);

    /**
     * Eagerly fetches the Order with its user, profile, and course in a single query.
     * Used by async email threads where the Hibernate session is already closed.
     */
    @Query("SELECT o FROM Order o " +
           "JOIN FETCH o.user u " +
           "LEFT JOIN FETCH u.profile " +
           "JOIN FETCH o.course " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    // ─── Analytics queries ────────────────────────────────────────────────────

    /**
     * Doanh thu theo tháng trong năm chỉ định.
     * Returns: [month(int), totalRevenue(BigDecimal), orderCount(Long)]
     */
    @Query("SELECT FUNCTION('MONTH', o.createdAt), " +
           "       SUM(o.finalAmount), " +
           "       COUNT(o), " +
           "       SUM(o.teacherRevenue) " +
           "FROM Order o " +
           "WHERE o.status = 'COMPLETED' " +
           "  AND FUNCTION('YEAR', o.createdAt) = :year " +
           "GROUP BY FUNCTION('MONTH', o.createdAt) " +
           "ORDER BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> revenueByMonth(@Param("year") int year);

    /**
     * Top N khóa học doanh thu cao nhất.
     * Returns: [courseId(Long), courseTitle(String), revenue(BigDecimal), orderCount(Long)]
     */
    @Query("SELECT o.course.id, o.course.title, SUM(o.finalAmount), COUNT(o), SUM(o.teacherRevenue) " +
           "FROM Order o " +
           "WHERE o.status = 'COMPLETED' " +
           "GROUP BY o.course.id, o.course.title " +
           "ORDER BY SUM(o.finalAmount) DESC")
    List<Object[]> topCoursesByRevenue(org.springframework.data.domain.Pageable pageable);

    /**
     * Tổng doanh thu, tổng đơn, đơn hoàn thành.
     * Returns a single-row list: [totalOrders, totalRevenue, completedOrders, pendingOrders, failedOrders]
     */
    @Query("SELECT COUNT(o), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN o.finalAmount ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'PENDING'   THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'FAILED'    THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN o.teacherRevenue ELSE 0 END) " +
           "FROM Order o")
    List<Object[]> overallStats();

    /**
     * Doanh thu theo trạng thái đơn hàng.
     */
    @Query("SELECT o.status, COUNT(o), SUM(o.finalAmount) " +
           "FROM Order o " +
           "GROUP BY o.status")
    List<Object[]> ordersByStatus();

    // ─── Teacher-scoped queries ───────────────────────────────────────────────

    /**
     * Overall stats cho một teacher cụ thể (chỉ khóa học của họ).
     * Returns: [totalOrders, totalRevenue, completedOrders, pendingOrders, failedOrders]
     */
    @Query("SELECT COUNT(o), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN o.finalAmount ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'PENDING'   THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'FAILED'    THEN 1 ELSE 0 END), " +
           "       SUM(CASE WHEN o.status = 'COMPLETED' THEN o.teacherRevenue ELSE 0 END) " +
           "FROM Order o WHERE o.course.teacher.id = :teacherId")
    List<Object[]> overallStatsByTeacher(@Param("teacherId") Long teacherId);

    /**
     * Doanh thu theo tháng cho một teacher.
     */
    @Query("SELECT FUNCTION('MONTH', o.createdAt), SUM(o.finalAmount), COUNT(o), SUM(o.teacherRevenue) " +
           "FROM Order o " +
           "WHERE o.status = 'COMPLETED' " +
           "  AND FUNCTION('YEAR', o.createdAt) = :year " +
           "  AND o.course.teacher.id = :teacherId " +
           "GROUP BY FUNCTION('MONTH', o.createdAt) " +
           "ORDER BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> revenueByMonthForTeacher(@Param("year") int year, @Param("teacherId") Long teacherId);

    /**
     * Top courses by revenue cho một teacher.
     */
    @Query("SELECT o.course.id, o.course.title, SUM(o.finalAmount), COUNT(o), SUM(o.teacherRevenue) " +
           "FROM Order o " +
           "WHERE o.status = 'COMPLETED' AND o.course.teacher.id = :teacherId " +
           "GROUP BY o.course.id, o.course.title " +
           "ORDER BY SUM(o.finalAmount) DESC")
    List<Object[]> topCoursesByRevenueForTeacher(@Param("teacherId") Long teacherId, org.springframework.data.domain.Pageable pageable);

    /**
     * Doanh thu tổng hợp theo từng teacher (admin view).
     * Returns: [teacherId, username, fullName, totalRevenue, orderCount]
     */
    @Query("SELECT o.course.teacher.id, o.course.teacher.username, " +
           "       o.course.teacher.profile.fullName, " +
           "       SUM(o.finalAmount), COUNT(o), SUM(o.teacherRevenue) " +
           "FROM Order o " +
           "WHERE o.status = 'COMPLETED' " +
           "GROUP BY o.course.teacher.id, o.course.teacher.username, o.course.teacher.profile.fullName " +
           "ORDER BY SUM(o.finalAmount) DESC")
    List<Object[]> revenueByTeacher();

    /**
     * Lấy danh sách học viên đã mua khóa học của một teacher.
     * Trả về: [userId, username, email, fullName, phone, courseTitle, purchaseDate, status]
     */
    @Query("SELECT o.user.id, o.user.username, o.user.email, " +
           "       o.user.profile.fullName, o.user.profile.phone, " +
           "       o.course.title, o.createdAt, o.status " +
           "FROM Order o " +
           "WHERE o.course.teacher.id = :teacherId " +
           "ORDER BY o.createdAt DESC")
    List<Object[]> findStudentsByTeacher(@Param("teacherId") Long teacherId);

    @Query("""
           SELECT COUNT(DISTINCT o.user.id)
           FROM Order o
           WHERE o.course.id = :courseId
             AND o.status = com.learning.englishpro.payment.entity.OrderStatus.COMPLETED
           """)
    long countDistinctCompletedStudentsByCourseId(@Param("courseId") Long courseId);
}

