<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white">
    <!-- Header -->
    <div class="sticky top-0 z-20 bg-white border-b border-gray-200 shadow-sm">
      <div class="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
        <div>
          <h1 class="text-xl font-bold text-gray-900">{{ detail?.examTitle || 'Chi tiết bài thi' }}</h1>
          <p class="text-sm text-gray-500 mt-1">
            Lần thi #{{ detail?.attemptNumber }} •
            Nộp lúc {{ formatDate(detail?.submittedAt) }}
          </p>
        </div>
        <div class="flex items-center gap-4">
          <!-- Score badge -->
          <div class="text-center">
            <div
              :class="[
                'text-2xl font-bold',
                detail?.isPassed ? 'text-green-600' : 'text-red-600',
              ]"
            >
              {{ detail?.score ?? 0 }} điểm
            </div>
            <span
              v-if="detail?.isPassed != null"
              :class="[
                'text-xs font-semibold px-2 py-0.5 rounded-full',
                detail?.isPassed
                  ? 'bg-green-100 text-green-700'
                  : 'bg-red-100 text-red-700',
              ]"
            >
              {{ detail?.isPassed ? 'ĐẬU' : 'CHƯA ĐẬU' }}
            </span>
          </div>
          <button
            @click="goBack"
            class="px-4 py-2 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition-colors"
          >
            ← Quay lại
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-24">
      <div class="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-gray-500">Đang tải chi tiết bài thi...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="max-w-5xl mx-auto px-6 py-24 text-center">
      <p class="text-red-500 text-lg mb-4">{{ error }}</p>
      <button
        @click="goBack"
        class="px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
      >
        ← Quay lại
      </button>
    </div>

    <!-- Content -->
    <div v-else-if="detail" class="max-w-5xl mx-auto px-6 py-8">
      <!-- Stats Summary -->
      <div class="grid grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-xl border border-gray-200 p-4 text-center shadow-sm">
          <p class="text-3xl font-bold text-green-600">{{ stats.correct }}</p>
          <p class="text-xs text-gray-500 mt-1">Trả lời đúng</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 text-center shadow-sm">
          <p class="text-3xl font-bold text-red-600">{{ stats.wrong }}</p>
          <p class="text-xs text-gray-500 mt-1">Trả lời sai</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 text-center shadow-sm">
          <p class="text-3xl font-bold text-gray-400">{{ stats.unanswered }}</p>
          <p class="text-xs text-gray-500 mt-1">Bỏ trống</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-200 p-4 text-center shadow-sm">
          <p class="text-3xl font-bold text-blue-600">{{ stats.total }}</p>
          <p class="text-xs text-gray-500 mt-1">Tổng số câu</p>
        </div>
      </div>

      <!-- Filter Tabs -->
      <div class="flex gap-2 mb-6">
        <button
          v-for="tab in filterTabs"
          :key="tab.value"
          @click="activeFilter = tab.value"
          :class="[
            'px-4 py-2 rounded-lg text-sm font-semibold transition-all',
            activeFilter === tab.value
              ? 'bg-blue-600 text-white shadow-sm'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
          ]"
        >
          {{ tab.label }}
          <span class="ml-1 text-xs opacity-75">({{ tab.count }})</span>
        </button>
      </div>

      <!-- Sections & Questions -->
      <div v-for="section in detail.sections" :key="section.sectionId" class="mb-8">
        <h2
          v-if="getFilteredQuestions(section.questions).length > 0"
          class="text-lg font-bold text-gray-900 mb-4 pb-2 border-b-2 border-blue-500"
        >
          {{ section.sectionName }}
        </h2>

        <div class="space-y-4">
          <div
            v-for="question in getFilteredQuestions(section.questions)"
            :key="question.questionId"
            :class="[
              'bg-white rounded-xl border-2 p-6 shadow-sm transition-all',
              question.isCorrect === true
                ? 'border-green-300'
                : question.isCorrect === false
                  ? 'border-red-300'
                  : 'border-gray-200',
            ]"
          >
            <!-- Question Header -->
            <div class="flex items-start justify-between mb-4">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span class="bg-blue-100 text-blue-700 text-xs font-bold px-2 py-1 rounded-lg">
                    Câu {{ getGlobalIndex(section, question) }}
                  </span>
                  <span
                    v-if="question.points"
                    class="text-xs text-gray-400 bg-gray-100 px-2 py-1 rounded-lg"
                  >
                    {{ question.points }} điểm
                  </span>
                </div>
                <p class="text-gray-900 font-semibold leading-relaxed">
                  {{ question.questionText }}
                </p>
              </div>

              <!-- Status Badge -->
              <div class="ml-4 shrink-0">
                <span
                  v-if="question.isCorrect === true"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-bold bg-green-100 text-green-700"
                >
                  ✓ Đúng
                </span>
                <span
                  v-else-if="question.isCorrect === false"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-bold bg-red-100 text-red-700"
                >
                  ✗ Sai
                </span>
                <span
                  v-else
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-bold bg-gray-100 text-gray-500"
                >
                  — Bỏ trống
                </span>
              </div>
            </div>

            <!-- Media -->
            <div v-if="question.mediaUrl" class="mb-4">
              <div v-if="isAudioUrl(question.mediaUrl)" class="p-3 bg-blue-50 rounded-lg">
                <audio :src="question.mediaUrl" controls class="w-full"></audio>
              </div>
              <img
                v-else
                :src="question.mediaUrl"
                :alt="`Question ${getGlobalIndex(section, question)}`"
                class="max-w-full h-auto max-h-80 object-contain rounded-lg border border-gray-200 bg-gray-50"
              />
            </div>

            <!-- Options (Multiple Choice) -->
            <div v-if="question.options && question.options.length > 0" class="space-y-2 mb-4">
              <div
                v-for="(option, optIdx) in question.options"
                :key="optIdx"
                :class="[
                  'border-2 rounded-lg px-4 py-3 text-sm transition-all',
                  getOptionClass(question, option),
                ]"
              >
                <div class="flex items-center gap-3">
                  <span
                    :class="[
                      'w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold shrink-0',
                      option === question.correctAnswer
                        ? 'bg-green-500 text-white'
                        : option === question.submittedAnswer && question.isCorrect === false
                          ? 'bg-red-500 text-white'
                          : 'bg-gray-200 text-gray-600',
                    ]"
                  >
                    {{ getOptionLabel(Number(optIdx)) }}
                  </span>
                  <span
                    :class="[
                      'flex-1',
                      option === question.correctAnswer
                        ? 'font-semibold text-green-800'
                        : option === question.submittedAnswer && question.isCorrect === false
                          ? 'text-red-700 line-through'
                          : 'text-gray-700',
                    ]"
                  >
                    {{ option }}
                  </span>
                  <span v-if="option === question.correctAnswer" class="text-green-600 text-lg">✓</span>
                  <span
                    v-else-if="option === question.submittedAnswer && question.isCorrect === false"
                    class="text-red-500 text-lg"
                  >✗</span>
                </div>
              </div>
            </div>

            <!-- Essay / Text Answer -->
            <div v-else class="mb-4">
              <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                <p class="text-xs text-gray-500 mb-1 font-semibold">Câu trả lời của bạn:</p>
                <p class="text-gray-900">{{ question.submittedAnswer || 'Không trả lời' }}</p>
              </div>
              <div v-if="question.correctAnswer" class="bg-green-50 rounded-lg p-4 border border-green-200 mt-2">
                <p class="text-xs text-green-600 mb-1 font-semibold">Đáp án đúng:</p>
                <p class="text-green-800 font-semibold">{{ question.correctAnswer }}</p>
              </div>
            </div>

            <!-- Explanation -->
            <div
              v-if="question.explanation"
              class="bg-blue-50 border border-blue-200 rounded-lg p-4 mt-3"
            >
              <p class="text-xs font-semibold text-blue-700 mb-1">💡 Giải thích:</p>
              <p class="text-sm text-blue-900 leading-relaxed">{{ question.explanation }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Actions -->
      <div class="flex gap-4 justify-center py-8 border-t border-gray-200">
        <button
          @click="goBack"
          class="px-8 py-3 bg-gray-200 text-gray-900 font-semibold rounded-lg hover:bg-gray-300 transition-colors"
        >
          ← Quay lại kết quả
        </button>
        <button
          @click="retakeExam"
          class="px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
        >
          Làm lại đề thi 🔄
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getExamResultDetail } from '../../api/exam'
import { useExamStore } from '../../stores/exam'

