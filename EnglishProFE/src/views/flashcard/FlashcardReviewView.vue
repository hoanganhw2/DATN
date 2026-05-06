<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <!-- Header -->
    <div class="bg-white border-b border-gray-100 shadow-sm">
      <div class="max-w-6xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <button
              @click="confirmExit"
              class="text-gray-500 hover:text-red-600 font-medium text-sm flex items-center gap-2 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
              Kết thúc session
            </button>
          </div>

          <div class="flex items-center gap-4">
            <div
              class="text-xs font-bold text-blue-600 bg-blue-50 px-4 py-1.5 rounded-full border border-blue-100 uppercase tracking-widest"
            >
              Thẻ {{ currentIndex + 1 }} / {{ totalCards }}
            </div>
          </div>
        </div>
      </div>

      <!-- Progress Bar -->
      <div class="h-1.5 bg-gray-100 w-full overflow-hidden">
        <div
          class="h-full bg-blue-500 transition-all duration-500 ease-out shadow-[0_0_10px_rgba(59,130,246,0.5)]"
          :style="{ width: reviewProgress + '%' }"
        />
      </div>
    </div>

    <!-- Main Content -->
    <main
      class="flex-1 flex flex-col items-center justify-center p-6 bg-gradient-to-b from-white to-gray-50"
    >
      <!-- Session Complete State -->
      <div
        v-if="isSessionComplete"
        class="text-center max-w-xl w-full animate-in zoom-in duration-500"
      >
        <div class="bg-white rounded-3xl p-12 shadow-2xl shadow-blue-100 border border-blue-50">
          <div
            class="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-8 shadow-inner"
          >
            <svg class="w-12 h-12 text-green-600" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
          </div>

          <h1 class="text-4xl font-extrabold text-gray-900 mb-3 tracking-tight">Tuyệt vời!</h1>
          <p class="text-gray-500 text-lg mb-10">Bạn đã hoàn thành việc ôn tập bộ thẻ này.</p>

          <div class="grid grid-cols-2 gap-4 mb-10">
            <div class="p-4 rounded-2xl bg-gray-50 border border-gray-100">
              <p class="text-gray-400 text-xs font-bold uppercase mb-1">Đã ôn tập</p>
              <p class="text-2xl font-black text-blue-600">{{ totalCards }}</p>
            </div>
            <div class="p-4 rounded-2xl bg-gray-50 border border-gray-100">
              <p class="text-gray-400 text-xs font-bold uppercase mb-1">Thời gian</p>
              <p class="text-2xl font-black text-gray-700">~2p</p>
            </div>
          </div>

          <div class="flex flex-col gap-3">
            <BaseButton
              variant="primary"
              size="lg"
              class="w-full py-4 text-base font-bold shadow-lg shadow-blue-200"
              @click="() => $router.push(`/flashcard/deck/${deckId}`)"
            >
              Xem kết quả trong Bộ thẻ
            </BaseButton>
            <div class="grid grid-cols-2 gap-3">
              <BaseButton
                variant="outline"
                size="lg"
                class="py-4 text-sm font-bold"
                @click="restartSession"
              >
                Ôn tiếp (Reset)
              </BaseButton>
              <BaseButton
                variant="outline"
                size="lg"
                class="py-4 text-sm font-bold text-gray-400 border-gray-100 hover:text-gray-600"
                @click="() => $router.push('/flashcard')"
              >
                Về Trang chủ
              </BaseButton>
            </div>
          </div>
        </div>
      </div>

      <!-- Active Flashcard -->
      <div v-else class="w-full max-w-2xl animate-in fade-in slide-in-from-bottom-4 duration-300">
        <!-- Flashcard Container -->
        <div class="relative group perspective-1000 mb-12">
          <div
            class="bg-white rounded-[2.5rem] shadow-2xl shadow-blue-100 border-2 border-transparent transition-all duration-500 overflow-hidden"
            :class="{
              'border-blue-400': !isAnswerShown,
              'scale-[1.02] shadow-blue-200': isAnswerShown,
            }"
          >
            <div class="p-10 md:p-16 flex flex-col items-center min-h-[400px]">
              <!-- Word Status/Badge -->
              <div class="flex items-center gap-2 mb-10">
                <span
                  class="px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-[10px] font-black uppercase tracking-widest border border-blue-100 shadow-sm"
                >
                  Smart Review
                </span>
              </div>

              <!-- Main Content Area -->
              <div class="flex-1 flex flex-col items-center justify-center w-full">
                <!-- Question State -->
                <div v-if="!isAnswerShown" class="text-center">
                  <h2
                    class="text-5xl md:text-7xl font-black text-gray-900 tracking-tighter leading-tight drop-shadow-sm"
                    :class="{ 'mb-3': parsedFront.phonetic, 'mb-8': !parsedFront.phonetic }"
                  >
                    {{ parsedFront.word }}
                  </h2>
                  <p
                    v-if="parsedFront.phonetic"
                    class="text-2xl font-medium text-gray-400 mb-8 font-mono tracking-widest"
                  >
                    {{ parsedFront.phonetic }}
                  </p>
                  <div class="animate-bounce mt-4">
                    <button
                      @click="isAnswerShown = true"
                      class="px-8 py-3 bg-gray-900 text-white rounded-full text-sm font-bold hover:bg-black transition-colors shadow-lg active:scale-95"
                    >
                      Lật thẻ để xem nghĩa (Space)
                    </button>
                  </div>
                </div>

                <!-- Answer State -->
                <div v-else class="text-center w-full animate-in zoom-in duration-300">
                  <div class="inline-flex items-center gap-4 mb-2">
                    <h2 class="text-5xl md:text-6xl font-black text-gray-900 tracking-tighter">
                      {{ parsedFront.word }}
                    </h2>
                    <button
                      @click="playAudio"
                      :disabled="isSpeaking"
                      class="w-12 h-12 flex items-center justify-center bg-blue-600 text-white rounded-2xl shadow-lg hover:bg-blue-700 active:scale-95 transition-all"
                    >
                      <svg
                        class="w-6 h-6"
                        :class="{ 'animate-pulse': isSpeaking }"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.31-2.5-4.06v8.12c1.48-.75 2.5-2.29 2.5-4.06zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"
                        />
                      </svg>
                    </button>
                  </div>

                  <p
                    v-if="parsedFront.phonetic"
                    class="text-xl font-medium text-gray-400 mb-6 font-mono tracking-widest"
                  >
                    {{ parsedFront.phonetic }}
                  </p>
                  <div v-else class="h-0.5 w-16 bg-blue-100 mx-auto mb-6 rounded-full"></div>

                  <p class="text-3xl font-extrabold text-blue-600 mb-6 tracking-tight leading-snug">
                    {{ currentCard?.backText }}
                  </p>

                  <div
                    v-if="currentCard?.exampleSentence"
                    class="bg-blue-50/50 rounded-2xl p-6 text-left border border-blue-50 max-w-lg mx-auto mb-6"
                  >
                    <p class="text-[10px] font-bold text-blue-400 uppercase tracking-widest mb-2">
                      Ví dụ trong câu
                    </p>
                    <p class="text-gray-700 italic font-medium leading-relaxed">
                      {{ currentCard.exampleSentence }}
                    </p>
                  </div>

                  <div
                    v-if="currentCard?.frontImageUrl"
                    class="max-w-xs mx-auto rounded-2xl overflow-hidden shadow-md border border-gray-100"
                  >
                    <img :src="currentCard.frontImageUrl" class="w-full h-32 object-cover" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Rating Buttons (Only shown when card is flipped) -->
        <div
          v-if="isAnswerShown"
          class="grid grid-cols-4 gap-3 animate-in fade-in slide-in-from-top-4 duration-300"
        >
          <button
            v-for="rating in [
              { value: 1, icon: '❌', label: 'Quá khó', color: 'red' },
              { value: 2, icon: '😐', label: 'Khó', color: 'orange' },
              { value: 3, icon: '🙂', label: 'Bình thường', color: 'blue' },
              { value: 4, icon: '😄', label: 'Dễ', color: 'green' },
            ]"
            :key="rating.value"
            @click="submitRating(rating.value as any)"
            :disabled="isSubmitting"
            class="flex flex-col items-center gap-2 p-4 rounded-2xl border-2 transition-all active:scale-95 disabled:opacity-50"
            :class="[
              rating.color === 'red'
                ? 'bg-red-50 border-red-100 hover:border-red-400 text-red-700'
                : rating.color === 'orange'
                  ? 'bg-amber-50 border-amber-100 hover:border-amber-400 text-amber-700'
                  : rating.color === 'blue'
                    ? 'bg-blue-50 border-blue-100 hover:border-blue-400 text-blue-700'
                    : 'bg-green-50 border-green-100 hover:border-green-400 text-green-700',
            ]"
          >
            <span class="text-2xl mb-1">{{ rating.icon }}</span>
            <span class="text-[11px] font-black uppercase tracking-tighter">{{
              rating.label
            }}</span>
            <span class="text-[10px] opacity-40 font-bold">Phím {{ rating.value }}</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'
