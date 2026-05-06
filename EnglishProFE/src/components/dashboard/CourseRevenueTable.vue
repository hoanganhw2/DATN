<template>
  <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
    <!-- Header with search + page size -->
    <div
      class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 px-6 py-4 border-b border-gray-50"
    >
      <div>
        <h3 class="font-black text-gray-900">{{ title }}</h3>
        <p v-if="subtitle" class="text-xs text-gray-400 mt-0.5">{{ subtitle }}</p>
      </div>
      <div class="flex items-center gap-3 w-full sm:w-auto">
        <!-- Search -->
        <div class="relative flex-1 sm:w-64">
          <svg
            class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm tên khóa học..."
            class="w-full pl-9 pr-4 py-2 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
          />
        </div>
        <!-- Page size -->
        <select
          v-model.number="pageSize"
          @change="currentPage = 0"
          class="py-2 px-3 text-sm rounded-xl border border-gray-200 focus:outline-none bg-white"
        >
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
      </div>
    </div>

    <!-- Table -->
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
              class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider cursor-pointer hover:text-blue-600 select-none"
              @click="toggleSort('courseTitle')"
            >
              Khóa học {{ sortIcon('courseTitle') }}
            </th>
            <th
              class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider cursor-pointer hover:text-blue-600 select-none"
              @click="toggleSort('orderCount')"
            >
              Đơn hàng {{ sortIcon('orderCount') }}
            </th>
            <th
              class="px-5 py-3.5 text-right font-semibold text-gray-600 text-xs uppercase tracking-wider cursor-pointer hover:text-blue-600 select-none"
              @click="toggleSort('revenue')"
            >
              Tổng doanh thu {{ sortIcon('revenue') }}
            </th>
            <th
              class="px-5 py-3.5 text-right font-semibold text-xs uppercase tracking-wider cursor-pointer hover:text-blue-600 select-none"
              :class="teacherColor + ' font-semibold'"
              @click="toggleSort('teacherRevenue')"
            >
              GV nhận (70%) {{ sortIcon('teacherRevenue') }}
            </th>
            <th
              class="px-5 py-3.5 text-right font-semibold text-xs uppercase tracking-wider cursor-pointer hover:text-blue-600 select-none"
              :class="adminColor + ' font-semibold'"
              @click="toggleSort('adminRevenue')"
            >
              Admin (30%) {{ sortIcon('adminRevenue') }}
            </th>
            <th
              class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider w-44"
            >
              Tỉ trọng
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="paginatedRows.length === 0">
            <td colspan="7" class="py-14 text-center">
              <div class="flex flex-col items-center gap-2 text-gray-400">
                <svg
                  class="w-10 h-10 text-gray-200"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="1.5"
                    d="M12 6.042A8.967 8.967 0 0 0 6 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 0 1 6 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 0 1 6-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0 0 18 18a8.967 8.967 0 0 0-6 2.292m0-14.25v14.25"
                  />
                </svg>
                <p class="font-medium text-sm">Không tìm thấy khóa học nào</p>
              </div>
            </td>
          </tr>
          <tr
            v-for="(course, idx) in paginatedRows"
            :key="course.courseId"
            class="hover:bg-blue-50/20 transition-colors"
          >
            <td class="px-5 py-3.5 text-gray-400 text-xs">
              {{ currentPage * pageSize + idx + 1 }}
            </td>
            <td class="px-5 py-3.5">
              <p
                class="font-semibold text-gray-900 truncate max-w-[220px]"
                :title="course.courseTitle"
              >
                {{ course.courseTitle }}
              </p>
            </td>
            <td class="px-5 py-3.5 text-center">
              <span class="bg-blue-100 text-blue-700 px-2.5 py-1 rounded-lg text-xs font-bold">
                {{ course.orderCount }}
              </span>
            </td>
            <td class="px-5 py-3.5 text-right font-semibold text-gray-700 text-xs">
              {{ fmt(course.revenue) }}
            </td>
            <td class="px-5 py-3.5 text-right font-bold text-xs" :class="teacherValueColor">
              {{ fmt(course.teacherRevenue) }}
            </td>
            <td class="px-5 py-3.5 text-right font-bold text-xs" :class="adminValueColor">
              {{ fmt(course.adminRevenue) }}
            </td>
            <td class="px-5 py-3.5 pr-8">
              <div class="flex items-center gap-2">
                <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    class="h-full rounded-full transition-all duration-700"
                    :class="barColor"
                    :style="{ width: `${pct(course.revenue)}%` }"
                  ></div>
                </div>
                <span class="text-xs text-gray-400 w-9 text-right">{{ pct(course.revenue) }}%</span>
              </div>
            </td>
          </tr>
        </tbody>
        <!-- Footer total -->
        <tfoot v-if="filteredRows.length > 0">
          <tr :class="footerBg + ' border-t-2 ' + footerBorder">
            <td class="px-5 py-3.5 font-black text-gray-900" colspan="2">
              Tổng ({{ filteredRows.length }} khóa học)
            </td>
            <td class="px-5 py-3.5 text-center font-black text-blue-700">{{ totalOrders }}</td>
            <td class="px-5 py-3.5 text-right font-black text-gray-900 text-xs">
              {{ fmt(totalRevenue) }}
            </td>
            <td class="px-5 py-3.5 text-right font-black text-xs" :class="teacherValueColor">
              {{ fmt(totalTeacher) }}
            </td>
            <td class="px-5 py-3.5 text-right font-black text-xs" :class="adminValueColor">
              {{ fmt(totalAdmin) }}
            </td>
            <td></td>
          </tr>
        </tfoot>
      </table>
    </div>

    <!-- Pagination -->
    <div
      class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50"
    >
      <p class="text-sm text-gray-500">
        Hiển thị
        <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span> trong
        <span class="font-semibold text-gray-700">{{ filteredRows.length }}</span> khóa học
      </p>
      <div class="flex items-center gap-1.5">
        <button
          :disabled="currentPage === 0"
          @click="currentPage = 0"
          class="w-9 h-9 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-sm"
        >
          «
        </button>
        <button
          :disabled="currentPage === 0"
          @click="currentPage--"
          class="w-9 h-9 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-sm"
        >
          ‹
        </button>
        <button
          v-for="p in pageNumbers"
          :key="p"
          @click="currentPage = p"
          :class="[
            'w-9 h-9 rounded-xl text-sm font-semibold transition border',
            p === currentPage
              ? activePageClass
              : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300',
          ]"
        >
          {{ p + 1 }}
        </button>
        <button
          :disabled="isLastPage"
          @click="currentPage++"
          class="w-9 h-9 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-sm"
        >
          ›
        </button>
        <button
          :disabled="isLastPage"
          @click="currentPage = totalPages - 1"
          class="w-9 h-9 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold text-sm"
        >
          »
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { CourseRevenue } from '@/api/stats'

