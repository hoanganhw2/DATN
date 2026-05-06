<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop -->
    <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm" @click="closeModal"></div>

    <!-- Modal Content -->
    <div
      class="relative bg-white rounded-2xl shadow-xl w-full max-w-5xl max-h-[90vh] flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-200"
    >
      <!-- Header -->
      <div
        class="flex items-center justify-between px-6 py-4 border-b border-gray-100 bg-gradient-to-r from-fuchsia-50 to-blue-50"
      >
        <h3 class="text-xl font-bold text-gray-900 flex items-center gap-2">
          <SparklesIcon class="w-6 h-6 text-amber-500" />
          Sinh Flashcard bằng AI
          <span class="text-xs font-normal text-gray-400 ml-1">(Kèm ảnh minh họa tự động)</span>
        </h3>
        <button
          @click="closeModal"
          class="p-2 text-gray-400 hover:bg-white/80 hover:text-gray-600 rounded-lg transition-colors"
        >
          <XIcon class="w-5 h-5" />
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 overflow-y-auto flex-1">
        <!-- Step 1: Input text -->
        <div v-if="!hasResults" class="space-y-4">
          <label class="block text-sm font-semibold text-gray-700">
            Dán đoạn văn bản Tiếng Anh (Reading passage, News, Transcript...)
          </label>
          <textarea
            v-model="sourceText"
            rows="8"
            placeholder="Ví dụ: Climate change refers to long-term shifts in temperatures and weather patterns..."
            class="w-full bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all hover:border-gray-300 resize-y"
            :disabled="isLoading"
          ></textarea>

          <!-- Options Row -->
          <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100">
            <label class="flex items-center gap-2.5 cursor-pointer select-none">
              <div
                class="relative w-10 h-5 rounded-full transition-colors duration-200 cursor-pointer"
                :class="withImages ? 'bg-blue-500' : 'bg-gray-300'"
                @click="withImages = !withImages"
              >
                <div
                  class="absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full shadow transition-transform duration-200"
                  :class="withImages ? 'translate-x-5' : 'translate-x-0'"
                />
              </div>
              <span class="text-sm font-medium text-gray-700">🖼️ Tự động thêm ảnh minh họa</span>
            </label>
            <span class="text-xs text-gray-400">(Tải ảnh từ Unsplash theo từ khóa mỗi thẻ)</span>
          </div>

          <button
            @click="generateSelected"
            :disabled="!sourceText.trim() || isLoading"
            class="w-full flex items-center justify-center gap-2 py-3.5 px-4 rounded-xl font-bold text-white transition-all shadow-md mt-4 disabled:opacity-50 disabled:cursor-not-allowed"
            :class="
              isLoading
                ? 'bg-gradient-to-r from-gray-400 to-gray-500'
                : 'bg-gradient-to-r from-fuchsia-600 to-blue-600 hover:from-fuchsia-500 hover:to-blue-500 shadow-blue-500/30'
            "
          >
            <template v-if="isLoading">
              <Loader2Icon class="w-5 h-5 animate-spin" />
              {{ loadingMessage }}
            </template>
            <template v-else>
              <SparklesIcon class="w-5 h-5" />
              Tự động tạo Flashcard bằng AI
            </template>
          </button>
        </div>

        <!-- Step 2: Show Results -->
        <div v-else class="space-y-5">
          <div class="flex items-center justify-between">
            <h4 class="font-bold text-gray-900 flex items-center gap-2">
              <CheckCircleIcon class="w-5 h-5 text-green-500" />
              Đã trích xuất {{ generatedCards.length }} từ vựng
              <span v-if="withImages" class="text-xs font-normal text-gray-400">(kèm ảnh minh họa)</span>
            </h4>
            <button
              @click="resetForm"
              class="text-sm font-medium text-blue-600 hover:text-blue-700"
            >
              Quét văn bản khác
            </button>
          </div>

          <!-- Cards Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="(card, index) in generatedCards"
              :key="index"
              class="bg-white border border-gray-100 rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition-all group"
            >
              <!-- Image Section -->
              <div class="relative h-36 bg-gradient-to-br from-blue-50 to-fuchsia-50 overflow-hidden">
                <!-- Loading skeleton -->
                <div
                  v-if="card.imageLoading"
                  class="absolute inset-0 flex items-center justify-center"
                >
                  <div class="w-6 h-6 border-2 border-blue-400 border-t-transparent rounded-full animate-spin"></div>
                </div>

                <!-- Image -->
                <img
                  v-if="card.imageUrl && !card.imageLoading && !card.imageError"
                  :src="card.imageUrl"
                  :alt="card.word"
                  class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                  @error="card.imageError = true"
                />

                <!-- Fallback: no image -->
                <div
                  v-else-if="!card.imageLoading"
                  class="absolute inset-0 flex flex-col items-center justify-center gap-1 text-gray-300"
                >
                  <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                      d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                  <span class="text-xs">Không có ảnh</span>
                </div>

                <!-- Delete button -->
                <button
                  @click="removeCard(index)"
                  class="absolute top-2 right-2 w-7 h-7 bg-red-500 text-white rounded-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-md hover:bg-red-600"
                  title="Xóa thẻ này"
                >
                  <TrashIcon class="w-3.5 h-3.5" />
                </button>

                <!-- Refresh image button -->
                <button
                  v-if="withImages"
                  @click="refreshImage(card)"
                  :disabled="card.imageLoading"
                  class="absolute top-2 left-2 w-7 h-7 bg-white/80 text-gray-600 rounded-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-md hover:bg-white disabled:opacity-50"
                  title="Tải ảnh khác"
                >
                  <svg class="w-3.5 h-3.5" :class="{'animate-spin': card.imageLoading}" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                  </svg>
                </button>
              </div>

              <!-- Card Content -->
              <div class="p-4 space-y-2.5">
                <div class="flex items-start gap-2">
                  <div class="flex-1">
                    <input
                      v-model="card.word"
                      class="font-bold text-lg text-gray-900 w-full border-b border-transparent hover:border-gray-200 focus:border-blue-500 focus:outline-none bg-transparent transition-colors"
                    />
                    <div class="flex items-center gap-2 mt-0.5">
                      <input
                        v-model="card.type"
                        class="text-xs font-semibold px-2 py-0.5 bg-gray-100 text-gray-600 rounded w-16 text-center focus:outline-none focus:ring-1 focus:ring-blue-500"
                      />
                      <input
                        v-model="card.phonetic"
                        class="text-sm text-gray-500 w-full focus:outline-none focus:border-b focus:border-blue-500 bg-transparent font-mono"
                      />
                    </div>
                  </div>
                </div>

                <div class="space-y-1.5">
                  <div class="relative">
                    <span class="text-[10px] font-bold text-blue-600 absolute left-1 top-2">VI</span>
                    <input
                      v-model="card.definition_vi"
                      class="w-full text-sm font-medium text-gray-800 pl-6 border border-transparent hover:border-gray-200 focus:border-blue-500 focus:outline-none rounded px-2 py-1.5 bg-gray-50 transition-colors"
                    />
                  </div>
                  <div class="relative">
                    <span class="text-[10px] font-bold text-gray-400 absolute left-1 top-2">EN</span>
                    <input
                      v-model="card.definition_en"
                      class="w-full text-sm text-gray-600 pl-6 border border-transparent hover:border-gray-200 focus:border-blue-500 focus:outline-none rounded px-2 py-1 transition-colors"
                    />
                  </div>
                  <div class="relative">
                    <span class="text-[10px] font-bold text-gray-400 absolute left-1 top-1.5">EX</span>
                    <textarea
                      v-model="card.example"
                      rows="2"
                      class="w-full text-sm italic text-gray-500 pl-6 border border-transparent hover:border-gray-200 focus:border-blue-500 focus:outline-none rounded px-2 py-1 transition-colors resize-none"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div
        v-if="hasResults"
        class="p-6 border-t border-gray-100 bg-gray-50 flex items-center justify-between gap-3"
      >
        <p class="text-xs text-gray-400">
          Ảnh được lấy từ Unsplash · Bạn có thể chỉnh sửa nội dung trước khi lưu
        </p>
        <div class="flex items-center gap-3">
          <button
            @click="closeModal"
            class="px-5 py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-200 bg-gray-100 rounded-xl transition-colors"
          >
            Hủy bỏ
          </button>
          <button
            @click="saveToDeck"
            class="px-5 py-2.5 text-sm font-semibold text-white bg-gradient-to-r from-fuchsia-600 to-blue-600 hover:from-fuchsia-500 hover:to-blue-500 rounded-xl transition-all shadow-md flex items-center gap-2"
          >
            <SaveIcon class="w-4 h-4" />
            Lưu tất cả vào Deck
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import api from '@/api/axios'
import SparklesIcon from '@/components/dashboard/icons/SparklesIcon.vue'
import XIcon from '@/components/dashboard/icons/XIcon.vue'
import TrashIcon from '@/components/dashboard/icons/TrashIcon.vue'
import { Loader2, CheckCircle, Save } from 'lucide-vue-next'
import { useDialog } from '@/composables/useDialog'

