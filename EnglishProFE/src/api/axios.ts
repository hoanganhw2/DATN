import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosError } from 'axios'

// Cấu hình base URL cho API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888/api/v1'

// Tạo instance Axios
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Flag để tránh gọi refresh token nhiều lần cùng lúc
let isRefreshing = false
let failedQueue: Array<{
  resolve: (token: string) => void
  reject: (error: any) => void
}> = []

// Xử lý queue request khi refresh token
const processQueue = (error: any, token: string | null = null): void => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token!)
    }
  })

  isRefreshing = false
  failedQueue = []
}

import { useAuthStore } from '@/stores/auth'

// ========== GET AUTH STORE ==========
const getAuthStore = () => {
  return useAuthStore()
}

// ========== Interceptor Request ==========
// Tự động thêm Authorization header vào mỗi request
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = getAuthStore()
    const token = authStore.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => Promise.reject(error),
)

// ========== Interceptor Response ==========
// Xử lý lỗi 401 và tự động refresh token
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    const authStore = getAuthStore()

    // Nếu lỗi 401 hoặc 403 (Token hết hạn thường bị trả về 403 nếu không config EntryPoint)
    // Nhưng KHÔNG refresh token nếu 403 là lỗi business logic (có message cụ thể từ backend)
    const status = error.response?.status
    const responseData = error.response?.data as any
    const isBusiness403 =
      status === 403 && responseData?.message && responseData.message !== 'Access Denied'

    if ((status === 401 || (status === 403 && !isBusiness403)) && !originalRequest._retry) {
      // Nếu đang refresh token, thêm vào queue
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return api(originalRequest)
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const refreshToken = authStore.refreshToken

        if (!refreshToken) {
          throw new Error('Không có refresh token')
        }

        // Gọi API refresh token
        const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, {
          refreshToken,
        })

        const { accessToken, refreshToken: newRefreshToken } = response.data

        // Cập nhật store
        authStore.setTokens(accessToken, newRefreshToken)

        // Cập nhật header cho original request
        originalRequest.headers.Authorization = `Bearer ${accessToken}`

        // Xử lý queue
        processQueue(null, accessToken)

        // Gọi lại request ban đầu
        return api(originalRequest)
      } catch (refreshError) {
        // Nếu refresh token thất bại, logout
        authStore.logout()
        processQueue(refreshError, null)

        // Redirect về login
        window.location.href = '/login'

        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export default api
