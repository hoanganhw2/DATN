<template>
  <div class="lesson-quiz w-full flex-1 flex flex-col p-4 md:p-8">
    <!-- State Loading -->
    <div v-if="isLoading" class="flex flex-col items-center justify-center py-20 text-gray-500">
      <div
        class="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mb-4"
      ></div>
      <p>Đang tải bài tập...</p>
    </div>

    <!-- State No Data -->
    <div
      v-else-if="!exercise || !questions.length"
      class="flex flex-col items-center justify-center py-20 text-gray-500"
    >
      <div class="text-6xl mb-4">📝</div>
      <h3 class="text-xl font-bold text-gray-800">Chưa có dữ liệu bài tập</h3>
      <p class="mt-2 text-center text-sm">Giáo viên chưa cấu hình câu hỏi cho bài học này.</p>
    </div>

    <!-- State Result Viewer -->
    <div
      v-else-if="submitResult"
      class="result-viewer animate-fade-in bg-white shadow-sm border border-gray-100 p-8 rounded-3xl w-full max-w-4xl mx-auto"
    >
      <div class="text-center mb-8">
        <div
          class="inline-flex items-center justify-center w-20 h-20 rounded-full mb-4 shadow-lg"
          :class="passPercentage >= 50 ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'"
        >
          <svg
            v-if="passPercentage >= 50"
            class="w-10 h-10"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
            ></path>
          </svg>
          <svg v-else class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            ></path>
          </svg>
        </div>
        <h2 class="text-3xl font-bold text-gray-800 mb-2">Kết quả bài thi</h2>
        <p class="text-lg text-gray-600 font-semibold">
          Bạn đã trả lời đúng
          <span class="text-blue-600"
            >{{ submitResult.correctCount }} / {{ submitResult.totalQuestions }}</span
          >
          câu hỏi
        </p>
        <p class="text-md text-gray-500 mt-1">
          Tổng điểm: <span class="font-bold text-gray-800">{{ submitResult.totalScore }}</span> /
          {{ exercise.totalPoints }}
        </p>
      </div>

      <div class="space-y-6">
        <div
          v-for="(res, idx) in submitResult.results"
          :key="res.questionId"
          class="p-5 rounded-2xl border"
          :class="res.isCorrect ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'"
        >
          <div class="flex items-start gap-3">
            <span
              class="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm"
              :class="res.isCorrect ? 'bg-green-200 text-green-700' : 'bg-red-200 text-red-700'"
              >{{ idx + 1 }}</span
            >
            <div class="flex-1">
              <p class="font-semibold text-gray-800 mb-3 whitespace-pre-wrap">
                {{ res.questionText }}
              </p>

              <div class="space-y-2 mb-3">
                <div
                  class="text-sm p-3 rounded-lg border border-dashed"
                  :class="
                    res.isCorrect
                      ? 'bg-green-100 border-green-300 text-green-800'
                      : 'bg-red-100 border-red-300 text-red-800'
                  "
                >
                  <span class="font-bold mr-1">Bạn chọn:</span>
                  <span>{{ formatAnswerDisplay(res.questionId, res.submittedAnswer) }}</span>
                </div>

                <div
                  v-if="!res.isCorrect"
                  class="text-sm p-3 rounded-lg bg-blue-50 border border-blue-200 border-dashed text-blue-800"
                >
                  <span class="font-bold mr-1">Đáp án đúng:</span>
                  <span>{{
                    formatAnswerDisplay(res.questionId, res.correctAnswer) || 'Không xác định'
                  }}</span>
                </div>
              </div>

              <div
                v-if="res.explanation"
                class="text-sm text-gray-600 bg-white p-4 rounded-xl border border-gray-100 mt-3 shadow-sm"
              >
                <span class="font-bold text-blue-600 mb-1 block">💡 Giải thích:</span>
                <span class="italic whitespace-pre-wrap">{{ res.explanation }}</span>
              </div>
            </div>

            <div
              class="flex-shrink-0 font-bold ml-4"
              :class="res.isCorrect ? 'text-green-600' : 'text-red-500'"
            >
              {{ res.isCorrect ? '+' + res.score : '0' }} điểm
            </div>
          </div>
        </div>
      </div>

      <div class="mt-8 flex justify-center gap-4">
        <button
          @click="resetQuiz"
          class="px-6 py-3 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-colors shadow-sm"
        >
          Làm lại
        </button>
        <button
          v-if="!isCompletedMarked"
          @click="emitComplete"
          class="px-6 py-3 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 transition-colors shadow-md shadow-blue-200 flex items-center gap-2"
        >
          <span>Tiếp tục học</span>
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 5l7 7-7 7"
            ></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- State Quiz Taker (Pagination) -->
    <div v-else class="quiz-taker flex-1 flex flex-col w-full max-w-7xl mx-auto">
      <div class="flex flex-col lg:flex-row gap-8 flex-1">
        <!-- Left: Question Area -->
        <div class="flex-1 flex flex-col h-full">
          <div class="quiz-header pb-4 mb-6">
            <h2 class="text-2xl font-bold text-gray-800">{{ exercise.title }}</h2>
            <p v-if="exercise.description" class="text-gray-500 mt-2">{{ exercise.description }}</p>
          </div>

          <div
            v-if="currentQuestion"
            class="question-card bg-white rounded-3xl p-6 md:p-8 border border-gray-100 shadow-sm flex-1 flex flex-col transition-all"
          >
            <div class="flex items-start gap-4 mb-8">
              <div
                class="w-12 h-12 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold text-xl flex-shrink-0 shadow-sm border border-blue-200 text-shadow"
              >
                {{ currentQuestionIndex + 1 }}
              </div>
              <div class="flex-1 mt-1">
                <h4 class="text-xl font-semibold text-gray-800 leading-relaxed whitespace-pre-wrap">
                  {{ currentQuestion.questionText }}
                </h4>
              </div>
              <div
                class="text-sm font-bold text-gray-500 bg-gray-200 px-3 py-1 rounded-full shadow-sm"
              >
                {{ currentQuestion.points }}đ
              </div>
            </div>

            <!-- Answer Selection -->
            <div class="answers-container ml-0 md:ml-16 flex-1">
              <!-- Single Choice -->
              <div v-if="currentQuestion.questionType === 'SINGLE_CHOICE'" class="space-y-4">
                <label
                  v-for="(opt, oIdx) in currentQuestion.options"
                  :key="oIdx"
                  class="flex items-center p-4 border rounded-xl cursor-pointer transition-all"
                  :class="
                    userAnswers[currentQuestion.id!] === oIdx.toString()
                      ? 'border-blue-500 bg-blue-50/50 shadow-md ring-1 ring-blue-500'
                      : 'border-gray-200 bg-white hover:border-gray-300 hover:shadow-sm'
                  "
                >
                  <input
                    type="radio"
                    name="single_choice_radio_group"
                    :value="oIdx.toString()"
                    v-model="userAnswers[currentQuestion.id!]"
                    class="w-5 h-5 text-blue-600 focus:ring-blue-500 cursor-pointer"
                  />
                  <span class="ml-4 text-gray-700 font-medium text-lg">{{ opt }}</span>
                </label>
              </div>

              <!-- Multiple Choice -->
              <div v-else-if="currentQuestion.questionType === 'MULTIPLE_CHOICE'" class="space-y-4">
                <label
                  v-for="(opt, oIdx) in currentQuestion.options"
                  :key="oIdx"
                  class="flex items-center p-4 border rounded-xl cursor-pointer transition-all"
                  :class="
                    isMultipleChoiceSelected(currentQuestion.id!, oIdx.toString())
                      ? 'border-blue-500 bg-blue-50/50 shadow-md ring-1 ring-blue-500'
                      : 'border-gray-200 bg-white hover:border-gray-300 hover:shadow-sm'
                  "
                  @click="toggleMultipleChoice(currentQuestion.id!, oIdx.toString())"
                >
                  <input
                    type="checkbox"
                    :value="oIdx.toString()"
                    @change="toggleMultipleChoice(currentQuestion.id!, oIdx.toString())"
                    :checked="isMultipleChoiceSelected(currentQuestion.id!, oIdx.toString())"
                    @click.stop
                    class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500 cursor-pointer"
                  />
                  <span class="ml-4 text-gray-700 font-medium text-lg">{{ opt }}</span>
                </label>
              </div>

              <!-- Text Input -->
              <div v-else-if="currentQuestion.questionType === 'TEXT_INPUT'">
                <p class="text-sm font-semibold text-gray-500 mb-2">
                  Nhập chính xác đáp án (không phân biệt viết hoa):
                </p>
                <input
                  type="text"
                  v-model="userAnswers[currentQuestion.id!]"
                  placeholder="Câu trả lời của bạn..."
                  class="w-full bg-white border border-gray-300 rounded-xl px-4 py-4 text-lg text-gray-900 focus:ring-4 focus:ring-blue-500/20 focus:border-blue-500 shadow-sm transition-all outline-none"
                />
              </div>

              <!-- Essay -->
              <div v-else-if="currentQuestion.questionType === 'ESSAY'">
                <textarea
                  v-model="userAnswers[currentQuestion.id!]"
                  rows="6"
                  placeholder="Nhập câu trả lời tự luận của bạn..."
                  class="w-full bg-white border border-gray-300 rounded-xl px-4 py-4 text-lg text-gray-900 focus:ring-4 focus:ring-blue-500/20 focus:border-blue-500 shadow-sm transition-all resize-y outline-none"
                ></textarea>
              </div>
            </div>

            <!-- Question Navigation Bottom -->
            <div class="mt-auto pt-6 border-t border-gray-100 flex justify-between items-center">
              <button
                @click="prevQuestion"
                :disabled="currentQuestionIndex === 0"
                class="flex items-center justify-center gap-2 px-6 py-2.5 bg-white border border-gray-300 rounded-xl font-semibold text-gray-700 transition hover:bg-gray-50 hover:shadow disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 19l-7-7 7-7"
                  ></path>
                </svg>
                Câu trước
              </button>

              <button
                @click="nextQuestion"
                :disabled="currentQuestionIndex === questions.length - 1"
                class="flex items-center justify-center gap-2 px-6 py-2.5 bg-blue-50 text-blue-700 border border-blue-100 rounded-xl font-semibold transition hover:bg-blue-100 hover:shadow disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Câu tiếp theo
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5l7 7-7 7"
                  ></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Right: Navigator & Submit -->
        <div class="w-full lg:w-80 flex-shrink-0 h-full">
          <div
            class="bg-white rounded-3xl p-6 border border-gray-100 sticky top-6 shadow-sm flex flex-col"
            style="max-height: calc(100vh - 150px)"
          >
            <div
              v-if="exercise.timeLimit"
              class="mb-8 text-center bg-white py-4 rounded-2xl shadow-sm border border-gray-100"
            >
              <div class="text-gray-500 text-sm mb-1 font-bold tracking-wide uppercase">
                Thời gian còn lại
              </div>
              <div
                class="text-4xl font-extrabold font-mono text-rose-600 tracking-tight drop-shadow-sm"
              >
                {{ formattedTime }}
              </div>
            </div>

            <div
              class="text-gray-700 font-bold mb-4 flex justify-between items-end border-b border-gray-200 pb-2"
            >
              <span class="text-lg">Bảng câu hỏi</span>
              <span class="text-sm bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full"
                >{{ answeredCount }} / {{ questions.length }}</span
              >
            </div>

            <div class="grid grid-cols-5 gap-2.5 mb-8 overflow-y-auto max-h-[300px] p-1">
              <button
                v-for="(q, index) in questions"
                :key="q.id"
                @click="currentQuestionIndex = index"
                class="w-12 h-12 rounded-xl flex items-center justify-center font-bold text-sm transition-all transform hover:-translate-y-0.5 active:translate-y-0"
                :class="[
                  currentQuestionIndex === index ? 'ring-4 ring-blue-300 z-10' : '',
                  isQuestionAnswered(q.id)
                    ? 'bg-blue-600 text-white shadow-md'
                    : 'bg-white border border-gray-300 text-gray-600 hover:bg-gray-100 shadow-sm',
                ]"
                :title="isQuestionAnswered(q.id) ? 'Đã làm' : 'Chưa làm'"
              >
                {{ index + 1 }}
              </button>
            </div>

            <button
              @click="submitQuiz"
              :disabled="isSubmitting"
              class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-bold rounded-2xl hover:from-blue-700 hover:to-indigo-700 disabled:opacity-50 transition-all shadow-lg shadow-blue-200 flex justify-center items-center gap-2 transform hover:-translate-y-1"
            >
              <svg v-if="isSubmitting" class="w-6 h-6 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                ></path>
              </svg>
              <span class="text-lg tracking-wide uppercase">Nộp bài</span>
              <svg
                v-if="!isSubmitting"
                class="w-5 h-5 ml-1"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                ></path>
              </svg>
            </button>
            <p class="text-xs text-center text-gray-400 mt-4">
              Vui lòng kiểm tra kỹ số lượng câu hỏi trước khi nộp.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import { useToastStore } from '@/stores/toast'
