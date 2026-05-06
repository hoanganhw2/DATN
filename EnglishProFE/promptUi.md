PROMPT F.0 – Khởi tạo Project & Design System (Chạy đầu tiên)
Hãy đóng vai Senior Frontend Engineer. Khởi tạo cấu trúc dự án Vue 3 cho EnglishPro.

Stack: Vue 3 (Composition API), Vite, TypeScript, Tailwind CSS, Pinia, Vue Router, Axios.
Màu chủ đạo (Brand Colors):

- Primary: #2563eb (Blue 600)
- Secondary: #4f46e5 (Indigo 600)
- Success: #10b981 (Emerald 500)
- Danger: #ef4444 (Red 500)
- Background: #f8fafc (Slate 50)

Yêu cầu:

1. Cấu hình tailwind.config.ts: Định nghĩa các biến màu primary, secondary, accent vào phần theme.extend.
2. Tạo file src/assets/main.css: Định nghĩa CSS Variables cho các màu trên.
3. Cấu hình Axios Instance:
   - Base URL: http://localhost:8080/api/v1.
   - Request Interceptor: Tự động đính kèm JWT từ Pinia Store.
   - Response Interceptor: Xử lý 401 (Unauthorized) để redirect về trang Login.
4. Tạo cấu trúc thư mục chuẩn: /src/api, /src/views, /src/components, /src/stores, /src/types.

Viết mã sạch, sử dụng script setup và kiến trúc modular.
🔐 MODULE 1 – AUTH & PROFILE
Implement chức năng Đăng ký & Đăng nhập cho EnglishPro dùng Vue 3.
Dựa trên Backend API: POST /auth/login, POST /auth/register.

Yêu cầu:

1. Auth Store (Pinia): Lưu trữ accessToken, refreshToken và thông tin user.
2. Login View:
   - Form validate bằng Vee-Validate và Zod.
   - Hiển thị thông báo lỗi từ Backend (dùng Toast notification).
3. Register View:
   - Field: username, email, password (validate 8 ký tự, hoa, thường, số), fullName.
   - Sau khi đăng ký thành công, tự động chuyển hướng sang Login.
4. Layout: Sử dụng một AuthLayout đơn giản với màu nền Primary nhạt.

Giữ đúng tone màu Blue #2563eb cho các nút bấm chính.
📖 MODULE 2 – LEARNING (COURSES & LESSONS)
Thiết kế giao diện học tập (Course Player) cho EnglishPro tương tự Udemy.
Dựa trên API: GET /courses/{slug} và POST /lessons/{lessonId}/complete.

Yêu cầu:

1. Giao diện chia 2 cột:
   - Cột trái (75%): Video Player (dùng vue-video-player hoặc iframe) và phần mô tả bài học.
   - Cột phải (25%): Danh sách Chapter và Lesson (Sidebar).
2. Sidebar:
   - Hiển thị tiến độ hoàn thành (checkbox đã học).
   - Click vào lesson sẽ load dữ liệu bài học đó mà không reload trang.
3. Logic: Khi video kết thúc (ended event), tự động gọi API complete lesson và update thanh tiến trình (Progress Bar).
4. Styling: Dùng màu Primary cho các lesson đang active.
   MODULE 3 – INTERACTIVE (FLASHCARDS & SRS)
   Implement giao diện học từ vựng Flashcard theo thuật toán SRS.
   API: GET /review-today, POST /flashcards/{id}/review.

Yêu cầu:

1. Card Component: Hiệu ứng xoay 180 độ (Flip animation) khi nhấn vào thẻ.
2. Control Buttons: Sau khi lật thẻ, hiện 4 nút: AGAIN (Đỏ), HARD (Cam), GOOD (Xanh lá), EASY (Xanh dương).
3. Audio: Nút bấm phát âm thanh (audioUrl) dùng HTML5 Audio API.
4. Keyboard Shortcuts: Phím Space để lật thẻ, phím 1-2-3-4 để chọn mức độ nhớ.
5. Progress: Hiển thị số thẻ còn lại trong hàng đợi.
   💳 MODULE 4 – PAYMENT & ORDER
   Implement luồng mua khóa học và thanh toán VNPay.
   API: POST /api/v1/orders.

Yêu cầu:

1. Course Detail: Nút "Mua khóa học" -> Nếu chưa login thì mở Modal Login, nếu rồi thì chuyển sang Checkout.
2. Checkout View: Hiển thị thông tin đơn hàng, tổng tiền, và chọn phương thức thanh toán.
3. Redirect Logic: Khi nhấn "Thanh toán", gọi API BE để lấy paymentUrl và redirect người dùng sang trang VNPay.
4. Success View: Nhận callback từ VNPay và hiển thị trạng thái đơn hàng (Thành công/Thất bại).
