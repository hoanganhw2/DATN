<template>
  <DashboardLayout>
  <div>
    <!-- Action Bar -->
    <div class="flex justify-end mb-6">
      <button
        @click="openCreateModal"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        Tạo mã mới
      </button>
    </div>

    <!-- Search / Filter bar -->
    <div class="mb-4 bg-white rounded-xl border border-gray-100 shadow-sm p-3 flex flex-col sm:flex-row gap-2 items-stretch sm:items-center">
      <!-- Keyword search -->
      <div class="relative flex-1">
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
        <input
          v-model="searchInput"
          @keyup.enter="doSearch"
          type="text"
          placeholder="Tìm theo mã coupon..."
          class="w-full pl-9 pr-4 py-2 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
        />
      </div>
      <!-- Scope filter (Admin only) -->
      <select
        v-if="isAdmin"
        v-model="scopeFilter"
        @change="doSearch"
        class="py-2 px-3 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
      >
        <option value="">Tất cả phạm vi</option>
        <option value="GLOBAL">🌐 Toàn hệ thống</option>
        <option value="SPECIFIC">📚 Theo khoá học</option>
      </select>
      <!-- Search btn -->
      <button
        @click="doSearch"
        class="px-5 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl transition shadow-sm"
      >Tìm kiếm</button>
      <!-- Clear btn -->
      <button
        v-if="searchInput || scopeFilter"
        @click="clearSearch"
        class="px-4 py-2 border border-gray-200 text-gray-500 hover:bg-gray-50 text-sm font-medium rounded-xl transition"
      >Xóa bộ lọc</button>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto custom-scrollbar">
        <table class="w-full text-left border-collapse whitespace-nowrap">
          <thead>
            <tr class="bg-gray-50 border-b border-gray-100 text-gray-500 text-sm">
              <th class="p-4 font-medium">Mã Coupon</th>
              <th class="p-4 font-medium">Giảm giá</th>
              <th class="p-4 font-medium">Phạm vi</th>
              <th v-if="isAdmin" class="p-4 font-medium">Người tạo</th>
              <th class="p-4 font-medium">Khoá học</th>
              <th class="p-4 font-medium">Lượt dùng</th>
              <th class="p-4 font-medium">Thời hạn</th>
              <th class="p-4 font-medium text-center">Trạng thái</th>
              <th class="p-4 font-medium text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="loading" class="animate-pulse">
              <td :colspan="isAdmin ? 9 : 8" class="p-4 text-center text-gray-400">Đang tải dữ liệu...</td>
            </tr>
            <tr v-else-if="coupons.length === 0">
              <td :colspan="isAdmin ? 9 : 8" class="p-8 text-center text-gray-400">
                Chưa có mã giảm giá nào. Nhanh tay tạo mã để thu hút học viên!
              </td>
            </tr>
            <tr
              v-else
              v-for="coupon in coupons"
              :key="coupon.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="p-4">
                <span class="inline-block bg-blue-50 text-blue-700 font-mono font-bold px-3 py-1 rounded border border-blue-100">
                  {{ coupon.code }}
                </span>
              </td>
              <td class="p-4 font-semibold text-green-600">-{{ coupon.discountPercent }}%</td>
              <td class="p-4">
                <span
                  :class="coupon.scope === 'GLOBAL'
                    ? 'bg-purple-100 text-purple-700'
                    : 'bg-orange-100 text-orange-700'"
                  class="text-xs px-2 py-1 rounded-full font-medium"
                >
                  {{ coupon.scope === 'GLOBAL' ? '🌐 Toàn hệ thống' : '📚 Theo khoá học' }}
                </span>
              </td>
              <td v-if="isAdmin" class="p-4 text-sm text-gray-500">
                {{ coupon.createdByUsername || '—' }}
              </td>
              <td class="p-4 text-sm text-gray-600 max-w-[200px]">
                <span v-if="coupon.scope === 'GLOBAL'" class="text-gray-400 italic">Tất cả khoá học</span>
                <span v-else-if="coupon.courseNames && coupon.courseNames.length > 0">
                  {{ coupon.courseNames.join(', ') }}
                </span>
                <span v-else class="text-gray-400 italic">Tất cả khoá của tôi</span>
              </td>
              <td class="p-4 text-sm text-gray-600">
                {{ coupon.usedCount }} / {{ coupon.usageLimit || '∞' }}
              </td>
              <td class="p-4 text-sm text-gray-600">
                {{ formatDate(coupon.startDate) }} — {{ formatDate(coupon.endDate) }}
              </td>
              <td class="p-4 text-center">
                <span
                  v-if="coupon.valid"
                  class="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full font-medium"
                >Hoạt động</span>
                <span
                  v-else
                  class="bg-red-100 text-red-700 text-xs px-2 py-1 rounded-full font-medium"
                >Hết hạn/Vô hiệu</span>
              </td>
              <td class="p-4 text-right">
                <div class="flex justify-end gap-2">
                  <button
                    @click="openEditModal(coupon)"
                    class="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded transition-colors"
                    title="Chỉnh sửa"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                    </svg>
                  </button>
                  <button
                    @click="handleDelete(coupon.id)"
                    class="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
                    title="Xóa"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                    </svg>
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
        <div class="flex items-center gap-3">
          <p class="text-sm text-gray-500">
            Hiển thị
            <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
            trong <span class="font-semibold text-gray-700">{{ totalElements }}</span> mã giảm giá
          </p>
          <select
            v-model.number="pageSize"
            @change="() => { currentPage = 0; fetchCoupons(0) }"
            class="py-1.5 px-3 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
          >
            <option :value="10">10 / trang</option>
            <option :value="20">20 / trang</option>
            <option :value="50">50 / trang</option>
          </select>
        </div>
        <div class="flex items-center gap-1.5">
          <button
            :disabled="currentPage === 0"
            @click="goToPage(0)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >«</button>
          <button
            :disabled="currentPage === 0"
            @click="goToPage(currentPage - 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >‹</button>
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
          >{{ p + 1 }}</button>
          <button
            :disabled="isLastPage"
            @click="goToPage(currentPage + 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >›</button>
          <button
            :disabled="isLastPage"
            @click="goToPage(totalPages - 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >»</button>
        </div>
      </div>
    </div>

    <!-- Modal Form (Create/Edit) -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/50 backdrop-blur-sm"
    >
      <div class="bg-white rounded-xl shadow-xl w-full max-w-lg overflow-hidden transform transition-all">
        <div class="p-5 border-b border-gray-100 flex justify-between items-center">
          <h3 class="font-bold text-lg text-gray-900">
            {{ isEditing ? 'Chỉnh sửa mã giảm giá' : 'Tạo mã mới' }}
          </h3>
          <button @click="showModal = false" class="text-gray-400 hover:text-gray-600">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form @submit.prevent="submitForm" class="p-5 space-y-4 max-h-[80vh] overflow-y-auto">
          <!-- Course selection (Teacher only) -->
          <div v-if="!isAdmin">
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Khoá học áp dụng
              <span class="text-gray-400 font-normal">(để trống = áp dụng tất cả khoá của bạn)</span>
            </label>
            <div v-if="loadingCourses" class="text-sm text-gray-400">Đang tải khoá học...</div>
            <div v-else class="space-y-1 max-h-40 overflow-y-auto border border-gray-200 rounded-lg p-2">
              <label
                v-for="course in myCourses"
                :key="course.id"
                class="flex items-center gap-2 p-1.5 rounded hover:bg-gray-50 cursor-pointer"
              >
                <input
                  type="checkbox"
                  :value="course.id"
                  v-model="form.courseIds"
                  class="rounded border-gray-300 text-blue-600"
                />
                <span class="text-sm text-gray-700">{{ course.title }}</span>
              </label>
              <p v-if="myCourses.length === 0" class="text-sm text-gray-400 p-2">Bạn chưa có khoá học nào.</p>
            </div>
          </div>

          <!-- Coupon code -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Mã Coupon *</label>
            <input
              v-model="form.code"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none uppercase"
              placeholder="VD: BANMOI, TET2025"
              :disabled="isEditing"
            />
          </div>

          <!-- Discount percent -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Giảm giá (%) *</label>
            <input
              v-model.number="form.discountPercent"
              type="number"
              min="1"
              max="100"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            />
          </div>

          <!-- Dates -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Ngày bắt đầu</label>
              <input
                v-model="form.startDate"
                type="datetime-local"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Ngày kết thúc</label>
              <input
                v-model="form.endDate"
                type="datetime-local"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
          </div>

          <!-- Usage limit -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Giới hạn số lần dùng</label>
            <input
              v-model.number="form.usageLimit"
              type="number"
              min="1"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              placeholder="Để trống nếu không giới hạn"
            />
          </div>

          <!-- Active toggle -->
          <div class="flex items-center gap-2">
            <input
              v-model="form.isActive"
              type="checkbox"
              id="isActiveCheck"
              class="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <label for="isActiveCheck" class="text-sm text-gray-700 cursor-pointer">Kích hoạt mã này ngay lập tức</label>
          </div>

          <!-- Actions -->
          <div class="pt-4 flex gap-3 justify-end border-t border-gray-100">
            <button
              type="button"
              @click="showModal = false"
              class="px-4 py-2 text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg font-medium transition-colors"
            >Hủy</button>
            <button
              type="submit"
              :disabled="submitting"
              class="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white rounded-lg font-medium transition-colors"
            >
              {{ submitting ? 'Đang lưu...' : (isEditing ? 'Lưu thay đổi' : 'Tạo mã') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { useToastStore } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth'
import { getCoupons, createCoupon, updateCoupon, deleteCoupon, type Coupon, type CouponScope } from '@/api/coupons'
import { useDialog } from '@/composables/useDialog'
import api from '@/api/axios'

const toast = useToastStore()
const dialog = useDialog()
const authStore = useAuthStore()

const isAdmin = computed(() => authStore.isAdmin)

const coupons = ref<Coupon[]>([])
const loading = ref(true)
const submitting = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)

// ── Computed pagination ────────────────────────────────────────────────────────
const isLastPage = computed(() => (currentPage.value + 1) * pageSize.value >= totalElements.value)
const displayStart = computed(() =>
  totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, totalElements.value),
)
const pageNumbers = computed(() => {
  const total = totalPages.value, current = currentPage.value, delta = 2
  const range: number[] = []
  for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++)
    range.push(i)
  return range
})

