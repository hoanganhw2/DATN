<template>
  <DashboardLayout>
    <div class="w-full">
      <!-- Actions & Filters -->
      <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
        <!-- Filter Tabs -->
        <div class="flex gap-3">
          <button
            v-for="tab in filterTabs"
            :key="tab.value"
            @click="flashcardStore.setFilter(tab.value)"
            class="px-4 py-2 rounded-lg font-medium transition-colors"
            :class="
              flashcardStore.filter === tab.value
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 border border-gray-200 hover:border-gray-300'
            "
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- Create Button -->
        <BaseButton variant="primary" @click="navigateToCreate">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          Tạo bộ thẻ mới
        </BaseButton>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="bg-white rounded-xl h-48 animate-pulse" />
      </div>

      <!-- Empty State -->
      <div v-else-if="flashcardStore.filteredDecks.length === 0" class="text-center py-12">
        <svg
          class="w-20 h-20 mx-auto text-gray-300 mb-4"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="1.5"
            d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V7a2 2 0 012-2h6a2 2 0 012 2v2M7 7a2 2 0 012-2h6a2 2 0 012 2"
          />
        </svg>
        <p class="text-gray-600 text-lg">Chưa có bộ thẻ nào</p>
        <p class="text-gray-500 mt-2">Hãy tạo bộ thẻ mới để bắt đầu học tập</p>
      </div>

      <!-- Deck Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="deck in flashcardStore.filteredDecks"
          :key="deck.id"
          @click="navigateToDeck(deck)"
          class="group bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg hover:border-blue-400 transition-all cursor-pointer"
        >
          <!-- Deck Image -->
          <div class="relative h-32 bg-linear-to-br from-blue-500 to-blue-600 overflow-hidden">
            <img
              v-if="deck.imageUrl"
              :src="deck.imageUrl"
              :alt="deck.title"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <svg class="w-12 h-12 text-blue-300" fill="currentColor" viewBox="0 0 20 20">
                <path
                  d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10c1.215-.165 2.444-.374 3.5-.604 1.086-.189 2.15-.122 3 .304v-9.704z"
                />
                <path
                  d="M15.5 4c-1.255 0-2.443.29-3.5.804v10c1.215-.165 2.444-.374 3.5-.604 1.086-.189 2.15-.122 3 .304v-9.704c-1.057-.514-2.245-.804-3.5-.804z"
                />
              </svg>
            </div>

            <!-- Due Badge -->
            <div
              v-if="deck.dueToday > 0"
              class="absolute top-3 right-3 bg-red-500 text-white rounded-full px-3 py-1 text-sm font-bold shadow-md"
            >
              {{ deck.dueToday }}
            </div>

            <!-- Delete Button -->
            <button
              @click.stop="handleDeleteDeck(deck)"
              class="absolute top-3 left-3 bg-red-500 hover:bg-red-600 text-white p-2 rounded-full shadow-md transition-colors opacity-0 group-hover:opacity-100"
              title="Xóa bộ thẻ"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>

          <!-- Deck Info -->
          <div class="p-4">
            <!-- Title -->
            <h3 class="font-bold text-gray-900 text-lg mb-1 line-clamp-2">{{ deck.title }}</h3>
            <p class="text-sm text-gray-600 mb-4 line-clamp-2">
              {{ deck.description || 'Không có mô tả' }}
            </p>

            <!-- Stats -->
            <div class="flex items-center justify-between text-sm text-gray-600 mb-3">
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v2h8v-2zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-2a4 4 0 00-8 0v2h8z"
                  />
                </svg>
                {{ deck.totalCards }} thẻ
              </span>
              <span class="font-semibold text-blue-600">{{ learnedPercent(deck) }}%</span>
            </div>

            <!-- Progress Bar -->
            <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
              <div
                class="bg-blue-600 h-full transition-all"
                :style="{ width: learnedPercent(deck) + '%' }"
              />
            </div>

            <!-- Due Today Label -->
            <div
              v-if="deck.dueToday > 0"
              class="mt-3 px-2 py-1 bg-red-50 rounded text-xs text-red-700 font-medium"
            >
              📌 Cần học hôm nay: {{ deck.dueToday }} thẻ
            </div>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useFlashcardStore } from '@/stores/flashcard'
import { getFlashcardDecks, deleteFlashcardDeck } from '@/api/flashcard'
import type { FlashcardDeck } from '@/stores/flashcard'
import { useDialog } from '@/composables/useDialog'

const router = useRouter()
const flashcardStore = useFlashcardStore()
const { confirm, alert } = useDialog()
const isLoading = ref(true)

const filterTabs = [
  { value: 'all' as const, label: 'Tất cả' },
  { value: 'learning' as const, label: 'Đang học' },
  { value: 'completed' as const, label: 'Đã hoàn thành' },
]

// Calculate learned percentage (cards reviewed at least once)
const learnedPercent = (deck: FlashcardDeck) => {
  if (!deck.totalCards || deck.totalCards === 0) return 0
  return Math.round(((deck.learnedCards || 0) / deck.totalCards) * 100)
}

// Navigation
const navigateToDeck = (deck: FlashcardDeck) => {
  router.push(`/flashcard/deck/${deck.id}`)
}

const navigateToCreate = () => {
  router.push('/flashcard/create')
}

// Delete deck
const handleDeleteDeck = async (deck: FlashcardDeck) => {
  const isConfirmed = await confirm(
    'Xác nhận xóa',
    `Bạn có chắc chắn muốn xóa bộ thẻ "${deck.title}" không? Hành động này không thể hoàn tác.`,
    'error'
  )
  
  if (isConfirmed) {
    try {
      await deleteFlashcardDeck(deck.id)
      flashcardStore.removeDeck(deck.id)
    } catch (error) {
      console.error('Error deleting deck:', error)
      await alert('Lỗi', 'Đã xảy ra lỗi khi xóa bộ thẻ. Vui lòng thử lại.', 'error')
    }
  }
}

// Fetch decks on mount
onMounted(async () => {
  try {
    isLoading.value = true
    const decks = await getFlashcardDecks()
    flashcardStore.setDecks(decks)
  } catch (error) {
    console.error('Error loading decks:', error)
  } finally {
    isLoading.value = false
  }
})
</script>