const dialog = useDialog()

const Loader2Icon = Loader2
const CheckCircleIcon = CheckCircle
const SaveIcon = Save

// ---- Types ----
interface GeneratedCard {
  word: string
  type: string
  phonetic: string
  definition_en: string
  definition_vi: string
  example: string
  imageUrl: string
  imageLoading: boolean
  imageError: boolean
}

const props = defineProps<{
  isOpen: boolean
  /** Words already in the deck — passed to AI to avoid duplicates */
  existingWords?: string[]
}>()

const emit = defineEmits<{
  'update:isOpen': [value: boolean]
  save: [cards: any[]]
}>()

const sourceText = ref('')
const isLoading = ref(false)
const loadingMessage = ref('AI đang đọc và quét dữ liệu...')
const withImages = ref(true)
const generatedCards = ref<GeneratedCard[]>([])

const hasResults = computed(() => generatedCards.value.length > 0)

// ---- Unsplash Image Fetcher (no API key required) ----
const fetchImageForWord = (word: string): string => {
  const seed = Math.floor(Math.random() * 1000)
  // Use a reliable image source that doesn't require auth
  return `https://source.unsplash.com/320x200/?${encodeURIComponent(word)},english&sig=${seed}`
}

const loadImagesForCards = async (cards: GeneratedCard[]) => {
  // Load images in chunks of 4 to avoid overwhelming the browser
  const chunkSize = 4
  for (let i = 0; i < cards.length; i += chunkSize) {
    const chunk = cards.slice(i, i + chunkSize)
    await Promise.all(
      chunk.map(async (card) => {
        try {
          card.imageLoading = true
          card.imageError = false
          card.imageUrl = fetchImageForWord(card.word)
          // Small delay to stagger the image loads
          await new Promise((resolve) => setTimeout(resolve, 100))
          card.imageLoading = false
        } catch {
          card.imageLoading = false
          card.imageError = true
        }
      })
    )
  }
}

