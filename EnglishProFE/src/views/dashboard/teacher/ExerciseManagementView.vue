<template>
  <DashboardLayout>
    <div class="max-w-6xl mx-auto space-y-6">
      <!-- Breadcrumb & Header -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2 text-sm text-gray-500">
          <router-link :to="`/dashboard/teacher/courses/${courseId}/curriculum`" class="hover:text-blue-600 transition">
            ← Quay lại giáo trình
          </router-link>
          <span>/</span>
          <span class="text-gray-900 font-semibold truncate max-w-[300px]">
            Quản lý Ngân hàng câu hỏi
          </span>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        <!-- Sidebar - Lessons List -->
        <div class="lg:col-span-4 bg-white rounded-2xl border border-gray-100 shadow-sm p-4 sticky top-6">
          <h3 class="text-lg font-bold text-gray-900 mb-4 px-2">Bài học có bài tập</h3>

          <div v-if="isLoading" class="flex flex-col items-center justify-center py-10">
             <div class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
          </div>

          <div v-else class="space-y-2 max-h-[calc(100vh-200px)] overflow-y-auto custom-scrollbar">
            <div
              v-for="lesson in quizLessons"
              :key="lesson.id"
              @click="selectLesson(lesson)"
              :class="[
                'p-3 rounded-xl cursor-pointer transition border',
                selectedLesson?.id === lesson.id
                  ? 'bg-blue-50 border-blue-200 shadow-sm'
                  : 'bg-white border-transparent hover:bg-gray-50',
              ]"
            >
              <div class="flex items-center gap-3">
                <div :class="[
                  'w-8 h-8 rounded-full flex items-center justify-center shrink-0',
                  selectedLesson?.id === lesson.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-400'
                ]">
                  <HelpCircleIcon class="w-4 h-4" />
                </div>
                <div>
                  <p class="font-semibold text-gray-800 text-sm line-clamp-2">{{ lesson.title }}</p>
                  <p class="text-xs text-gray-500 mt-0.5" v-if="selectedLesson?.id === lesson.id && currentExercise">
                    {{ questions.length }} câu hỏi
                  </p>
                </div>
              </div>
            </div>

            <div v-if="quizLessons.length === 0" class="text-center py-6">
              <p class="text-sm text-gray-500">Giáo trình chưa có bài học dạng "Bài tập / Đề thi".</p>
            </div>
          </div>
        </div>

        <!-- Main Workspace - Exercise Questions Editor -->
        <div class="lg:col-span-8">
          <div v-if="selectedLesson" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden min-h-[500px]">
            <!-- Header -->
            <div class="border-b border-gray-100 px-6 py-5 flex items-center justify-between bg-white">
              <div>
                <h2 class="text-xl font-bold text-gray-900 pr-4">
                   {{ selectedLesson.title }}
                </h2>
                <p class="text-sm text-gray-500 mt-1">Cấu hình câu hỏi cho học phần này</p>
              </div>
              <Button variant="primary" size="sm" @click="openAddQuestionModal" class="shadow-sm">
                + Thêm câu hỏi
              </Button>
            </div>

            <!-- Loading Questions -->
            <div v-if="isLoadingQuestions" class="flex flex-col items-center justify-center py-20">
               <div class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
               <p class="text-sm text-gray-500 mt-3">Đang tải câu hỏi...</p>
            </div>

            <!-- Questions List -->
            <div v-else class="p-6 space-y-4">
              <div
                v-for="(question, idx) in questions"
                :key="idx"
                class="bg-gray-50 border border-gray-200 rounded-xl p-5 hover:border-gray-300 transition-colors"
              >
                <div class="flex justify-between items-start mb-4">
                  <div class="flex items-center gap-2">
                    <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-blue-100 text-blue-700 font-bold text-xs">
                      {{ idx + 1 }}
                    </span>
                    <span class="text-xs font-semibold px-2 py-1 bg-white border border-gray-200 rounded-md text-gray-600">
                      {{ question.questionType === 'MULTIPLE_CHOICE' ? 'Nhiều lựa chọn' : (question.questionType === 'SINGLE_CHOICE' ? 'Một lựa chọn' : 'Văn bản') }}
                    </span>
                  </div>
                  <div class="flex gap-1">
                    <button
                      @click="editQuestion(question, idx)"
                      class="p-1.5 text-blue-500 hover:bg-blue-100 rounded-lg transition"
                    >
                      <EditIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click="confirmDeleteQuestion(idx)"
                      class="p-1.5 text-red-500 hover:bg-red-100 rounded-lg transition"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </div>
                </div>

                <p class="text-gray-800 font-medium mb-4 whitespace-pre-wrap">{{ question.questionText }}</p>

                <!-- Options Display -->
                <div v-if="['MULTIPLE_CHOICE', 'SINGLE_CHOICE'].includes(question.questionType)" class="space-y-2">
                  <div
                    v-for="(option, oIdx) in question.options"
                    :key="oIdx"
                    :class="[
                      'px-4 py-2.5 rounded-lg border text-sm',
                      question.correctAnswer?.includes(oIdx.toString()) || question.correctAnswer === option
                        ? 'bg-green-50 border-green-200 text-green-800 font-medium'
                        : 'bg-white border-gray-200 text-gray-700',
                    ]"
                  >
                    <span class="font-medium mr-2">{{ String.fromCharCode(65 + oIdx) }}.</span> {{ option }}
                  </div>
                </div>

                <!-- Text Display -->
                <div v-else class="bg-white border border-gray-200 rounded-lg px-4 py-2.5 text-sm">
                  Đáp án chuẩn: <span class="font-semibold text-green-700">{{ question.correctAnswer }}</span>
                </div>

                <div class="mt-4 flex items-center justify-between border-t border-gray-100 pt-3">
                   <p class="text-xs text-gray-500">Điểm số: <span class="font-semibold text-gray-700">{{ question.points }}</span></p>
                </div>
              </div>

              <!-- Empty State -->
              <div v-if="questions.length === 0" class="text-center py-12">
                <div class="w-16 h-16 bg-blue-50 text-blue-300 rounded-full flex items-center justify-center mx-auto mb-4">
                  <DatabaseIcon class="w-8 h-8" />
                </div>
                <h3 class="text-lg font-bold text-gray-900">Ngân hàng câu hỏi trống</h3>
                <p class="text-gray-500 mt-2">Bài học này chưa có câu hỏi nào. Nhấn "Thêm câu hỏi" để bắt đầu.</p>
              </div>
            </div>
          </div>

          <!-- Empty Workspace State -->
          <div v-else class="h-full min-h-[500px] flex flex-col items-center justify-center bg-gray-50/50 border border-gray-200 border-dashed rounded-2xl text-center p-12">
            <div class="w-20 h-20 bg-white rounded-full shadow-sm flex items-center justify-center mb-6">
              <CheckSquareIcon class="w-10 h-10 text-blue-200" />
            </div>
            <h3 class="mt-2 text-xl font-bold text-gray-900">Kho lưu trữ câu hỏi</h3>
            <p class="mt-2 text-gray-500 max-w-sm">Chọn một học phần loại Đề thi từ danh sách bên trái để tải ngân hàng câu hỏi.</p>
          </div>
        </div>
      </div>

      <!-- Add/Edit Question Modal -->
      <Modal v-model="showQuestionModal" :title="editingIndex >= 0 ? 'Sửa câu hỏi' : 'Thêm câu hỏi mới'" @confirm="submitQuestion">
        <div class="p-2 space-y-5">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Loại câu hỏi <span class="text-red-500">*</span></label>
            <select
              v-model="questionForm.questionType"
              class="w-full bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-gray-900 focus:ring-blue-500"
            >
              <option value="SINGLE_CHOICE">Trắc nghiệm một lựa chọn</option>
              <option value="MULTIPLE_CHOICE">Trắc nghiệm nhiều lựa chọn</option>
              <option value="TEXT_INPUT">Điền từ tự do</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Nội dung câu hỏi <span class="text-red-500">*</span></label>
            <textarea
              v-model="questionForm.questionText"
              rows="3"
              placeholder="VD: What relies on a system of lines and nodes?"
              class="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-gray-900 focus:ring-blue-500 focus:border-blue-500 resize-y"
            ></textarea>
          </div>

          <!-- Multiple / Single Choice Options -->
          <div v-if="['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(questionForm.questionType)">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Các đáp án lựa chọn</label>
            <div class="space-y-3">
              <div v-for="(option, idx) in questionForm.options" :key="idx" class="flex gap-3 items-center">
                <span class="font-bold text-gray-400 w-5">{{ String.fromCharCode(65 + idx) }}</span>
                <input
                  v-model="questionForm.options![idx]"
                  type="text"
                  placeholder="Nhập phương án..."
                  class="flex-1 bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900"
                />
                <label v-if="questionForm.questionType === 'SINGLE_CHOICE'" class="flex items-center cursor-pointer">
                  <input
                    type="radio"
                    name="correct_answer"
                    :value="idx.toString()"
                    v-model="questionForm.correctAnswer"
                    class="w-4 h-4 text-blue-600"
                  />
                  <span class="ml-2 text-sm text-gray-600">Đúng</span>
                </label>
                <label v-else class="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    :value="idx.toString()"
                    v-model="multipleCorrectAnswers"
                    class="w-4 h-4 text-blue-600 rounded"
                  />
                  <span class="ml-2 text-sm text-gray-600">Đúng</span>
                </label>
                <button @click="removeOption(idx)" class="text-gray-400 hover:text-red-500 p-1">
                  <XIcon class="w-4 h-4" />
                </button>
              </div>
            </div>
            <button @click="addOption" class="mt-3 text-sm text-blue-600 font-medium hover:text-blue-700 flex items-center gap-1">
              <PlusIcon class="w-4 h-4" /> Thêm phương án
            </button>
          </div>

          <!-- Text Input -->
          <div v-else>
            <FormInput
              v-model="questionForm.correctAnswer"
              label="Đáp án đúng *"
              placeholder="Nhập 1 đáp án chuẩn..."
              required
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
             <FormInput
               v-model.number="questionForm.points"
               label="Điểm *"
               type="number"
               min="1"
               required
             />
          </div>
        </div>
      </Modal>

    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'

