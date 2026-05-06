import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface FlashcardCard {
  id: number
  deckId: number
  frontText: string
  backText: string
  exampleSentence?: string
  frontImageUrl?: string
  backImageUrl?: string
  audioUrl?: string
  nextReviewDate?: string
  status: 'new' | 'learning' | 'review' | 'mastered'
}

export interface FlashcardDeck {
  id: number
  title: string
  description?: string
  imageUrl?: string
  totalCards: number
  learnedCards: number
  masteredCards: number
  dueToday: number
  lastReviewedAt?: string
  createdAt: string
  updatedAt: string
}

export interface ReviewSession {
  deckId: number
  cards: FlashcardCard[]
  currentIndex: number
  startTime: Date
  responses: {
    cardId: number
    rating: 1 | 2 | 3 | 4
  }[]
}

export const useFlashcardStore = defineStore('flashcard', () => {
  const decks = ref<FlashcardDeck[]>([])
  const currentDeck = ref<FlashcardDeck | null>(null)
  const currentCards = ref<FlashcardCard[]>([])
  const reviewSession = ref<ReviewSession | null>(null)
  const isLoading = ref(false)
  const filter = ref<'all' | 'learning' | 'completed'>('all')

  // Computed properties
  const filteredDecks = computed(() => {
    if (filter.value === 'all') return decks.value
    if (filter.value === 'learning')
      return decks.value.filter((d) => (d.learnedCards || 0) < d.totalCards)
    if (filter.value === 'completed')
      return decks.value.filter((d) => (d.learnedCards || 0) >= d.totalCards && d.totalCards > 0)
    return decks.value
  })

  const totalNewCards = computed(() => {
    return currentCards.value.filter((c) => c.status === 'new').length
  })

  const totalLearningCards = computed(() => {
    return currentCards.value.filter((c) => c.status === 'learning').length
  })

  const totalMasteredCards = computed(() => {
    return currentCards.value.filter((c) => c.status === 'mastered').length
  })

  const reviewProgress = computed(() => {
    if (!reviewSession.value) return 0
    return Math.round(
      (reviewSession.value.responses.length / reviewSession.value.cards.length) * 100,
    )
  })

  // Deck operations
  const setDecks = (newDecks: FlashcardDeck[]) => {
    decks.value = newDecks
  }

  const removeDeck = (deckId: number) => {
    decks.value = decks.value.filter(d => d.id !== deckId)
  }

  const setCurrentDeck = (deck: FlashcardDeck) => {
    currentDeck.value = deck
  }

  const setCurrentCards = (cards: FlashcardCard[]) => {
    currentCards.value = cards
  }

  // Review session operations
  const startReviewSession = (deckId: number, cards: FlashcardCard[]) => {
    reviewSession.value = {
      deckId,
      cards,
      currentIndex: 0,
      startTime: new Date(),
      responses: [],
    }
  }

  const answerCard = (cardId: number, rating: 1 | 2 | 3 | 4) => {
    if (!reviewSession.value) return

    // Add response
    reviewSession.value.responses.push({
      cardId,
      rating,
    })

    // Move to next card
    if (reviewSession.value.currentIndex < reviewSession.value.cards.length - 1) {
      reviewSession.value.currentIndex++
    }
  }

  const moveToNextCard = () => {
    if (!reviewSession.value) return
    if (reviewSession.value.currentIndex < reviewSession.value.cards.length - 1) {
      reviewSession.value.currentIndex++
    }
  }

  const getNextCard = () => {
    if (!reviewSession.value) return null
    return reviewSession.value.cards[reviewSession.value.currentIndex]
  }

  const isSessionComplete = () => {
    if (!reviewSession.value) return false
    return reviewSession.value.responses.length === reviewSession.value.cards.length
  }

  const endReviewSession = () => {
    reviewSession.value = null
  }

  // Filter operations
  const setFilter = (newFilter: 'all' | 'learning' | 'completed') => {
    filter.value = newFilter
  }

  return {
    decks,
    currentDeck,
    currentCards,
    reviewSession,
    isLoading,
    filter,
    filteredDecks,
    totalNewCards,
    totalLearningCards,
    totalMasteredCards,
    reviewProgress,
    setDecks,
    removeDeck,
    setCurrentDeck,
    setCurrentCards,
    startReviewSession,
    answerCard,
    moveToNextCard,
    getNextCard,
    isSessionComplete,
    endReviewSession,
    setFilter,
  }
})
