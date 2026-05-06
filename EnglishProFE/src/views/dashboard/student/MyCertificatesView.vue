<template>
  <DashboardLayout>
    <div class="space-y-7">

      <!-- Search -->
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <div class="relative group flex-1 w-full">
          <SearchIcon
            class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-gray-400 group-focus-within:text-blue-500 transition-colors"
          />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo mã chứng chỉ hoặc tên khóa học..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
          />
        </div>
        <!-- Sort -->
        <select
          v-model="sortBy"
          class="bg-white border border-gray-200 rounded-xl px-4 py-2.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm shrink-0"
        >
          <option value="recent">Mới nhất</option>
          <option value="name">Tên A-Z</option>
        </select>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col items-center justify-center py-20">
        <div class="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-500">Đang tải danh sách chứng chỉ...</p>
      </div>

      <!-- Data Table -->
      <div v-else-if="filteredCerts.length > 0" class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="overflow-x-auto custom-scrollbar">
          <table class="w-full text-sm whitespace-nowrap">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100">
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">#</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Mã chứng chỉ</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Khóa học</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Học viên</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Giảng viên</th>
                <th class="px-5 py-3.5 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Ngày cấp</th>
                <th class="px-5 py-3.5 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr
                v-for="(cert, idx) in paginatedCerts"
                :key="cert.id"
                class="hover:bg-blue-50/20 transition-colors"
              >
                <td class="px-5 py-4 text-gray-400 text-xs">
                  {{ (currentPage - 1) * pageSize + idx + 1 }}
                </td>
                <td class="px-5 py-4">
                  <span class="text-xs font-mono font-medium text-amber-700 bg-amber-50 px-2 py-1 rounded-md border border-amber-200">
                    {{ cert.certificateCode }}
                  </span>
                </td>
                <td class="px-5 py-4 min-w-[250px]">
                  <p class="font-semibold text-gray-900 truncate max-w-[280px]" :title="cert.courseName">
                    {{ cert.courseName }}
                  </p>
                </td>
                <td class="px-5 py-4 text-gray-600">
                  {{ cert.studentName }}
                </td>
                <td class="px-5 py-4 text-gray-500">
                  {{ cert.instructorName || 'N/A' }}
                </td>
                <td class="px-5 py-4 text-gray-500 text-xs">
                  {{ formatDate(cert.issuedAt) }}
                </td>
                <td class="px-5 py-4 text-center">
                  <div class="flex items-center justify-center gap-2">
                    <button
                      @click="viewCertificate(cert)"
                      class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition"
                    >
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                      Xem
                    </button>
                    <button
                      @click="downloadCertificate(cert)"
                      :disabled="isDownloading"
                      class="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-bold text-amber-700 bg-amber-100 hover:bg-amber-200 rounded-lg transition disabled:opacity-50"
                    >
                      <div v-if="isDownloading && downloadingId === cert.id" class="w-3 h-3 border-2 border-amber-600 border-t-transparent rounded-full animate-spin"></div>
                      <svg v-else class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                      </svg>
                      Tải PNG
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50">
          <p class="text-sm text-gray-500">
            Hiển thị <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
            trong số <span class="font-semibold text-gray-700">{{ totalElements }}</span> chứng chỉ
          </p>
          <div class="flex items-center gap-1.5">
            <button :disabled="currentPage === 1" @click="currentPage = 1" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">«</button>
            <button :disabled="currentPage === 1" @click="currentPage--" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">‹</button>
            <button
              v-for="p in pageNumbers"
              :key="p"
              @click="currentPage = p"
              :class="['w-10 h-10 rounded-xl text-sm font-semibold transition border', currentPage === p ? 'bg-blue-600 text-white border-blue-600 shadow' : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300']"
            >{{ p }}</button>
            <button :disabled="isLastPage" @click="currentPage++" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">›</button>
            <button :disabled="isLastPage" @click="currentPage = totalPages" class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold">»</button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-gray-100">
        <div class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
          <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
        </div>
        <h4 class="text-xl font-bold text-gray-900 mb-2">Chưa có chứng chỉ nào</h4>
        <p class="text-gray-500 max-w-sm mx-auto mb-8">
          Hoàn thành 100% một khóa học để nhận chứng chỉ đầu tiên của bạn!
        </p>
        <router-link
          to="/dashboard/student/courses"
          class="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition shadow-lg shadow-blue-200"
        >
          Xem khóa học của tôi
        </router-link>
      </div>

      <!-- ═══════ Certificate Preview Modal ═══════ -->
      <Teleport to="body">
        <div v-if="showPreview" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showPreview = false"></div>
          <div class="relative w-full max-w-3xl">
            <!-- Close -->
            <button
              @click="showPreview = false"
              class="absolute -top-3 -right-3 z-10 w-10 h-10 bg-white rounded-full shadow-lg flex items-center justify-center hover:bg-gray-100 transition-colors"
            >
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>

            <!-- Certificate Preview Canvas -->
            <div class="bg-white rounded-xl shadow-2xl overflow-hidden">
              <canvas ref="previewCanvasRef" class="w-full h-auto"></canvas>
            </div>

            <!-- Download from preview -->
            <div class="flex justify-center mt-4">
              <button
                @click="downloadFromPreview"
                class="inline-flex items-center gap-2 px-6 py-3 bg-white text-gray-800 text-sm font-semibold rounded-xl shadow-lg hover:bg-gray-50 transition-colors"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                Tải chứng chỉ (PNG)
              </button>
            </div>
          </div>
        </div>
      </Teleport>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import SearchIcon from '@/components/dashboard/icons/SearchIcon.vue'
