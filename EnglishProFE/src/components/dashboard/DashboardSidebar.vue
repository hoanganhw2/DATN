<template>
  <aside
    :class="[
      'bg-white border-r border-gray-200 flex flex-col shadow-sm transition-all duration-300 relative',
      isCollapsed ? 'w-20' : 'w-64',
    ]"
  >
    <!-- Logo Section -->
    <router-link
      to="/"
      class="p-6 border-b border-gray-200 flex items-center hover:bg-gray-50 transition-colors"
      :class="isCollapsed ? 'justify-center' : 'space-x-3'"
    >
      <div
        class="w-10 h-10 rounded-lg bg-linear-to-br from-blue-600 to-blue-700 flex items-center justify-center font-bold text-lg text-white shrink-0 shadow-md"
      >
        E
      </div>
      <span
        v-if="!isCollapsed"
        class="text-xl font-bold text-blue-600 truncate transition-all duration-300"
      >
        EnglishPro
      </span>
    </router-link>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto px-3 py-6 space-y-2 custom-scrollbar">
      <!-- Admin Menu -->
      <template v-if="authStore.isAdmin">
        <SidebarSection title="Quản trị" :is-collapsed="isCollapsed">
          <SidebarLink
            to="/dashboard/admin"
            :icon="BarChart3Icon"
            label="Thống kê hệ thống"
            :active="isActive('/dashboard/admin')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/users"
            :icon="UserCogIcon"
            label="Quản lý người dùng"
            :active="isActive('/dashboard/admin/users')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/orders"
            :icon="ReceiptIcon"
            label="Quản lý đơn hàng"
            :active="isActive('/dashboard/admin/orders')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/statistics"
            :icon="TrendingUpIcon"
            label="Thống kê doanh thu"
            :active="isActive('/dashboard/admin/statistics')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/settings"
            :icon="SettingsIcon"
            label="Cài đặt hệ thống"
            :active="isActive('/dashboard/admin/settings')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/course-approvals"
            :icon="LayersIcon"
            label="Duyệt khóa học"
            :active="isActive('/dashboard/admin/course-approvals')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/coupons"
            :icon="TagIcon"
            label="Mã giảm giá"
            :active="isActive('/dashboard/admin/coupons')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/admin/exams"
            :icon="ClipboardIcon"
            label="Ngân hàng đề thi"
            :active="isActive('/dashboard/admin/exams')"
            :is-collapsed="isCollapsed"
          />
        </SidebarSection>
      </template>

      <!-- Student Menu -->
      <template v-if="authStore.isStudent">
        <SidebarSection title="Học tập" :is-collapsed="isCollapsed">
          <SidebarLink
            to="/dashboard/student"
            :icon="BarChart3Icon"
            label="Trang tổng quan"
            :active="isActive('/dashboard/student')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/student/courses"
            :icon="LayersIcon"
            label="Khóa học của tôi"
            :active="isActive('/dashboard/student/courses')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/flashcard"
            :icon="SparklesIcon"
            label="Flashcard"
            :active="isActive('/flashcard')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/exam"
            :icon="ClipboardIcon"
            label="Luyện thi"
            :active="isActive('/exam')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/student/orders"
            :icon="ReceiptIcon"
            label="Đơn hàng đã mua"
            :active="isActive('/dashboard/student/orders')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/student/certificates"
            :icon="AwardIcon"
            label="Chứng chỉ"
            :active="isActive('/dashboard/student/certificates')"
            :is-collapsed="isCollapsed"
          />
        </SidebarSection>
      </template>

      <!-- Teacher Menu -->
      <template v-if="authStore.isTeacher">
        <SidebarSection title="Giảng dạy" :is-collapsed="isCollapsed">
          <SidebarLink
            to="/dashboard/teacher"
            :icon="BarChart3Icon"
            label="Trang tổng quan"
            :active="isActive('/dashboard/teacher')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/teacher/courses"
            :icon="LayersIcon"
            label="Khóa học của tôi"
            :active="isActive('/dashboard/teacher/courses')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/teacher/students"
            :icon="GraduationCapIcon"
            label="Quản lý học viên"
            :active="isActive('/dashboard/teacher/students')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/teacher/revenue"
            :icon="TrendingUpIcon"
            label="Doanh thu của tôi"
            :active="isActive('/dashboard/teacher/revenue')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/teacher/coupons"
            :icon="TagIcon"
            label="Mã giảm giá"
            :active="isActive('/dashboard/teacher/coupons')"
            :is-collapsed="isCollapsed"
          />
          <SidebarLink
            to="/dashboard/teacher/exams"
            :icon="ClipboardIcon"
            label="Quản lý đề thi"
            :active="isActive('/dashboard/teacher/exams')"
            :is-collapsed="isCollapsed"
          />
        </SidebarSection>
      </template>

      <!-- Common -->
      <SidebarSection title="Khác" :is-collapsed="isCollapsed">
        <SidebarLink
          to="/"
          :icon="HomeIcon"
          label="Về trang chủ"
          :active="false"
          :is-collapsed="isCollapsed"
        />
      </SidebarSection>
    </nav>

    <!-- User Profile Section -->
    <div class="p-4 border-t border-gray-200">
      <div
        @click="showUserMenu = !showUserMenu"
        class="flex items-center space-x-3 cursor-pointer hover:bg-gray-100 p-2 rounded-lg transition-all duration-200"
        :class="isCollapsed ? 'justify-center' : ''"
      >
        <div
          class="w-10 h-10 rounded-full bg-linear-to-br from-blue-500 to-blue-600 flex items-center justify-center text-sm font-bold text-white shrink-0 border-2 border-white shadow-sm"
        >
          {{ getInitials(authStore.user?.fullName || 'U') }}
        </div>
        <div v-if="!isCollapsed" class="flex-1 min-w-0 transition-all duration-300">
          <p class="text-sm font-semibold truncate text-gray-900">{{ authStore.user?.fullName }}</p>
          <p class="text-xs text-gray-500 truncate">{{ authStore.user?.role }}</p>
        </div>
      </div>

      <!-- User Menu Dropdown -->
      <div v-if="showUserMenu && !isCollapsed" class="mt-2 space-y-1">
        <button
          @click="logout"
          class="w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg transition"
        >
          Đăng xuất
        </button>
      </div>
    </div>

    <!-- Toggle Button -->
    <button
      @click="toggleSidebar"
      class="absolute -right-3 top-20 w-6 h-6 bg-white border border-gray-200 rounded-full flex items-center justify-center shadow-md hover:text-blue-600 transition h-8 w-8 -right-4"
    >
      <ChevronToggleIcon :collapsed="isCollapsed" class="w-4 h-4" />
    </button>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SidebarSection from './SidebarSection.vue'
