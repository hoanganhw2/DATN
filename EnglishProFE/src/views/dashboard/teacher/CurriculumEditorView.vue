<template>
  <DashboardLayout>
    <div class="max-w-7xl mx-auto space-y-6">
      <!-- Breadcrumb & Header -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2 text-sm text-gray-500">
          <router-link to="/dashboard/teacher/courses" class="hover:text-blue-600 transition">
            Khóa học
          </router-link>
          <span>/</span>
          <span class="text-gray-900 font-semibold truncate max-w-[300px]">
            {{ courseTitle }}
          </span>
          <span>/</span>
          <span class="text-blue-600 font-bold">Chương trình</span>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        <!-- Sidebar - Curriculum Tree -->
        <div
          class="lg:col-span-4 bg-white rounded-2xl border border-gray-100 shadow-sm p-5 self-start sticky top-6"
        >
          <div class="flex justify-between items-center mb-5 pb-3 border-b border-gray-100">
            <h3 class="text-lg font-bold text-gray-900">Nội dung khóa học</h3>
            <button
              @click="openAddChapterModal"
              class="flex items-center justify-center p-2 rounded-xl bg-blue-50 text-blue-600 hover:bg-blue-100 hover:shadow-sm transition"
              title="Thêm chương mới"
            >
              <PlusIcon class="w-5 h-5" />
            </button>
          </div>

          <!-- Loading State -->
          <div v-if="isLoading" class="flex flex-col items-center justify-center py-10">
            <div
              class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <p class="text-sm text-gray-500 mt-3">Đang tải chương trình học...</p>
          </div>

          <!-- Chapters Tree -->
          <draggable
            v-else
            v-model="chapters"
            item-key="id"
            handle=".drag-handle-chapter"
            @end="onReorder"
            class="space-y-3 max-h-[calc(100vh-200px)] overflow-y-auto custom-scrollbar pr-2"
          >
            <template #item="{ element: chapter, index }">
              <div
                class="bg-gray-50 border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:border-gray-300 transition-colors"
              >
                <!-- Chapter Header -->
                <div
                  class="flex items-center justify-between p-3.5 bg-white cursor-pointer group"
                  @click="toggleChapter(chapter.id)"
                >
                  <div class="flex items-center gap-3">
                    <div
                      class="drag-handle-chapter cursor-grab text-gray-400 hover:text-gray-600 p-1"
                      @click.stop
                    >
                      <GripVerticalIcon class="w-4 h-4" />
                    </div>
                    <ChevronRightIcon
                      :class="[
                        'w-4 h-4 text-gray-400 transition-transform',
                        expandedChapters.includes(chapter.id) ? 'rotate-90' : '',
                      ]"
                    />
                    <span class="font-bold text-gray-800 text-sm"
                      >Chương {{ index + 1 }}: {{ chapter.title }}</span
                    >
                  </div>
                  <div
                    class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <button
                      @click.stop="editChapter(chapter)"
                      class="p-1.5 text-blue-500 hover:bg-blue-50 rounded-lg"
                    >
                      <EditIcon class="w-4 h-4" />
                    </button>
                    <button
                      @click.stop="confirmDeleteChapter(chapter)"
                      class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </div>
                </div>

                <!-- Lessons List -->
                <div
                  v-if="expandedChapters.includes(chapter.id)"
                  class="bg-gray-50/50 pb-2 border-t border-gray-100"
                >
                  <draggable
                    v-model="chapter.lessons"
                    item-key="id"
                    group="lessons"
                    handle=".drag-handle-lesson"
                    @end="onReorder"
                    class="space-y-1 mt-2"
                  >
                    <template #item="{ element: lesson }">
                      <div
                        @click="selectLesson(lesson, chapter)"
                        :class="[
                          'flex items-center justify-between px-4 py-2.5 mx-2 rounded-lg cursor-pointer transition border',
                          selectedLesson?.id === lesson.id
                            ? 'bg-blue-50 border-blue-200 shadow-sm'
                            : 'border-transparent hover:bg-gray-100/80',
                        ]"
                      >
                        <div class="flex items-center gap-2.5 overflow-hidden">
                          <div
                            class="drag-handle-lesson cursor-grab text-gray-300 hover:text-gray-500"
                            @click.stop
                          >
                            <GripVerticalIcon class="w-3.5 h-3.5" />
                          </div>
                          <component
                            :is="getLessonIcon(lesson.contentType)"
                            class="w-4 h-4 text-gray-400 flex-shrink-0"
                          />
                          <span
                            class="text-sm font-medium text-gray-700 truncate"
                            :title="lesson.title"
                            >{{ lesson.title }}</span
                          >
                          <span
                            v-if="lesson.isFree"
                            class="bg-green-100 text-green-700 text-[10px] font-bold px-1.5 py-0.5 rounded ml-1 flex-shrink-0"
                            >FREE</span
                          >
                        </div>
                        <div class="flex items-center pl-2">
                          <button
                            @click.stop="confirmDeleteLesson(lesson)"
                            class="p-1 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded"
                          >
                            <TrashIcon class="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </template>
                  </draggable>

                  <button
                    @click="openAddLessonModal(chapter.id)"
                    class="flex items-center justify-center gap-2 w-[calc(100%-16px)] mx-auto mt-2 py-2 text-sm text-blue-600 bg-white border border-dashed border-blue-200 rounded-lg hover:bg-blue-50 hover:border-blue-400 transition"
                  >
                    <PlusIcon class="w-4 h-4" /> Thêm bài học mới
                  </button>
                </div>
              </div>
            </template>
          </draggable>
        </div>

        <!-- Main Workspace - Lesson Editor -->
        <div class="lg:col-span-8">
          <div
            v-if="selectedLesson"
            class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
          >
            <!-- Workspace Header -->
            <div
              class="border-b border-gray-100 px-6 py-4 flex items-center justify-between bg-white"
            >
              <h2 class="text-xl font-bold text-gray-900 truncate pr-4">
                {{ selectedLesson.title || 'Chưa có tiêu đề' }}
              </h2>
              <div class="flex gap-3 flex-shrink-0">
                <Button variant="secondary" size="sm" class="font-medium" @click="handlePreview">
                  Xem thử
                </Button>
                <Button
                  variant="primary"
                  size="sm"
                  class="font-medium shadow-sm"
                  :disabled="isSaving"
                  @click="saveLesson"
                >
                  <span
                    v-if="isSaving"
                    class="animate-spin mr-2 border-2 border-white rounded-full w-4 h-4 border-t-transparent"
                  ></span>
                  Lưu bài học
                </Button>
              </div>
            </div>

            <!-- Tabs Navigation -->
            <div class="flex px-6 pt-2 bg-gray-50/50 border-b border-gray-200">
              <button
                @click="activeTab = 'config'"
                :class="[
                  'py-3.5 px-5 font-semibold text-sm transition-colors border-b-2 -mb-[1px]',
                  activeTab === 'config'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700',
                ]"
              >
                Cấu hình chung
              </button>
              <button
                @click="activeTab = 'content'"
                :class="[
                  'py-3.5 px-5 font-semibold text-sm transition-colors border-b-2 -mb-[1px]',
                  activeTab === 'content'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700',
                ]"
              >
                Nội dung & Media
              </button>
            </div>

            <div class="p-6">
              <!-- Config Tab -->
              <div v-show="activeTab === 'config'" class="space-y-6">
                <!-- Title & Type -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-2"
                      >Tiêu đề bài học <span class="text-red-500">*</span></label
                    >
                    <input
                      v-model="lessonForm.title"
                      type="text"
                      class="w-full bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-gray-900 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-2"
                      >Dạng bài học <span class="text-red-500">*</span></label
                    >
                    <select
                      v-model="lessonForm.contentType"
                      class="w-full bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-gray-900 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
                    >
                      <option value="VIDEO">Video truyền thống</option>
                      <option value="TEXT">Bài giảng dạng văn bản</option>
                      <option value="AUDIO">Audio nghe hiểu</option>
                      <option value="QUIZ">Bài tập ngắn (Quiz)</option>
                      <option value="EXAM">Bài thi (TOEIC/IELTS)</option>
                      <option value="DOCUMENT">Tài liệu (PDF, Word...)</option>
                    </select>
                  </div>
                </div>

                <!-- Settings Row -->
                <div
                  class="grid grid-cols-1 md:grid-cols-2 gap-6 bg-gray-50 p-5 rounded-xl border border-gray-100"
                >
                  <div class="flex items-center justify-between">
                    <div>
                      <h4 class="font-semibold text-gray-900">Cho phép học thử</h4>
                      <p class="text-xs text-gray-500 mt-1">Học viên có thể xem mà không cần mua</p>
                    </div>
                    <label class="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" v-model="lessonForm.isFree" class="sr-only peer" />
                      <div
                        class="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
                      ></div>
                    </label>
                  </div>

                  <div>
                    <label class="block text-sm font-semibold text-gray-700 flex justify-between">
                      Thời lượng dự kiến (phút/giây)
                      <span class="font-normal text-gray-500">~{{ formattedFormDuration }}</span>
                    </label>
                    <input
                      v-model.number="lessonForm.duration"
                      type="number"
                      min="0"
                      class="mt-1 w-full bg-white border border-gray-200 rounded-xl px-4 py-2 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
                    />
                  </div>
                </div>
              </div>

              <!-- Content & Media Tab -->
              <div v-show="activeTab === 'content'" class="space-y-6">
                <!-- Video/Audio Upload Zone -->
                <div
                  v-if="['VIDEO', 'AUDIO', 'DOCUMENT'].includes(lessonForm.contentType)"
                  class="rounded-xl border border-gray-200 bg-white overflow-hidden shadow-sm"
                >
                  <div class="bg-gray-50 p-4 border-b border-gray-200">
                    <h4 class="font-semibold text-gray-800 flex items-center gap-2">
                      <MonitorPlayIcon class="w-5 h-5 text-blue-500" />
                      Media Source
                    </h4>
                  </div>

                  <div class="p-5">
                    <!-- URL Input -->
                    <div class="mb-5">
                      <label class="block text-sm font-medium text-gray-700 mb-2"
                        >Đường dẫn URL (Youtube / Vimeo / MP4)</label
                      >
                      <input
                        v-model="lessonForm.contentUrl"
                        type="text"
                        placeholder="https://..."
                        class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2.5 focus:border-blue-500 transition"
                      />
                    </div>

                    <!-- Or Upload File -->
                    <div class="relative w-full">
                      <div class="absolute inset-0 flex items-center" aria-hidden="true">
                        <div class="w-full border-t border-gray-200"></div>
                      </div>
                      <div class="relative flex justify-center">
                        <span class="bg-white px-2 text-sm text-gray-500">HOẶC TẢI LÊN</span>
                      </div>
                    </div>

                    <div
                      class="mt-5 border-2 border-dashed border-gray-300 hover:border-blue-500 rounded-xl p-8 text-center bg-gray-50 transition relative group"
                    >
                      <input
                        type="file"
                        @change="handleFileUpload"
                        accept="video/*,audio/*,.pdf,.doc,.docx,.xls,.xlsx"
                        class="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
                      />

                      <div v-if="isUploading" class="flex flex-col items-center justify-center">
                        <div
                          class="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mb-3"
                        ></div>
                        <p class="font-semibold text-blue-600">Đang tải lên media...</p>
                        <p class="text-sm text-gray-500 mt-1">Vui lòng không đóng cửa sổ</p>
                      </div>
                      <div v-else>
                        <UploadCloudIcon
                          class="mx-auto h-12 w-12 text-gray-400 group-hover:text-blue-500 transition"
                        />
                        <div class="mt-4 flex text-sm leading-6 text-gray-600 justify-center">
                          <label
                            class="relative cursor-pointer rounded-md bg-transparent font-semibold text-blue-600 focus-within:outline-none hover:text-blue-500"
                          >
                            <span>Tải file từ máy tính</span>
                          </label>
                          <p class="pl-1">hoặc kéo thả vào đây</p>
                        </div>
                        <p class="text-xs leading-5 text-gray-500 mt-2">
                          MP4, MP3, PDF, DOCX, XLSX (up to 500MB)
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Quiz Builder Placeholder -->
                <div
                  v-if="lessonForm.contentType === 'QUIZ'"
                  class="rounded-xl border border-gray-200 bg-blue-50 overflow-hidden shadow-sm p-8 text-center"
                >
                  <div
                    class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-white shadow-sm mb-4"
                  >
                    <QuizIcon class="w-8 h-8 text-blue-600" />
                  </div>
                  <h3 class="text-lg font-bold text-gray-900">Quản lý bài tập ngắn</h3>
                  <p class="text-sm text-gray-600 mt-2 max-w-md mx-auto">
                    Đây là học phần dạng Bài tập ngắn. Bạn cần lưu bài học này trước, sau đó truy
                    cập vào trình quản lý cấu hình các câu hỏi trắc nghiệm riêng biệt.
                  </p>
                  <button
                    @click="openExerciseEditor"
                    class="mt-5 px-5 py-2.5 bg-blue-600 text-white font-medium rounded-xl hover:bg-blue-700 shadow-sm transition inline-flex items-center gap-2"
                  >
                    <EditIcon class="w-4 h-4" />
                    Mở bộ soạn thảo câu hỏi
                  </button>
                </div>

                <!-- Exam Selector State -->
                <div
                  v-if="lessonForm.contentType === 'EXAM'"
                  class="p-6 bg-indigo-50 border border-indigo-100 rounded-xl mt-4 mb-4"
                >
                  <div class="flex items-center gap-3 mb-4">
                    <QuizIcon class="w-8 h-8 text-indigo-600" />
                    <div>
                      <h3 class="text-lg font-bold text-gray-900">Chọn đề thi TOEIC/IELTS</h3>
                      <p class="text-sm text-gray-600">Bài thi sẽ được nhúng vào khóa học của bạn</p>
                    </div>
                  </div>
                  <select
                    v-model="lessonForm.contentUrl"
                    class="w-full bg-white border border-gray-300 rounded-xl px-4 py-3 text-gray-900 focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition"
                  >
                    <option value="" disabled>-- Chọn đề thi từ thư viện --</option>
                    <option v-for="exam in availableExams" :key="exam.id" :value="'exam:' + exam.id">
                      {{ exam.title }} ({{ exam.examType }})
                    </option>
                  </select>
                  <p class="mt-3 text-sm text-gray-500">
                    Chưa có đề thi? 
                    <router-link to="/dashboard/teacher/exams/create" target="_blank" class="text-indigo-600 hover:underline font-medium">
                      Tạo đề thi mới tại đây
                    </router-link>
                  </p>
                </div>

                <!-- Text Content Editor -->
                <div>
                  <div class="flex items-center justify-between mb-2">
                    <label class="block text-sm font-semibold text-gray-700"
                      >Mô tả / Hướng dẫn thêm</label
                    >
                  </div>
                  <textarea
                    v-model="lessonForm.description"
                    placeholder="Nhập nội dung giảng dạy, hướng dẫn làm bài hoặc mô tả bổ sung..."
                    rows="10"
                    class="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-gray-900 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition resize-y font-mono text-sm"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State Workspace -->
          <div
            v-else
            class="h-full min-h-[500px] flex flex-col items-center justify-center bg-gray-50/50 border border-gray-200 border-dashed rounded-2xl text-center p-12"
          >
            <div
              class="w-20 h-20 bg-white rounded-full shadow-sm flex items-center justify-center mb-6"
            >
              <BookOpenIcon class="w-10 h-10 text-blue-200" />
            </div>
            <h3 class="mt-2 text-xl font-bold text-gray-900">Trình soạn thảo bài học</h3>
            <p class="mt-2 text-gray-500 max-w-sm">
              Chọn một bài học từ danh sách bên trái hoặc tạo bài học mới để bắt đầu thiết kế nội
              dung giảng dạy.
            </p>
          </div>
        </div>
      </div>

      <!-- Add/Edit Chapter Modal -->
      <Modal
        v-model="showChapterModal"
        :title="editingChapter ? 'Sửa chương' : 'Thêm chương mới'"
        @confirm="submitChapter"
      >
        <div class="p-2 space-y-4">
          <FormInput
            v-model="chapterForm.title"
            label="Tên chương *"
            placeholder="VD: Chương 1: Giới thiệu cơ bản"
            required
          />
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả chương</label>
            <textarea
              v-model="chapterForm.description"
              class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 focus:border-blue-500 transition"
              rows="3"
              placeholder="Giới thiệu nhanh về nội dung chương này..."
            ></textarea>
          </div>
        </div>
      </Modal>

      <!-- Delete Confirmation Modals -->
      <Modal
        v-model="showConfirmDeleteChapter"
        title="Xóa chương học"
        confirm-text="Xóa vĩnh viễn"
        confirm-color="red"
        @confirm="executeDeleteChapter"
      >
        <div class="p-2">
          <p class="text-gray-700 mb-3">
            Bạn có chắc muốn xóa chương: <strong>{{ chapterToDelete?.title }}</strong
            >?
          </p>
          <div
            class="bg-red-50 text-red-700 text-sm p-4 rounded-lg border border-red-100 flex gap-3"
          >
            <AlertTriangleIcon class="w-5 h-5 flex-shrink-0" />
            <p>
              Hành động này sẽ xóa vĩnh viễn chương này
              <strong>và TOÀN BỘ các bài học bên trong</strong>. Không thể hoàn tác!
            </p>
          </div>
        </div>
      </Modal>

      <Modal
        v-model="showConfirmDeleteLesson"
        title="Xóa bài học"
        confirm-text="Xóa"
        confirm-color="red"
        @confirm="executeDeleteLesson"
      >
        <p class="text-gray-700 p-2">
          Bạn có chắc chắn muốn xóa bài học: <strong>{{ lessonToDelete?.title }}</strong
          >?
        </p>
      </Modal>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'
