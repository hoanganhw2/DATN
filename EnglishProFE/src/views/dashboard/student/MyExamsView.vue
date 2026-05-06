<template>
  <DashboardLayout>
    <div class="space-y-7">
      <!-- Header Stats -->
      <div class="flex flex-wrap gap-3">
        <div
          class="flex items-center gap-2 bg-blue-50 border border-blue-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
          <span class="text-sm font-semibold text-blue-700">{{ totalExams }} Đề thi</span>
        </div>
        <div
          class="flex items-center gap-2 bg-green-50 border border-green-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-green-500 rounded-full"></div>
          <span class="text-sm font-semibold text-green-700">{{ toeicCount }} TOEIC</span>
        </div>
        <div
          class="flex items-center gap-2 bg-blue-50 border border-blue-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
          <span class="text-sm font-semibold text-blue-700">{{ ieltsCount }} IELTS</span>
        </div>
      </div>

      <!-- Search & Filter -->
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <div class="relative group flex-1 w-full">
          <svg
            class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 group-focus-within:text-blue-500 transition-colors"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm đề thi..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
          />
        </div>
        <div class="flex bg-gray-100 rounded-xl p-1 gap-1 shrink-0">
          <button
            v-for="tab in typeTabs"
            :key="tab.value"
            @click="activeType = tab.value"
            :class="[
              'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
              activeType === tab.value
                ? 'bg-white text-blue-600 shadow-sm'
                : 'text-gray-500 hover:text-gray-700',
            ]"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <div
          v-for="i in 6"
          :key="i"
          class="bg-white border border-gray-100 rounded-2xl p-5 shadow-sm animate-pulse"
        >
          <div class="h-5 bg-gray-200 rounded w-3/4 mb-3"></div>
          <div class="h-3 bg-gray-100 rounded w-1/2 mb-5"></div>
          <div class="h-10 bg-gray-100 rounded-xl w-full"></div>
        </div>
      </div>

      <!-- Exam Grid -->
      <div v-else-if="paginatedExams.length > 0" class="space-y-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          <div
            v-for="exam in paginatedExams"
            :key="exam.id"
            class="group bg-white border border-gray-100 rounded-2xl p-5 shadow-sm hover:shadow-lg hover:border-blue-200 transition-all duration-300"
          >
            <!-- Type Badge + Level -->
            <div class="flex items-center justify-between mb-4">
              <span
                :class="[
                  'px-2.5 py-1 rounded-lg text-xs font-bold uppercase tracking-wider',
                  exam.type === 'TOEIC'
                    ? 'bg-blue-50 text-blue-700 border border-blue-100'
                    : exam.type === 'IELTS'
                      ? 'bg-blue-50 text-blue-700 border border-blue-100'
                      : 'bg-gray-50 text-gray-600 border border-gray-100',
                ]"
                >{{ exam.type }}</span
              >
              <span class="text-xs text-gray-400 font-medium bg-gray-50 px-2 py-1 rounded-lg">
                {{ exam.duration }} phút
              </span>
            </div>

            <h3
              class="font-bold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors line-clamp-2 leading-snug"
            >
              {{ exam.title }}
            </h3>
            <p class="text-xs text-gray-400 mb-4 line-clamp-2">
              {{ exam.description || 'Luyện tập đề thi chuẩn quốc tế.' }}
            </p>

            <!-- Meta -->
            <div class="flex items-center gap-3 text-xs text-gray-400 mb-5">
              <span class="flex items-center gap-1"> 📝 {{ exam.totalQuestions }} câu hỏi </span>
              <span class="flex items-center gap-1"> 🏆 {{ exam.totalScore }} điểm </span>
            </div>

            <!-- Action -->
            <router-link
              :to="`/exam/${exam.id}`"
              class="block w-full text-center py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold rounded-xl transition-colors shadow-sm"
            >
              Làm bài thi →
            </router-link>
          </div>
        </div>

        <!-- Pagination -->
        <div
          v-if="totalPages > 1"
          class="flex flex-col sm:flex-row items-center justify-between gap-4 pt-4 border-t border-gray-100"
        >
          <p class="text-sm text-gray-500">
            Hiển thị
            <span class="font-semibold text-gray-700"
              >{{ (currentPage - 1) * pageSize + 1 }}–{{
                Math.min(currentPage * pageSize, filteredExams.length)
              }}</span
            >
            trong số <span class="font-semibold text-gray-700">{{ filteredExams.length }}</span> đề
            thi
          </p>
          <div class="flex items-center gap-1">
            <button
              :disabled="currentPage === 1"
              @click="currentPage--"
              class="flex items-center gap-1 px-3 py-2 text-sm font-medium rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
            >
              ← Trước
            </button>
            <template v-for="p in totalPages" :key="p">
              <button
                v-if="p === 1 || p === totalPages || Math.abs(p - currentPage) <= 1"
                @click="currentPage = p"
                :class="[
                  'w-9 h-9 text-sm font-bold rounded-lg transition-all',
                  currentPage === p
                    ? 'bg-blue-600 text-white shadow-sm shadow-blue-200'
                    : 'text-gray-600 hover:bg-gray-100 border border-gray-200',
                ]"
              >
                {{ p }}
              </button>
              <span
                v-else-if="
                  (p === currentPage - 2 && p > 2) || (p === currentPage + 2 && p < totalPages - 1)
                "
                class="w-9 h-9 flex items-center justify-center text-gray-400 text-sm"
                >…</span
              >
            </template>
            <button
              :disabled="currentPage >= totalPages"
              @click="currentPage++"
              class="flex items-center gap-1 px-3 py-2 text-sm font-medium rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
            >
              Sau →
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else
        class="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-gray-100"
      >
        <div class="text-5xl mb-4">📝</div>
        <h4 class="text-xl font-bold text-gray-900 mb-2">Không tìm thấy đề thi</h4>
        <p class="text-gray-500 max-w-sm mx-auto mb-8">
          {{
            searchQuery || activeType !== 'ALL'
              ? 'Không có đề thi phù hợp với bộ lọc.'
              : 'Hiện chưa có đề thi nào. Hãy quay lại sau!'
          }}
        </p>
        <router-link
          to="/exam"
          class="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
        >
          Xem tất cả bài thi
        </router-link>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getPublishedExams } from '@/api/exam'

