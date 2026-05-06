<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-gray-900">Giỏ hàng của bạn</h1>
        <p class="text-gray-600 mt-2">{{ cartStore.itemCount }} khóa học</p>
      </div>

      <!-- Empty State -->
      <div v-if="cartStore.isEmpty" class="flex items-center justify-center min-h-[60vh]">
        <div class="text-center">
          <div class="mb-6">
            <svg
              class="w-24 h-24 mx-auto text-gray-300"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="1.5"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Giỏ hàng của bạn trống</h2>
          <p class="text-gray-600 mb-8">Hãy thêm các khóa học yêu thích để bắt đầu học tập</p>
          <BaseButton variant="primary" size="lg" @click="navigateToCourses">
            Khám phá khóa học ngay
          </BaseButton>
        </div>
      </div>

      <!-- Cart Content -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Left Column: Product List -->
        <div class="lg:col-span-2">
          <div class="space-y-4">
            <div
              v-for="item in cartStore.items"
              :key="item.id"
              class="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow"
            >
              <div class="flex gap-4 p-4">
                <!-- Thumbnail -->
                <div class="flex-shrink-0">
                  <img
                    :src="item.image"
                    :alt="item.title"
                    class="w-32 h-24 object-cover rounded-lg"
                  />
                </div>

                <!-- Product Info -->
                <div class="flex-1 flex flex-col justify-between">
                  <div>
                    <h3 class="text-lg font-bold text-gray-900 line-clamp-2 mb-1">
                      {{ item.title }}
                    </h3>
                    <p class="text-sm text-gray-600">{{ item.teacher }}</p>
                  </div>

                  <!-- Price -->
                  <div class="flex items-center justify-between">
                    <span class="text-xl font-bold text-blue-600">
                      {{ item.price.toLocaleString('vi-VN') }}đ
                    </span>

                    <!-- Remove Button -->
                    <button
                      @click="handleRemoveFromCart(item.id, item.title)"
                      class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                      title="Xóa khỏi giỏ"
                    >
                      <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                        <path
                          fill-rule="evenodd"
                          d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                          clip-rule="evenodd"
                        />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column: Order Summary -->
        <div class="lg:col-span-1">
          <div class="sticky top-4 bg-white rounded-xl border border-gray-200 p-6 shadow-md">
            <!-- Summary Title -->
            <h2 class="text-xl font-bold text-gray-900 mb-6">Tóm tắt đơn hàng</h2>

            <!-- Price Items -->
            <div class="space-y-4 pb-4 border-b border-gray-200">
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Tạm tính:</span>
                <span class="font-semibold text-gray-900">
                  {{ cartStore.totalPrice.toLocaleString('vi-VN') }}đ
                </span>
              </div>

            </div>

            <!-- Final Price -->
            <div class="py-4 mb-6">
              <div class="flex justify-between items-center">
                <span class="text-lg font-bold text-gray-900">Tổng cộng:</span>
                <span class="text-2xl font-bold text-blue-600">
                  {{ cartStore.totalPrice.toLocaleString('vi-VN') }}đ
                </span>
              </div>
            </div>

            <!-- Checkout Button -->
            <BaseButton variant="primary" size="lg" class="w-full" @click="navigateToCheckout">
              Thanh toán ngay
            </BaseButton>

            <!-- Continue Shopping -->
            <button
              @click="navigateToCourses"
              class="w-full mt-3 px-4 py-2 border border-gray-300 rounded-lg text-gray-900 font-medium hover:border-gray-400 transition-colors"
            >
              Tiếp tục mua sắm
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/views/home/components/Navbar.vue'
import Footer from '@/views/home/components/Footer.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useCartStore } from '@/stores/cart'
import { useToastStore } from '@/stores/toast'

const router = useRouter()
const cartStore = useCartStore()
const toastStore = useToastStore()

// Initialize cart from localStorage
cartStore.initializeCart()

const handleRemoveFromCart = (id: number, title: string) => {
  cartStore.removeFromCart(id)
  toastStore.info(`Đã xóa "${title}" khỏi giỏ hàng`)
}



const navigateToCheckout = () => {
  router.push('/checkout')
}

const navigateToCourses = () => {
  router.push('/courses')
}
</script>
