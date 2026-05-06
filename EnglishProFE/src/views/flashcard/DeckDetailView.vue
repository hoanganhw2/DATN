<template>
  <DashboardLayout>
    <div class="w-full">
      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-20">
        <div class="text-center">
          <div
            class="inline-flex items-center justify-center w-12 h-12 bg-blue-100 rounded-full mb-4"
          >
            <svg
              class="w-6 h-6 text-blue-600 animate-spin"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
          </div>
          <p class="text-gray-600">Đang tải dữ liệu bộ thẻ...</p>
        </div>
      </div>

      <!-- Main Content -->
      <div v-else-if="deck" class="space-y-8 animate-in fade-in duration-500">
        <!-- Header -->
        <div class="flex flex-col md:flex-row md:items-start justify-between gap-4">
          <div class="flex items-start gap-4">
            <button
              @click="goBack"
              class="p-2 -ml-2 text-gray-400 hover:text-gray-900 rounded-full hover:bg-gray-100 transition-colors mt-1"
              title="Quay lại danh sách"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M10 19l-7-7m0 0l7-7m-7 7h18"
                />
              </svg>
            </button>
            <div class="space-y-1">
              <h1 class="text-3xl font-bold text-gray-900 tracking-tight">{{ deck.title }}</h1>
              <p v-if="deck.description" class="text-gray-500 text-lg">{{ deck.description }}</p>
              <p v-else class="text-gray-400 italic">Không có mô tả cho bộ thẻ này.</p>
            </div>
          </div>

          <div class="flex flex-wrap items-center gap-3 shrink-0">
            <button
              @click="navigateToAddCards"
              class="inline-flex items-center justify-center gap-2 px-5 py-3 rounded-xl border-2 border-gray-200 text-gray-700 font-bold bg-white hover:border-gray-300 hover:bg-gray-50 active:scale-95 transition-all shadow-xs"
            >
              <svg
                class="w-5 h-5 text-gray-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2.5"
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Thêm thẻ
            </button>
            <button
              v-if="deck.flashcards && deck.flashcards.length > 0"
              @click="startReview"
              class="inline-flex items-center justify-center gap-2 px-8 py-3 rounded-xl text-white font-bold bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 active:scale-95 transition-all shadow-lg shadow-blue-500/30"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M8 5.14v14l11-7-11-7z" />
              </svg>
              Học ngay
            </button>
          </div>
        </div>

        <!-- Progress Overview -->
        <div v-if="stats" class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-xs">
            <p class="text-sm font-medium text-gray-500 mb-1">Tổng cộng</p>
            <p class="text-2xl font-bold text-gray-900">{{ deck.totalCards }}</p>
          </div>
          <div
            class="bg-white p-5 rounded-2xl border border-gray-100 shadow-xs border-l-4 border-l-blue-500"
          >
            <p class="text-sm font-medium text-gray-500 mb-1">Thẻ mới</p>
            <p class="text-2xl font-bold text-blue-600">{{ stats.newCards || 0 }}</p>
          </div>
          <div
            class="bg-white p-5 rounded-2xl border border-gray-100 shadow-xs border-l-4 border-l-amber-500"
          >
            <p class="text-sm font-medium text-gray-500 mb-1">Đang học</p>
            <p class="text-2xl font-bold text-amber-600">
              {{ (stats.learnedCards || 0) - (stats.masteredCards || 0) }}
            </p>
          </div>
          <div
            class="bg-white p-5 rounded-2xl border border-gray-100 shadow-xs border-l-4 border-l-green-500"
          >
            <p class="text-sm font-medium text-gray-500 mb-1">Thành thạo</p>
            <p class="text-2xl font-bold text-green-600">{{ stats.masteredCards || 0 }}</p>
          </div>
        </div>

        <!-- Cards List -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-xs overflow-hidden">
          <div
            class="px-6 py-4 border-b border-gray-50 flex items-center justify-between bg-gray-50/50"
          >
            <h2 class="text-lg font-bold text-gray-900">Nội dung bộ thẻ</h2>
            <span class="text-xs font-medium px-2.5 py-1 bg-gray-200 text-gray-700 rounded-full">
              {{ deck.flashcards?.length || 0 }} thẻ
            </span>
          </div>

          <!-- Empty State -->
          <div v-if="!deck.flashcards || deck.flashcards.length === 0" class="p-16 text-center">
            <div
              class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4"
            >
              <svg
                class="w-8 h-8 text-gray-300"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
                />
              </svg>
            </div>
            <p class="text-gray-500 font-medium">Bộ thẻ này hiện đang trống</p>
            <div class="mt-4">
              <BaseButton variant="outline" size="sm" @click="navigateToAddCards"
                >Cập nhật nội dung</BaseButton
              >
            </div>
          </div>

          <!-- Cards Table -->
          <div v-else class="overflow-x-auto custom-scrollbar">
            <table class="w-full text-left border-collapse whitespace-nowrap">
              <thead>
                <tr class="text-xs font-semibold text-gray-400 uppercase tracking-wider">
                  <th class="px-6 py-4 font-medium">Mặt trước</th>
                  <th class="px-6 py-4 font-medium">Mặt sau</th>
                  <th class="px-6 py-4 font-medium">Độ khó / Trạng thái</th>
                  <th class="px-6 py-4 font-medium text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr
                  v-for="card in deck.flashcards"
                  :key="card.id"
                  class="hover:bg-blue-50/30 transition-colors group"
                >
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                      <div
                        v-if="card.frontImageUrl"
                        class="w-10 h-10 rounded shadow-sm overflow-hidden flex-shrink-0"
                      >
                        <img :src="card.frontImageUrl" class="w-full h-full object-cover" />
                      </div>
                      <span class="font-semibold text-gray-900">{{ card.frontText }}</span>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <div class="space-y-1">
                      <p class="text-gray-700">{{ card.backText }}</p>
                      <p v-if="card.exampleSentence" class="text-xs text-blue-500 italic">
                        {{ card.exampleSentence }}
                      </p>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-2">
                      <span
                        v-if="card.status"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider shadow-sm"
                        :class="getStatusClass(card.status)"
                      >
                        {{ getStatusLabel(card.status) }}
                      </span>
                      <span v-else class="text-xs text-gray-400 font-medium">Chưa học</span>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <div
                      class="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      <button
                        @click="handleDeleteCard(card.id)"
                        class="p-2 text-gray-400 hover:text-red-600 transition-colors"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                          />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import {
  getDeckDetail,
  getFlashcardStats,
  deleteFlashcard,
  type FlashcardDeckDetail,
  type DeckProgress,
} from '@/api/flashcard'
import { useToastStore } from '@/stores/toast'
import { useDialog } from '@/composables/useDialog'