import { useFlashcardStore } from '@/stores/flashcard'
import { getCardsToReviewToday, submitCardReview } from '@/api/flashcard'
import { useDialog } from '@/composables/useDialog'

const router = useRouter()
const route = useRoute()
const flashcardStore = useFlashcardStore()
const dialog = useDialog()

const isSubmitting = ref(false)
const isSpeaking = ref(false)
const isAnswerShown = ref(false)

const deckId = computed(() => Number(route.params.id))

const currentIndex = computed(() => flashcardStore.reviewSession?.currentIndex || 0)
const totalCards = computed(() => flashcardStore.reviewSession?.cards.length || 0)
const currentCard = computed(() => flashcardStore.getNextCard())
const reviewProgress = computed(() => flashcardStore.reviewProgress)
const isSessionComplete = computed(() => flashcardStore.isSessionComplete())

const parsedFront = computed(() => {
  const text = currentCard.value?.frontText || ''
  // Match a word, then optionally some phonetic enclosed in / / or [ ]
  const match = text.match(/^(.*?)\s*(\/.*?\/|\[.*?\])\s*(.*)$/)
  if (match) {
    return {
      word: (match[1] + ' ' + match[3]).trim(),
      phonetic: match[2],
    }
  }
  return { word: text.trim(), phonetic: '' }
})

