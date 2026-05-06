import api from './axios'

// ─── Types ────────────────────────────────────────────────────────────────────

export interface Certificate {
  id: number
  certificateCode: string
  studentName: string
  courseName: string
  instructorName: string
  courseId: number
  issuedAt: string // ISO instant string
}

// ─── API helpers ──────────────────────────────────────────────────────────────

/**
 * Yêu cầu cấp chứng chỉ cho khóa học đã hoàn thành 100%.
 * POST /api/v1/certificates/courses/{courseId}
 */
export const issueCertificate = async (courseId: number): Promise<Certificate> => {
  const response = await api.post<{ data: Certificate }>(`/certificates/courses/${courseId}`)
  return response.data.data
}

/**
 * Lấy chứng chỉ của khóa học cụ thể (nếu đã cấp).
 * GET /api/v1/certificates/courses/{courseId}
 */
export const getCertificate = async (courseId: number): Promise<Certificate | null> => {
  const response = await api.get<{ data: Certificate | null }>(`/certificates/courses/${courseId}`)
  return response.data.data
}

/**
 * Lấy tất cả chứng chỉ của user đang đăng nhập.
 * GET /api/v1/certificates/me
 */
export const getMyCertificates = async (): Promise<Certificate[]> => {
  const response = await api.get<{ data: Certificate[] }>('/certificates/me')
  return response.data.data ?? []
}

/**
 * Xác minh chứng chỉ bằng mã (public, không cần đăng nhập).
 * GET /api/v1/certificates/verify/{code}
 */
export const verifyCertificate = async (code: string): Promise<Certificate | null> => {
  const response = await api.get<{ data: Certificate | null }>(`/certificates/verify/${code}`)
  return response.data.data
}
