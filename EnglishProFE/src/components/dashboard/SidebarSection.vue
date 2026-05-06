<template>
  <div class="mb-4">
    <p
      v-if="!isCollapsed"
      class="text-xs font-semibold text-gray-500 px-3 py-2 uppercase tracking-wider transition-all duration-300"
    >
      {{ title }}
    </p>
    <div :class="isCollapsed ? 'space-y-2' : ''">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  isCollapsed?: boolean
}>()
</script>