const playAudio = () => {
  const text = parsedFront.value.word
  if (!text) return

  if (window.speechSynthesis.speaking) window.speechSynthesis.cancel()

  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  utterance.rate = 0.85

  utterance.onstart = () => (isSpeaking.value = true)
  utterance.onend = () => (isSpeaking.value = false)
  utterance.onerror = () => (isSpeaking.value = false)

  window.speechSynthesis.speak(utterance)
}

const submitRating = async (rating: 1 | 2 | 3 | 4) => {
  if (!currentCard.value || isSubmitting.value) return

  isSubmitting.value = true
  try {
    // API call only needs cardId and rating
    await submitCardReview(currentCard.value.id, rating)

    // Store logic
    flashcardStore.answerCard(currentCard.value.id, rating)

    // Reset state for next card
    if (!flashcardStore.isSessionComplete()) {
      isAnswerShown.value = false
      // Move after tiny delay for smoother feel
      setTimeout(() => {
        // In store, answerCard already moves currentIndex in some implementations,
        // but here we ensure we're ready for next card
      }, 50)
    }
  } catch (error) {
    console.error('Error submitting rating:', error)
  } finally {
    isSubmitting.value = false
  }
}

const restartSession = async () => {
  try {
    const cards = await getCardsToReviewToday(deckId.value)
    flashcardStore.startReviewSession(deckId.value, cards)
    isAnswerShown.value = false
  } catch (error) {
    console.error('Error restarting session:', error)
  }
}

const confirmExit = async () => {
  const confirmed = await dialog.confirm(
    'Kết thúc phiên học',
    'Bạn muốn dừng việc học tại đây? Các thẻ đã trả lời vẫn sẽ được lưu lại.',
    'warning',
  )
  if (confirmed) {
    router.push('/flashcard')
  }
}

const handleKeydown = (e: KeyboardEvent) => {
  if (isSessionComplete.value) return

  if (e.code === 'Space') {
    e.preventDefault()
    if (!isAnswerShown.value) {
      isAnswerShown.value = true
    } else {
      playAudio()
    }
  }

  const num = parseInt(e.key)
  if (num >= 1 && num <= 4 && isAnswerShown.value) {
    submitRating(num as 1 | 2 | 3 | 4)
  }
}

onMounted(async () => {
  document.addEventListener('keydown', handleKeydown)

  if (!flashcardStore.reviewSession) {
    try {
      const cards = await getCardsToReviewToday(deckId.value)
      if (cards.length === 0) {
        await dialog.alert(
          'Không có thẻ cần ôn',
          'Hôm nay bạn không có thẻ nào cần ôn tập trong bộ này!',
        )
        router.push('/flashcard')
        return
      }
      flashcardStore.startReviewSession(deckId.value, cards)
    } catch (error) {
      console.error('Error loading review cards:', error)
      router.push('/flashcard')
    }
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
  if (window.speechSynthesis.speaking) window.speechSynthesis.cancel()
})
</script>

<style scoped>
.perspective-1000 {
  perspective: 1000px;
}
</style>
