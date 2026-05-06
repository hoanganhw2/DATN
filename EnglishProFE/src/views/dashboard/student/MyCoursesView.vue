<template>
  <DashboardLayout>
    <div class="space-y-7">

      <!-- Summary Stats Chips -->
      <div class="flex flex-wrap gap-3">
        <div class="flex items-center gap-2 bg-blue-50 border border-blue-100 rounded-xl px-4 py-2 shadow-sm">
          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
          <span class="text-sm font-semibold text-blue-700">{{ totalEnrolled }} Đã đăng ký</span>
        </div>
        <div class="flex items-center gap-2 bg-green-50 border border-green-100 rounded-xl px-4 py-2 shadow-sm">
          <div class="w-2 h-2 bg-green-500 rounded-full"></div>
          <span class="text-sm font-semibold text-green-700">{{ completedCount }} Đã hoàn thành</span>
        </div>
        <div class="flex items-center gap-2 bg-orange-50 border border-orange-100 rounded-xl px-4 py-2 shadow-sm">
          <div class="w-2 h-2 bg-orange-500 rounded-full"></div>
          <span class="text-sm font-semibold text-orange-700">{{ inProgressCount }} Đang học</span>
        </div>
      </div>

      <!-- Filters & Search -->
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <div class="relative group flex-1 w-full">
          <SearchIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-gray-400 group-focus-within:text-blue-500 transition-colors" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm khóa học..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
          />
        </div>
        <!-- Status Filter tabs -->
        <div class="flex bg-gray-100 rounded-xl p-1 gap-1 shrink-0">
          <button
            v-for="tab in filterTabs"
            :key="tab.value"
            @click="activeFilter = tab.value"
            :class="[
              'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
              activeFilter === tab.value
                ? 'bg-white text-blue-600 shadow-sm'
                : 'text-gray-500 hover:text-gray-700',
            ]"
          >
            {{ tab.label }}
          </button>
        </div>
        <!-- Sort -->
        <select
          v-model="sortBy"
          class="bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm shrink-0"
        >
          <option value="recent">Gần đây nhất</option>
          <option value="progress">Tiến độ cao nhất</option>
          <option value="name">Tên A-Z</option>
        </select>
      </div>

      <!-- Course Table -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-24">
          <div class="flex flex-col items-center gap-3">
            <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
            <p class="text-sm text-gray-500">Đang tải dữ liệu...</p>
          </div>
        </div>

        <template v-else>
          <div class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">#</th>
                  <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Khóa học</th>
                  <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Trạng thái</th>
                  <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Tiến độ</th>
                  <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Ngày đăng ký</th>
                  <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Thao tác</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <!-- Empty -->
                <tr v-if="paginatedCourses.length === 0">
                  <td colspan="6" class="py-16 text-center text-gray-400">
                    <div class="flex flex-col items-center gap-2">
                      <BookOpenIcon class="w-12 h-12 text-gray-200" />
                      <p class="font-medium">Không tìm thấy khóa học</p>
                      <router-link to="/courses" class="mt-2 px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 transition">Khám phá khóa học</router-link>
                    </div>
                  </td>
                </tr>
                <!-- Rows -->
                <tr
                  v-for="(course, idx) in paginatedCourses"
                  :key="course.courseId"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-4 text-gray-400 text-xs">
                    {{ (currentPage - 1) * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-4 min-w-[300px]">
                    <div class="flex items-center gap-4">
                      <div class="w-20 h-14 rounded-lg overflow-hidden shrink-0 border border-gray-100">
                        <img :src="course.thumbnailUrl || 'https://images.unsplash.com/photo-1546410531-bb4caa6b0e8b?w=200&h=140&fit=crop'" class="w-full h-full object-cover" />
                      </div>
                      <div class="min-w-0">
                        <p class="font-semibold text-gray-900 truncate max-w-[250px]" :title="course.title">{{ course.title }}</p>
                        <p class="text-xs text-gray-500 mt-1 flex items-center gap-1">
                          <GraduationCapIcon class="w-3.5 h-3.5" />
                          {{ course.teacherName || 'N/A' }}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-center">
                    <span
                      :class="[
                        'px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider shadow-sm',
                        course.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : course.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-600'
                      ]"
                    >
                      {{ statusLabel(course.status) }}
                    </span>
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex flex-col items-center justify-center gap-1.5 w-32 mx-auto">
                      <div class="flex items-center justify-between w-full text-xs">
                        <span class="text-gray-500">{{ course.completedLessons }}/{{ course.totalLessons }} bài</span>
                        <span class="font-bold text-blue-600">{{ Math.round(course.progressPercent) }}%</span>
                      </div>
                      <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div
                          class="h-full rounded-full transition-all duration-700"
                          :class="course.status === 'COMPLETED' ? 'bg-green-500' : 'bg-blue-500'"
                          :style="{ width: Math.round(course.progressPercent) + '%' }"
                        ></div>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-gray-500 text-xs">
                    {{ formatDate(course.enrolledAt) }}
                    <div v-if="course.lastAccessedAt" class="mt-1 text-[10px] text-gray-400">
                      Vào: {{ timeAgo(course.lastAccessedAt) }}
                    </div>
                  </td>
                  <td class="px-5 py-4 text-center">
                    <div class="flex items-center justify-center gap-2">
                      <router-link
                        :to="`/learning/${course.courseId}`"
                        class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition"
                      >
                        <PlayIcon v-if="course.status !== 'COMPLETED'" class="w-3 h-3" />
                        <TrophyIcon v-else class="w-3 h-3" />
                        {{ course.status === 'COMPLETED' ? 'Xem lại' : 'Học tiếp' }}
                      </router-link>
                      <button
                        v-if="course.status === 'COMPLETED'"
                        @click="handleViewCertificate(course.courseId)"
                        :disabled="issuingCertId === course.courseId"
                        class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-yellow-700 bg-yellow-100 hover:bg-yellow-200 rounded-lg transition disabled:opacity-50"
                      >
                        <span v-if="issuingCertId === course.courseId" class="w-3 h-3 border-2 border-yellow-600 border-t-transparent rounded-full animate-spin"></span>
                        <span v-else>🏆</span>
                        {{ issuingCertId === course.courseId ? 'Đang cấp...' : 'C.Chỉ' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50">
            <p class="text-sm text-gray-500">
              Hiển thị <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
              trong <span class="font-semibold text-gray-700">{{ totalElements }}</span> khóa học
            </p>
            <div class="flex items-center gap-1.5">
              <button :disabled="currentPage === 1" @click="currentPage = 1" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">«</button>
              <button :disabled="currentPage === 1" @click="currentPage--" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">‹</button>
              <button
                v-for="p in pageNumbers"
                :key="p"
                @click="currentPage = p"
                :class="['w-10 h-10 rounded-xl text-sm font-semibold transition border', currentPage === p ? 'bg-blue-600 text-white border-blue-600 shadow' : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300']"
              >{{ p }}</button>
              <button :disabled="isLastPage" @click="currentPage++" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">›</button>
              <button :disabled="isLastPage" @click="currentPage = totalPages" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">»</button>
            </div>
          </div>
        </template>
      </div>

    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import BookOpenIcon from '@/components/dashboard/icons/BookOpenIcon.vue'
import PlayIcon from '@/components/dashboard/icons/PlayIcon.vue'
import ClockIcon from '@/components/dashboard/icons/ClockIcon.vue'
import SearchIcon from '@/components/dashboard/icons/SearchIcon.vue'
import SparklesIcon from '@/components/dashboard/icons/SparklesIcon.vue'
import TrophyIcon from '@/components/dashboard/icons/TrophyIcon.vue'
import GraduationCapIcon from '@/components/dashboard/icons/GraduationCapIcon.vue'
import { Calendar } from 'lucide-vue-next'

import { getMyEnrollments, type EnrolledCourse } from '@/api/enrollments'
import { issueCertificate } from '@/api/certificates'
import { useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'

const CalendarIcon = Calendar

// ── State ──────────────────────────────────────────────────────────────────

const router = useRouter()
const toastStore = useToastStore()

const courses    = ref<EnrolledCourse[]>([])
const loading    = ref(true)
const error      = ref<string | null>(null)
const issuingCertId = ref<number | null>(null)

const searchQuery  = ref('')
const activeFilter = ref('all')
const sortBy       = ref('recent')

// Pagination
const currentPage = ref(1)
const pageSize    = 6

// ── Filter tabs ────────────────────────────────────────────────────────────

const filterTabs = [
  { label: 'Tất cả',       value: 'all' },
  { label: 'Đang học',     value: 'IN_PROGRESS' },
  { label: 'Hoàn thành',   value: 'COMPLETED' },
  { label: 'Chưa bắt đầu', value: 'NOT_STARTED' },
]

// ── Helpers ────────────────────────────────────────────────────────────────

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    IN_PROGRESS:  'Đang học',
    COMPLETED:    'Hoàn thành',
    NOT_STARTED:  'Chưa bắt đầu',
  }
  return map[status] ?? status
}

const formatDate = (iso: string): string => {
  if (!iso) return ''
  return new Date(iso).toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })
}

