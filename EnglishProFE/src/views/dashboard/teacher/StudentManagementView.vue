<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Filter Bar -->
      <div
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex flex-col md:flex-row gap-3 items-center"
      >
        <!-- Course Selector -->
        <select
          v-model.number="selectedCourseId"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition min-w-[240px]"
        >
          <option :value="0">-- Chọn khóa học --</option>
          <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }}</option>
        </select>

        <!-- Search -->
        <div class="relative flex-1 w-full">
          <svg
            class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo họ tên, email..."
            class="w-full pl-9 pr-4 py-2.5 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
          />
        </div>

        <select
          v-model.number="pageSize"
          @change="
            () => {
              currentPage = 1
            }
          "
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
        >
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
      </div>

      <!-- No course selected -->
      <div
        v-if="!selectedCourseId"
        class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
      >
        <div class="flex flex-col items-center justify-center py-24 gap-3">
          <svg
            class="w-16 h-16 text-gray-200"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
            />
          </svg>
          <p class="text-gray-400 font-medium">
            Vui lòng chọn một khóa học để xem danh sách học viên
          </p>
        </div>
      </div>

      <!-- Main Content -->
      <div v-else class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-24">
          <div class="flex flex-col items-center gap-3">
            <div
              class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <p class="text-sm text-gray-500">Đang tải dữ liệu...</p>
          </div>
        </div>

        <!-- Table -->
        <template v-else>
          <div class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap text-left">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    #
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Học viên
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Email
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Tiến độ
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Bài học
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Trạng thái
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Ngày đăng ký
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Hành động
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <!-- Empty -->
                <tr v-if="filteredStudents.length === 0">
                  <td colspan="8" class="py-16 text-center text-gray-400">
                    <div class="flex flex-col items-center gap-2">
                      <svg
                        class="w-12 h-12 text-gray-200"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="1.5"
                          d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                      </svg>
                      <p class="font-medium">Chưa có học viên nào đăng ký khóa học này</p>
                    </div>
                  </td>
                </tr>
                <!-- Rows -->
                <tr
                  v-for="(student, idx) in paginatedStudents"
                  :key="student.userId"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-4 text-gray-400 text-xs">
                    {{ (currentPage - 1) * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex items-center gap-3">
                      <div
                        class="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold shrink-0 shadow-sm"
                      >
                        {{ getInitials(student.fullName || student.username) }}
                      </div>
                      <div>
                        <p class="font-semibold text-gray-900 leading-none">
                          {{ student.fullName || 'Ẩn danh' }}
                        </p>
                        <p class="text-xs text-gray-400 mt-1">@{{ student.username }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-gray-600 text-xs">{{ student.email }}</td>
                  <td class="px-5 py-4 text-center">
                    <div class="flex items-center gap-2 justify-center">
                      <div class="w-20 h-2 bg-gray-100 rounded-full overflow-hidden">
                        <div
                          class="h-full rounded-full transition-all duration-500"
                          :class="getProgressBarColor(student.progressPercent)"
                          :style="{ width: `${student.progressPercent ?? 0}%` }"
                        ></div>
                      </div>
                      <span
                        class="text-xs font-bold"
                        :class="getProgressTextColor(student.progressPercent)"
                      >
                        {{ (student.progressPercent ?? 0).toFixed(0) }}%
                      </span>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-center text-sm text-gray-700">
                    <span class="font-bold text-blue-600">{{ student.completedLessons }}</span>
                    <span class="text-gray-400">/{{ student.totalLessons }}</span>
                  </td>
                  <td class="px-5 py-4 text-center">
                    <span
                      :class="getStatusClass(student.enrollmentStatus)"
                      class="px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider shadow-sm border"
                    >
                      {{ getStatusLabel(student.enrollmentStatus) }}
                    </span>
                  </td>
                  <td class="px-5 py-4 text-gray-500">
                    <p class="text-xs font-medium">{{ formatDate(student.enrolledAt) }}</p>
                  </td>
                  <td class="px-5 py-4 text-center">
                    <button
                      @click="openDetailModal(student)"
                      class="px-3 py-1.5 text-xs font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition border border-blue-100"
                    >
                      Chi tiết
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div
            class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50"
          >
            <p class="text-sm text-gray-500">
              Hiển thị
              <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
              trong
              <span class="font-semibold text-gray-700">{{ filteredStudents.length }}</span> học
              viên
            </p>
            <div class="flex items-center gap-1.5">
              <button
                :disabled="currentPage === 1"
                @click="goToPage(1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold shadow-sm"
              >
                «
              </button>
              <button
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold shadow-sm"
              >
                ‹
              </button>
              <button
                v-for="p in totalPages"
                :key="p"
                @click="goToPage(p)"
                :class="[
                  'w-10 h-10 rounded-xl text-sm font-semibold transition border shadow-sm',
                  p === currentPage
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 bg-white',
                ]"
              >
                {{ p }}
              </button>
              <button
                :disabled="currentPage === totalPages || totalPages === 0"
                @click="goToPage(currentPage + 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold shadow-sm"
              >
                ›
              </button>
              <button
                :disabled="currentPage === totalPages || totalPages === 0"
                @click="goToPage(totalPages)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold shadow-sm"
              >
                »
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- ═══ Detail Modal ═══ -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showDetail"
          class="fixed inset-0 z-[9999] flex items-center justify-center p-4"
          @click.self="showDetail = false"
        >
          <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" @click="showDetail = false"></div>
          <div
            class="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[85vh] overflow-hidden z-10 flex flex-col"
          >
            <!-- Header -->
            <div
              class="px-6 py-5 border-b border-gray-100 flex items-center justify-between shrink-0"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-11 h-11 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-bold text-sm shadow"
                >
                  {{ getInitials(selectedStudent?.fullName || selectedStudent?.username || '') }}
                </div>
                <div>
                  <h3 class="text-lg font-bold text-gray-900">{{ selectedStudent?.fullName }}</h3>
                  <p class="text-xs text-gray-400">
                    @{{ selectedStudent?.username }} · {{ selectedStudent?.email }}
                  </p>
                </div>
              </div>
              <button
                @click="showDetail = false"
                class="w-9 h-9 rounded-xl bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition text-gray-500"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>

            <!-- Body -->
            <div class="flex-1 overflow-y-auto p-6 space-y-6">
              <!-- Progress Overview -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="bg-blue-50 rounded-xl p-4 text-center border border-blue-100">
                  <p class="text-2xl font-bold text-blue-600">
                    {{ (selectedStudent?.progressPercent ?? 0).toFixed(0) }}%
                  </p>
                  <p class="text-xs text-blue-500 mt-1 font-medium">Tiến độ</p>
                </div>
                <div class="bg-green-50 rounded-xl p-4 text-center border border-green-100">
                  <p class="text-2xl font-bold text-green-600">
                    {{ selectedStudent?.completedLessons || 0 }}/{{
                      selectedStudent?.totalLessons || 0
                    }}
                  </p>
                  <p class="text-xs text-green-500 mt-1 font-medium">Bài học</p>
                </div>
                <div class="bg-purple-50 rounded-xl p-4 text-center border border-purple-100">
                  <p class="text-2xl font-bold text-purple-600">
                    {{ selectedStudent?.exerciseScores?.length || 0 }}
                  </p>
                  <p class="text-xs text-purple-500 mt-1 font-medium">Bài tập đã làm</p>
                </div>
                <div class="bg-amber-50 rounded-xl p-4 text-center border border-amber-100">
                  <p class="text-2xl font-bold text-amber-600">{{ averageScore }}%</p>
                  <p class="text-xs text-amber-500 mt-1 font-medium">Điểm TB</p>
                </div>
              </div>

              <!-- Enrollment Info -->
              <div class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                <h4 class="text-sm font-bold text-gray-700 mb-3">Thông tin đăng ký</h4>
                <div class="grid grid-cols-1 md:grid-cols-4 gap-3 text-sm">
                  <div>
                    <span class="text-gray-400 text-xs block">Ngày đăng ký</span>
                    <span class="font-medium text-gray-800">{{
                      formatDate(selectedStudent?.enrolledAt || '')
                    }}</span>
                  </div>
                  <div>
                    <span class="text-gray-400 text-xs block">Đăng nhập gần nhất</span>
                    <span class="font-medium text-gray-800">{{
                      selectedStudent?.lastLoginAt
                        ? formatDateTime(selectedStudent.lastLoginAt)
                        : '—'
                    }}</span>
                  </div>
                  <div>
                    <span class="text-gray-400 text-xs block">Trạng thái</span>
                    <span
                      :class="getStatusClass(selectedStudent?.enrollmentStatus || '')"
                      class="px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider border inline-block mt-0.5"
                    >
                      {{ getStatusLabel(selectedStudent?.enrollmentStatus || '') }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- Exercise Scores Table -->
              <div>
                <h4 class="text-sm font-bold text-gray-700 mb-3">Điểm bài tập</h4>
                <div
                  v-if="!selectedStudent?.exerciseScores?.length"
                  class="text-center py-8 text-gray-400 text-sm"
                >
                  Học viên chưa làm bài tập nào
                </div>
                <div v-else class="border border-gray-100 rounded-xl overflow-x-auto">
                  <table class="w-full text-sm whitespace-nowrap">
                    <thead>
                      <tr class="bg-gray-50 border-b border-gray-100">
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider"
                        >
                          Bài tập
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider"
                        >
                          Bài học
                        </th>
                        <th
                          class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider"
                        >
                          Kết quả
                        </th>
                        <th
                          class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider"
                        >
                          Điểm
                        </th>
                        <th
                          class="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider"
                        >
                          Ngày làm
                        </th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-50">
                      <tr
                        v-for="score in selectedStudent.exerciseScores"
                        :key="score.exerciseId"
                        class="hover:bg-gray-50/50"
                      >
                        <td class="px-4 py-3 font-medium text-gray-800">
                          {{ score.exerciseTitle }}
                        </td>
                        <td class="px-4 py-3 text-gray-500 text-xs">{{ score.lessonTitle }}</td>
                        <td class="px-4 py-3 text-center">
                          <span class="text-xs font-bold">
                            <span class="text-green-600">{{ score.correctAnswers }}</span>
                            <span class="text-gray-400">/{{ score.totalQuestions }}</span>
                          </span>
                        </td>
                        <td class="px-4 py-3 text-center">
                          <span
                            class="px-2.5 py-1 rounded-full text-xs font-bold border"
                            :class="getScoreClass(score.scorePercent)"
                          >
                            {{ score.scorePercent.toFixed(0) }}%
                          </span>
                        </td>
                        <td class="px-4 py-3 text-right text-xs text-gray-400">
                          {{ score.submittedAt ? formatDate(score.submittedAt) : '—' }}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getTeacherCourses } from '@/api/courses'
import { getCourseStudents, type CourseStudentDetail } from '@/api/stats'

// ── State ─────────────────────────────────────────────────────────────────
const courses = ref<{ id: number; title: string }[]>([])
const selectedCourseId = ref(0)
const students = ref<CourseStudentDetail[]>([])
const loading = ref(false)
const searchQuery = ref('')
const pageSize = ref(10)
const currentPage = ref(1)

// Detail modal
const showDetail = ref(false)
const selectedStudent = ref<CourseStudentDetail | null>(null)

// ── Lifecycle ─────────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    const page = await getTeacherCourses(1, 100)
    courses.value = (page.content || []).map((c: any) => ({ id: c.id, title: c.title }))
  } catch (e) {
    console.error('Failed to load courses:', e)
  }
})

