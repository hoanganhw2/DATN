<template>
  <section id="courses" class="py-16 sm:py-20 px-4 sm:px-6 lg:px-8 bg-gray-50">
    <div class="max-w-7xl mx-auto">
      <h2 class="text-3xl sm:text-4xl font-bold text-gray-900 mb-4">Khóa Học Nổi Bật</h2>
      <p class="text-gray-600 mb-12 max-w-2xl">
        Khám phá những khóa học được yêu thích nhất từ hàng trăm tùy chọn được tạo bởi các chuyên
        gia
      </p>

      <!-- Loading State -->
      <div v-if="isLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <SkeletonCard v-for="i in 4" :key="i" />
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-12 bg-white rounded-xl border border-red-200">
        <svg
          class="w-12 h-12 text-red-500 mx-auto mb-4"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4v.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Không thể tải khóa học</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <BaseButton variant="primary" @click="fetchCourses"> Thử lại </BaseButton>
      </div>

      <!-- Courses Grid -->
      <div
        v-else-if="courses.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
      >
        <CourseCard v-for="course in courses" :key="course.id" :course="course" />
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-12 bg-white rounded-xl border border-gray-200">
        <svg
          class="w-12 h-12 text-gray-300 mx-auto mb-4"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 6v6m0 0v6m0-6h6m-6 0H6"
          />
        </svg>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Chưa có khóa học</h3>
        <p class="text-gray-600">Chúng tôi sẽ sớm cập nhật thêm khóa học mới</p>
      </div>

      <!-- View All Button -->
      <div v-if="courses.length > 0" class="text-center mt-12">
        <router-link to="/courses">
          <BaseButton variant="outline" size="lg"> Xem tất cả khóa học </BaseButton>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
/// <reference lib="es2015" />
import { ref, onMounted } from 'vue'
import api from '@/api/axios'
import { mapCourseListItem } from '@/api/courses'
import CourseCard from '@/components/CourseCard.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'
import BaseButton from '@/components/BaseButton.vue'

interface Course {
  id: number
  slug: string
  title: string
  thumbnailUrl?: string
  level?: string
  price?: number
  teacherFullName?: string
}

const courses = ref<Course[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const fetchCourses = async () => {
  isLoading.value = true
  error.value = null

  try {
    const response = await api.get('/courses', {
      params: {
        page: 0,
        size: 4,
        sort: 'totalEnrollments,desc', // Ưu tiên các khóa học có nhiều người học
      },
    })

    if (response.data.data?.content) {
      courses.value = response.data.data.content.map((c: Record<string, unknown>) =>
        mapCourseListItem(c),
      )
    } else {
      courses.value = []
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Có lỗi khi tải khóa học'
    console.error('Error fetching courses:', err)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})
</script>
