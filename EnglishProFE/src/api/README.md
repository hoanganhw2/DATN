# Cấu hình Axios với JWT - Hướng dẫn sử dụng

## 📋 Tổng quan

Hệ thống Axios này tự động xử lý:

- ✅ Thêm JWT token vào mỗi request
- ✅ Tự động làm mới token khi hết hạn (Auto Refresh)
- ✅ Xử lý lỗi 401 một cách thông minh
- ✅ Tránh gọi refresh token nhiều lần cùng lúc (Queue management)

## 🔧 Cấu hình

### 1. Tạo file `.env.local`

Sao chép từ `.env.example`:

```bash
VITE_API_URL=http://localhost:8888/api/v1
```

### 2. Sử dụng trong Vue components

```javascript
import api from '@/api/axios'

// Hoặc import các hàm từ examples.js
import { login, getUserInfo, logout } from '@/api/examples'

export default {
  methods: {
    async handleLogin() {
      try {
        const data = await login('user@example.com', 'password123')
        console.log('Login success:', data)
      } catch (error) {
        console.error('Login error:', error)
      }
    },

    async fetchUserInfo() {
      try {
        // Interceptor sẽ tự động thêm Authorization header
        const userInfo = await getUserInfo()
        this.user = userInfo
      } catch (error) {
        console.error('Fetch user info error:', error)
      }
    },
  },
}
```

## 🔑 Quản lý Token

### Lưu token sau khi login

```javascript
import { tokenManager } from '@/api/axios'

// Sau khi login thành công
tokenManager.setToken(accessToken)
tokenManager.setRefreshToken(refreshToken)
```

### Lấy token

```javascript
const token = tokenManager.getToken()
const refreshToken = tokenManager.getRefreshToken()
```

### Xóa token khi logout

```javascript
import { tokenManager } from '@/api/axios'

tokenManager.clearTokens() // Hoặc dùng logout() từ examples.js
```

## 🔄 Cách hoạt động của Auto Refresh Token

### Kịch bản 1: Token hợp lệ

```
Request 1 → ✅ Success (token hợp lệ)
```

### Kịch bản 2: Token hết hạn

```
Request 1 → ❌ 401 Unauthorized
           ↓
Gọi /auth/refresh-token → ✅ Nhận token mới
           ↓
Tự động gọi lại Request 1 → ✅ Success
```

### Kịch bản 3: Nhiều request cùng lúc mà token hết hạn

```
Request 1 → ❌ 401
Request 2 → ❌ 401 (đang chờ refresh)
Request 3 → ❌ 401 (đang chờ refresh)
           ↓
Gọi /auth/refresh-token 1 lần duy nhất → ✅ Lấy token mới
           ↓
Tất cả 3 requests được gọi lại → ✅ Success
```

## 📡 Định dạng Response từ Backend

Backend cần trả về token theo format này:

### Login endpoint: `POST /auth/login`

```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

### Refresh token endpoint: `POST /auth/refresh-token`

```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...(optional)"
}
```

## ⚠️ Lỗi phổ biến

### 1. Token không được thêm vào request

**Vấn đề**: Một số endpoint không nhận được Authorization header

**Giải pháp**: Kiểm tra:

- Token có được lưu bằng `tokenManager.setToken()` không?
- Có sử dụng đúng instance axios không? (nhập từ `@/api/axios`)

### 2. Redirect vòng lặp

**Vấn đề**: Ứng dụng liên tục redirect về `/login`

**Giải pháp**: Kiểm tra:

- Backend có đúng không? (refresh token endpoint)
- Refresh token có hợp lệ không?
- Có xử lý redirect trong router guards không?

### 3. Refresh token không hoạt động

**Vấn đề**: Vẫn logout dù có refresh token

**Giải pháp**:

- Kiểm tra CORS settings trên backend
- Kiểm tra request body được gửi chính xác không
- Kiểm tra response format từ backend

## 🛡️ Security Notes

1. **Không bao giờ** lưu token trong sessionStorage hoặc cookie (dễ bị XSS)
   - localStorage là an toàn hơn cho SPA
   - Hoặc dùng httpOnly cookies nếu backend hỗ trợ

2. **Luôn** sử dụng HTTPS trong production

3. **Token expiration**:
   - Access token nên có thời gian hết hạn ngắn (15 phút)
   - Refresh token nên có thời gian dài hơn (7 ngày)

4. **Xoá token khi logout** để tránh reuse token cũ

## 📝 Ví dụ sử dụng trong Pinia Store

```javascript
// stores/auth.js
import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/examples'
import { tokenManager } from '@/api/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
  }),

  actions: {
    async login(email, password) {
      const data = await login(email, password)
      this.user = data.user
      this.isAuthenticated = true
      return data
    },

    async loadUserInfo() {
      try {
        const userInfo = await getUserInfo()
        this.user = userInfo
        this.isAuthenticated = true
      } catch (error) {
        this.isAuthenticated = false
        throw error
      }
    },

    logout() {
      logout()
      this.user = null
      this.isAuthenticated = false
      tokenManager.clearTokens()
    },
  },
})
```

## 🔗 File liên quan

- `src/api/axios.js` - Cấu hình Axios chính
- `src/api/examples.js` - Ví dụ API calls
- `.env.example` - Cấu hình môi trường