import draggable from 'vuedraggable'

// Components
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import FormInput from '@/components/dashboard/FormInput.vue'
import Button from '@/components/dashboard/Button.vue'
import Modal from '@/components/dashboard/Modal.vue'

// Icons
import PlusIcon from '@/components/dashboard/icons/PlusIcon.vue'
import EditIcon from '@/components/dashboard/icons/EditIcon.vue'
import TrashIcon from '@/components/dashboard/icons/TrashIcon.vue'
import ChevronRightIcon from '@/components/dashboard/icons/ChevronRightIcon.vue'
import PlayIcon from '@/components/dashboard/icons/PlayIcon.vue'
import {
  GripVertical as GripVerticalIcon,
  MonitorPlay as MonitorPlayIcon,
  UploadCloud as UploadCloudIcon,
  FileText as FileTextIcon,
  HelpCircle as QuizIcon,
  Mic as AudioIcon,
  BookOpen as BookOpenIcon,
  AlertTriangle as AlertTriangleIcon,
  File as FileIcon,
} from 'lucide-vue-next'

// APIs
import {
  getCourseDetail,
  createChapter,
  updateChapter,
  deleteChapter as apiDeleteChapter,
  createLesson,
  updateLesson,
  deleteLesson as apiDeleteLesson,
  uploadLessonMedia,
  reorderCurriculum,
  type Chapter,
  type Lesson,
} from '@/api/courses'
import { getMyExams } from '@/api/exam'

