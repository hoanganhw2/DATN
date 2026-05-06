<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- ─── Stats Cards ──────────────────────────────────────────── -->
      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5">
        <div
          v-for="stat in stats"
          :key="stat.label"
          class="bg-white rounded-2xl border border-gray-100 p-5 flex items-center gap-4 shadow-sm hover:shadow-md transition-shadow"
        >
          <div :class="`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 ${stat.bg}`">
            <component :is="stat.icon" :class="`w-6 h-6 ${stat.iconColor}`" />
          </div>
          <div>
            <p class="text-xs text-gray-500 font-medium">{{ stat.label }}</p>
            <p class="text-xl font-bold text-gray-900 mt-0.5">{{ stat.value }}</p>
          </div>
        </div>
      </div>

      <!-- ─── Filter Bar ────────────────────────────────────────────── -->
      <div
        class="bg-white rounded-2xl border border-gray-100 p-4 shadow-sm flex flex-col md:flex-row gap-3 items-center"
      >
        <div class="relative flex-1 w-full">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            id="admin-order-search"
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo mã đơn, tên, username, khóa học..."
            class="w-full pl-9 pr-4 py-2.5 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
          />
        </div>
        <select
          id="admin-order-status-filter"
          v-model="statusFilter"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 bg-white transition"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="PENDING">Chờ thanh toán</option>
          <option value="COMPLETED">Đã hoàn thành</option>
          <option value="FAILED">Thất bại</option>
          <option value="CANCELLED">Đã hủy</option>
          <option value="REFUNDED">Hoàn tiền</option>
        </select>
        <select
          id="admin-order-page-size"
          v-model.number="pageSize"
          @change="onPageSizeChange"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 bg-white transition"
        >
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
      </div>

      <!-- ─── Table ─────────────────────────────────────────────────── -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-24">
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
                    Mã đơn
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Người mua
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Khóa học
                  </th>
                  <th
                    class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Giá gốc
                  </th>
                  <th
                    class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Giảm giá
                  </th>
                  <th
                    class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Thực thu
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Trạng thái
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Ngày tạo
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <!-- Empty state -->
                <tr v-if="filteredOrders.length === 0">
                  <td colspan="10" class="py-16 text-center">
                    <div class="flex flex-col items-center gap-2 text-gray-400">
                      <ShoppingCart class="w-12 h-12 text-gray-200" />
                      <p class="font-medium">Không có đơn hàng nào</p>
                    </div>
                  </td>
                </tr>

                <!-- Rows -->
                <tr
                  v-for="(order, idx) in filteredOrders"
                  :key="order.id"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-3.5 text-gray-400 text-xs">
                    {{ currentPage * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-3.5">
                    <span
                      class="font-mono text-xs font-bold text-blue-700 bg-blue-50 px-2 py-1 rounded"
                    >
                      {{ order.orderCode }}
                    </span>
                  </td>
                  <td class="px-5 py-3.5">
                    <div class="flex items-center gap-3">
                      <div
                        class="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold shrink-0"
                      >
                        {{ getInitials(order.fullName || order.username) }}
                      </div>
                      <div>
                        <p class="font-semibold text-gray-900 truncate max-w-[130px]">
                          {{ order.fullName || '—' }}
                        </p>
                        <p class="text-xs text-gray-400">@{{ order.username }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-3.5">
                    <p
                      class="text-sm text-gray-800 truncate max-w-[200px]"
                      :title="order.courseTitle"
                    >
                      {{ order.courseTitle }}
                    </p>
                  </td>
                  <td class="px-5 py-3.5 text-right text-sm text-gray-600 whitespace-nowrap">
                    {{ formatVND(order.amount) }}
                  </td>
                  <td class="px-5 py-3.5 text-right whitespace-nowrap">
                    <span
                      v-if="Number(order.discountAmount) > 0"
                      class="text-red-500 text-sm font-semibold"
                    >
                      -{{ formatVND(order.discountAmount) }}
                    </span>
                    <span v-else class="text-gray-300">—</span>
                  </td>
                  <td
                    class="px-5 py-3.5 text-right font-bold text-sm text-gray-900 whitespace-nowrap"
                  >
                    {{ formatVND(order.finalAmount) }}
                  </td>
                  <td class="px-5 py-3.5 text-center">
                    <span
                      :class="getStatusClass(order.status)"
                      class="px-2.5 py-1 rounded-full text-xs font-bold"
                    >
                      {{ getStatusLabel(order.status) }}
                    </span>
                  </td>
                  <td class="px-5 py-3.5 text-gray-500 text-xs whitespace-nowrap">
                    {{ formatDate(order.createdAt) }}
                  </td>
                  <td class="px-5 py-3.5 text-center">
                    <button
                      @click="openDetail(order)"
                      class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition"
                    >
                      <Eye class="w-3.5 h-3.5" />
                      Chi tiết
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- ─── Pagination ────────────────────────────────────────── -->
          <div
            class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50"
          >
            <!-- Info text -->
            <p class="text-sm text-gray-500">
              Hiển thị
              <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
              trong tổng
              <span class="font-semibold text-gray-700">{{ totalElements }}</span>
              đơn hàng
            </p>

            <!-- Buttons -->
            <div class="flex items-center gap-1.5">
              <!-- First -->
              <button
                :disabled="currentPage === 0"
                @click="goToPage(0)"
                title="Trang đầu"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-lg"
              >
                «
              </button>

              <!-- Prev -->
              <button
                :disabled="currentPage === 0"
                @click="goToPage(currentPage - 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-lg"
              >
                ‹
              </button>

              <!-- Page numbers -->
              <button
                v-for="p in pageNumbers"
                :key="p"
                @click="goToPage(p)"
                :class="[
                  'w-10 h-10 rounded-xl text-sm font-semibold transition border',
                  p === currentPage
                    ? 'bg-blue-600 text-white border-blue-600 shadow-md'
                    : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300',
                ]"
              >
                {{ p + 1 }}
              </button>

              <!-- Next -->
              <button
                :disabled="isLastPage"
                @click="goToPage(currentPage + 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-lg"
              >
                ›
              </button>

              <!-- Last -->
              <button
                :disabled="isLastPage"
                @click="goToPage(totalPages - 1)"
                title="Trang cuối"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-lg"
              >
                »
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- ─── Order Detail Modal ───────────────────────────────────── -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div
          v-if="showDetail && selectedOrder"
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
        >
          <div
            class="absolute inset-0 bg-black/40 backdrop-blur-sm"
            @click="showDetail = false"
          ></div>
          <div
            class="relative bg-white rounded-3xl shadow-2xl w-full max-w-lg z-10 overflow-hidden"
          >
            <!-- Header -->
            <div class="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-blue-100 text-xs font-medium uppercase tracking-widest mb-1">
                    Chi tiết đơn hàng
                  </p>
                  <h2 class="text-xl font-bold font-mono">{{ selectedOrder.orderCode }}</h2>
                </div>
                <button
                  @click="showDetail = false"
                  class="w-9 h-9 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition"
                >
                  <X class="w-5 h-5" />
                </button>
              </div>
              <div class="mt-4">
                <span
                  :class="getStatusClassLight(selectedOrder.status)"
                  class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider"
                >
                  {{ getStatusLabel(selectedOrder.status) }}
                </span>
              </div>
            </div>

            <!-- Body -->
            <div class="p-6 space-y-5">
              <div class="flex gap-4 p-4 bg-gray-50 rounded-2xl">
                <img
                  v-if="selectedOrder.courseThumbnail"
                  :src="selectedOrder.courseThumbnail"
                  class="w-20 h-14 object-cover rounded-xl shrink-0"
                />
                <div class="min-w-0">
                  <p class="text-xs text-gray-400 mb-1">Khóa học</p>
                  <p class="font-bold text-gray-900 leading-snug">
                    {{ selectedOrder.courseTitle }}
                  </p>
                  <p class="text-xs text-gray-500 mt-1">ID: #{{ selectedOrder.courseId }}</p>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <p class="text-xs text-gray-400">Người mua</p>
                  <p class="font-semibold text-gray-900">{{ selectedOrder.fullName || '—' }}</p>
                  <p class="text-xs text-gray-500">@{{ selectedOrder.username }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-400">Ngày đặt</p>
                  <p class="font-semibold text-gray-900">
                    {{ formatDate(selectedOrder.createdAt) }}
                  </p>
                </div>
                <div>
                  <p class="text-xs text-gray-400">Giá gốc</p>
                  <p class="font-semibold text-gray-900">{{ formatVND(selectedOrder.amount) }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-400">Giảm giá</p>
                  <p
                    :class="
                      Number(selectedOrder.discountAmount) > 0
                        ? 'font-semibold text-red-600'
                        : 'text-gray-400'
                    "
                  >
                    {{
                      Number(selectedOrder.discountAmount) > 0
                        ? '-' + formatVND(selectedOrder.discountAmount)
                        : '—'
                    }}
                  </p>
                </div>
              </div>

              <div
                class="bg-gradient-to-r from-green-50 to-emerald-50 rounded-2xl p-4 flex items-center justify-between border border-green-100"
              >
                <p class="text-base text-gray-600 font-semibold">Tổng thanh toán</p>
                <p class="text-3xl font-extrabold text-green-700">
                  {{ formatVND(selectedOrder.finalAmount) }}
                </p>
              </div>

              <div
                v-if="selectedOrder.expiredAt"
                class="flex items-center gap-2 text-xs text-gray-400"
              >
                <Clock class="w-4 h-4" />
                Hết hạn: {{ formatDate(selectedOrder.expiredAt) }}
              </div>
            </div>

            <div class="px-6 pb-6">
              <button
                @click="showDetail = false"
                class="w-full py-3 bg-gray-900 hover:bg-gray-800 text-white font-semibold rounded-2xl transition"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ShoppingCart, TrendingUp, CheckCircle, Clock, Search, Eye, X } from 'lucide-vue-next'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getAllOrders, type OrderResponse } from '@/api/orders'
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()

// ─── State ─────────────────────────────────────────────────────────────────
const orders = ref<OrderResponse[]>([])
const loading = ref(true)
const totalElements = ref(0)
const currentPage = ref(0)
const pageSize = ref(10)
const searchQuery = ref('')
const statusFilter = ref('')
const showDetail = ref(false)
const selectedOrder = ref<OrderResponse | null>(null)

// ─── Pagination computed ───────────────────────────────────────────────────
const totalPages = computed(() => Math.max(1, Math.ceil(totalElements.value / pageSize.value)))

const isLastPage = computed(() => (currentPage.value + 1) * pageSize.value >= totalElements.value)

/** Số thứ tự đơn hàng đầu tiên đang hiển thị */
const displayStart = computed(() =>
  totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1,
)

/** Số thứ tự đơn hàng cuối cùng đang hiển thị */
const displayEnd = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, totalElements.value),
)

