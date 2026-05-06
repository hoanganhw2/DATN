<template>
  <DashboardLayout>
    <div class="space-y-5">
      <!-- Filter Bar -->
      <div
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex flex-col md:flex-row gap-3 items-center"
      >
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
            v-model="searchInput"
            type="text"
            placeholder="Tìm theo tên khóa học, giảng viên..."
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
        <button
          @click="fetchPending"
          class="px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-bold rounded-xl transition shadow-sm border border-gray-200"
        >
          Làm mới
        </button>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="isLoading" class="flex items-center justify-center py-24">
          <div class="flex flex-col items-center gap-3">
            <div
              class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <p class="text-sm text-gray-500">Đang tải dữ liệu...</p>
          </div>
        </div>

        <template v-else>
          <div class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    #
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Khóa học
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Giảng viên
                  </th>
                  <th
                    class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Giá
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <!-- Empty -->
                <tr v-if="filteredCourses.length === 0">
                  <td colspan="5" class="py-16 text-center text-gray-400">
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
                          d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                        />
                      </svg>
                      <p class="font-medium">Không có khóa học nào đang chờ duyệt</p>
                    </div>
                  </td>
                </tr>
                <!-- Rows -->
                <tr
                  v-for="(course, idx) in paginatedCourses"
                  :key="course.id"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-4 text-gray-400 text-xs">
                    {{ (currentPage - 1) * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex items-center gap-3">
                      <div>
                        <p class="font-semibold text-gray-900">{{ course.title }}</p>
                        <p class="text-xs text-gray-400 max-w-xs truncate" :title="course.description">{{ course.description || 'Không có mô tả' }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-gray-600">
                    {{ course.teacherFullName || course.teacher?.fullName || 'N/A' }}
                  </td>
                  <td class="px-5 py-4 text-right font-medium text-gray-700">
                    {{ Number(course.price || 0).toLocaleString('vi-VN') }}đ
                  </td>
                  <td class="px-5 py-4 text-center">
                    <div class="flex items-center justify-center gap-2">
                      <button
                        @click="openPreview(course)"
                        class="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-gray-700 bg-gray-50 hover:bg-gray-100 border border-gray-200 rounded-lg transition"
                      >
                        <EyeIcon class="w-3.5 h-3.5" />
                        Xem nội dung
                      </button>
                      <button
                        @click="review(course.id, false)"
                        class="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition"
                      >
                        <XCircleIcon class="w-3.5 h-3.5" />
                        Từ chối
                      </button>
                      <button
                        @click="review(course.id, true)"
                        class="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition"
                      >
                        <CheckCircleIcon class="w-3.5 h-3.5" />
                        Duyệt & phát hành
                      </button>
                    </div>
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
              trong <span class="font-semibold text-gray-700">{{ filteredCourses.length }}</span> khóa học
            </p>
            <div class="flex items-center gap-1.5">
              <button
                :disabled="currentPage === 1"
                @click="goToPage(1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                «
              </button>
              <button
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                ‹
              </button>
              <button
                v-for="p in pageNumbers"
                :key="p"
                @click="goToPage(p)"
                :class="[
                  'w-10 h-10 rounded-xl text-sm font-semibold transition border',
                  p === currentPage
                    ? 'bg-blue-600 text-white border-blue-600 shadow'
                    : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300',
                ]"
              >
                {{ p }}
              </button>
              <button
                :disabled="isLastPage"
                @click="goToPage(currentPage + 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                ›
              </button>
              <button
                :disabled="isLastPage"
                @click="goToPage(totalPages)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                »
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- Preview Modal -->
    <div v-if="previewCourse" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden">
        
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h3 class="text-lg font-bold text-gray-900 line-clamp-1 flex-1 mr-4">Xem trước: {{ previewCourse.title }}</h3>
          <button @click="closePreview" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-200 rounded-full transition-colors">
            <XIcon class="w-5 h-5" />
          </button>
        </div>

        <!-- Body -->
        <div class="p-6 overflow-y-auto flex-1">
          <div v-if="isPreviewLoading" class="flex justify-center py-10">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
          <div v-else-if="previewDetail" class="space-y-8">
            <!-- Thông tin chung -->
            <div class="flex gap-6">
              <img v-if="previewDetail.thumbnailUrl" :src="previewDetail.thumbnailUrl" class="w-48 h-32 object-cover rounded-lg border shadow-sm" alt="Thumbnail" />
              <div v-else class="w-48 h-32 bg-gray-100 rounded-lg border shadow-sm flex items-center justify-center text-gray-400">
                Không có ảnh
              </div>
              <div class="flex-1 space-y-2">
                <p class="text-sm text-gray-600 whitespace-pre-wrap">{{ previewDetail.description || 'Không có mô tả chi tiết' }}</p>
                <div class="flex flex-wrap gap-4 text-sm text-gray-600 mt-4">
                  <div class="bg-gray-100 px-3 py-1 rounded-full text-xs font-medium">Cấp độ: {{ previewDetail.level }}</div>
                  <div class="bg-gray-100 px-3 py-1 rounded-full text-xs font-medium">Giá: {{ Number(previewDetail.price || 0).toLocaleString('vi-VN') }}đ</div>
                  <div class="bg-gray-100 px-3 py-1 rounded-full text-xs font-medium">{{ previewDetail.totalLessons || 0 }} bài học</div>
                </div>
              </div>
            </div>

            <!-- Nội dung chương trình -->
            <div>
              <h4 class="text-lg font-bold text-gray-900 mb-4 border-b pb-2">Nội dung khóa học</h4>
              <div v-if="!previewDetail.chapters || previewDetail.chapters.length === 0" class="text-gray-500 italic">
                Chưa có chương/bài học nào.
              </div>
              <div v-else class="space-y-4">
                <div v-for="(chapter, idx) in previewDetail.chapters" :key="chapter.id" class="border border-gray-200 rounded-xl overflow-hidden bg-white">
                  <div class="bg-gray-50 px-4 py-3 font-semibold text-gray-900 flex justify-between items-center border-b border-gray-100">
                    <span>Chương {{ idx + 1 }}: {{ chapter.title }}</span>
                    <span class="text-xs font-normal text-gray-500 bg-white px-2 py-1 rounded-md border">{{ chapter.lessons?.length || 0 }} bài</span>
                  </div>
                  <div class="divide-y divide-gray-100">
                    <div v-if="!chapter.lessons || chapter.lessons.length === 0" class="p-3 text-sm text-gray-400 italic pl-8">
                      Trống
                    </div>
                    <div v-for="(lesson, lIdx) in chapter.lessons" :key="lesson.id" 
                         @click="viewLesson(lesson)"
                         class="px-4 py-3 flex items-center gap-3 hover:bg-blue-50/30 transition-colors cursor-pointer group">
                      <component :is="getLessonIcon(lesson.contentType)" class="w-4 h-4 text-gray-400 group-hover:text-blue-500 transition-colors" />
                      <div class="flex-1">
                        <span class="text-sm font-medium text-gray-700 group-hover:text-blue-600 transition-colors">{{ lIdx + 1 }}. {{ lesson.title }}</span>
                      </div>
                      <div class="text-xs text-gray-500 flex gap-3">
                        <span v-if="lesson.isFree" class="text-green-600 bg-green-50 px-2 py-0.5 rounded-full">Học thử</span>
                        <span v-if="lesson.duration">{{ Math.round(lesson.duration / 60) }} phút</span>
                      </div>
                      <EyeIcon class="w-4 h-4 text-gray-300 group-hover:text-blue-500 transition-colors" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 border-t border-gray-100 bg-gray-50 flex justify-end gap-3 rounded-b-2xl">
          <Button variant="secondary" @click="closePreview">Đóng</Button>
          <Button variant="secondary" class="border-red-200 text-red-600 hover:bg-red-50" @click="review(previewCourse.id, false); closePreview()">Từ chối</Button>
          <Button variant="primary" @click="review(previewCourse.id, true); closePreview()">Duyệt & phát hành</Button>
        </div>
      </div>
    </div>

    <!-- Lesson Preview Modal -->
    <div v-if="viewingLesson" class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-3xl max-h-[90vh] flex flex-col overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h3 class="text-lg font-bold text-gray-900 line-clamp-1 flex-1 mr-4">Bài học: {{ viewingLesson.title }}</h3>
          <button @click="closeLessonView" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-200 rounded-full transition-colors">
            <XIcon class="w-5 h-5" />
          </button>
        </div>
        <div class="p-6 overflow-y-auto flex-1 space-y-4">
          <div v-if="viewingLesson.contentType === 'VIDEO'">
            <div v-if="viewingLesson.contentUrl && isYoutubeUrl(viewingLesson.contentUrl)" class="w-full aspect-video">
              <iframe
                :src="getYoutubeEmbedUrl(viewingLesson.contentUrl)"
                frameborder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen
                class="w-full h-full rounded-lg"
              ></iframe>
            </div>
            <video v-else-if="viewingLesson.contentUrl" :src="getContentUrl(viewingLesson.contentUrl)" controls class="w-full rounded-lg bg-black"></video>
            <p v-else class="text-gray-500 italic">Chưa có video</p>
          </div>
          <div v-else-if="viewingLesson.contentType === 'AUDIO'">
            <audio v-if="viewingLesson.contentUrl" :src="getContentUrl(viewingLesson.contentUrl)" controls class="w-full"></audio>
            <p v-else class="text-gray-500 italic">Chưa có audio</p>
          </div>
          <div v-else-if="viewingLesson.contentType === 'DOCUMENT'">
            <template v-if="viewingLesson.contentUrl">
              <iframe v-if="isPdf(viewingLesson.contentUrl)" :src="getContentUrl(viewingLesson.contentUrl)" class="w-full h-[600px] rounded-lg border"></iframe>
              <div v-else class="p-8 text-center bg-gray-50 rounded-lg border border-gray-200">
                 <FileIcon class="w-12 h-12 text-gray-400 mx-auto mb-3" />
                 <p class="text-gray-600 mb-4">Định dạng tài liệu không hỗ trợ xem trước trực tiếp. Bạn có muốn tải xuống không?</p>
                 <a :href="getContentUrl(viewingLesson.contentUrl)" target="_blank" class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium transition-colors">
                   Tải xuống tài liệu
                 </a>
              </div>
            </template>
            <p v-else class="text-gray-500 italic">Chưa có tài liệu</p>
          </div>
          <div v-else-if="viewingLesson.contentType === 'TEXT'">
            <div class="prose max-w-none" v-html="viewingLesson.description || 'Chưa có nội dung'"></div>
          </div>
          <div v-else-if="viewingLesson.contentType === 'QUIZ'">
            <div class="p-4 bg-yellow-50 border border-yellow-200 text-yellow-800 rounded-lg">
              Bài tập trắc nghiệm (Admin không hỗ trợ làm bài tập, vui lòng kiểm tra mô tả nếu có)
            </div>
            <div class="prose max-w-none mt-4" v-html="viewingLesson.description || ''"></div>
          </div>
          
          <div v-if="viewingLesson.description && viewingLesson.contentType !== 'TEXT' && viewingLesson.contentType !== 'QUIZ'" class="mt-6 pt-6 border-t border-gray-100">
             <h4 class="font-bold text-gray-900 mb-2">Mô tả</h4>
             <div class="prose max-w-none" v-html="viewingLesson.description"></div>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import Button from '@/components/dashboard/Button.vue'
import { getPendingCoursesForAdmin, reviewCourseApproval, getCourseDetail, type Course, type CourseDetail, type Lesson } from '@/api/courses'
import { useToastStore } from '@/stores/toast'
import { PlayIcon, FileTextIcon, HelpCircleIcon, XIcon, HeadphonesIcon, EyeIcon, XCircleIcon, CheckCircleIcon, FileIcon } from 'lucide-vue-next'

const toastStore = useToastStore()
const courses = ref<Course[]>([])
const isLoading = ref(false)
const searchInput = ref('')
const pageSize = ref(20)
const currentPage = ref(1)

const filteredCourses = computed(() => {
  const q = searchInput.value.toLowerCase().trim()
  if (!q) return courses.value
  return courses.value.filter(c =>
    c.title.toLowerCase().includes(q) ||
    (c.teacherFullName && c.teacherFullName.toLowerCase().includes(q)) ||
    (c.teacher?.fullName && c.teacher.fullName.toLowerCase().includes(q))
  )
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredCourses.value.length / pageSize.value)))
const isLastPage = computed(() => currentPage.value >= totalPages.value)
const displayStart = computed(() =>
  filteredCourses.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min(currentPage.value * pageSize.value, filteredCourses.value.length),
)

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredCourses.value.slice(start, start + pageSize.value)
})

