<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Filter Bar -->
      <div
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex flex-col md:flex-row gap-3 items-center"
      >
        <div class="relative flex-1 w-full">
          <SearchIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo tiêu đề hoặc mô tả khóa học..."
            class="w-full pl-9 pr-4 py-2.5 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
          />
        </div>
        <select
          v-model="selectedStatus"
          @change="currentPage = 1"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="published">Đã xuất bản</option>
          <option value="pending">Chờ duyệt</option>
          <option value="rejected">Bị từ chối</option>
        </select>
        <select
          v-model.number="pageSize"
          @change="currentPage = 1"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
        >
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
        <router-link to="/dashboard/teacher/courses/create">
          <Button variant="primary" :icon="PlusIcon">Tạo khóa học</Button>
        </router-link>
      </div>

      <!-- Table Section -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
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
                    Khóa học
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Mức độ
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Giá
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider text-center"
                  >
                    Học viên
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider text-center"
                  >
                    Bài học
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider text-center"
                  >
                    Trạng thái
                  </th>
                  <th
                    class="px-5 py-3.5 font-semibold text-gray-600 text-xs uppercase tracking-wider text-center"
                  >
                    Hành động
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr v-if="filteredCourses.length === 0">
                  <td colspan="8" class="py-16 text-center text-gray-400">
                    <div class="flex flex-col items-center gap-2">
                      <BookOpenIcon class="w-12 h-12 text-gray-200" />
                      <p class="font-medium">Không tìm thấy khóa học</p>
                    </div>
                  </td>
                </tr>
                <tr
                  v-for="(course, idx) in paginatedCourses"
                  :key="course.id"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-4 text-gray-400 text-xs">
                    {{ (currentPage - 1) * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex items-center gap-3 min-w-[280px]">
                      <img
                        :src="course.thumbnailUrl || fallbackThumbnail"
                        :alt="course.title"
                        class="w-14 h-10 rounded-lg object-cover border border-gray-100"
                      />
                      <div class="min-w-0">
                        <p class="font-semibold text-gray-900 truncate">{{ course.title }}</p>
                        <p class="text-xs text-gray-500 truncate max-w-[320px]">
                          {{ course.description || 'Không có mô tả' }}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-xs font-medium text-gray-700">{{ course.level }}</td>
                  <td class="px-5 py-4 text-xs font-semibold text-green-700">
                    {{
                      course.price > 0
                        ? `${Number(course.price).toLocaleString('vi-VN')}đ`
                        : 'Miễn phí'
                    }}
                  </td>
                  <td class="px-5 py-4 text-center text-sm text-gray-700">
                    {{ course.totalEnrollments || 0 }}
                  </td>
                  <td class="px-5 py-4 text-center text-sm text-gray-700">
                    {{ course.totalLessons || 0 }}
                  </td>
                  <td class="px-5 py-4 text-center">
                    <span
                      :class="statusClass(course.status)"
                      class="px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider border"
                    >
                      {{ statusLabel(course.status) }}
                    </span>
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex items-center justify-center gap-2">
                      <!-- <router-link :to="`/dashboard/teacher/courses/${course.id}/edit`">
                        <Button variant="secondary" size="sm" :icon="EditIcon">Sửa</Button>
                      </router-link> -->
                      <router-link :to="`/dashboard/teacher/courses/${course.id}/curriculum`">
                        <Button variant="secondary" size="sm" :icon="LayersIcon"
                          >Chương trình</Button
                        >
                      </router-link>
                      <button
                        v-if="course.status === 'rejected'"
                        @click="resubmitCourse(course)"
                        class="p-2 text-amber-500 hover:bg-amber-50 rounded-lg transition"
                        title="Gửi lại yêu cầu duyệt"
                      >
                        <RefreshCcwIcon class="w-4 h-4" />
                      </button>
                      <button
                        @click="openDeleteModal(course)"
                        class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition"
                        title="Xóa khóa học"
                      >
                        <TrashIcon class="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div
            class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50"
          >
            <p class="text-sm text-gray-500">
              Hiển thị
              <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
              trong
              <span class="font-semibold text-gray-700">{{ filteredCourses.length }}</span> khóa học
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

      <!-- Delete Confirmation Modal -->
      <Modal
        v-model="showDeleteModal"
        title="Xác nhận xóa khóa học"
        confirm-text="Xóa"
        confirm-color="red"
        @confirm="confirmDelete"
      >
        <div class="p-1">
          <p class="text-gray-600 leading-relaxed">
            Bạn có chắc chắn muốn xóa khóa học
            <strong class="text-gray-900">{{ courseToDelete?.title }}</strong
            >?
          </p>
          <div class="mt-4 p-3 bg-red-50 border border-red-100 rounded-lg flex items-start gap-3">
            <div class="mt-0.5">
              <svg
                class="w-5 h-5 text-red-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
            <p class="text-xs text-red-700">
              Hành động này không thể hoàn tác. Tất cả bài học và dữ liệu liên quan sẽ bị xóa vĩnh
              viễn.
            </p>
          </div>
        </div>
      </Modal>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useToastStore } from '@/stores/toast'
import {
  deleteCourse,
  getTeacherCourses,
  updateCourse,
  type Course,
  type CourseApprovalStatus,
} from '@/api/courses'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import Button from '@/components/dashboard/Button.vue'
import Modal from '@/components/dashboard/Modal.vue'
import SearchIcon from '@/components/dashboard/icons/SearchIcon.vue'
import PlusIcon from '@/components/dashboard/icons/PlusIcon.vue'
import EditIcon from '@/components/dashboard/icons/EditIcon.vue'
import TrashIcon from '@/components/dashboard/icons/TrashIcon.vue'
import BookOpenIcon from '@/components/dashboard/icons/BookOpenIcon.vue'
import LayersIcon from '@/components/dashboard/icons/LayersIcon.vue'
import { RefreshCcwIcon } from 'lucide-vue-next'

const toastStore = useToastStore()

type TeacherCourseStatus = 'published' | 'pending' | 'rejected'
type TeacherCourse = Course & { status: TeacherCourseStatus }

const fallbackThumbnail =
  'https://images.unsplash.com/photo-1513258496099-48168024aec0?w=400&h=200&fit=crop'
const courses = ref<TeacherCourse[]>([])
const isLoading = ref(false)

const searchQuery = ref('')
const selectedStatus = ref('')
const pageSize = ref(10)
const currentPage = ref(1)
const showDeleteModal = ref(false)
const courseToDelete = ref<TeacherCourse | null>(null)

const mapStatus = (
  approvalStatus?: CourseApprovalStatus,
  isPublished?: boolean,
): TeacherCourseStatus => {
  if (isPublished) return 'published'
  if (approvalStatus === 'REJECTED') return 'rejected'
  return 'pending'
}

const statusLabel = (status: TeacherCourseStatus) => {
  if (status === 'published') return 'Đã xuất bản'
  if (status === 'rejected') return 'Bị từ chối'
  return 'Chờ duyệt'
}

const statusClass = (status: TeacherCourseStatus) => {
  if (status === 'published') return 'bg-green-50 text-green-700 border-green-200'
  if (status === 'rejected') return 'bg-red-50 text-red-700 border-red-200'
  return 'bg-amber-50 text-amber-700 border-amber-200'
}

const fetchTeacherCourses = async () => {
  isLoading.value = true
  try {
    const page = await getTeacherCourses(1, 100)
    courses.value = page.content.map((course) => ({
      ...course,
      status: mapStatus(course.approvalStatus, course.isPublished),
    }))
  } catch (error) {
    toastStore.error('Không thể tải danh sách khóa học')
  } finally {
    isLoading.value = false
  }
}

const filteredCourses = computed(() => {
  return courses.value.filter((course) => {
    const matchesSearch =
      course.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (course.description || '').toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesStatus = !selectedStatus.value || course.status === selectedStatus.value

    return matchesSearch && matchesStatus
  })
})

const totalPages = computed(() => Math.ceil(filteredCourses.value.length / pageSize.value))

const displayStart = computed(() => {
  if (filteredCourses.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const displayEnd = computed(() =>
  Math.min(currentPage.value * pageSize.value, filteredCourses.value.length),
)

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredCourses.value.slice(start, start + pageSize.value)
})

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const openDeleteModal = (course: TeacherCourse) => {
  courseToDelete.value = course
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!courseToDelete.value) return
  try {
    await deleteCourse(courseToDelete.value.id)
    courses.value = courses.value.filter((c) => c.id !== courseToDelete.value?.id)
    toastStore.success('Xóa khóa học thành công')
  } catch (error) {
    toastStore.error('Không thể xóa khóa học')
  } finally {
    showDeleteModal.value = false
    courseToDelete.value = null
  }
}

const resubmitCourse = async (course: TeacherCourse) => {
  try {
    isLoading.value = true
    await updateCourse(course.id, {})
    toastStore.success('Đã gửi lại yêu cầu duyệt khóa học')
    await fetchTeacherCourses()
  } catch (error) {
    toastStore.error('Không thể gửi lại yêu cầu duyệt')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchTeacherCourses()
})

watch([searchQuery, selectedStatus], () => {
  currentPage.value = 1
})
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
</style>