import {
  getExercisesByLesson,
  getExerciseDetail,
  submitExercise,
  type ExerciseDetailResponse,
  type ExerciseSubmitResponse,
} from '@/api/exercises'

const props = defineProps<{
  lessonId: number
}>()

const emit = defineEmits<{
  (e: 'completed'): void
}>()

const toastStore = useToastStore()

// State
const isLoading = ref(true)
const isSubmitting = ref(false)
const exercise = ref<ExerciseDetailResponse | null>(null)
const questions = computed(() => {
  if (!exercise.value?.questions) return []
  return [...exercise.value.questions].sort((a, b) => a.orderIndex - b.orderIndex)
})
const userAnswers = ref<Record<number, string>>({})
const multipleChoiceAnswers = ref<Record<number, string[]>>({})
const submitResult = ref<ExerciseSubmitResponse | null>(null)
const isCompletedMarked = ref(false)

// Pagination State
const currentQuestionIndex = ref(0)
const currentQuestion = computed(() => {
  if (!questions.value || questions.value.length === 0) return null
  return questions.value[currentQuestionIndex.value]
})

const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) currentQuestionIndex.value--
}
const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) currentQuestionIndex.value++
}

const isQuestionAnswered = (qId?: number) => {
  if (!qId) return false
  const q = questions.value.find((x) => x.id === qId)
  if (!q) return false

  if (q.questionType === 'MULTIPLE_CHOICE') {
    return multipleChoiceAnswers.value[qId] && multipleChoiceAnswers.value[qId].length > 0
  }

  const ans = userAnswers.value[qId]
  return !!(ans && ans.trim() !== '' && ans !== '[]')
}

