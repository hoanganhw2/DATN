import api from './axios'
import type { ApiResponse, PageResponse } from './courses'

export interface CourseReview {
  id: number
  userId: number
  userFullName: string | null
  stars: number
  comment: string | null
  createdAt: string
  updatedAt: string
}

export interface UpsertCourseReviewBody {
  stars: number
  comment?: string
}

/** GET /courses/{courseId}/reviews — public */
export async function listCourseReviews(courseId: number, page = 0, size = 10) {
  const { data } = await api.get<ApiResponse<PageResponse<CourseReview>>>(
    `/courses/${courseId}/reviews`,
    { params: { page, size, sort: 'createdAt,desc' } },
  )
  return data.data
}

/** GET /courses/{courseId}/reviews/me — auth; data có thể null */
export async function getMyCourseReview(courseId: number) {
  const { data } = await api.get<ApiResponse<CourseReview | null>>(`/courses/${courseId}/reviews/me`)
  return data.data
}

/** POST /courses/{courseId}/reviews — tạo / cập nhật */
export async function upsertCourseReview(courseId: number, body: UpsertCourseReviewBody) {
  const { data } = await api.post<ApiResponse<CourseReview>>(`/courses/${courseId}/reviews`, body)
  return data.data
}

/** DELETE /courses/{courseId}/reviews/me */
export async function deleteMyCourseReview(courseId: number) {
  await api.delete(`/courses/${courseId}/reviews/me`)
}
