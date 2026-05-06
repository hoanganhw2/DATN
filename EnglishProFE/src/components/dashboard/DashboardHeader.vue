<template>
  <header
    class="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center shadow-sm"
  >
    <div>
      <h1 class="text-2xl font-bold text-gray-900">{{ pageTitle }}</h1>
      <p class="text-sm text-gray-600 mt-1">{{ pageSubtitle }}</p>
    </div>

    <div class="flex items-center space-x-4">
      <!-- Profile Menu -->
      <div class="relative">
        <button
          @click="showMenu = !showMenu"
          class="flex items-center space-x-2 p-2 hover:bg-gray-100 rounded-lg transition"
        >
          <img
            :src="getAvatar(authStore.user?.fullName || 'User')"
            :alt="authStore.user?.fullName"
            class="w-8 h-8 rounded-full"
          />
          <ChevronDownIcon class="w-4 h-4 text-gray-600" />
        </button>

        <!-- Dropdown Menu -->
        <div
          v-if="showMenu"
          class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 overflow-hidden"
        >
          <router-link
            to="/profile"
            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition"
          >
            Hồ sơ cá nhân
          </router-link>
          <button
            @click="logout"
            class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition"
          >
            Đăng xuất
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import BellIcon from './icons/BellIcon.vue'
import SunIcon from './icons/SunIcon.vue'
import ChevronDownIcon from './icons/ChevronDownIcon.vue'
import ShoppingCartIcon from './icons/ShoppingCartIcon.vue'

const props = defineProps<{
  isCollapsed?: boolean
}>()

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
cartStore.initializeCart()
const showMenu = ref(false)

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/dashboard/admin': 'Trang tổng quan',
    '/dashboard/admin/users': 'Quản lý người dùng',
    '/dashboard/admin/orders': 'Quản lý đơn hàng',
    '/dashboard/admin/revenue': 'Thống kê doanh thu',
    '/dashboard/admin/course-approvals': 'Duyệt khóa học',
    '/dashboard/admin/coupons': 'Mã giảm giá',
    '/dashboard/admin/exams/create': 'Tạo đề thi mới',
    '/dashboard/admin/exams': 'Ngân hàng đề thi',
    '/dashboard/teacher': 'Trang tổng quan',
    '/dashboard/teacher/courses': 'Khóa học của tôi',
    '/dashboard/teacher/students': 'Danh sách học viên',
    '/dashboard/teacher/revenue': 'Doanh thu của tôi',
    '/dashboard/teacher/coupons': 'Mã giảm giá',
    '/dashboard/teacher/exams/create': 'Tạo đề thi mới',
    '/dashboard/teacher/exams': 'Quản lý đề thi',
    '/dashboard/student': 'Trang tổng quan',
    '/dashboard/student/courses': 'Khóa học của tôi',
    '/dashboard/student/orders': 'Lịch sử đơn hàng',
    '/dashboard/student/certificates': 'Chứng chỉ của tôi',
    '/flashcard/create': 'Tạo bộ thẻ mới',
    '/flashcard/deck': 'Chi tiết bộ thẻ',
    '/flashcard': 'Bộ thẻ học tập',
    '/exam': 'Bài kiểm tra',
    '/profile': 'Hồ sơ cá nhân',
  }

  // Sort paths by length descending to match more specific routes first
  const sortedPaths = Object.keys(titles).sort((a, b) => b.length - a.length)

  for (const path of sortedPaths) {
    if (router.currentRoute.value.path.startsWith(path)) {
      return titles[path]
    }
  }

  return 'Dashboard'
})

const pageSubtitle = computed(() => {
  const subtitles: Record<string, string> = {
    '/dashboard/admin': 'Tổng quan hệ thống',
    '/dashboard/admin/users': 'Quản lý tài khoản người dùng',
    '/dashboard/admin/orders': 'Quản lý các giao dịch thanh toán',
    '/dashboard/admin/revenue': 'Phân tích tài chính toàn hệ thống (Teacher 70% / Admin 30%)',
    '/dashboard/admin/course-approvals':
      'Danh sách khóa học do giảng viên gửi lên đang chờ admin duyệt.',
    '/dashboard/admin/coupons': 'Quản lý mã giảm giá toàn hệ thống',
    '/dashboard/admin/exams/create': 'Soạn đề thi TOEIC/IELTS mới cho hệ thống',
    '/dashboard/admin/exams': 'Quản lý kho đề thi chính thức của hệ thống',
    '/dashboard/teacher': 'Thống kê khóa học và học viên',
    '/dashboard/teacher/courses': 'Danh sách và quản lý khóa học',
    '/dashboard/teacher/students': 'Học viên đã đăng ký',
    '/dashboard/teacher/revenue': 'Phần bạn nhận được: 70% doanh thu từ khóa học của bạn',
    '/dashboard/teacher/coupons': 'Tạo mã giảm giá cho khóa học của bạn',
    '/dashboard/teacher/exams/create': 'Soạn đề thi TOEIC/IELTS mới',
    '/dashboard/teacher/exams': 'Quản lý đề thi do bạn tạo',
    '/dashboard/student': 'Xin chào! Hôm nay bạn muốn học gì?',
    '/dashboard/student/courses': 'Các khóa học bạn đã đăng ký',
    '/dashboard/student/orders': 'Quản lý các khóa học bạn đã mua',
    '/dashboard/student/certificates': 'Các chứng chỉ bạn đã nhận được khi hoàn thành khóa học',
    '/flashcard/create': 'Thêm từ vựng thủ công hoặc dùng AI để tạo thẻ',
    '/flashcard/deck': 'Thông tin chi tiết bộ thẻ',
    '/flashcard': 'Quản lý và học tập từ vựng với hệ thống Flashcard',
    '/exam': 'Danh sách bài thi',
    '/profile': 'Xem và chỉnh sửa thông tin tài khoản',
  }

  const sortedPaths = Object.keys(subtitles).sort((a, b) => b.length - a.length)

  for (const path of sortedPaths) {
    if (router.currentRoute.value.path.startsWith(path)) {
      return subtitles[path]
    }
  }

  return ''
})

const getAvatar = (name: string) => {
  const initials = name
    .split(' ')
    .map((word) => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

  return `https://ui-avatars.com/api/?name=${initials}&background=0d8abc&color=fff`
}

const logout = async () => {
  authStore.logout()
  await router.push('/login')
}
</script>
