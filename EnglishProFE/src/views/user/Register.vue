<template>
  <AuthLayout>
    <!-- Title -->
    <div class="mb-8">
      <h2 class="text-3xl font-bold text-gray-900 mb-2">Tạo tài khoản</h2>
      <p class="text-gray-600">Bắt đầu hành trình học tiếng Anh của bạn ngay hôm nay</p>
    </div>

    <!-- Success Message -->
    <transition name="fade">
      <div
        v-if="successMessage"
        class="mb-6 p-4 bg-green-50 border border-green-200 rounded-xl flex items-center gap-3"
      >
        <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
          <path
            fill-rule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
            clip-rule="evenodd"
          />
        </svg>
        <span class="text-green-800 font-medium">{{ successMessage }}</span>
      </div>
    </transition>

    <!-- Form -->
    <form @submit.prevent="handleRegister" class="space-y-4">
      <!-- Full Name Field -->
      <div>
        <label for="fullName" class="block text-sm font-semibold text-gray-900 mb-2">
          Họ và Tên <span class="text-red-500">*</span>
        </label>
        <input
          id="fullName"
          v-model="formData.fullName"
          type="text"
          placeholder="Ví dụ: Nguyễn Văn A"
          class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
          :class="{ 'border-red-500 focus:ring-red-500': errors.fullName }"
        />
        <p v-if="errors.fullName" class="mt-1.5 text-sm text-red-600">{{ errors.fullName }}</p>
      </div>

      <!-- Email Field -->
      <div>
        <label for="email" class="block text-sm font-semibold text-gray-900 mb-2">
          Email <span class="text-red-500">*</span>
        </label>
        <input
          id="email"
          v-model="formData.email"
          type="email"
          placeholder="your@email.com"
          class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
          :class="{ 'border-red-500 focus:ring-red-500': errors.email }"
        />
        <p v-if="errors.email" class="mt-1.5 text-sm text-red-600">{{ errors.email }}</p>
      </div>

      <!-- Username Field -->
      <div>
        <label for="username" class="block text-sm font-semibold text-gray-900 mb-2">
          Tên đăng nhập <span class="text-red-500">*</span>
        </label>
        <input
          id="username"
          v-model="formData.username"
          type="text"
          placeholder="Chữ cái, số, dấu gạch dưới (3-20 ký tự)"
          class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
          :class="{ 'border-red-500 focus:ring-red-500': errors.username }"
        />
        <p v-if="errors.username" class="mt-1.5 text-sm text-red-600">{{ errors.username }}</p>
      </div>

      <!-- Phone Field -->
      <div>
        <label for="phone" class="block text-sm font-semibold text-gray-900 mb-2">
          Số điện thoại <span class="text-red-500">*</span>
        </label>
        <input
          id="phone"
          v-model="formData.phone"
          type="tel"
          placeholder="+84 123 456 789"
          class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
          :class="{ 'border-red-500 focus:ring-red-500': errors.phone }"
        />
        <p v-if="errors.phone" class="mt-1.5 text-sm text-red-600">{{ errors.phone }}</p>
      </div>

      <!-- Password Field -->
      <div>
        <label for="password" class="block text-sm font-semibold text-gray-900 mb-2">
          Mật khẩu <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <input
            id="password"
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Tối thiểu 8 ký tự, có chữ hoa, số, ký tự đặc biệt"
            class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all pr-12"
            :class="{ 'border-red-500 focus:ring-red-500': errors.password }"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
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
        <p v-if="errors.password" class="mt-1.5 text-sm text-red-600">{{ errors.password }}</p>

        <!-- Password Requirements -->
        <div class="mt-3 space-y-2">
          <div
            class="flex items-center gap-2 text-sm"
            :class="passwordChecks.minLength ? 'text-green-600' : 'text-gray-600'"
          >
            <svg
              v-if="passwordChecks.minLength"
              class="w-4 h-4"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clip-rule="evenodd"
              />
            </svg>
            <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              />
            </svg>
            Tối thiểu 8 ký tự
          </div>
          <div
            class="flex items-center gap-2 text-sm"
            :class="passwordChecks.hasUpperCase ? 'text-green-600' : 'text-gray-600'"
          >
            <svg
              v-if="passwordChecks.hasUpperCase"
              class="w-4 h-4"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clip-rule="evenodd"
              />
            </svg>
            <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              />
            </svg>
            Chứa chữ cái in hoa (A-Z)
          </div>
          <div
            class="flex items-center gap-2 text-sm"
            :class="passwordChecks.hasNumber ? 'text-green-600' : 'text-gray-600'"
          >
            <svg
              v-if="passwordChecks.hasNumber"
              class="w-4 h-4"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clip-rule="evenodd"
              />
            </svg>
            <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              />
            </svg>
            Chứa số (0-9)
          </div>
          <div
            class="flex items-center gap-2 text-sm"
            :class="passwordChecks.hasSpecial ? 'text-green-600' : 'text-gray-600'"
          >
            <svg
              v-if="passwordChecks.hasSpecial"
              class="w-4 h-4"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clip-rule="evenodd"
              />
            </svg>
            <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              />
            </svg>
            Ký tự đặc biệt (!@#$%^&*)
          </div>
        </div>
      </div>

      <!-- Role Selection (Nguyen Tac) -->
      <div>
        <label class="block text-sm font-semibold text-gray-900 mb-3">
          Bạn muốn đăng ký là <span class="text-red-500">*</span>
        </label>
        <div class="space-y-2">
          <label
            class="flex items-center p-4 border-2 rounded-xl cursor-pointer transition-all"
            :class="
              formData.role === 'STUDENT'
                ? 'border-blue-600 bg-blue-50'
                : 'border-gray-300 hover:border-gray-400'
            "
          >
            <input
              v-model="formData.role"
              type="radio"
              value="STUDENT"
              class="w-4 h-4 text-blue-600 cursor-pointer"
            />
            <div class="ml-3">
              <p class="font-semibold text-gray-900">Học viên</p>
              <p class="text-sm text-gray-600">Tôi muốn học tiếng Anh</p>
            </div>
          </label>

          <label
            class="flex items-center p-4 border-2 rounded-xl cursor-pointer transition-all"
            :class="
              formData.role === 'TEACHER'
                ? 'border-blue-600 bg-blue-50'
                : 'border-gray-300 hover:border-gray-400'
            "
          >
            <input
              v-model="formData.role"
              type="radio"
              value="TEACHER"
              class="w-4 h-4 text-blue-600 cursor-pointer"
            />
            <div class="ml-3">
              <p class="font-semibold text-gray-900">Giáo viên</p>
              <p class="text-sm text-gray-600">Tôi muốn dạy tiếng Anh</p>
            </div>
          </label>
        </div>
        <p v-if="errors.role" class="mt-1.5 text-sm text-red-600">{{ errors.role }}</p>
      </div>

      <!-- Terms & Conditions -->
      <div class="flex items-start gap-2">
        <input
          id="terms"
          v-model="formData.agreeToTerms"
          type="checkbox"
          class="w-4 h-4 mt-1 rounded border-gray-300 text-blue-600 cursor-pointer"
        />
        <label for="terms" class="text-sm text-gray-700 cursor-pointer">
          Tôi đồng ý với
          <router-link to="/terms" class="text-blue-600 hover:underline"
            >Điều khoản dịch vụ</router-link
          >
          và
          <router-link to="/privacy" class="text-blue-600 hover:underline"
            >Chính sách riêng tư</router-link
          >
        </label>
      </div>
      <p v-if="errors.agreeToTerms" class="text-sm text-red-600">{{ errors.agreeToTerms }}</p>

      <!-- Register Button -->
      <BaseButton
        variant="primary"
        size="lg"
        type="submit"
        class="w-full mt-6"
        :loading="isLoading"
        :disabled="!formData.agreeToTerms || isLoading"
      >
        Tạo tài khoản
      </BaseButton>
    </form>

    <!-- Sign In Link -->
    <div class="mt-8 text-center">
      <p class="text-gray-700">
        Đã có tài khoản?
        <router-link to="/login" class="font-semibold text-blue-600 hover:text-blue-700">
          Đăng nhập ngay
        </router-link>
      </p>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BaseButton from '@/components/BaseButton.vue'

interface FormData {
  fullName: string
  email: string
  username: string
  phone: string
  password: string
  role: 'STUDENT' | 'TEACHER'
  agreeToTerms: boolean
}

interface Errors {
  fullName?: string
  email?: string
  username?: string
  phone?: string
  password?: string
  role?: string
  agreeToTerms?: string
}

const router = useRouter()
const authStore = useAuthStore()

const formData = reactive<FormData>({
  fullName: '',
  email: '',
  username: '',
  phone: '',
  password: '',
  role: 'STUDENT',
  agreeToTerms: false,
})

const errors = reactive<Errors>({})
const isLoading = ref(false)
const showPassword = ref(false)
const successMessage = ref('')

// Password validation checks
const passwordChecks = computed(() => ({
  minLength: formData.password.length >= 8,
  hasUpperCase: /[A-Z]/.test(formData.password),
  hasNumber: /[0-9]/.test(formData.password),
  hasSpecial: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(formData.password),
}))

// Validation functions
const validateFullName = (name: string): string | null => {
  if (!name.trim()) return 'Họ tên không được để trống'
  if (name.trim().length < 3) return 'Họ tên phải có ít nhất 3 ký tự'
  return null
}

const validateEmail = (email: string): string | null => {
  if (!email.trim()) return 'Email không được để trống'
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) return 'Email không hợp lệ'
  return null
}

