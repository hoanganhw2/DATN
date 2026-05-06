<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Action Bar -->
      <div class="flex flex-col sm:flex-row items-center justify-end gap-3 w-full">
        <select
          v-model.number="selectedYear"
          @change="loadStats"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 bg-white font-semibold focus:outline-none focus:ring-2 focus:ring-blue-500/20"
        >
          <option v-for="y in yearOptions" :key="y" :value="y">Năm {{ y }}</option>
        </select>
        <button
          @click="loadStats"
          :disabled="loading"
          class="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white text-sm font-bold rounded-xl transition"
        >
          {{ loading ? 'Đang tải...' : 'Làm mới' }}
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="space-y-6">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <div v-for="i in 3" :key="i" class="h-32 bg-gray-100 rounded-2xl animate-pulse"></div>
        </div>
        <div class="h-80 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <template v-else-if="stats">
        <!-- Revenue Split Banner -->
        <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl p-6 text-white">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
            <div class="text-center">
              <p class="text-blue-200 text-xs font-semibold uppercase tracking-wider mb-1">
                Tổng doanh thu khóa học
              </p>
              <p class="text-3xl font-black">{{ formatVND(stats.overall.totalRevenue) }}</p>
              <p class="text-blue-200 text-sm mt-1">
                {{ stats.overall.completedOrders }} đơn hoàn thành
              </p>
            </div>
            <div class="text-center border-l border-r border-white/20 px-4">
              <p class="text-yellow-200 text-xs font-semibold uppercase tracking-wider mb-1">
                🎓 Bạn nhận (70%)
              </p>
              <p class="text-3xl font-black text-yellow-200">
                {{ formatVND(stats.overall.teacherRevenue) }}
              </p>
              <div class="mt-2 bg-white/10 rounded-full h-2.5">
                <div
                  class="bg-yellow-300 h-2.5 rounded-full transition-all"
                  style="width: 70%"
                ></div>
              </div>
            </div>
            <div class="text-center">
              <p class="text-blue-200 text-xs font-semibold uppercase tracking-wider mb-1">
                🏛 Phần admin (30%)
              </p>
              <p class="text-3xl font-black text-blue-200">
                {{ formatVND(stats.overall.adminRevenue) }}
              </p>
              <div class="mt-2 bg-white/10 rounded-full h-2.5">
                <div class="bg-blue-300 h-2.5 rounded-full" style="width: 30%"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- KPI Cards -->
        <div class="grid grid-cols-2 xl:grid-cols-4 gap-4">
          <div
            v-for="card in kpiCards"
            :key="card.label"
            class="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm hover:shadow-md transition-shadow"
          >
            <div :class="`w-10 h-10 rounded-xl flex items-center justify-center ${card.bg} mb-3`">
              <span class="text-lg">{{ card.emoji }}</span>
            </div>
            <p class="text-xs text-gray-500 font-medium uppercase tracking-wider">
              {{ card.label }}
            </p>
            <p class="text-xl font-black mt-1" :class="card.color ?? 'text-gray-900'">
              {{ card.value }}
            </p>
          </div>
        </div>

        <!-- Charts -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Monthly Line Chart -->
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <div class="flex items-center justify-between mb-6">
              <div>
                <h3 class="font-black text-gray-900">Thu nhập theo tháng</h3>
                <p class="text-xs text-gray-400 mt-0.5">Năm {{ selectedYear }}</p>
              </div>
              <div class="flex items-center gap-3 text-xs">
                <span class="flex items-center gap-1.5"
                  ><span class="w-3 h-3 rounded-full bg-blue-500 inline-block"></span> Tổng</span
                >
                <span class="flex items-center gap-1.5"
                  ><span class="w-3 h-3 rounded-full bg-yellow-400 inline-block"></span> Bạn
                  nhận</span
                >
              </div>
            </div>
            <div class="h-64">
              <Line :data="lineChartData" :options="lineChartOptions" />
            </div>
          </div>

          <!-- Top Courses Bar (top 10 only) -->
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <h3 class="font-black text-gray-900 mb-1">Top 10 khóa học của tôi</h3>
            <p class="text-xs text-gray-400 mb-6">Theo thu nhập bạn nhận (70%)</p>
            <div class="h-64">
              <Bar :data="barChartData" :options="barChartOptions" />
            </div>
          </div>
        </div>

        <!-- Course DataTable -->
        <CourseRevenueTable
          :courses="stats.topCourses"
          title="Chi tiết từng khóa học"
          subtitle="Phần bạn nhận: 70% doanh thu mỗi khóa học"
          theme="teacher"
        />

        <!-- Monthly Table -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-50">
            <h3 class="font-black text-gray-900">Thu nhập theo tháng — {{ selectedYear }}</h3>
          </div>
          <div class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th
                    class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Tháng
                  </th>
                  <th
                    class="px-5 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Số đơn
                  </th>
                  <th
                    class="px-5 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider"
                  >
                    Tổng
                  </th>
                  <th
                    class="px-5 py-3 text-right text-xs font-semibold text-yellow-600 uppercase tracking-wider"
                  >
                    Bạn nhận (70%)
                  </th>
                  <th
                    class="px-5 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-52"
                  >
                    Tỉ lệ
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr
                  v-for="row in monthlyRows"
                  :key="row.month"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-3.5 font-semibold text-gray-900">
                    Tháng {{ String(row.month).padStart(2, '0') }}
                  </td>
                  <td class="px-5 py-3.5 text-center">
                    <span
                      v-if="row.orderCount > 0"
                      class="bg-blue-100 text-blue-700 px-2.5 py-1 rounded-lg text-xs font-bold"
                      >{{ row.orderCount }}</span
                    >
                    <span v-else class="text-gray-300">—</span>
                  </td>
                  <td class="px-5 py-3.5 text-right text-xs font-medium text-gray-600">
                    {{ row.revenue > 0 ? formatVND(row.revenue) : '—' }}
                  </td>
                  <td
                    class="px-5 py-3.5 text-right font-bold"
                    :class="row.teacherRevenue > 0 ? 'text-yellow-700' : 'text-gray-300'"
                  >
                    {{ row.teacherRevenue > 0 ? formatVND(row.teacherRevenue) : '—' }}
                  </td>
                  <td class="px-5 py-3.5">
                    <div class="flex items-center gap-2">
                      <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                        <div
                          class="h-full bg-blue-500 rounded-full transition-all duration-700"
                          :style="{ width: `${row.pct}%` }"
                        ></div>
                      </div>
                      <span class="text-xs text-gray-400 w-10 text-right">{{ row.pct }}%</span>
                    </div>
                  </td>
                </tr>
              </tbody>
              <tfoot>
                <tr class="bg-blue-50 border-t-2 border-blue-100">
                  <td class="px-5 py-3.5 font-black text-gray-900">Tổng cộng</td>
                  <td class="px-5 py-3.5 text-center font-black text-blue-700">
                    {{ stats.overall.completedOrders }}
                  </td>
                  <td class="px-5 py-3.5 text-right font-black text-gray-700">
                    {{ formatVND(stats.overall.totalRevenue) }}
                  </td>
                  <td class="px-5 py-3.5 text-right font-black text-yellow-700">
                    {{ formatVND(stats.overall.teacherRevenue) }}
                  </td>
                  <td></td>
                </tr>
              </tfoot>
            </table>
          </div>
        </div>
      </template>

      <div
        v-else-if="error"
        class="flex flex-col items-center justify-center py-24 gap-4 text-gray-400"
      >
        <span class="text-5xl">😕</span>
        <p class="font-semibold text-gray-500">{{ error }}</p>
        <button
          @click="loadStats"
          class="px-5 py-2.5 bg-blue-600 text-white text-sm font-bold rounded-xl hover:bg-blue-700 transition"
        >
          Thử lại
        </button>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js'
