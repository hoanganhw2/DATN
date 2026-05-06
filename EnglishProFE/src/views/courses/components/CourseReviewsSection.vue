<template>
  <div class="bg-white rounded-xl p-6 border border-gray-200 shadow-sm">
    <h3 class="text-xl font-bold text-gray-900 mb-2">Đánh giá từ học viên</h3>
    <p class="text-sm text-gray-500 mb-6">Ý kiến của những người đã học khóa này</p>

    <!-- Form: chỉ học viên đã đăng ký, không phải giảng viên chủ khóa -->
    <div
      v-if="showReviewForm"
      class="mb-8 p-4 rounded-xl border border-blue-100 bg-blue-50/40"
    >
      <h4 class="font-semibold text-gray-900 mb-3">
        {{ myReview ? 'Cập nhật đánh giá của bạn' : 'Viết đánh giá' }}
      </h4>
      <div class="flex items-center gap-1 mb-3" role="group" aria-label="Chọn số sao">
        <button
          v-for="s in 5"
          :key="s"
          type="button"
          class="text-2xl transition-transform hover:scale-110 focus:outline-none focus:ring-2 focus:ring-blue-400 rounded"
          :class="s <= formStars ? 'text-amber-400' : 'text-gray-300'"
          :aria-pressed="s <= formStars"
          @click="formStars = s"
        >
          ★
        </button>
        <span class="ml-2 text-sm text-gray-600">{{ formStars }}/5</span>
      </div>
      <textarea
        v-model="formComment"
        rows="3"
        maxlength="2000"
        placeholder="Chia sẻ trải nghiệm của bạn (không bắt buộc)"
        class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm text-gray-900 placeholder:text-gray-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 mb-3"
      />
      <div class="flex flex-wrap gap-2">
        <BaseButton variant="primary" size="md" :disabled="submitting" @click="submitReview">
          {{ submitting ? 'Đang lưu…' : myReview ? 'Cập nhật' : 'Gửi đánh giá' }}
        </BaseButton>
        <BaseButton
          v-if="myReview"
          variant="outline"
          size="md"
          :disabled="submitting"
          @click="confirmDelete"
        >
          Xóa đánh giá
        </BaseButton>
      </div>
    </div>

    <div v-else-if="authStore.isAuthenticated && isCourseOwner" class="mb-6 text-sm text-gray-500 italic">
      Giảng viên không thể đánh giá khóa học của chính mình.
    </div>

    <!-- Danh sách -->
    <div v-if="reviewsLoading && reviews.length === 0" class="space-y-3 animate-pulse">
      <div v-for="i in 3" :key="i" class="h-20 bg-gray-100 rounded-lg" />
    </div>

    <div v-else-if="reviews.length === 0" class="text-center py-8 text-gray-500 text-sm">
      Chưa có đánh giá nào. Hãy là người đầu tiên!
    </div>

    <ul v-else class="space-y-4">
      <li
        v-for="r in reviews"
        :key="r.id"
        class="border border-gray-100 rounded-lg p-4 bg-gray-50/50"
      >
        <div class="flex items-start justify-between gap-3 mb-2">
          <div>
            <p class="font-semibold text-gray-900">{{ r.userFullName || 'Học viên' }}</p>
            <p class="text-xs text-gray-400">{{ formatDate(r.createdAt) }}</p>
          </div>
          <div class="flex text-amber-400 text-sm shrink-0" aria-hidden="true">
            <span v-for="i in 5" :key="i">{{ i <= r.stars ? '★' : '☆' }}</span>
          </div>
        </div>
        <p v-if="r.comment" class="text-sm text-gray-700 whitespace-pre-wrap">{{ r.comment }}</p>
      </li>
    </ul>

    <div v-if="reviewPage && reviewPage.totalPages > 1" class="mt-6 flex justify-center">
      <button
        type="button"
        class="text-sm font-medium text-blue-600 hover:text-blue-800 disabled:opacity-50"
        :disabled="reviewsLoading || reviewPage.number >= reviewPage.totalPages - 1"
        @click="loadMore"
      >
        {{ reviewsLoading ? 'Đang tải…' : 'Xem thêm đánh giá' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toast'
import {
  listCourseReviews,
  getMyCourseReview,
  upsertCourseReview,
  deleteMyCourseReview,
  type CourseReview,
} from '@/api/courseReviews'

const props = defineProps<{
  courseId: number
  teacherId?: number
  isEnrolled: boolean
}>()

const emit = defineEmits<{
  statsUpdated: []
}>()

const authStore = useAuthStore()
const toastStore = useToastStore()

const reviews = ref<CourseReview[]>([])
const reviewPage = ref<{ number: number; totalPages: number; size: number } | null>(null)
const reviewsLoading = ref(false)
const myReview = ref<CourseReview | null>(null)
const formStars = ref(5)
const formComment = ref('')
const submitting = ref(false)

const uid = computed(() => (authStore.user?.id ? Number(authStore.user.id) : null))

const isCourseOwner = computed(() => {
  if (!uid.value || !props.teacherId) return false
  return uid.value === props.teacherId
})

const showReviewForm = computed(
  () => authStore.isAuthenticated && props.isEnrolled && !isCourseOwner.value,
)

async function fetchReviews(reset: boolean) {
  reviewsLoading.value = true
  try {
    const pageIdx = reset ? 0 : (reviewPage.value?.number ?? 0) + 1
    const page = await listCourseReviews(props.courseId, pageIdx, 10)
    if (reset) {
      reviews.value = [...page.content]
    } else {
      reviews.value = [...reviews.value, ...page.content]
    }
    reviewPage.value = {
      number: page.number,
      totalPages: page.totalPages,
      size: page.size,
    }
  } catch (e) {
    console.error(e)
    toastStore.error('Không tải được danh sách đánh giá')
  } finally {
    reviewsLoading.value = false
  }
}

async function fetchMyReview() {
  if (!authStore.isAuthenticated || !showReviewForm.value) {
    myReview.value = null
    return
  }
  try {
    const data = await getMyCourseReview(props.courseId)
    myReview.value = data
    if (data) {
      formStars.value = data.stars
      formComment.value = data.comment || ''
    } else {
      formStars.value = 5
      formComment.value = ''
    }
  } catch {
    myReview.value = null
  }
}

function loadMore() {
  if (!reviewPage.value || reviewPage.value.number >= reviewPage.value.totalPages - 1) return
  fetchReviews(false)
}

async function submitReview() {
  submitting.value = true
  try {
    await upsertCourseReview(props.courseId, {
      stars: formStars.value,
      comment: formComment.value.trim() || undefined,
    })
    toastStore.success('Đã lưu đánh giá')
    await fetchMyReview()
    await fetchReviews(true)
    emit('statsUpdated')
  } catch (err: any) {
    const msg =
      err.response?.data?.message ||
      err.response?.data?.data?.message ||
      'Không thể gửi đánh giá (cần đăng ký khóa học)'
    toastStore.error(typeof msg === 'string' ? msg : 'Không thể gửi đánh giá')
  } finally {
    submitting.value = false
  }
}

async function confirmDelete() {
  if (!confirm('Xóa đánh giá của bạn?')) return
  submitting.value = true
  try {
    await deleteMyCourseReview(props.courseId)
    toastStore.success('Đã xóa đánh giá')
    myReview.value = null
    formStars.value = 5
    formComment.value = ''
    await fetchReviews(true)
    emit('statsUpdated')
  } catch (err: any) {
    toastStore.error(err.response?.data?.message || 'Không thể xóa')
  } finally {
    submitting.value = false
  }
}

function formatDate(iso: string) {
  try {
    return new Date(iso).toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return iso
  }
}

watch(
  () => props.courseId,
  () => {
    fetchReviews(true)
    fetchMyReview()
  },
)

onMounted(() => {
  fetchReviews(true)
  fetchMyReview()
})

watch(showReviewForm, (show) => {
  if (show) fetchMyReview()
})
</script>
