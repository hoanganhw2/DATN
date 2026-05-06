/**
 * Exam Store - Pinia
 * Purpose: Manage exam session state (answers, timer, submission)
 * Connected to real backend API via UserExamController
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  startExamApi,
  submitExamApi,
  type StartExamData,
  type SectionResponse,
  type QuestionResponse,
  type SubmitExamData,
} from '../api/exam'

// ─── Local types (internal to the store) ────────────────────────────────────────

export interface StoreQuestion {
  id: string           // questionId as string for consistency
  numericId: number    // original numeric ID for backend calls
  sectionId: string
  questionNumber: number
  questionText: string
  mediaUrl: string | null
  options: { id: string; text: string; order: number }[]
  questionType: string
  points: number
}

export interface StoreSection {
  id: string
  sectionName: string
  sectionOrder: number
  duration: number | null
  questions: StoreQuestion[]
}

export interface StoreExam {
  id: string
  numericId: number
  title: string
  sections: StoreSection[]
  totalScore: number
  duration: number  // seconds
}

// ─── Store ──────────────────────────────────────────────────────────────────────

export const useExamStore = defineStore('exam', () => {
  // State
  const currentExam = ref<StoreExam | null>(null)
  const allQuestions = ref<StoreQuestion[]>([])
  const userAnswers = ref<Record<string, string>>({}) // questionId -> option text
  const markedForReview = ref<Set<string>>(new Set())
  const currentQuestionIndex = ref(0)
  const timeRemaining = ref(0) // in seconds
  const isExamStarted = ref(false)
  const isExamSubmitted = ref(false)
  const examStartTime = ref<number | null>(null)
  const examResultId = ref<number | null>(null) // backend exam_result PK
  const deadline = ref<string | null>(null)
  const lastSubmitResult = ref<SubmitExamData | null>(null) // cached submit response

  // Computed
  const currentQuestion = computed(() => allQuestions.value[currentQuestionIndex.value] || null)
  const totalQuestions = computed(() => allQuestions.value.length)
  const answeredCount = computed(() => Object.keys(userAnswers.value).length)
  const markedCount = computed(() => markedForReview.value.size)

  const progressPercentage = computed(() => {
    if (totalQuestions.value === 0) return 0
    return Math.round((answeredCount.value / totalQuestions.value) * 100)
  })

  const timeRemainingFormatted = computed(() => {
    const hours = Math.floor(timeRemaining.value / 3600)
    const minutes = Math.floor((timeRemaining.value % 3600) / 60)
    const seconds = timeRemaining.value % 60
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  })

  const isTimeWarning = computed(() => timeRemaining.value < 300) // Less than 5 minutes

  const questionStatus = computed(() => (questionId: string) => {
    if (userAnswers.value[questionId]) return 'answered'
    if (markedForReview.value.has(questionId)) return 'marked'
    return 'unanswered'
  })

  // ── Map backend data to store format ──────────────────────────────────────────

  const mapBackendToStore = (data: StartExamData, examId: string | number) => {
    const sections: StoreSection[] = []
    const questions: StoreQuestion[] = []
    let questionNumber = 1

    for (const section of data.sections) {
      const storeSection: StoreSection = {
        id: String(section.sectionId),
        sectionName: section.sectionName,
        sectionOrder: section.sectionOrder,
        duration: section.duration,
        questions: [],
      }

      for (const q of section.questions) {
        const storeQ: StoreQuestion = {
          id: String(q.questionId),
          numericId: q.questionId,
          sectionId: String(section.sectionId),
          questionNumber: questionNumber++,
          questionText: q.questionText,
          mediaUrl: q.mediaUrl,
          options: q.options.map((optText, idx) => ({
            id: optText,        // Use the option text as ID for answer matching
            text: optText,
            order: idx + 1,
          })),
          questionType: q.questionType,
          points: q.points ?? 1,
        }
        storeSection.questions.push(storeQ)
        questions.push(storeQ)
      }

      sections.push(storeSection)
    }

    return { sections, questions }
  }

  // ── Actions ───────────────────────────────────────────────────────────────────

  const initializeExam = async (examId: string | number) => {
    try {
      console.log('[ExamStore] Starting exam via API:', examId)
      const data = await startExamApi(examId)

      console.log('[ExamStore] Backend response:', {
        examResultId: data.examResultId,
        sections: data.sections?.length,
        deadline: data.deadline,
      })

      const { sections, questions } = mapBackendToStore(data, examId)

      if (questions.length === 0) {
        throw new Error('Đề thi chưa có câu hỏi nào')
      }

      // Calculate time remaining from deadline
      let duration = 0
      if (data.deadline) {
        const deadlineMs = new Date(data.deadline).getTime()
        const nowMs = Date.now()
        duration = Math.max(0, Math.floor((deadlineMs - nowMs) / 1000))
      } else {
        // Fallback: sum section durations or use a default
        duration = sections.reduce((sum, s) => sum + (s.duration || 0), 0)
        if (duration === 0) duration = 7200 // 2-hour default
      }

      // Set state
      examResultId.value = data.examResultId
      deadline.value = data.deadline
      currentExam.value = {
        id: String(examId),
        numericId: typeof examId === 'number' ? examId : parseInt(examId, 10),
        title: '', // Will be set from ExamListView context
        sections,
        totalScore: 0,
        duration,
      }
      allQuestions.value = questions
      currentQuestionIndex.value = 0
      timeRemaining.value = duration
      isExamStarted.value = false
      isExamSubmitted.value = false
      examStartTime.value = null

      // Restore previously submitted answers (resume scenario)
      const restoredAnswers: Record<string, string> = {}
      for (const section of data.sections) {
        for (const q of section.questions) {
          if (q.submittedAnswer) {
            restoredAnswers[String(q.questionId)] = q.submittedAnswer
          }
        }
      }
      userAnswers.value = restoredAnswers
      markedForReview.value = new Set()

      console.log('[ExamStore] Exam initialized:', {
        questions: questions.length,
        duration,
        resumed: Object.keys(restoredAnswers).length,
      })
    } catch (error) {
      console.error('[ExamStore] Failed to initialize exam:', error)
      throw error
    }
  }

  const setExamTitle = (title: string) => {
    if (currentExam.value) {
      currentExam.value.title = title
    }
  }

  const setExamMeta = (meta: { totalScore?: number; title?: string }) => {
    if (currentExam.value) {
      if (meta.totalScore != null) currentExam.value.totalScore = meta.totalScore
      if (meta.title != null) currentExam.value.title = meta.title
    }
  }

  const startExam = () => {
    isExamStarted.value = true
    examStartTime.value = Date.now()
  }

  const updateAnswer = (questionId: string, answer: string) => {
    userAnswers.value[questionId] = answer
  }

  const toggleMarkForReview = (questionId: string) => {
    if (markedForReview.value.has(questionId)) {
      markedForReview.value.delete(questionId)
    } else {
      markedForReview.value.add(questionId)
    }
  }

  const goToQuestion = (index: number) => {
    if (index >= 0 && index < totalQuestions.value) {
      currentQuestionIndex.value = index
    }
  }

  const goToNextQuestion = () => {
    if (currentQuestionIndex.value < totalQuestions.value - 1) {
      currentQuestionIndex.value++
    }
  }

  const goToPreviousQuestion = () => {
    if (currentQuestionIndex.value > 0) {
      currentQuestionIndex.value--
    }
  }

  const decrementTimer = () => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    }
  }

  /** Submit to backend and get graded results */
  const submitExamToBackend = async (): Promise<SubmitExamData> => {
    if (!examResultId.value) {
      throw new Error('No active exam result to submit')
    }

    // Build answer items from the user answers
    const answers = allQuestions.value.map((q) => ({
      questionId: q.numericId,
      answerText: userAnswers.value[q.id] || '',
    }))

    const result = await submitExamApi(examResultId.value, answers)
    console.log('[ExamStore] Raw submit result:', JSON.stringify(result, null, 2))
    console.log('[ExamStore] sectionResults:', result.sectionResults)
    isExamSubmitted.value = true
    lastSubmitResult.value = result
    return result
  }

  const submitExam = () => {
    isExamSubmitted.value = true
  }

  const getExamResults = () => {
    if (!currentExam.value) return null

    let correctCount = 0
    const detailedResults: Record<
      string,
      { question: string; userAnswer: string; correct: boolean }
    > = {}

    allQuestions.value.forEach((question) => {
      const userAnswer = userAnswers.value[question.id]
      // Without correctAnswer from backend, we can't determine locally
      // So this is a client-side fallback — real grading is done by submitExamToBackend
      detailedResults[question.id] = {
        question: question.questionText,
        userAnswer: userAnswer || 'No answer',
        correct: false, // Will be overridden by backend result
      }
    })

    const percentage = totalQuestions.value > 0 ? (correctCount / totalQuestions.value) * 100 : 0
    const score = (percentage / 100) * (currentExam.value.totalScore || 0)

    return {
      examId: currentExam.value.id,
      examTitle: currentExam.value.title,
      score: Math.round(score),
      totalScore: currentExam.value.totalScore,
      percentage: Math.round(percentage),
      correctCount,
      totalCount: totalQuestions.value,
      timeSpent: examStartTime.value ? Date.now() - examStartTime.value : 0,
      submittedAt: new Date().toISOString(),
      detailedResults,
    }
  }

  const resetExam = () => {
    currentExam.value = null
    allQuestions.value = []
    userAnswers.value = {}
    markedForReview.value = new Set()
    currentQuestionIndex.value = 0
    timeRemaining.value = 0
    isExamStarted.value = false
    isExamSubmitted.value = false
    examStartTime.value = null
    examResultId.value = null
    deadline.value = null
    lastSubmitResult.value = null
  }

  return {
    // State
    currentExam,
    allQuestions,
    userAnswers,
    markedForReview,
    currentQuestionIndex,
    timeRemaining,
    isExamStarted,
    isExamSubmitted,
    examStartTime,
    examResultId,
    deadline,
    lastSubmitResult,

    // Computed
    currentQuestion,
    totalQuestions,
    answeredCount,
    markedCount,
    progressPercentage,
    timeRemainingFormatted,
    isTimeWarning,
    questionStatus,

    // Actions
    initializeExam,
    setExamTitle,
    setExamMeta,
    startExam,
    updateAnswer,
    toggleMarkForReview,
    goToQuestion,
    goToNextQuestion,
    goToPreviousQuestion,
    decrementTimer,
    getExamResults,
    submitExam,
    submitExamToBackend,
    resetExam,
  }
})