const pageNumbers = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const delta = 2
  const range: number[] = []
  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) {
    range.push(i)
  }
  return range
})

const goToPage = (p: number) => {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
}

watch(searchInput, () => {
  currentPage.value = 1
})

// Preview State
const previewCourse = ref<Course | null>(null)
const previewDetail = ref<CourseDetail | null>(null)
const isPreviewLoading = ref(false)

const viewingLesson = ref<Lesson | null>(null)
const viewLesson = (lesson: Lesson) => {
  viewingLesson.value = lesson
}
const closeLessonView = () => {
  viewingLesson.value = null
}

const getLessonIcon = (type: string) => {
  switch (type) {
    case 'VIDEO':
      return PlayIcon
    case 'QUIZ':
      return HelpCircleIcon
    case 'AUDIO':
      return HeadphonesIcon
    case 'DOCUMENT':
      return FileIcon
    default:
      return FileTextIcon
  }
}

const isYoutubeUrl = (url: string) => {
  return url.includes('youtube.com') || url.includes('youtu.be')
}

const getYoutubeEmbedUrl = (url: string) => {
  const match = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&]+)/)
  return match ? `https://www.youtube.com/embed/${match[1]}` : url
}

const getContentUrl = (path: string | null | undefined): string => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return `http://localhost:8888/api/v1/files/${path}`
}