// Load students when course changes
watch(selectedCourseId, async (courseId) => {
  if (!courseId) {
    students.value = []
    return
  }
  loading.value = true
  try {
    students.value = await getCourseStudents(courseId)
  } catch (e) {
    console.error('Failed to load students:', e)
    students.value = []
  } finally {
    loading.value = false
    currentPage.value = 1
  }
})

// ── Computed ──────────────────────────────────────────────────────────────
const filteredStudents = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return students.value
  return students.value.filter(
    (s) =>
      s.fullName?.toLowerCase().includes(q) ||
      s.username.toLowerCase().includes(q) ||
      s.email.toLowerCase().includes(q),
  )
})

const totalPages = computed(() => Math.ceil(filteredStudents.value.length / pageSize.value))
const displayStart = computed(() =>
  filteredStudents.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min(currentPage.value * pageSize.value, filteredStudents.value.length),
)
const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredStudents.value.slice(start, start + pageSize.value)
})

const averageScore = computed(() => {
  const scores = selectedStudent.value?.exerciseScores
  if (!scores || scores.length === 0) return '0'
  const avg = scores.reduce((sum, s) => sum + s.scorePercent, 0) / scores.length
  return avg.toFixed(0)
})

// ── Methods ──────────────────────────────────────────────────────────────
const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) currentPage.value = page
}