/** Danh sách số trang hiển thị (±2 xung quanh trang hiện tại) */
const pageNumbers = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const delta = 2
  const range: number[] = []
  const start = Math.max(0, current - delta)
  const end = Math.min(total - 1, current + delta)
  for (let i = start; i <= end; i++) range.push(i)
  return range
})

// ─── Stats (tính từ toàn bộ trang hiện tại) ───────────────────────────────
const stats = computed(() => {
  const all = orders.value
  const completed = all.filter((o) => o.status === 'COMPLETED')
  const pending = all.filter((o) => o.status === 'PENDING')
  const revenue = completed.reduce((s, o) => s + Number(o.finalAmount), 0)

  return [
    {
      label: 'Tổng đơn hàng',
      value: totalElements.value,
      icon: ShoppingCart,
      bg: 'bg-blue-50',
      iconColor: 'text-blue-600',
    },
    {
      label: 'Doanh thu (trang)',
      value: formatVND(revenue),
      icon: TrendingUp,
      bg: 'bg-green-50',
      iconColor: 'text-green-600',
    },
    {
      label: 'Đã hoàn thành',
      value: completed.length,
      icon: CheckCircle,
      bg: 'bg-emerald-50',
      iconColor: 'text-emerald-600',
    },
    {
      label: 'Chờ thanh toán',
      value: pending.length,
      icon: Clock,
      bg: 'bg-amber-50',
      iconColor: 'text-amber-600',
    },
  ]
})

