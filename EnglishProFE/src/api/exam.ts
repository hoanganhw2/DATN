/**
 * Exam API Service
 * Author: Senior Backend Engineer
 * Purpose: Handle exam-related API calls
 */

import api from './axios'

// ─── Types ──────────────────────────────────────────────────────────────────────

export interface ExamSummary {
  id: number
  title: string
  description: string
  examType: string
  level: string
  duration: number        // seconds
  totalScore: number
  passingScore: number | null
  maxAttempts: number | null
  isPublished: boolean
}

export interface QuestionOption {
  text: string
  index: number   // 0-based, derived from array position
}

export interface QuestionResponse {
  questionId: number
  questionText: string
  mediaUrl: string | null
  questionType: string
  options: string[]       // Backend returns List<String>
  points: number | null
  orderIndex: number | null
  submittedAnswer: string | null  // populated when resuming
}

export interface SectionResponse {
  sectionId: number
  sectionName: string
  sectionOrder: number
  duration: number | null
  questions: QuestionResponse[]
}

export interface StartExamData {
  examResultId: number
  startTime: string
  deadline: string | null
  sections: SectionResponse[]
}

export interface SubmitAnswerItem {
  questionId: number
  answerText: string
}

export interface SubmitExamData {
  examResultId: number
  totalScore: number
  isPassed: boolean | null
  submittedAt: string
  sectionResults: {
    sectionName: string
    correct: number
    total: number
    score: number
  }[]
}

export interface ExamDetailSection {
  id: number
  sectionName: string
  sectionOrder: number
  duration: number | null
  totalQuestions: number
  questions: ExamDetailQuestion[]
}

export interface ExamDetailQuestion {
  id: number
  questionText: string
  mediaUrl: string | null
  questionType: string
  options: string[]
  correctAnswer: string
  explanation: string | null
  points: number | null
  orderIndex: number | null
}

export interface ExamDetail {
  id: number
  title: string
  description: string
  examType: string
  level: string
  duration: number
  totalScore: number
  passingScore: number | null
  maxAttempts: number | null
  isPublished: boolean
  sections: ExamDetailSection[]
}

// ─── Student-Facing APIs ────────────────────────────────────────────────────────

/** Get paginated published exams for students */
export const getPublishedExams = async (params = {}) => {
  const response = await api.get('/exams/published', { params })
  return response.data
}

/** Start or resume an exam attempt — POST /exams/{examId}/start */
export const startExamApi = async (examId: number | string): Promise<StartExamData> => {
  const response = await api.post(`/exams/${examId}/start`)
  // ApiResponse { code, message, data }
  return response.data?.data ?? response.data
}

/** Submit exam answers — POST /exam-results/{examResultId}/submit */
export const submitExamApi = async (
  examResultId: number,
  answers: SubmitAnswerItem[],
): Promise<SubmitExamData> => {
  const response = await api.post(`/exam-results/${examResultId}/submit`, { answers })
  return response.data?.data ?? response.data
}

/** Get exam result detail — GET /exam-results/{examResultId} */
export const getExamResultDetail = async (examResultId: number | string) => {
  const response = await api.get(`/exam-results/${examResultId}`)
  return response.data?.data ?? response.data
}

/** Get exam result history for a specific exam */
export const getExamHistory = async (examId: number | string, params = {}) => {
  const response = await api.get('/exam-results', { params: { examId, ...params } })
  return response.data?.data ?? response.data
}

/** Get full exam detail (with sections & questions) — for admin/teacher view */
export const getExamById = async (id: number | string): Promise<ExamDetail> => {
  const response = await api.get(`/exams/${id}`)
  return response.data?.data ?? response.data
}

// ─── Management APIs ────────────────────────────────────────────────────────────

export const getMyExams = async (params = {}) => {
  const response = await api.get('/exams/me', { params })
  return response.data
}

export const createExam = async (data: any) => {
  const response = await api.post('/exams', data)
  return response.data
}

export const updateExam = async (id: number | string, data: any) => {
  const response = await api.put(`/exams/${id}`, data)
  return response.data
}

export const deleteExam = async (id: number | string) => {
  const response = await api.delete(`/exams/${id}`)
  return response.data
}

export const uploadExamMedia = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  const response = await api.post('/exams/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}
