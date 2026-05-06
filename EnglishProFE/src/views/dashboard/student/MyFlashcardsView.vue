<template>
  <DashboardLayout>
    <div class="space-y-7">
      <!-- Header Stats -->
      <div class="flex flex-wrap gap-3">
        <div
          class="flex items-center gap-2 bg-blue-50 border border-blue-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
          <span class="text-sm font-semibold text-blue-700">{{ totalDecks }} Bộ từ</span>
        </div>
        <div
          class="flex items-center gap-2 bg-blue-50 border border-blue-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
          <span class="text-sm font-semibold text-blue-700">{{ totalCards }} Thẻ từ</span>
        </div>
        <div
          class="flex items-center gap-2 bg-green-50 border border-green-100 rounded-xl px-4 py-2 shadow-sm"
        >
          <div class="w-2 h-2 bg-green-500 rounded-full"></div>
          <span class="text-sm font-semibold text-green-700">{{ masteredCards }} Đã thuộc</span>
        </div>
      </div>

      <!-- Search & Actions -->
      <div class="flex flex-col sm:flex-row gap-4 items-center">
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
            placeholder="Tìm kiếm bộ flashcard..."
            class="w-full bg-white border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
          />
        </div>
        <router-link
          to="/flashcard/create"
          class="shrink-0 inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white text-sm font-bold rounded-xl hover:bg-blue-700 transition-colors shadow-sm shadow-blue-200"
        >
          + Tạo bộ mới
        </router-link>
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
          <div class="flex gap-3">
            <div class="h-8 bg-gray-100 rounded-lg flex-1"></div>
            <div class="h-8 bg-gray-100 rounded-lg flex-1"></div>
          </div>
        </div>
      </div>

      <!-- Deck Grid -->
      <div v-else-if="paginatedDecks.length > 0" class="space-y-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          <div
            v-for="deck in paginatedDecks"
            :key="deck.id"
            class="group bg-white border border-gray-100 rounded-2xl p-5 shadow-sm hover:shadow-lg hover:border-blue-200 transition-all duration-300"
          >
            <!-- Deck Icon + Title -->
            <div class="flex items-start justify-between mb-3">
              <div
                class="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center text-xl shrink-0"
              >
                🃏
              </div>
              <span class="text-xs font-bold text-gray-400 bg-gray-50 px-2 py-1 rounded-lg">
                {{ deck.totalCards }} thẻ
              </span>
            </div>
            <h3
              class="font-bold text-gray-900 mb-1 group-hover:text-blue-600 transition-colors line-clamp-1"
            >
              {{ deck.title }}
            </h3>
            <p class="text-xs text-gray-400 mb-4 line-clamp-1">
              {{ deck.description || 'Không có mô tả' }}
            </p>

            <!-- Progress mini -->
            <div class="mb-4">
              <div class="flex justify-between text-xs text-gray-400 mb-1">
                <span>Tiến độ</span>
                <span class="font-bold text-gray-600"
                  >{{ deck.masteredCards }}/{{ deck.totalCards }}</span
                >
              </div>
              <div class="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                <div
                  class="h-full bg-gradient-to-r from-blue-500 to-indigo-500 rounded-full transition-all duration-700"
                  :style="{
                    width: deck.totalCards
                      ? (deck.masteredCards / deck.totalCards) * 100 + '%'
                      : '0%',
                  }"
                ></div>
              </div>
            </div>

            <!-- Due today badge -->
            <div v-if="deck.dueToday > 0" class="mb-3">
              <span
                class="inline-flex items-center gap-1 text-xs font-bold text-orange-700 bg-orange-50 border border-orange-100 px-2 py-1 rounded-lg"
              >
                ⏰ {{ deck.dueToday }} thẻ cần ôn hôm nay
              </span>
            </div>

            <!-- Actions -->
            <div class="flex gap-2">
              <router-link
                :to="`/flashcard/review/${deck.id}`"
                class="flex-1 text-center py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl transition-colors shadow-sm"
              >
                Ôn tập
              </router-link>
              <router-link
                :to="`/flashcard/deck/${deck.id}`"
                class="flex-1 text-center py-2 border border-gray-200 text-gray-600 hover:bg-gray-50 text-xs font-bold rounded-xl transition-colors"
              >
                Xem chi tiết
              </router-link>
            </div>
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
                Math.min(currentPage * pageSize, filteredDecks.length)
              }}</span
            >
            trong số <span class="font-semibold text-gray-700">{{ filteredDecks.length }}</span> bộ
            flashcard
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
        <div class="text-5xl mb-4">🃏</div>
        <h4 class="text-xl font-bold text-gray-900 mb-2">Chưa có bộ flashcard nào</h4>
        <p class="text-gray-500 max-w-sm mx-auto mb-8">
          {{
            searchQuery
              ? 'Không tìm thấy bộ flashcard phù hợp.'
              : 'Tạo bộ flashcard đầu tiên để bắt đầu luyện từ vựng hiệu quả!'
          }}
        </p>
        <router-link
          to="/flashcard/create"
          class="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
        >
          + Tạo bộ flashcard
        </router-link>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getFlashcardDecks, type FlashcardDeck } from '@/api/flashcard'

const decks = ref<FlashcardDeck[]>([])
const loading = ref(true)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 9

const totalDecks = computed(() => decks.value.length)
const totalCards = computed(() => decks.value.reduce((s, d) => s + d.totalCards, 0))
const masteredCards = computed(() => decks.value.reduce((s, d) => s + d.masteredCards, 0))

const filteredDecks = computed(() => {
  if (!searchQuery.value.trim()) return decks.value
  const q = searchQuery.value.toLowerCase()
  return decks.value.filter(
    (d) => d.title.toLowerCase().includes(q) || (d.description ?? '').toLowerCase().includes(q),
  )
})

const totalPages = computed(() => Math.ceil(filteredDecks.value.length / pageSize))
const paginatedDecks = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredDecks.value.slice(start, start + pageSize)
})

watch(searchQuery, () => {
  currentPage.value = 1
})

const fetchDecks = async () => {
  try {
    loading.value = true
    decks.value = await getFlashcardDecks()
  } catch (e) {
    console.error('Lỗi tải flashcard:', e)
  } finally {
    loading.value = false
  }
}

onMounted(fetchDecks)
</script>
