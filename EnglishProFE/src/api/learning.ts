import api from './axios'

// ─── Types ────────────────────────────────────────────────────────────────────

export interface LessonInfo {
  id: number
  title: string
  duration: number
  contentType: 'VIDEO' | 'TEXT' | 'QUIZ' | 'AUDIO'
  contentUrl?: string
  isFree: boolean
  orderIndex: number
}

export interface ChapterInfo {
  id: number
  title: string
  orderIndex: number
  lessons: LessonInfo[]
}

export interface CourseLearnData {
  id: number
  title: string
  slug: string
  description?: string
  thumbnailUrl?: string
  level: string
  teacher?: {
    id: number
    fullName: string
    avatar?: string
  }
  chapters: ChapterInfo[]
  totalLessons?: number
  totalDurationSecs?: number
}

export interface ChapterProgress {
  chapterId: number
  title: string
  completedLessons: number
  totalLessons: number
  completed: boolean
  completedLessonIds: number[]
}

export interface CourseProgressDetail {
  courseId: number
  courseTitle: string
  chapters: ChapterProgress[]
}

export interface LessonCompleteResult {
  lessonId: number
  completedAt: string
  courseProgress: {
    courseId: number
    completedLessons: number
    totalLessons: number
    progressPercent: number
    completed: boolean
  }
}

// ─── API helpers ──────────────────────────────────────────────────────────────

/**
 * Lấy chi tiết khóa học (dùng course ID) — reuse endpoint /courses/:slug nhưng cần slug.
 * Ở đây ta dùng lại getCourseDetail bằng slug,
 * nhưng nếu chỉ có courseId thì dùng workaround:
 * fetch tất cả enrollments → lấy slug → gọi detail.
 */
export const getCourseForLearning = async (courseId: number): Promise<CourseLearnData> => {
  // Dùng endpoint courses by id (slug hoặc id đều được do controller logic)
  const response = await api.get<{ data: CourseLearnData }>(`/courses/${courseId}`)
  return response.data.data
}

/**
 * Lấy tiến độ từng chương.
 * GET /api/v1/enrollments/{courseId}/progress
 */
export const getCourseProgress = async (courseId: number): Promise<CourseProgressDetail> => {
  const response = await api.get<{ data: CourseProgressDetail }>(`/enrollments/${courseId}/progress`)
  return response.data.data
}

/**
 * Đánh dấu hoàn thành bài học.
 * POST /api/v1/lessons/{lessonId}/complete
 */
export const markLessonComplete = async (lessonId: number): Promise<LessonCompleteResult> => {
  const response = await api.post<{ data: LessonCompleteResult }>(`/lessons/${lessonId}/complete`)
  return response.data.data
}

/**
 * Kiểm tra quyền truy cập trang học.
 * Nếu chưa mua hoặc chưa enroll → backend trả 403.
 * GET /api/v1/enrollments/{courseId}/verify-access
 */
export const verifyLearningAccess = async (courseId: number): Promise<boolean> => {
  const response = await api.get<{ data: { hasAccess: boolean } }>(`/enrollments/${courseId}/verify-access`)
  return response.data.data.hasAccess
}

/**
 * Lấy danh sách lesson đã hoàn thành để biết lesson nào checked.
 * Dùng completedLessonIds từ API progress response.
 */
export const getCompletedLessonIds = async (courseId: number, chapters: ChapterInfo[]): Promise<Set<number>> => {
  try {
    const progress = await getCourseProgress(courseId)
    const completedIds = new Set<number>()

    for (const chProgress of progress.chapters) {
      if (chProgress.completedLessonIds && chProgress.completedLessonIds.length > 0) {
        chProgress.completedLessonIds.forEach(id => completedIds.add(id))
      }
    }

    return completedIds
  } catch {
    return new Set()
  }
}