const validateUsername = (username: string): string | null => {
  if (!username.trim()) return 'Tên đăng nhập không được để trống'
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/
  if (!usernameRegex.test(username)) {
    return 'Tên đăng nhập: 3-20 ký tự, chỉ chứa chữ, số, dấu gạch dưới'
  }
  return null
}

const validatePhone = (phone: string): string | null => {
  if (!phone.trim()) return 'Số điện thoại không được để trống'
  const phoneRegex = /^[0-9+\s\-()]{10,}$/
  if (!phoneRegex.test(phone)) return 'Số điện thoại không hợp lệ'
  return null
}

const validatePassword = (password: string): string | null => {
  if (!password) return 'Mật khẩu không được để trống'
  if (password.length < 8) return 'Mật khẩu phải có ít nhất 8 ký tự'
  if (!/[A-Z]/.test(password)) return 'Mật khẩu phải chứa ít nhất một chữ cái in hoa'
  if (!/[0-9]/.test(password)) return 'Mật khẩu phải chứa ít nhất một số'
  if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
    return 'Mật khẩu phải chứa ít nhất một ký tự đặc biệt'
  }
  return null
}

// Handle Register
const handleRegister = async () => {
  // Reset errors
  Object.keys(errors).forEach((key) => {
    delete errors[key as keyof Errors]
  })

  // Validate
  const fullNameError = validateFullName(formData.fullName)
  const emailError = validateEmail(formData.email)
  const usernameError = validateUsername(formData.username)
  const phoneError = validatePhone(formData.phone)
  const passwordError = validatePassword(formData.password)

  if (fullNameError) errors.fullName = fullNameError
  if (emailError) errors.email = emailError
  if (usernameError) errors.username = usernameError
  if (phoneError) errors.phone = phoneError
  if (passwordError) errors.password = passwordError
  if (!formData.agreeToTerms) errors.agreeToTerms = 'Bạn phải đồng ý với điều khoản dịch vụ'

  if (Object.keys(errors).length > 0) {
    return
  }

  isLoading.value = true

  try {
    await authStore.register({
      fullName: formData.fullName,
      email: formData.email,
      username: formData.username,
      phone: formData.phone,
      password: formData.password,
      role: formData.role,
    })

    // Success - show message and redirect
    successMessage.value = '✓ Đăng ký thành công! Đang chuyển hướng...'

    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    const message = error.response?.data?.message || 'Đăng ký thất bại'

    if (message.toLowerCase().includes('email')) {
      errors.email = 'Email đã được sử dụng'
    } else if (message.toLowerCase().includes('username')) {
      errors.username = 'Tên đăng nhập đã tồn tại'
    } else {
      errors.fullName = message
    }

    console.error('Register error:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
