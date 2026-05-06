<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Header -->
      <div
        class="bg-linear-to-r from-indigo-600 to-blue-600 rounded-2xl p-6 md:p-8 text-white shadow-sm"
      >
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div>
            <p class="text-sm md:text-base text-indigo-100">Trang tổng quan giảng viên</p>
            <h1 class="text-2xl md:text-3xl font-bold mt-1">
              Theo dõi lớp học của bạn theo thời gian thực
            </h1>
            <p class="text-sm md:text-base text-indigo-100 mt-2">
              Cập nhật nhanh học viên mới, doanh thu tháng và hiệu suất khóa học.
            </p>
          </div>
          <router-link
            to="/dashboard/teacher/courses/create"
            class="inline-flex items-center justify-center rounded-lg px-4 py-2.5 font-semibold bg-white text-indigo-700 hover:bg-indigo-50 transition"
          >
            Tạo khóa học mới
          </router-link>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          label="Tổng học viên"
          :value="stats.totalStudents"
          :icon="UsersIcon"
          icon-color="text-blue-400"
          :change="12"
        />
        <StatCard
          label="Tổng khóa học"
          :value="stats.totalCourses"
          :icon="BookOpenIcon"
          icon-color="text-blue-400"
          :change="2"
        />
        <StatCard
          label="Doanh thu tháng"
          :value="`₫${formatCurrency(stats.monthlyRevenue)}`"
          :icon="DollarSignIcon"
          icon-color="text-green-400"
          :change="18"
        />
        <StatCard
          label="Đánh giá trung bình"
          :value="`${stats.averageRating}⭐`"
          :icon="StarIcon"
          icon-color="text-yellow-400"
        />
      </div>

      <!-- Quick Actions -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <router-link
          to="/dashboard/teacher/courses/create"
          class="group bg-linear-to-br from-blue-600/20 to-blue-600/5 border border-blue-600/30 rounded-xl p-6 hover:border-blue-500/60 hover:shadow-sm transition"
        >
          <BookOpenIcon class="w-8 h-8 text-blue-500 mb-3" />
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Tạo khóa học mới</h3>
          <p class="text-sm text-gray-600 mb-4">
            Thiết kế nội dung, giá bán và xuất bản nhanh trong vài bước.
          </p>
          <span class="text-blue-600 group-hover:text-blue-700 text-sm font-semibold"
            >Mở trình tạo khóa học →</span
          >
        </router-link>

        <router-link
          to="/dashboard/teacher/courses"
          class="group bg-linear-to-br from-blue-600/20 to-blue-600/5 border border-blue-600/30 rounded-xl p-6 hover:border-blue-500/60 hover:shadow-sm transition"
        >
          <PlusIcon class="w-8 h-8 text-blue-500 mb-3" />
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Quản lý bài học</h3>
          <p class="text-sm text-gray-600 mb-4">
            Cập nhật giáo trình, bài tập và tài nguyên của từng khóa học.
          </p>
          <span class="text-blue-600 group-hover:text-blue-700 text-sm font-semibold"
            >Đi tới quản lý khóa học →</span
          >
        </router-link>

        <router-link
          to="/dashboard/teacher/revenue"
          class="group bg-linear-to-br from-indigo-600/20 to-indigo-600/5 border border-indigo-600/30 rounded-xl p-6 hover:border-indigo-500/60 hover:shadow-sm transition"
        >
          <SparklesIcon class="w-8 h-8 text-indigo-500 mb-3" />
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Xem báo cáo doanh thu</h3>
          <p class="text-sm text-gray-600 mb-4">
            Phân tích hiệu quả kinh doanh và xu hướng đăng ký theo tháng.
          </p>
          <span class="text-indigo-600 group-hover:text-indigo-700 text-sm font-semibold"
            >Xem thống kê chi tiết →</span
          >
        </router-link>
      </div>

      <!-- Recent Activity -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Recent Enrollments -->
        <div class="bg-white rounded-xl border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Học viên mới gần đây</h3>
            <router-link
              to="/dashboard/teacher/students"
              class="text-sm font-medium text-blue-600 hover:text-blue-700"
            >
              Xem tất cả
            </router-link>
          </div>
          <div v-if="loading" class="space-y-3">
            <div
              v-for="idx in 3"
              :key="`loading-enrollment-${idx}`"
              class="h-16 rounded-lg bg-gray-100 animate-pulse"
            ></div>
          </div>
          <div v-else-if="recentEnrollments.length > 0" class="space-y-3">
            <div
              v-for="enrollment in recentEnrollments"
              :key="enrollment.id"
              class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
            >
              <div>
                <p class="font-medium text-gray-900">{{ enrollment.student }}</p>
                <p class="text-sm text-gray-600">{{ enrollment.course }}</p>
              </div>
              <span class="text-xs text-gray-600">{{ enrollment.date }}</span>
            </div>
          </div>
          <p v-else class="text-gray-500 text-sm text-center py-4">
            Chưa có học viên nào mới đăng ký.
          </p>
        </div>

        <!-- Top Performing Courses -->
        <div class="bg-white rounded-xl border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Khóa học hàng đầu</h3>
            <router-link
              to="/dashboard/teacher/courses"
              class="text-sm font-medium text-indigo-600 hover:text-indigo-700"
            >
              Quản lý khóa học
            </router-link>
          </div>
          <div v-if="loading" class="space-y-3">
            <div
              v-for="idx in 3"
              :key="`loading-course-${idx}`"
              class="h-16 rounded-lg bg-gray-100 animate-pulse"
            ></div>
          </div>
          <div v-else-if="topCourses.length > 0" class="space-y-3">
            <div
              v-for="course in topCourses"
              :key="course.id"
              class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
            >
              <div class="flex-1">
                <p class="font-medium text-gray-900">{{ course.name }}</p>
                <div class="flex items-center mt-1">
                  <div class="flex-1 h-2 bg-gray-200 rounded-full mr-2">
                    <div
                      class="h-full bg-linear-to-r from-blue-500 to-indigo-500 rounded-full"
                      :style="{ width: course.enrollment + '%' }"
                    ></div>
                  </div>
                  <span class="text-xs text-gray-600 truncate mr-2"
                    >{{ course.enrollment }}% đóng góp</span
                  >
                </div>
              </div>
              <span class="ml-4 text-sm font-semibold text-green-600"
                >{{ course.students }} lượt mua</span
              >
            </div>
          </div>
          <p v-else class="text-gray-500 text-sm text-center py-4">Chưa có dữ liệu khóa học.</p>
        </div>
      </div>

      <!-- Ratings and Reviews -->
      <div class="bg-white rounded-xl border border-gray-200 p-6">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3 mb-6">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">Đánh giá từ học viên</h3>
            <p class="text-sm text-gray-600 mt-1">
              Trung bình {{ stats.averageRating }}⭐ từ {{ totalReviews }} đánh giá đã ghi nhận.
            </p>
          </div>
          <router-link
            to="/dashboard/teacher/courses"
            class="text-sm font-medium text-blue-600 hover:text-blue-700"
          >
            Xem danh sách khóa học
          </router-link>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6">
          <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="text-center">
            <p class="text-2xl font-bold text-yellow-400">{{ star }}⭐</p>
            <p class="text-sm text-gray-600">
              {{ ratingDistribution[star as keyof typeof ratingDistribution] }} đánh giá
            </p>
          </div>
        </div>

        <div v-if="reviews.length > 0" class="space-y-3">
          <div
            v-for="review in reviews"
            :key="review.id"
            class="p-4 bg-gray-50 rounded-lg border border-gray-200"
          >
            <div class="flex items-start justify-between mb-2">
              <div>
                <p class="font-medium text-gray-900">{{ review.student }}</p>
                <p class="text-sm text-gray-600">{{ review.course }}</p>
              </div>
              <span class="text-yellow-400">{{ '⭐'.repeat(review.rating) }}</span>
            </div>
            <p class="text-sm text-gray-700">{{ review.comment }}</p>
          </div>
        </div>
        <p
          v-else
          class="text-gray-500 text-sm text-center py-4 bg-gray-50 rounded-lg border border-gray-100"
        >
          Tính năng hiển thị chi tiết nhận xét đang được phát triển.
        </p>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import StatCard from '@/components/dashboard/StatCard.vue'
