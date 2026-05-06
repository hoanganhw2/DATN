<template>
  <AuthLayout>
    <!-- Title -->
    <div class="mb-8">
      <h2 class="text-3xl font-bold text-gray-900 mb-2">Đăng nhập</h2>
      <p class="text-gray-600">Chào mừng bạn trở lại! Đăng nhập để tiếp tục học tập</p>
    </div>

    <!-- Form -->
    <form @submit.prevent="handleLogin" class="space-y-5">
      <!-- Email/Username Field -->
      <div>
        <label for="email" class="block text-sm font-semibold text-gray-900 mb-2">
          Email hoặc Tên đăng nhập
        </label>
        <div class="relative">
          <div class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
              />
            </svg>
          </div>
          <input
            id="email"
            v-model="formData.email"
            type="text"
            placeholder="example@email.com"
            class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            :class="{ 'border-red-500 focus:ring-red-500': errors.email }"
          />
        </div>
        <p v-if="errors.email" class="mt-1.5 text-sm text-red-600 flex items-center gap-1">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
              clip-rule="evenodd"
            />
          </svg>
          {{ errors.email }}
        </p>
      </div>

      <!-- Password Field -->
      <div>
        <div class="flex items-center justify-between mb-2">
          <label for="password" class="block text-sm font-semibold text-gray-900"> Mật khẩu </label>
          <router-link
            to="/forgot-password"
            class="text-sm text-blue-600 hover:text-blue-700 font-medium"
          >
            Quên mật khẩu?
          </router-link>
        </div>
        <div class="relative">
          <div class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
              />
            </svg>
          </div>
          <input
            id="password"
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Nhập mật khẩu"
            class="w-full pl-12 pr-12 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            :class="{ 'border-red-500 focus:ring-red-500': errors.password }"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg
              v-if="showPassword"
              class="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-4.803m5.596-3.856a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0z"
              />
            </svg>
            <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
              />
            </svg>
          </button>
        </div>
        <p v-if="errors.password" class="mt-1.5 text-sm text-red-600 flex items-center gap-1">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
              clip-rule="evenodd"
            />
          </svg>
          {{ errors.password }}
        </p>
      </div>

      <!-- Remember Me Checkbox -->
      <div class="flex items-center">
        <input
          id="remember"
          v-model="formData.rememberMe"
          type="checkbox"
          class="w-4 h-4 rounded border-gray-300 text-blue-600 cursor-pointer"
        />
        <label for="remember" class="ml-2 text-sm text-gray-700 cursor-pointer">
          Ghi nhớ tôi
        </label>
      </div>

      <!-- Login Button -->
      <BaseButton variant="primary" size="lg" type="submit" class="w-full" :loading="isLoading">
        Đăng nhập
      </BaseButton>
    </form>

    <!-- Divider -->
    <div class="my-6 flex items-center gap-3">
      <div class="flex-1 h-px bg-gray-200"></div>
      <span class="text-sm text-gray-500">hoặc</span>
      <div class="flex-1 h-px bg-gray-200"></div>
    </div>

    <!-- Social Login (Optional) -->
    <div class="space-y-3">
      <button
        type="button"
        class="w-full flex items-center justify-center gap-3 px-4 py-3 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors font-medium text-gray-700"
      >
        <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
          <path
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
            fill="#4285F4"
          />
          <path
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
            fill="#34A853"
          />
          <path
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
            fill="#FBBC05"
          />
          <path
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
            fill="#EA4335"
          />
        </svg>
        Đăng nhập với Google
      </button>
    </div>

    <!-- Sign Up Link -->
    <div class="mt-8 text-center">
      <p class="text-gray-700">
        Chưa có tài khoản?
        <router-link to="/register" class="font-semibold text-blue-600 hover:text-blue-700">
          Đăng ký ngay
        </router-link>
      </p>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BaseButton from '@/components/BaseButton.vue'

interface FormData {
  email: string
  password: string
  rememberMe: boolean
}

interface Errors {
  email?: string
  password?: string
}

const router = useRouter()
const authStore = useAuthStore()

const formData = reactive<FormData>({
  email: '',
  password: '',
  rememberMe: false,
})

const errors = reactive<Errors>({})
const isLoading = ref(false)
const showPassword = ref(false)

// Validation functions
const validateEmail = (email: string): string | null => {
  if (!email.trim()) {
    return 'Email không được để trống'
  }
  if (!isValidEmail(email) && !isValidUsername(email)) {
    return 'Vui lòng nhập email hoặc tên đăng nhập hợp lệ'
  }
  return null
}

const validatePassword = (password: string): string | null => {
  if (!password) {
    return 'Mật khẩu không được để trống'
  }
  if (password.length < 6) {
    return 'Mật khẩu phải có ít nhất 6 ký tự'
  }
  return null
}

const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const isValidUsername = (username: string): boolean => {
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  return usernameRegex.test(username)
}

// Handle Login
const handleLogin = async () => {
  // Reset errors
  errors.email = undefined
  errors.password = undefined

  // Validate
  const emailError = validateEmail(formData.email)
  const passwordError = validatePassword(formData.password)

  if (emailError) errors.email = emailError
  if (passwordError) errors.password = passwordError

  if (emailError || passwordError) {
    return
  }

  isLoading.value = true

  try {
    await authStore.login({
      email: formData.email,
      password: formData.password,
    })

    // Success - redirect to home
    router.push('/')
  } catch (error: any) {
    const message = error.response?.data?.message || 'Đăng nhập thất bại'

    // Show error based on type
    if (message.toLowerCase().includes('email') || message.toLowerCase().includes('không tìm')) {
      errors.email = 'Email hoặc tên đăng nhập không được tìm thấy'
    } else if (
      message.toLowerCase().includes('password') ||
      message.toLowerCase().includes('mật khẩu')
    ) {
      errors.password = 'Mật khẩu không chính xác'
    } else if (message.toLowerCase().includes('khóa') || message.toLowerCase().includes('locked')) {
      errors.email = 'Tài khoản của bạn đã bị khóa. Vui lòng liên hệ hỗ trợ'
    } else {
      errors.email = message
    }

    console.error('Login error:', error)
  } finally {
    isLoading.value = false
  }
}
</script>