// ─── Filter (client-side trên trang hiện tại) ─────────────────────────────
const filteredOrders = computed(() => {
  let result = orders.value

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(
      (o) =>
        o.orderCode.toLowerCase().includes(q) ||
        o.username.toLowerCase().includes(q) ||
        (o.fullName || '').toLowerCase().includes(q) ||
        o.courseTitle.toLowerCase().includes(q),
    )
  }

  if (statusFilter.value) {
    result = result.filter((o) => o.status === statusFilter.value)
  }

  return result
})

// ─── API ───────────────────────────────────────────────────────────────────
const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await getAllOrders(currentPage.value, pageSize.value)
    orders.value = res.content
    totalElements.value = res.totalElements
  } catch (err: any) {
    toastStore.error(err?.response?.data?.message || 'Không thể tải danh sách đơn hàng')
  } finally {
    loading.value = false
  }
}

onMounted(fetchOrders)
watch(currentPage, fetchOrders)

const goToPage = (p: number) => {
  if (p < 0 || p >= totalPages.value) return
  currentPage.value = p
}

const onPageSizeChange = () => {
  currentPage.value = 0
  fetchOrders()
}

// ─── Detail ────────────────────────────────────────────────────────────────
const openDetail = (order: OrderResponse) => {
  selectedOrder.value = order
  showDetail.value = true
}