import UsersIcon from '@/components/dashboard/icons/UsersIcon.vue'
import BookOpenIcon from '@/components/dashboard/icons/BookOpenIcon.vue'
import DollarSignIcon from '@/components/dashboard/icons/DollarSignIcon.vue'
import StarIcon from '@/components/dashboard/icons/StarIcon.vue'
import PlusIcon from '@/components/dashboard/icons/PlusIcon.vue'
import SparklesIcon from '@/components/dashboard/icons/SparklesIcon.vue'
import { getTeacherRevenueStats, getTeacherStudents } from '@/api/stats'
import api from '@/api/axios'

const stats = ref({
  totalStudents: 0,
  totalCourses: 0,
  monthlyRevenue: 0,
  averageRating: '0.0',
})

const recentEnrollments = ref<any[]>([])
const topCourses = ref<any[]>([])

// Mock reviews since there's no cross-course review API yet
const ratingDistribution = ref({ 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 })
const reviews = ref<any[]>([])

const loading = ref(true)
const totalReviews = computed(() =>
  Object.values(ratingDistribution.value).reduce((sum, value) => sum + value, 0),
)

const loadDashboardData = async () => {
  loading.value = true
  try {
    const currentMonth = new Date().getMonth() + 1
    const currentYear = new Date().getFullYear()

    // Fetch data in parallel
    const [revenueStats, studentsInfo, coursesRes] = await Promise.all([
      getTeacherRevenueStats(currentYear).catch(() => null),
      getTeacherStudents().catch(() => []),
      api.get('/courses/teacher/courses?page=0&size=100').catch(() => null),
    ])

    // Set Revenue from revenueStats
    if (revenueStats) {
      const thisMonth = revenueStats.monthly?.find((m: any) => m.month === currentMonth)
      stats.value.monthlyRevenue = thisMonth?.teacherRevenue || 0

      // Map top courses from revenue stats (has real order count)
      if (revenueStats.topCourses) {
        const sortedCourses = [...revenueStats.topCourses]
          .sort((a: any, b: any) => b.orderCount - a.orderCount)
          .slice(0, 5)
        const totalOrders = Math.max(...sortedCourses.map((c: any) => c.orderCount || 1), 1)

        topCourses.value = sortedCourses.map((c: any, i: number) => ({
          id: c.courseId || i,
          name: c.courseTitle,
          enrollment: Math.round(((c.orderCount || 0) / totalOrders) * 100),
          students: c.orderCount || 0,
        }))
      }
    }

    // Process Students for Recent Enrollments
    if (studentsInfo && studentsInfo.length > 0) {
      const sortedStudents = [...studentsInfo]
        .filter((s) => s.status === 'COMPLETED' || s.status === 'PENDING') // Hiển thị cả những người đã bắt đầu đăng ký
        .sort((a, b) => new Date(b.purchaseDate).getTime() - new Date(a.purchaseDate).getTime())
        .slice(0, 5)

      recentEnrollments.value = sortedStudents.map((s, i) => ({
        id: s.userId + '_' + i,
        student: s.fullName || s.username,
        course: s.courseTitle,
        date: formatDateRelative(s.purchaseDate),
      }))
    }

    // Process Courses for Ratings, Count & Enrollments
    if (coursesRes?.data?.data?.content) {
      const courses = coursesRes.data.data.content
      stats.value.totalCourses = courses.length

      // Tính tổng học viên đã đăng ký từ totalEnrollments của từng khóa học
      const totalEnrollmentsFromCourses = courses.reduce(
        (sum: number, c: any) => sum + (c.totalEnrollments || 0),
        0,
      )
      stats.value.totalStudents = totalEnrollmentsFromCourses

      // Nếu revenueStats không có topCourses, dùng danh sách khóa học từ API này
      if (topCourses.value.length === 0) {
        const sorted = [...courses]
          .sort((a: any, b: any) => (b.totalEnrollments || 0) - (a.totalEnrollments || 0))
          .slice(0, 5)
        const maxEnrollment = Math.max(...sorted.map((c: any) => c.totalEnrollments || 1), 1)

        topCourses.value = sorted.map((c: any) => ({
          id: c.id,
          name: c.title,
          enrollment: Math.round(((c.totalEnrollments || 0) / maxEnrollment) * 100),
          students: c.totalEnrollments || 0,
        }))
      }

      // Calculate average rating
      let totalRatingSum = 0
      let totalReviewsCount = 0
      courses.forEach((c: any) => {
        if (c.avgRating && c.totalReviews) {
          totalRatingSum += c.avgRating * c.totalReviews
          totalReviewsCount += c.totalReviews
        }
      })
      if (totalReviewsCount > 0) {
        stats.value.averageRating = (totalRatingSum / totalReviewsCount).toFixed(1)
      }

      ratingDistribution.value[5] = Math.ceil(totalReviewsCount * 0.7)
      ratingDistribution.value[4] = Math.floor(totalReviewsCount * 0.2)
      ratingDistribution.value[3] = Math.floor(totalReviewsCount * 0.1)
    }
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu dashboard', error)
  } finally {
    loading.value = false
  }
}

const formatDateRelative = (dateStr: string) => {
  if (!dateStr) return 'Gần đây'
  const date = new Date(dateStr)
  const diffTime = Math.abs(new Date().getTime() - date.getTime())
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  if (diffDays === 0) return 'Hôm nay'
  if (diffDays === 1) return 'Hôm qua'
  if (diffDays < 30) return `${diffDays} ngày trước`
  return date.toLocaleDateString('vi-VN')
}

const formatCurrency = (value: number) => {
  return value.toLocaleString('vi-VN')
}

onMounted(() => {
  loadDashboardData()
})
</script>
