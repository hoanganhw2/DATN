<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import BookOpenIcon from '@/components/dashboard/icons/BookOpenIcon.vue'
import PlayIcon from '@/components/dashboard/icons/PlayIcon.vue'
import ClockIcon from '@/components/dashboard/icons/ClockIcon.vue'
import CheckIcon from '@/components/dashboard/icons/CheckIcon.vue'
import StarIcon from '@/components/dashboard/icons/StarIcon.vue'
import ChevronRightIcon from '@/components/dashboard/icons/ChevronRightIcon.vue'
import TrophyIcon from '@/components/dashboard/icons/TrophyIcon.vue'
import TrendingUpIcon from '@/components/dashboard/icons/TrendingUpIcon.vue'

import { getMyEnrollments, type EnrolledCourse } from '@/api/enrollments'
import { getCourses, type Course } from '@/api/courses'

const authStore = useAuthStore()

const stats = ref({
  enrolledCourses: 0,
  completedCourses: 0,
  completedLessons: 0,
  totalLessons: 0,
  totalHours: 0,
  certificates: 0,
})

const enrolledCourses = ref<EnrolledCourse[]>([])
const recommendedCourses = ref<Course[]>([])

const overallProgress = computed(() => {
  if (!stats.value.totalLessons) return 0
  return Math.round((stats.value.completedLessons / stats.value.totalLessons) * 100)
})

const formatCurrency = (value: number) => value.toLocaleString('vi-VN')

