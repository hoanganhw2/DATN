<template>
  <teleport to="body">
    <div v-if="true" class="fixed inset-0 z-50 overflow-y-auto">
      <!-- Backdrop -->
      <div class="fixed inset-0 bg-black/50 transition-opacity" @click="$emit('close')"></div>

      <!-- Modal -->
      <div class="flex items-end justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:p-0">
        <div
          class="relative bg-white rounded-t-2xl w-full max-w-2xl shadow-xl transform transition-all"
        >
          <!-- Header -->
          <div class="flex items-center justify-between border-b border-gray-200 p-6">
            <h2 class="text-lg font-bold text-gray-900">Bộ lọc</h2>
            <button
              @click="$emit('close')"
              class="text-gray-400 hover:text-gray-600 focus:outline-none"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>

          <!-- Content -->
          <div class="p-6 max-h-[60vh] overflow-y-auto">
            <!-- Category Filter -->
            <div class="mb-8">
              <h3 class="font-semibold text-gray-900 mb-4">Phân loại</h3>
              <div class="space-y-3">
                <label
                  v-for="cat in categories"
                  :key="cat"
                  class="flex items-center cursor-pointer"
                >
                  <input
                    type="checkbox"
                    :checked="filters.category?.includes(cat)"
                    @change="
                      (e) => handleCategoryChange(cat, (e.target as HTMLInputElement).checked)
                    "
                    class="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-3 text-gray-700">{{ cat }}</span>
                </label>
              </div>
            </div>

            <!-- Level Filter -->
            <div class="mb-8">
              <h3 class="font-semibold text-gray-900 mb-4">Trình độ</h3>
              <div class="space-y-3">
                <label
                  v-for="level in levels"
                  :key="level"
                  class="flex items-center cursor-pointer"
                >
                  <input
                    type="checkbox"
                    :checked="filters.level?.includes(level)"
                    @change="
                      (e) => handleLevelChange(level, (e.target as HTMLInputElement).checked)
                    "
                    class="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-3 text-gray-700">{{ getLevelLabel(level) }}</span>
                </label>
              </div>
            </div>

            <!-- Price Filter -->
            <div class="mb-8">
              <h3 class="font-semibold text-gray-900 mb-4">Học phí</h3>
              <div class="space-y-3">
                <label class="flex items-center cursor-pointer">
                  <input
                    type="radio"
                    name="price"
                    value="all"
                    :checked="filters.priceRange === 'all'"
                    @change="handlePriceChange('all')"
                    class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                  />
                  <span class="ml-3 text-gray-700">Tất cả</span>
                </label>
                <label class="flex items-center cursor-pointer">
                  <input
                    type="radio"
                    name="price"
                    value="free"
                    :checked="filters.priceRange === 'free'"
                    @change="handlePriceChange('free')"
                    class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                  />
                  <span class="ml-3 text-gray-700">Miễn phí</span>
                </label>
                <label class="flex items-center cursor-pointer">
                  <input
                    type="radio"
                    name="price"
                    value="paid"
                    :checked="filters.priceRange === 'paid'"
                    @change="handlePriceChange('paid')"
                    class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                  />
                  <span class="ml-3 text-gray-700">Có phí</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="border-t border-gray-200 p-6 flex gap-3">
            <button
              @click="$emit('close')"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-900 font-medium hover:bg-gray-50 transition-colors"
            >
              Đóng
            </button>
            <button
              @click="applyFilters"
              class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
            >
              Áp dụng
            </button>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import type { CourseFilterParams } from '@/api/courses'

interface Props {
  filters: CourseFilterParams
}

const props = defineProps<Props>()

const emit = defineEmits<{
  close: []
  update: [filters: Partial<CourseFilterParams>]
}>()

const categories = ['TOEIC', 'IELTS', 'Giao tiếp', 'Ngữ pháp']
const levels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED']

const localFilters = reactive({
  category: [...(props.filters.category || [])],
  level: [...(props.filters.level || [])],
  priceRange: props.filters.priceRange || 'all',
})

const getLevelLabel = (level: string): string => {
  const labels: Record<string, string> = {
    BEGINNER: 'Sơ cấp',
    INTERMEDIATE: 'Trung cấp',
    ADVANCED: 'Nâng cao',
  }
  return labels[level] || level
}

const handleCategoryChange = (category: string, checked: boolean) => {
  if (checked) {
    if (!localFilters.category.includes(category)) {
      localFilters.category.push(category)
    }
  } else {
    localFilters.category = localFilters.category.filter((c) => c !== category)
  }
}

const handleLevelChange = (level: string, checked: boolean) => {
  if (checked) {
    if (!localFilters.level.includes(level)) {
      localFilters.level.push(level)
    }
  } else {
    localFilters.level = localFilters.level.filter((l) => l !== level)
  }
}

const handlePriceChange = (price: 'all' | 'free' | 'paid') => {
  localFilters.priceRange = price
}

const applyFilters = () => {
  emit('update', {
    category: localFilters.category,
    level: localFilters.level,
    priceRange: localFilters.priceRange,
  })
  emit('close')
}
</script>