// Components
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import FormInput from '@/components/dashboard/FormInput.vue'
import Button from '@/components/dashboard/Button.vue'
import Modal from '@/components/dashboard/Modal.vue'

// Icons
import {
  HelpCircle as HelpCircleIcon,
  Edit as EditIcon,
  Trash2 as TrashIcon,
  Plus as PlusIcon,
  CheckSquare as CheckSquareIcon,
  Database as DatabaseIcon,
  X as XIcon
} from 'lucide-vue-next'

// API
import { getCourseDetail, type Lesson } from '@/api/courses'
import { 
  getExercisesByLesson, 
  getTeacherExerciseDetail, 
  createExercise, 
  updateExercise,
  type CreateExercisePayload,
  type CreateExerciseQuestionPayload
} from '@/api/exercises'

const route = useRoute()
const router = useRouter()
const toastStore = useToastStore()
const courseId = Number(route.params.id)

// State
const isLoading = ref(true)
const isLoadingQuestions = ref(false)
const quizLessons = ref<Lesson[]>([])
const selectedLesson = ref<Lesson | null>(null)

// Exercise / Questions State
const currentExercise = ref<any>(null)
const questions = ref<CreateExerciseQuestionPayload[]>([])

// Modal State
const showQuestionModal = ref(false)
const editingIndex = ref<number>(-1)
const multipleCorrectAnswers = ref<string[]>([])

