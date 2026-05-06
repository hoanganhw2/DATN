<template>
  <button
    :type="type"
    :class="[
      'inline-flex items-center justify-center space-x-2 px-4 py-2 rounded-lg font-medium transition duration-200',
      'disabled:opacity-50 disabled:cursor-not-allowed',
      variantClasses,
      sizeClasses,
    ]"
    :disabled="disabled"
  >
    <component v-if="icon" :is="icon" class="w-5 h-5" />
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
    size?: 'sm' | 'md' | 'lg'
    type?: 'button' | 'submit' | 'reset'
    disabled?: boolean
    icon?: any
  }>(),
  {
    variant: 'primary',
    size: 'md',
    type: 'button',
    disabled: false,
  },
)

const variantClasses = computed(() => {
  switch (props.variant) {
    case 'primary':
      return 'bg-blue-600 hover:bg-blue-700 text-white'
    case 'secondary':
      return 'border border-gray-300 text-gray-700 hover:bg-gray-100'
    case 'danger':
      return 'bg-red-600 hover:bg-red-700 text-white'
    case 'ghost':
      return 'text-gray-700 hover:bg-gray-100'
    default:
      return ''
  }
})

const sizeClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'px-3 py-1 text-sm'
    case 'lg':
      return 'px-6 py-3 text-lg'
    default:
      return 'px-4 py-2 text-base'
  }
})
</script>
