<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-50 to-white py-12">
    <div class="max-w-4xl mx-auto px-6">
      <!-- Loading -->
      <div v-if="loading" class="flex flex-col items-center justify-center py-24">
        <div class="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-gray-500">Đang tải kết quả...</p>
      </div>

      <template v-else-if="resultData">
        <!-- Header -->
        <div class="text-center mb-12">
          <h1 class="text-4xl font-bold text-gray-900 mb-2">🎉 Hoàn thành bài thi!</h1>
          <p class="text-gray-600">{{ resultData.examTitle }}</p>
        </div>

        <!-- Score Card -->
        <div class="bg-white rounded-2xl shadow-xl p-12 mb-8 border-2 border-blue-200">
          <div class="grid grid-cols-2 gap-8 mb-8">
            <!-- Score Display -->
            <div class="text-center">
              <div
                class="inline-flex items-center justify-center w-32 h-32 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 mb-4"
              >
                <div class="text-center">
                  <div class="text-4xl font-bold text-white">{{ resultData.totalScore }}</div>
                  <div class="text-white text-sm mt-1">điểm</div>
                </div>
              </div>
              <p class="text-gray-600 font-semibold">Điểm của bạn</p>
            </div>

            <!-- Pass/Fail & Rating -->
            <div class="flex flex-col justify-center space-y-4">
              <!-- Pass / Fail badge -->
              <div v-if="resultData.isPassed != null" class="text-center">
                <span
                  :class="[
                    'px-6 py-2 rounded-full text-lg font-bold',
                    resultData.isPassed
                      ? 'bg-green-100 text-green-700'
                      : 'bg-red-100 text-red-700',
                  ]"
                >
                  {{ resultData.isPassed ? '✅ ĐẬU' : '❌ CHƯA ĐẬU' }}
                </span>
              </div>

              <!-- Section breakdown -->
              <div
                v-for="section in resultData.sectionResults"
                :key="section.sectionName"
                class="bg-blue-50 rounded-lg p-3"
              >
                <p class="text-xs text-gray-500">{{ section.sectionName }}</p>
                <p class="font-bold text-gray-900">
                  {{ section.correct }}/{{ section.total }} đúng
                  <span class="text-blue-600 ml-2">({{ section.score }} điểm)</span>
                </p>
              </div>
            </div>
          </div>

          <!-- Overall Stats -->
          <div class="grid grid-cols-3 gap-4 pt-8 border-t border-gray-200">
            <div class="text-center">
              <p class="text-3xl font-bold text-green-600">{{ totalCorrect }}</p>
              <p class="text-sm text-gray-600 mt-1">Trả lời đúng</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-red-600">{{ totalWrong }}</p>
              <p class="text-sm text-gray-600 mt-1">Trả lời sai</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-blue-600">{{ totalQuestionsCount }}</p>
              <p class="text-sm text-gray-600 mt-1">Tổng số câu</p>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex gap-4 justify-center">
          <button
            @click="goToDetails"
            class="px-8 py-3 bg-white text-blue-600 border-2 border-blue-600 font-semibold rounded-lg hover:bg-blue-50 transition-colors"
          >
            📋 Xem chi tiết
          </button>
          <button
            @click="router.push({ name: 'exam-list' })"
            class="px-8 py-3 bg-gray-200 text-gray-900 font-semibold rounded-lg hover:bg-gray-300 transition-colors"
          >
            ← Quay lại danh sách
          </button>
          <button
            @click="retakeExam"
            class="px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
          >
            Làm lại đề thi 🔄
          </button>
        </div>
      </template>

      <!-- Error State -->
      <div v-else class="text-center py-24">
        <p class="text-gray-500 text-lg mb-4">Không tìm thấy kết quả bài thi</p>
        <button
          @click="router.push({ name: 'exam-list' })"
          class="px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors"
        >
          ← Quay lại danh sách
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useExamStore } from '../../stores/exam'
import { getExamResultDetail } from '../../api/exam'

const router = useRouter()
const route = useRoute()
const examStore = useExamStore()

// State
const loading = ref(true)
const resultData = ref<any>(null)

// Computed from section results
const totalCorrect = computed(() => {
  if (!resultData.value?.sectionResults?.length) return 0
  return resultData.value.sectionResults.reduce((sum: number, s: any) => sum + (s.correct || 0), 0)
})

const totalWrong = computed(() => {
  return totalQuestionsCount.value - totalCorrect.value
})

