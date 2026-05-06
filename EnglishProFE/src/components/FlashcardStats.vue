<template>
  <div class="bg-white rounded-xl border border-gray-200 p-6">
    <!-- Header -->
    <h3 class="text-xl font-bold text-gray-900 mb-6">Tiến độ học tập</h3>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
      <!-- New Cards -->
      <div class="p-4 bg-blue-50 rounded-lg border border-blue-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-blue-700">Thẻ Mới</p>
            <p class="text-3xl font-bold text-blue-600">{{ stats.new }}</p>
          </div>
          <svg class="w-8 h-8 text-blue-300" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M7 3a1 1 0 000 2h6a1 1 0 000-2H7zM4 7a1 1 0 011-1h10a1 1 0 011 1v10a2 2 0 01-2 2H6a2 2 0 01-2-2V7z"
            />
          </svg>
        </div>
      </div>

      <!-- Learning Cards -->
      <div class="p-4 bg-yellow-50 rounded-lg border border-yellow-200">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-yellow-700">Đang Học</p>
            <p class="text-3xl font-bold text-yellow-600">{{ stats.learning }}</p>
          </div>
          <svg class="w-8 h-8 text-yellow-300" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
            />
          </svg>
        </div>
      </div>

      <!-- Mastered Cards -->
      <div class="p-4 bg-green-50 rounded-lg border border-green-200 md:col-span-2">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-green-700">Đã Thuộc</p>
            <p class="text-3xl font-bold text-green-600">{{ stats.mastered }}</p>
          </div>
          <svg class="w-8 h-8 text-green-300" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
              clip-rule="evenodd"
            />
          </svg>
        </div>
      </div>
    </div>

    <!-- Study Streak -->
    <div
      class="mb-8 p-4 bg-gradient-to-r from-orange-50 to-red-50 rounded-lg border border-orange-200"
    >
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-orange-700 font-medium">Chuỗi học tập</p>
          <p class="text-2xl font-bold text-orange-600 flex items-center gap-2">
            <span>🔥</span> {{ streak.currentStreak }} ngày
          </p>
          <p class="text-xs text-orange-600 mt-1">Kỷ lục: {{ streak.longestStreak }} ngày</p>
        </div>
        <svg class="w-12 h-12 text-orange-200" fill="currentColor" viewBox="0 0 20 20">
          <path
            d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z"
          />
          <path
            d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1V5a1 1 0 00-1-1H3zM14 7a1 1 0 00-1 1v6.05A2.5 2.5 0 0115.95 16H17a1 1 0 001-1v-5a1 1 0 00-.293-.707l-2-2A1 1 0 0015 7h-1z"
          />
        </svg>
      </div>
    </div>

    <!-- Review Schedule Chart -->
    <div v-if="reviewSchedule.length > 0">
      <h4 class="font-semibold text-gray-900 mb-4">Lượng học trong 7 ngày tới</h4>

      <!-- Simple Bar Chart -->
      <div class="space-y-3">
        <div v-for="(item, index) in reviewSchedule" :key="index" class="flex items-center gap-3">
          <!-- Date Label -->
          <span class="text-xs text-gray-600 w-12 text-right">{{ formatDate(item.date) }}</span>

          <!-- Bar Container -->
          <div class="flex-1 bg-gray-100 rounded-full h-6 overflow-hidden">
            <div
              class="h-full bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-end pr-2 transition-all"
              :style="{ width: (item.count / maxCount) * 100 + '%' }"
            >
              <span v-if="item.count > 0" class="text-xs font-bold text-white">
                {{ item.count }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-8 text-gray-500">
      <p class="text-sm">Chưa có dữ liệu lịch học</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  stats: {
    new: number
    learning: number
    mastered: number
  }
  streak: {
    currentStreak: number
    longestStreak: number
  }
  reviewSchedule: {
    date: string
    count: number
  }[]
}

const props = defineProps<Props>()

const maxCount = computed(() => {
  const counts = props.reviewSchedule.map((item) => item.count)
  return Math.max(...counts, 10)
})

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)

  if (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  ) {
    return 'Hôm nay'
  }

  if (
    date.getFullYear() === tomorrow.getFullYear() &&
    date.getMonth() === tomorrow.getMonth() &&
    date.getDate() === tomorrow.getDate()
  ) {
    return 'Ngày mai'
  }

  return date.toLocaleDateString('vi-VN', { month: 'short', day: 'numeric' })
}
</script>
