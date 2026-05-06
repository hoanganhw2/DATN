<template>
  <div v-if="isEmpty" class="text-gray-400" :class="countClass">Chưa có đánh giá</div>
  <div v-else class="flex items-center gap-1.5 flex-wrap" :class="wrapperClass">
    <div class="flex items-center text-amber-400" :class="starClass" aria-hidden="true">
      <span v-for="i in 5" :key="i">{{ starChar(i) }}</span>
    </div>
    <span v-if="showNumeric" class="font-semibold tabular-nums" :class="textClass">
      {{ label }}
    </span>
    <span v-if="totalReviews != null && totalReviews > 0" class="text-gray-500" :class="countClass">
      ({{ totalReviews }} đánh giá)
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    /** Điểm trung bình 0–5 */
    avg: number | undefined | null
    totalReviews?: number | null
    /** kích thước: sm | md */
    size?: 'sm' | 'md'
  }>(),
  {
    totalReviews: 0,
    size: 'md',
  },
)

const avgNum = computed(() => {
  const n = props.avg
  if (n == null || Number.isNaN(Number(n))) return 0
  return Math.min(5, Math.max(0, Number(n)))
})

const tr = computed(() => (props.totalReviews != null ? Number(props.totalReviews) : 0))

const isEmpty = computed(() => avgNum.value <= 0 && tr.value <= 0)

const showNumeric = computed(
  () => avgNum.value > 0 || tr.value > 0,
)

const label = computed(() => avgNum.value.toFixed(1))

const starClass = computed(() => (props.size === 'sm' ? 'text-sm' : 'text-base'))
const textClass = computed(() => (props.size === 'sm' ? 'text-xs text-gray-800' : 'text-sm text-gray-900'))
const countClass = computed(() => (props.size === 'sm' ? 'text-xs' : 'text-sm'))
const wrapperClass = computed(() => (props.size === 'sm' ? 'gap-1' : 'gap-2'))

/** Đơn giản: làm tròn để quyết định số sao vàng */
function starChar(index: number) {
  const rounded = Math.round(avgNum.value)
  return index <= rounded ? '★' : '☆'
}
</script>
