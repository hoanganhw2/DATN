<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="flex-1">
      <!-- Main Content -->
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <!-- Search Bar Section -->
        <div class="mb-8">
          <div class="relative">
            <input
              v-model="filterParams.keyword"
              type="text"
              placeholder="Tìm kiếm khóa học..."
              @keyup.enter="handleSearch"
              class="w-full px-5 py-3 pl-5 pr-12 rounded-lg text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm border border-gray-200 hover:border-gray-300 transition-colors"
            />
            <button
              @click="handleSearch"
              class="absolute right-3 top-1/2 -translate-y-1/2 p-2 text-gray-400 hover:text-blue-600 transition-colors"
              aria-label="Tìm kiếm"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
            </button>
          </div>
        </div>

        <div class="flex gap-6">
          <!-- Sidebar Filter - Desktop (Hidden on Mobile) -->
          <aside class="hidden lg:block w-1/4">
            <FilterSidebar
              :initial-filters="{
                keyword: filterParams.keyword || '',
                level: filterParams.level || [],
                category: filterParams.category || [],
                priceRange: filterParams.priceRange || 'all',
                page: currentPage,
                size: 12,
                sort: filterParams.sort,
              }"
              @update="handleFilterUpdate"
            />
          </aside>

          <!-- Mobile Filter Button -->
          <div class="lg:hidden w-full mb-6">
            <BaseButton
              variant="outline"
              size="md"
              class="w-full flex items-center justify-center gap-2"
              @click="showMobileFilter = true"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
                />
              </svg>
              Bộ lọc
            </BaseButton>
          </div>

          <!-- Main Content Area -->
          <div class="w-full lg:w-3/4">
            <!-- Toolbar -->
            <div class="flex flex-col gap-4 mb-6 bg-white p-4 rounded-xl border border-gray-200">
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div class="text-gray-600 text-sm sm:text-base">
                  Tìm thấy <span class="font-bold text-blue-600">{{ totalCourses }}</span> khóa học
                </div>

                <!-- Sort Dropdown -->
                <div class="flex gap-3 items-center">
                  <label class="text-sm font-medium text-gray-700 whitespace-nowrap"
                    >Sắp xếp:</label
                  >
                  <select
                    v-model="filterParams.sort"
                    @change="handleSort"
                    class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent flex-1 sm:flex-auto"
                  >
                    <option value="">Mới nhất</option>
                    <option value="price_asc">Giá thấp → cao</option>
                    <option value="price_desc">Giá cao → thấp</option>
                    <option value="rating">Xếp hạng</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Loading State -->
            <div v-if="isLoading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <SkeletonCard v-for="i in 6" :key="i" />
            </div>

            <!-- Empty State -->
            <div v-else-if="!isLoading && courses.length === 0" class="text-center py-12">
              <div class="mb-4">
                <svg
                  class="w-16 h-16 mx-auto text-gray-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                  />
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">
                Không tìm thấy khóa học phù hợp
              </h3>
              <p class="text-gray-600 mb-6">Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
              <BaseButton variant="primary" @click="resetFilters"> Xóa bộ lọc </BaseButton>
            </div>

            <!-- Course Grid -->
            <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              <CourseCard
                v-for="course in courses"
                :key="course.id"
                :course="course"
                @click="navigateToCourse(course.slug)"
              />
            </div>

            <!-- Pagination -->
            <Pagination
              v-if="!isLoading && courses.length > 0"
              :current-page="currentPage"
              :total-pages="totalPages"
              @update:current-page="handlePageChange"
            />
          </div>
        </div>

        <!-- Mobile Filter Modal -->
        <FilterModal
          v-if="showMobileFilter"
          :filters="{
            keyword: filterParams.keyword || '',
            level: filterParams.level || [],
            category: filterParams.category || [],
            priceRange: filterParams.priceRange || 'all',
            page: currentPage,
            size: 12,
            sort: filterParams.sort,
          }"
          @close="showMobileFilter = false"
          @update="handleFilterUpdate"
        />
      </div>
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CourseCard from '@/components/CourseCard.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'
import BaseButton from '@/components/BaseButton.vue'
import Navbar from '@/views/home/components/Navbar.vue'
import Footer from '@/views/home/components/Footer.vue'
import FilterSidebar from './components/FilterSidebar.vue'
import FilterModal from './components/FilterModal.vue'
import Pagination from './components/Pagination.vue'
import { getCourses, type Course, type CourseFilterParams } from '@/api/courses'