const refreshImage = async (card: GeneratedCard) => {
  card.imageError = false
  card.imageLoading = true
  card.imageUrl = ''
  await new Promise((resolve) => setTimeout(resolve, 200))
  card.imageUrl = fetchImageForWord(card.word)
  card.imageLoading = false
}

// ---- Actions ----
const closeModal = () => {
  if (isLoading.value) return
  emit('update:isOpen', false)
}

const resetForm = () => {
  sourceText.value = ''
  generatedCards.value = []
}

const generateSelected = async () => {
  if (!sourceText.value.trim()) return

  isLoading.value = true
  loadingMessage.value = 'AI đang đọc và quét dữ liệu...'

  try {
    const response = await api.post('/ai/flashcards/generate', {
      text: sourceText.value,
      existingWords: props.existingWords ?? [],
    })
    if (response.data && response.data.data) {
      const rawCards: GeneratedCard[] = response.data.data.map((c: any) => ({
        ...c,
        imageUrl: '',
        imageLoading: false,
        imageError: false,
      }))
      generatedCards.value = rawCards

      // Load images after generating cards
      if (withImages.value) {
        loadingMessage.value = 'Đang tải ảnh minh họa...'
        await loadImagesForCards(rawCards)
      }
    }
  } catch (error) {
    console.error('Error generating flashcards with AI:', error)
    await dialog.alert('Lỗi AI', 'Có lỗi xảy ra khi gọi AI. Vui lòng thử lại.', 'error')
  } finally {
    isLoading.value = false
  }
}

const removeCard = (index: number) => {
  generatedCards.value.splice(index, 1)
}

const saveToDeck = () => {
  // Pass image URL forward so the parent can map it to frontImageUrl
  const cardsToSave = generatedCards.value.map((card) => ({
    ...card,
    frontImageUrl: (!card.imageError && card.imageUrl) ? card.imageUrl : '',
  }))
  emit('save', cardsToSave)
  emit('update:isOpen', false)
  setTimeout(resetForm, 300)
}
</script>