const router = useRouter()
const route = useRoute()
const examStore = useExamStore()

// State
const loading = ref(true)
const error = ref<string | null>(null)
const detail = ref<any>(null)
const activeFilter = ref('all')

// Stats
const stats = computed(() => {
  if (!detail.value?.sections) return { correct: 0, wrong: 0, unanswered: 0, total: 0 }

  let correct = 0
  let wrong = 0
  let unanswered = 0

  for (const section of detail.value.sections) {
    for (const q of section.questions || []) {
      if (q.isCorrect === true) correct++
      else if (q.isCorrect === false) wrong++
      else unanswered++
    }
  }
  return { correct, wrong, unanswered, total: correct + wrong + unanswered }
})

// Filter tabs
const filterTabs = computed(() => [
  { label: 'Tất cả', value: 'all', count: stats.value.total },
  { label: '✓ Đúng', value: 'correct', count: stats.value.correct },
  { label: '✗ Sai', value: 'wrong', count: stats.value.wrong },
  { label: '— Bỏ trống', value: 'unanswered', count: stats.value.unanswered },
])

// Methods
const getFilteredQuestions = (questions: any[]) => {
  if (activeFilter.value === 'all') return questions
  return questions.filter((q: any) => {
    if (activeFilter.value === 'correct') return q.isCorrect === true
    if (activeFilter.value === 'wrong') return q.isCorrect === false
    if (activeFilter.value === 'unanswered') return q.isCorrect == null
    return true
  })
}

