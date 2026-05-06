<template>
  <DashboardLayout>
    <div class="max-w-3xl mx-auto space-y-6">
      <!-- Profile Card -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Top accent bar -->
        <div class="h-1.5 bg-gradient-to-r from-blue-500 to-indigo-500"></div>

        <div class="p-6 md:p-8">
          <div class="flex flex-col sm:flex-row items-start sm:items-center gap-5">
            <!-- Avatar -->
            <div
              class="shrink-0 w-20 h-20 rounded-2xl border-2 border-gray-100 shadow-sm overflow-hidden bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center"
            >
              <img
                v-if="user?.avatar"
                :src="user.avatar"
                :alt="user?.fullName || ''"
                class="w-full h-full object-cover"
              />
              <span v-else class="text-2xl font-bold text-white">{{
                getInitials(user?.fullName || 'U')
              }}</span>
            </div>

            <!-- Info -->
            <div class="flex-1 min-w-0">
              <div class="flex flex-wrap items-center gap-3 mb-1">
                <h1 class="text-xl font-black text-gray-900">
                  {{ user?.fullName || 'Người dùng' }}
                </h1>
                <span :class="roleBadgeClass" class="px-2.5 py-0.5 rounded-lg text-xs font-bold">
                  {{ roleLabel }}
                </span>
              </div>
              <p class="text-sm text-gray-500">{{ user?.email }}</p>
              <p v-if="user?.username" class="text-xs text-gray-400 mt-0.5">@{{ user.username }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab bar -->
      <div class="flex bg-gray-100 rounded-xl p-1 gap-1">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="activeTab = tab.value"
          :class="[
            'flex-1 py-2.5 text-sm font-semibold rounded-lg transition-all duration-200',
            activeTab === tab.value
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- TAB: Thông tin cá nhân -->
      <div
        v-if="activeTab === 'info'"
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 md:p-8"
      >
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-base font-black text-gray-900">Thông tin cá nhân</h2>
          <button
            v-if="!isEditing"
            @click="startEdit"
            class="flex items-center gap-1.5 px-4 py-2 text-sm font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-xl transition-colors"
          >
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
              />
            </svg>
            Chỉnh sửa
          </button>
        </div>

        <form @submit.prevent="saveInfo" class="space-y-5">
          <!-- Họ và tên -->
          <div>
            <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
              >Họ và tên</label
            >
            <input
              v-model="form.fullName"
              type="text"
              :disabled="!isEditing"
              :class="
                isEditing
                  ? 'bg-white border-gray-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20'
                  : 'bg-gray-50 border-gray-100 cursor-not-allowed text-gray-600'
              "
              class="w-full border rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none transition-all"
            />
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <!-- Email -->
            <div>
              <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                >Email</label
              >
              <div class="relative">
                <input
                  :value="user?.email"
                  type="email"
                  disabled
                  class="w-full bg-gray-50 border border-gray-100 rounded-xl px-4 py-3 text-sm text-gray-500 cursor-not-allowed pr-28"
                />
                <span
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-[10px] font-bold bg-gray-200 text-gray-400 px-2 py-0.5 rounded"
                  >Không đổi</span
                >
              </div>
            </div>

            <!-- Username -->
            <div>
              <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                >Tài khoản</label
              >
              <input
                v-model="form.username"
                type="text"
                :disabled="!isEditing"
                :class="
                  isEditing
                    ? 'bg-white border-gray-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20'
                    : 'bg-gray-50 border-gray-100 cursor-not-allowed text-gray-600'
                "
                class="w-full border rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none transition-all"
              />
            </div>

            <!-- Phone -->
            <div>
              <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                >Số điện thoại</label
              >
              <input
                v-model="form.phone"
                type="tel"
                :disabled="!isEditing"
                :placeholder="isEditing ? '0901234567' : user?.phone ? '' : 'Chưa cập nhật'"
                :class="
                  isEditing
                    ? 'bg-white border-gray-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20'
                    : 'bg-gray-50 border-gray-100 cursor-not-allowed text-gray-600'
                "
                class="w-full border rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none transition-all"
              />
            </div>

            <!-- Gender -->
            <div>
              <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                >Giới tính</label
              >
              <select
                v-model="form.gender"
                :disabled="!isEditing"
                :class="
                  isEditing
                    ? 'bg-white border-gray-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20'
                    : 'bg-gray-50 border-gray-100 cursor-not-allowed text-gray-600'
                "
                class="w-full border rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none transition-all appearance-none"
              >
                <option value="">Chưa chọn</option>
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </div>

            <!-- Role -->
            <div>
              <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                >Vai trò</label
              >
              <div
                class="flex items-center gap-2 bg-gray-50 border border-gray-100 rounded-xl px-4 py-3"
              >
                <span :class="roleDotClass" class="w-2 h-2 rounded-full shrink-0"></span>
                <span class="text-sm text-gray-600 font-medium">{{ roleLabel }}</span>
              </div>
            </div>
          </div>

          <!-- Address — full width -->
          <div>
            <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
              >Địa chỉ</label
            >
            <input
              v-model="form.address"
              type="text"
              :disabled="!isEditing"
              :placeholder="
                isEditing
                  ? 'Số nhà, đường, quận/huyện, tỉnh/thành phố...'
                  : form.address || 'Chưa cập nhật'
              "
              :class="
                isEditing
                  ? 'bg-white border-gray-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20'
                  : 'bg-gray-50 border-gray-100 cursor-not-allowed text-gray-600'
              "
              class="w-full border rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none transition-all"
            />
          </div>

          <!-- Actions -->
          <div v-if="isEditing" class="flex gap-3 pt-4 border-t border-gray-50">
            <button
              type="submit"
              :disabled="saving"
              class="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white text-sm font-bold rounded-xl transition-colors shadow-sm shadow-blue-200"
            >
              {{ saving ? 'Đang lưu...' : '✓ Lưu thay đổi' }}
            </button>
            <button
              type="button"
              @click="cancelEdit"
              class="px-5 py-2.5 border border-gray-200 text-gray-600 text-sm font-bold rounded-xl hover:bg-gray-50 transition-colors"
            >
              Hủy
            </button>
          </div>
        </form>
      </div>

      <!-- TAB: Bảo mật -->
      <div
        v-if="activeTab === 'security'"
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 md:p-8"
      >
        <div class="mb-6">
          <h2 class="text-base font-black text-gray-900">Đổi mật khẩu</h2>
          <p class="text-sm text-gray-500 mt-1">
            Sử dụng mật khẩu mạnh để bảo vệ tài khoản của bạn
          </p>
        </div>

        <!-- Password tip -->
        <div class="mb-6 p-4 bg-blue-50 border border-blue-100 rounded-xl text-sm text-blue-700">
          💡 Mật khẩu cần ít nhất 8 ký tự, bao gồm chữ hoa, số và ký tự đặc biệt.
        </div>

        <form @submit.prevent="changePassword" class="space-y-5 max-w-md">
          <!-- Current password -->
          <div>
            <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
              >Mật khẩu hiện tại</label
            >
            <div class="relative">
              <input
                v-model="pwdForm.current"
                :type="showPwd.current ? 'text' : 'password'"
                placeholder="••••••••"
                class="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 pr-12 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
              />
              <button
                type="button"
                @click="showPwd.current = !showPwd.current"
                class="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <EyeIcon class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- New password -->
          <div>
            <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
              >Mật khẩu mới</label
            >
            <div class="relative">
              <input
                v-model="pwdForm.newPwd"
                :type="showPwd.newPwd ? 'text' : 'password'"
                placeholder="••••••••"
                class="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 pr-12 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
              />
              <button
                type="button"
                @click="showPwd.newPwd = !showPwd.newPwd"
                class="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <EyeIcon class="w-4 h-4" />
              </button>
            </div>
            <!-- Strength bar -->
            <div v-if="pwdForm.newPwd" class="mt-2 space-y-1">
              <div class="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-500"
                  :class="strengthBarClass"
                  :style="{ width: strengthPercent + '%' }"
                ></div>
              </div>
              <p class="text-xs font-medium" :class="strengthTextClass">{{ strengthLabel }}</p>
            </div>
          </div>

          <!-- Confirm password -->
          <div>
            <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
              >Xác nhận mật khẩu mới</label
            >
            <div class="relative">
              <input
                v-model="pwdForm.confirm"
                :type="showPwd.confirm ? 'text' : 'password'"
                placeholder="••••••••"
                :class="
                  pwdMismatch
                    ? 'border-red-300 bg-red-50 focus:ring-red-500/20 focus:border-red-400'
                    : 'border-gray-200 bg-white focus:ring-blue-500/20 focus:border-blue-500'
                "
                class="w-full border rounded-xl px-4 py-3 pr-12 text-sm focus:outline-none focus:ring-2 transition-all"
              />
              <button
                type="button"
                @click="showPwd.confirm = !showPwd.confirm"
                class="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <EyeIcon class="w-4 h-4" />
              </button>
            </div>
            <p v-if="pwdMismatch" class="text-xs text-red-500 mt-1.5">⚠️ Mật khẩu không khớp</p>
            <p v-else-if="pwdForm.confirm && !pwdMismatch" class="text-xs text-green-600 mt-1.5">
              ✓ Mật khẩu khớp
            </p>
          </div>

          <!-- Error / Success -->
          <div
            v-if="pwdError"
            class="p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-600"
          >
            ⚠️ {{ pwdError }}
          </div>
          <div
            v-if="pwdSuccess"
            class="p-3 bg-green-50 border border-green-200 rounded-xl text-sm text-green-600"
          >
            ✅ {{ pwdSuccess }}
          </div>

          <button
            type="submit"
            :disabled="pwdMismatch || !pwdForm.current || !pwdForm.newPwd || !pwdForm.confirm"
            class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed text-white text-sm font-bold rounded-xl transition-colors shadow-sm shadow-blue-200"
          >
            Đổi mật khẩu
          </button>
        </form>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useToastStore } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import EyeIcon from '@/components/dashboard/icons/EyeIcon.vue'
