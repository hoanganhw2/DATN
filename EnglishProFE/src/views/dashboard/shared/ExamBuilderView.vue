<template>
  <DashboardLayout>
    <div class="max-w-6xl mx-auto pb-20">
      <!-- Action Bar -->
      <div class="flex items-center justify-between mb-6">
        <button @click="goBack" class="p-2 hover:bg-gray-100 rounded-full transition-colors">
          <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/></svg>
        </button>
        <div class="flex items-center gap-3">
          <button
            @click="saveDraft"
            class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition"
            :disabled="isSaving"
          >
            Lưu nháp
          </button>
          <button
            @click="submitExam"
            class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition flex items-center gap-2"
            :disabled="isSaving || isFetching"
          >
            <span v-if="isSaving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            {{ isEditMode ? 'Cập nhật Đề thi' : 'Xuất bản Đề thi' }}
          </button>
        </div>
      </div>

      <!-- Loading Overlay -->
      <div v-if="isFetching" class="flex flex-col items-center justify-center py-20">
        <div class="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-gray-500 font-medium">Đang tải dữ liệu đề thi...</p>
      </div>
      <div v-else>

      <!-- Stepper -->
      <div class="mb-8 flex items-center justify-center">
        <div class="flex items-center max-w-2xl w-full">
          <div class="flex-1 text-center">
            <button
              @click="currentStep = 1"
              :class="['w-10 h-10 rounded-full font-bold flex items-center justify-center mx-auto transition-colors', currentStep === 1 ? 'bg-blue-600 text-white' : 'bg-green-500 text-white']"
            >
              1
            </button>
            <p class="mt-2 text-sm font-medium text-gray-700">Thông tin chung</p>
          </div>
          <div class="w-full h-1 bg-gray-200 relative -mt-6">
            <div class="absolute h-full bg-green-500 transition-all duration-300" :style="{ width: currentStep === 2 ? '100%' : '0%' }"></div>
          </div>
          <div class="flex-1 text-center">
            <button
              @click="goToStep2"
              :class="['w-10 h-10 rounded-full font-bold flex items-center justify-center mx-auto transition-colors', currentStep === 2 ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-500']"
            >
              2
            </button>
            <p class="mt-2 text-sm font-medium text-gray-700">Soạn câu hỏi</p>
          </div>
        </div>
      </div>

      <!-- STEP 1: GENERAL INFO -->
      <div v-show="currentStep === 1" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-bold text-gray-900 mb-6">Thông tin cơ bản</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Tiêu đề đề thi <span class="text-red-500">*</span></label>
            <input v-model="form.title" type="text" placeholder="VD: TOEIC Full Test 1 (2025)" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
          </div>
          
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
            <textarea v-model="form.description" rows="3" placeholder="Giới thiệu về đề thi..." class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Loại đề thi <span class="text-red-500">*</span></label>
            <select v-model="form.examType" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition bg-white">
              <option value="TOEIC">TOEIC</option>
              <option value="IELTS">IELTS</option>
              <option value="GENERAL">Tiếng Anh Tổng Quát</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Trình độ</label>
            <select v-model="form.level" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition bg-white">
              <option value="">Không phân loại</option>
              <option value="BEGINNER">Cơ bản (Beginner)</option>
              <option value="INTERMEDIATE">Trung cấp (Intermediate)</option>
              <option value="ADVANCED">Nâng cao (Advanced)</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Thời lượng (Phút) <span class="text-red-500">*</span></label>
            <input v-model.number="durationMinutes" type="number" min="1" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Tổng điểm quy đổi <span class="text-red-500">*</span></label>
            <input v-model.number="form.totalScore" type="number" min="1" placeholder="VD: 990" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Ngưỡng điểm đạt (%)</label>
            <input v-model.number="form.passingScore" type="number" min="0" max="100" placeholder="VD: 50" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Số lần thi tối đa</label>
            <input v-model.number="form.maxAttempts" type="number" min="1" placeholder="Để trống nếu không giới hạn" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
          </div>
        </div>

        <div class="mt-8 flex justify-end">
          <button @click="goToStep2" class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition flex items-center gap-2">
            Tiếp tục soạn câu hỏi
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
          </button>
        </div>
      </div>

      <!-- STEP 2: BUILDER -->
      <div v-show="currentStep === 2" class="flex flex-col lg:flex-row gap-6">
        <!-- Sidebar: Sections List -->
        <div class="w-full lg:w-1/3 space-y-4">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
            <div class="flex items-center justify-between mb-4">
              <h2 class="font-bold text-gray-900">Cấu trúc đề thi</h2>
              <button @click="addSection" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
              </button>
            </div>
            
            <draggable
              v-model="sections"
              item-key="id"
              handle=".drag-handle"
              class="space-y-2"
            >
              <template #item="{ element: section, index }">
                <div
                  @click="activeSectionIndex = index"
                  :class="[
                    'p-3 rounded-xl border cursor-pointer transition flex items-center gap-3',
                    activeSectionIndex === index ? 'bg-blue-50 border-blue-200 ring-1 ring-blue-500' : 'bg-white border-gray-200 hover:border-blue-300'
                  ]"
                >
                  <div class="drag-handle cursor-grab text-gray-400 hover:text-gray-600">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/></svg>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="font-medium text-sm text-gray-900 truncate">{{ section.sectionName || `Phần ${index + 1}` }}</p>
                    <p class="text-xs text-gray-500">{{ section.questions.length }} câu hỏi</p>
                  </div>
                  <button @click.stop="removeSection(index)" class="p-1.5 text-red-500 hover:bg-red-100 rounded-lg transition">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                  </button>
                </div>
              </template>
            </draggable>

            <div v-if="sections.length === 0" class="text-center py-6">
              <p class="text-sm text-gray-500">Chưa có phần thi nào. Hãy nhấn dấu + để thêm.</p>
            </div>
          </div>
        </div>

        <!-- Main Area: Active Section Editor -->
        <div class="w-full lg:w-2/3" v-if="activeSection">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4">Cài đặt Phần thi</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Tên phần thi</label>
                <input v-model="activeSection.sectionName" type="text" placeholder="VD: Part 1 - Listening" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
              </div>
              <!-- Optional Section Audio/Media could be added here in the future -->
            </div>
          </div>

          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-gray-900">Danh sách Câu hỏi</h3>
            <button @click="addQuestion" class="px-3 py-1.5 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition flex items-center gap-1.5">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
              Thêm câu hỏi
            </button>
          </div>

          <draggable
            v-model="activeSection.questions"
            item-key="id"
            handle=".drag-handle-q"
            class="space-y-4"
          >
            <template #item="{ element: q, index: qIndex }">
              <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 relative group">
                <div class="absolute top-4 right-4 flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <div class="drag-handle-q cursor-grab p-1.5 text-gray-400 hover:text-gray-600 bg-gray-50 rounded-lg">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/></svg>
                  </div>
                  <button @click="removeQuestion(qIndex)" class="p-1.5 text-red-500 hover:text-red-700 bg-red-50 rounded-lg">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                  </button>
                </div>

                <div class="mb-4 pr-20">
                  <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-blue-100 text-blue-700 text-xs font-bold mr-2">
                    {{ qIndex + 1 }}
                  </span>
                  <label class="text-sm font-medium text-gray-700">Nội dung câu hỏi</label>
                  <textarea v-model="q.questionText" rows="2" class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm" placeholder="Nhập câu hỏi..."></textarea>
                </div>

                <!-- Type Selector -->
                <div class="mb-4">
                  <label class="text-xs font-medium text-gray-500 uppercase tracking-wider">Loại câu hỏi</label>
                  <select v-model="q.questionType" @change="onQuestionTypeChange(q)" class="mt-1 block w-48 px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:ring-blue-500 outline-none">
                    <option value="MULTIPLE_CHOICE">Trắc nghiệm</option>
                    <option value="FILL_IN_BLANK">Điền từ</option>
                  </select>
                </div>

                <!-- Media Input (Audio/Image URL) -->
                <div class="mb-4">
                  <label class="text-xs font-medium text-gray-500 uppercase tracking-wider">Đính kèm (Audio/Image)</label>
                  <div class="flex items-center gap-2 mt-1">
                    <input v-model="q.mediaUrl" type="text" placeholder="https://... hoặc tải lên" class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 outline-none text-sm" />
                    <input type="file" @change="(e) => uploadMediaFile(e, q)" accept="image/*,audio/*" class="hidden" :id="`upload-${q.id}`" />
                    <label :for="`upload-${q.id}`" class="cursor-pointer px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition whitespace-nowrap flex items-center gap-1">
                      <svg v-if="!q.isUploading" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"/></svg>
                      <span v-else class="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin"></span>
                      Tải lên
                    </label>
                  </div>
                </div>

                <!-- Multiple Choice Options -->
                <div v-if="q.questionType === 'MULTIPLE_CHOICE'" class="space-y-2 mb-4">
                  <label class="text-xs font-medium text-gray-500 uppercase tracking-wider">Đáp án trắc nghiệm</label>
                  <div v-for="(opt, optIndex) in q.options" :key="optIndex" class="flex items-center gap-2">
                    <input type="radio" :name="`correct-${q.id}`" :value="opt" v-model="q.correctAnswer" class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500" />
                    <input v-model="q.options[optIndex]" type="text" class="flex-1 px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:ring-blue-500 outline-none" :placeholder="`Đáp án ${String.fromCharCode(65 + optIndex)}`" />
                    <button @click="q.options.splice(optIndex, 1)" class="p-1 text-gray-400 hover:text-red-500"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg></button>
                  </div>
                  <button @click="q.options.push('')" class="text-xs font-medium text-blue-600 hover:underline mt-1">+ Thêm lựa chọn</button>
                </div>

                <!-- Fill in Blank Correct Answer -->
                <div v-if="q.questionType === 'FILL_IN_BLANK'" class="mb-4">
                  <label class="text-xs font-medium text-gray-500 uppercase tracking-wider">Đáp án đúng</label>
                  <input v-model="q.correctAnswer" type="text" placeholder="Nhập đáp án chính xác..." class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 outline-none text-sm" />
                </div>

                <!-- Points & Explanation -->
                <div class="grid grid-cols-1 md:grid-cols-4 gap-4 bg-gray-50 p-3 rounded-lg border border-gray-100">
                  <div class="col-span-1">
                    <label class="text-xs font-medium text-gray-700">Điểm số</label>
                    <input v-model.number="q.points" type="number" min="1" class="mt-1 w-full px-2 py-1 text-sm border border-gray-300 rounded focus:ring-blue-500 outline-none" />
                  </div>
                  <div class="col-span-3">
                    <label class="text-xs font-medium text-gray-700">Giải thích (Tuỳ chọn)</label>
                    <input v-model="q.explanation" type="text" placeholder="Giải thích tại sao đáp án này đúng..." class="mt-1 w-full px-2 py-1 text-sm border border-gray-300 rounded focus:ring-blue-500 outline-none" />
                  </div>
                </div>

              </div>
            </template>
          </draggable>

          <div v-if="activeSection.questions.length === 0" class="text-center py-10 bg-white border border-dashed border-gray-300 rounded-2xl">
            <svg class="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            <p class="text-gray-500 font-medium">Chưa có câu hỏi nào</p>
            <button @click="addQuestion" class="mt-2 text-sm text-blue-600 hover:underline">Thêm câu hỏi đầu tiên</button>
          </div>
        </div>

        <div v-else class="w-full lg:w-2/3 flex items-center justify-center bg-gray-50 rounded-2xl border border-dashed border-gray-300 h-96">
          <div class="text-center text-gray-500">
            <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
            <p class="font-medium text-lg">Chọn một phần thi để chỉnh sửa</p>
            <p class="text-sm mt-1">Hoặc tạo một phần thi mới ở cột bên trái</p>
          </div>
        </div>
      </div>
      </div> <!-- End of v-else wrap -->
    </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToastStore } from '@/stores/toast'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import draggable from 'vuedraggable'
