package com.learning.englishpro.payment.controller;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.payment.dto.RevenueStatsResponse;
import com.learning.englishpro.payment.dto.TeacherStudentResponse;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.progress.dto.response.CourseStudentDetailResponse;
import com.learning.englishpro.progress.service.TeacherStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.learning.englishpro.payment.dto.RevenueStatsResponse.*;

@Tag(name = "Statistics", description = "Revenue & analytics for Admin and Teacher")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final OrderRepository orderRepository;
    private final TeacherStudentService teacherStudentService;


    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN endpoint — GET /admin/stats/revenue?year=
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "[ADMIN] Full revenue dashboard with 70/30 split")
    @GetMapping("/admin/stats/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStats>> getAdminStats(
            @RequestParam(defaultValue = "0") int year) {

        if (year <= 0) year = LocalDate.now().getYear();

        // Overall
        OverallStats overall = buildOverall(orderRepository.overallStats());

        // Monthly
        List<MonthlyRevenue> monthly = buildMonthly(orderRepository.revenueByMonth(year));

        // Top 10 courses (all)
        List<CourseRevenue> topCourses = buildTopCourses(
                orderRepository.topCoursesByRevenue(PageRequest.of(0, 1000)));

        // Status breakdown
        List<StatusBreakdown> statusBreakdown = buildStatusBreakdown(orderRepository.ordersByStatus());

        // Teacher breakdown (admin sees all teachers)
        List<TeacherSummary> teacherBreakdown = buildTeacherBreakdown(orderRepository.revenueByTeacher());

        return ResponseEntity.ok(ApiResponse.ok(
                new DashboardStats(overall, monthly, topCourses, statusBreakdown, teacherBreakdown, year)
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEACHER endpoint — GET /teacher/stats/revenue?year=
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "[TEACHER] Revenue dashboard for my courses only (70% share)")
    @GetMapping("/teacher/stats/revenue")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboard>> getTeacherStats(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int year) {

        if (year <= 0) year = LocalDate.now().getYear();
        Long teacherId = currentUser.getId();

        // Overall — chỉ khóa học của teacher này
        OverallStats overall = buildOverall(orderRepository.overallStatsByTeacher(teacherId));

        // Monthly
        List<MonthlyRevenue> monthly = buildMonthly(
                orderRepository.revenueByMonthForTeacher(year, teacherId));

        // Top courses của teacher
        List<CourseRevenue> topCourses = buildTopCourses(
                orderRepository.topCoursesByRevenueForTeacher(teacherId, PageRequest.of(0, 1000)));

        return ResponseEntity.ok(ApiResponse.ok(
                new TeacherDashboard(overall, monthly, topCourses, year)
        ));
    }

    @Operation(summary = "[TEACHER] Get students enrolled in teacher's courses")
    @GetMapping("/teacher/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherStudentResponse>>> getTeacherStudents(
            @AuthenticationPrincipal User currentUser) {
        
        List<Object[]> rows = orderRepository.findStudentsByTeacher(currentUser.getId());
        List<TeacherStudentResponse> students = rows.stream().map(r -> TeacherStudentResponse.builder()
                .userId(((Number) r[0]).longValue())
                .username((String) r[1])
                .email((String) r[2])
                .fullName(r[3] != null ? (String) r[3] : (String) r[1])
                .phone((String) r[4])
                .courseTitle((String) r[5])
                .purchaseDate(r[6] instanceof java.time.Instant ? 
                        LocalDateTime.ofInstant((java.time.Instant) r[6], java.time.ZoneId.systemDefault()) : 
                        (LocalDateTime) r[6])
                .status(r[7].toString())
                .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(students));
    }

    @Operation(summary = "[TEACHER] Get students in a specific course with progress & exercise scores")
    @GetMapping("/teacher/courses/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<CourseStudentDetailResponse>>> getCourseStudents(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long courseId) {

        List<CourseStudentDetailResponse> students =
                teacherStudentService.getStudentsByCourse(courseId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(students));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private OverallStats buildOverall(List<Object[]> rows) {
        Object[] r = rows.isEmpty() ? new Object[6] : rows.get(0);
        long   totalOrders = r[0] != null ? ((Number) r[0]).longValue() : 0L;
        BigDecimal revenue = r[1] != null ? (BigDecimal) r[1]           : BigDecimal.ZERO;
        long completed     = r[2] != null ? ((Number) r[2]).longValue() : 0L;
        long pending       = r[3] != null ? ((Number) r[3]).longValue() : 0L;
        long failed        = r[4] != null ? ((Number) r[4]).longValue() : 0L;
        BigDecimal teacherRevBase = (r.length > 5 && r[5] != null) ? (BigDecimal) r[5] : BigDecimal.ZERO;
        
        BigDecimal tShare = teacherShare(teacherRevBase);
        return new OverallStats(totalOrders, revenue, completed, pending, failed,
                tShare, adminShare(revenue, tShare));
    }

    private List<MonthlyRevenue> buildMonthly(List<Object[]> rows) {
        List<MonthlyRevenue> list = new ArrayList<>();
        for (Object[] r : rows) {
            int        month = ((Number) r[0]).intValue();
            BigDecimal rev   = r[1] != null ? (BigDecimal) r[1] : BigDecimal.ZERO;
            long       cnt   = ((Number) r[2]).longValue();
            BigDecimal teacherRevBase = (r.length > 3 && r[3] != null) ? (BigDecimal) r[3] : BigDecimal.ZERO;
            
            BigDecimal tShare = teacherShare(teacherRevBase);
            list.add(new MonthlyRevenue(month, rev, cnt, tShare, adminShare(rev, tShare)));
        }
        return list;
    }

    private List<CourseRevenue> buildTopCourses(List<Object[]> rows) {
        List<CourseRevenue> list = new ArrayList<>();
        for (Object[] r : rows) {
            Long       courseId = ((Number) r[0]).longValue();
            String     title    = (String) r[1];
            BigDecimal rev      = r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO;
            long       cnt      = ((Number) r[3]).longValue();
            BigDecimal teacherRevBase = (r.length > 4 && r[4] != null) ? (BigDecimal) r[4] : BigDecimal.ZERO;
            
            BigDecimal tShare = teacherShare(teacherRevBase);
            list.add(new CourseRevenue(courseId, title, rev, cnt, tShare, adminShare(rev, tShare)));
        }
        return list;
    }

    private List<StatusBreakdown> buildStatusBreakdown(List<Object[]> rows) {
        List<StatusBreakdown> list = new ArrayList<>();
        for (Object[] r : rows) {
            String     status = r[0].toString();
            long       cnt    = ((Number) r[1]).longValue();
            BigDecimal amt    = r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO;
            list.add(new StatusBreakdown(status, cnt, amt));
        }
        return list;
    }

    private List<TeacherSummary> buildTeacherBreakdown(List<Object[]> rows) {
        List<TeacherSummary> list = new ArrayList<>();
        for (Object[] r : rows) {
            Long       teacherId = ((Number) r[0]).longValue();
            String     username  = (String) r[1];
            String     fullName  = r[2] != null ? (String) r[2] : username;
            BigDecimal rev       = r[3] != null ? (BigDecimal) r[3] : BigDecimal.ZERO;
            long       cnt       = ((Number) r[4]).longValue();
            BigDecimal teacherRevBase = (r.length > 5 && r[5] != null) ? (BigDecimal) r[5] : BigDecimal.ZERO;
            
            BigDecimal tShare = teacherShare(teacherRevBase);
            list.add(new TeacherSummary(teacherId, username, fullName, rev, cnt,
                    tShare, adminShare(rev, tShare)));
        }
        return list;
    }
}