import { Line, Bar } from 'vue-chartjs'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import CourseRevenueTable from '@/components/dashboard/CourseRevenueTable.vue'
import { getTeacherRevenueStats, type TeacherRevenueStats } from '@/api/stats'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler,
)

const currentYear = new Date().getFullYear()
const selectedYear = ref(currentYear)
const loading = ref(false)
const error = ref('')
const stats = ref<TeacherRevenueStats | null>(null)
const yearOptions = Array.from({ length: 5 }, (_, i) => currentYear - i)

const loadStats = async () => {
  loading.value = true
  error.value = ''
  try {
    stats.value = await getTeacherRevenueStats(selectedYear.value)
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Không thể tải dữ liệu'
  } finally {
    loading.value = false
  }
}
onMounted(loadStats)

const kpiCards = computed(() => {
  if (!stats.value) return []
  const { overall } = stats.value
  return [
    {
      label: 'Tổng doanh thu',
      value: formatVND(overall.totalRevenue),
      emoji: '💰',
      bg: 'bg-gray-50',
      color: 'text-gray-900',
    },
    {
      label: 'Bạn nhận (70%)',
      value: formatVND(overall.teacherRevenue),
      emoji: '🎓',
      bg: 'bg-yellow-50',
      color: 'text-yellow-700',
    },
    {
      label: 'Đơn hoàn thành',
      value: overall.completedOrders.toLocaleString('vi-VN'),
      emoji: '✅',
      bg: 'bg-green-50',
      color: 'text-green-700',
    },
    {
      label: 'Chờ thanh toán',
      value: overall.pendingOrders.toLocaleString('vi-VN'),
      emoji: '⏳',
      bg: 'bg-amber-50',
      color: 'text-amber-700',
    },
  ]
})