import { getMe, updateMe } from '@/api/users'

const authStore = useAuthStore()
const toastStore = useToastStore()
const user = computed(() => authStore.user)

// ── Tabs ──────────────────────────────────────────────────────────────────────
const tabs = [
  { label: 'Thông tin cá nhân', value: 'info' as const },
  { label: 'Bảo mật', value: 'security' as const },
]
const activeTab = ref<'info' | 'security'>('info')

// ── Role helpers ───────────────────────────────────────────────────────────────
const roleLabel = computed(() => {
  const map: Record<string, string> = {
    ADMIN: 'Quản trị viên',
    TEACHER: 'Giảng viên',
    STUDENT: 'Học viên',
  }
  return map[user.value?.role || ''] || 'Không xác định'
})
const roleBadgeClass = computed(() => {
  const map: Record<string, string> = {
    ADMIN: 'bg-red-100 text-red-700',
    TEACHER: 'bg-blue-100 text-blue-700',
    STUDENT: 'bg-blue-100 text-blue-700',
  }
  return map[user.value?.role || ''] || 'bg-gray-100 text-gray-700'
})
const roleDotClass = computed(() => {
  const map: Record<string, string> = {
    ADMIN: 'bg-red-500',
    TEACHER: 'bg-blue-500',
    STUDENT: 'bg-blue-500',
  }
  return map[user.value?.role || ''] || 'bg-gray-400'
})

