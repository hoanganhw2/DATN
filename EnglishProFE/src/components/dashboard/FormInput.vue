<template>
  <div class="flex flex-col">
    <label v-if="label" class="block text-sm font-medium text-gray-700 mb-2">
      {{ label }}
      <span v-if="required" class="text-red-600">*</span>
    </label>

    <div class="relative">
      <component
        v-if="type === 'textarea'"
        :is="'textarea'"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
        :placeholder="placeholder"
        :rows="rows"
        :class="inputClasses"
        class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
      />
      <input
        v-else
        :type="type"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        :placeholder="placeholder"
        :class="inputClasses"
        class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
      />
      <component
        v-if="icon"
        :is="icon"
        class="absolute right-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400 pointer-events-none"
      />
    </div>

    <p v-if="error" class="mt-1 text-sm text-red-600">{{ error }}</p>
    <p v-if="hint" class="mt-1 text-sm text-gray-600">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

withDefaults(
  defineProps<{
    modelValue: string | number
    label?: string
    type?: string
    placeholder?: string
    error?: string
    hint?: string
    icon?: any
    required?: boolean
    rows?: number
    disabled?: boolean
  }>(),
  {
    type: 'text',
    placeholder: '',
    rows: 3,
    disabled: false,
  },
)

defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const inputClasses = computed(() => ({
  'opacity-50 cursor-not-allowed pointer-events-none': !!false, // placeholder for disabled state
}))
</script>
