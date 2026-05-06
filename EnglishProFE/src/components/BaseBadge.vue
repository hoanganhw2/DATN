<template>
  <span
    :class="[
      'inline-flex items-center gap-2 font-semibold rounded-full px-3 py-1 text-xs uppercase tracking-wide',
      colorClasses,
    ]"
  >
    <slot></slot>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'beginner' | 'intermediate' | 'advanced' | 'expert' | 'primary' | 'secondary'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
})

const colorClasses = computed(() => {
  const variants = {
    beginner: 'bg-green-100 text-green-800',
    intermediate: 'bg-blue-100 text-blue-800',
    advanced: 'bg-yellow-100 text-yellow-800',
    expert: 'bg-blue-100 text-blue-800',
    primary: 'bg-blue-100 text-blue-800',
    secondary: 'bg-gray-100 text-gray-800',
  }
  return variants[props.variant]
})
</script>