const props = withDefaults(
  defineProps<{
    courses: CourseRevenue[]
    title?: string
    subtitle?: string
    /** 'admin' = blue theme | 'teacher' = blue/yellow theme */
    theme?: 'admin' | 'teacher'
  }>(),
  {
    title: 'Chi tiết doanh thu khóa học',
    theme: 'admin',
  },
)

// ── State ──────────────────────────────────────────────────────────────────────
const searchQuery = ref('')
const currentPage = ref(0)
const pageSize = ref(10)
const sortKey = ref<keyof CourseRevenue>('revenue')
const sortAsc = ref(false)

// Reset page when search changes
watch(searchQuery, () => {
  currentPage.value = 0
})

// ── Theme ──────────────────────────────────────────────────────────────────────
const teacherColor = computed(() =>
  props.theme === 'teacher' ? 'text-yellow-600' : 'text-blue-500',
)
const adminColor = computed(() => (props.theme === 'teacher' ? 'text-gray-400' : 'text-blue-500'))
const teacherValueColor = computed(() =>
  props.theme === 'teacher' ? 'text-yellow-700' : 'text-blue-700',
)
const adminValueColor = computed(() =>
  props.theme === 'teacher' ? 'text-gray-400' : 'text-blue-700',
)
const barColor = computed(() => (props.theme === 'teacher' ? 'bg-blue-500' : 'bg-blue-500'))
const footerBg = computed(() => (props.theme === 'teacher' ? 'bg-blue-50' : 'bg-blue-50'))
const footerBorder = computed(() =>
  props.theme === 'teacher' ? 'border-blue-100' : 'border-blue-100',
)
const activePageClass = computed(() =>
  props.theme === 'teacher'
    ? 'bg-blue-600 text-white border-blue-600 shadow'
    : 'bg-blue-600 text-white border-blue-600 shadow',
)

// ── Sort ───────────────────────────────────────────────────────────────────────
const toggleSort = (key: keyof CourseRevenue) => {
  if (sortKey.value === key) sortAsc.value = !sortAsc.value
  else {
    sortKey.value = key
    sortAsc.value = false
  }
  currentPage.value = 0
}
const sortIcon = (key: keyof CourseRevenue) => {
  if (sortKey.value !== key) return '↕'
  return sortAsc.value ? '↑' : '↓'
}

// ── Filter + Sort ──────────────────────────────────────────────────────────────
const filteredRows = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  let rows = q
    ? props.courses.filter((c) => c.courseTitle.toLowerCase().includes(q))
    : [...props.courses]

  const k = sortKey.value
  rows.sort((a, b) => {
    const av = typeof a[k] === 'string' ? (a[k] as string).toLowerCase() : Number(a[k])
    const bv = typeof b[k] === 'string' ? (b[k] as string).toLowerCase() : Number(b[k])
    if (av < bv) return sortAsc.value ? -1 : 1
    if (av > bv) return sortAsc.value ? 1 : -1
    return 0
  })
  return rows
})

// ── Paginate ───────────────────────────────────────────────────────────────────
const totalPages = computed(() =>
  Math.max(1, Math.ceil(filteredRows.value.length / pageSize.value)),
)
const isLastPage = computed(() => currentPage.value >= totalPages.value - 1)
const paginatedRows = computed(() => {
  const start = currentPage.value * pageSize.value
  return filteredRows.value.slice(start, start + pageSize.value)
})

const displayStart = computed(() =>
  filteredRows.value.length === 0 ? 0 : currentPage.value * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, filteredRows.value.length),
)

const pageNumbers = computed(() => {
  const total = totalPages.value,
    cur = currentPage.value,
    delta = 2
  const range: number[] = []
  for (let i = Math.max(0, cur - delta); i <= Math.min(total - 1, cur + delta); i++) range.push(i)
  return range
})

// ── Totals (filtered) ──────────────────────────────────────────────────────────
const totalRevenue = computed(() => filteredRows.value.reduce((s, c) => s + Number(c.revenue), 0))
const totalTeacher = computed(() =>
  filteredRows.value.reduce((s, c) => s + Number(c.teacherRevenue), 0),
)
const totalAdmin = computed(() =>
  filteredRows.value.reduce((s, c) => s + Number(c.adminRevenue), 0),
)
const totalOrders = computed(() => filteredRows.value.reduce((s, c) => s + Number(c.orderCount), 0))

// ── Helpers ────────────────────────────────────────────────────────────────────
const maxRevenue = computed(() => Math.max(...props.courses.map((c) => Number(c.revenue)), 1))
const pct = (v: number | string) => Math.round((Number(v) / maxRevenue.value) * 100)
const fmt = (v: number | string) =>
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
