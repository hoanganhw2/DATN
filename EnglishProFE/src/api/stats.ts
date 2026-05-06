import api from './axios'

// ── Shared types ──────────────────────────────────────────────────────────────

export interface OverallStats {
  totalOrders: number
  totalRevenue: number
  completedOrders: number
  pendingOrders: number
  failedOrders: number
  teacherRevenue: number   // 70%
  adminRevenue: number     // 30%
}

export interface MonthlyRevenue {
  month: number
  revenue: number
  orderCount: number
  teacherRevenue: number
  adminRevenue: number
}

export interface CourseRevenue {
  courseId: number
  courseTitle: string
  revenue: number
  orderCount: number
  teacherRevenue: number
  adminRevenue: number
}

export interface StatusBreakdown {
  status: string
  count: number
  totalAmount: number
}

export interface TeacherSummary {
  teacherId: number
  username: string
  fullName: string
  totalRevenue: number
  orderCount: number
  teacherRevenue: number
  adminRevenue: number
}

// ── Admin ─────────────────────────────────────────────────────────────────────

export interface AdminRevenueStats {
  overall: OverallStats
  monthly: MonthlyRevenue[]
  topCourses: CourseRevenue[]
  statusBreakdown: StatusBreakdown[]
  teacherBreakdown: TeacherSummary[]
  year: number
}

/**
 * GET /admin/stats/revenue?year=
 */
export const getAdminRevenueStats = async (year: number): Promise<AdminRevenueStats> => {
  const res = await api.get(`/admin/stats/revenue?year=${year}`)
  return res.data?.data ?? res.data
}

// ── Teacher ───────────────────────────────────────────────────────────────────

export interface TeacherRevenueStats {
  overall: OverallStats
  monthly: MonthlyRevenue[]
  topCourses: CourseRevenue[]
  year: number
}

export interface TeacherStudentResponse {
  userId: number
  username: string
  email: string
  fullName: string
  phone: string
  courseTitle: string
  purchaseDate: string
  status: string
}

/**
 * GET /teacher/stats/revenue?year=
 */
export const getTeacherRevenueStats = async (year: number): Promise<TeacherRevenueStats> => {
  const res = await api.get(`/teacher/stats/revenue?year=${year}`)
  return res.data?.data ?? res.data
}

/**
 * GET /teacher/students
 */
export const getTeacherStudents = async (): Promise<TeacherStudentResponse[]> => {
  const res = await api.get('/teacher/students')
  return res.data?.data ?? res.data
}

// ── Per-course student details (progress + exercise scores) ──────────────

export interface ExerciseScoreItem {
  exerciseId: number
  exerciseTitle: string
  lessonTitle: string
  totalQuestions: number
  correctAnswers: number
  scorePercent: number
  submittedAt: string | null
}

export interface CourseStudentDetail {
  userId: number
  username: string
  email: string
  fullName: string
  avatarUrl: string | null
  enrolledAt: string
  lastAccessedAt: string | null
  lastLoginAt: string | null
  enrollmentStatus: string
  progressPercent: number
  completedLessons: number
  totalLessons: number
  exerciseScores: ExerciseScoreItem[]
}

/**
 * GET /teacher/courses/{courseId}/students
 */
export const getCourseStudents = async (courseId: number): Promise<CourseStudentDetail[]> => {
  const res = await api.get(`/teacher/courses/${courseId}/students`)
  return res.data?.data ?? res.data
}
