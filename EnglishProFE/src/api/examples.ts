// Ví dụ sử dụng axiosapi
import api from './axios'

interface LoginResponse {
  accessToken: string
  refreshToken: string
  user?: Record<string, any>
}

interface UserInfo {
  id: string
  email: string
  fullName: string
  [key: string]: any
}

interface Exercise {
  id: string
  title: string
  [key: string]: any
}

interface ExercisesResponse {
  data: Exercise[]
  total: number
  page: number
  limit: number
}

interface SubmitExerciseResponse {
  success: boolean
  score?: number
  [key: string]: any
}

// ========== LOGIN - Lấy access token và refresh token ==========
export const login = async (email: string, password: string): Promise<LoginResponse> => {
  try {
    const response = await api.post<LoginResponse>('/auth/login', {
      email,
      password,
    })

    // Server trả về accessToken và refreshToken
    const { accessToken, refreshToken } = response.data

    // Lưu vào localStorage
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    return response.data
  } catch (error) {
    console.error('Login failed:', error)
    throw error
  }
}

// ========== GET USER INFO - Sử dụng interceptor tự động thêm token ==========
export const getUserInfo = async (): Promise<UserInfo> => {
  try {
    // Interceptor sẽ tự động thêm: Authorization: Bearer <token>
    const response = await api.get<UserInfo>('/user/info')
    return response.data
  } catch (error) {
    console.error('Get user info failed:', error)
    throw error
  }
}

// ========== LOGOUT ==========
export const logout = () => {
  // Xóa token khỏi localStorage
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')

  // Có thể gọi API logout nếu backend yêu cầu
  return api.post('/auth/logout')
}

// ========== CÁC API KHÁC ==========
export const getExercises = async (
  page: number = 1,
  limit: number = 10,
): Promise<ExercisesResponse> => {
  try {
    const response = await api.get<ExercisesResponse>('/exercises', {
      params: { page, limit },
    })
    return response.data
  } catch (error) {
    console.error('Get exercises failed:', error)
    throw error
  }
}

export const submitExercise = async (
  exerciseId: string,
  answers: Record<string, any>,
): Promise<SubmitExerciseResponse> => {
  try {
    const response = await api.post<SubmitExerciseResponse>(
      `/exercises/${exerciseId}/submit`,
      {
        answers,
      },
    )
    return response.data
  } catch (error) {
    console.error('Submit exercise failed:', error)
    throw error
  }
}
