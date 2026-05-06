<template>
  <div class="flex flex-col min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-gray-900">Thanh toán</h1>
        <p class="text-gray-600 mt-2">Chọn phương thức thanh toán và hoàn tất đơn hàng</p>
      </div>

      <!-- Checkout Form -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Left Column: Payment Methods -->
        <div class="lg:col-span-2 space-y-8">
          <!-- Payment Methods Section -->
          <div class="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
            <h2 class="text-xl font-bold text-gray-900 mb-6">Phương thức thanh toán</h2>

            <div class="space-y-3">
              <label v-for="method in paymentMethods" :key="method.id" class="cursor-pointer block">
                <div
                  class="flex items-center gap-4 p-4 border-2 rounded-xl transition-all duration-200"
                  :class="
                    selectedPaymentMethod === method.id
                      ? 'border-blue-600 bg-blue-50 shadow-sm'
                      : 'border-gray-200 hover:border-gray-300 hover:shadow-sm'
                  "
                >
                  <!-- Radio Button -->
                  <input
                    v-model="selectedPaymentMethod"
                    type="radio"
                    :value="method.id"
                    class="w-5 h-5 text-blue-600 cursor-pointer"
                  />

                  <!-- Payment Method Info -->
                  <div class="flex-1">
                    <h3 class="font-semibold text-gray-900">{{ method.name }}</h3>
                    <p class="text-sm text-gray-600">{{ method.description }}</p>
                  </div>

                  <!-- Logo/Icon -->
                  <div class="flex-shrink-0 w-16 h-10 flex items-center justify-center">
                    <img
                      v-if="method.logo"
                      :src="method.logo"
                      :alt="method.name"
                      class="h-8 object-contain"
                    />
                    <span v-else class="text-2xl">{{ method.icon }}</span>
                  </div>
                </div>
              </label>
            </div>

            <!-- Info Message -->
            <div class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg flex gap-3">
              <svg class="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
              <p class="text-sm text-blue-800">
                Khóa học sẽ được kích hoạt ngay sau khi thanh toán thành công. Bạn sẽ được chuyển đến trang thanh toán VNPay để hoàn tất giao dịch.
              </p>
            </div>
          </div>

          <!-- User Email Section -->
          <div class="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
            <h2 class="text-xl font-bold text-gray-900 mb-4">Thông tin xác nhận</h2>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
              <input
                :value="userEmail"
                type="email"
                disabled
                class="w-full px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900 cursor-not-allowed"
              />
              <p class="text-xs text-gray-600 mt-2">Khóa học và hóa đơn sẽ được gửi đến email này.</p>
            </div>
          </div>
        </div>

        <!-- Right Column: Order Details -->
        <div class="lg:col-span-1">
          <div class="sticky top-4 bg-white rounded-2xl border border-gray-200 p-6 shadow-md">
            <!-- Title -->
            <h2 class="text-xl font-bold text-gray-900 mb-6">Chi tiết đơn hàng</h2>

            <!-- Courses List -->
            <div class="space-y-4 pb-4 border-b border-gray-200 mb-4">
              <div
                v-for="item in cartStore.items"
                :key="item.id"
                class="flex justify-between items-start gap-2"
              >
                <div class="flex-1">
                  <p class="font-medium text-gray-900 text-sm line-clamp-2">{{ item.title }}</p>
                  <p class="text-xs text-gray-600 mt-1">{{ item.teacher }}</p>
                </div>
                <p class="font-semibold text-gray-900 text-sm whitespace-nowrap">
                  {{ item.price.toLocaleString('vi-VN') }}đ
                </p>
              </div>
            </div>

            <!-- Coupon Input -->
            <div class="mb-6 space-y-2">
              <label class="block text-sm font-medium text-gray-700">Mã giảm giá</label>
              <div class="flex gap-2">
                <input
                  v-model="inputCoupon"
                  type="text"
                  placeholder="Nhập mã..."
                  class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 uppercase outline-none"
                  :disabled="isApplyingCoupon || cartStore.discountPercent > 0"
                />
                <button
                  v-if="cartStore.discountPercent > 0"
                  @click="removeCoupon"
                  class="px-4 py-2 border border-red-200 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 font-medium transition-colors"
                >
                  Xóa
                </button>
                <button
                  v-else
                  @click="applyCouponCode"
                  :disabled="isApplyingCoupon || !inputCoupon || cartStore.isEmpty"
                  class="px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 disabled:opacity-50 font-medium transition-colors whitespace-nowrap"
                >
                  <span v-if="isApplyingCoupon">Đang xử lý</span>
                  <span v-else>Áp dụng</span>
                </button>
              </div>
            </div>

            <!-- Price Summary -->
            <div class="space-y-3 mb-6">
              <div class="flex justify-between text-gray-600">
                <span>Tạm tính:</span>
                <span>{{ cartStore.totalPrice.toLocaleString('vi-VN') }}đ</span>
              </div>

              <div v-if="cartStore.discountPercent > 0" class="flex justify-between text-green-600">
                <span>Giảm {{ cartStore.discountPercent }}%:</span>
                <span>-{{ cartStore.discountAmount.toLocaleString('vi-VN') }}đ</span>
              </div>

              <div class="flex justify-between items-center pt-3 border-t border-gray-200">
                <span class="font-bold text-gray-900">Tổng cộng:</span>
                <span class="text-2xl font-bold text-blue-600">
                  {{ cartStore.finalPrice.toLocaleString('vi-VN') }}đ
                </span>
              </div>
            </div>

            <!-- Checkout Button -->
            <button
              :disabled="isProcessing || cartStore.isEmpty"
              @click="handleCheckout"
              class="w-full py-3 px-6 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold rounded-xl transition-all duration-200 flex items-center justify-center gap-2 shadow-lg shadow-blue-200"
            >
              <svg v-if="isProcessing" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              {{ isProcessing ? 'Đang tạo đơn hàng...' : 'Xác nhận & Thanh toán' }}
            </button>

            <!-- Back to Cart -->
            <button
              @click="navigateToCart"
              class="w-full mt-3 px-4 py-2 border border-gray-300 rounded-xl text-gray-700 font-medium hover:border-gray-400 hover:bg-gray-50 transition-colors"
            >
              Quay lại giỏ hàng
            </button>

            <!-- Security Badge -->
            <div class="mt-6 flex items-center justify-center gap-2 text-xs text-gray-500">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
              </svg>
              Thanh toán an toàn · Bảo vệ bởi SSL & VNPay
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <Footer />

    <!-- ── VNPay QR Modal ──────────────────────────────────────────────────── -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="showQrModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <!-- Backdrop -->
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="closeQrModal" />

          <!-- Modal card -->
          <div class="relative bg-white rounded-3xl shadow-2xl w-full max-w-sm overflow-hidden z-10">
            <!-- Header gradient -->
            <div class="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 pt-6 pb-8 text-center">
              <div class="flex justify-end mb-2">
                <button @click="closeQrModal" class="text-white/70 hover:text-white transition-colors">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>
              <img src="https://sandbox.vnpayment.vn/apis/assets/images/logo.svg" alt="VNPay" class="h-10 mx-auto mb-3 brightness-0 invert" onerror="this.style.display='none'" />
              <h3 class="text-xl font-bold text-white">Thanh toán VNPay</h3>
              <p class="text-blue-100 text-sm mt-1">Quét mã QR hoặc nhấn nút bên dưới</p>
            </div>

            <!-- Content -->
            <div class="px-6 py-5 -mt-4">
              <!-- QR Code area -->
              <div class="bg-white rounded-2xl shadow-lg p-4 border border-gray-100 mb-4">
                <div class="flex items-center justify-center">
                  <!-- QR code rendered via qrcode.js injected into canvas -->
                  <canvas ref="qrCanvas" class="rounded-xl" />
                </div>
                <p class="text-center text-xs text-gray-500 mt-2">Mở app ngân hàng → Quét mã</p>
              </div>

              <!-- Order info -->
              <div class="bg-gray-50 rounded-xl px-4 py-3 mb-4 space-y-2">
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">Mã đơn hàng</span>
                  <span class="font-semibold text-gray-900">{{ currentOrder?.orderCode }}</span>
                </div>
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">Khóa học</span>
                  <span class="font-semibold text-gray-900 text-right max-w-[180px] truncate">{{ currentOrder?.courseTitle }}</span>
                </div>
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">Số tiền</span>
                  <span class="font-bold text-blue-600 text-lg">
                    {{ currentOrder?.finalAmount?.toLocaleString('vi-VN') }}đ
                  </span>
                </div>
                <!-- Countdown -->
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">Hết hạn sau</span>
                  <span :class="countdown <= 60 ? 'text-red-500 font-bold' : 'text-orange-500 font-semibold'">
                    {{ formatCountdown(countdown) }}
                  </span>
                </div>
              </div>

              <!-- Action buttons -->
              <a
                :href="currentOrder?.paymentUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="block w-full py-3 bg-blue-600 hover:bg-blue-700 text-white text-center font-semibold rounded-xl transition-colors shadow-lg shadow-blue-200 mb-3"
              >
                Mở trang thanh toán VNPay →
              </a>

              <button
                @click="closeQrModal"
                class="w-full py-2.5 border border-gray-300 text-gray-600 rounded-xl hover:bg-gray-50 font-medium transition-colors text-sm"
              >
                Hủy đơn hàng này
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Navbar from '@/views/home/components/Navbar.vue'
import Footer from '@/views/home/components/Footer.vue'
import { useCartStore } from '@/stores/cart'
import { useToastStore } from '@/stores/toast'
import { createOrder, type CreateOrderResponse } from '@/api/orders'
import { validateCoupon } from '@/api/coupons'

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()
const toastStore = useToastStore()
const selectedPaymentMethod = ref<string>('VNPAY')
const isProcessing = ref<boolean>(false)

