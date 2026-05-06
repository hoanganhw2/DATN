<template>
  <DashboardLayout>
    <div class="max-w-5xl mx-auto space-y-6">
      <!-- ─── Header ──────────────────────────────────────────────────────── -->
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Cài đặt hệ thống</h1>
        <p class="text-gray-500 text-sm mt-1">Quản lý cấu hình chung, thanh toán và giao diện nền tảng.</p>
      </div>

      <div class="flex flex-col md:flex-row gap-6">
        <!-- ─── Sidebar Menu ──────────────────────────────────────────────── -->
        <div class="w-full md:w-64 shrink-0">
          <nav class="flex flex-col space-y-1">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="[
                'flex items-center gap-3 px-4 py-3 text-sm font-medium rounded-xl transition-colors',
                activeTab === tab.id
                  ? 'bg-blue-50 text-blue-700'
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              ]"
            >
              <component :is="tab.icon" class="w-5 h-5" />
              {{ tab.label }}
            </button>
          </nav>
        </div>

        <!-- ─── Content Area ──────────────────────────────────────────────── -->
        <div class="flex-1">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm">
            <!-- General Settings -->
            <div v-if="activeTab === 'general'" class="p-6 space-y-6 animate-fade-in">
              <h2 class="text-lg font-semibold text-gray-900 border-b border-gray-100 pb-4">Thông tin chung</h2>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Tên nền tảng</label>
                  <input
                    v-model="form.siteName"
                    type="text"
                    class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Email hỗ trợ</label>
                  <input
                    v-model="form.supportEmail"
                    type="email"
                    class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Số điện thoại hotline</label>
                  <input
                    v-model="form.hotline"
                    type="text"
                    class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Ngôn ngữ mặc định</label>
                  <select
                    v-model="form.language"
                    class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                  >
                    <option value="vi">Tiếng Việt</option>
                    <option value="en">English</option>
                  </select>
                </div>
                <div class="col-span-1 md:col-span-2 space-y-2">
                  <label class="text-sm font-medium text-gray-700">Mô tả ngắn (SEO)</label>
                  <textarea
                    v-model="form.description"
                    rows="3"
                    class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none resize-none"
                  ></textarea>
                </div>
              </div>
            </div>

            <!-- Payment Settings -->
            <div v-else-if="activeTab === 'payment'" class="p-6 space-y-6 animate-fade-in">
              <div class="flex items-center justify-between border-b border-gray-100 pb-4">
                <h2 class="text-lg font-semibold text-gray-900">Cấu hình thanh toán VNPay</h2>
                <div class="flex items-center gap-2">
                  <span class="text-sm text-gray-600">Trạng thái:</span>
                  <span class="px-2.5 py-1 text-xs font-bold text-green-700 bg-green-100 rounded-full uppercase tracking-widest">
                    Đã kết nối
                  </span>
                </div>
              </div>

              <div class="space-y-6">
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Môi trường</label>
                  <div class="flex gap-4">
                    <label class="flex items-center gap-2 cursor-pointer">
                      <input type="radio" v-model="form.vnpayEnv" value="sandbox" class="text-blue-600 focus:ring-blue-500">
                      <span class="text-sm text-gray-700">Sandbox (Thử nghiệm)</span>
                    </label>
                    <label class="flex items-center gap-2 cursor-pointer">
                      <input type="radio" v-model="form.vnpayEnv" value="production" class="text-blue-600 focus:ring-blue-500">
                      <span class="text-sm text-gray-700">Production (Thực tế)</span>
                    </label>
                  </div>
                </div>

                <div class="grid grid-cols-1 gap-6">
                  <div class="space-y-2">
                    <label class="text-sm font-medium text-gray-700">vnp_TmnCode (Terminal ID)</label>
                    <div class="relative">
                      <input
                        v-model="form.vnpayTmnCode"
                        type="text"
                        class="w-full pl-4 pr-10 py-2 bg-gray-50 border border-gray-200 rounded-xl font-mono text-sm focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                      />
                      <KeyRound class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm font-medium text-gray-700">vnp_HashSecret (Secret Key)</label>
                    <div class="relative">
                      <input
                        v-model="form.vnpayHashSecret"
                        :type="showSecret ? 'text' : 'password'"
                        class="w-full pl-4 pr-12 py-2 bg-gray-50 border border-gray-200 rounded-xl font-mono text-sm focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                      />
                      <button 
                        @click="showSecret = !showSecret"
                        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition"
                      >
                        <Eye v-if="!showSecret" class="w-4 h-4" />
                        <EyeOff v-else class="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Email & Notifications Tab -->
            <div v-else-if="activeTab === 'notifications'" class="p-6 space-y-6 animate-fade-in">
              <h2 class="text-lg font-semibold text-gray-900 border-b border-gray-100 pb-4">Thông báo & Email</h2>
              
              <div class="space-y-4">
                <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl border border-gray-100">
                  <div>
                    <h3 class="font-medium text-gray-900">Email hoá đơn mua hàng</h3>
                    <p class="text-sm text-gray-500 mt-0.5">Tự động gửi email hoá đơn cho học viên khi thanh toán khóa học thành công.</p>
                  </div>
                  <label class="relative inline-flex items-center cursor-pointer">
                    <input type="checkbox" v-model="form.notifyNewOrder" class="sr-only peer">
                    <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                  </label>
                </div>

                <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl border border-gray-100">
                  <div>
                    <h3 class="font-medium text-gray-900">Email nhắc nhở học viên</h3>
                    <p class="text-sm text-gray-500 mt-0.5">Website sẽ tự động gửi email nếu học viên chưa vào học trong vòng 7 ngày.</p>
                  </div>
                  <label class="relative inline-flex items-center cursor-pointer">
                    <input type="checkbox" v-model="form.notifyIdleStudents" class="sr-only peer">
                    <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                  </label>
                </div>
              </div>
            </div>

            <!-- Gemini AI Settings Tab -->
            <div v-else-if="activeTab === 'ai'" class="p-6 space-y-6 animate-fade-in">
              <h2 class="text-lg font-semibold text-gray-900 border-b border-gray-100 pb-4">Tích hợp Trí tuệ Nhân tạo (Gemini)</h2>
              
              <div class="space-y-6">
                <div class="space-y-2">
                  <label class="text-sm font-medium text-gray-700">Gemini API Key</label>
                  <p class="text-xs text-gray-500 mb-2">Được sử dụng cho chức năng Chatbot và tự động tạo bài tập.</p>
                  <div class="relative">
                    <input
                      v-model="form.geminiKey"
                      type="password"
                      class="w-full pl-4 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl font-mono text-sm focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none"
                    />
                  </div>
                  <a href="https://aistudio.google.com/app/apikey" target="_blank" class="text-xs text-blue-600 hover:underline">Lấy mã API của bạn tại thư viện AI Studio</a>
                </div>

                <div class="space-y-2 pt-4">
                  <label class="text-sm font-medium text-gray-700">System Prompt mặt định (Dành cho AI Chatbot)</label>
                  <textarea
                    v-model="form.geminiPrompt"
                    rows="5"
                    class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl font-mono text-sm focus:bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition outline-none resize-none"
                  ></textarea>
                </div>
              </div>
            </div>

            <!-- Footer Toolbar -->
            <div class="p-6 bg-gray-50/50 border-t border-gray-100 rounded-b-2xl flex justify-end gap-3">
              <button 
                type="button"
                @click="resetForm"
                class="px-5 py-2.5 text-sm font-medium text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:text-gray-900 transition"
              >
                Khôi phục
              </button>
              <button 
                type="button"
                @click="saveSettings"
                class="inline-flex items-center gap-2 px-5 py-2.5 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-xl hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition shadow-sm"
              >
                <Save class="w-4 h-4" />
                Lưu thay đổi
                <span v-if="saving" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { Settings, CreditCard, Bell, Bot, Save, KeyRound, Eye, EyeOff } from 'lucide-vue-next'
