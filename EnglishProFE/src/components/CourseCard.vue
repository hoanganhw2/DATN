<template>
  <div
    class="h-full flex flex-col rounded-xl bg-white border border-gray-200 overflow-hidden shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
  >
    <!-- Course Thumbnail -->
    <div class="relative w-full aspect-video bg-gray-200 overflow-hidden">
      <img
        v-if="course.thumbnailUrl"
        :src="course.thumbnailUrl"
        :alt="course.title"
        class="w-full h-full object-cover hover:scale-110 transition-transform duration-300"
      />
      <div
        v-else
        class="w-full h-full bg-linear-to-br from-blue-400 via-blue-500 to-blue-700 flex flex-col items-center justify-center relative overflow-hidden"
      >
        <!-- Gradient Overlay Pattern -->
        <div
          class="absolute inset-0 opacity-20"
          style="
            background-image: radial-gradient(
              circle at 20% 50%,
              rgba(255, 255, 255, 0.3) 1px,
              transparent 1px
            );
            background-size: 20px 20px;
          "
        ></div>
        <!-- Placeholder Content -->
        <div class="relative z-10 flex flex-col items-center justify-center text-center">
          <div class="text-3xl font-bold text-white mb-2 opacity-90">EP</div>
          <p class="text-xs text-white/80 font-medium px-2">{{ course.title || 'Khóa học' }}</p>
        </div>
      </div>

      <!-- Level Badge -->
      <div class="absolute top-3 right-3">
        <BaseBadge :variant="getLevelVariant()">
          {{ getLevelText() }}
        </BaseBadge>
      </div>
    </div>

    <!-- Course Content -->
    <div class="flex-1 flex flex-col p-4 gap-3">
      <!-- Title -->
      <router-link :to="`/courses/${course.slug}`">
        <h3
          class="text-base font-bold text-gray-900 line-clamp-2 hover:text-blue-600 transition-colors"
        >
          {{ course.title }}
        </h3>
      </router-link>

      <!-- Teacher Name -->
      <p class="text-sm text-gray-600">
        <span class="font-medium">{{ displayTeacher }}</span>
      </p>

      <!-- Rating -->
      <StarRatingDisplay
        :avg="avgRatingNum"
        :total-reviews="totalReviewsNum"
        size="sm"
      />

      <!-- Price -->
      <div class="mt-auto pt-3 border-t border-gray-200">
        <div class="text-lg font-bold text-blue-600">
          {{ formatPrice(course.price) }}
        </div>
      </div>

      <!-- CTA Buttons -->
      <div class="flex gap-2 mt-2">
        <router-link :to="`/courses/${course.slug}`" class="flex-1" @click.stop>
          <BaseButton variant="outline" size="md" class="w-full"> Chi tiết </BaseButton>
        </router-link>
        <BaseButton
          variant="primary"
          size="md"
          class="flex-1"
          @click.stop="handleAddToCart"
        >
          Thêm vào giỏ
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import BaseBadge from './BaseBadge.vue'
import BaseButton from './BaseButton.vue'
import StarRatingDisplay from './StarRatingDisplay.vue'
import { useCartStore } from '@/stores/cart'
import { useToastStore } from '@/stores/toast'

interface Course {
  id?: number
  slug?: string
  title: string
  thumbnailUrl?: string
  level?: string
  price?: number
  teacherFullName?: string
  teacher?: { fullName?: string }
  avgRating?: number
  totalReviews?: number
}

interface Props {
  course: Course
}

const props = withDefaults(defineProps<Props>(), {})
const cartStore = useCartStore()
const toastStore = useToastStore()

const displayTeacher = computed(
  () => props.course.teacher?.fullName || props.course.teacherFullName || 'Unknown Teacher',
)

const avgRatingNum = computed(() => {
  const r = props.course.avgRating
  return r != null ? Number(r) : 0
})

const totalReviewsNum = computed(() => {
  const n = props.course.totalReviews
  return n != null ? Number(n) : 0
})

const handleAddToCart = () => {
  if (!props.course.id) return

  cartStore.addToCart({
    id: props.course.id,
    title: props.course.title,
    teacher: displayTeacher.value,
    price: props.course.price || 0,
    image: props.course.thumbnailUrl || '',
  })

  toastStore.success('Đã thêm khóa học vào giỏ hàng!')
}

const getLevelVariant = () => {
  const levelMap: Record<string, any> = {
    BEGINNER: 'beginner',
    INTERMEDIATE: 'intermediate',
    ADVANCED: 'advanced',
    EXPERT: 'expert',
  }
  return levelMap[props.course?.level?.toUpperCase() || 'BEGINNER'] || 'beginner'
}

const getLevelText = () => {
  const textMap: Record<string, string> = {
    BEGINNER: 'Sơ cấp',
    INTERMEDIATE: 'Trung cấp',
    ADVANCED: 'Nâng cao',
    EXPERT: 'Chuyên gia',
  }
  return textMap[props.course?.level?.toUpperCase() || 'BEGINNER'] || 'Sơ cấp'
}

const formatPrice = (price?: number) => {
  if (!price || price === 0) return 'Miễn phí'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
  }).format(price)
}
</script>