const route = useRoute()
const router = useRouter()
const toastStore = useToastStore()
const courseId = Number(route.params.id)

// State
const isLoading = ref(true)
const courseTitle = ref('Đang tải...')
const chapters = ref<Chapter[]>([])
const expandedChapters = ref<number[]>([])
const availableExams = ref<any[]>([])

// Workspace State
const selectedLesson = ref<Lesson | null>(null)
const selectedChapterId = ref<number | null>(null)
const activeTab = ref('config')
const isSaving = ref(false)
const isUploading = ref(false)

// Modals State
const showChapterModal = ref(false)
const editingChapter = ref<Chapter | null>(null)
const chapterForm = ref({ title: '', description: '' })

const showConfirmDeleteChapter = ref(false)
const chapterToDelete = ref<Chapter | null>(null)

const showConfirmDeleteLesson = ref(false)
const lessonToDelete = ref<Lesson | null>(null)

// Forms
const lessonForm = ref({
  title: '',
  description: '',
  contentType: 'VIDEO',
  contentUrl: '',
  duration: 0,
  isFree: false,
})

// Lifecycle
onMounted(async () => {
  await loadCurriculum()
  await loadExams()
})

const loadExams = async () => {
  try {
    const res = await getMyExams({ size: 100 })
    if (res.status === 200) {
      availableExams.value = res.data.content
    }
  } catch (error) {
    console.error('Không thể tải danh sách đề thi', error)
  }
}

