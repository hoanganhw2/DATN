<template>
  <DashboardLayout>
    <div class="space-y-6">
      <!-- Filter Bar -->
      <div
        class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex flex-col md:flex-row gap-3 items-center"
      >
        <div class="relative flex-1 w-full">
          <svg
            class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input
            v-model="searchInput"
            @keyup.enter="doSearch"
            type="text"
            placeholder="Tìm theo họ tên, email, username..."
            class="w-full pl-9 pr-4 py-2.5 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition"
          />
        </div>
        <select
          v-model="roleFilter"
          @change="doSearch"
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
        >
          <option value="">Tất cả vai trò</option>
          <option value="ADMIN">Quản trị viên</option>
          <option value="TEACHER">Giảng viên</option>
          <option value="STUDENT">Học viên</option>
        </select>
        <select
          v-model.number="pageSize"
          @change="
            () => {
              currentPage = 0
              fetchUsers()
            }
          "
          class="py-2.5 px-4 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
        >
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
        <button
          @click="doSearch"
          class="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold rounded-xl transition shadow-sm"
        >
          Tìm kiếm
        </button>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-24">
          <div class="flex flex-col items-center gap-3">
            <div
              class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <p class="text-sm text-gray-500">Đang tải dữ liệu...</p>
          </div>
        </div>

        <template v-else>
          <div class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-sm whitespace-nowrap">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100">
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    #
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Người dùng
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Email
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Vai trò
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Trạng thái
                  </th>
                  <th
                    class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Ngày tạo
                  </th>
                  <th
                    class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider"
                  >
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <!-- Empty -->
                <tr v-if="filteredUsers.length === 0">
                  <td colspan="7" class="py-16 text-center text-gray-400">
                    <div class="flex flex-col items-center gap-2">
                      <svg
                        class="w-12 h-12 text-gray-200"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="1.5"
                          d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                      </svg>
                      <p class="font-medium">Không tìm thấy người dùng</p>
                    </div>
                  </td>
                </tr>
                <!-- Rows -->
                <tr
                  v-for="(u, idx) in filteredUsers"
                  :key="u.id"
                  class="hover:bg-blue-50/20 transition-colors"
                >
                  <td class="px-5 py-4 text-gray-400 text-xs">
                    {{ currentPage * pageSize + idx + 1 }}
                  </td>
                  <td class="px-5 py-4">
                    <div class="flex items-center gap-3">
                      <!-- Avatar initials -->
                      <div
                        class="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-xs font-bold shrink-0"
                      >
                        {{ getInitials(u.fullName || u.username) }}
                      </div>
                      <div>
                        <p class="font-semibold text-gray-900">{{ u.fullName || '—' }}</p>
                        <p class="text-xs text-gray-400">@{{ u.username }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-5 py-4 text-gray-600">{{ u.email }}</td>
                  <td class="px-5 py-4 text-center">
                    <span
                      :class="getRoleBadge(u.role)"
                      class="px-2.5 py-1 rounded-lg text-xs font-bold"
                    >
                      {{ getRoleLabel(u.role) }}
                    </span>
                  </td>
                  <td class="px-5 py-4 text-center">
                    <span
                      :class="getStatusBadge(u.status)"
                      class="px-2.5 py-1 rounded-full text-xs font-bold"
                    >
                      {{ getStatusLabel(u.status) }}
                    </span>
                  </td>
                  <td class="px-5 py-4 text-gray-500 text-xs">{{ formatDate(u.createdAt) }}</td>
                  <td class="px-5 py-4 text-center">
                    <div class="flex items-center justify-center gap-2">
                      <button
                        @click="openEdit(u)"
                        class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition"
                      >
                        ✏️ Sửa
                      </button>
                      <button
                        @click="openDelete(u)"
                        class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition"
                      >
                        🗑 Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div
            class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50"
          >
            <p class="text-sm text-gray-500">
              Hiển thị
              <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
              trong <span class="font-semibold text-gray-700">{{ totalElements }}</span> người dùng
            </p>
            <div class="flex items-center gap-1.5">
              <button
                :disabled="currentPage === 0"
                @click="goToPage(0)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                «
              </button>
              <button
                :disabled="currentPage === 0"
                @click="goToPage(currentPage - 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                ‹
              </button>
              <button
                v-for="p in pageNumbers"
                :key="p"
                @click="goToPage(p)"
                :class="[
                  'w-10 h-10 rounded-xl text-sm font-semibold transition border',
                  p === currentPage
                    ? 'bg-blue-600 text-white border-blue-600 shadow'
                    : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300',
                ]"
              >
                {{ p + 1 }}
              </button>
              <button
                :disabled="isLastPage"
                @click="goToPage(currentPage + 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                ›
              </button>
              <button
                :disabled="isLastPage"
                @click="goToPage(totalPages - 1)"
                class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
              >
                »
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- Edit Modal -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div
          v-if="showEditModal && editingUser"
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
        >
          <div
            class="absolute inset-0 bg-black/40 backdrop-blur-sm"
            @click="showEditModal = false"
          ></div>
          <div
            class="relative bg-white rounded-3xl shadow-2xl w-full max-w-md z-10 overflow-hidden"
          >
            <div class="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
              <h2 class="text-lg font-bold">Cập nhật người dùng</h2>
              <p class="text-blue-100 text-sm mt-1">@{{ editingUser.username }}</p>
            </div>
            <div class="p-6 space-y-5">
              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                  >Vai trò</label
                >
                <select
                  v-model="editForm.role"
                  class="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
                >
                  <option value="STUDENT">Học viên</option>
                  <option value="TEACHER">Giảng viên</option>
                  <option value="ADMIN">Quản trị viên</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2"
                  >Trạng thái</label
                >
                <select
                  v-model="editForm.status"
                  class="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
                >
                  <option value="ACTIVE">Hoạt động</option>
                  <option value="INACTIVE">Vô hiệu hóa</option>
                  <option value="BANNED">Bị khóa</option>
                </select>
              </div>
              <div class="flex gap-3 pt-2">
                <button
                  @click="saveEdit"
                  :disabled="saving"
                  class="flex-1 py-3 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white font-bold rounded-xl transition"
                >
                  {{ saving ? 'Đang lưu...' : '✓ Lưu thay đổi' }}
                </button>
                <button
                  @click="showEditModal = false"
                  class="flex-1 py-3 border border-gray-200 text-gray-600 font-bold rounded-xl hover:bg-gray-50 transition"
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Delete Modal -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div
          v-if="showDeleteModal && deletingUser"
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
        >
          <div
            class="absolute inset-0 bg-black/40 backdrop-blur-sm"
            @click="showDeleteModal = false"
          ></div>
          <div class="relative bg-white rounded-3xl shadow-2xl w-full max-w-sm z-10 p-8">
            <div class="text-center mb-6">
              <div
                class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4"
              >
                <span class="text-3xl">🗑</span>
              </div>
              <h3 class="text-lg font-bold text-gray-900">Xác nhận xóa</h3>
              <p class="text-gray-500 text-sm mt-2">
                Bạn có chắc muốn xóa người dùng
                <strong class="text-gray-900">{{
                  deletingUser.fullName || deletingUser.username
                }}</strong
                >? Hành động này không thể hoàn tác.
              </p>
            </div>
            <div class="flex gap-3">
              <button
                @click="confirmDelete"
                :disabled="saving"
                class="flex-1 py-3 bg-red-600 hover:bg-red-700 disabled:opacity-60 text-white font-bold rounded-xl transition"
              >
                {{ saving ? 'Đang xóa...' : '🗑 Xóa' }}
              </button>
              <button
                @click="showDeleteModal = false"
                class="flex-1 py-3 border border-gray-200 text-gray-600 font-bold rounded-xl hover:bg-gray-50 transition"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useToastStore } from '@/stores/toast'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getAllUsers, adminUpdateUser, deleteUser, type AdminUserResponse } from '@/api/users'