// Router & Route
const router = useRouter()
const route = useRoute()

// State
const courses = ref<Course[]>([])
const totalCourses = ref(0)
const totalPages = ref(1)
const currentPage = ref(parseInt(route.query.page as string) || 1)
const isLoading = ref(false)
const showMobileFilter = ref(false)
const skipWatch = ref(false) // Flag to prevent watcher from refetching

// Filter Parameters
const filterParams = reactive<Partial<CourseFilterParams>>({
  keyword: (route.query.keyword as string) || '',
  level: (route.query.level as string)?.split(',').filter(Boolean) || [],
  category: (route.query.category as string)?.split(',').filter(Boolean) || [],
  priceRange: (route.query.priceRange as any) || 'all',
  page: parseInt(route.query.page as string) || 1,
  size: 12,
  sort: (route.query.sort as any) || undefined,
})

// Methods
const handleSearch = () => {
  currentPage.value = 1
  fetchCourses()
}

const fetchCourses = async () => {
  try {
    isLoading.value = true

    // Map FE sort format to BE sort format
    let sortValue = filterParams.sort
    if (sortValue === 'price_asc') sortValue = 'price,asc'
    else if (sortValue === 'price_desc') sortValue = 'price,desc'
    else if (sortValue === 'rating') sortValue = 'avgRating,desc'
    else if (!sortValue) sortValue = 'createdAt,desc'

    const response = await getCourses({
      keyword: filterParams.keyword,
      level: filterParams.level || [],
      category: filterParams.category || [],
      page: currentPage.value,
      size: filterParams.size || 12,
      sort: sortValue,
    })

    // response ở đây chính là PageResponse
    courses.value = response.content
    totalCourses.value = response.totalElements
    totalPages.value = response.totalPages

    // Update URL without triggering watcher
    syncQueryParams()
  } catch (error) {
    console.error('Error fetching courses:', error)
    courses.value = []
    totalCourses.value = 0
    totalPages.value = 1
  } finally {
    isLoading.value = false
  }
}

const syncQueryParams = () => {
  const query: Record<string, any> = {}

  if (currentPage.value > 1) query.page = currentPage.value
  if (filterParams.keyword) query.keyword = filterParams.keyword
  if (filterParams.level && (filterParams.level as any).length > 0)
    query.level = (filterParams.level as any).join(',')
  if (filterParams.category && (filterParams.category as any).length > 0)
    query.category = (filterParams.category as any).join(',')
  if (filterParams.sort) query.sort = filterParams.sort

  // Set flag to skip the watcher's refetch
  skipWatch.value = true
  router.replace({ path: route.path, query }).finally(() => {
    // Reset flag after navigation completes
    setTimeout(() => { skipWatch.value = false }, 50)
  })
}

const handleFilterUpdate = (newFilters: Partial<CourseFilterParams>) => {
  Object.assign(filterParams, newFilters)
  currentPage.value = 1
  showMobileFilter.value = false
  fetchCourses()
}

const handlePageChange = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
  fetchCourses()
}

const handleSort = () => {
  currentPage.value = 1
  fetchCourses()
}

const resetFilters = () => {
  filterParams.keyword = ''
  filterParams.level = []
  filterParams.category = []
  filterParams.sort = undefined
  currentPage.value = 1
  fetchCourses()
}

const navigateToCourse = (slug: string) => {
  router.push(`/courses/${slug}`)
}

// Watch for external query changes (e.g. browser back/forward)
watch(
  () => route.query,
  () => {
    if (skipWatch.value) return // Skip if we just updated the query ourselves

    filterParams.keyword = (route.query.keyword as string) || ''
    filterParams.level = (route.query.level as string)?.split(',').filter(Boolean) || []
    filterParams.category = (route.query.category as string)?.split(',').filter(Boolean) || []
    filterParams.sort = (route.query.sort as string) || undefined
    currentPage.value = parseInt(route.query.page as string) || 1
    fetchCourses()
  },
  { immediate: false },
)

// Initial load
onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
/* Smooth animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