// Methods: Load Data
const loadCurriculum = async () => {
  isLoading.value = true
  try {
    const detail = await getCourseDetail(courseId.toString()) // By ID works
    courseTitle.value = detail.title
    chapters.value = detail.chapters || []

    // Auto expand first chapter
    if (chapters.value.length > 0) {
      expandedChapters.value.push(chapters.value[0].id)
    }
  } catch (error) {
    toastStore.error('Không thể tải dữ liệu chương trình học')
  } finally {
    isLoading.value = false
  }
}

// Methods: Chapters
const toggleChapter = (id: number) => {
  const index = expandedChapters.value.indexOf(id)
  if (index > -1) expandedChapters.value.splice(index, 1)
  else expandedChapters.value.push(id)
}

const openAddChapterModal = () => {
  editingChapter.value = null
  chapterForm.value = { title: '', description: '' }
  showChapterModal.value = true
}

const editChapter = (chapter: Chapter) => {
  editingChapter.value = chapter
  chapterForm.value = { title: chapter.title, description: '' }
  showChapterModal.value = true
}

const submitChapter = async () => {
  if (!chapterForm.value.title.trim()) {
    toastStore.error('Vui lòng nhập tên chương')
    return
  }

  try {
    const payload = {
      title: chapterForm.value.title,
      description: chapterForm.value.description,
      orderIndex: chapters.value.length + 1,
    }

    if (editingChapter.value) {
      await updateChapter(courseId, editingChapter.value.id, payload)
      toastStore.success('Đã cập nhật chương')
    } else {
      await createChapter(courseId, payload)
      toastStore.success('Đã tạo chương mới')
    }

    showChapterModal.value = false
    await loadCurriculum()
  } catch (err) {
    toastStore.error('Lỗi khi lưu chương học')
  }
}

