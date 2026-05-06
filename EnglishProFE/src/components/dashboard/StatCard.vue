<template>
  <div
    class="bg-linear-to-br from-white to-gray-50 rounded-lg border border-gray-200 p-6 hover:border-blue-200 transition shadow-sm"
  >
    <div class="flex justify-between items-start mb-4">
      <div>
        <p class="text-sm text-gray-600 mb-1">{{ label }}</p>
        <h3 class="text-3xl font-bold text-gray-900">{{ value }}</h3>
      </div>
      <div v-if="icon" :class="`text-2xl ${iconColor}`">
        <component :is="icon" class="w-8 h-8" />
      </div>
    </div>

    <div
      v-if="change"
      :class="['text-sm font-medium', change > 0 ? 'text-green-400' : 'text-red-400']"
    >
      <span v-if="change > 0">↑</span>
      <span v-else>↓</span>
      {{ Math.abs(change) }}% from last month
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  icon?: any
  iconColor?: string
  change?: number
}>()
</script>
