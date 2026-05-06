<template>
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="state.visible" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <!-- Backdrop -->
        <div
          class="fixed inset-0 bg-black/50 backdrop-blur-md cursor-pointer"
          @click="handleCancel"
        ></div>

        <!-- Dialog -->
        <div
          class="relative bg-white rounded-xl shadow-2xl max-w-sm w-full overflow-hidden transform transition-all scale-100"
        >
          <!-- Close Button -->
          <button
            v-if="state.isAlert"
            @click="handleCancel"
            class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>

          <!-- Icon -->
          <div v-if="state.type !== 'none'" class="flex justify-center pt-6">
            <div
              :class="[
                'w-16 h-16 rounded-full flex items-center justify-center',
                state.type === 'success'
                  ? 'bg-green-100'
                  : state.type === 'error'
                    ? 'bg-red-100'
                    : state.type === 'warning'
                      ? 'bg-yellow-100'
                      : 'bg-blue-100',
              ]"
            >
              <svg
                v-if="state.type === 'success'"
                class="w-8 h-8 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
              <svg
                v-else-if="state.type === 'error'"
                class="w-8 h-8 text-red-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
              <svg
                v-else-if="state.type === 'warning'"
                class="w-8 h-8 text-yellow-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
              <svg
                v-else
                class="w-8 h-8 text-blue-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
          </div>

          <!-- Content -->
          <div class="px-6 pt-4 pb-6 text-center">
            <h3 class="text-xl font-bold text-gray-900 mb-2">{{ state.title }}</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ state.message }}</p>
          </div>

          <!-- Footer -->
          <div class="flex justify-center gap-3 px-6 pb-6">
            <button
              v-if="state.showCancel"
              @click="handleCancel"
              class="px-6 py-2.5 rounded-lg font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 transition-colors"
            >
              {{ state.cancelText }}
            </button>
            <button
              @click="handleConfirm"
              :class="[
                'px-6 py-2.5 rounded-lg font-medium text-white transition-colors',
                state.type === 'success'
                  ? 'bg-green-600 hover:bg-green-700'
                  : state.type === 'error'
                    ? 'bg-red-600 hover:bg-red-700'
                    : state.type === 'warning'
                      ? 'bg-yellow-600 hover:bg-yellow-700'
                      : 'bg-blue-600 hover:bg-blue-700',
              ]"
            >
              {{ state.confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { dialogState as state } from '@/composables/useDialog'

const handleConfirm = () => {
  state.resolve?.(true)
  state.visible = false
}

const handleCancel = () => {
  state.resolve?.(false)
  state.visible = false
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

.dialog-enter-active .bg-white,
.dialog-leave-active .bg-white {
  transition: transform 0.3s ease;
}

.dialog-enter-from .bg-white,
.dialog-leave-to .bg-white {
  transform: scale(0.9);
}
</style>