const confirmDeleteChapter = (chapter: Chapter) => {
  chapterToDelete.value = chapter
  showConfirmDeleteChapter.value = true
}

const executeDeleteChapter = async () => {
  if (!chapterToDelete.value) return

  try {
    await apiDeleteChapter(courseId, chapterToDelete.value.id)
    toastStore.success('Đã xóa chương')
    if (selectedChapterId.value === chapterToDelete.value.id) {
      selectedLesson.value = null
    }
    showConfirmDeleteChapter.value = false
    await loadCurriculum()
  } catch (err) {
    toastStore.error('Không thể xóa chương này')
  }
}

// Methods: Reorder (drag-and-drop persistence)
const onReorder = async () => {
  try {
    // Collect current order from the reactive chapters array
    const chapterItems = chapters.value.map((ch, idx) => ({
      id: ch.id,
      orderIndex: idx + 1,
    }))

    const lessonItems: { id: number; orderIndex: number }[] = []
    for (const ch of chapters.value) {
      ch.lessons.forEach((lesson, idx) => {
        lessonItems.push({ id: lesson.id, orderIndex: idx + 1 })
      })
    }

    await reorderCurriculum(courseId, {
      chapters: chapterItems,
      lessons: lessonItems,
    })

    toastStore.success('Đã cập nhật thứ tự')
  } catch (err) {
    toastStore.error('Lỗi khi cập nhật thứ tự')
    // Reload to restore correct order from server
    await loadCurriculum()
  }
}

