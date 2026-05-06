# Dashboard Setup & Configuration Guide

## 🚀 Bắt Đầu Nhanh (Quick Start)

### 1. Đảm bảo Dependencies Đã Cài Đặt

```bash
npm install
```

### 2. Các Package Cần Thiết

Dashboard system yêu cầu các package sau (đã có trong project):

- Vue 3
- Vue Router 5
- Pinia 3
- TypeScript
- Tailwind CSS 4
- Axios

### 3. Chạy Development Server

```bash
npm run dev
```

Truy cập dashboard tại:

- Admin: `http://localhost:5173/dashboard/admin`
- Teacher: `http://localhost:5173/dashboard/teacher`

## 📁 Cấu Trúc File

```
src/
├── components/dashboard/
│   ├── DashboardLayout.vue         # Main layout wrapper
│   ├── DashboardSidebar.vue        # Dynamic sidebar
│   ├── DashboardHeader.vue         # Header component
│   ├── SidebarSection.vue
│   ├── SidebarLink.vue
│   ├── DataTable.vue               # Reusable table
│   ├── StatCard.vue                # Statistics card
│   ├── Modal.vue                   # Dialog/Modal
│   ├── Button.vue                  # Button component
│   ├── FormInput.vue               # Input component
│   └── icons/                      # SVG icon files (25+ icons)
│
├── stores/
│   ├── auth.ts                     # Updated with Admin role
│   └── toast.ts                    # NEW - Toast notifications
│
├── views/dashboard/
│   ├── README.md                   # Dashboard documentation
│   ├── admin/
│   │   ├── DashboardView.vue
│   │   ├── UserManagementView.vue
│   │   └── OrderManagementView.vue
│   └── teacher/
│       ├── DashboardView.vue
│       ├── CourseManagementView.vue
│       ├── CreateCourseView.vue
│       ├── EditCourseView.vue
│       ├── CurriculumEditorView.vue
│       ├── ExerciseManagementView.vue
│       └── StudentManagementView.vue
│
└── router/
    └── index.js                    # Updated with role guards
```

## 🔐 Authentication & Role System

### User Roles

```typescript
type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT'
```

### Role-Based Access Control

```typescript
// In router/index.js
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
    } else if (to.meta.roles && !to.meta.roles.includes(authStore.user?.role)) {
      next('/')
    }
  }
  next()
})
```

### Check Role in Components

```vue
<script setup>
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Check specific role
if (authStore.isAdmin) {
  /* ... */
}
if (authStore.isTeacher) {
  /* ... */
}
if (authStore.isStudent) {
  /* ... */
}

// Check any role
if (authStore.hasRole('ADMIN')) {
  /* ... */
}
</script>
```

## 🎨 Component Usage Examples

### 1. Dashboard Layout

```vue
<template>
  <DashboardLayout>
    <!-- Your dashboard content -->
  </DashboardLayout>
</template>

<script setup>
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
</script>
```

### 2. Statistics Card

```vue
<StatCard
  label="Total Revenue"
  value="₫125,000,000"
  :icon="DollarSignIcon"
  icon-color="text-green-400"
  :change="15"
/>
```

### 3. Data Table

```vue
<DataTable
  :columns="[
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'status', label: 'Status', type: 'badge' },
  ]"
  :rows="users"
  :actions="[
    {
      label: 'Edit',
      icon: EditIcon,
      handler: (row) => editUser(row),
      color: 'blue',
    },
    {
      label: 'Delete',
      icon: TrashIcon,
      handler: (row) => deleteUser(row),
      color: 'red',
    },
  ]"
/>
```

### 4. Modal Dialog

```vue
<Modal
  v-model="showModal"
  title="Confirm Delete"
  confirm-text="Delete"
  confirm-color="red"
  @confirm="handleDelete"
>
  <p>Are you sure you want to delete this item?</p>
</Modal>
```

### 5. Form Input

```vue
<FormInput
  v-model="formData.email"
  label="Email Address"
  type="email"
  placeholder="user@example.com"
  required
  :error="errors.email"
  hint="We'll never share your email"
/>
```

### 6. Button

```vue
<!-- Primary Button -->
<Button variant="primary" @click="create">Create</Button>

<!-- Secondary Button -->
<Button variant="secondary">Cancel</Button>

<!-- Danger Button -->
<Button variant="danger">Delete</Button>

<!-- Button with Icon -->
<Button :icon="PlusIcon">Add New</Button>

<!-- Different Sizes -->
<Button size="sm">Small</Button>
<Button size="md">Medium</Button>
<Button size="lg">Large</Button>
```

## 📢 Toast Notifications

### Usage

```typescript
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()

// Success
toastStore.success('Operation completed!')

// Error
toastStore.error('An error occurred')

// Warning
toastStore.warning('Please be careful')

// Info
toastStore.info('Information message')

// Custom duration (milliseconds)
toastStore.success('Custom duration', 6000)
```

## 🔄 State Management (Pinia)

### Auth Store

```typescript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Getters
authStore.isAuthenticated // boolean
authStore.getCurrentUser // User object
authStore.isAdmin // boolean
authStore.isTeacher // boolean
authStore.isStudent // boolean
authStore.hasRole('ADMIN') // boolean

// Actions
await authStore.login({ email, password })
await authStore.register(userData)
authStore.setAuthData(token, refreshToken, user)
authStore.logout()
authStore.clearError()
```