const getInitials = (name: string) =>
  name
    .split(' ')
    .map((w) => w[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

// ── Info form ─────────────────────────────────────────────────────────────────
const isEditing = ref(false)
const saving = ref(false)
const form = ref({
  fullName: user.value?.fullName || '',
  username: user.value?.username || '',
  phone: user.value?.phone || '',
  gender: '',
  address: '',
})

// Load profile data (gender, address) from API on mount
onMounted(async () => {
  try {
    const me = await getMe()
    form.value.fullName = me.fullName || user.value?.fullName || ''
    form.value.username = me.username || user.value?.username || ''
    form.value.phone = me.profile?.phone || user.value?.phone || ''
    form.value.gender = me.profile?.gender || ''
    form.value.address = me.profile?.address || ''
  } catch (e) {
    console.error('Không thể tải thông tin hồ sơ:', e)
  }
})

const startEdit = () => {
  isEditing.value = true
}
const cancelEdit = () => {
  isEditing.value = false
}
const saveInfo = async () => {
  saving.value = true
  try {
    await updateMe({
      fullName: form.value.fullName,
      phone: form.value.phone,
      gender: form.value.gender || undefined,
      address: form.value.address || undefined,
    })
    toastStore.success('Cập nhật thông tin thành công!')
    isEditing.value = false
  } catch (e: any) {
    toastStore.error(e?.response?.data?.message || 'Có lỗi xảy ra khi lưu thông tin')
  } finally {
    saving.value = false
  }
}

// ── Password form ─────────────────────────────────────────────────────────────
const showPwd = ref({ current: false, newPwd: false, confirm: false })
const pwdError = ref('')
const pwdSuccess = ref('')
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })

const pwdMismatch = computed(
  () => !!pwdForm.value.confirm && pwdForm.value.newPwd !== pwdForm.value.confirm,
)

// Strength
const hasMinLength = computed(() => pwdForm.value.newPwd.length >= 8)
const hasUppercase = computed(() => /[A-Z]/.test(pwdForm.value.newPwd))
const hasNumber = computed(() => /\d/.test(pwdForm.value.newPwd))
const hasSpecial = computed(() => /[!@#$%^&*(),.?":{}|<>]/.test(pwdForm.value.newPwd))
const strengthScore = computed(
  () =>
    [hasMinLength.value, hasUppercase.value, hasNumber.value, hasSpecial.value].filter(Boolean)
      .length,
)
const strengthPercent = computed(() => strengthScore.value * 25)
const strengthBarClass = computed(() => {
  if (strengthScore.value <= 1) return 'bg-red-500'
  if (strengthScore.value === 2) return 'bg-orange-400'
  if (strengthScore.value === 3) return 'bg-yellow-400'
  return 'bg-green-500'
})
const strengthTextClass = computed(() => {
  if (strengthScore.value <= 1) return 'text-red-500'
  if (strengthScore.value === 2) return 'text-orange-500'
  if (strengthScore.value === 3) return 'text-yellow-600'
  return 'text-green-600'
})
const strengthLabel = computed(() => {
  if (strengthScore.value <= 1) return 'Rất yếu'
  if (strengthScore.value === 2) return 'Yếu'
  if (strengthScore.value === 3) return 'Trung bình'
  return 'Mạnh 💪'
})

const changePassword = () => {
  pwdError.value = ''
  pwdSuccess.value = ''
  if (pwdForm.value.newPwd.length < 6) {
    pwdError.value = 'Mật khẩu mới phải có ít nhất 6 ký tự'
    return
  }
  pwdSuccess.value = 'Đổi mật khẩu thành công!'
  toastStore.success('Đổi mật khẩu thành công!')
  pwdForm.value = { current: '', newPwd: '', confirm: '' }
}
</script>