import SidebarLink from './SidebarLink.vue'

// Icons
import HomeIcon from './icons/HomeIcon.vue'
import BarChart3Icon from './icons/BarChart3Icon.vue'
import UserCogIcon from './icons/UserCogIcon.vue'
import ReceiptIcon from './icons/ReceiptIcon.vue'
import SettingsIcon from './icons/SettingsIcon.vue'
import LayersIcon from './icons/LayersIcon.vue'
import GraduationCapIcon from './icons/GraduationCapIcon.vue'
import ChevronToggleIcon from './icons/ChevronToggleIcon.vue'
import SparklesIcon from './icons/SparklesIcon.vue'
import ClipboardIcon from './icons/ClipboardIcon.vue'
import TrendingUpIcon from './icons/TrendingUpIcon.vue'
import TagIcon from './icons/TagIcon.vue'
import AwardIcon from './icons/AwardIcon.vue'

const props = defineProps<{
  isCollapsed: boolean
}>()

const emit = defineEmits(['toggle'])

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const showUserMenu = ref(false)

const toggleSidebar = () => {
  emit('toggle')
}

const isActive = (path: string) => {
  return (
    route.path === path ||
    (path !== '/dashboard/admin' &&
      path !== '/dashboard/teacher' &&
      path !== '/dashboard/student' &&
      route.path.startsWith(path))
  )
}

const getInitials = (name: string) => {
  return name
    .split(' ')
    .map((word) => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

const logout = async () => {
  authStore.logout()
  await router.push('/login')
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #d1d5db;
}
</style>
