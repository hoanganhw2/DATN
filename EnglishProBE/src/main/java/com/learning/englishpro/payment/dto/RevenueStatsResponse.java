package com.learning.englishpro.payment.dto;

import java.math.BigDecimal;
import java.util.List;

public class RevenueStatsResponse {

    /** Tỉ lệ chia doanh thu */
    public static final BigDecimal TEACHER_RATE = new BigDecimal("0.70");
    public static final BigDecimal ADMIN_RATE   = new BigDecimal("0.30");

    public static BigDecimal teacherShare(BigDecimal teacherRevBase) {
        return teacherRevBase == null ? BigDecimal.ZERO : teacherRevBase.multiply(TEACHER_RATE);
    }
    public static BigDecimal adminShare(BigDecimal totalRevenue, BigDecimal teacherPayout) {
        if (totalRevenue == null) return BigDecimal.ZERO;
        if (teacherPayout == null) teacherPayout = BigDecimal.ZERO;
        return totalRevenue.subtract(teacherPayout);
    }

    // ── DTOs ─────────────────────────────────────────────────────────────────

    public record OverallStats(
            long totalOrders,
            BigDecimal totalRevenue,
            long completedOrders,
            long pendingOrders,
            long failedOrders,
            BigDecimal teacherRevenue,   // 70%
            BigDecimal adminRevenue      // 30%
    ) {}

    public record MonthlyRevenue(
            int month,
            BigDecimal revenue,
            long orderCount,
            BigDecimal teacherRevenue,
            BigDecimal adminRevenue
    ) {}

    public record CourseRevenue(
            Long courseId,
            String courseTitle,
            BigDecimal revenue,
            long orderCount,
            BigDecimal teacherRevenue,
            BigDecimal adminRevenue
    ) {}

    public record StatusBreakdown(
            String status,
            long count,
            BigDecimal totalAmount
    ) {}

    public record TeacherSummary(
            Long teacherId,
            String username,
            String fullName,
            BigDecimal totalRevenue,
            long orderCount,
            BigDecimal teacherRevenue,   // 70%
            BigDecimal adminRevenue      // 30%
    ) {}

    public record DashboardStats(
            OverallStats overall,
            List<MonthlyRevenue> monthly,
            List<CourseRevenue> topCourses,
            List<StatusBreakdown> statusBreakdown,
            List<TeacherSummary> teacherBreakdown,   // admin only
            int year
    ) {}

    /** Dùng cho endpoint của Teacher */
    public record TeacherDashboard(
            OverallStats overall,
            List<MonthlyRevenue> monthly,
            List<CourseRevenue> topCourses,
            int year
    ) {}
}