const answeredCount = computed(() => {
  return questions.value.filter((q) => isQuestionAnswered(q.id)).length
})

// Timer
const timeLeft = ref(0)
const timerInterval = ref<any>(null)

const formattedTime = computed(() => {
  if (timeLeft.value <= 0) return '00:00'
  const m = Math.floor(timeLeft.value / 60)
  const s = timeLeft.value % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
})

const startTimer = () => {
  if (!exercise.value?.timeLimit) return
  timeLeft.value = exercise.value.timeLimit * 60 // Convert to seconds

  if (timerInterval.value) clearInterval(timerInterval.value)

  timerInterval.value = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      clearInterval(timerInterval.value)
      toastStore.warning('Hết thời gian làm bài! Hệ thống tự động nộp bài.')
      submitQuiz()
    }
  }, 1000)
}

const passPercentage = computed(() => {
  if (!submitResult.value || submitResult.value.totalQuestions === 0) return 0
  return (submitResult.value.correctCount / submitResult.value.totalQuestions) * 100
})

// Initialize
const loadExercise = async () => {
  isLoading.value = true
  submitResult.value = null
  userAnswers.value = {}
  multipleChoiceAnswers.value = {}
  currentQuestionIndex.value = 0
  if (timerInterval.value) clearInterval(timerInterval.value)

  try {
    const list = await getExercisesByLesson(props.lessonId)
    if (list && list.length > 0) {
      // Pick first exercise for lesson
      const detail = await getExerciseDetail(list[0].id)
      exercise.value = detail

      // Init answers
      detail.questions.forEach((q) => {
        if (q.questionType === 'MULTIPLE_CHOICE') {
          multipleChoiceAnswers.value[q.id] = []
        } else {
          userAnswers.value[q.id] = ''
        }
      })

      if (detail.timeLimit) {
        startTimer()
      }
    } else {
      exercise.value = null
    }
  } catch (err) {
    console.error(err)
    toastStore.error('Không thể tải bài tập.')
  } finally {
    isLoading.value = false
  }
}

