import { defineStore } from 'pinia'

interface Toast {
  id: string
  message: string
  type: 'success' | 'error' | 'warning' | 'info'
  duration?: number
}

interface ToastState {
  toasts: Toast[]
}

let toastId = 0

export const useToastStore = defineStore('toast', {
  state: (): ToastState => ({
    toasts: [],
  }),

  actions: {
    addToast(
      message: string,
      type: 'success' | 'error' | 'warning' | 'info' = 'info',
      duration: number = 4000,
    ) {
      const id = `toast-${toastId++}`
      const toast: Toast = { id, message, type, duration }

      this.toasts.push(toast)

      if (duration > 0) {
        setTimeout(() => {
          this.removeToast(id)
        }, duration)
      }

      return id
    },

    removeToast(id: string) {
      const index = this.toasts.findIndex((t) => t.id === id)
      if (index > -1) {
        this.toasts.splice(index, 1)
      }
    },

    success(message: string, duration?: number) {
      return this.addToast(message, 'success', duration)
    },

    error(message: string, duration?: number) {
      return this.addToast(message, 'error', duration)
    },

    warning(message: string, duration?: number) {
      return this.addToast(message, 'warning', duration)
    },

    info(message: string, duration?: number) {
      return this.addToast(message, 'info', duration)
    },
  },
})