import { createExam, updateExam, getExamById, uploadExamMedia } from '@/api/exam'

const router = useRouter()
const route = useRoute()
const toastStore = useToastStore()

const isEditMode = ref(false)
const isFetching = ref(false)
const examId = ref(null)

const isSaving = ref(false)
const currentStep = ref(1)

// Form State
const form = reactive({
  title: '',
  description: '',
  examType: 'TOEIC',
  level: '',
  totalScore: 990,
  passingScore: 50,
  maxAttempts: null
})

const durationMinutes = ref(120)

// Builder State
const sections = ref([])
const activeSectionIndex = ref(-1)

const activeSection = computed(() => {
  if (activeSectionIndex.value >= 0 && activeSectionIndex.value < sections.value.length) {
    return sections.value[activeSectionIndex.value]
  }
  return null
})

// Methods
const goBack = () => {
  router.back()
}

const goToStep2 = () => {
  if (!form.title) {
    alert('Vui lòng nhập tiêu đề đề thi')
    return
  }
  currentStep.value = 2
  if (sections.value.length === 0) {
    addSection()
  }
}

const generateId = () => Math.random().toString(36).substr(2, 9)

const addSection = () => {
  sections.value.push({
    id: generateId(),
    sectionName: `Phần ${sections.value.length + 1}`,
    questions: []
  })
  activeSectionIndex.value = sections.value.length - 1
}

