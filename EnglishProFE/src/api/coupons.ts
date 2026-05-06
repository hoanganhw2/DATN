import api from './axios'

export type CouponScope = 'GLOBAL' | 'SPECIFIC'

export interface Coupon {
  id: number
  code: string
  discountPercent: number
  isActive: boolean
  startDate: string | null
  endDate: string | null
  usageLimit: number | null
  usedCount: number
  valid: boolean
  scope: CouponScope
  createdById: number | null
  createdByUsername: string | null
  courseIds: number[]
  courseNames: string[]
}

export interface CouponPage {
  content: Coupon[]
  totalPages: number
  totalElements: number
  size: number
  number: number
}

// Lấy danh sách — Admin thấy tất cả, Teacher thấy của mình
export const getCoupons = async (page = 0, size = 10, keyword = '', scope = '') => {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  if (keyword) params.set('keyword', keyword)
  if (scope)   params.set('scope', scope)
  const response = await api.get<any>(`/coupons?${params.toString()}`)
  return response.data
}

// Validate lấy thông tin mã lúc user checkout
export const validateCoupon = async (code: string) => {
  const response = await api.get<any>(`/coupons/validate/${code}`)
  return response.data
}

export const createCoupon = async (data: {
  code: string
  discountPercent: number
  isActive: boolean
  startDate: string | null
  endDate: string | null
  usageLimit: number | null
  scope: CouponScope
  courseIds: number[]
}) => {
  const response = await api.post<any>('/coupons', data)
  return response.data
}

export const updateCoupon = async (id: number, data: {
  discountPercent?: number
  isActive?: boolean
  startDate?: string | null
  endDate?: string | null
  usageLimit?: number | null
  courseIds?: number[]
}) => {
  const response = await api.put<any>(`/coupons/${id}`, data)
  return response.data
}

export const deleteCoupon = async (id: number) => {
  const response = await api.delete<any>(`/coupons/${id}`)
  return response.data
}