const totalQuestionsCount = computed(() => {
  if (!resultData.value?.sectionResults?.length) return 0
  return resultData.value.sectionResults.reduce((sum: number, s: any) => sum + (s.total || 0), 0)
})

// Methods
const goToDetails = async () => {
  const resultId = resultData.value?.examResultId || route.query.resultId
  if (resultId) {
    await router.push({
      name: 'exam-review',
      params: {
        id: route.params.id,
        resultId: String(resultId),
      },
    })
  }
}

const retakeExam = async () => {
  examStore.resetExam()
  await router.push({ name: 'exam-list' })
}

/**
 * Build resultData from the SubmitExamResponse stored in the exam store.
 * This is the primary source — no extra API call needed.
 */
const buildFromStoreSubmitResult = () => {
  const submitResult = examStore.lastSubmitResult
  if (!submitResult) return false

  console.log('[ExamResults] submitResult from store:', JSON.stringify(submitResult, null, 2))

  const mappedSections = (submitResult.sectionResults || []).map((sr: any) => ({
    sectionName: sr.sectionName,
    correct: sr.correct ?? 0,
    total: sr.total ?? 0,
    score: sr.score ?? 0,
  }))

  // Fallback: if sectionResults is empty, derive stats from store questions
  if (mappedSections.length === 0 && examStore.currentExam) {
    const totalQ = examStore.allQuestions.length
    const answeredQ = Object.keys(examStore.userAnswers).length
    mappedSections.push({
      sectionName: 'Tổng',
      correct: 0, // We don't know correct count without backend — use totalScore as approximation
      total: totalQ,
      score: submitResult.totalScore ?? 0,
    })
  }

  resultData.value = {
    examResultId: submitResult.examResultId,
    examTitle: examStore.currentExam?.title || 'Đề thi',
    totalScore: submitResult.totalScore,
    isPassed: submitResult.isPassed,
    sectionResults: mappedSections,
    submittedAt: submitResult.submittedAt,
  }
  return true
}

/**
 * Build resultData from the ExamResultDetailResponse (API call).
 * sectionDetails → questionDetails with isCorrect / pointsEarned.
 */
const buildFromDetailApi = async (resultId: string) => {
  const detail = await getExamResultDetail(resultId)

  resultData.value = {
    examResultId: detail.id || resultId,
    examTitle: detail.examTitle || examStore.currentExam?.title || 'Đề thi',
    totalScore: detail.score ?? 0,
    isPassed: detail.isPassed ?? null,
    sectionResults: (detail.sections || detail.sectionDetails || []).map((s: any) => {
      const questions = s.questions || s.questionDetails || []
      return {
        sectionName: s.sectionName,
        correct: questions.filter((q: any) => q.isCorrect === true).length,
        total: questions.length,
        score: questions.reduce(
          (sum: number, q: any) => sum + (Number(q.pointsEarned) || 0),
          0,
        ),
      }
    }),
    submittedAt: detail.submittedAt,
  }
}

// Lifecycle
onMounted(async () => {
  try {
    const score = route.query.score
    const passed = route.query.passed
    const resultId = route.query.resultId

    // ── Priority 1: Use cached submit result from exam store ──
    if (buildFromStoreSubmitResult()) {
      console.log('[ExamResults] Loaded from store submit result')
      loading.value = false
      return
    }

    // ── Priority 2: Fetch from backend API by resultId ──
    if (resultId) {
      try {
        await buildFromDetailApi(String(resultId))
        console.log('[ExamResults] Loaded from detail API')
      } catch (err) {
        console.warn('[ExamResults] Detail API failed, using query fallback', err)
        // ── Priority 3: Use query params as fallback ──
        resultData.value = {
          examResultId: resultId,
          examTitle: examStore.currentExam?.title || 'Đề thi',
          totalScore: Number(score) || 0,
          isPassed: passed === 'true' ? true : passed === 'false' ? false : null,
          sectionResults: [],
        }
      }
    } else if (score != null) {
      resultData.value = {
        examResultId: null,
        examTitle: examStore.currentExam?.title || 'Đề thi',
        totalScore: Number(score) || 0,
        isPassed: passed === 'true' ? true : passed === 'false' ? false : null,
        sectionResults: [],
      }
    } else {
      resultData.value = null
    }
  } catch (error) {
    console.error('Failed to load exam results:', error)
    resultData.value = null
  } finally {
    loading.value = false
  }
})
</script>
