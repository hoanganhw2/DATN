import api from './axios'

export type CourseLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'
export type CourseApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface Course {
  id: number
  title: string
  slug: string
  description?: string
  thumbnailUrl?: string
  level: CourseLevel
  category?: string
  price: number
  originalPrice?: number
  isPublished?: boolean
  approvalStatus?: CourseApprovalStatus
  teacherFullName?: string
  teacher?: { id: number; fullName?: string }
  teacherId?: number
  avgRating?: number
  totalReviews?: number
  totalLessons?: number
  totalEnrollments?: number
}

export interface Lesson {
  id: number
  title: string
  description?: string
  duration: number
  contentType: 'VIDEO' | 'TEXT' | 'QUIZ' | 'AUDIO' | 'DOCUMENT'
  contentUrl?: string
  isFree: boolean
  orderIndex?: number
}

export interface Chapter {
  id: number
  title: string
  orderIndex: number
  lessons: Lesson[]
}

export interface CourseDetail extends Course {
  totalDurationSecs?: number
  teacher?: {
    id: number
    fullName: string
    avatar?: string
    bio?: string
  }
  chapters: Chapter[]
  enrollmentInfo?: {
    isEnrolled: boolean
    enrolledAt?: string
    enrollmentDate?: string
    progressPercent?: number
    progress?: number
  }
}