const questionForm = ref<CreateExerciseQuestionPayload>({
  questionText: '',
  questionType: 'SINGLE_CHOICE',
  options: ['', '', '', ''],
  correctAnswer: '0',
  points: 10
})

onMounted(async () => {
  await loadCourseData()
  // Check if lessonId is in query
  const queryLessonId = route.query.lessonId
  if (queryLessonId) {
    const lesson = quizLessons.value.find(l => l.id === Number(queryLessonId))
    if (lesson) {
      selectLesson(lesson)
    }
  }
})

const loadCourseData = async () => {
  isLoading.value = true
  try {
    const detail = await getCourseDetail(courseId.toString())
    // Flatten lessons that are QUIZ
    const lessons: Lesson[] = []
    detail.chapters?.forEach(chap => {
      chap.lessons?.forEach(l => {
        if (l.contentType === 'QUIZ') {
          lessons.push(l)
        }
      })
    })
    quizLessons.value = lessons
  } catch (err) {
    toastStore.error('Không thể tải giáo trình')
  } finally {
    isLoading.value = false
  }
}

const selectLesson = async (lesson: Lesson) => {
  selectedLesson.value = lesson
  router.replace({ query: { lessonId: lesson.id } })
  await loadQuestions(lesson.id)
}

const loadQuestions = async (lessonId: number) => {
  isLoadingQuestions.value = true
  questions.value = []
  currentExercise.value = null
  try {
    const summaries = await getExercisesByLesson(lessonId)
    if (summaries && summaries.length > 0) {
      currentExercise.value = summaries[0]
      const detail = await getTeacherExerciseDetail(currentExercise.value.id)
      if (detail && detail.questions) {
        questions.value = detail.questions.map(q => ({
          questionText: q.questionText,
          questionType: q.questionType as any,
          options: q.options || [],
          correctAnswer: q.correctAnswer || '',
          points: q.points,
          orderIndex: q.orderIndex
        }))
      }
    }
  } catch (err) {
    console.error('No exercise found or error', err)
  } finally {
    isLoadingQuestions.value = false
  }
}

