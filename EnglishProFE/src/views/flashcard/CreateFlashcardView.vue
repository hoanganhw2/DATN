<template>
  <DashboardLayout>
    <div class="w-full">
      <!-- Action Header: Close button -->
      <div class="mb-6 flex justify-end">
        <router-link
          to="/flashcard"
          class="inline-flex items-center gap-2 px-4 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors border border-gray-200 shadow-sm"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 19l-7-7m0 0l7-7m-7 7h18"
            />
          </svg>
          Quay lại
        </router-link>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Main Form -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Deck Info (Only show if creating new) -->
          <div v-if="!existingDeckId" class="bg-white rounded-xl border border-gray-200 p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
              <span
                class="w-7 h-7 bg-blue-100 text-blue-700 rounded-lg flex items-center justify-center text-sm font-bold"
                >1</span
              >
              Thông tin bộ thẻ
            </h2>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">
                  Tên bộ thẻ <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="deckForm.title"
                  type="text"
                  placeholder="vd: Từ vựng IELTS Band 7+"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-shadow"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Mô tả</label>
                <textarea
                  v-model="deckForm.description"
                  placeholder="Mô tả về bộ thẻ học tập của bạn..."
                  rows="2"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                />
              </div>
            </div>
          </div>

          <!-- Cards Section: Tabs -->
          <div class="bg-white rounded-xl border border-gray-200 p-6">
            <h2 class="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
              <span
                class="w-7 h-7 bg-blue-100 text-blue-700 rounded-lg flex items-center justify-center text-sm font-bold"
              >
                {{ existingDeckId ? '1' : '2' }}
              </span>
              {{ existingDeckId ? `Thêm thẻ vào bộ: ${existingDeckTitle}` : 'Thêm thẻ học' }}
            </h2>

            <!-- Tab Buttons -->
            <div class="flex gap-2 mb-6 border-b border-gray-200 pb-4">
              <button
                @click="activeTab = 'manual'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeTab === 'manual'
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-gray-600 hover:bg-gray-100',
                ]"
              >
                ✏️ Nhập thủ công
              </button>
              <button
                @click="activeTab = 'ai'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeTab === 'ai'
                    ? 'bg-gradient-to-r from-blue-600 to-blue-600 text-white shadow-sm'
                    : 'text-gray-600 hover:bg-gray-100',
                ]"
              >
                🤖 Tạo bằng AI
              </button>
            </div>

            <!-- === MANUAL TAB === -->
            <div v-if="activeTab === 'manual'">
              <!-- Table Header -->
              <div class="grid grid-cols-12 gap-2 mb-2 px-1">
                <div class="col-span-1 text-xs font-medium text-gray-400 text-center">#</div>
                <div class="col-span-4 text-xs font-medium text-gray-500">
                  Mặt trước <span class="text-red-400">*</span>
                </div>
                <div class="col-span-4 text-xs font-medium text-gray-500">
                  Mặt sau <span class="text-red-400">*</span>
                </div>
                <div class="col-span-3 text-xs font-medium text-gray-500">Câu ví dụ</div>
              </div>

              <!-- Cards Rows -->
              <div class="space-y-2">
                <div
                  v-for="(card, index) in cards"
                  :key="index"
                  class="grid grid-cols-12 gap-2 items-start group relative"
                >
                  <div class="col-span-1 flex items-center justify-center pt-2.5">
                    <span class="text-xs text-gray-400 font-medium">{{ index + 1 }}</span>
                  </div>
                  <div class="col-span-4">
                    <input
                      v-model="card.frontText"
                      type="text"
                      placeholder="Từ / Mặt trước..."
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div class="col-span-4">
                    <input
                      v-model="card.backText"
                      type="text"
                      placeholder="Nghĩa / Mặt sau..."
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div class="col-span-2">
                    <input
                      v-model="card.exampleSentence"
                      type="text"
                      placeholder="Ví dụ..."
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                  <div class="col-span-1 flex items-center justify-center pt-1.5">
                    <button
                      @click="removeRow(index)"
                      :disabled="cards.length <= 1"
                      class="p-1.5 text-gray-300 hover:text-red-500 disabled:opacity-30 disabled:cursor-not-allowed rounded transition-colors"
                      title="Xóa hàng"
                    >
                      <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path
                          fill-rule="evenodd"
                          d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                          clip-rule="evenodd"
                        />
                      </svg>
                    </button>
                  </div>

                  <!-- Expand row: Image fields -->
                  <div
                    v-if="card.expanded"
                    class="col-span-12 grid grid-cols-2 gap-2 mt-1 ml-8 pl-1 border-l-2 border-blue-100"
                  >
                    <div>
                      <label class="block text-xs text-gray-500 mb-1">Ảnh mặt trước (URL)</label>
                      <input
                        v-model="card.frontImageUrl"
                        type="text"
                        placeholder="https://..."
                        class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
                      />
                    </div>
                    <div>
                      <label class="block text-xs text-gray-500 mb-1">Ảnh mặt sau (URL)</label>
                      <input
                        v-model="card.backImageUrl"
                        type="text"
                        placeholder="https://..."
                        class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
                      />
                    </div>
                  </div>

                  <!-- Toggle expand -->
                  <button
                    @click="card.expanded = !card.expanded"
                    class="col-span-12 flex items-center gap-1 text-xs text-blue-500 hover:text-blue-700 ml-8 mt-0.5 w-fit transition-colors"
                  >
                    <svg
                      class="w-3 h-3 transition-transform"
                      :class="card.expanded ? 'rotate-90' : ''"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                        clip-rule="evenodd"
                      />
                    </svg>
                    {{ card.expanded ? 'Ẩn thêm' : 'Thêm ảnh minh họa' }}
                  </button>
                </div>
              </div>

              <!-- Add row buttons -->
              <div class="flex gap-2 mt-4 pt-4 border-t border-gray-100">
                <button
                  @click="addRow(1)"
                  class="px-3 py-1.5 text-sm text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
                >
                  + 1 hàng
                </button>
                <button
                  @click="addRow(5)"
                  class="px-3 py-1.5 text-sm text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
                >
                  + 5 hàng
                </button>
                <button
                  @click="addRow(10)"
                  class="px-3 py-1.5 text-sm text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
                >
                  + 10 hàng
                </button>
              </div>
            </div>

            <!-- === AI TAB === -->
            <div v-if="activeTab === 'ai'">
              <p class="text-sm text-gray-500 mb-4">
                Nhập chủ đề bạn muốn học và số lượng thẻ, AI sẽ tự động biên soạn bộ từ vựng chất
                lượng cho bạn.
              </p>

              <div class="space-y-4">
                <div>
                  <label
                    class="block text-xs font-semibold text-gray-500 mb-1.5 uppercase tracking-wider"
                    >Chủ đề (Topic)</label
                  >
                  <input
                    v-model="aiTopic"
                    type="text"
                    placeholder="vd: Daily Routine, Business English, IELTS Writing Task 2..."
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  />
                </div>

                <div>
                  <label
                    class="block text-xs font-semibold text-gray-500 mb-1.5 uppercase tracking-wider"
                    >Số lượng thẻ</label
                  >
                  <select
                    v-model="aiCount"
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all bg-white"
                  >
                    <option v-for="n in [5, 10, 15, 20].filter(n => n <= remainingSlots)" :key="n" :value="n">{{ n }} thẻ</option>
                    <option v-if="remainingSlots <= 0" :value="0" disabled>Bộ thẻ đã đầy</option>
                  </select>
                </div>
              </div>

              <button
                @click="generateWithAI"
                :disabled="!aiTopic.trim() || isGeneratingAI"
                class="mt-6 w-full px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-600 text-white font-semibold rounded-lg hover:from-blue-700 hover:to-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 transition-all shadow-md active:scale-[0.98]"
              >
                <svg
                  v-if="isGeneratingAI"
                  class="animate-spin w-5 h-5"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                {{ isGeneratingAI ? 'AI đang biên soạn...' : '🤖 Bắt đầu tạo bằng AI' }}
              </button>

              <!-- AI Results Preview -->
              <div v-if="aiResults.length > 0" class="mt-5">
                <div class="flex items-center justify-between mb-3">
                  <h4 class="text-sm font-bold text-gray-900">
                    AI đã tạo {{ aiResults.length }} thẻ:
                  </h4>
                  <button
                    @click="addAIResultsToCards"
                    class="px-4 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 font-medium"
                  >
                    ✅ Thêm tất cả vào bộ thẻ
                  </button>
                </div>
                <div class="space-y-2 max-h-72 overflow-y-auto pr-1">
                  <div
                    v-for="(item, i) in aiResults"
                    :key="i"
                    class="flex gap-3 p-3 bg-blue-50 border border-blue-100 rounded-lg"
                  >
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-2 flex-wrap mb-1">
                        <span class="font-bold text-sm text-gray-900">{{ item.word }}</span>
                        <span class="text-xs text-blue-600 bg-blue-100 px-2 py-0.5 rounded-full">{{
                          item.type
                        }}</span>
                        <span class="text-xs text-gray-400">{{ item.phonetic }}</span>
                      </div>
                      <p class="text-xs text-gray-700 font-medium">{{ item.definition_vi }}</p>
                      <p class="text-xs text-gray-400 italic mt-0.5">{{ item.example }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- === SIDEBAR === -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-xl border border-gray-200 p-6 sticky top-4 space-y-6">
            <!-- Summary -->
            <div>
              <h3 class="text-base font-bold text-gray-900 mb-3">Tóm tắt</h3>
              <div class="space-y-2 text-sm">
                <div class="flex justify-between items-center py-1.5 border-b border-gray-100">
                  <span class="text-gray-500">Tổng số thẻ:</span>
                  <span class="font-bold text-base" :class="isAtLimit ? 'text-red-500' : 'text-blue-600'">{{ cards.length }}</span>
                </div>
                <div class="flex justify-between items-center py-1.5 border-b border-gray-100">
                  <span class="text-gray-500">Thẻ hợp lệ:</span>
                  <span class="font-semibold text-green-600">{{ validCards.length }}</span>
                </div>
                <div class="flex justify-between items-center py-1.5">
                  <span class="text-gray-500">Còn có thể thêm:</span>
                  <span class="font-semibold" :class="remainingSlots === 0 ? 'text-red-500' : 'text-gray-700'">{{ remainingSlots }} / 50</span>
                </div>
              </div>
            </div>

            <!-- Preview -->
            <div v-if="validCards.length > 0">
              <h3 class="text-sm font-bold text-gray-900 mb-2">Xem trước thẻ</h3>
              <div class="space-y-2">
                <div
                  v-for="(card, i) in validCards.slice(0, 3)"
                  :key="i"
                  class="p-2.5 bg-gray-50 rounded-lg border border-gray-100"
                >
                  <p class="text-xs font-semibold text-gray-800 truncate">{{ card.frontText }}</p>
                  <p class="text-xs text-gray-500 truncate">{{ card.backText }}</p>
                  <p
                    v-if="card.exampleSentence"
                    class="text-xs text-blue-500 italic truncate mt-0.5"
                  >
                    {{ card.exampleSentence }}
                  </p>
                </div>
                <p v-if="validCards.length > 3" class="text-xs text-center text-gray-400">
                  +{{ validCards.length - 3 }} thẻ khác
                </p>
              </div>
            </div>

            <!-- Deck full warning -->
            <div v-if="isAtLimit" class="p-3 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-xs text-red-700 font-semibold">🚫 Bộ thẻ đã đạt giới hạn 50 thẻ!</p>
              <p class="text-xs text-red-500 mt-1">Để học hiệu quả, mỗi bộ thẻ chỉ nên chứa tối đa 50 thẻ. Hãy tạo một bộ thẻ mới.</p>
            </div>

            <!-- Near limit warning -->
            <div v-else-if="remainingSlots <= 5 && remainingSlots > 0" class="p-3 bg-amber-50 border border-amber-200 rounded-lg">
              <p class="text-xs text-amber-700">⚠️ Bộ thẻ sắp đầy! Còn chỗ cho <strong>{{ remainingSlots }}</strong> thẻ nữa.</p>
            </div>

            <!-- Validation warning -->
            <div v-else-if="!isFormValid" class="p-3 bg-amber-50 border border-amber-200 rounded-lg">
              <p class="text-xs text-amber-700">
                ⚠️
                {{
                  existingDeckId
                    ? 'Cần ít nhất 1 thẻ đã điền đầy đủ mặt trước + mặt sau'
                    : 'Cần có tên bộ thẻ và ít nhất 1 thẻ đã điền đầy đủ mặt trước + mặt sau'
                }}
              </p>
            </div>

            <!-- Submit Button -->
            <button
              :disabled="!isFormValid || isSubmitting || isAtLimit"
              @click="submitForm"
              class="w-full px-4 py-3 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm"
            >
              <span v-if="isSubmitting" class="flex items-center justify-center gap-2">
                <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                Đang xử lý...
              </span>
              <span v-else
                >{{ existingDeckId ? 'Thêm thẻ' : 'Tạo bộ thẻ' }} ({{
                  validCards.length
                }}
                thẻ)</span
              >
            </button>
          </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { createFlashcardDeck, addCardsToDeck, generateFlashcardsAI, getDeckDetail } from '@/api/flashcard'
import { useToastStore } from '@/stores/toast'

const router = useRouter()
const route = useRoute()
const toastStore = useToastStore()

const existingDeckId = ref<number | null>(null)
const existingDeckTitle = ref<string>('')
/** Số thẻ đã có trong bộ thẻ (fetch từ server khi có existingDeckId) */
const existingCardCount = ref(0)

onMounted(async () => {
  if (route.query.deckId) {
    existingDeckId.value = Number(route.query.deckId)
    existingDeckTitle.value = (route.query.deckTitle as string) || 'Đang tải...'
    try {
      const detail = await getDeckDetail(existingDeckId.value)
      existingCardCount.value = detail?.totalCards ?? detail?.flashcards?.length ?? 0
    } catch {
      // fallback: giả sử 0 nếu không fetch được
      existingCardCount.value = 0
    }
  }
})

// ---- Types ----
interface CardRow {
  frontText: string
  backText: string
  exampleSentence: string
  frontImageUrl: string
  backImageUrl: string
  expanded: boolean
}

// ---- State ----
const deckForm = reactive({
  title: '',
  description: '',
})

const cards = ref<CardRow[]>([createEmptyCard(), createEmptyCard(), createEmptyCard()])

const activeTab = ref<'manual' | 'ai'>('manual')
const isSubmitting = ref(false)
const isGeneratingAI = ref(false)
const aiTopic = ref('')
const aiCount = ref(10)
const aiResults = ref<
  {
    word: string
    type: string
    phonetic: string
    definition_en: string
    definition_vi: string
    example: string
  }[]
>([])

// ---- Helpers ----
function createEmptyCard(): CardRow {
  return {
    frontText: '',
    backText: '',
    exampleSentence: '',
    frontImageUrl: '',
    backImageUrl: '',
    expanded: false,
  }
}

// ---- Computed ----
const MAX_CARDS_PER_DECK = 50

const validCards = computed(() =>
  cards.value.filter((c) => c.frontText.trim() && c.backText.trim()),
)

// Build a flat list of existing front-text words to send to the AI (dedup filter)
const existingWordsList = computed(() =>
  cards.value
    .map((c) => c.frontText.trim())
    .filter(Boolean)
)

const remainingSlots = computed(() =>
  Math.max(0, MAX_CARDS_PER_DECK - existingCardCount.value - validCards.value.length)
)
const isAtLimit = computed(() =>
  existingCardCount.value + validCards.value.length >= MAX_CARDS_PER_DECK
)

const isFormValid = computed(() => {
  if (existingDeckId.value) return validCards.value.length >= 1
  return deckForm.title.trim().length > 0 && validCards.value.length >= 1
})

// ---- Methods ----
const addRow = (count: number = 1) => {
  for (let i = 0; i < count; i++) cards.value.push(createEmptyCard())
}

const removeRow = (index: number) => {
  if (cards.value.length > 1) cards.value.splice(index, 1)
}

const generateWithAI = async () => {
  if (!aiTopic.value.trim()) return
  isGeneratingAI.value = true
  aiResults.value = []
  try {
    const results = await generateFlashcardsAI(aiTopic.value, aiCount.value, existingWordsList.value)
    aiResults.value = results
    toastStore.success(`AI đã tạo ${results.length} thẻ cho chủ đề "${aiTopic.value}"!`)
  } catch {
    toastStore.error('Có lỗi khi gọi AI. Vui lòng thử lại.')
  } finally {
    isGeneratingAI.value = false
  }
}

const addAIResultsToCards = () => {
  // Remove empty rows first
  cards.value = cards.value.filter((c) => c.frontText || c.backText)

  for (const item of aiResults.value) {
    cards.value.push({
      frontText: `${item.word}${item.phonetic ? ' ' + item.phonetic : ''}`,
      backText: item.definition_vi || item.definition_en,
      exampleSentence: item.example,
      frontImageUrl: (item as any).frontImageUrl || '',
      backImageUrl: '',
      expanded: false,
    })
  }

  toastStore.success(`Đã thêm ${aiResults.value.length} thẻ!`)
  aiResults.value = []
  activeTab.value = 'manual'
}

const submitForm = async () => {
  if (!isFormValid.value) return
  isSubmitting.value = true
  try {
    const cardsPayload = validCards.value.map((c, i) => ({
      frontText: c.frontText.trim(),
      backText: c.backText.trim(),
      exampleSentence: c.exampleSentence.trim() || undefined,
      frontImageUrl: c.frontImageUrl.trim() || undefined,
      backImageUrl: c.backImageUrl.trim() || undefined,
      orderIndex: i,
    }))

    if (existingDeckId.value) {
      await addCardsToDeck(existingDeckId.value, cardsPayload)
      toastStore.success('Thêm thẻ mới thành công!')
      router.push(`/flashcard/deck/${existingDeckId.value}`)
    } else {
      const deck = await createFlashcardDeck({
        title: deckForm.title,
        description: deckForm.description,
      })
      await addCardsToDeck(deck.id, cardsPayload)
      toastStore.success('Tạo bộ thẻ thành công!')
      router.push('/flashcard')
    }
  } catch {
    toastStore.error('Có lỗi xảy ra. Vui lòng thử lại.')
  } finally {
    isSubmitting.value = false
  }
}
</script>
