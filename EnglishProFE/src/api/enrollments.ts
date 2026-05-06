import api from './axios'

// ─── Types ────────────────────────────────────────────────────────────────────

export type EnrollmentStatus = 'IN_PROGRESS' | 'COMPLETED' | 'NOT_STARTED'

export interface EnrolledCourse {
  courseId: number
  slug: string
  title: string
  thumbnailUrl?: string
  teacherName?: string
  status: EnrollmentStatus
  progressPercent: number
  completedLessons: number
  totalLessons: number
  totalDurationSecs?: number
  enrolledAt: string       // ISO instant string
  lastAccessedAt?: string
  completedAt?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// ─── API helpers ──────────────────────────────────────────────────────────────

/**
 * Lấy danh sách khóa học đã đăng ký của student đang đăng nhập.
 * GET /api/v1/enrollments
 */
export const getMyEnrollments = async (
  page = 0,
  size = 20,
): Promise<PageResponse<EnrolledCourse>> => {
  const response = await api.get<{ data: PageResponse<EnrolledCourse> }>(
    `/enrollments?page=${page}&size=${size}`,
  )
  return response.data.data
}

/**
 * Đăng ký khóa học (thường gọi sau khi thanh toán thành công).
 * POST /api/v1/enrollments
 */
export const enrollCourse = async (courseId: number): Promise<EnrolledCourse> => {
  const response = await api.post<{ data: EnrolledCourse }>('/enrollments', { courseId })
  return response.data.data
}
