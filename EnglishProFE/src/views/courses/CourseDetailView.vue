<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Loading State -->
    <main v-if="isLoading" class="flex-1 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      <div class="animate-pulse space-y-8">
        <div class="h-96 bg-gray-200 rounded-xl"></div>
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-4">
            <div class="h-10 bg-gray-200 w-3/4 rounded"></div>
            <div class="h-6 bg-gray-200 w-1/2 rounded"></div>
            <div class="h-32 bg-gray-200 rounded"></div>
          </div>
          <div class="lg:col-span-1 border border-gray-200 rounded-xl p-6 bg-white min-h-[400px]"></div>
        </div>
      </div>
    </main>

    <!-- Error State -->
    <main v-else-if="error" class="flex-1 flex flex-col items-center justify-center py-20 text-center">
      <div class="text-6xl mb-4">😕</div>
      <h2 class="text-2xl font-bold text-gray-900 mb-2">{{ error }}</h2>
      <BaseButton variant="primary" @click="fetchCourseDetail">Thử lại</BaseButton>
    </main>

    <!-- Main Content -->
    <main v-else-if="course" class="flex-1 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Left Content -->
        <div class="lg:col-span-2">
          <!-- Hero Image -->
          <div class="mb-8 rounded-xl overflow-hidden shadow-md">
            <img 
              :src="course.thumbnailUrl || 'https://images.unsplash.com/photo-1516321318423-f06f70fc504e?w=800&q=80'" 
              :alt="course.title" 
              class="w-full h-96 object-cover" 
            />
          </div>

          <!-- Course Title & Rating -->
          <div class="mb-8">
            <div class="flex items-start justify-between gap-4 mb-4">
              <div class="flex-1">
                <h1 class="text-4xl font-bold text-gray-900 mb-2">{{ course.title }}</h1>
                <p class="text-lg text-gray-600 line-clamp-3">{{ course.description }}</p>
              </div>
              <span class="px-4 py-2 bg-blue-100 text-blue-800 rounded-lg font-medium text-sm whitespace-nowrap">
                {{ course.level }}
              </span>
            </div>

            <!-- Rating & Stats -->
            <div class="flex flex-wrap items-center gap-6 pb-6 border-b border-gray-200">
              <StarRatingDisplay
                :avg="numericAvgRating"
                :total-reviews="course.totalReviews ?? 0"
                size="md"
              />
              <span class="text-sm text-gray-600">
                <span class="font-bold text-gray-900">{{ course.totalEnrollments || 0 }}</span> học viên đã đăng ký
              </span>
            </div>
          </div>

          <!-- Teacher Section -->
          <div class="bg-white rounded-xl p-6 mb-8 border border-gray-200 shadow-sm">
            <h3 class="text-xl font-bold text-gray-900 mb-4">Giáo viên</h3>
            <div class="flex items-center gap-4">
              <div class="w-16 h-16 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-xl overflow-hidden">
                <img v-if="course.teacher?.avatar" :src="course.teacher.avatar" class="w-full h-full object-cover" />
                <span v-else>{{ course.teacher?.fullName?.[0] || 'T' }}</span>
              </div>
              <div class="flex-1">
                <h4 class="font-bold text-gray-900 text-lg">{{ course.teacher?.fullName || 'Giảng viên EnglishPro' }}</h4>
                <p class="text-sm text-gray-600">Expert English Instructor</p>
              </div>
            </div>
          </div>

          <!-- Course Content (Curriculum) -->
          <div class="bg-white rounded-xl p-6 mb-8 border border-gray-200 shadow-sm">
            <h3 class="text-xl font-bold text-gray-900 mb-4">Nội dung khóa học</h3>
            <div class="text-sm text-gray-600 mb-4 flex gap-4">
              <span>{{ course.chapters?.length || 0 }} chương</span>
              <span>•</span>
              <span>{{ course.totalLessons || 0 }} bài học</span>
              <span>•</span>
              <span>Thờ lượng: {{ formatDuration(course.totalDurationSecs) }}</span>
            </div>

            <div class="space-y-3">
              <div v-for="(chapter, idx) in course.chapters" :key="chapter.id" class="border border-gray-200 rounded-lg overflow-hidden">
                <button
                  @click="toggleChapter(Number(idx))"
                  class="w-full px-4 py-4 flex items-center justify-between hover:bg-gray-50 transition-colors"
                >
                  <div class="flex items-center gap-3">
                    <svg class="w-5 h-5 text-gray-400 transition-transform" :class="{ 'rotate-90': expandedChapters[Number(idx)] }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                    <span class="font-semibold text-gray-900">{{ Number(idx) + 1 }}. {{ chapter.title }}</span>
                  </div>
                  <span class="text-sm text-gray-500">{{ chapter.lessons?.length || 0 }} bài</span>
                </button>

                <!-- Lessons List -->
                <div v-if="expandedChapters[Number(idx)]" class="border-t border-gray-100 bg-gray-50/50">
                  <div v-for="(lesson, lIdx) in chapter.lessons" :key="lesson.id" class="px-4 py-3 flex items-center justify-between border-b border-gray-100 last:border-0 hover:bg-blue-50/50 transition-colors">
                    <div class="flex items-center gap-3">
                      <div class="text-blue-500">
                        <svg v-if="lesson.contentType === 'VIDEO'" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" />
                        </svg>
                        <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
                        </svg>
                      </div>
                      <span class="text-gray-700 text-sm font-medium">{{ lesson.title }}</span>
                      <span v-if="lesson.isFree" class="text-[10px] bg-green-100 text-green-700 px-1.5 py-0.5 rounded font-bold uppercase tracking-wider">Miễn phí</span>
                    </div>
                    <div class="flex items-center gap-4">
                      <span class="text-xs text-gray-500">{{ formatDuration(lesson.duration) }}</span>
                      <button v-if="lesson.isFree && lesson.contentType === 'VIDEO'" @click="openVideoModal(lesson)" class="text-blue-600 hover:text-blue-800 text-xs font-bold underline">
                        Xem thử
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Reviews -->
          <CourseReviewsSection
            v-if="course.id"
            :course-id="course.id"
            :teacher-id="course.teacher?.id"
            :is-enrolled="canSubmitReview"
            @stats-updated="fetchCourseDetail"
          />
        </div>

        <!-- Sidebar (Enrollment Card) -->
        <div class="lg:col-span-1">
          <div class="sticky top-4 bg-white rounded-xl shadow-xl p-6 border border-gray-200 overflow-hidden">

            <!-- ✅ Đã mua banner -->
            <div v-if="isPurchased" class="mb-5 flex items-center gap-3 bg-green-50 border border-green-200 rounded-xl px-4 py-3">
              <svg class="w-6 h-6 text-green-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
              <div>
                <p class="font-semibold text-green-800 text-sm">Bạn đã sở hữu khóa học này</p>
                <p class="text-green-600 text-xs mt-0.5">Truy cập không giới hạn trọn đời</p>
              </div>
            </div>

            <!-- Price Section (ẩn nếu đã mua) -->
            <div v-if="!isPurchased" class="mb-6">
              <div class="flex items-center gap-3 mb-1">
                <span class="text-3xl font-bold text-gray-900">{{ formatPrice(course.price) }}</span>
              </div>
              <p v-if="course.originalPrice && course.originalPrice > course.price" class="text-sm text-gray-500 line-through">
                {{ formatPrice(course.originalPrice) }}
              </p>
            </div>

            <!-- CTA -->
            <div class="space-y-3 mb-6">
              <!-- Đã mua: chỉ hiện nút học -->
              <BaseButton
                v-if="isPurchased"
                variant="primary"
                size="lg"
                class="w-full h-12 text-lg !bg-green-600 hover:!bg-green-700"
                @click="goToLearning"
              >
                Tiếp tục học →
              </BaseButton>

              <!-- Chưa mua -->
              <template v-else>
                <BaseButton 
                  variant="primary" 
                  size="lg" 
                  class="w-full h-12 text-lg"
                  @click="enrollCourse"
                >
                  {{ isEnrolled ? 'Vào học ngay' : 'Đăng ký khóa học' }}
                </BaseButton>
                
                <BaseButton 
                  v-if="!isEnrolled"
                  variant="outline" 
                  size="lg" 
                  class="w-full h-12"
                  @click="addToCart"
                >
                  Thêm vào giỏ hàng
                </BaseButton>
              </template>
            </div>

            <!-- Course Features -->
            <div class="space-y-4 pt-6 border-t border-gray-100">
              <h4 class="font-bold text-gray-900 text-sm mb-4">Khóa học này bao gồm:</h4>
              <div class="flex items-center gap-3 text-sm text-gray-600">
                <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                </svg>
                <span>Học online qua video & bài tập</span>
              </div>
              <div class="flex items-center gap-3 text-sm text-gray-600">
                <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Sở hữu trọn đời</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <Footer />

    <!-- Video Modal -->
    <div
      v-if="selectedVideo"
      class="fixed inset-0 bg-black/90 z-50 flex items-center justify-center p-4 backdrop-blur-sm"
      @click="selectedVideo = null"
    >
      <div class="relative w-full max-w-4xl bg-black rounded-lg overflow-hidden shadow-2xl" @click.stop>
        <div class="p-4 bg-gray-900 text-white flex justify-between items-center">
          <h3 class="font-bold truncate pr-8">{{ selectedVideo.title }}</h3>
          <button @click="selectedVideo = null" class="absolute top-4 right-4 text-white hover:text-gray-300 transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="aspect-video bg-black">
          <iframe 
            v-if="selectedVideo.url.includes('youtube')"
            :src="selectedVideo.url.replace('watch?v=', 'embed/')" 
            class="w-full h-full" 
            frameborder="0" 
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
            allowfullscreen
          ></iframe>
          <video v-else :src="selectedVideo.url" controls autoplay class="w-full h-full"></video>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useToastStore } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth'