const goToPage = (p: number) => {
  if (p < 0 || p >= totalPages.value) return
  currentPage.value = p
  fetchCoupons(p)
}

// Courses for SPECIFIC scope
const myCourses = ref<{ id: number; title: string }[]>([])
const loadingCourses = ref(false)

const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)

const form = ref({
  code: '',
  discountPercent: 10,
  startDate: '',
  endDate: '',
  usageLimit: null as number | null,
  isActive: true,
  scope: 'GLOBAL' as CouponScope,
  courseIds: [] as number[],
})

// Reset scope default for teacher
if (!authStore.isAdmin) {
  form.value.scope = 'SPECIFIC'
}

const searchInput = ref('')
const scopeFilter = ref('')

const fetchCoupons = async (page = 0) => {
  loading.value = true
  try {
    const res = await getCoupons(page, pageSize.value, searchInput.value, scopeFilter.value)
    const data = res.data ?? res
    const pageInfo = data.page ?? data
    coupons.value = data.content ?? []
    currentPage.value = pageInfo.number ?? 0
    totalPages.value = pageInfo.totalPages ?? 1
    totalElements.value = pageInfo.totalElements ?? (data.content ?? []).length
  } catch {
    toast.error('Lỗi khi tải danh sách mã giảm giá')
  } finally {
    loading.value = false
  }
}

