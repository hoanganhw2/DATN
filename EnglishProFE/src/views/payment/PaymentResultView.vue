<template>
  <div class="flex flex-col min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="flex-1 flex items-center justify-center px-4 py-8">
      <div class="w-full max-w-md">

        <!-- Loading / Processing State -->
        <Transition name="fade" mode="out-in">
          <div v-if="isLoading" key="loading" class="bg-white rounded-3xl p-8 text-center shadow-xl border border-gray-100">
            <div class="mb-6 flex justify-center">
              <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center">
                <svg class="w-10 h-10 text-blue-600 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
                </svg>
              </div>
            </div>
            <h1 class="text-2xl font-bold text-gray-900 mb-2">Đang xác minh thanh toán...</h1>
            <p class="text-gray-600">Vui lòng không đóng cửa sổ này.</p>
          </div>

          <!-- Success State -->
          <div v-else-if="paymentStatus === 'success'" key="success" class="animate-fadeInScale">
            <div class="bg-white rounded-3xl p-8 text-center shadow-xl border border-gray-100 overflow-hidden relative">
              <!-- Confetti top bar -->
              <div class="absolute top-0 left-0 right-0 h-2 bg-gradient-to-r from-green-400 via-emerald-500 to-teal-400"/>

              <!-- Success Icon -->
              <div class="mb-6 flex justify-center mt-4">
                <div class="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center animate-scaleIn shadow-lg shadow-green-100">
                  <svg class="w-12 h-12 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                </div>
              </div>

              <h1 class="text-3xl font-bold text-gray-900 mb-2">Thanh toán thành công! 🎉</h1>
              <p class="text-gray-600 mb-6">
                Khóa học đã được kích hoạt. Chúc bạn học tốt!
              </p>

              <!-- Order Details -->
              <div class="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl p-4 mb-6 text-left border border-green-100">
                <div class="space-y-3">
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-500">Mã đơn hàng</span>
                    <span class="font-bold text-gray-900">{{ orderCode }}</span>
                  </div>
                  <div v-if="transactionNo" class="flex justify-between text-sm">
                    <span class="text-gray-500">Mã giao dịch</span>
                    <span class="font-semibold text-gray-900">{{ transactionNo }}</span>
                  </div>
                  <div v-if="amount > 0" class="flex justify-between text-sm">
                    <span class="text-gray-500">Số tiền</span>
                    <span class="font-bold text-green-700">{{ amount.toLocaleString('vi-VN') }}đ</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-500">Phương thức</span>
                    <span class="font-semibold text-gray-900">VNPay</span>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="space-y-3">
                <button
                  @click="navigateToDashboard"
                  class="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-colors shadow-lg shadow-blue-200"
                >
                  Vào lớp học ngay →
                </button>
                <button
                  @click="navigateToCourses"
                  class="w-full py-2.5 border border-gray-300 text-gray-600 rounded-xl hover:bg-gray-50 font-medium transition-colors text-sm"
                >
                  Khám phá thêm khóa học
                </button>
              </div>
            </div>
          </div>

          <!-- Failure State -->
          <div v-else-if="paymentStatus === 'failure'" key="failure" class="animate-fadeInScale">
            <div class="bg-white rounded-3xl p-8 text-center shadow-xl border border-gray-100 overflow-hidden relative">
              <!-- Error top bar -->
              <div class="absolute top-0 left-0 right-0 h-2 bg-gradient-to-r from-red-400 to-rose-500"/>

              <!-- Error Icon -->
              <div class="mb-6 flex justify-center mt-4">
                <div class="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center animate-scaleIn shadow-lg shadow-red-100">
                  <svg class="w-12 h-12 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                  </svg>
                </div>
              </div>

              <h1 class="text-3xl font-bold text-gray-900 mb-2">Thanh toán thất bại</h1>
              <p class="text-gray-600 mb-4">
                Giao dịch không được hoàn tất. Vui lòng thử lại.
              </p>

              <!-- Error info box -->
              <div class="bg-red-50 rounded-xl p-4 mb-6 text-left border border-red-100">
                <div class="space-y-2 text-sm">
                  <div v-if="orderCode" class="flex justify-between">
                    <span class="text-gray-500">Mã đơn hàng</span>
                    <span class="font-semibold text-gray-900">{{ orderCode }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-500">Mã lỗi</span>
                    <span class="font-semibold text-red-700">{{ responseCode }}</span>
                  </div>
                  <p class="text-red-700 text-xs mt-1">{{ vnpayErrorMessage }}</p>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="space-y-3">
                <button
                  @click="navigateToCheckout"
                  class="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-colors"
                >
                  Thử lại
                </button>
                <button
                  @click="navigateToCart"
                  class="w-full py-2.5 border border-gray-300 text-gray-600 rounded-xl hover:bg-gray-50 font-medium transition-colors text-sm"
                >
                  Quay lại giỏ hàng
                </button>
              </div>
            </div>
          </div>
        </Transition>

        <!-- Support footer -->
        <p class="text-center text-xs text-gray-500 mt-6">
          Cần hỗ trợ?
          <a href="mailto:support@englishpro.vn" class="text-blue-600 hover:underline">Liên hệ chúng tôi</a>
        </p>
      </div>
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import Navbar from '@/views/home/components/Navbar.vue'
import Footer from '@/views/home/components/Footer.vue'
import { verifyVNPayReturn } from '@/api/orders'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

// State
const isLoading = ref(true)
const paymentStatus = ref<'success' | 'failure' | null>(null)
const orderCode = ref('')
const transactionNo = ref('')
const amount = ref(0)
const responseCode = ref('')

// VNPay error code descriptions
const vnpayErrorMessages: Record<string, string> = {
  '07': 'Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).',
  '09': 'Thẻ/Tài khoản chưa đăng ký InternetBanking tại ngân hàng.',
  '10': 'Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.',
  '11': 'Đã hết hạn chờ thanh toán. Vui lòng thực hiện lại giao dịch.',
  '12': 'Thẻ/Tài khoản bị khóa.',
  '13': 'Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).',
  '24': 'Khách hàng hủy giao dịch.',
  '51': 'Tài khoản không đủ số dư để thực hiện giao dịch.',
  '65': 'Tài khoản vượt quá hạn mức giao dịch trong ngày.',
  '75': 'Ngân hàng thanh toán đang bảo trì.',
  '79': 'Khách hàng nhập sai mật khẩu thanh toán quá số lần quy định.',
  '99': 'Lỗi không xác định. Vui lòng liên hệ hỗ trợ.',
}

const vnpayErrorMessage = computed(() => {
  return vnpayErrorMessages[responseCode.value] || `Giao dịch thất bại (mã ${responseCode.value})`
})

onMounted(async () => {
  const queryParams = route.query as Record<string, string>

  // Nếu không có params VNPay → đây là URL trực tiếp, không phải redirect từ VNPay
  if (!queryParams.vnp_ResponseCode) {
    // Check session storage (từ luồng cũ)
    const storedStatus = sessionStorage.getItem('paymentStatus')
    if (storedStatus === 'success') {
      paymentStatus.value = 'success'
    } else {
      paymentStatus.value = 'failure'
    }
    isLoading.value = false
    return
  }

  // Có params VNPay → xác minh với backend
  try {
    const rawQuery = window.location.search.substring(1)
    const result = await verifyVNPayReturn(rawQuery)
    orderCode.value = result.orderCode
    transactionNo.value = result.transactionNo
    amount.value = result.amount
    responseCode.value = result.responseCode

    if (result.success) {
      paymentStatus.value = 'success'
      cartStore.clearCart()
    } else {
      paymentStatus.value = 'failure'
    }
  } catch (err) {
    console.error('Payment verification error:', err)
    // Fallback: đọc trực tiếp từ params nếu backend lỗi
    responseCode.value = queryParams.vnp_ResponseCode || '99'
    orderCode.value = queryParams.vnp_TxnRef || ''
    transactionNo.value = queryParams.vnp_TransactionNo || ''
    try { amount.value = parseInt(queryParams.vnp_Amount || '0') / 100 } catch {}

    paymentStatus.value = responseCode.value === '00' ? 'success' : 'failure'
    if (paymentStatus.value === 'success') cartStore.clearCart()
  } finally {
    isLoading.value = false
  }
})

// Navigation
const navigateToDashboard = () => router.push('/dashboard/student')
const navigateToCourses = () => router.push('/courses')
const navigateToCheckout = () => router.push('/checkout')
const navigateToCart = () => router.push('/cart')
</script>

<style scoped>
@keyframes fadeInScale {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to   { opacity: 1; transform: scale(1) translateY(0);       }
}
@keyframes scaleIn {
  from { transform: scale(0); }
  to   { transform: scale(1); }
}

.animate-fadeInScale { animation: fadeInScale 0.5s ease-out; }
.animate-scaleIn     { animation: scaleIn 0.6s cubic-bezier(0.16, 1, 0.3, 1); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to       { opacity: 0;                     }
</style>
