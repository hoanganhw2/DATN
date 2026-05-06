# CourseListView - Trang Danh sách Khóa học

## 📋 Tổng quan

Trang danh sách khóa học hoàn chỉnh với các tính năng lọc nâng cao, tìm kiếm, sắp xếp và phân trang.

## 📁 Cấu trúc File

```
src/views/courses/
├── CourseListView.vue          # Component chính - Danh sách khóa học
├── CourseDetailView.vue        # Placeholder - Chi tiết khóa học
└── components/
    ├── FilterSidebar.vue       # Sidebar filter (Desktop)
    ├── FilterModal.vue         # Modal filter (Mobile)
    └── Pagination.vue          # Component phân trang

src/api/
└── courses.ts                  # API service & type definitions
```

## 🎨 Layout & Responsive

### Desktop (≥1024px)

- **Trái (25%)**: Sidebar filters (sticky)
- **Phải (75%)**: Danh sách khóa học + Pagination

### Mobile & Tablet

- **Filter**: Hiển thị dưới dạng Modal/Drawer
- **Khóa học**: Full width grid (1-2 cột)

## ✨ Tính năng

### 1. **Header Search**

- Banner xanh dương gradient
- Search bar lớn với icon search
- Enter để tìm kiếm

### 2. **Sidebar Filter** (Desktop)

- ✅ Phân loại: TOEIC, IELTS, Giao tiếp, Ngữ pháp
- ✅ Trình độ: Sơ cấp, Trung cấp, Nâng cao (Checkbox)
- ✅ Học phí: Miễn phí, Có phí (Radio)
- ✅ Nút "Xóa bộ lọc" để reset

### 3. **Main Content**

- **Toolbar**: Hiển thị số kết quả + Dropdown sắp xếp
- **Course Grid**: 3 cột desktop, responsive
- **Loading**: SkeletonCard (6-8 skeleton)
- **Empty State**: Hình minh họa + thông báo

### 4. **Sắp xếp**

- Mới nhất (mặc định)
- Giá thấp → cao
- Giá cao → thấp
- Xếp hạng

### 5. **Phân trang**

- Nút Previous/Next
- Hiển thị 5 trang cùng lúc
- Trang hiện tại màu Primary (blue)

## 🔧 Logic kỹ thuật

### Reactive State

```typescript
- courses: Course[]             // Danh sách khóa học
- totalCourses: number          // Tổng số kết quả
- totalPages: number            // Tổng số trang
- currentPage: number           // Trang hiện tại
- isLoading: boolean            // Trạng thái loading
- filterParams: Partial<CourseFilterParams>  // Bộ lọc
```

### API Call

```typescript
GET /api/v1/courses?page=1&size=12&keyword=...&level=...&category=...&sort=...
```

### URL Query Strings

- Tự động cập nhật URL khi thay đổi filter
- Cho phép copy & share link kết quả tìm kiếm
- Khôi phục state khi reload page

### Watch

- Giám sát thay đổi query params từ router
- Tự động gọi API khi URL thay đổi

## 📱 Mobile Experience

### Filter Modal

- Teleport to body
- Backdrop overlay
- Nút Apply/Close
- Smooth transition

### Responsive Grid

```css
sm: 1 cột
md: 2 cột
lg: 3 cột
```

## 🎯 Hướng sử dụng

### 1. Thêm route vào router/index.js

✅ Đã thêm

```typescript
{
  path: '/courses',
  name: 'CourseList',
  component: () => import('@/views/courses/CourseListView.vue'),
}
```

### 2. Truy cập

```
http://localhost:5175/courses
http://localhost:5175/courses?keyword=TOEIC&level=BEGINNER
```

### 3. Backend API

API endpoint cần implement:

```
GET /api/v1/courses?page=1&size=12&keyword=...&level=...&category=...&sort=...

Response:
{
  data: Course[],
  total: number,
  page: number,
  size: number,
  totalPages: number
}
```

## 🚀 Cải thiện trong tương lai

- [ ] Filter by rating
- [ ] Filter by student count
- [ ] Save favorite courses
- [ ] Sort by popularity
- [ ] Infinite scroll option
- [ ] Advanced search filters
- [ ] Filter pills/tags display

## 📝 Type Definitions

```typescript
interface Course {
  id: number
  title: string
  description?: string
  thumbnailUrl?: string
  level: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'
  category?: string
  price: number
  teacherFullName: string
  teacherId?: number
  studentCount?: number
  rating?: number
  createdAt?: string
}

interface CourseFilterParams {
  keyword?: string
  level?: string[]
  category?: string[]
  priceRange?: 'free' | 'paid' | 'all'
  page: number
  size: number
  sort?: 'newest' | 'price_asc' | 'price_desc' | 'rating'
}
```

## 🔗 Dependencies

- Vue 3 (Composition API + Script Setup)
- Vue Router
- TypeScript
- Tailwind CSS
- Axios (via @/api/axios)

## ✅ Testing Checklist

- [ ] Desktop filter works
- [ ] Mobile filter modal works
- [ ] Search input works
- [ ] Sort dropdown works
- [ ] Pagination works
- [ ] URL query strings update correctly
- [ ] Empty state displays when no results
- [ ] Loading skeletons show while fetching
- [ ] Course cards display correctly
- [ ] Responsive on all screen sizes
