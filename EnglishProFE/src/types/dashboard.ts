/**
 * EnglishPro Dashboard - Type Definitions
 * Centralized TypeScript types for the dashboard system
 */

// ==================== User & Auth ====================

export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT'
export type UserStatus = 'active' | 'inactive'

export interface User {
  id: string
  fullName: string
  email: string
  username: string
  phone?: string
  role: UserRole
  status?: UserStatus
  avatar?: string
  createdAt?: string
  updatedAt?: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: User
}

// ==================== Course ====================

export type CourseLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1'
export type CourseCategory = 'beginner' | 'intermediate' | 'advanced'
export type CourseStatus = 'published' | 'draft'

export interface Course {
  id: string
  title: string
  description: string
  teacherId: string
  teacher?: User
  price: number
  level: CourseLevel
  category: CourseCategory
  thumbnail?: string
  language: string
  duration: number
  status: CourseStatus
  skills: string[]
  students?: number
  lessons?: number
  averageRating?: number
  createdAt?: string
  updatedAt?: string
}

export interface CourseCreateRequest {
  title: string
  description: string
  price: number
  level: CourseLevel
  category: CourseCategory
  thumbnail?: string
  language: string
  duration: number
  status: CourseStatus
  skills: string[]
}

// ==================== Chapter & Lesson ====================

export interface Chapter {
  id: string
  courseId: string
  title: string
  description?: string
  order: number
  lessons?: Lesson[]
  createdAt?: string
  updatedAt?: string
}

export interface ChapterCreateRequest {
  title: string
  description?: string
}

export interface Lesson {
  id: string
  courseId: string
  chapterId: string
  title: string
  content: string
  media?: string
  order: number
  duration?: number
  createdAt?: string
  updatedAt?: string
}

export interface LessonCreateRequest {
  title: string
  content: string
  media?: string
}

// ==================== Exercise ====================

export type ExerciseType = 'multiple' | 'fillBlank'

export interface ExerciseOption {
  id?: string
  text: string
  isCorrect?: boolean
}

export interface Exercise {
  id: string
  lessonId: string
  type: ExerciseType
  question: string
  options?: ExerciseOption[]
  correctAnswer?: number
  answer?: string
  points: number
  explanation?: string
  createdAt?: string
  updatedAt?: string
}

export interface ExerciseCreateRequest {
  type: ExerciseType
  question: string
  options?: string[]
  correctAnswer?: number
  answer?: string
  points: number
  explanation?: string
}

export interface StudentAnswer {
  studentId: string
  exerciseId: string
  answer: string | number
  isCorrect: boolean
  score: number
  completedAt: string
}

// ==================== Enrollment ====================

export type EnrollmentStatus = 'active' | 'completed' | 'paused'

export interface Enrollment {
  id: string
  courseId: string
  studentId: string
  course?: Course
  student?: User
  status: EnrollmentStatus
  progressPercentage: number
  score: number
  completedAt?: string
  enrolledAt: string
  lastAccessedAt?: string
}

// ==================== Order ====================

export type OrderStatus = 'pending' | 'success' | 'failed' | 'refunded'
export type PaymentMethod = 'vnpay' | 'momo' | 'card' | 'bank_transfer'

export interface Order {
  id: string
  studentId: string
  courseId: string
  amount: number
  method: PaymentMethod
  status: OrderStatus
  transactionCode?: string
  notes?: string
  createdAt: string
  updatedAt?: string
  student?: User
  course?: Course
}

export interface OrderUpdateRequest {
  status: OrderStatus
  transactionCode?: string
  notes?: string
}

// ==================== Dashboard Stats ====================

export interface AdminDashboardStats {
  totalRevenue: number
  totalUsers: number
  totalCourses: number
  todayOrders: number
  userStats: {
    students: number
    teachers: number
    admins: number
  }
  recentOrders?: Order[]
}

export interface TeacherDashboardStats {
  totalStudents: number
  totalCourses: number
  monthlyRevenue: number
  averageRating: number
  recentEnrollments?: Enrollment[]
  topCourses?: CoursePerformance[]
}

export interface CoursePerformance {
  id: string
  name: string
  enrollment: number
  students: number
  averageScore: number
  rating: number
}

// ==================== Student Progress ====================

export interface StudentProgress {
  studentId: string
  courseId: string
  progressPercentage: number
  averageScore: number
  completedLessons: number
  totalLessons: number
  lastAccessedAt?: string
  lessonProgress?: LessonProgress[]
}

export interface LessonProgress {
  lessonId: string
  title: string
  completedAt?: string
  score?: number
  maxScore?: number
  timeSpent?: number
}

// ==================== Review & Rating ====================

export interface Review {
  id: string
  studentId: string
  courseId: string
  rating: number
  comment: string
  createdAt: string
  student?: User
}

// ==================== API Response ====================

export interface ApiResponse<T> {
  success: boolean
  data?: T
  error?: string
  message?: string
}

export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  limit: number
  totalPages: number
}

// ==================== Form Data ====================

export interface UserFormData {
  fullName: string
  email: string
  username: string
  phone?: string
  role: UserRole
  password?: string
  status: UserStatus
}

export interface CourseFormData {
  title: string
  description: string
  price: number
  level: CourseLevel
  category: CourseCategory
  thumbnail?: string
  language: string
  duration: number
  status: CourseStatus
  skills: string[]
}

// ==================== Table Column ====================

export type ColumnType = 'text' | 'badge' | 'date' | 'number'

export interface TableColumn<T = any> {
  key: keyof T | string
  label: string
  type?: ColumnType
  sortable?: boolean
  render?: (row: T) => string | number | void
  width?: string
}

export interface TableAction<T = any> {
  label: string
  icon?: any
  handler: (row: T) => void
  color?: 'red' | 'blue' | 'gray'
}

// ==================== Modal ====================

export interface ModalState {
  isOpen: boolean
  title: string
  content?: any
  confirmText?: string
  cancelText?: string
}

// ==================== Toast ====================

export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface Toast {
  id: string
  message: string
  type: ToastType
  duration?: number
}

// ==================== Sidebar Navigation ====================

export interface NavItem {
  label: string
  path: string
  icon?: any
  roles?: UserRole[]
  children?: NavItem[]
}

// ==================== Filter & Search ====================

export interface FilterOptions {
  search?: string
  role?: UserRole
  status?: UserStatus | OrderStatus
  courseId?: string
  page?: number
  limit?: number
  sort?: string
}

export interface SearchResult<T> {
  items: T[]
  total: number
  hasMore: boolean
}

// ==================== Validation ====================

export interface ValidationError {
  field: string
  message: string
}

export interface ValidationResult {
  isValid: boolean
  errors: ValidationError[]
}

// ==================== UI State ====================

export interface LoadingState {
  isLoading: boolean
  error?: string
}

export interface DeleteConfirmation {
  itemId: string
  itemName: string
  onConfirm: () => void
}

// ==================== Chart Data ====================

export interface ChartDataPoint {
  label: string
  value: number
  date?: string
}

export interface ChartData {
  labels: string[]
  datasets: Array<{
    label: string
    data: number[]
    backgroundColor?: string
    borderColor?: string
  }>
}

// ==================== Helper Types ====================

export type Nullable<T> = T | null
export type Optional<T> = T | undefined
export type AsyncFunction<T> = () => Promise<T>