const toastStore = useToastStore()

// ── State ──────────────────────────────────────────────────────────────────────
const users = ref<AdminUserResponse[]>([])
const loading = ref(true)
const saving = ref(false)
const totalElements = ref(0)
const currentPage = ref(0)
const pageSize = ref(20)
const searchInput = ref('')
const roleFilter = ref('')

// Modals
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const editingUser = ref<AdminUserResponse | null>(null)
const deletingUser = ref<AdminUserResponse | null>(null)
const editForm = ref({ role: '', status: '' })

// ── Computed ───────────────────────────────────────────────────────────────────
const totalPages = computed(() => Math.max(1, Math.ceil(totalElements.value / pageSize.value)))
const isLastPage = computed(() => (currentPage.value + 1) * pageSize.value >= totalElements.value)
const displayStart = computed(() =>
  totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, totalElements.value),
)

const pageNumbers = computed(() => {
  const total = totalPages.value,
    current = currentPage.value,
    delta = 2
  const range: number[] = []
  for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++)
    range.push(i)
  return range
})

const filteredUsers = computed(() => {
  if (!roleFilter.value) return users.value
  return users.value.filter((u) => u.role === roleFilter.value)
})

// ── API ────────────────────────────────────────────────────────────────────────
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers(currentPage.value, pageSize.value, searchInput.value)
    users.value = res.content
    totalElements.value = res.totalElements
  } catch (e: any) {
    toastStore.error(e?.response?.data?.message || 'Không thể tải danh sách người dùng')
  } finally {
    loading.value = false
  }
}