const removeSection = (index) => {
  if (confirm('Xoá phần thi này sẽ xoá toàn bộ câu hỏi bên trong. Tiếp tục?')) {
    sections.value.splice(index, 1)
    if (activeSectionIndex.value === index) {
      activeSectionIndex.value = Math.max(0, index - 1)
      if (sections.value.length === 0) activeSectionIndex.value = -1
    } else if (activeSectionIndex.value > index) {
      activeSectionIndex.value--
    }
  }
}

const addQuestion = () => {
  if (!activeSection.value) return
  activeSection.value.questions.push({
    id: generateId(),
    questionText: '',
    mediaUrl: '',
    questionType: 'MULTIPLE_CHOICE',
    options: ['Đáp án A', 'Đáp án B', 'Đáp án C', 'Đáp án D'],
    correctAnswer: 'Đáp án A',
    explanation: '',
    points: 1
  })
}

const removeQuestion = (qIndex) => {
  if (activeSection.value) {
    activeSection.value.questions.splice(qIndex, 1)
  }
}

const uploadMediaFile = async (event, q) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    q.isUploading = true
    const res = await uploadExamMedia(file)
    if (res.status === 200) {
      q.mediaUrl = res.data // Set the URL returned from backend
    }
  } catch (error) {
    alert(error.response?.data?.message || 'Có lỗi xảy ra khi tải lên file')
  } finally {
    q.isUploading = false
    event.target.value = '' // Reset input
  }
}

