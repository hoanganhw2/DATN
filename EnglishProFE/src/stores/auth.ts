import { defineStore } from 'pinia'
import api from '@/api/axios'

interface User {
  id?: string
  fullName: string
  email: string
  username: string
  phone?: string
  avatar?: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
}

interface AuthState {
  user: User | null
  token: string | null
  refreshToken: string | null
  isLoading: boolean
  error: string | null
}

interface LoginCredentials {
  email: string
  password: string
}

interface RegisterData {
  fullName: string
  email: string
  username: string
  phone: string
  password: string
  role: 'TEACHER' | 'STUDENT'
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: JSON.parse(localStorage.getItem('englishpro_user') || 'null'),
    token: localStorage.getItem('englishpro_token'),
    refreshToken: localStorage.getItem('englishpro_refresh_token'),
    isLoading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    getCurrentUser: (state) => state.user,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isTeacher: (state) => state.user?.role === 'TEACHER',
    isStudent: (state) => state.user?.role === 'STUDENT',
    hasRole: (state) => (role: string) => state.user?.role === role,
  },

  actions: {
    async login(credentials: LoginCredentials) {
      this.isLoading = true
      this.error = null

      try {
        const response = await api.post('/auth/login', {
          email: credentials.email,
          password: credentials.password,
        })

        const payload = response.data.data ?? response.data
        const { accessToken, refreshToken, userId, email, username, role } = payload
        
        const user = {
          id: userId != null ? String(userId) : undefined,
          email,
          username,
          role,
          fullName: payload.fullName || username
        }

        this.setAuthData(accessToken, refreshToken, user)

        return { success: true, user }
      } catch (err: any) {
        const message = err.response?.data?.message || 'Email hoặc mật khẩu không chính xác'
        this.error = message
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async register(userData: RegisterData) {
      this.isLoading = true
      this.error = null
      try {
        const response = await api.post('/auth/register', userData)
        return { success: true, user: response.data }
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Đăng ký thất bại'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    setAuthData(token: string, refreshToken: string, user: User) {
      this.token = token
      this.refreshToken = refreshToken
      this.user = user

      localStorage.setItem('englishpro_token', token)
      localStorage.setItem('englishpro_refresh_token', refreshToken)
      localStorage.setItem('englishpro_user', JSON.stringify(user))
    },

    setTokens(accessToken: string, refreshToken?: string) {
      this.token = accessToken
      localStorage.setItem('englishpro_token', accessToken)
      if (refreshToken) {
        this.refreshToken = refreshToken
        localStorage.setItem('englishpro_refresh_token', refreshToken)
      }
    },

    logout() {
      this.user = null
      this.token = null
      this.refreshToken = null
      this.error = null

      localStorage.removeItem('englishpro_token')
      localStorage.removeItem('englishpro_refresh_token')
      localStorage.removeItem('englishpro_user')
    },

    clearError() {
      this.error = null
    },
  },
})