const isPdf = (url: string) => url.toLowerCase().endsWith('.pdf')

const openPreview = async (course: Course) => {
  previewCourse.value = course
  isPreviewLoading.value = true
  previewDetail.value = null
  try {
    // Gọi API lấy detail để thấy được curriculum
    previewDetail.value = await getCourseDetail(course.slug || course.id.toString())
  } catch (error) {
    toastStore.error('Không thể tải chi tiết khóa học')
  } finally {
    isPreviewLoading.value = false
  }
}

const closePreview = () => {
  previewCourse.value = null
  previewDetail.value = null
}

const fetchPending = async () => {
  isLoading.value = true
  try {
    const page = await getPendingCoursesForAdmin(1, 100)
    courses.value = page.content
  } catch (error) {
    toastStore.error('Không thể tải danh sách chờ duyệt')
  } finally {
    isLoading.value = false
  }
}

const review = async (courseId: number, approved: boolean) => {
  try {
    await reviewCourseApproval(courseId, approved)
    courses.value = courses.value.filter((c) => c.id !== courseId)
    toastStore.success(approved ? 'Đã duyệt và phát hành khóa học' : 'Đã từ chối khóa học')
  } catch (error) {
    toastStore.error('Không thể cập nhật trạng thái duyệt')
  }
}

onMounted(() => {
  fetchPending()
})
</script>