// Watch lessonId change
watch(
  () => props.lessonId,
  () => {
    loadExercise()
  },
  { immediate: true },
)

onUnmounted(() => {
  if (timerInterval.value) clearInterval(timerInterval.value)
})

// Multiple Choice Handlers
const toggleMultipleChoice = (qId: number, optionIdx: string) => {
  if (!multipleChoiceAnswers.value[qId]) {
    multipleChoiceAnswers.value[qId] = []
  }
  const arr = multipleChoiceAnswers.value[qId]
  const idx = arr.indexOf(optionIdx)
  if (idx === -1) {
    arr.push(optionIdx)
  } else {
    arr.splice(idx, 1)
  }
  userAnswers.value[qId] = JSON.stringify(arr)
}

const isMultipleChoiceSelected = (qId: number, optionIdx: string) => {
  if (!multipleChoiceAnswers.value[qId]) return false
  return multipleChoiceAnswers.value[qId].includes(optionIdx)
}

// Submit Handlers
const submitQuiz = async () => {
  if (!exercise.value) return

  if (timerInterval.value) clearInterval(timerInterval.value)
  isSubmitting.value = true

  try {
    const payloadAnswers = questions.value.map((q) => {
      let ansText = ''
      if (q.questionType === 'MULTIPLE_CHOICE') {
        ansText = userAnswers.value[q.id] || '[]'
      } else {
        ansText = userAnswers.value[q.id] || ''
      }
      return {
        questionId: q.id,
        answerText: ansText,
      }
    })

    const res = await submitExercise(exercise.value.id, { answers: payloadAnswers })
    submitResult.value = res
    toastStore.success('Đã nộp bài thành công!')

    // Auto emit complete if they pass or just completed it
    if (!isCompletedMarked.value) {
      setTimeout(() => {
        emitComplete()
      }, 1000)
    }
  } catch (err) {
    console.error(err)
    toastStore.error('Lỗi nộp bài thi.')
  } finally {
    isSubmitting.value = false
  }
}