const MONTH_LABELS = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12']
const monthlyMap = computed(() => {
  const m: Record<number, any> = {}
  stats.value?.monthly.forEach((r) => (m[r.month] = r))
  return m
})
const allMonths = computed(() =>
  Array.from(
    { length: 12 },
    (_, i) =>
      monthlyMap.value[i + 1] ?? {
        month: i + 1,
        revenue: 0,
        orderCount: 0,
        teacherRevenue: 0,
        adminRevenue: 0,
      },
  ),
)
const maxRevenue = computed(() => Math.max(...allMonths.value.map((m) => m.revenue), 1))
const monthlyRows = computed(() =>
  allMonths.value.map((m) => ({
    ...m,
    pct: Math.round((m.revenue / maxRevenue.value) * 100),
  })),
)

const lineChartData = computed(() => ({
  labels: MONTH_LABELS,
  datasets: [
    {
      label: 'Tổng doanh thu',
      data: allMonths.value.map((m) => m.revenue),
      borderColor: '#a855f7',
      backgroundColor: 'rgba(168,85,247,0.08)',
      fill: true,
      tension: 0.4,
      pointRadius: 3,
      pointBackgroundColor: '#a855f7',
    },
    {
      label: 'Bạn nhận (70%)',
      data: allMonths.value.map((m) => m.teacherRevenue),
      borderColor: '#eab308',
      backgroundColor: 'transparent',
      tension: 0.4,
      pointRadius: 3,
      pointBackgroundColor: '#eab308',
      borderDash: [4, 4],
    },
  ],
}))
const lineChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: { callbacks: { label: (ctx: any) => ` ${ctx.dataset.label}: ${formatVND(ctx.raw)}` } },
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(0,0,0,0.04)' },
      ticks: {
        callback: (v: string | number) => {
          const n = Number(v)
          return n >= 1_000_000 ? `${(n / 1_000_000).toFixed(0)}tr` : v
        },
      },
    },
    x: { grid: { display: false } },
  },
}

const BAR_COLORS = [
  '#a855f7',
  '#6366f1',
  '#8b5cf6',
  '#ec4899',
  '#f59e0b',
  '#10b981',
  '#14b8a6',
  '#f97316',
  '#ef4444',
  '#84cc16',
]
// Limit to top 10 for bar chart readability
const barChartData = computed(() => ({
  labels: (stats.value?.topCourses ?? [])
    .slice(0, 10)
    .map((c) => (c.courseTitle.length > 18 ? c.courseTitle.slice(0, 18) + '…' : c.courseTitle)),
  datasets: [
    {
      label: 'Bạn nhận (70%)',
      data: (stats.value?.topCourses ?? []).slice(0, 10).map((c) => c.teacherRevenue),
      backgroundColor: BAR_COLORS,
      borderRadius: 8,
      borderSkipped: false,
    },
  ],
}))
const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y' as const,
  plugins: {
    legend: { display: false },
    tooltip: { callbacks: { label: (ctx: any) => ` ${formatVND(ctx.raw)}` } },
  },
  scales: {
    x: {
      beginAtZero: true,
      grid: { color: 'rgba(0,0,0,0.04)' },
      ticks: {
        callback: (v: string | number) => {
          const n = Number(v)
          return n >= 1_000_000 ? `${(n / 1_000_000).toFixed(0)}tr` : v
        },
      },
    },
    y: { grid: { display: false } },
  },
}

const maxCourseRevenue = computed(() =>
  Math.max(...(stats.value?.topCourses ?? []).map((c) => Number(c.revenue)), 1),
)
const coursePct = (rev: number) => Math.round((Number(rev) / maxCourseRevenue.value) * 100)
const formatVND = (v: number | string) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Number(v))
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
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