const router = useRouter()
const route = useRoute()
const toastStore = useToastStore()
const dialog = useDialog()

const deckId = computed(() => Number(route.params.id))
const deck = ref<FlashcardDeckDetail | null>(null)
const stats = ref<DeckProgress | null>(null)
const isLoading = ref(true)

const loadData = async () => {
  try {
    isLoading.value = true
    const [deckData, statsData] = await Promise.all([
      getDeckDetail(deckId.value),
      getFlashcardStats(deckId.value),
    ])
    deck.value = deckData
    stats.value = statsData
  } catch (error) {
    console.error('Error loading deck:', error)
    toastStore.error('Không thể tải dữ liệu bộ thẻ.')
  } finally {
    isLoading.value = false
  }
}

const goBack = () => {
  router.push('/flashcard')
}

const startReview = () => {
  router.push(`/flashcard/review/${deckId.value}`)
}

const navigateToAddCards = () => {
  if (deck.value) {
    router.push({
      path: '/flashcard/create',
      query: { deckId: deckId.value, deckTitle: deck.value.title },
    })
  }
}

const handleDeleteCard = async (cardId: number) => {
  const confirmed = await dialog.confirm('Xóa thẻ', 'Bạn có chắc muốn xóa thẻ này khỏi bộ thẻ?', 'warning')
  if (!confirmed) return

  try {
    await deleteFlashcard(cardId)
    toastStore.success('Đã xóa thẻ.')
    if (deck.value) {
      deck.value.flashcards = deck.value.flashcards.filter(c => c.id !== cardId)
      deck.value.totalCards--
    }
  } catch (error) {
    toastStore.error('Không thể xóa thẻ.')
  }
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    new: 'Mới',
    learning: 'Đang học',
    review: 'Cần ôn',
    mastered: 'Thành thạo',
  }
  return labels[status] || status
}

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    new: 'bg-blue-100 text-blue-700',
    learning: 'bg-amber-100 text-amber-700',
    review: 'bg-red-100 text-red-700',
    mastered: 'bg-green-100 text-green-700',
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

onMounted(() => {
  loadData()
})
</script>