const onQuestionTypeChange = (q) => {
  if (q.questionType === 'FILL_IN_BLANK') {
    q.options = []
    q.correctAnswer = ''
  } else if (q.questionType === 'MULTIPLE_CHOICE') {
    if (!q.options || q.options.length === 0) {
      q.options = ['Đáp án A', 'Đáp án B', 'Đáp án C', 'Đáp án D']
      q.correctAnswer = 'Đáp án A'
    }
  }
}

const buildPayload = (isPublished = false) => {
  // Convert sections array to proper payload
  const payloadSections = sections.value.map((sec, sIdx) => ({
    sectionName: sec.sectionName,
    sectionOrder: sIdx,
    totalQuestions: sec.questions.length,
    questions: sec.questions.map((q, qIdx) => ({
      questionText: q.questionText || '(Chưa nhập)',
      mediaUrl: q.mediaUrl,
      questionType: q.questionType,
      options: q.options,
      correctAnswer: q.correctAnswer,
      explanation: q.explanation,
      points: q.points || 1,
      orderIndex: qIdx
    }))
  }))

  return {
    ...form,
    duration: durationMinutes.value * 60, // convert back to seconds
    isPublished,
    sections: payloadSections
  }
}

const validatePayload = (payload) => {
  if (!payload.title) return 'Tiêu đề không được để trống'
  if (payload.sections.length === 0) return 'Đề thi phải có ít nhất 1 phần thi'
  
  for (let i = 0; i < payload.sections.length; i++) {
    const sec = payload.sections[i]
    if (sec.questions.length === 0) return `Phần thi "${sec.sectionName}" phải có ít nhất 1 câu hỏi`
    for (let j = 0; j < sec.questions.length; j++) {
      const q = sec.questions[j]
      if (q.questionType === 'MULTIPLE_CHOICE' && (!q.options || q.options.length === 0)) {
        return `Câu hỏi số ${j+1} trong phần "${sec.sectionName}" chưa có đáp án trắc nghiệm`
      }
      if (!q.correctAnswer) {
        return `Câu hỏi số ${j+1} trong phần "${sec.sectionName}" chưa có đáp án đúng`
      }
    }
  }
  return null
}