// Methods: Lessons
const openAddLessonModal = async (chapterId: number) => {
  // Directly create a new lesson with default values, then select it to edit
  try {
    const chapter = chapters.value.find((c) => c.id === chapterId)
    const newOrder = chapter ? chapter.lessons.length + 1 : 1

    const newLesson = await createLesson(courseId, chapterId, {
      title: 'Bài học mới',
      contentType: 'VIDEO',
      orderIndex: newOrder,
      isFree: false,
    })

    toastStore.success('Đã tạo bài học, vui lòng chỉnh sửa nội dung')
    await loadCurriculum()

    // Auto select the new lesson
    const updatedChapter = chapters.value.find((c) => c.id === chapterId)
    if (updatedChapter) {
      const created = updatedChapter.lessons.find((l) => l.id === newLesson.id)
      if (created) selectLesson(created, updatedChapter)
    }
  } catch (err) {
    toastStore.error('Lỗi khi tạo bài học mới')
  }
}

const selectLesson = (lesson: Lesson, chapter: Chapter) => {
  selectedLesson.value = lesson
  selectedChapterId.value = chapter.id
  activeTab.value = 'config'

  // Clone to form
  lessonForm.value = {
    title: lesson.title || '',
    description: (lesson as any).description || '',
    contentType: lesson.contentType || 'VIDEO',
    contentUrl: lesson.contentUrl || '',
    duration: lesson.duration || 0,
    isFree: lesson.isFree || false,
  }
}