const exams = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')
const activeType = ref('ALL')
const currentPage = ref(1)
const pageSize = 9

const typeTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'TOEIC', value: 'TOEIC' },
  { label: 'IELTS', value: 'IELTS' },
  { label: 'Khác', value: 'GENERAL' },
]

const totalExams = computed(() => exams.value.length)
const toeicCount = computed(() => exams.value.filter((e) => (e.examType || e.type) === 'TOEIC').length)
const ieltsCount = computed(() => exams.value.filter((e) => (e.examType || e.type) === 'IELTS').length)

const filteredExams = computed(() => {
  let result = exams.value
  if (activeType.value !== 'ALL') {
    result = result.filter((e) => (e.examType || e.type) === activeType.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter((e) => e.title.toLowerCase().includes(q))
  }
  return result
})

const totalPages = computed(() => Math.ceil(filteredExams.value.length / pageSize))
const paginatedExams = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredExams.value.slice(start, start + pageSize)
})

watch([searchQuery, activeType], () => {
  currentPage.value = 1
})

const fetchExams = async () => {
  try {
    loading.value = true
    const res = await getPublishedExams({ size: 100 })
    const pageData = res?.data || res
    exams.value = (pageData?.content || []).map((e: any) => ({
      ...e,
      type: e.examType || e.type,
      totalQuestions: e.totalQuestions || 0,
    }))
  } catch (e) {
    console.error('Lỗi tải đề thi:', e)
  } finally {
    loading.value = false
  }
}

onMounted(fetchExams)
</script>