const saveDraft = async () => {
  await submit(false)
}

const submitExam = async () => {
  await submit(true)
}

const submit = async (isPublished) => {
  const payload = buildPayload(isPublished)
  
  const error = validatePayload(payload)
  if (error) {
    toastStore.error(error)
    return
  }

  try {
    isSaving.value = true
    let res
    if (isEditMode.value) {
      res = await updateExam(examId.value, payload)
    } else {
      res = await createExam(payload)
    }
    
    if (res.status === 201 || res.status === 200) {
      toastStore.success(isEditMode.value ? 'Cập nhật đề thi thành công!' : 'Tạo đề thi thành công!')
      goBack()
    }
  } catch (error) {
    toastStore.error(error.response?.data?.message || 'Có lỗi xảy ra')
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  if (route.params.id) {
    isEditMode.value = true
    examId.value = route.params.id
    isFetching.value = true
    try {
      const data = await getExamById(examId.value)
      form.title = data.title || ''
      form.description = data.description || ''
      form.examType = data.examType || 'TOEIC'
      form.level = data.level || ''
      form.totalScore = data.totalScore || 990
      form.passingScore = data.passingScore || 50
      form.maxAttempts = data.maxAttempts || null
      durationMinutes.value = Math.round((data.duration || 7200) / 60)
      
      // Load sections
      if (data.sections && data.sections.length > 0) {
        sections.value = data.sections.map(s => ({
          id: s.id || generateId(),
          sectionName: s.sectionName,
          questions: (s.questions || []).map(q => ({
            id: q.id || generateId(),
            questionText: q.questionText,
            mediaUrl: q.mediaUrl || '',
            questionType: q.questionType || 'MULTIPLE_CHOICE',
            options: q.options || [],
            correctAnswer: q.correctAnswer || '',
            explanation: q.explanation || '',
            points: q.points || 1,
            isUploading: false
          }))
        }))
        if (sections.value.length > 0) {
          activeSectionIndex.value = 0
        }
      }

      // Jump to step 2 (question editor) if requested via query param
      if (route.query.step === '2' || route.query.step === 2) {
        currentStep.value = 2
        if (sections.value.length === 0) {
          addSection()
        }
      }
    } catch (error) {
      toastStore.error('Không thể tải dữ liệu đề thi')
    } finally {
      isFetching.value = false
    }
  }
})
</script>
