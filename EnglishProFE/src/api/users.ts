import api from './axios'

export interface UserProfileData {
  phone?: string
  avatarUrl?: string
  gender?: string
  address?: string
  dateOfBirth?: string
  country?: string
  learningGoal?: string
}

export interface UserMeResponse {
  id: number
  username: string
  email: string
  fullName?: string
  role: string
  status: string
  createdAt?: string
  profile?: UserProfileData
}

/**
 * GET /users/me — lấy thông tin chi tiết user đang đăng nhập (bao gồm profile)
 */
export const getMe = async (): Promise<UserMeResponse> => {
  const res = await api.get('/users/me')
  return res.data?.data ?? res.data
}

/**
 * PATCH /users/me — cập nhật profile (fullName, phone, gender, address, ...)
 */
export const updateMe = async (data: {
  fullName?: string
  phone?: string
  gender?: string
  address?: string
  country?: string
  dateOfBirth?: string
  learningGoal?: string
}): Promise<UserMeResponse> => {
  const res = await api.patch('/users/me', data)
  return res.data?.data ?? res.data
}

// ─── Admin APIs ───────────────────────────────────────────────────────────────

export interface AdminUserResponse {
  id: number
  username: string
  email: string
  fullName?: string
  phone?: string
  role: string
  status: string
  createdAt?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

/**
 * GET /users?page=&size=&keyword= — [ADMIN] lấy danh sách tất cả người dùng
 *
 * Spring Data trả về format mới:
 * { content: [...], page: { size, number, totalElements, totalPages } }
 */
export const getAllUsers = async (
  page = 0,
  size = 20,
  keyword = '',
): Promise<PageResponse<AdminUserResponse>> => {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  if (keyword.trim()) params.append('keyword', keyword.trim())
  const res = await api.get(`/users?${params}`)
  const data = res.data?.data ?? res.data

  // Hỗ trợ cả 2 format: page object lồng vào (Spring 3+) và flat object cũ
  const pageInfo = data.page ?? data
  return {
    content:       data.content ?? [],
    totalElements: pageInfo.totalElements ?? 0,
    totalPages:    pageInfo.totalPages ?? 1,
    number:        pageInfo.number ?? 0,
    size:          pageInfo.size ?? size,
  }
}

/**
 * PUT /users/{id} — [ADMIN] cập nhật role/status người dùng
 */
export const adminUpdateUser = async (
  id: number,
  data: { role: string; status: string },
): Promise<AdminUserResponse> => {
  const res = await api.put(`/users/${id}`, data)
  return res.data?.data ?? res.data
}

/**
 * DELETE /users/{id} — [ADMIN] xóa người dùng
 */
export const deleteUser = async (id: number): Promise<void> => {
  await api.delete(`/users/${id}`)
}

export interface UserStatsResponse {
  totalUsers: number
  students: number
  teachers: number
  admins: number
}

export const getUserStats = async (): Promise<UserStatsResponse> => {
  const res = await api.get('/users/stats')
  return res.data?.data ?? res.data
}

