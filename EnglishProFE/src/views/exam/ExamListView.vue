<template>
  <DashboardLayout>
    <div class="space-y-7">
      <!-- Filters & Search -->
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <div class="relative group flex-1 w-full">
          <svg class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-gray-400 group-focus-within:text-blue-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm đề thi theo tên..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
            @input="onSearchInput"
          />
        </div>
        <div class="flex bg-gray-100 rounded-xl p-1 gap-1 shrink-0">
          <button
            v-for="tab in filterTabs"
            :key="tab.value"
            @click="changeFilter(tab.value)"
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
      </div>

      <!-- Loading -->
      <div v-if="loading" class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col items-center justify-center py-20">
        <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-500">Đang tải danh sách đề thi...</p>
      </div>

      <!-- Exam Cards Grid -->
      <div v-else-if="exams.length > 0">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="exam in exams"
            :key="exam.id"
            class="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg hover:border-blue-400 transition-all duration-300 flex flex-col"
          >
            <!-- Card Header with Gradient -->
            <div
              :class="[
                'h-32 px-6 py-6 flex flex-col justify-center',
                exam.examType === 'TOEIC'
                  ? 'bg-gradient-to-r from-blue-600 to-blue-400'
                  : 'bg-gradient-to-r from-indigo-600 to-purple-500',
              ]"
            >
              <div class="flex items-center justify-between">
                <h3 class="text-xl font-bold text-white truncate mr-2">{{ exam.title }}</h3>
                <span
                  :class="[
                    'px-3 py-1 rounded-full text-xs font-semibold shrink-0',
                    exam.examType === 'TOEIC'
                      ? 'bg-white/20 text-white'
                      : 'bg-white/20 text-white',
                  ]"
                >
                  {{ exam.examType }}
                </span>
              </div>
              <p class="text-white/70 text-sm mt-2">Tổng điểm: {{ exam.totalScore }}</p>
            </div>

            <!-- Card Body -->
            <div class="p-6 flex-1 flex flex-col">
              <!-- Description -->
              <p class="text-gray-600 text-sm mb-4 line-clamp-2">{{ exam.description || 'Chưa có mô tả' }}</p>

              <!-- Info Grid -->
              <div class="space-y-3 mb-6">
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-600">⏱️ Thời gian:</span>
                  <span class="font-semibold text-gray-900">{{ exam.duration ? Math.round(exam.duration / 60) : 0 }} phút</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-600">📉 Điểm đậu:</span>
                  <span class="font-semibold text-gray-900">{{ exam.passingScore || 0 }}</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-600">🔄 Lượt làm tối đa:</span>
                  <span class="font-semibold text-gray-900">{{ exam.maxAttempts ? exam.maxAttempts : 'Không giới hạn' }}</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-600">📈 Độ khó:</span>
                  <span
                    :class="[
                      'font-semibold px-2 py-1 rounded',
                      exam.level === 'BEGINNER'
                        ? 'bg-green-100 text-green-700'
                        : exam.level === 'INTERMEDIATE'
                          ? 'bg-yellow-100 text-yellow-700'
                          : 'bg-red-100 text-red-700',
                    ]"
                  >
                    {{ getLevelText(exam.level) }}
                  </span>
                </div>
              </div>

              <!-- Start Button -->
              <button
                @click="startExam(exam)"
                class="w-full bg-blue-600 text-white font-semibold py-3 rounded-lg hover:bg-blue-700 transition-colors shadow-md hover:shadow-lg mt-auto"
              >
                Bắt đầu thi →
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3 mt-6 bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4">
          <p class="text-sm text-gray-500">
            Hiển thị <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
            trong số <span class="font-semibold text-gray-700">{{ totalElements }}</span> đề thi
          </p>
          <div class="flex items-center gap-1.5">
            <button :disabled="page === 0" @click="goToPage(0)" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">«</button>
            <button :disabled="page === 0" @click="goToPage(page - 1)" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">‹</button>
            <button
              v-for="p in pageNumbers"
              :key="p"
              @click="goToPage(p)"
              :class="['w-10 h-10 rounded-xl text-sm font-semibold transition border', page === p ? 'bg-blue-600 text-white border-blue-600 shadow' : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300']"
            >{{ p + 1 }}</button>
            <button :disabled="isLastPage" @click="goToPage(page + 1)" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">›</button>
            <button :disabled="isLastPage" @click="goToPage(totalPages - 1)" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">»</button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-gray-100">
        <div class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
          <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
        </div>
        <h4 class="text-xl font-bold text-gray-900 mb-2">Không tìm thấy đề thi nào</h4>
        <p class="text-gray-500 max-w-sm mx-auto">Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
      </div>
    </div>

    <!-- Rules Modal -->
    <Teleport to="body" v-if="showModal">
      <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-xl max-w-lg w-full shadow-2xl">
          <!-- Modal Header -->
          <div class="bg-blue-600 text-white px-6 py-6 rounded-t-xl">
            <h2 class="text-2xl font-bold">Quy chế thi</h2>
            <p class="text-blue-100 text-sm mt-1">{{ selectedExamForModal?.title }}</p>
          </div>

          <!-- Modal Content -->
          <div class="px-6 py-6 space-y-4">
            <div class="bg-red-50 border border-red-200 rounded-lg p-4">
              <h3 class="font-semibold text-red-900 mb-2">⚠️ Lưu ý quan trọng:</h3>
              <ul class="space-y-2 text-red-900 text-sm">
                <li>• ⏱️ Thời gian thi sẽ đếm ngược, không thể tạm dừng</li>
                <li>• 🔒 Không thể quay lại câu trước khi nộp bài</li>
                <li>• 🚫 Vấn đề kỹ thuật sẽ không được gia hạn thời gian</li>
                <li>• 📱 Không được sử dụng các tab/ứng dụng khác</li>
              </ul>
            </div>

            <div class="grid grid-cols-2 gap-4 pt-4 border-t border-gray-200">
              <div class="text-center">
                <p class="text-gray-600 text-sm">Thời gian:</p>
                <p class="text-2xl font-bold text-blue-600">
                  {{ selectedExamForModal?.duration ? Math.round(selectedExamForModal.duration / 60) : 0 }} phút
                </p>
              </div>
              <div class="text-center">
                <p class="text-gray-600 text-sm">Tổng điểm:</p>
                <p class="text-2xl font-bold text-blue-600">
                  {{ selectedExamForModal?.totalScore }}
                </p>
              </div>
            </div>

            <div class="flex items-center gap-2 p-3 bg-blue-50 rounded-lg">
              <input type="checkbox" v-model="agreedToRules" class="w-4 h-4" />
              <label class="text-sm text-gray-700">Tôi đã đọc và đồng ý với quy chế thi</label>
            </div>
          </div>

          <!-- Modal Footer -->
          <div class="px-6 py-4 bg-gray-50 flex gap-3 rounded-b-xl">
            <button
              @click="closeModal"
              class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
            >
              Hủy
            </button>
            <button
              @click="confirmStartExam"
              :disabled="!agreedToRules"
              :class="[
                'flex-1 px-4 py-2 font-semibold rounded-lg transition-colors',
                agreedToRules
                  ? 'bg-blue-600 text-white hover:bg-blue-700'
                  : 'bg-gray-300 text-gray-500 cursor-not-allowed',
              ]"
            >
              Bắt đầu ngay
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { useExamStore } from '../../stores/exam'
import { getPublishedExams } from '../../api/exam'
import { useDialog } from '@/composables/useDialog'
import { useToastStore } from '@/stores/toast'

