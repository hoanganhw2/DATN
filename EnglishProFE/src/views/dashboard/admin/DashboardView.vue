<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5">
        <div
          v-for="stat in statCards"
          :key="stat.label"
          class="bg-white rounded-2xl border border-gray-100 p-5 flex items-center gap-4 shadow-sm hover:shadow-md transition-shadow"
        >
          <div :class="`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 ${stat.bg}`">
            <component :is="stat.icon" :class="`w-6 h-6 ${stat.iconColor}`" />
          </div>
          <div>
            <p class="text-xs text-gray-500 font-medium">{{ stat.label }}</p>
            <p v-if="statsLoading" class="text-xl font-bold text-gray-200 animate-pulse mt-0.5">
              —
            </p>
            <p v-else class="text-xl font-bold text-gray-900 mt-0.5">{{ stat.value }}</p>
          </div>
        </div>
      </div>

      <!-- Two-column: Recent Orders + User Distribution -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Recent Orders (2/3 width) -->
        <div
          class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
        >
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-50">
            <h3 class="font-bold text-gray-900">Đơn hàng gần đây</h3>
            <router-link
              to="/dashboard/admin/orders"
              class="text-sm font-semibold text-blue-600 hover:text-blue-700"
            >
              Xem tất cả →
            </router-link>
          </div>

          <div v-if="ordersLoading" class="flex justify-center py-12">
            <div
              class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
          </div>

          <div v-else class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th
                    class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Mã đơn
                  </th>
                  <th
                    class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Người mua
                  </th>
                  <th
                    class="px-5 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Thực thu
                  </th>
                  <th
                    class="px-5 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Trạng thái
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr v-if="recentOrders.length === 0">
                  <td colspan="4" class="py-8 text-center text-gray-400 text-sm">
                    Chưa có đơn hàng nào
                  </td>
                </tr>
                <tr
                  v-for="o in recentOrders"
                  :key="o.id"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-3.5">
                    <span
                      class="font-mono text-xs font-bold text-blue-700 bg-blue-50 px-2 py-1 rounded"
                      >{{ o.orderCode }}</span
                    >
                  </td>
                  <td class="px-5 py-3.5">
                    <p class="font-medium text-gray-900 truncate max-w-[140px]">
                      {{ o.fullName || o.username }}
                    </p>
                    <p class="text-xs text-gray-400">@{{ o.username }}</p>
                  </td>
                  <td class="px-5 py-3.5 text-right font-bold text-gray-900">
                    {{ formatVND(o.finalAmount) }}
                  </td>
                  <td class="px-5 py-3.5 text-center">
                    <span
                      :class="getStatusBadge(o.status)"
                      class="px-2.5 py-1 rounded-full text-xs font-bold"
                    >
                      {{ getStatusLabel(o.status) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- User stats (1/3 width) -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <h3 class="font-bold text-gray-900 mb-6">Phân bố người dùng</h3>

          <div v-if="statsLoading" class="space-y-4">
            <div v-for="i in 3" :key="i" class="h-16 bg-gray-100 rounded-xl animate-pulse"></div>
          </div>

          <div v-else class="space-y-4">
            <div
              v-for="item in userDistribution"
              :key="item.label"
              class="flex items-center justify-between p-4 rounded-xl border border-gray-50 bg-gray-50"
            >
              <div class="flex items-center gap-3">
                <div :class="`w-3 h-3 rounded-full ${item.dot}`"></div>
                <span class="text-sm font-medium text-gray-700">{{ item.label }}</span>
              </div>
              <span :class="`text-lg font-black ${item.color}`">{{ item.count }}</span>
            </div>

            <!-- Bar chart visual -->
            <div class="mt-4 space-y-2">
              <div v-for="item in userDistribution" :key="item.label + '_bar'">
                <div class="flex justify-between text-xs text-gray-400 mb-1">
                  <span>{{ item.label }}</span>
                  <span
                    >{{
                      item.count > 0 ? Math.round((item.count / overallStats.totalUsers) * 100) : 0
                    }}%</span
                  >
                </div>
                <div class="h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    :class="`h-full rounded-full ${item.bar}`"
                    :style="{
                      width:
                        overallStats.totalUsers > 0
                          ? `${(item.count / overallStats.totalUsers) * 100}%`
                          : '0%',
                    }"
                    class="transition-all duration-700"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Users -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-50">
          <h3 class="font-bold text-gray-900">Người dùng mới đăng ký</h3>
          <router-link
            to="/dashboard/admin/users"
            class="text-sm font-semibold text-blue-600 hover:text-blue-700"
          >
            Xem tất cả →
          </router-link>
        </div>

        <div v-if="usersLoading" class="flex justify-center py-10">
          <div
            class="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
          ></div>
        </div>

        <div v-else class="overflow-x-auto custom-scrollbar">
          <table class="w-full text-sm whitespace-nowrap">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100">
                <th
                  class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Người dùng
                </th>
                <th
                  class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Email
                </th>
                <th
                  class="px-5 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Vai trò
                </th>
                <th
                  class="px-5 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Trạng thái
                </th>
                <th
                  class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Ngày tạo
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr
                v-for="u in recentUsers"
                :key="u.id"
                class="hover:bg-blue-50/20 transition-colors"
              >
                <td class="px-5 py-3.5">
                  <div class="flex items-center gap-3">
                    <div
                      class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold shrink-0"
                    >
                      {{ getInitials(u.fullName || u.username) }}
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ u.fullName || '—' }}</p>
                      <p class="text-xs text-gray-400">@{{ u.username }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-5 py-3.5 text-gray-600">{{ u.email }}</td>
                <td class="px-5 py-3.5 text-center">
                  <span
                    :class="getRoleBadge(u.role)"
                    class="px-2.5 py-1 rounded-lg text-xs font-bold"
                  >
                    {{ getRoleLabel(u.role) }}
                  </span>
                </td>
                <td class="px-5 py-3.5 text-center">
                  <span
                    :class="getStatusBadge2(u.status)"
                    class="px-2.5 py-1 rounded-full text-xs font-bold"
                  >
                    {{ getStatusLabel2(u.status) }}
                  </span>
                </td>
                <td class="px-5 py-3.5 text-gray-500 text-xs">{{ formatDate(u.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ShoppingCart, TrendingUp, Users, BookOpen } from 'lucide-vue-next'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getAllOrders, type OrderResponse } from '@/api/orders'
import { getAllUsers, getUserStats, type AdminUserResponse } from '@/api/users'

// ── State ──────────────────────────────────────────────────────────────────────
const statsLoading = ref(true)
const ordersLoading = ref(true)
const usersLoading = ref(true)

const recentOrders = ref<OrderResponse[]>([])
const recentUsers = ref<AdminUserResponse[]>([])
const overallStats = ref({
  totalUsers: 0,
  totalOrders: 0,
  totalRevenue: 0,
  students: 0,
  teachers: 0,
  admins: 0,
})

// ── Stat cards ─────────────────────────────────────────────────────────────────
const statCards = computed(() => [
  {
    label: 'Tổng người dùng',
    value: overallStats.value.totalUsers,
    icon: Users,
    bg: 'bg-blue-50',
    iconColor: 'text-blue-600',
  },
  {
    label: 'Tổng đơn hàng',
    value: overallStats.value.totalOrders,
    icon: ShoppingCart,
    bg: 'bg-orange-50',
    iconColor: 'text-orange-600',
  },
  {
    label: 'Doanh thu',
    value: formatVND(overallStats.value.totalRevenue),
    icon: TrendingUp,
    bg: 'bg-green-50',
    iconColor: 'text-green-600',
  },
  {
    label: 'Giảng viên',
    value: overallStats.value.teachers,
    icon: BookOpen,
    bg: 'bg-blue-50',
    iconColor: 'text-blue-600',
  },
])

const userDistribution = computed(() => [
  {
    label: 'Học viên',
    count: overallStats.value.students,
    color: 'text-blue-600',
    dot: 'bg-blue-500',
    bar: 'bg-blue-500',
  },
  {
    label: 'Giảng viên',
    count: overallStats.value.teachers,
    color: 'text-blue-600',
    dot: 'bg-blue-500',
    bar: 'bg-blue-500',
  },
  {
    label: 'Quản trị viên',
    count: overallStats.value.admins,
    color: 'text-red-600',
    dot: 'bg-red-500',
    bar: 'bg-red-500',
  },
])

// ── Fetch ──────────────────────────────────────────────────────────────────────
const fetchDashboard = async () => {
  // Orders
  ordersLoading.value = true
  try {
    const res = await getAllOrders(0, 5)
    recentOrders.value = res.content
    const completedRevenue = res.content
      .filter((o) => o.status === 'COMPLETED')
      .reduce((s, o) => s + Number(o.finalAmount), 0)
    overallStats.value.totalOrders = res.totalElements
    overallStats.value.totalRevenue = completedRevenue
  } catch (e) {
    console.error('Lỗi tải đơn hàng:', e)
  } finally {
    ordersLoading.value = false
  }

  // Users
  usersLoading.value = true
  statsLoading.value = true
  try {
    const [usersRes, statsRes] = await Promise.all([getAllUsers(0, 10), getUserStats()])
    recentUsers.value = usersRes.content

    // Set actual real stats from DB
    overallStats.value.totalUsers = statsRes.totalUsers
    overallStats.value.students = statsRes.students
    overallStats.value.teachers = statsRes.teachers
    overallStats.value.admins = statsRes.admins
  } catch (e) {
    console.error('Lỗi tải users:', e)
  } finally {
    usersLoading.value = false
    statsLoading.value = false
  }
}

onMounted(fetchDashboard)

// ── Helpers ────────────────────────────────────────────────────────────────────
const formatVND = (v: number | string) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Number(v))

const formatDate = (d?: string) =>
  d
    ? new Date(d).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : '—'

const getInitials = (name: string) =>
  name
    .split(' ')
    .map((w) => w[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

const STATUS_LABEL: Record<string, string> = {
  PENDING: 'Chờ',
  COMPLETED: 'Hoàn thành',
  FAILED: 'Thất bại',
  CANCELLED: 'Đã hủy',
}
const STATUS_BADGE: Record<string, string> = {
  COMPLETED: 'bg-green-100 text-green-700',
  PENDING: 'bg-amber-100 text-amber-700',
  FAILED: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-500',
}
const getStatusLabel = (s: string) => STATUS_LABEL[s] ?? s
const getStatusBadge = (s: string) => STATUS_BADGE[s] ?? 'bg-gray-100 text-gray-500'

const getRoleLabel = (r: string) =>
  ({ ADMIN: 'Admin', TEACHER: 'Giảng viên', STUDENT: 'Học viên' })[r] ?? r
const getRoleBadge = (r: string) =>
  ({
    ADMIN: 'bg-red-100 text-red-700',
    TEACHER: 'bg-blue-100 text-blue-700',
    STUDENT: 'bg-blue-100 text-blue-700',
  })[r] ?? 'bg-gray-100 text-gray-600'

const getStatusLabel2 = (s: string) =>
  ({ ACTIVE: 'Hoạt động', INACTIVE: 'Vô hiệu', BANNED: 'Bị khóa' })[s] ?? s
const getStatusBadge2 = (s: string) =>
  ({
    ACTIVE: 'bg-green-100 text-green-700',
    INACTIVE: 'bg-gray-100 text-gray-500',
    BANNED: 'bg-red-100 text-red-700',
  })[s] ?? 'bg-gray-100 text-gray-500'
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