### Toast Store

```typescript
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()

// State
toastStore.toasts // Array of toast messages

// Actions
toastStore.addToast(message, type, duration)
toastStore.removeToast(id)
toastStore.success(message, duration)
toastStore.error(message, duration)
toastStore.warning(message, duration)
toastStore.info(message, duration)
```

## 🎯 Routes

### Admin Routes

```
GET  /dashboard/admin                    # Dashboard overview
GET  /dashboard/admin/users              # User management
GET  /dashboard/admin/orders             # Order management
```

### Teacher Routes

```
GET  /dashboard/teacher                  # Dashboard overview
GET  /dashboard/teacher/courses          # Course list
GET  /dashboard/teacher/courses/create   # Create course
GET  /dashboard/teacher/courses/:id/edit # Edit course
GET  /dashboard/teacher/courses/:id/curriculum # Edit curriculum
GET  /dashboard/teacher/courses/:id/exercises  # Manage exercises
GET  /dashboard/teacher/students        # Student list
```

## 🔌 API Integration

### Setup Axios

The project uses `@/api/axios` for API calls. Ensure your backend is running:

```typescript
import api from '@/api/axios'

// Example: Get users
const response = await api.get('/admin/users', {
  params: {
    page: 1,
    limit: 10,
    role: 'STUDENT',
    status: 'active',
  },
})

// Example: Create course
const course = await api.post('/teacher/courses', {
  title: 'English for Beginners',
  description: 'Basic English course',
  price: 2500000,
  level: 'A1',
})

// Example: Update order status
await api.put('/admin/orders/ORD001/status', {
  status: 'success',
})
```

## 🎨 Customization

### Colors

Modify in component classes:

- Primary: `blue-600`, `from-blue-600 to-indigo-600`
- Success: `green-400`, `green-900/30`
- Error: `red-600`, `red-900/30`
- Warning: `yellow-600`, `yellow-900/30`

### Fonts

Update in `assets/main.css` or Tailwind config:

```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
```

### Dark Mode

Currently using:

- `bg-gray-950` - Main background
- `bg-gray-800` - Card/Panel background
- `border-gray-700` - Border color
- `text-white/gray-300` - Text color

## 🚨 Common Issues & Solutions

### Issue: Icons not showing

**Solution:** Ensure all icon components are imported correctly

```vue
import DashboardIcon from '@/components/dashboard/icons/DashboardIcon.vue'
```

### Issue: Role-based routes not working

**Solution:** Check auth store has user data with role property

```typescript
// In auth store setup
user: User | null = null  // Must have role: 'ADMIN' | 'TEACHER' | 'STUDENT'
```

### Issue: Toast notifications not appearing

**Solution:** Ensure DashboardLayout is wrapped around your content

```vue
<DashboardLayout>
  <!-- Content will show toasts -->
</DashboardLayout>
```

### Issue: Sidebar not updating on role change

**Solution:** Ensure useAuthStore is imported in DashboardSidebar

```typescript
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
```

## 📝 Best Practices

1. **Always wrap dashboard pages with DashboardLayout**
2. **Use FormInput for all form fields** - ensures consistent styling
3. **Use Button component** - maintains design consistency
4. **Use DataTable for lists** - reusable and sortable
5. **Show toasts for user feedback** - don't rely on console errors
6. **Validate form data before submission** - improves UX
7. **Use computed properties for filtered data** - reactive updates
8. **Handle errors gracefully** - catch and show toast messages

## 🔧 Development Workflow

1. **Create new dashboard page:**
   - Create Vue file in `src/views/dashboard/[admin|teacher]/`
   - Wrap with `DashboardLayout`
   - Add route in `src/router/index.js`

2. **Add new components:**
   - Create in `src/components/dashboard/`
   - Export from component
   - Import where needed

3. **Add new icons:**
   - Create SVG component in `src/components/dashboard/icons/`
   - Import and use in components

4. **Test role-based access:**
   - Login with different role users
   - Verify correct menu items show
   - Verify unauthorized routes redirect

## 📚 Resources

- [Vue 3 Docs](https://vuejs.org/)
- [Pinia Docs](https://pinia.vuejs.org/)
- [Vue Router Docs](https://router.vuejs.org/)
- [Tailwind CSS Docs](https://tailwindcss.com/)
- [Heroicons (for icons)](https://heroicons.com/)

## ✅ Deployment Checklist

- [ ] Replace mock data with real API calls
- [ ] Update backend API endpoints
- [ ] Test all routes with different user roles
- [ ] Verify dark mode across all browsers
- [ ] Test responsive design on mobile/tablet
- [ ] Set up proper error logging
- [ ] Implement auth token refresh
- [ ] Configure CORS for backend
- [ ] Test file uploads for course thumbnails
- [ ] Add analytics tracking
- [ ] Set up error boundaries
- [ ] Add loading states for API calls

## 📞 Support

For issues or questions:

1. Check the components/views README
2. Review example implementations
3. Check browser console for errors
4. Verify API endpoints are running
5. Check auth store has correct user data