const openAddQuestionModal = () => {
  editingIndex.value = -1
  questionForm.value = {
    questionText: '',
    questionType: 'SINGLE_CHOICE',
    options: ['', '', '', ''],
    correctAnswer: '0',
    points: 10
  }
  multipleCorrectAnswers.value = []
  showQuestionModal.value = true
}

const editQuestion = (q: CreateExerciseQuestionPayload, idx: number) => {
  editingIndex.value = idx
  questionForm.value = {
    questionText: q.questionText,
    questionType: q.questionType,
    options: [...(q.options || [])],
    correctAnswer: q.correctAnswer,
    points: q.points
  }
  if (q.questionType === 'MULTIPLE_CHOICE') {
    try {
      multipleCorrectAnswers.value = JSON.parse(q.correctAnswer || '[]')
    } catch {
      multipleCorrectAnswers.value = q.correctAnswer ? q.correctAnswer.split(',') : []
    }
  } else {
    multipleCorrectAnswers.value = []
  }
  showQuestionModal.value = true
}

const addOption = () => {
  questionForm.value.options?.push('')
}

const removeOption = (idx: number) => {
  questionForm.value.options?.splice(idx, 1)
}

const submitQuestion = async () => {
  // Translate form data
  const form = { ...questionForm.value }

  if (!form.questionText.trim()) {
    toastStore.error('Nội dung câu hỏi không được trống')
    return
  }

  if (form.questionType === 'MULTIPLE_CHOICE') {
    form.correctAnswer = JSON.stringify(multipleCorrectAnswers.value)
    if (multipleCorrectAnswers.value.length === 0) {
      toastStore.error('Phải chọn ít nhất 1 đáp án đúng')
      return
    }
  } else if (form.questionType === 'SINGLE_CHOICE') {
    if (form.correctAnswer === '') {
      toastStore.error('Vui lòng chọn 1 đáp án đúng')
      return
    }
  }

  const updatedQuestions = [...questions.value]
  if (editingIndex.value >= 0) {
    updatedQuestions[editingIndex.value] = form
  } else {
    updatedQuestions.push({ ...form, orderIndex: updatedQuestions.length + 1 })
  }

  await saveExerciseRemotely(updatedQuestions)
}

const confirmDeleteQuestion = async (idx: number) => {
  if (confirm('Bạn có chắc muốn xóa câu hỏi này không?')) {
    const updatedQuestions = [...questions.value]
    updatedQuestions.splice(idx, 1)
    await saveExerciseRemotely(updatedQuestions)
  }
}

const saveExerciseRemotely = async (updatedQuestions: CreateExerciseQuestionPayload[]) => {
  if (!selectedLesson.value) return

  // Need at least 1 question for validation usually
  if (updatedQuestions.length === 0 && currentExercise.value) {
     toastStore.warning('Cần ít nhất 1 câu hỏi. Xóa bài tập hoàn toàn chưa được hỗ trợ tại đây.')
     return
  }

  const totalPoints = updatedQuestions.reduce((sum, q) => sum + (q.points || 0), 0)

  const payload: CreateExercisePayload = {
    title: `Bài thi: ${selectedLesson.value.title}`,
    type: 'MULTIPLE_CHOICE', // Overall type
    totalPoints: totalPoints || 10,
    questions: updatedQuestions
  }

  try {
    if (currentExercise.value && currentExercise.value.id) {
       await updateExercise(currentExercise.value.id, payload)
       toastStore.success('Đã lưu hệ thống câu hỏi')
    } else {
       const res = await createExercise(selectedLesson.value.id, payload)
       currentExercise.value = res
       toastStore.success('Đã khởi tạo đề thi và thêm câu hỏi')
    }
    // Refresh
    await loadQuestions(selectedLesson.value.id)
    showQuestionModal.value = false
  } catch (err) {
    toastStore.error('Lỗi khi lưu đề thi. Vui lòng kiểm tra lại dữ liệu.')
  }
}

</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 10px;
}
</style>
