import api from './axios'

export interface CreateExerciseQuestionPayload {
  questionText: string
  mediaUrl?: string
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'TEXT_INPUT' | 'ESSAY'
  options?: string[]
  correctAnswer: string
  explanation?: string
  points: number
  orderIndex?: number
}

export interface CreateExercisePayload {
  title: string
  description?: string
  type: 'MULTIPLE_CHOICE' | 'FILL_BLANK' | 'MATCHING' | 'ESSAY'
  totalPoints: number
  timeLimit?: number
  questions: CreateExerciseQuestionPayload[]
}

export interface ExerciseSummaryResponse {
  id: number
  title: string
  description?: string
  type: string
  totalPoints: number
  timeLimit?: number
  questionCount: number
}

export interface ExerciseDetailResponse {
  id: number
  title: string
  description?: string
  type: string
  totalPoints: number
  timeLimit?: number
  questions: {
    id: number
    questionText: string
    mediaUrl?: string
    questionType: string
    options?: string[]
    points: number
    orderIndex: number
    correctAnswer?: string
    explanation?: string
  }[]
}

export interface ExerciseSubmitResponse {
  exerciseId: number
  totalQuestions: number
  correctCount: number
  totalScore: number
  results: {
    questionId: number
    questionText: string
    submittedAnswer: string
    correctAnswer: string
    isCorrect: boolean
    score: number
    explanation?: string
  }[]
}

interface ApiResponse<T> {
  message?: string
  data: T
}

export const getExercisesByLesson = async (lessonId: number) => {
  const response = await api.get<ApiResponse<ExerciseSummaryResponse[]>>(`/lessons/${lessonId}/exercises`)
  return response.data.data
}

export const getExerciseDetail = async (exerciseId: number) => {
  const response = await api.get<ApiResponse<ExerciseDetailResponse>>(`/exercises/${exerciseId}`)
  return response.data.data
}

export const getTeacherExerciseDetail = async (exerciseId: number) => {
  const response = await api.get<ApiResponse<ExerciseDetailResponse>>(`/teacher/exercises/${exerciseId}`)
  return response.data.data
}

export const submitExercise = async (exerciseId: number, payload: { answers: { questionId: number, answerText: string }[] }) => {
  const response = await api.post<ApiResponse<ExerciseSubmitResponse>>(`/exercises/${exerciseId}/submit`, payload)
  return response.data.data
}

export const createExercise = async (lessonId: number, payload: CreateExercisePayload) => {
  const response = await api.post<ApiResponse<ExerciseSummaryResponse>>(`/lessons/${lessonId}/exercises`, payload)
  return response.data.data
}

export const updateExercise = async (exerciseId: number, payload: CreateExercisePayload) => {
  const response = await api.put<ApiResponse<ExerciseSummaryResponse>>(`/exercises/${exerciseId}`, payload)
  return response.data.data
}

export const deleteExercise = async (exerciseId: number) => {
  await api.delete(`/exercises/${exerciseId}`)
}