const formatHours = (secs: number): string =>
  (secs / 3600).toFixed(1)

const timeAgo = (iso: string): string => {
  if (!iso) return ''
  const diff = Date.now() - new Date(iso).getTime()
  const mins  = Math.floor(diff / 60_000)
  const hours = Math.floor(mins / 60)
  const days  = Math.floor(hours / 24)
  const weeks = Math.floor(days / 7)
  const months = Math.floor(days / 30)
  if (months >= 1)  return `${months} tháng trước`
  if (weeks  >= 1)  return `${weeks} tuần trước`
  if (days   >= 1)  return days === 1 ? 'Hôm qua' : `${days} ngày trước`
  if (hours  >= 1)  return `${hours} giờ trước`
  return 'Vừa xong'
}

// ── Computed Stats ─────────────────────────────────────────────────────────

const totalEnrolled  = computed(() => courses.value.length)
const completedCount = computed(() => courses.value.filter(c => c.status === 'COMPLETED').length)
const inProgressCount = computed(() => courses.value.filter(c => c.status === 'IN_PROGRESS').length)

// ── Filtered / Sorted list ─────────────────────────────────────────────────

const filteredCourses = computed(() => {
  let result = courses.value

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(
      c => c.title.toLowerCase().includes(q) || (c.teacherName ?? '').toLowerCase().includes(q),
    )
  }

  if (activeFilter.value !== 'all') {
    result = result.filter(c => c.status === activeFilter.value)
  }

  if (sortBy.value === 'progress') {
    result = [...result].sort((a, b) => b.progressPercent - a.progressPercent)
  } else if (sortBy.value === 'name') {
    result = [...result].sort((a, b) => a.title.localeCompare(b.title))
  }

  return result
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredCourses.value.length / pageSize)))
const isLastPage = computed(() => currentPage.value >= totalPages.value)
const totalElements = computed(() => filteredCourses.value.length)
const displayStart = computed(() => totalElements.value === 0 ? 0 : (currentPage.value - 1) * pageSize + 1)
const displayEnd = computed(() => Math.min(currentPage.value * pageSize, totalElements.value))