import { getMyCertificates, type Certificate } from '@/api/certificates'
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()

// ── State ──────────────────────────────────────────────────────────────────
const certificates = ref<Certificate[]>([])
const loading = ref(true)
const searchQuery = ref('')
const sortBy = ref('recent')
const showPreview = ref(false)
const previewCert = ref<Certificate | null>(null)
const previewCanvasRef = ref<HTMLCanvasElement | null>(null)
const isDownloading = ref(false)
const downloadingId = ref<number | null>(null)

// Pagination
const currentPage = ref(1)
const pageSize = 6

// ── Fetch ──────────────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    certificates.value = await getMyCertificates()
  } catch (err) {
    toastStore.error('Không thể tải danh sách chứng chỉ')
  } finally {
    loading.value = false
  }
})

// ── Filtered / Sorted ──────────────────────────────────────────────────────
const filteredCerts = computed(() => {
  let result = certificates.value

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(
      (c) =>
        c.courseName.toLowerCase().includes(q) ||
        c.certificateCode.toLowerCase().includes(q) ||
        c.studentName.toLowerCase().includes(q),
    )
  }

  if (sortBy.value === 'name') {
    result = [...result].sort((a, b) => a.courseName.localeCompare(b.courseName))
  }

  return result
})

// ── Pagination Computed ────────────────────────────────────────────────────
const totalElements = computed(() => filteredCerts.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalElements.value / pageSize)))
const isLastPage = computed(() => currentPage.value >= totalPages.value)
const displayStart = computed(() => totalElements.value === 0 ? 0 : (currentPage.value - 1) * pageSize + 1)
const displayEnd = computed(() => Math.min(currentPage.value * pageSize, totalElements.value))

const pageNumbers = computed(() => {
  const total = totalPages.value, current = currentPage.value, delta = 2
  const range: number[] = []
  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) range.push(i)
  return range
})

const paginatedCerts = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredCerts.value.slice(start, start + pageSize)
})

// Reset page on filter change
watch([searchQuery, sortBy], () => { currentPage.value = 1 })

// ── Helpers ────────────────────────────────────────────────────────────────
const formatDate = (isoString?: string) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