// Coupon state
const inputCoupon = ref<string>('')
const isApplyingCoupon = ref<boolean>(false)

// QR Modal state
const showQrModal = ref(false)
const currentOrder = ref<CreateOrderResponse | null>(null)
const qrCanvas = ref<HTMLCanvasElement | null>(null)
const countdown = ref(900) // 15 phút
let countdownTimer: ReturnType<typeof setInterval> | null = null

// Initialize cart from localStorage
cartStore.initializeCart()

// Payment methods
const paymentMethods = ref([
  {
    id: 'VNPAY',
    name: 'VNPay',
    description: 'Cổng thanh toán VNPay – Quét QR hoặc ATM/Visa/MasterCard',
    icon: '💳',
    logo: null,
  },
])

// Get user email (from auth store or session)
const userEmail = computed(() => {
  return authStore.user?.email || 'user@example.com'
})

// Format countdown mm:ss
function formatCountdown(seconds: number): string {
  const m = Math.floor(seconds / 60).toString().padStart(2, '0')
  const s = (seconds % 60).toString().padStart(2, '0')
  return `${m}:${s}`
}

// Generate QR code using qrcode.js (loaded from CDN)
async function generateQR(url: string) {
  await nextTick()
  const canvas = qrCanvas.value
  if (!canvas) return

  // Dynamically load qrcode.js if not present
  if (!(window as any).QRCode) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script')
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js'
      script.onload = () => resolve()
      script.onerror = reject
      document.head.appendChild(script)
    })
  }

  // Clear previous QR
  canvas.getContext('2d')?.clearRect(0, 0, canvas.width, canvas.height)
  const parent = canvas.parentElement!
  parent.innerHTML = '' // clear old canvas

  // Render new QR
  new (window as any).QRCode(parent, {
    text: url,
    width: 200,
    height: 200,
    colorDark: '#1e3a8a',
    colorLight: '#ffffff',
    correctLevel: (window as any).QRCode.CorrectLevel.H,
  })
}