const doSearch = () => {
  currentPage.value = 0
  fetchCoupons(0)
}

const clearSearch = () => {
  searchInput.value = ''
  scopeFilter.value = ''
  doSearch()
}

const fetchMyCourses = async () => {
  loadingCourses.value = true
  try {
    const res = await api.get('/courses/teacher/courses?size=100')
    const data = res.data?.data ?? res.data
    myCourses.value = (data.content ?? []).map((c: any) => ({ id: c.id, title: c.title }))
  } catch {
    // silently fail
  } finally {
    loadingCourses.value = false
  }
}

// Teacher: tự động load khóa học khi mount
if (!isAdmin.value) {
  fetchMyCourses()
}

const openCreateModal = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    code: '',
    discountPercent: 10,
    startDate: '',
    endDate: '',
    usageLimit: null,
    isActive: true,
    scope: isAdmin.value ? 'GLOBAL' : 'SPECIFIC',
    courseIds: [],
  }
  showModal.value = true
}

const openEditModal = (coupon: Coupon) => {
  isEditing.value = true
  editingId.value = coupon.id

  const formatForInput = (isoDate: string | null) => {
    if (!isoDate) return ''
    return isoDate.substring(0, 16)
  }

  form.value = {
    code: coupon.code,
    discountPercent: coupon.discountPercent,
    startDate: formatForInput(coupon.startDate),
    endDate: formatForInput(coupon.endDate),
    usageLimit: coupon.usageLimit,
    isActive: coupon.isActive,
    scope: coupon.scope ?? (isAdmin.value ? 'GLOBAL' : 'SPECIFIC'),
    courseIds: coupon.courseIds ?? [],
  }
  showModal.value = true
}

const submitForm = async () => {
  submitting.value = true
  try {
    const payload = {
      code: form.value.code,
      discountPercent: form.value.discountPercent,
      usageLimit: form.value.usageLimit || null,
      isActive: form.value.isActive,
      startDate: form.value.startDate ? new Date(form.value.startDate).toISOString() : null,
      endDate: form.value.endDate ? new Date(form.value.endDate).toISOString() : null,
      scope: form.value.scope,
      courseIds: form.value.courseIds,
    }

    if (isEditing.value && editingId.value) {
      await updateCoupon(editingId.value, payload)
      toast.success('Đã cập nhật mã giảm giá')
    } else {
      await createCoupon(payload)
      toast.success('Đã tạo mã giảm giá mới')
    }

    showModal.value = false
    fetchCoupons(currentPage.value)  // stay on same page after edit/create
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Có lỗi xảy ra')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  const confirmed = await dialog.confirm(
    'Xóa mã giảm giá',
    'Bạn có chắc chắn muốn xóa mã giảm giá này?',
    'warning',
  )
  if (!confirmed) return

  try {
    await deleteCoupon(id)
    toast.success('Đã xóa thành công')
    fetchCoupons(currentPage.value)
  } catch {
    toast.error('Xóa thất bại')
  }
}

const formatDate = (dateStr: string | null) => {
  if (!dateStr) return 'Vô thời hạn'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

onMounted(() => {
  fetchCoupons()
})
</script>
