import axios from './axios'

export interface FlashcardDeck {
  id: number
  title: string
  description?: string
  totalCards: number
  learnedCards: number
  masteredCards: number
  dueToday: number
  createdAt: string
  updatedAt: string
}

export interface FlashcardCard {
  id: number
  deckId: number
  frontText: string
  backText: string
  exampleSentence?: string
  frontImageUrl?: string
  backImageUrl?: string
  status: 'new' | 'learning' | 'review' | 'mastered'
  orderIndex: number
}

export interface FlashcardDeckDetail extends FlashcardDeck {
  flashcards: FlashcardCard[]
}

export interface DeckProgress {
  newCards: number
  learnedCards: number
  masteredCards: number
  dueToday: number
  totalCards: number
}

/**
 * Get flashcard decks of the currently logged-in user
 */
export const getFlashcardDecks = async (): Promise<FlashcardDeck[]> => {
  const response = await axios.get('/flashcard-decks/me')
  const payload = response.data?.data
  return payload?.content ?? payload ?? []
}

/**
 * Get deck detail with its cards
 */
export const getDeckDetail = async (deckId: number): Promise<FlashcardDeckDetail> => {
  const response = await axios.get(`/flashcard-decks/${deckId}`)
  return response.data?.data
}

/**
 * Get cards to review today
 */
export const getCardsToReviewToday = async (deckId: number): Promise<FlashcardCard[]> => {
  const response = await axios.get(`/flashcard-decks/${deckId}/review-today`)
  const reviewData = response.data?.data
  if (!reviewData?.cards) return []

  // Map backend format (flashcardId, frontText, backText) to frontend FlashcardCard (id, frontText, backText)
  const mappedCards = reviewData.cards.map((c: any) => ({
    id: c.flashcardId || c.id,
    deckId: deckId,
    frontText: c.frontText,
    backText: c.backText,
    status: c.isNew ? 'new' : 'learning',
    frontImageUrl: '',
    backImageUrl: '',
    audioUrl: '',
    orderIndex: 0
  }))

  // Shuffle the mapped cards using Fisher-Yates algorithm
  for (let i = mappedCards.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [mappedCards[i], mappedCards[j]] = [mappedCards[j], mappedCards[i]];
  }

  return mappedCards;
}

/**
 * Submit review for a card
 */
export const submitCardReview = async (
  cardId: number,
  rating: 1 | 2 | 3 | 4,
): Promise<void> => {
  const gradeMap: Record<number, string> = {
    1: 'AGAIN',
    2: 'HARD',
    3: 'GOOD',
    4: 'EASY'
  }
  await axios.post(`/flashcards/${cardId}/review`, { grade: gradeMap[rating] })
}

/**
 * Create a new flashcard deck
 */
export const createFlashcardDeck = async (data: {
  title: string
  description?: string
}): Promise<FlashcardDeck> => {
  const response = await axios.post('/flashcard-decks', data)
  return response.data?.data
}

/**
 * Generate flashcards from a topic using AI
 * @param existingWords - list of words already in the deck to avoid duplicates
 */
export const generateFlashcardsAI = async (
  topic: string,
  count: number,
  existingWords: string[] = [],
): Promise<any[]> => {
  const response = await axios.post('/ai/flashcards/generate', { topic, count, existingWords })
  return response.data?.data ?? []
}

/**
 * Add cards to a deck (bulk create)
 */
export const addCardsToDeck = async (
  deckId: number,
  cards: any[],
): Promise<any> => {
  const response = await axios.post(`/flashcard-decks/${deckId}/cards`, { cards })
  return response.data?.data
}

/**
 * Get flashcard stats (progress)
 */
export const getFlashcardStats = async (deckId: number): Promise<DeckProgress> => {
  const response = await axios.get(`/flashcard-decks/${deckId}/progress`)
  return response.data?.data
}

/**
 * Delete a card
 */
export const deleteFlashcard = async (cardId: number): Promise<void> => {
  await axios.delete(`/flashcards/${cardId}`)
}

/**
 * Delete a flashcard deck
 */
export const deleteFlashcardDeck = async (deckId: number): Promise<void> => {
  await axios.delete(`/flashcard-decks/${deckId}`)
}
