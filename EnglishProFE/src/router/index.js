import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/home/Home.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/user/Login.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/user/Register.vue'),
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/user/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/courses',
      name: 'CourseList',
      component: () => import('@/views/courses/CourseListView.vue'),
    },
    {
      path: '/courses/:slug',
      name: 'CourseDetail',
      component: () => import('@/views/courses/CourseDetailView.vue'),
    },
    {
      path: '/cart',
      name: 'Cart',
      component: () => import('@/views/cart/CartView.vue'),
    },
    {
      path: '/checkout',
      name: 'Checkout',
      component: () => import('@/views/checkout/CheckoutView.vue'),
    },
    {
      path: '/payment-result',
      name: 'PaymentResult',
      component: () => import('@/views/payment/PaymentResultView.vue'),
    },
    {
      path: '/flashcard',
      name: 'FlashcardList',
      component: () => import('@/views/flashcard/FlashcardListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/flashcard/create',
      name: 'CreateFlashcard',
      component: () => import('@/views/flashcard/CreateFlashcardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/flashcard/review/:id',
      name: 'FlashcardReview',
      component: () => import('@/views/flashcard/FlashcardReviewView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/flashcard/deck/:id',
      name: 'FlashcardDeck',
      component: () => import('@/views/flashcard/DeckDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/exam',
      name: 'exam-list',
      component: () => import('@/views/exam/ExamListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/exam/:id',
      name: 'exam-taking',
      component: () => import('@/views/exam/ExamTakingView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/exam/:id/results',
      name: 'exam-results',
      component: () => import('@/views/exam/ExamResultsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/exam/:id/review/:resultId',
      name: 'exam-review',
      component: () => import('@/views/exam/ExamReviewView.vue'),
      meta: { requiresAuth: true },
    },

    // Learning Page (Student)
    {
      path: '/learning/:courseId',
      name: 'Learning',
      component: () => import('@/views/learning/LearningView.vue'),
      meta: { requiresAuth: true },
    },

    // Dashboard Routes - Admin
    {
      path: '/dashboard/admin',
      name: 'AdminDashboard',
      component: () => import('@/views/dashboard/admin/DashboardView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/users',
      name: 'UserManagement',
      component: () => import('@/views/dashboard/admin/UserManagementView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/orders',
      name: 'OrderManagement',
      component: () => import('@/views/dashboard/admin/OrderManagementView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/statistics',
      name: 'RevenueStatistics',
      component: () => import('@/views/dashboard/admin/RevenueStatisticsView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/settings',
      name: 'SystemSettings',
      component: () => import('@/views/dashboard/admin/SettingsView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/course-approvals',
      name: 'CourseApproval',
      component: () => import('@/views/dashboard/admin/CourseApprovalView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/coupons',
      name: 'CouponManagement',
      component: () => import('@/views/admin/CouponManagementView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN', 'TEACHER'] },
    },
    {
      path: '/dashboard/admin/exams',
      name: 'AdminExamManagement',
      component: () => import('@/views/dashboard/shared/ExamManagementView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/exams/create',
      name: 'AdminCreateExam',
      component: () => import('@/views/dashboard/shared/ExamBuilderView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/admin/exams/:id/edit',
      name: 'AdminEditExam',
      component: () => import('@/views/dashboard/shared/ExamBuilderView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/dashboard/teacher/coupons',
      name: 'TeacherCouponManagement',
      component: () => import('@/views/admin/CouponManagementView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    // Dashboard Routes - Teacher
    {
      path: '/dashboard/teacher',
      name: 'TeacherDashboard',
      component: () => import('@/views/dashboard/teacher/DashboardView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/courses',
      name: 'CourseManagement',
      component: () => import('@/views/dashboard/teacher/CourseManagementView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/courses/create',
      name: 'CreateCourse',
      component: () => import('@/views/dashboard/teacher/CreateCourseView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/courses/:id/edit',
      name: 'EditCourse',
      component: () => import('@/views/dashboard/teacher/EditCourseView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/courses/:id/curriculum',
      name: 'CurriculumEditor',
      component: () => import('@/views/dashboard/teacher/CurriculumEditorView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/courses/:id/exercises',
      name: 'ExerciseManagement',
      component: () => import('@/views/dashboard/teacher/ExerciseManagementView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/students',
      name: 'StudentManagement',
      component: () => import('@/views/dashboard/teacher/StudentManagementView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/revenue',
      name: 'TeacherRevenue',
      component: () => import('@/views/dashboard/teacher/TeacherRevenueView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/exams',
      name: 'TeacherExamManagement',
      component: () => import('@/views/dashboard/shared/ExamManagementView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/exams/create',
      name: 'TeacherCreateExam',
      component: () => import('@/views/dashboard/shared/ExamBuilderView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/teacher/exams/:id/edit',
      name: 'TeacherEditExam',
      component: () => import('@/views/dashboard/shared/ExamBuilderView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    // Dashboard Routes - Student
    {
      path: '/dashboard/student',
      name: 'StudentDashboard',
      component: () => import('@/views/dashboard/student/DashboardView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/dashboard/student/courses',
      name: 'StudentCourses',
      component: () => import('@/views/dashboard/student/MyCoursesView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/dashboard/student/orders',
      name: 'StudentOrders',
      component: () => import('@/views/dashboard/student/StudentOrdersView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/dashboard/student/flashcards',
      name: 'StudentFlashcards',
      component: () => import('@/views/dashboard/student/MyFlashcardsView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/dashboard/student/exams',
      name: 'StudentExams',
      component: () => import('@/views/dashboard/student/MyExamsView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/dashboard/student/certificates',
      name: 'StudentCertificates',
      component: () => import('@/views/dashboard/student/MyCertificatesView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/forbidden',
      name: 'Forbidden',
      component: () => import('@/views/error/ForbiddenView.vue'),
    },
  ],
})

// Navigation Guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }

    // Check if user has required role
    const requiredRoles = to.meta.roles
    if (requiredRoles && Array.isArray(requiredRoles)) {
      const userRole = authStore.user?.role || ''
      if (!requiredRoles.includes(userRole)) {
        // Chuyển hướng sang trang lỗi 403 nếu không có quyền
        next('/forbidden')
        return
      }
    }
  }

  next()
})

export default router
