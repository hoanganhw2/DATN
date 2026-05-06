<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- Backdrop -->
        <div
          @click="$emit('update:modelValue', false)"
          class="fixed inset-0 bg-black/50 backdrop-blur-sm"
        ></div>

        <!-- Modal -->
        <div
          class="relative bg-white rounded-lg shadow-2xl border border-gray-200 max-w-md w-full mx-4 overflow-hidden"
        >
          <!-- Header -->
          <div class="flex justify-between items-center px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-bold text-gray-900">{{ title }}</h3>
            <button
              @click="$emit('update:modelValue', false)"
              class="text-gray-500 hover:text-gray-700 transition"
            >
              <XIcon class="w-6 h-6" />
            </button>
          </div>

          <!-- Content -->
          <div class="px-6 py-4 text-gray-700">
            <slot />
          </div>

          <!-- Footer -->
          <div
            v-if="showFooter"
            class="flex justify-end space-x-3 px-6 py-4 border-t border-gray-200"
          >
            <button
              @click="$emit('update:modelValue', false)"
              class="px-4 py-2 rounded-lg text-gray-700 hover:bg-gray-100 transition"
            >
              {{ cancelText }}
            </button>
            <button
              @click="$emit('confirm')"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition text-white',
                confirmColor === 'red'
                  ? 'bg-red-600 hover:bg-red-700'
                  : 'bg-blue-600 hover:bg-blue-700',
              ]"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import XIcon from './icons/XIcon.vue'

withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    confirmText?: string
    cancelText?: string
    showFooter?: boolean
    confirmColor?: 'blue' | 'red'
  }>(),
  {
    confirmText: 'Xác nhận',
    cancelText: 'Hủy',
    showFooter: true,
    confirmColor: 'blue',
  },
)

defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
}>()
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