import Navbar from '@/views/home/components/Navbar.vue'
import Footer from '@/views/home/components/Footer.vue'
import BaseButton from '@/components/BaseButton.vue'
import StarRatingDisplay from '@/components/StarRatingDisplay.vue'
import CourseReviewsSection from '@/views/courses/components/CourseReviewsSection.vue'
import { getCourseDetail, type CourseDetail } from '@/api/courses'
import { checkCoursePurchased } from '@/api/orders'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const toastStore = useToastStore()
const authStore = useAuthStore()

const slug = route.params.slug as string
const course = ref<CourseDetail | null>(null)
const isLoading = ref(true)
const error = ref<string | null>(null)
const isWishlisted = ref(false)
const expandedChapters = ref<Record<number, boolean>>({})
const selectedVideo = ref<any>(null)
const isPurchased = ref(false)

const isEnrolled = computed(() => course.value?.enrollmentInfo?.isEnrolled || false)

/** Đăng ký (hoặc đã mua) — đủ điều kiện đánh giá theo backend */
const canSubmitReview = computed(() => isEnrolled.value || isPurchased.value)

const numericAvgRating = computed(() => {
  const r = course.value?.avgRating
  if (r == null || Number.isNaN(Number(r))) return 0
  return Number(r)
})

const fetchCourseDetail = async () => {
  try {
    isLoading.value = true
    error.value = null
    const data = await getCourseDetail(slug)
    course.value = data
    
    // Auto expand first chapter
    if (data.chapters && data.chapters.length > 0) {
      expandedChapters.value[0] = true
    }

    // Kiểm tra trạng thái mua nếu đã đăng nhập
    if (authStore.isAuthenticated && data.id) {
      isPurchased.value = await checkCoursePurchased(data.id)
    }
  } catch (err: any) {
    console.error('Error fetching course detail:', err)
    error.value = 'Không thể tải thông tin khóa học. Vui lòng thử lại sau.'
    toastStore.error(error.value)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchCourseDetail()
})