import { useToastStore } from '@/stores/toast'
import { getSettings, updateSettings } from '@/api/settings'

const toastStore = useToastStore()

const tabs = [
  { id: 'general', label: 'Cấu hình chung', icon: Settings },
  { id: 'payment', label: 'Cổng thanh toán', icon: CreditCard },
  { id: 'notifications', label: 'Email & Thông báo', icon: Bell },
  { id: 'ai', label: 'Tích hợp AI', icon: Bot },
]

const activeTab = ref('general')
const showSecret = ref(false)
const saving = ref(false)

const initialForm = {
  siteName: 'EnglishPro',
  supportEmail: 'support@englishpro.edu.vn',
  hotline: '1900 1234',
  language: 'vi',
  description: 'Nền tảng học tiếng Anh trực tuyến hàng đầu Việt Nam.',
  vnpayEnv: 'sandbox',
  vnpayTmnCode: '22F9EEQE',
  vnpayHashSecret: '3ACLGAN68GTTH4DPJV848ZSTPUWADRT8',
  notifyNewOrder: true,
  notifyIdleStudents: false,
  geminiKey: '',
  geminiPrompt: 'Bạn là trợ giảng AI tài năng của hệ thống EnglishPro. Nhiệm vụ của bạn là giải đáp các thắc mắc về tiếng Anh của học viên một cách thân thiện và dễ hiểu.'
}

const form = ref({ ...initialForm })

const loadSettings = async () => {
  try {
    const data = await getSettings()
    if (Object.keys(data).length > 0) {
      form.value = {
        siteName: data.siteName || initialForm.siteName,
        supportEmail: data.supportEmail || initialForm.supportEmail,
        hotline: data.hotline || initialForm.hotline,
        language: data.language || initialForm.language,
        description: data.description || initialForm.description,
        vnpayEnv: data.vnpayEnv || initialForm.vnpayEnv,
        vnpayTmnCode: data.vnpayTmnCode || initialForm.vnpayTmnCode,
        vnpayHashSecret: data.vnpayHashSecret || initialForm.vnpayHashSecret,
        notifyNewOrder: data.notifyNewOrder === 'true',
        notifyIdleStudents: data.notifyIdleStudents === 'true',
        geminiKey: data.geminiKey || initialForm.geminiKey,
        geminiPrompt: data.geminiPrompt || initialForm.geminiPrompt
      }
    }
  } catch (error) {
    toastStore.error('Không thể tải cài đặt')
  }
}

const resetForm = () => {
  loadSettings()
  toastStore.success('Đã khôi phục các thay đổi chưa lưu')
}

const saveSettings = async () => {
  if (saving.value) return
  
  saving.value = true
  try {
    const payload: Record<string, string> = {
      siteName: form.value.siteName,
      supportEmail: form.value.supportEmail,
      hotline: form.value.hotline,
      language: form.value.language,
      description: form.value.description,
      vnpayEnv: form.value.vnpayEnv,
      vnpayTmnCode: form.value.vnpayTmnCode,
      vnpayHashSecret: form.value.vnpayHashSecret,
      notifyNewOrder: form.value.notifyNewOrder ? 'true' : 'false',
      notifyIdleStudents: form.value.notifyIdleStudents ? 'true' : 'false',
      geminiKey: form.value.geminiKey,
      geminiPrompt: form.value.geminiPrompt
    }
    await updateSettings(payload)
    toastStore.success('Đã lưu cấu hình thành công!')
  } catch (error) {
    toastStore.error('Không thể lưu cài đặt')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