const saveLesson = async () => {
  if (!selectedLesson.value) return
  if (!lessonForm.value.title.trim()) {
    toastStore.error('Vui lòng nhập tên bài học')
    return
  }

  isSaving.value = true
  try {
    await updateLesson(selectedLesson.value.id, {
      title: lessonForm.value.title,
      description: lessonForm.value.description,
      contentType: lessonForm.value.contentType as any,
      contentUrl: lessonForm.value.contentUrl,
      duration: lessonForm.value.duration,
      orderIndex: selectedLesson.value.orderIndex || 1,
      isFree: lessonForm.value.isFree,
    })

    toastStore.success('Đã lưu bài học thành công')
    await loadCurriculum()

    // Re-select to keep selection active visually
    if (selectedChapterId.value) {
      const updatedChap = chapters.value.find((c) => c.id === selectedChapterId.value)
      if (updatedChap) {
        const sl = updatedChap.lessons.find((l) => l.id === selectedLesson.value?.id)
        if (sl) selectedLesson.value = sl
      }
    }
  } catch (err) {
    toastStore.error('Lỗi khi lưu bài học')
  } finally {
    isSaving.value = false
  }
}

const confirmDeleteLesson = (lesson: Lesson) => {
  lessonToDelete.value = lesson
  showConfirmDeleteLesson.value = true
}

const executeDeleteLesson = async () => {
  if (!lessonToDelete.value) return

  try {
    await apiDeleteLesson(lessonToDelete.value.id)
    toastStore.success('Đã xóa bài học')
    if (selectedLesson.value?.id === lessonToDelete.value.id) {
      selectedLesson.value = null
    }
    showConfirmDeleteLesson.value = false
    await loadCurriculum()
  } catch (err) {
    toastStore.error('Lỗi sửa đổi nội dung')
  }
}

// Media Upload
const handleFileUpload = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file || !selectedLesson.value) return

  // Basic validation
  if (file.size > 500 * 1024 * 1024) {
    toastStore.error('File quá lớn (Tối đa 500MB)')
    return
  }

  isUploading.value = true
  try {
    const res = await uploadLessonMedia(selectedLesson.value.id, file)
    lessonForm.value.contentUrl = res.contentUrl
    if (res.duration) lessonForm.value.duration = res.duration
    toastStore.success('Tải lên Media thành công, đang lưu bài học...')
    // Auto-save immediately so contentUrl is persisted to DB right away
    await saveLesson()
  } catch (err) {
    toastStore.error('Tải lên thất bại')
  } finally {
    isUploading.value = false
    // Reset file input
    ;(event.target as HTMLInputElement).value = ''
  }
}

const handlePreview = () => {
  if (lessonForm.value.contentUrl) {
    window.open(lessonForm.value.contentUrl, '_blank')
  } else {
    toastStore.info('Vui lòng lưu URL media trước khi xem thử')
  }
}

const openExerciseEditor = () => {
  if (selectedLesson.value && courseId) {
    router.push(`/dashboard/teacher/courses/${courseId}/exercises?lessonId=${selectedLesson.value.id}`)
  } else {
    toastStore.warning('Vui lòng chọn bài học hợp lệ')
  }
}

const getLessonIcon = (type: string) => {
  switch (type) {
    case 'VIDEO':
      return PlayIcon
    case 'TEXT':
      return FileTextIcon
    case 'AUDIO':
      return AudioIcon
    case 'QUIZ':
      return QuizIcon
    case 'EXAM':
      return QuizIcon
    case 'DOCUMENT':
      return FileIcon
    default:
      return PlayIcon
  }
}

const formattedFormDuration = computed(() => {
  const d = lessonForm.value.duration
  if (!d) return '0 giây'
  const m = Math.floor(d / 60)
  const s = d % 60
  return `${m}m ${s}s`
})
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

/* Drag handling transitions */
.sortable-ghost {
  opacity: 0.5;
  background: #f1f5f9;
}
.sortable-drag {
  cursor: grabbing !important;
}
</style>
