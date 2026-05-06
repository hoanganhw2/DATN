<template>
  <DashboardLayout>
    <div class="space-y-7">
      <!-- Filters & Search -->
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <div class="relative group flex-1 w-full">
          <SearchIcon
            class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-gray-400 group-focus-within:text-blue-500 transition-colors"
          />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo mã đơn hoặc tên khóa học..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
          />
        </div>
        <div class="flex bg-gray-100 rounded-xl p-1 gap-1 shrink-0">
          <button
            v-for="tab in filterTabs"
            :key="tab.value"
            @click="activeFilter = tab.value"
            :class="[
              'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
              activeFilter === tab.value
                ? 'bg-white text-blue-600 shadow-sm'
                : 'text-gray-500 hover:text-gray-700',
            ]"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- Orders Table -->
      <div v-if="loading" class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col items-center justify-center py-20">
        <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-500">Đang tải danh sách đơn hàng...</p>
      </div>

      <div v-else-if="filteredOrders.length > 0" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="overflow-x-auto custom-scrollbar">
          <table class="w-full text-sm whitespace-nowrap">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100">
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">#</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Mã đơn</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Khóa học</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Ngày đặt</th>
                <th class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider">Thanh toán</th>
                <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Trạng thái</th>
                <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="(order, idx) in filteredOrders" :key="order.id" class="hover:bg-blue-50/20 transition-colors">
                <td class="px-5 py-4 text-gray-400 text-xs">
                  {{ page * pageSize + idx + 1 }}
                </td>
                <td class="px-5 py-4">
                  <span class="text-xs font-mono font-medium text-blue-600 bg-blue-50 px-2 py-1 rounded-md">{{ order.orderCode }}</span>
                </td>
                <td class="px-5 py-4 min-w-[250px]">
                  <div class="flex items-center gap-3">
                    <div class="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden shrink-0 border border-gray-100">
                      <img v-if="order.courseThumbnail" :src="order.courseThumbnail" class="w-full h-full object-cover" />
                      <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                        <LayersIcon class="w-6 h-6" />
                      </div>
                    </div>
                    <p class="font-semibold text-gray-900 truncate max-w-[200px]" :title="order.courseTitle">{{ order.courseTitle }}</p>
                  </div>
                </td>
                <td class="px-5 py-4 text-gray-500 text-xs">
                  {{ formatDate(order.createdAt) }}
                </td>
                <td class="px-5 py-4 text-right">
                  <p v-if="order.discountAmount > 0" class="text-[10px] text-gray-400 line-through mb-0.5">{{ formatVND(order.amount) }}</p>
                  <p class="font-bold text-gray-900">{{ formatVND(order.finalAmount) }}</p>
                </td>
                <td class="px-5 py-4 text-center">
                  <span :class="getStatusClass(order.status)" class="px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider shadow-sm">
                    {{ getStatusLabel(order.status) }}
                  </span>
                </td>
                <td class="px-5 py-4 text-center">
                  <div class="flex items-center justify-center gap-2">
                    <router-link
                      v-if="order.status === 'COMPLETED'"
                      :to="`/courses/${order.courseId}`"
                      class="inline-flex items-center px-3 py-1.5 bg-blue-600 text-white rounded-lg text-xs font-semibold hover:bg-blue-700 transition"
                    >
                      Vào học ngay
                    </router-link>
                    <button
                      v-if="order.status === 'PENDING'"
                      @click="handleCancel(order)"
                      class="px-3 py-1.5 border border-gray-300 text-gray-600 bg-white rounded-lg text-xs font-semibold hover:bg-gray-50 transition"
                    >
                      Hủy đơn
                    </button>
                    <button
                      v-if="order.status === 'PENDING'"
                      @click="repay(order)"
                      :disabled="repayingId === order.id"
                      class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-orange-500 text-white rounded-lg text-xs font-semibold hover:bg-orange-600 disabled:opacity-60 disabled:cursor-not-allowed transition"
                    >
                      <svg v-if="repayingId === order.id" class="animate-spin h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                      </svg>
                      {{ repayingId === order.id ? 'Đang xử lý...' : 'Thanh toán' }}
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50">
          <p class="text-sm text-gray-500">
            Hiển thị <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
            trong số <span class="font-semibold text-gray-700">{{ totalElements }}</span> đơn hàng
          </p>
          <div class="flex items-center gap-1.5">
            <button :disabled="page === 0" @click="page = 0" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">«</button>
            <button :disabled="page === 0" @click="page--" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">‹</button>
            <button
              v-for="p in pageNumbers"
              :key="p"
              @click="page = p"
              :class="['w-10 h-10 rounded-xl text-sm font-semibold transition border', page === p ? 'bg-blue-600 text-white border-blue-600 shadow' : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300']"
            >{{ p + 1 }}</button>
            <button :disabled="isLastPage" @click="page++" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">›</button>
            <button :disabled="isLastPage" @click="page = totalPages - 1" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">»</button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else
        class="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-gray-100"
      >
        <div
          class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6"
        >
          <ReceiptIcon class="w-10 h-10 text-gray-300" />
        </div>
        <h4 class="text-xl font-bold text-gray-900 mb-2">Bạn chưa có đơn hàng nào</h4>
        <p class="text-gray-500 max-w-sm mx-auto mb-8">
          Khám phá các khóa học hấp dẫn để bắt đầu hành trình chinh phục tiếng Anh của bạn ngay hôm
          nay!
        </p>
        <router-link
          to="/courses"
          class="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition shadow-lg shadow-blue-200"
        >
          Khám phá khóa học
        </router-link>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import SearchIcon from '@/components/dashboard/icons/SearchIcon.vue'
