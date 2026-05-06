import api from './axios'

interface ApiResponse<T> {
  message?: string
  data: T
}

export const getSettings = async () => {
  const response = await api.get<ApiResponse<Record<string, string>>>('/settings')
  return response.data.data
}

export const updateSettings = async (settings: Record<string, string>) => {
  const response = await api.put<ApiResponse<void>>('/settings', settings)
  return response.data.data
}
