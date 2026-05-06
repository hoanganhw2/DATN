<template>
  <div class="bg-white rounded-xl p-6 border border-gray-200 sticky top-6">
    <h2 class="text-lg font-bold text-gray-900 mb-6">Bộ lọc</h2>

    <!-- Category Filter -->
    <div class="mb-8 pb-8 border-b border-gray-200">
      <h3 class="font-semibold text-gray-900 mb-4">Phân loại</h3>
      <div class="space-y-3">
        <label v-for="cat in categories" :key="cat" class="flex items-center cursor-pointer">
          <input
            type="checkbox"
            :checked="initialFilters.category?.includes(cat)"
            @change="(e) => handleCategoryChange(cat, (e.target as HTMLInputElement).checked)"
            class="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 cursor-pointer"
          />
          <span class="ml-3 text-gray-700">{{ cat }}</span>
        </label>
      </div>
    </div>

    <!-- Level Filter -->
    <div class="mb-8 pb-8 border-b border-gray-200">
      <h3 class="font-semibold text-gray-900 mb-4">Trình độ</h3>
      <div class="space-y-3">
        <label v-for="level in levels" :key="level" class="flex items-center cursor-pointer">
          <input
            type="checkbox"
            :checked="initialFilters.level?.includes(level)"
            @change="(e) => handleLevelChange(level, (e.target as HTMLInputElement).checked)"
            class="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 cursor-pointer"
          />
          <span class="ml-3 text-gray-700">{{ getLevelLabel(level) }}</span>
        </label>
      </div>
    </div>

    <!-- Price Filter -->
    <div class="mb-8 pb-8 border-b border-gray-200">
      <h3 class="font-semibold text-gray-900 mb-4">Học phí</h3>
      <div class="space-y-3">
        <label class="flex items-center cursor-pointer">
          <input
            type="radio"
            name="price"
            value="all"
            :checked="initialFilters.priceRange === 'all'"
            @change="handlePriceChange('all')"
            class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500 cursor-pointer"
          />
          <span class="ml-3 text-gray-700">Tất cả</span>
        </label>
        <label class="flex items-center cursor-pointer">
          <input
            type="radio"
            name="price"
            value="free"
            :checked="initialFilters.priceRange === 'free'"
            @change="handlePriceChange('free')"
            class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500 cursor-pointer"
          />
          <span class="ml-3 text-gray-700">Miễn phí</span>
        </label>
        <label class="flex items-center cursor-pointer">
          <input
            type="radio"
            name="price"
            value="paid"
            :checked="initialFilters.priceRange === 'paid'"
            @change="handlePriceChange('paid')"
            class="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500 cursor-pointer"
          />
          <span class="ml-3 text-gray-700">Có phí</span>
        </label>
      </div>
    </div>

    <!-- Reset Button -->
    <BaseButton variant="outline" class="w-full"> Xóa bộ lọc </BaseButton>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import type { CourseFilterParams } from '@/api/courses'

interface Props {
  initialFilters: CourseFilterParams
}

const props = defineProps<Props>()

const emit = defineEmits<{
  update: [filters: Partial<CourseFilterParams>]
}>()

const categories = ['TOEIC', 'IELTS', 'Giao tiếp', 'Ngữ pháp']
const levels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED']

const localFilters = reactive({
  category: [...(props.initialFilters.category || [])],
  level: [...(props.initialFilters.level || [])],
  priceRange: props.initialFilters.priceRange || 'all',
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
  emit('update', { category: localFilters.category })
}

const handleLevelChange = (level: string, checked: boolean) => {
  if (checked) {
    if (!localFilters.level.includes(level)) {
      localFilters.level.push(level)
    }
  } else {
    localFilters.level = localFilters.level.filter((l) => l !== level)
  }
  emit('update', { level: localFilters.level })
}

const handlePriceChange = (price: 'all' | 'free' | 'paid') => {
  localFilters.priceRange = price
  emit('update', { priceRange: price })
}
</script>