watch(searchQuery, () => {
  currentPage.value = 1
})

const openDetailModal = (student: CourseStudentDetail) => {
  selectedStudent.value = student
  showDetail.value = true
}

const getInitials = (name: string) => {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .substring(0, 2)
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getProgressBarColor = (percent: number) => {
  if (percent >= 80) return 'bg-green-500'
  if (percent >= 40) return 'bg-blue-500'
  return 'bg-amber-500'
}

const getProgressTextColor = (percent: number) => {
  if (percent >= 80) return 'text-green-600'
  if (percent >= 40) return 'text-blue-600'
  return 'text-amber-600'
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'COMPLETED':
      return 'bg-green-50 text-green-700 border-green-200'
    case 'IN_PROGRESS':
      return 'bg-blue-50 text-blue-700 border-blue-200'
    case 'EXPIRED':
      return 'bg-red-50 text-red-700 border-red-200'
    default:
      return 'bg-gray-50 text-gray-700 border-gray-200'
  }
}

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'COMPLETED':
      return 'Hoàn thành'
    case 'IN_PROGRESS':
      return 'Đang học'
    case 'EXPIRED':
      return 'Hết hạn'
    default:
      return status
  }
}

const getScoreClass = (percent: number) => {
  if (percent >= 80) return 'bg-green-50 text-green-700 border-green-200'
  if (percent >= 50) return 'bg-blue-50 text-blue-700 border-blue-200'
  return 'bg-red-50 text-red-700 border-red-200'
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}

/* Modal transition */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-active .relative,
.modal-leave-active .relative {
  transition:
    transform 0.2s ease,
    opacity 0.2s ease;
}
.modal-enter-from .relative {
  transform: scale(0.95);
  opacity: 0;
}
.modal-leave-to .relative {
  transform: scale(0.95);
  opacity: 0;
}
</style>
