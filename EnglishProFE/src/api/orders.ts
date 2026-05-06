import axios from './axios'

// ─── Request / Response Types ────────────────────────────────────────────────
export interface CreateOrderRequest {
  courseId: number
  paymentMethod: string
  couponCode?: string
}

export interface CreateOrderResponse {
  orderId: number
  orderCode: string
  courseId: number
  courseTitle: string
  amount: number
  finalAmount: number
  status: string
  paymentUrl: string
  expiredAt: string
}

export interface OrderInfo {
  orderCode: string
  amount: number
  status: string
  courseTitle: string
}

export interface OrderResponse {
  id: number
  orderCode: string
  username: string
  fullName: string
  courseId: number
  courseTitle: string
  courseThumbnail?: string
  amount: number
  discountAmount: number
  finalAmount: number
  status: string
  expiredAt: string
  createdAt: string
}

// ─── API helpers ──────────────────────────────────────────────────────────────

/**
 * Tạo đơn hàng mới và nhận VNPay payment URL
 */
export const createOrder = async (data: CreateOrderRequest): Promise<CreateOrderResponse> => {
  const response = await axios.post<{ data: CreateOrderResponse }>('/orders', data)
  return response.data.data
}

/**
 * Lấy thông tin đơn hàng theo orderCode (public, không cần auth)
 */
export const getOrderInfo = async (orderCode: string): Promise<OrderInfo> => {
  const response = await axios.get<OrderInfo>(`/payments/order-info/${orderCode}`)
  return response.data
}

/**
 * Xác minh kết quả thanh toán từ VNPay return URL params
 */
export const verifyVNPayReturn = async (
  rawQueryString: string,
): Promise<{
  valid: boolean
  success: boolean
  responseCode: string
  orderCode: string
  transactionNo: string
  amount: number
}> => {
  const response = await axios.get(`/payments/return?${rawQueryString}`)
  return response.data
}

/**
 * Lấy lịch sử đơn hàng của bản thân (Student)
 */
export const getMyOrders = async (
  page = 0,
  size = 10,
): Promise<{ content: OrderResponse[]; totalElements: number }> => {
  const response = await axios.get(`/orders/me?page=${page}&size=${size}`)
  const data = response.data.data
  return {
    content: data.content,
    totalElements: data.totalElements ?? data.page?.totalElements ?? 0
  }
}

/**
 * Lấy tất cả đơn hàng (Admin)
 */
export const getAllOrders = async (
  page = 0,
  size = 10,
): Promise<{ content: OrderResponse[]; totalElements: number }> => {
  const response = await axios.get(`/orders?page=${page}&size=${size}`)
  const data = response.data.data
  return {
    content: data.content,
    totalElements: data.totalElements ?? data.page?.totalElements ?? 0
  }
}

/**
 * Hủy đơn hàng đang trong trạng thái PENDING
 */
export const cancelOrder = async (orderId: number): Promise<void> => {
  await axios.put(`/orders/${orderId}/cancel`)
}

/**
 * Tạo lại URL thanh toán VNPay cho đơn hàng PENDING
 * Backend sẽ tự gia hạn thêm 15 phút cho đơn hàng.
 * @returns paymentUrl - URL redirect đến cổng thanh toán VNPay
 */
export const repayOrder = async (orderId: number): Promise<string> => {
  const response = await axios.get<{ data: string }>(`/orders/${orderId}/repay`)
  return response.data.data
}

/**
 * Kiểm tra xem người dùng hiện tại đã mua (COMPLETED) khóa học hay chưa.
 * Trả về false nếu chưa đăng nhập hoặc chưa mua.
 */
export const checkCoursePurchased = async (courseId: number): Promise<boolean> => {
  try {
    const response = await axios.get<{ data: { purchased: boolean } }>(`/orders/me/purchased/${courseId}`)
    return response.data.data.purchased
  } catch (err: any) {
    console.error('[checkCoursePurchased] Lỗi khi kiểm tra trạng thái mua:', err?.response?.status, err?.response?.data || err?.message)
    return false
  }
}
