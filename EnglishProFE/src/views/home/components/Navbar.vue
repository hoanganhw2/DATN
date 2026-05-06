<template>
  <nav class="sticky top-0 z-50 bg-white border-b border-gray-200 shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center gap-2">
          <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
            <span class="text-white font-bold text-lg">EP</span>
          </div>
          <span class="font-bold text-xl text-gray-900 hidden sm:inline">EnglishPro</span>
        </router-link>

        <!-- Desktop Menu -->
        <div class="hidden md:flex items-center gap-8">
          <a
            href="#courses"
            class="text-gray-700 hover:text-blue-600 font-medium transition-colors"
          >
            Khóa học
          </a>
          <a
            href="#features"
            class="text-gray-700 hover:text-blue-600 font-medium transition-colors"
          >
            Luyện thi
          </a>
          <a
            href="#features"
            class="text-gray-700 hover:text-blue-600 font-medium transition-colors"
          >
            Flashcards
          </a>
        </div>

        <!-- Auth Section -->
        <div class="flex items-center gap-3">
          <!-- Cart Icon -->
          <router-link
            to="/cart"
            class="relative p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all duration-200"
            title="Giỏ hàng"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <span
              v-if="cartStore.itemCount > 0"
              class="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1 shadow-sm"
            >
              {{ cartStore.itemCount > 9 ? '9+' : cartStore.itemCount }}
            </span>
          </router-link>

          <template v-if="isAuthenticated">
            <!-- User Avatar -->
            <router-link
              to="/profile"
              class="flex items-center gap-2 cursor-pointer hover:bg-gray-100 px-3 py-2 rounded-lg transition-colors"
            >
              <img
                v-if="user?.avatar"
                :src="user.avatar"
                :alt="user.fullName"
                class="w-8 h-8 rounded-full object-cover"
              />
              <div v-else class="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center">
                <span class="text-white font-bold text-sm">{{ getInitials() }}</span>
              </div>
              <span class="text-gray-900 font-medium hidden sm:inline">{{ user?.fullName }}</span>
            </router-link>
          </template>
          <template v-else>
            <!-- Auth Buttons -->
            <router-link to="/login">
              <BaseButton variant="ghost" size="md"> Đăng nhập </BaseButton>
            </router-link>
            <router-link to="/register">
              <BaseButton variant="primary" size="md"> Đăng ký </BaseButton>
            </router-link>
          </template>
        </div>

        <!-- Mobile Menu Toggle -->
        <button
          class="md:hidden p-2 hover:bg-gray-100 rounded-lg transition-colors"
          @click="mobileMenuOpen = !mobileMenuOpen"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h16M4 18h16"
            />
          </svg>
        </button>
      </div>

      <!-- Mobile Menu -->
      <div v-if="mobileMenuOpen" class="md:hidden pb-4 border-t border-gray-200">
        <a href="#courses" class="block px-4 py-2 text-gray-700 hover:text-blue-600 font-medium">
          Khóa học
        </a>
        <a href="#features" class="block px-4 py-2 text-gray-700 hover:text-blue-600 font-medium">
          Luyện thi
        </a>
        <a href="#features" class="block px-4 py-2 text-gray-700 hover:text-blue-600 font-medium">
          Flashcards
        </a>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import BaseButton from '@/components/BaseButton.vue'

const authStore = useAuthStore()
const cartStore = useCartStore()
const mobileMenuOpen = ref(false)

onMounted(() => cartStore.initializeCart())

const isAuthenticated = computed(() => authStore.isAuthenticated)
const user = computed(() => authStore.getCurrentUser)

const getInitials = () => {
  const name = user.value?.fullName || 'U'
  return name
    .split(' ')
    .map((n: string) => n[0])
    .join('')
    .toUpperCase()
    .substring(0, 2)
}
</script>