const resetQuiz = () => {
  submitResult.value = null
  currentQuestionIndex.value = 0
  // Re-init answers
  questions.value.forEach((q) => {
    if (q.questionType === 'MULTIPLE_CHOICE') {
      multipleChoiceAnswers.value[q.id] = []
      userAnswers.value[q.id] = '[]'
    } else {
      userAnswers.value[q.id] = ''
    }
  })
  if (exercise.value?.timeLimit) {
    startTimer()
  }
}

const emitComplete = () => {
  isCompletedMarked.value = true
  emit('completed')
}

// Format Answer Display (convert indices to real text)
const formatAnswerDisplay = (qId: number, ansValueStr: string) => {
  const q = questions.value.find((x) => x.id === qId)
  if (!q) return ansValueStr

  if (!ansValueStr) return 'Chưa chọn'

  if (q.questionType === 'SINGLE_CHOICE') {
    const idx = parseInt(ansValueStr, 10)
    if (!isNaN(idx) && q.options && q.options[idx]) {
      return String.fromCharCode(65 + idx) + '. ' + q.options[idx]
    }
  } else if (q.questionType === 'MULTIPLE_CHOICE') {
    try {
      const arr = JSON.parse(ansValueStr)
      if (Array.isArray(arr) && q.options) {
        const texts = arr.map((idxStr: string) => {
          const idx = parseInt(idxStr, 10)
          return !isNaN(idx) && q.options![idx]
            ? String.fromCharCode(65 + idx) + '. ' + q.options![idx]
            : idxStr
        })
        return texts.join('; ')
      }
    } catch {}
  }

  return ansValueStr
}
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Custom shadow utilities for a more premium look */
.text-shadow {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
</style>