// Start countdown timer
function startCountdown() {
  countdown.value = 900
  if (countdownTimer) clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer!)
      closeQrModal()
      toastStore.warning('Đơn hàng đã hết hạn. Vui lòng thử lại.')
    }
  }, 1000)
}

function closeQrModal() {
  showQrModal.value = false
  if (countdownTimer) clearInterval(countdownTimer)
}

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

// Handle checkout
const handleCheckout = async () => {
  if (cartStore.isEmpty) {
    toastStore.warning('Giỏ hàng trống')
    return
  }
  if (!authStore.isAuthenticated) {
    toastStore.warning('Vui lòng đăng nhập để thanh toán')
    router.push('/login')
    return
  }

  isProcessing.value = true

  try {
    // Backend chỉ hỗ trợ 1 course / order – lấy item đầu tiên
    const firstItem = cartStore.items[0]
    const orderResp = await createOrder({
      courseId: firstItem.id,
      paymentMethod: selectedPaymentMethod.value,
      couponCode: cartStore.discountCode || undefined
    })

    currentOrder.value = orderResp
    showQrModal.value = true
    startCountdown()

    // Render QR từ paymentUrl
    await generateQR(orderResp.paymentUrl)
  } catch (err: any) {
    const msg =
      err?.response?.data?.message || err?.message || 'Có lỗi xảy ra. Vui lòng thử lại.'
    toastStore.error(msg)
  } finally {
    isProcessing.value = false
  }
}

const navigateToCart = () => {
  router.push('/cart')
}

// Handle applying coupon
const applyCouponCode = async () => {
  const code = inputCoupon.value.trim().toUpperCase()
  if (!code) return
  
  isApplyingCoupon.value = true
  try {
    const res = await validateCoupon(code)
    cartStore.applyDiscount(res.data.code, res.data.discountPercent)
    toastStore.success(`Áp dụng thành công mã giảm giá ${res.data.discountPercent}%`)
  } catch (err: any) {
    console.error('Coupon Error:', err)
    const msg = err.response?.data?.message || 'Mã giảm giá không hợp lệ hoặc đã hết hạn'
    toastStore.error(msg)
    cartStore.removeDiscount()
  } finally {
    isApplyingCoupon.value = false
  }
}

const removeCoupon = () => {
  cartStore.removeDiscount()
  inputCoupon.value = ''
  toastStore.info('Đã gỡ mã giảm giá')
}
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.25s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
.modal-fade-enter-active > div:last-child,
.modal-fade-leave-active > div:last-child {
  transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}
.modal-fade-enter-from > div:last-child,
.modal-fade-leave-to > div:last-child {
  transform: scale(0.95) translateY(8px);
}
</style>
