<template>
  <nav class="flex items-center justify-center gap-2 mt-12 py-4" aria-label="Pagination">
    <!-- Previous Button -->
    <button
      @click="$emit('update:current-page', currentPage - 1)"
      :disabled="currentPage === 1"
      class="flex items-center gap-2 px-4 py-2 text-sm font-semibold transition-all duration-200 rounded-xl"
      :class="[
        currentPage === 1
          ? 'text-gray-300 cursor-not-allowed bg-gray-50'
          : 'text-gray-700 bg-white border border-gray-200 hover:border-blue-500 hover:text-blue-600 hover:shadow-lg hover:-translate-x-1 shadow-sm'
      ]"
    >
      <svg class="w-5 h-5 transition-transform group-hover:-translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      <span class="hidden sm:inline">Trước</span>
    </button>

    <!-- Page Numbers -->
    <div class="flex items-center gap-2 px-2 bg-gray-100/50 p-1 rounded-2xl backdrop-blur-sm border border-gray-100 ml-2 mr-2">
      <!-- First page -->
      <button
        v-if="startPage > 1"
        @click="$emit('update:current-page', 1)"
        class="w-10 h-10 flex items-center justify-center text-sm font-bold rounded-xl transition-all duration-200 hover:bg-white hover:text-blue-600 hover:shadow-sm text-gray-600"
      >
        1
      </button>

      <!-- Ellipsis -->
      <span v-if="startPage > 2" class="w-10 h-10 flex items-center justify-center text-gray-400">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM18 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
      </span>

      <!-- Page range -->
      <button
        v-for="page in visiblePages"
        :key="page"
        @click="$emit('update:current-page', page)"
        class="w-10 h-10 flex items-center justify-center text-sm font-bold rounded-xl transition-all duration-200"
        :class="[
          page === currentPage
            ? 'bg-blue-600 text-white shadow-lg shadow-blue-200 scale-110'
            : 'text-gray-600 hover:bg-white hover:text-blue-600 hover:shadow-sm'
        ]"
      >
        {{ page }}
      </button>

      <!-- Ellipsis -->
      <span v-if="endPage < totalPages - 1" class="w-10 h-10 flex items-center justify-center text-gray-400">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM18 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
      </span>

      <!-- Last page -->
      <button
        v-if="endPage < totalPages"
        @click="$emit('update:current-page', totalPages)"
        class="w-10 h-10 flex items-center justify-center text-sm font-bold rounded-xl transition-all duration-200 hover:bg-white hover:text-blue-600 hover:shadow-sm text-gray-600"
      >
        {{ totalPages }}
      </button>
    </div>

    <!-- Next Button -->
    <button
      @click="$emit('update:current-page', currentPage + 1)"
      :disabled="currentPage === totalPages"
      class="flex items-center gap-2 px-4 py-2 text-sm font-semibold transition-all duration-200 rounded-xl"
      :class="[
        currentPage === totalPages
          ? 'text-gray-300 cursor-not-allowed bg-gray-50'
          : 'text-gray-700 bg-white border border-gray-200 hover:border-blue-500 hover:text-blue-600 hover:shadow-lg hover:translate-x-1 shadow-sm'
      ]"
    >
      <span class="hidden sm:inline">Sau</span>
      <svg class="w-5 h-5 transition-transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
      </svg>
    </button>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  currentPage: number
  totalPages: number
  maxVisiblePages?: number
}

const props = withDefaults(defineProps<Props>(), {
  maxVisiblePages: 5,
})

defineEmits<{
  'update:current-page': [page: number]
}>()

const startPage = computed(() => {
  const half = Math.floor(props.maxVisiblePages / 2)
  const start = Math.max(1, props.currentPage - half)
  return Math.min(start, Math.max(1, props.totalPages - props.maxVisiblePages + 1))
})

const endPage = computed(() => {
  return Math.min(props.totalPages, startPage.value + props.maxVisiblePages - 1)
})

const visiblePages = computed(() => {
  const pages = []
  for (let i = startPage.value; i <= endPage.value; i++) {
    pages.push(i)
  }
  return pages
})
</script>