// ── Canvas Drawing ─────────────────────────────────────────────────────────
const drawCertificate = (canvas: HTMLCanvasElement, cert: Certificate) => {
  const W = 1200
  const H = 850
  canvas.width = W
  canvas.height = H
  const ctx = canvas.getContext('2d')!

  // Background
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, W, H)

  // Outer gold border
  ctx.strokeStyle = '#d4a017'
  ctx.lineWidth = 12
  ctx.strokeRect(20, 20, W - 40, H - 40)

  // Inner gold border
  ctx.strokeStyle = '#e6b422'
  ctx.lineWidth = 3
  ctx.strokeRect(36, 36, W - 72, H - 72)

  // Corner ornaments
  const cornerSize = 40
  const corners = [
    { x: 45, y: 45, dx: 1, dy: 1 },
    { x: W - 45, y: 45, dx: -1, dy: 1 },
    { x: 45, y: H - 45, dx: 1, dy: -1 },
    { x: W - 45, y: H - 45, dx: -1, dy: -1 },
  ]
  ctx.strokeStyle = '#d4a017'
  ctx.lineWidth = 3
  corners.forEach(({ x, y, dx, dy }) => {
    ctx.beginPath()
    ctx.moveTo(x, y + cornerSize * dy)
    ctx.lineTo(x, y)
    ctx.lineTo(x + cornerSize * dx, y)
    ctx.stroke()
  })

  // Shield icon (gold circle)
  const centerX = W / 2
  ctx.beginPath()
  ctx.arc(centerX, 120, 35, 0, Math.PI * 2)
  const goldGrad = ctx.createRadialGradient(centerX, 115, 5, centerX, 120, 35)
  goldGrad.addColorStop(0, '#f6d365')
  goldGrad.addColorStop(1, '#d4a017')
  ctx.fillStyle = goldGrad
  ctx.fill()

  // Checkmark in shield
  ctx.strokeStyle = '#ffffff'
  ctx.lineWidth = 4
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  ctx.beginPath()
  ctx.moveTo(centerX - 12, 120)
  ctx.lineTo(centerX - 3, 130)
  ctx.lineTo(centerX + 14, 110)
  ctx.stroke()

  // "EnglishPro" label
  ctx.fillStyle = '#9ca3af'
  ctx.font = '500 14px Arial, sans-serif'
  ctx.textAlign = 'center'
  ctx.fillText('E N G L I S H P R O', centerX, 190)

  // Main title
  ctx.fillStyle = '#1f2937'
  ctx.font = 'bold 42px Georgia, "Times New Roman", serif'
  ctx.fillText('CHỨNG CHỈ HOÀN THÀNH', centerX, 250)

  // Decorative line
  ctx.strokeStyle = '#d4a017'
  ctx.lineWidth = 2
  ctx.beginPath()
  ctx.moveTo(centerX - 60, 275)
  ctx.lineTo(centerX + 60, 275)
  ctx.stroke()

  // "Chứng nhận rằng"
  ctx.fillStyle = '#6b7280'
  ctx.font = '16px Arial, sans-serif'
  ctx.fillText('Chứng nhận rằng', centerX, 320)

  // Student name
  ctx.fillStyle = '#1d4ed8'
  ctx.font = 'bold 36px Georgia, "Times New Roman", serif'
  ctx.fillText(cert.studentName, centerX, 375)

  // "đã hoàn thành xuất sắc khóa học"
  ctx.fillStyle = '#6b7280'
  ctx.font = '16px Arial, sans-serif'
  ctx.fillText('đã hoàn thành xuất sắc khóa học', centerX, 420)

  // Course name
  ctx.fillStyle = '#1f2937'
  ctx.font = 'bold 28px Georgia, "Times New Roman", serif'
  const maxTextWidth = W - 200
  if (ctx.measureText(cert.courseName).width > maxTextWidth) {
    ctx.font = 'bold 22px Georgia, "Times New Roman", serif'
  }
  ctx.fillText(cert.courseName, centerX, 470, maxTextWidth)

  // Bottom section
  const bottomY = 600

  // Date
  ctx.strokeStyle = '#d1d5db'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(180, bottomY)
  ctx.lineTo(380, bottomY)
  ctx.stroke()
  ctx.fillStyle = '#6b7280'
  ctx.font = '12px Arial, sans-serif'
  ctx.fillText('Ngày cấp', 280, bottomY + 22)
  ctx.fillStyle = '#374151'
  ctx.font = '600 15px Arial, sans-serif'
  ctx.fillText(formatDate(cert.issuedAt), 280, bottomY + 42)

  // Instructor
  ctx.strokeStyle = '#d1d5db'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(W - 380, bottomY)
  ctx.lineTo(W - 180, bottomY)
  ctx.stroke()
  ctx.fillStyle = '#6b7280'
  ctx.font = '12px Arial, sans-serif'
  ctx.fillText('Giảng viên', W - 280, bottomY + 22)
  ctx.fillStyle = '#374151'
  ctx.font = '600 15px Arial, sans-serif'
  ctx.fillText(cert.instructorName || 'EnglishPro', W - 280, bottomY + 42)

  // Certificate code
  ctx.fillStyle = '#9ca3af'
  ctx.font = '13px "Courier New", monospace'
  ctx.fillText(`Mã xác minh: ${cert.certificateCode}`, centerX, H - 70)
}

// ── Actions ────────────────────────────────────────────────────────────────
const viewCertificate = async (cert: Certificate) => {
  previewCert.value = cert
  showPreview.value = true
  await nextTick()
  if (previewCanvasRef.value) {
    drawCertificate(previewCanvasRef.value, cert)
  }
}

const downloadFromPreview = () => {
  if (!previewCert.value || !previewCanvasRef.value) return
  triggerDownload(previewCanvasRef.value, previewCert.value.certificateCode)
}

const downloadCertificate = async (cert: Certificate) => {
  isDownloading.value = true
  downloadingId.value = cert.id
  try {
    const offscreen = document.createElement('canvas')
    drawCertificate(offscreen, cert)
    triggerDownload(offscreen, cert.certificateCode)
    toastStore.success('Đã tải chứng chỉ thành công!')
  } catch (err) {
    console.error('Download error:', err)
    toastStore.error('Có lỗi khi tải chứng chỉ. Vui lòng thử lại.')
  } finally {
    isDownloading.value = false
    downloadingId.value = null
  }
}

const triggerDownload = (canvas: HTMLCanvasElement, code: string) => {
  const link = document.createElement('a')
  link.download = `certificate-${code}.png`
  link.href = canvas.toDataURL('image/png')
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
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