const addToCart = () => {
  if (!course.value) return

  if (isPurchased.value) {
    toastStore.warning('Bạn đã mua khóa học này rồi!')
    return
  }

  cartStore.addToCart({
    id: course.value.id,
    title: course.value.title,
    teacher: course.value.teacher?.fullName || 'Unknown Teacher',
    price: course.value.price,
    image: course.value.thumbnailUrl || '',
  })
  toastStore.success('Đã thêm vào giỏ hàng!')
}

const goToLearning = () => {
  if (!course.value) return
  router.push(`/learning/${course.value.id}`)
}

const enrollCourse = () => {
  if (!course.value) return

  if (isPurchased.value) {
    goToLearning()
    return
  }
  
  if (isEnrolled.value) {
    router.push(`/learning/${course.value.id}`)
  } else {
    addToCart()
    router.push('/cart')
  }
}

const toggleWishlist = () => {
  isWishlisted.value = !isWishlisted.value
  toastStore.info(isWishlisted.value ? 'Đã thêm vào danh sách yêu thích' : 'Đã xóa khỏi danh sách yêu thích')
}

const toggleChapter = (chapterIndex: number) => {
  expandedChapters.value[chapterIndex] = !expandedChapters.value[chapterIndex]
}

const openVideoModal = (lesson: any) => {
  if (lesson.contentType === 'VIDEO' && lesson.contentUrl) {
    selectedVideo.value = {
      url: lesson.contentUrl,
      title: lesson.title
    }
  } else {
    toastStore.info('Nội dung này không phải video hoặc chưa khả dụng.')
  }
}

const formatDuration = (seconds?: number) => {
  if (!seconds) return '0 phút'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes} phút`
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60
  if (remainingMinutes === 0) return `${hours} giờ`
  return `${hours} giờ ${remainingMinutes} phút`
}

const formatPrice = (price?: number) => {
  if (price === undefined || price === null) return '0đ'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
  }).format(price)
}
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