const doSearch = () => {
  currentPage.value = 0
  fetchUsers()
}

const goToPage = (p: number) => {
  if (p < 0 || p >= totalPages.value) return
  currentPage.value = p
  fetchUsers()
}

onMounted(fetchUsers)

// ── Edit ───────────────────────────────────────────────────────────────────────
const openEdit = (u: AdminUserResponse) => {
  editingUser.value = u
  editForm.value = { role: u.role, status: u.status }
  showEditModal.value = true
}

const saveEdit = async () => {
  if (!editingUser.value) return
  saving.value = true
  try {
    const updated = await adminUpdateUser(editingUser.value.id, editForm.value)
    const idx = users.value.findIndex((u) => u.id === updated.id)
    if (idx !== -1) users.value[idx] = updated
    toastStore.success('Cập nhật người dùng thành công!')
    showEditModal.value = false
  } catch (e: any) {
    toastStore.error(e?.response?.data?.message || 'Lỗi khi cập nhật')
  } finally {
    saving.value = false
  }
}

// ── Delete ─────────────────────────────────────────────────────────────────────
const openDelete = (u: AdminUserResponse) => {
  deletingUser.value = u
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!deletingUser.value) return
  saving.value = true
  try {
    await deleteUser(deletingUser.value.id)
    users.value = users.value.filter((u) => u.id !== deletingUser.value!.id)
    totalElements.value--
    toastStore.success('Đã xóa người dùng thành công!')
    showDeleteModal.value = false
  } catch (e: any) {
    toastStore.error(e?.response?.data?.message || 'Lỗi khi xóa người dùng')
  } finally {
    saving.value = false
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
const getInitials = (name: string) =>
  name
    .split(' ')
    .map((w) => w[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

const formatDate = (d?: string) =>
  d
    ? new Date(d).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : '—'

const getRoleLabel = (r: string) =>
  ({ ADMIN: 'Admin', TEACHER: 'Giảng viên', STUDENT: 'Học viên' })[r] ?? r
const getRoleBadge = (r: string) =>
  ({
    ADMIN: 'bg-red-100 text-red-700',
    TEACHER: 'bg-blue-100 text-blue-700',
    STUDENT: 'bg-blue-100 text-blue-700',
  })[r] ?? 'bg-gray-100 text-gray-600'

const getStatusLabel = (s: string) =>
  ({ ACTIVE: 'Hoạt động', INACTIVE: 'Vô hiệu', BANNED: 'Bị khóa' })[s] ?? s
const getStatusBadge = (s: string) =>
  ({
    ACTIVE: 'bg-green-100 text-green-700',
    INACTIVE: 'bg-gray-100 text-gray-500',
    BANNED: 'bg-red-100 text-red-700',
  })[s] ?? 'bg-gray-100 text-gray-500'
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.2s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 8px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
