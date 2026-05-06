import { ref, shallowRef } from 'vue'
import ConfirmDialog from './ConfirmDialog.vue'

const dialogRef = shallowRef(null)
const dialogState = ref({
  show: false,
  title: '',
  message: '',
  type: 'none' as 'success' | 'error' | 'warning' | 'info' | 'none',
  confirmText: 'Xác nhận',
  cancelText: 'Hủy',
  showCancel: true,
  isAlert: false,
})

export function useDialog() {
  const show = (
    title: string,
    message: string,
    options?: {
      type?: 'success' | 'error' | 'warning' | 'info' | 'none'
      confirmText?: string
      cancelText?: string
      showCancel?: boolean
      isAlert?: boolean
    }
  ) => {
    return new Promise<void>((resolve, reject) => {
      dialogState.value = {
        show: true,
        title,
        message,
        type: options?.type || 'none',
        confirmText: options?.confirmText || 'Xác nhận',
        cancelText: options?.cancelText || 'Hủy',
        showCancel: options?.showCancel ?? true,
        isAlert: options?.isAlert ?? false,
      }

      const handleConfirm = () => {
        cleanup()
        resolve()
      }

      const handleCancel = () => {
        cleanup()
        reject(new Error('canceled'))
      }

      const cleanup = () => {
        dialogState.value.show = false
        dialogRef.value?.removeEventListener('confirm', handleConfirm)
        dialogRef.value?.removeEventListener('cancel', handleCancel)
      }

      setTimeout(() => {
        const dialog = document.querySelector('.dialog-container')
        if (dialog) {
          dialog.addEventListener('confirm', handleConfirm)
          dialog.addEventListener('cancel', handleCancel)
        }
      }, 100)
    })
  }

  return { show }
}

export function ConfirmDialogWrapper() {
  return (
    <ConfirmDialog
      v-model={dialogState.value.show}
      title={dialogState.value.title}
      message={dialogState.value.message}
      type={dialogState.value.type}
      confirmText={dialogState.value.confirmText}
      cancelText={dialogState.value.cancelText}
      showCancel={dialogState.value.showCancel}
      isAlert={dialogState.value.isAlert}
      onConfirm={() => {
        const event = new CustomEvent('dialog-confirm')
        window.dispatchEvent(event)
      }}
      onCancel={() => {
        const event = new CustomEvent('dialog-cancel')
        window.dispatchEvent(event)
      }}
    />
  )
}