// ─── Helpers ───────────────────────────────────────────────────────────────
const formatVND = (value: number | string) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Number(value))

const formatDate = (date: string) =>
  new Date(date).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })

const getInitials = (name: string) =>
  name
    .split(' ')
    .map((w) => w[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

const STATUS_LABEL: Record<string, string> = {
  PENDING: 'Chờ thanh toán',
  COMPLETED: 'Đã hoàn thành',
  FAILED: 'Thất bại',
  CANCELLED: 'Đã hủy',
  REFUNDED: 'Hoàn tiền',
}
const STATUS_CLASS: Record<string, string> = {
  COMPLETED: 'bg-green-100 text-green-700',
  PENDING: 'bg-amber-100 text-amber-700',
  FAILED: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-500',
  REFUNDED: 'bg-blue-100 text-blue-700',
}
const STATUS_CLASS_LIGHT: Record<string, string> = {
  COMPLETED: 'bg-green-100 text-green-700',
  PENDING: 'bg-amber-100 text-amber-700',
  FAILED: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-700',
  REFUNDED: 'bg-blue-100 text-blue-700',
}

const getStatusLabel = (s: string) => STATUS_LABEL[s] ?? s
const getStatusClass = (s: string) => STATUS_CLASS[s] ?? 'bg-gray-100 text-gray-500'
const getStatusClassLight = (s: string) => STATUS_CLASS_LIGHT[s] ?? 'bg-gray-100 text-gray-700'
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.2s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* Custom Scrollbar for Webkit */
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9; /* Tailwind slate-100 */
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1; /* Tailwind slate-300 */
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8; /* Tailwind slate-400 */
}
</style>
