# EnglishPro Dashboard System

Hệ thống quản trị Dashboard cho nền tảng học tiếng Anh trực tuyến EnglishPro.

## 📋 Mục lục

- [Tính năng cho Admin](#admin-features)
- [Tính năng cho Teacher](#teacher-features)
- [Cấu trúc thư mục](#directory-structure)
- [Hướng dẫn sử dụng](#usage)
- [API Integration](#api-integration)

## Admin Features

### 1. Dashboard Tổng quát (`/dashboard/admin`)

- Thống kê doanh thu, người dùng, khóa học
- Biểu đồ doanh thu 12 tháng
- Danh sách đơn hàng gần đây
- Phân bố người dùng theo vai trò

**Components:**

- `StatCard` - Thẻ thống kê với icon và xu hướng
- `DataTable` - Bảng dữ liệu với sort, filter
- Charts (placeholders for Chart.js integration)

### 2. Quản lý Người dùng (`/dashboard/admin/users`)

- Liệt kê tất cả người dùng
- Tìm kiếm theo tên, email
- Lọc theo vai trò (Admin, Teacher, Student)
- Lọc theo trạng thái (Active, Inactive)
- Thêm, sửa, xóa người dùng
- Cấp quyền vai trò
- Khóa/mở khóa tài khoản

**Features:**

- Form validation
- Modal dialog
- Toast notification (success/error)
- Responsive table design

### 3. Quản lý Đơn hàng (`/dashboard/admin/orders`)

- Liệt kê giao dịch thanh toán
- Tìm kiếm theo mã đơn hàng, email
- Lọc theo phương thức (VNPay, Momo, Card)
- Lọc theo trạng thái (Success, Pending, Failed)
- Xem chi tiết đơn hàng
- Cập nhật trạng thái đơn hàng
- Xuất dữ liệu Excel (placeholder)

**Stats Displayed:**

- Tổng giao dịch
- Tổng doanh thu
- Đơn hàng thành công
- Đơn hàng đang xử lý

## Teacher Features

### 1. Dashboard Giảng viên (`/dashboard/teacher`)

- Thống kê học viên, khóa học, doanh thu
- Quick actions (Tạo khóa học, Thêm bài học)
- Danh sách học viên mới gần đây
- Khóa học hàng đầu
- Đánh giá từ học viên

**Quick Actions:**

- Tạo khóa học mới
- Thêm bài học
- Xem báo cáo

### 2. Quản lý Khóa học (`/dashboard/teacher/courses`)

- Liệt kê khóa học của giáo viên
- Hiển thị thumbnail, mô tả, giá
- Thống kê học viên, bài học
- Lọc theo trạng thái (Published, Draft)
- Tìm kiếm khóa học
- Xem chi tiết, sửa, xóa khóa học
- Quản lý nội dung (Curriculum)
- Quản lý bài tập (Exercise Management)

**Action Buttons:**

- Sửa khóa học
- Quản lý bài học
- Xóa khóa học

### 3. Tạo/Sửa Khóa học (`/dashboard/teacher/courses/create` và `/edit/:id`)

- Nhập tiêu đề, mô tả
- Chọn danh mục, cấp độ
- Nhập giá
- Upload ảnh bìa
- Chọn ngôn ngữ
- Nhập thời lượng
- Thêm kỹ năng
- Chọn trạng thái (Draft/Published)
- Form validation

**Validation Rules:**

- Tiêu đề không được trống
- Mô tả không được trống
- Giá phải > 0
- Ảnh bìa tối đa 2MB, 1280x720px

### 4. Curriculum Editor (`/dashboard/teacher/courses/:id/curriculum`)

- Quản lý chương (Chapters)
- Quản lý bài học (Lessons) trong chương
- Tree view giao diện
- Thêm/sửa/xóa chương
- Thêm/sửa/xóa bài học
- Editor nội dung bài học (text, video/audio)
- Tabs: Nội dung, Media (Video/Audio)

**Features:**

- Expandable chapters
- Lesson selection
- Content editor with rich formatting
- Media URL input

### 5. Quản lý Bài tập (`/dashboard/teacher/courses/:id/exercises`)

- Tạo bài tập cho bài học
- Hỗ trợ 2 loại câu hỏi:
  - Multiple Choice (Trắc nghiệm)
  - Fill in the Blank (Điền từ)
- Thiết lập đáp án đúng
- Cấu hình điểm số
- Xem danh sách bài tập
- Sửa, xóa bài tập

**Exercise Types:**

1. **Multiple Choice**
   - Nhập câu hỏi
   - Nhập 4 lựa chọn
   - Chọn đáp án đúng
   - Cấu hình điểm

2. **Fill in the Blank**
   - Nhập câu hỏi với khoảng trống
   - Nhập đáp án đúng
   - Cấu hình điểm

### 6. Quản lý Học viên (`/dashboard/teacher/students`)

- Liệt kê học viên đã đăng ký
- Tìm kiếm theo tên, email
- Lọc theo khóa học
- Hiển thị tiến độ (%)
- Hiển thị điểm số
- Xem chi tiết tiến độ học viên
- Gửi tin nhắn/thông báo

**Student Progress:**

- Tổng tiến độ khóa học (%)
- Điểm trung bình
- Danh sách bài học hoàn thành
- Thời gian học
- Điểm từng bài

## Cấu trúc Thư mục

```
src/
├── components/dashboard/          # Reusable components
│   ├── DashboardLayout.vue        # Main layout
│   ├── DashboardSidebar.vue       # Sidebar navigation
│   ├── DashboardHeader.vue        # Header with user info
│   ├── SidebarSection.vue         # Sidebar section
│   ├── SidebarLink.vue            # Sidebar link
│   ├── DataTable.vue              # Reusable table
│   ├── StatCard.vue               # Statistics card
│   ├── Modal.vue                  # Modal dialog
│   ├── Button.vue                 # Reusable button
│   ├── FormInput.vue              # Form input
│   └── icons/                      # SVG icons
│       ├── DashboardIcon.vue
│       ├── UsersIcon.vue
│       ├── BookOpenIcon.vue
│       ├── ShoppingCartIcon.vue
│       ├── SearchIcon.vue
│       └── ... more icons
├── views/dashboard/
│   ├── admin/
│   │   ├── DashboardView.vue      # Admin dashboard
│   │   ├── UserManagementView.vue # User management
│   │   └── OrderManagementView.vue# Order management
│   └── teacher/
│       ├── DashboardView.vue      # Teacher dashboard
│       ├── CourseManagementView.vue # Course list
│       ├── CreateCourseView.vue   # Create/edit course
│       ├── EditCourseView.vue     # Edit course (optional)
│       ├── CurriculumEditorView.vue # Chapter/lesson editor
│       ├── ExerciseManagementView.vue # Exercise editor
│       └── StudentManagementView.vue  # Student list
├── stores/
│   ├── auth.ts                    # Auth store (updated)
│   └── toast.ts                   # Toast notification store
└── router/
    └── index.js                   # Router (updated)
```

## Usage

### Accessing Dashboard

1. **Login** (/login)
   - Admin users will see admin dashboard menu
   - Teacher users will see teacher dashboard menu
   - Student users won't see dashboard menu

2. **Role-based Routing**
   - Routes are protected by `meta.requiresAuth` and `meta.roles`
   - Unauthorized access redirects to home page

3. **Admin Access**

   ```
   /dashboard/admin                 # Admin Dashboard
   /dashboard/admin/users           # User Management
   /dashboard/admin/orders          # Order Management
   ```

4. **Teacher Access**
   ```
   /dashboard/teacher               # Teacher Dashboard
   /dashboard/teacher/courses       # Course Management
   /dashboard/teacher/courses/create # Create Course
   /dashboard/teacher/courses/:id/edit # Edit Course
   /dashboard/teacher/courses/:id/curriculum # Curriculum Editor
   /dashboard/teacher/courses/:id/exercises # Exercise Management
   /dashboard/teacher/students      # Student Management
   ```

## API Integration

### Backend API Endpoints Required

#### Authentication

- `POST /auth/login` - User login
- `POST /auth/register` - User registration

#### Admin APIs

- `GET /admin/users` - List users (with pagination, filtering)
- `POST /admin/users` - Create user
- `PUT /admin/users/:id` - Update user
- `DELETE /admin/users/:id` - Delete user
- `GET /admin/orders` - List orders (with filtering)
- `PUT /admin/orders/:id/status` - Update order status
- `GET /admin/dashboard/stats` - Dashboard statistics

#### Teacher APIs

- `GET /teacher/courses` - List teacher's courses
- `POST /teacher/courses` - Create course
- `PUT /teacher/courses/:id` - Update course
- `DELETE /teacher/courses/:id` - Delete course
- `GET /teacher/courses/:id/chapters` - Get course chapters
- `POST /teacher/courses/:id/chapters` - Create chapter
- `PUT /teacher/courses/:id/chapters/:chapterId` - Update chapter
- `DELETE /teacher/courses/:id/chapters/:chapterId` - Delete chapter
- `GET /teacher/courses/:id/lessons` - Get lessons
- `POST /teacher/courses/:id/lessons` - Create lesson
- `PUT /teacher/courses/:id/lessons/:lessonId` - Update lesson
- `DELETE /teacher/courses/:id/lessons/:lessonId` - Delete lesson
- `GET /teacher/courses/:id/exercises` - Get exercises
- `POST /teacher/courses/:id/exercises` - Create exercise
- `PUT /teacher/courses/:id/exercises/:exerciseId` - Update exercise
- `DELETE /teacher/courses/:id/exercises/:exerciseId` - Delete exercise
- `GET /teacher/students` - List enrolled students
- `GET /teacher/dashboard/stats` - Dashboard statistics

### API Call Example

```typescript
import api from '@/api/axios'

// Get users
const response = await api.get('/admin/users', {
  params: {
    page: 1,
    limit: 10,
    role: 'STUDENT',
    status: 'active',
  },
})

// Create course
const courseResponse = await api.post('/teacher/courses', {
  title: 'English for Beginners',
  description: 'Basic English course',
  price: 2500000,
  level: 'A1',
  category: 'beginner',
})
```

## Styling

### Color Scheme

- **Primary**: Blue (#3B82F6)
- **Secondary**: Indigo (#6366F1)
- **Success**: Green (#10B981)
- **Warning**: Yellow (#F59E0B)
- **Error**: Red (#EF4444)
- **Background**: Dark Gray (#111827)

### Dark Mode

- Base: #111827 (bg-gray-950)
- Card: #1F2937 (bg-gray-800)
- Border: #374151 (border-gray-700)
- Text: #F3F4F6 (text-white/gray-300)

## Components Documentation

### DashboardLayout

Wrapper component for all dashboard pages. Includes sidebar, header, and toast notifications.

```vue
<DashboardLayout>
  <!-- Page content -->
</DashboardLayout>
```

### StatCard

Displays statistics with icon and trend.

```vue
<StatCard
  label="Total Revenue"
  value="₫125,000,000"
  :icon="DollarSignIcon"
  icon-color="text-green-400"
  :change="15"
/>
```

### DataTable

Reusable data table with sorting and actions.

```vue
<DataTable
  :columns="[
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
  ]"
  :rows="users"
  :actions="[
    {
      label: 'Edit',
      icon: EditIcon,
      handler: editUser,
      color: 'blue',
    },
  ]"
/>
```

### Modal

Modal dialog component.

```vue
<Modal
  v-model="showModal"
  title="Confirm Delete"
  confirm-text="Delete"
  confirm-color="red"
  @confirm="deleteUser"
>
  Are you sure?
</Modal>
```

### Button

Reusable button component.

```vue
<Button variant="primary" size="lg" :icon="PlusIcon" @click="handleClick">
  Create New
</Button>
```

### FormInput

Form input component with validation.

```vue
<FormInput
  v-model="formData.email"
  label="Email"
  type="email"
  placeholder="example@test.com"
  required
  :error="emailError"
  hint="Enter a valid email"
/>
```

## Future Enhancements

- [ ] Chart integration (Chart.js or similar)
- [ ] Real-time notifications
- [ ] File upload with progress
- [ ] Export to PDF/Excel
- [ ] Advanced filtering and search
- [ ] User activity logs
- [ ] Course analytics and insights
- [ ] Student feedback system
- [ ] Bulk operations (edit, delete)
- [ ] Two-factor authentication
- [ ] Activity timeline

## notes

- All routes are protected with role-based access control
- Toast notifications are handled via Pinia store
- Sidebar automatically adapts based on user role
- All forms include validation with error messages
- Responsive design works on desktop, tablet, and mobile
- Dark mode is the primary theme
- Icons are SVG components from Heroicons