const pageNumbers = computed(() => {
  const total = totalPages.value, current = currentPage.value, delta = 2
  const range: number[] = []
  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) range.push(i)
  return range
})
const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredCourses.value.slice(start, start + pageSize)
})

// Reset về trang 1 khi filter thay đổi
watch([searchQuery, activeFilter, sortBy], () => { currentPage.value = 1 })

// ── Fetch ──────────────────────────────────────────────────────────────────

const fetchEnrollments = async () => {
  loading.value = true
  error.value   = null
  try {
    const page = await getMyEnrollments(0, 50)
    courses.value = page.content
  } catch (e: any) {
    console.error('Lấy danh sách khóa học thất bại:', e)
    error.value = 'Không thể tải danh sách khóa học. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

onMounted(fetchEnrollments)

// ── Certificate ────────────────────────────────────────────────────────────

const handleViewCertificate = async (courseId: number) => {
  issuingCertId.value = courseId
  try {
    // Cấp chứng chỉ (idempotent — nếu đã cấp rồi sẽ trả về cert cũ)
    await issueCertificate(courseId)
    toastStore.success('🏆 Chứng chỉ đã sẵn sàng!')
    router.push('/dashboard/student/certificates')
  } catch (err: any) {
    const msg = err?.response?.data?.message || 'Không thể cấp chứng chỉ'
    toastStore.error(msg)
  } finally {
    issuingCertId.value = null
  }
}
</script>