const getImageUrl = (url: string | null | undefined) => {
  if (!url) return null
  if (url.includes('drive.google.com')) {
    // Regex cải tiến để dừng lại trước /, ?, & hoặc #
    const fileId = url.match(/\/d\/([^/?#&]+)/)?.[1] || url.match(/id=([^/?#&]+)/)?.[1]
    if (fileId) {
      return `https://drive.google.com/uc?export=view&id=${fileId}`
    }
  }
  return url
}

const greetingTime = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Chào buổi sáng'
  if (h < 18) return 'Chào buổi chiều'
  return 'Chào buổi tối'
})

const fetchData = async () => {
  try {
    const enrollsPage = await getMyEnrollments(0, 100)

    enrolledCourses.value = enrollsPage.content
      .filter((c: EnrolledCourse) => c.status !== 'COMPLETED')
      .slice(0, 3)

    let completedCourses = 0
    let completedLessons = 0
    let totalLessons = 0
    let totalSeconds = 0

    enrollsPage.content.forEach((c: EnrolledCourse) => {
      if (c.status === 'COMPLETED') completedCourses++
      completedLessons += c.completedLessons
      totalLessons += c.totalLessons
      if (c.totalDurationSecs) {
        totalSeconds += c.totalDurationSecs * (c.progressPercent / 100)
      }
    })

    stats.value = {
      enrolledCourses: enrollsPage.totalElements,
      completedCourses,
      completedLessons,
      totalLessons,
      totalHours: Math.round(totalSeconds / 3600) || Math.round(completedLessons * 0.5),
      certificates: completedCourses,
    }

    const coursesPage = await getCourses({ page: 1, size: 3 })
    recommendedCourses.value = coursesPage.content
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

onMounted(fetchData)
</script>

<template>
  <DashboardLayout>
    <div class="min-h-screen bg-gray-50/50">
      <!-- Top greeting bar -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <p class="text-sm font-medium text-blue-600 mb-1">{{ greetingTime }},</p>
          <h1 class="text-2xl font-black text-gray-900">
            {{ authStore.user?.fullName || 'Học viên' }} 👋
          </h1>
          <p class="text-sm text-gray-500 mt-1">
            Đây là tổng quan hành trình học tập của bạn hôm nay.
          </p>
        </div>
        <router-link
          to="/courses"
          class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold rounded-xl shadow-lg shadow-blue-100 transition-all hover:-translate-y-0.5"
        >
          <PlayIcon class="w-4 h-4" />
          Học ngay
        </router-link>
      </div>

      <!-- Main 2-col layout -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">
        <!-- LEFT COLUMN -->
        <div class="xl:col-span-2 space-y-6">
          <!-- Overall Progress Hero Card -->
          <div
            class="relative bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
          >
            <!-- decorative strip -->
            <div
              class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-blue-500 to-indigo-500"
            ></div>
            <div class="p-6 md:p-8">
              <div class="flex flex-col md:flex-row md:items-center gap-8">
                <!-- Circular progress -->
                <div class="relative flex-shrink-0 flex flex-col items-center">
                  <svg class="w-36 h-36 -rotate-90" viewBox="0 0 120 120">
                    <circle cx="60" cy="60" r="52" fill="none" stroke="#eff6ff" stroke-width="12" />
                    <circle
                      cx="60"
                      cy="60"
                      r="52"
                      fill="none"
                      stroke="url(#grad)"
                      stroke-width="12"
                      stroke-linecap="round"
                      :stroke-dasharray="`${2 * Math.PI * 52}`"
                      :stroke-dashoffset="`${2 * Math.PI * 52 * (1 - overallProgress / 100)}`"
                      style="transition: stroke-dashoffset 1s ease"
                    />
                    <defs>
                      <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#3b82f6" />
                        <stop offset="100%" stop-color="#6366f1" />
                      </linearGradient>
                    </defs>
                  </svg>
                  <div class="absolute inset-0 flex flex-col items-center justify-center">
                    <span class="text-3xl font-black text-gray-900">{{ overallProgress }}%</span>
                    <span class="text-xs font-medium text-gray-400">hoàn thành</span>
                  </div>
                </div>
                <!-- Text summary -->
                <div class="flex-1 space-y-4">
                  <div>
                    <h2 class="text-xl font-black text-gray-900 mb-1">Tiến độ tổng thể</h2>
                    <p class="text-sm text-gray-500">
                      Tổng hợp từ tất cả các khóa học bạn đang tham gia.
                    </p>
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div class="p-4 bg-blue-50 rounded-xl border border-blue-100">
                      <p class="text-xs font-bold text-blue-600 uppercase tracking-wider mb-1">
                        Đã hoàn thành
                      </p>
                      <p class="text-2xl font-black text-gray-900">
                        {{ stats.completedLessons }}
                        <span class="text-sm font-medium text-gray-400">bài</span>
                      </p>
                    </div>
                    <div class="p-4 bg-gray-50 rounded-xl border border-gray-100">
                      <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1">
                        Còn lại
                      </p>
                      <p class="text-2xl font-black text-gray-900">
                        {{ stats.totalLessons - stats.completedLessons }}
                        <span class="text-sm font-medium text-gray-400">bài</span>
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Continue Learning -->
          <div>
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-base font-black text-gray-900 uppercase tracking-widest">
                Tiếp tục học
              </h2>
              <router-link
                to="/dashboard/student/courses"
                class="text-sm font-bold text-blue-600 hover:text-blue-800 flex items-center gap-0.5 group"
              >
                Xem tất cả
                <ChevronRightIcon
                  class="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform"
                />
              </router-link>
            </div>

            <div v-if="enrolledCourses.length > 0" class="space-y-3">
              <div
                v-for="(course, i) in enrolledCourses"
                :key="course.courseId"
                class="group bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-blue-200 transition-all duration-300 p-4 flex items-center gap-4"
              >
                <!-- Number badge -->
                <div
                  class="shrink-0 w-8 h-8 rounded-xl flex items-center justify-center text-xs font-black text-blue-700 bg-blue-50"
                >
                  {{ String(i + 1).padStart(2, '0') }}
                </div>
                <!-- Thumbnail -->
                <div class="shrink-0 w-16 h-12 rounded-lg overflow-hidden">
                  <img
                    :src="
                      getImageUrl(course.thumbnailUrl) ||
                      'https://images.unsplash.com/photo-1546410531-bb4caa6b0e8b?w=400&h=240&fit=crop'
                    "
                    :alt="course.title"
                    class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                </div>
                <!-- Info -->
                <div class="flex-1 min-w-0">
                  <h4
                    class="text-sm font-bold text-gray-900 truncate group-hover:text-blue-700 transition-colors"
                  >
                    {{ course.title }}
                  </h4>
                  <div class="flex items-center gap-2 mt-1.5">
                    <div class="flex-1 h-1 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        class="h-full bg-gradient-to-r from-blue-500 to-indigo-500 rounded-full"
                        :style="{
                          width: course.progressPercent + '%',
                          transition: 'width 1s ease',
                        }"
                      ></div>
                    </div>
                    <span class="text-xs font-bold text-gray-500 shrink-0"
                      >{{ Math.round(course.progressPercent) }}%</span
                    >
                  </div>
                </div>
                <!-- Play btn -->
                <router-link
                  :to="`/learning/${course.courseId}`"
                  class="shrink-0 w-9 h-9 rounded-xl bg-gray-50 group-hover:bg-blue-600 text-gray-400 group-hover:text-white flex items-center justify-center transition-all duration-300 shadow-sm"
                >
                  <PlayIcon class="w-4 h-4 ml-0.5" />
                </router-link>
              </div>
            </div>

            <div
              v-else
              class="bg-white rounded-2xl border-2 border-dashed border-gray-200 p-10 text-center"
            >
              <div
                class="w-14 h-14 bg-blue-50 rounded-2xl flex items-center justify-center mx-auto mb-4"
              >
                <BookOpenIcon class="w-7 h-7 text-blue-500" />
              </div>
              <p class="text-sm font-bold text-gray-700 mb-1">Chưa có khóa học nào</p>
              <p class="text-xs text-gray-400 mb-5">
                Bắt đầu hành trình học tiếng Anh của bạn ngay hôm nay!
              </p>
              <router-link
                to="/courses"
                class="inline-flex items-center gap-2 px-5 py-2 bg-blue-600 text-white text-xs font-bold rounded-xl hover:bg-blue-700 transition-colors"
              >
                Khám phá khóa học
              </router-link>
            </div>
          </div>

          <!-- Recommended Courses -->
          <div v-if="recommendedCourses.length > 0">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-base font-black text-gray-900 uppercase tracking-widest">
                Gợi ý cho bạn
              </h2>
              <router-link
                to="/courses"
                class="text-xs font-bold text-blue-600 hover:text-blue-800 flex items-center gap-0.5 group"
              >
                Xem thêm
                <ChevronRightIcon
                  class="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform"
                />
              </router-link>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div
                v-for="course in recommendedCourses"
                :key="course.id"
                class="group bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 shadow-sm"
              >
                <div class="relative h-32 overflow-hidden">
                  <img
                    :src="
                      getImageUrl(course.thumbnailUrl) ||
                      'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=250&fit=crop'
                    "
                    :alt="course.title"
                    class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                  <span
                    class="absolute top-2 right-2 px-2 py-0.5 bg-white/90 text-gray-700 text-[10px] font-bold rounded-md"
                  >
                    {{ course.level }}
                  </span>
                </div>
                <div class="p-4">
                  <h4
                    class="text-sm font-bold text-gray-900 line-clamp-1 mb-2 group-hover:text-blue-700 transition-colors"
                  >
                    {{ course.title }}
                  </h4>
                  <div class="flex items-center justify-between">
                    <span class="text-sm font-black text-blue-700"
                      >₫{{ formatCurrency(course.price) }}</span
                    >
                    <router-link
                      :to="`/courses/${course.slug}`"
                      class="text-[11px] font-bold text-gray-400 hover:text-blue-600 transition-colors"
                    >
                      Xem chi tiết →
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- RIGHT SIDEBAR -->
        <div class="space-y-6">
          <!-- Stat block stacked -->
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-50">
              <h3 class="text-xs font-black text-gray-400 uppercase tracking-widest">
                Thống kê của bạn
              </h3>
            </div>

            <div class="divide-y divide-gray-50">
              <div class="flex items-center gap-4 px-6 py-4 hover:bg-gray-50/50 transition-colors">
                <div
                  class="w-10 h-10 bg-blue-50 text-blue-500 rounded-xl flex items-center justify-center shrink-0"
                >
                  <BookOpenIcon class="w-5 h-5" />
                </div>
                <div class="flex-1">
                  <p class="text-xs text-gray-400 font-medium">Khóa học đã đăng ký</p>
                  <p class="text-xl font-black text-gray-900 leading-tight">
                    {{ stats.enrolledCourses }}
                  </p>
                </div>
                <div class="text-right shrink-0">
                  <p class="text-xs font-bold text-blue-600">{{ stats.completedCourses }} xong</p>
                </div>
              </div>

              <div class="flex items-center gap-4 px-6 py-4 hover:bg-gray-50/50 transition-colors">
                <div
                  class="w-10 h-10 bg-violet-50 text-violet-500 rounded-xl flex items-center justify-center shrink-0"
                >
                  <CheckIcon class="w-5 h-5" />
                </div>
                <div class="flex-1">
                  <p class="text-xs text-gray-400 font-medium">Bài học đã học</p>
                  <p class="text-xl font-black text-gray-900 leading-tight">
                    {{ stats.completedLessons }}
                  </p>
                </div>
                <div class="text-right shrink-0">
                  <p class="text-xs font-bold text-gray-400">/ {{ stats.totalLessons }} bài</p>
                </div>
              </div>

              <div class="flex items-center gap-4 px-6 py-4 hover:bg-gray-50/50 transition-colors">
                <div
                  class="w-10 h-10 bg-amber-50 text-amber-500 rounded-xl flex items-center justify-center shrink-0"
                >
                  <ClockIcon class="w-5 h-5" />
                </div>
                <div class="flex-1">
                  <p class="text-xs text-gray-400 font-medium">Tổng giờ học</p>
                  <p class="text-xl font-black text-gray-900 leading-tight">
                    {{ stats.totalHours }}
                  </p>
                </div>
                <div class="text-right shrink-0">
                  <p class="text-xs font-bold text-gray-400">giờ</p>
                </div>
              </div>

              <div class="flex items-center gap-4 px-6 py-4 hover:bg-gray-50/50 transition-colors">
                <div
                  class="w-10 h-10 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center shrink-0"
                >
                  <TrophyIcon class="w-5 h-5" />
                </div>
                <div class="flex-1">
                  <p class="text-xs text-gray-400 font-medium">Chứng chỉ đã nhận</p>
                  <p class="text-xl font-black text-gray-900 leading-tight">
                    {{ stats.certificates }}
                  </p>
                </div>
                <div class="text-right shrink-0">
                  <TrophyIcon class="w-4 h-4 text-amber-400" />
                </div>
              </div>
            </div>
          </div>

          <!-- Learning tip card -->
          <div
            class="bg-gradient-to-br from-blue-600 to-indigo-700 rounded-2xl p-6 text-white shadow-lg shadow-blue-100 relative overflow-hidden"
          >
            <div class="absolute -bottom-8 -right-8 w-32 h-32 bg-white/10 rounded-full"></div>
            <div class="absolute -top-4 -left-4 w-20 h-20 bg-white/5 rounded-full"></div>
            <div class="relative z-10">
              <p class="text-[10px] font-black uppercase tracking-widest text-blue-200 mb-2">
                Mẹo hôm nay
              </p>
              <p class="text-sm font-bold leading-relaxed mb-5">
                "Luyện nghe mỗi ngày 15 phút hiệu quả hơn học dồn 2 tiếng mỗi tuần."
              </p>
              <div class="flex items-center gap-2 pt-4 border-t border-white/20">
                <TrendingUpIcon class="w-4 h-4 text-blue-200" />
                <span class="text-xs text-blue-100 font-medium"
                  >Học đều đặn để ghi nhớ lâu dài</span
                >
              </div>
            </div>
          </div>

          <!-- Completed courses section -->
          <div
            v-if="stats.completedCourses > 0"
            class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 text-center"
          >
            <div
              class="w-14 h-14 bg-amber-50 rounded-2xl flex items-center justify-center mx-auto mb-4"
            >
              <TrophyIcon class="w-7 h-7 text-amber-500" />
            </div>
            <p class="text-lg font-black text-gray-900 mb-1">🎉 Xuất sắc!</p>
            <p class="text-xs text-gray-500 mb-4">
              Bạn đã hoàn thành
              <span class="font-bold text-gray-800">{{ stats.completedCourses }}</span> khóa học.
              Tiếp tục phát huy!
            </p>
            <router-link
              to="/courses"
              class="block w-full py-2.5 text-xs font-bold text-blue-600 border border-blue-200 rounded-xl hover:bg-blue-50 transition-colors"
            >
              Học thêm khóa mới
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>