const getGlobalIndex = (section: any, question: any) => {
  if (!detail.value?.sections) return 0
  let idx = 0
  for (const s of detail.value.sections) {
    for (const q of s.questions || []) {
      idx++
      if (q.questionId === question.questionId) return idx
    }
  }
  return idx
}

const getOptionLabel = (index: number) => String.fromCharCode(65 + index)

const isAudioUrl = (url: string) => {
  if (!url) return false
  return /\.(mp3|wav|ogg|aac|m4a)$/i.test(url) || url.includes('/audio/')
}

const getOptionClass = (question: any, option: string) => {
  const isCorrectOption = option === question.correctAnswer
  const isUserAnswer = option === question.submittedAnswer

  if (isCorrectOption) return 'border-green-400 bg-green-50'
  if (isUserAnswer && question.isCorrect === false) return 'border-red-400 bg-red-50'
  return 'border-gray-200 bg-white'
}

const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return ''
  try {
    return new Date(dateStr).toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return dateStr
  }
}

const goBack = () => {
  router.back()
}

const retakeExam = () => {
  examStore.resetExam()
  router.push({ name: 'exam-list' })
}

// Fetch detail
onMounted(async () => {
  try {
    const resultId = route.params.resultId as string
    if (!resultId) {
      error.value = 'Không tìm thấy mã kết quả thi'
      return
    }

    const data = await getExamResultDetail(resultId)
    detail.value = data
  } catch (err: any) {
    console.error('Failed to load exam detail:', err)
    error.value =
      err?.response?.data?.message || 'Không thể tải chi tiết bài thi. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
})
</script>