const router = useRouter()
const examStore = useExamStore()
const dialog = useDialog()
const toastStore = useToastStore()

// ── State ──────────────────────────────────────────────────────────────────────
const exams = ref<any[]>([])
const loading = ref(true)
const page = ref(0)
const pageSize = 9 // 3x3 grid
const totalElements = ref(0)
const searchQuery = ref('')
const activeFilter = ref('ALL')
const showModal = ref(false)
const selectedExamForModal = ref<any | null>(null)
const agreedToRules = ref(false)

let searchTimeout: ReturnType<typeof setTimeout> | null = null

const filterTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'TOEIC', value: 'TOEIC' },
  { label: 'IELTS', value: 'IELTS' },
]

// ── Computed pagination ────────────────────────────────────────────────────────
const totalPages = computed(() => Math.max(1, Math.ceil(totalElements.value / pageSize)))
const isLastPage = computed(() => (page.value + 1) * pageSize >= totalElements.value)
const displayStart = computed(() => totalElements.value === 0 ? 0 : page.value * pageSize + 1)
const displayEnd = computed(() => Math.min((page.value + 1) * pageSize, totalElements.value))
const pageNumbers = computed(() => {
  const total = totalPages.value, current = page.value, delta = 2
  const range: number[] = []
  for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++)
    range.push(i)
  return range
})

// ── Helpers ────────────────────────────────────────────────────────────────────
const getLevelText = (level: string) => {
  const map: Record<string, string> = {
    BEGINNER: 'Dễ',
    INTERMEDIATE: 'Trung bình',
    ADVANCED: 'Khó',
  }
  return map[level] || level || 'Chưa xác định'
}

// ── Fetch ──────────────────────────────────────────────────────────────────────
const fetchExams = async () => {
  loading.value = true
  try {
    const params: any = { page: page.value, size: pageSize }
    if (activeFilter.value !== 'ALL') params.examType = activeFilter.value
    if (searchQuery.value.trim()) params.keyword = searchQuery.value.trim()

    const res = await getPublishedExams(params)
    // ApiResponse { data: Page { content, page } }
    const pageData = res?.data || res
    if (pageData && pageData.content) {
      exams.value = pageData.content
      totalElements.value = pageData.page?.totalElements || pageData.totalElements || 0
    }
  } catch (error) {
    console.error('[ExamListView] Failed to load exams:', error)
  } finally {
    loading.value = false
  }
}

const goToPage = (p: number) => {
  if (p < 0 || p >= totalPages.value) return
  page.value = p
  fetchExams()
}

const changeFilter = (value: string) => {
  activeFilter.value = value
  page.value = 0
  fetchExams()
}

const onSearchInput = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    page.value = 0
    fetchExams()
  }, 400)
}

// ── Modal ──────────────────────────────────────────────────────────────────────
const startExam = (exam: any) => {
  selectedExamForModal.value = exam
  agreedToRules.value = false
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedExamForModal.value = null
  agreedToRules.value = false
}

const confirmStartExam = async () => {
  if (!selectedExamForModal.value) return
  try {
    const exam = selectedExamForModal.value
    const examId = exam.id
    await examStore.initializeExam(examId)
    // Set metadata that the start API doesn't return
    examStore.setExamMeta({
      title: exam.title,
      totalScore: exam.totalScore,
    })
    examStore.startExam()
    closeModal()
    await router.push({ name: 'exam-taking', params: { id: examId } })
  } catch (error: any) {
    console.error('[ExamListView] Failed to start exam:', error)
    closeModal()
    // Extract readable error message from backend response
    const msg = error?.response?.data?.message || (error instanceof Error ? error.message : 'Lỗi không xác định')
    toastStore.error(msg)
  }
}

// ── Lifecycle ──────────────────────────────────────────────────────────────────
onMounted(fetchExams)
</script>