interface ApiResponse<T> {
  message?: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface CourseFilterParams {
  keyword?: string
  level?: string[] | string
  category?: string[] | string
  page: number
  size: number
  sort?: string
}

export interface CreateCoursePayload {
  title: string
  slug: string
  description?: string
  level: CourseLevel
  category?: string
  price: number
  thumbnailUrl?: string
}

export function mapCourseListItem(raw: Record<string, unknown>) {
  const teacher = raw.teacher as { id?: number; fullName?: string } | undefined
  return {
    ...raw,
    teacherFullName: teacher?.fullName ?? (raw.teacherFullName as string | undefined) ?? '',
    teacherId: teacher?.id ?? raw.teacherId,
    price: raw.price != null ? Number(raw.price) : 0,
    originalPrice: raw.originalPrice != null ? Number(raw.originalPrice) : undefined,
    avgRating: raw.avgRating != null ? Number(raw.avgRating) : 0,
    totalReviews: raw.totalReviews != null ? Number(raw.totalReviews) : 0,
    totalLessons: raw.totalLessons != null ? Number(raw.totalLessons) : 0,
    totalEnrollments: raw.totalEnrollments != null ? Number(raw.totalEnrollments) : 0,
  } as Course
}

function mapCourseDetail(raw: CourseDetail): CourseDetail {
  const e = raw.enrollmentInfo
  const mappedEnrollment =
    e && !e.progress && e.progressPercent != null ? { ...e, progress: Number(e.progressPercent) } : e
  return {
    ...raw,
    price: raw.price != null ? Number(raw.price) : 0,
    originalPrice: raw.originalPrice != null ? Number(raw.originalPrice) : undefined,
    avgRating: raw.avgRating != null ? Number(raw.avgRating) : 0,
    totalReviews: raw.totalReviews != null ? Number(raw.totalReviews) : 0,
    enrollmentInfo: mappedEnrollment as CourseDetail['enrollmentInfo'],
  }
}

export const getCourses = async (params: CourseFilterParams) => {
  const queryParams = {
    page: params.page - 1,
    size: params.size,
    keyword: params.keyword || undefined,
    level: Array.isArray(params.level) ? params.level[0] : params.level,
    category: Array.isArray(params.category) ? params.category[0] : params.category,
    sort: params.sort || undefined,
  }
  const response = await api.get<ApiResponse<PageResponse<Record<string, unknown>>>>('/courses', {
    params: queryParams,
  })
  const page = response.data.data
  return {
    ...page,
    content: (page.content || []).map((c) => mapCourseListItem(c)),
  }
}

export const getCourseDetail = async (slug: string) => {
  const response = await api.get<ApiResponse<CourseDetail>>(`/courses/${slug}`)
  return mapCourseDetail(response.data.data)
}

export const getTeacherCourses = async (page = 1, size = 20) => {
  const response = await api.get<ApiResponse<PageResponse<Record<string, unknown>>>>(
    '/courses/teacher/courses',
    { params: { page: page - 1, size } },
  )
  const pageData = response.data.data
  return {
    ...pageData,
    content: (pageData.content || []).map((c) => mapCourseListItem(c)),
  }
}

export const createCourse = async (payload: CreateCoursePayload) => {
  const response = await api.post<ApiResponse<CourseDetail>>('/courses/', payload)
  return mapCourseDetail(response.data.data)
}

export interface UpdateCoursePayload {
  title?: string
  slug?: string
  description?: string
  level?: CourseLevel
  category?: string
  price?: number
  thumbnailUrl?: string
}

export const updateCourse = async (courseId: number, payload: UpdateCoursePayload) => {
  const response = await api.put<ApiResponse<CourseDetail>>(`/courses/${courseId}`, payload)
  return mapCourseDetail(response.data.data)
}

export const deleteCourse = async (courseId: number) => {
  await api.delete(`/courses/courses/${courseId}`)
}

export const getPendingCoursesForAdmin = async (page = 1, size = 20) => {
  const response = await api.get<ApiResponse<PageResponse<Record<string, unknown>>>>(
    '/courses/admin/pending',
    { params: { page: page - 1, size } },
  )
  const pageData = response.data.data
  return {
    ...pageData,
    content: (pageData.content || []).map((c) => mapCourseListItem(c)),
  }
}

export const reviewCourseApproval = async (courseId: number, approved: boolean) => {
  const response = await api.put<ApiResponse<CourseDetail>>(`/courses/${courseId}/approval`, { approved })
  return mapCourseDetail(response.data.data)
}

// --- CURRICULUM MANAGEMENT ---

export interface CreateChapterPayload {
  title: string
  description?: string
  orderIndex: number
}

export const createChapter = async (courseId: number, payload: CreateChapterPayload) => {
  const response = await api.post<ApiResponse<Chapter>>(`/courses/${courseId}/chapters`, payload)
  return response.data.data
}

export const updateChapter = async (courseId: number, chapterId: number, payload: CreateChapterPayload) => {
  const response = await api.put<ApiResponse<Chapter>>(`/courses/${courseId}/chapters/${chapterId}`, payload)
  return response.data.data
}

export const deleteChapter = async (courseId: number, chapterId: number) => {
  await api.delete(`/courses/${courseId}/chapters/${chapterId}`)
}

export interface CreateLessonPayload {
  title: string
  description?: string
  contentType: 'VIDEO' | 'TEXT' | 'QUIZ' | 'AUDIO' | 'DOCUMENT'
  contentUrl?: string
  duration?: number
  orderIndex: number
  isFree: boolean
}

export const createLesson = async (courseId: number, chapterId: number, payload: CreateLessonPayload) => {
  const response = await api.post<ApiResponse<Lesson>>(`/courses/${courseId}/chapters/${chapterId}/lessons`, payload)
  return response.data.data
}

export const updateLesson = async (lessonId: number, payload: CreateLessonPayload) => {
  const response = await api.put<ApiResponse<Lesson>>(`/lessons/${lessonId}`, payload)
  return response.data.data
}

export const deleteLesson = async (lessonId: number) => {
  await api.delete(`/lessons/${lessonId}`)
}

export const uploadLessonMedia = async (lessonId: number, file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  const response = await api.post<ApiResponse<{ contentUrl: string; duration?: number }>>(
    `/lessons/${lessonId}/upload`,
    formData,
    { headers: { 'Content-Type': 'multipart/form-data' } }
  )
  return response.data.data
}

// ── Reorder ───────────────────────────────────────────────────────────────

export interface ReorderItem {
  id: number
  orderIndex: number
}

export interface ReorderPayload {
  chapters: ReorderItem[]
  lessons: ReorderItem[]
}

export const reorderCurriculum = async (courseId: number, payload: ReorderPayload) => {
  await api.put(`/courses/${courseId}/chapters/reorder`, payload)
}
