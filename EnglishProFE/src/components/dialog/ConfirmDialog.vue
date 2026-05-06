<template>
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <!-- Backdrop -->
        <div
          @click="!closeOnBackdrop && $emit('update:modelValue', false)"
          :class="[
            'fixed inset-0 transition-all duration-300',
            closeOnBackdrop ? 'cursor-not-allowed' : 'cursor-pointer',
            isAlert ? 'bg-red-500/20' : 'bg-black/50',
            isAlert ? 'backdrop-blur-sm' : 'backdrop-blur-md',
          ]"
        ></div>

        <!-- Dialog -->
        <div
          :class="[
            'relative bg-white rounded-xl shadow-2xl max-w-sm w-full overflow-hidden transform transition-all',
            size === 'sm' ? 'max-w-xs' : size === 'lg' ? 'max-w-lg' : 'max-w-md',
          ]"
        >
          <!-- Close Button -->
          <button
            v-if="showClose"
            @click="$emit('update:modelValue', false)"
            class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XIcon class="w-5 h-5" />
          </button>

          <!-- Icon -->
          <div v-if="type !== 'none'" class="flex justify-center pt-6">
            <div
              :class="[
                'w-16 h-16 rounded-full flex items-center justify-center',
                type === 'success'
                  ? 'bg-green-100'
                  : type === 'error'
                    ? 'bg-red-100'
                    : type === 'warning'
                      ? 'bg-yellow-100'
                      : type === 'info'
                        ? 'bg-blue-100'
                        : 'bg-gray-100',
              ]"
            >
              <component
                :is="getIcon(type)"
                :class="[
                  'w-8 h-8',
                  type === 'success'
                    ? 'text-green-600'
                    : type === 'error'
                      ? 'text-red-600'
                      : type === 'warning'
                        ? 'text-yellow-600'
                        : type === 'info'
                          ? 'text-blue-600'
                          : 'text-gray-600',
                ]"
              />
            </div>
          </div>

          <!-- Content -->
          <div class="px-6 pt-4 pb-6 text-center">
            <h3 class="text-xl font-bold text-gray-900 mb-2">{{ title }}</h3>
            <p class="text-gray-600">{{ message }}</p>

            <!-- Slot for additional content -->
            <slot />
          </div>

          <!-- Footer -->
          <div v-if="!isAlert" class="flex justify-center gap-3 px-6 pb-6">
            <button
              v-if="showCancel"
              @click="handleCancel"
              class="px-6 py-2.5 rounded-lg font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 transition-colors"
            >
              {{ cancelText }}
            </button>
            <button
              @click="handleConfirm"
              :class="[
                'px-6 py-2.5 rounded-lg font-medium text-white transition-colors',
                type === 'success'
                  ? 'bg-green-600 hover:bg-green-700'
                  : type === 'error'
                    ? 'bg-red-600 hover:bg-red-700'
                    : type === 'warning'
                      ? 'bg-yellow-600 hover:bg-yellow-700'
                      : 'bg-blue-600 hover:bg-blue-700',
              ]"
            >
              {{ confirmText }}
            </button>
          </div>

          <!-- Alert footer (only OK button) -->
          <div v-else class="flex justify-center px-6 pb-6">
            <button
              @click="$emit('update:modelValue', false)"
              class="px-6 py-2.5 rounded-lg font-medium text-white bg-gray-900 hover:bg-gray-800 transition-colors"
            >
              Đã hiểu
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import XIcon from './icons/XIcon.vue'
import CheckIcon from './icons/CheckIcon.vue'
import XCircleIcon from './icons/XCircleIcon.vue'
import AlertTriangleIcon from './icons/AlertTriangleIcon.vue'
import InfoIcon from './icons/InfoIcon.vue'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    message: string
    type?: 'success' | 'error' | 'warning' | 'info' | 'none'
    confirmText?: string
    cancelText?: string
    showCancel?: boolean
    isAlert?: boolean
    size?: 'sm' | 'md' | 'lg'
    closeOnBackdrop?: boolean
    showClose?: boolean
  }>(),
  {
    type: 'none',
    confirmText: 'Xác nhận',
    cancelText: 'Hủy',
    showCancel: true,
    isAlert: false,
    size: 'md',
    closeOnBackdrop: false,
    showClose: false,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

const getIcon = (type: string) => {
  switch (type) {
    case 'success':
      return CheckIcon
    case 'error':
      return XCircleIcon
    case 'warning':
      return AlertTriangleIcon
    case 'info':
      return InfoIcon
    default:
      return null
  }
}

const handleConfirm = () => {
  emit('confirm')
  emit('update:modelValue', false)
}

const handleCancel = () => {
  emit('cancel')
  emit('update:modelValue', false)
}
</script>

<style scoped>
.dialog-enter-active,
.dialog-leave-active {
  transition: opacity 0.3s ease;
}

.dialog-enter-from,
.dialog-leave-to {
  opacity: 0;
}

.dialog-enter-active .relative,
.dialog-leave-active .relative {
  transition: transform 0.3s ease;
}

.dialog-enter-from .relative,
.dialog-leave-to .relative {
  transform: scale(0.9);
}
</style>
