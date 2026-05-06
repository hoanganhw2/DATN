<template>
  <div class="flex h-screen bg-gray-50 flex-col">
    <!-- Sticky Header -->
    <div class="sticky top-0 z-20 bg-white border-b border-gray-300 shadow-md">
      <div class="px-6 py-4 flex items-center justify-between">
        <!-- Left: Exam Title & Progress -->
        <div class="flex-1">
          <h1 class="text-xl font-bold text-gray-900">{{ examStore.currentExam?.title || 'Đề thi' }}</h1>
          <div class="mt-2 w-full bg-gray-200 rounded-full h-2">
            <div
              class="bg-blue-600 h-2 rounded-full transition-all duration-300"
              :style="{ width: examStore.progressPercentage + '%' }"
            ></div>
          </div>
          <p class="text-xs text-gray-600 mt-1">
            Tiến độ: {{ examStore.answeredCount }}/{{ examStore.totalQuestions }} câu
          </p>
        </div>

        <!-- Center: Timer -->
        <div class="mx-8 text-center">
          <div
            :class="[
              'text-3xl font-bold font-mono',
              examStore.isTimeWarning ? 'text-red-600' : 'text-gray-900',
            ]"
          >
            {{ examStore.timeRemainingFormatted }}
          </div>
          <p :class="['text-xs', examStore.isTimeWarning ? 'text-red-600' : 'text-gray-600']">
            {{ examStore.isTimeWarning ? '⚠️ Còn ít thời gian!' : 'Thời gian còn lại' }}
          </p>
        </div>

        <!-- Right: Submit Button -->
        <button
          @click="handleSubmitExam"
          class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors shadow-lg"
        >
          Nộp bài →
        </button>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="flex flex-1 overflow-hidden gap-4 p-4">
      <!-- Left Column: Question Content -->
      <div
        class="flex-1 bg-white rounded-xl border border-gray-200 overflow-hidden flex flex-col shadow-sm"
      >
        <!-- Question Navigation -->
        <div
          class="bg-blue-50 border-b border-gray-200 px-6 py-4 flex items-center justify-between"
        >
          <div>
            <p class="text-sm text-gray-600">
              Câu {{ examStore.currentQuestionIndex + 1 }}/{{ examStore.totalQuestions }}
            </p>
            <p class="text-xs text-gray-500 mt-1">{{ getCurrentSectionName() }}</p>
          </div>
          <div class="flex gap-2">
            <button
              @click="examStore.goToPreviousQuestion"
              :disabled="examStore.currentQuestionIndex === 0"
              class="px-3 py-1 text-sm bg-gray-200 text-gray-700 rounded hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              ← Câu trước
            </button>
            <button
              @click="examStore.goToNextQuestion"
              :disabled="examStore.currentQuestionIndex === examStore.totalQuestions - 1"
              class="px-3 py-1 text-sm bg-gray-200 text-gray-700 rounded hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Câu sau →
            </button>
          </div>
        </div>

        <!-- Question Content -->
        <div class="flex-1 overflow-y-auto p-6">
          <div v-if="examStore.currentQuestion">
            <!-- Question Text -->
            <h2 class="text-lg font-bold text-gray-900 mb-4">
              Câu {{ examStore.currentQuestionIndex + 1 }}: {{ examStore.currentQuestion.questionText }}
            </h2>

            <!-- Media (image/audio) -->
            <div v-if="examStore.currentQuestion.mediaUrl" class="mb-6">
              <!-- Audio -->
              <div
                v-if="isAudioUrl(examStore.currentQuestion.mediaUrl)"
                class="p-4 bg-blue-50 rounded-lg"
              >
                <label class="text-sm font-semibold text-gray-900 mb-3 block">🔊 Phần nghe:</label>
                <audio :src="examStore.currentQuestion.mediaUrl" controls class="w-full"></audio>
              </div>
              <!-- Image -->
              <img
                v-else
                :src="examStore.currentQuestion.mediaUrl"
                :alt="`Question ${examStore.currentQuestion.questionNumber}`"
                class="w-full h-96 object-contain rounded-xl border border-gray-300 bg-gray-50"
              />
            </div>

            <!-- Options -->
            <div class="space-y-3">
              <div
                v-for="(option, index) in examStore.currentQuestion.options"
                :key="index"
                class="border-2 rounded-lg p-4 cursor-pointer transition-all"
                :class="[
                  isOptionSelected(option.id)
                    ? 'border-blue-600 bg-blue-50'
                    : 'border-gray-300 hover:border-blue-400',
                ]"
                @click="selectOption(option.id)"
              >
                <label class="flex items-center cursor-pointer">
                  <input
                    type="radio"
                    name="exam_question_options"
                    :checked="isOptionSelected(option.id)"
                    class="w-4 h-4 text-blue-600"
                    @change="selectOption(option.id)"
                  />
                  <span class="ml-3 text-gray-900">
                    <strong>{{ getOptionLabel(index) }}.</strong> {{ option.text }}
                  </span>
                </label>
              </div>
            </div>

            <!-- Mark for Review Checkbox -->
            <div class="mt-6 pt-6 border-t border-gray-200 flex items-center gap-3">
              <input
                type="checkbox"
                :checked="isMarkedForReview"
                @change="toggleMarkForReview"
                class="w-4 h-4 rounded text-yellow-500"
              />
              <label class="text-sm text-gray-700">Đánh dấu để xem lại</label>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column: Question Map Sidebar -->
      <div
        class="w-80 bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm flex flex-col"
      >
        <div class="bg-blue-600 text-white px-4 py-4">
          <h3 class="font-bold text-lg">Bản đồ câu hỏi</h3>
          <p class="text-blue-100 text-xs mt-1">
            Tổng: {{ examStore.totalQuestions }} | Làm được: {{ examStore.answeredCount }}
          </p>
        </div>

        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-5 gap-2">
            <button
              v-for="(question, index) in examStore.allQuestions"
              :key="question.id"
              @click="examStore.goToQuestion(index)"
              :class="[
                'w-full aspect-square rounded-lg font-semibold text-sm transition-all flex items-center justify-center',
                examStore.currentQuestionIndex === index
                  ? 'bg-blue-600 text-white scale-105 shadow-lg'
                  : getQuestionStatusClass(question.id),
              ]"
              :title="`Câu ${index + 1}`"
            >
              {{ index + 1 }}
            </button>
          </div>

          <!-- Legend -->
          <div class="mt-6 pt-4 border-t border-gray-200 space-y-2">
            <div class="flex items-center gap-2 text-xs">
              <div class="w-3 h-3 bg-blue-600 rounded"></div>
              <span class="text-gray-700">Câu hiện tại</span>
            </div>
            <div class="flex items-center gap-2 text-xs">
              <div class="w-3 h-3 bg-green-500 rounded"></div>
              <span class="text-gray-700">Đã làm</span>
            </div>
            <div class="flex items-center gap-2 text-xs">
              <div class="w-3 h-3 bg-yellow-500 rounded"></div>
              <span class="text-gray-700">Đánh dấu</span>
            </div>
            <div class="flex items-center gap-2 text-xs">
              <div class="w-3 h-3 bg-gray-300 rounded"></div>
              <span class="text-gray-700">Chưa làm</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Submit Confirmation Modal -->
    <Teleport to="body" v-if="showSubmitModal">
      <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-xl max-w-md w-full shadow-2xl">
          <div class="bg-blue-600 text-white px-6 py-6">
            <h2 class="text-2xl font-bold">Xác nhận nộp bài</h2>
          </div>
          <div class="px-6 py-6 space-y-4">
            <div class="bg-yellow-50 border border-yellow-300 rounded-lg p-4">
              <p class="text-yellow-900 text-sm font-semibold mb-2">⚠️ Lưu ý:</p>
              <ul class="space-y-1 text-yellow-900 text-xs">
                <li>
                  • Bạn đã làm được {{ examStore.answeredCount }}/{{ examStore.totalQuestions }} câu
                </li>
                <li>
                  • Bạn có {{ examStore.totalQuestions - examStore.answeredCount }} câu chưa làm
                </li>
                <li v-if="examStore.markedCount > 0">
                  • Bạn có {{ examStore.markedCount }} câu đánh dấu để xem lại
                </li>
                <li>• Sau khi nộp bài, bạn không thể chỉnh sửa câu trả lời</li>
              </ul>
            </div>
            <p class="text-gray-700 text-sm">Bạn có chắc chắn muốn nộp bài?</p>
          </div>
          <div class="px-6 py-4 bg-gray-50 flex gap-3 rounded-b-xl">
            <button
              @click="showSubmitModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
            >
              Tiếp tục làm
            </button>
            <button
              @click="submitExam"
              :disabled="isSubmitting"
              class="flex-1 px-4 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
              {{ isSubmitting ? 'Đang nộp...' : 'Nộp bài' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useExamStore } from '../../stores/exam'
import { useDialog } from '@/composables/useDialog'

const router = useRouter()
const route = useRoute()
const examStore = useExamStore()
const dialog = useDialog()

// State
const showSubmitModal = ref(false)
const timerInterval = ref<ReturnType<typeof setInterval> | null>(null)
const isSubmitting = ref(false)

// Computed
const isMarkedForReview = computed(() => {
  return examStore.markedForReview.has(examStore.currentQuestion?.id || '')
})

// Methods
const getCurrentSectionName = () => {
  if (!examStore.currentQuestion || !examStore.currentExam) return ''
  const section = examStore.currentExam.sections.find(
    (s) => s.id === examStore.currentQuestion?.sectionId,
  )
  return section?.sectionName || ''
}

const isAudioUrl = (url: string) => {
  if (!url) return false
  return /\.(mp3|wav|ogg|aac|m4a)$/i.test(url) || url.includes('/audio/')
}

const getOptionLabel = (index: number) => {
  return String.fromCharCode(65 + index) // A, B, C, D...
}

const isOptionSelected = (optionId: string) => {
  return examStore.userAnswers[examStore.currentQuestion?.id || ''] === optionId
}

const selectOption = (optionId: string) => {
  if (examStore.currentQuestion) {
    examStore.updateAnswer(examStore.currentQuestion.id, optionId)
  }
}

const toggleMarkForReview = () => {
  if (examStore.currentQuestion) {
    examStore.toggleMarkForReview(examStore.currentQuestion.id)
  }
}

const getQuestionStatusClass = (questionId: string) => {
  const status = examStore.questionStatus(questionId)
  if (status === 'answered') return 'bg-green-500 text-white'
  if (status === 'marked') return 'bg-yellow-500 text-white'
  return 'bg-gray-300 text-gray-700 hover:bg-gray-400'
}

const handleSubmitExam = () => {
  showSubmitModal.value = true
}

const submitExam = async () => {
  isSubmitting.value = true
  try {
    const result = await examStore.submitExamToBackend()
    showSubmitModal.value = false

    // Navigate to results page with backend data
    await router.push({
      name: 'exam-results',
      params: { id: route.params.id },
      query: {
        resultId: String(result.examResultId),
        score: String(result.totalScore),
        passed: result.isPassed != null ? String(result.isPassed) : undefined,
      },
    })
  } catch (error: any) {
    console.error('Failed to submit exam:', error)
    const msg = error?.response?.data?.message || (error instanceof Error ? error.message : 'Lỗi không xác định')
    await dialog.alert('Lỗi nộp bài', `Lỗi khi nộp bài: ${msg}`, 'error')
  } finally {
    isSubmitting.value = false
  }
}

const startTimer = () => {
  timerInterval.value = setInterval(() => {
    examStore.decrementTimer()

    // Auto-submit when time is up
    if (examStore.timeRemaining === 0) {
      submitExam()
    }
  }, 1000)
}

const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (!examStore.isExamSubmitted) {
    event.preventDefault()
    event.returnValue = ''
    return ''
  }
}

// Lifecycle
onMounted(async () => {
  try {
    const examId = route.params.id as string
    if (!examStore.currentExam) {
      await examStore.initializeExam(examId)
    }

    if (!examStore.isExamStarted) {
      examStore.startExam()
    }

    startTimer()
    window.addEventListener('beforeunload', handleBeforeUnload)
  } catch (error: any) {
    console.error('Failed to initialize exam taking:', error)
    const msg = error?.response?.data?.message || (error instanceof Error ? error.message : 'Lỗi không xác định')
    await dialog.alert('Lỗi tải đề', `Không thể tải đề thi: ${msg}`, 'error')
    await router.push({ name: 'exam-list' })
  }
})

onBeforeUnmount(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
  }
  window.removeEventListener('beforeunload', handleBeforeUnload)
})
</script>