import ReceiptIcon from '@/components/dashboard/icons/ReceiptIcon.vue'
import LayersIcon from '@/components/dashboard/icons/LayersIcon.vue'
import ChevronRightIcon from '@/components/dashboard/icons/ChevronRightIcon.vue'
import { Calendar, ChevronLeft } from 'lucide-vue-next'
import { getMyOrders, cancelOrder, repayOrder, type OrderResponse } from '@/api/orders'
import { useToastStore } from '@/stores/toast'
import { useDialog } from '@/composables/useDialog'

const CalendarIcon = Calendar
const ChevronLeftIcon = ChevronLeft
const toastStore = useToastStore()
const dialog = useDialog()

const orders = ref<OrderResponse[]>([])
const loading = ref(true)
const totalElements = ref(0)
const page = ref(0)
const pageSize = 10
const totalPages = computed(() => Math.max(1, Math.ceil(totalElements.value / pageSize)))
const isLastPage = computed(() => (page.value + 1) * pageSize >= totalElements.value)
const displayStart = computed(() => totalElements.value === 0 ? 0 : page.value * pageSize + 1)
const displayEnd = computed(() => Math.min((page.value + 1) * pageSize, totalElements.value))

const pageNumbers = computed(() => {
  const total = totalPages.value, current = page.value, delta = 2
  const range: number[] = []
  for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++) range.push(i)
  return range
})
const searchQuery = ref('')
const activeFilter = ref('ALL')
const repayingId = ref<number | null>(null) // track đơn hàng đang được repay

const filterTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'Chờ thanh toán', value: 'PENDING' },
  { label: 'Đã thanh toán', value: 'COMPLETED' },
  { label: 'Thất bại/Hủy', value: 'FAILED' }, // Combine filters temporarily
  { label: 'Đã hủy', value: 'CANCELLED' },
]

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await getMyOrders(page.value, pageSize)
    orders.value = res.content
    totalElements.value = res.totalElements
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchOrders)
watch(page, fetchOrders)

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING: 'Chờ thanh toán',
    COMPLETED: 'Đã hoàn thành',
    FAILED: 'Thất bại',
    REFUNDED: 'Hoàn tiền',
    CANCELLED: 'Đã hủy',
  }
  return map[status] || status
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'COMPLETED':
      return 'bg-green-500/90 text-white'
    case 'PENDING':
      return 'bg-amber-400 text-amber-900 border border-amber-200'
    case 'FAILED':
      return 'bg-red-500/90 text-white'
    case 'REFUNDED':
      return 'bg-blue-500/90 text-white'
    case 'CANCELLED':
      return 'bg-gray-500/90 text-white'
    default:
      return 'bg-gray-500/90 text-white'
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatVND = (value: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(value)
}

const repay = async (order: OrderResponse) => {
  try {
    repayingId.value = order.id
    const paymentUrl = await repayOrder(order.id)
    // Redirect đến cổng thanh toán VNPay trong tab hiện tại
    window.location.href = paymentUrl
  } catch (error: any) {
    const msg = error?.response?.data?.message || 'Không thể tạo lại liên kết thanh toán'
    toastStore.error(msg)
  } finally {
    repayingId.value = null
  }
}

const handleCancel = async (order: OrderResponse) => {
  const isConfirmed = await dialog.confirm(
    'Xác nhận hủy',
    `Bạn có chắc muốn hủy đơn hàng ${order.orderCode}?`,
    'warning',
  )
  if (!isConfirmed) return

  try {
    await cancelOrder(order.id)
    toastStore.success('Đã hủy đơn hàng thành công')
    await fetchOrders() // Refresh
  } catch (error: any) {
    const msg = error?.response?.data?.message || 'Có lỗi xảy ra khi hủy đơn'
    toastStore.error(msg)
  }
}

const filteredOrders = computed(() => {
  let result = orders.value

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(
      (o) => o.courseTitle.toLowerCase().includes(q) || o.orderCode.toLowerCase().includes(q),
    )
  }

  if (activeFilter.value !== 'ALL') {
    result = result.filter((o) => o.status === activeFilter.value)
  }

  return result
})
</script>
