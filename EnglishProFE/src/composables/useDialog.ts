import { reactive } from 'vue'

type DialogType = 'success' | 'error' | 'warning' | 'info' | 'none'

interface DialogOptions {
  title: string
  message: string
  type?: DialogType
  confirmText?: string
  cancelText?: string
  showCancel?: boolean
  isAlert?: boolean
}

interface DialogState {
  visible: boolean
  title: string
  message: string
  type: DialogType
  confirmText: string
  cancelText: string
  showCancel: boolean
  isAlert: boolean
  resolve: ((value: boolean) => void) | null
  reject: ((reason?: any) => void) | null
}

const state = reactive<DialogState>({
  visible: false,
  title: '',
  message: '',
  type: 'none',
  confirmText: 'Xác nhận',
  cancelText: 'Hủy',
  showCancel: true,
  isAlert: false,
  resolve: null,
  reject: null,
})

export function useDialog() {
  const show = (options: DialogOptions | string, maybeMessage?: string): Promise<boolean> => {
    return new Promise((resolve) => {
      if (typeof options === 'string') {
        state.title = 'Thông báo'
        state.message = options
        state.type = 'none'
        state.isAlert = true
      } else {
        state.title = options.title
        state.message = options.message
        state.type = options.type || 'none'
        state.confirmText = options.confirmText || 'Xác nhận'
        state.cancelText = options.cancelText || 'Hủy'
        state.showCancel = options.showCancel ?? true
        state.isAlert = options.isAlert ?? false
      }

      state.resolve = resolve
      state.visible = true
    })
  }

  const confirm = (title: string, message: string, type: DialogType = 'none') => {
    return show({ title, message, type, showCancel: true, isAlert: false })
  }

  const alert = (title: string, message: string, type: DialogType = 'none') => {
    return show({ title, message, type, showCancel: false, isAlert: true })
  }

  return { show, confirm, alert }
}

export { state as dialogState }
