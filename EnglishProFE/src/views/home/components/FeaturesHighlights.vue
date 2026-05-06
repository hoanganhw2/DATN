<template>
  <section id="features" class="py-16 sm:py-20 px-4 sm:px-6 lg:px-8 bg-white">
    <div class="max-w-7xl mx-auto">
      <div class="text-center mb-16">
        <h2 class="text-3xl sm:text-4xl font-bold text-gray-900 mb-4">Tính Năng Nổi Bật</h2>
        <p class="text-gray-600 max-w-2xl mx-auto">
          Những công cụ mạnh mẽ được thiết kế để giúp bạn học tiếng Anh hiệu quả nhất
        </p>
      </div>

      <!-- Features Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Feature 1: SRS Flashcard -->
        <div
          class="p-8 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg hover:scale-105 transition-all duration-300 group cursor-pointer"
        >
          <div
            class="w-16 h-16 bg-blue-100 rounded-xl flex items-center justify-center mb-4 group-hover:bg-blue-200 transition-colors duration-300"
          >
            <svg
              class="w-8 h-8 text-blue-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
              />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-gray-900 mb-2">Hệ thống SRS Flashcard</h3>
          <p class="text-gray-600">
            Sử dụng công nghệ Spaced Repetition System (SRS) để giúp bạn ghi nhớ từ vựng hiệu quả
            hơn
          </p>
        </div>

        <!-- Feature 2: TOEIC Testing -->
        <div
          class="p-8 rounded-xl border border-gray-200 hover:border-green-300 hover:shadow-lg hover:scale-105 transition-all duration-300 group cursor-pointer"
        >
          <div
            class="w-16 h-16 bg-green-100 rounded-xl flex items-center justify-center mb-4 group-hover:bg-green-200 transition-colors duration-300"
          >
            <svg
              class="w-8 h-8 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-gray-900 mb-2">Luyện thi TOEIC/IELTS</h3>
          <p class="text-gray-600">
            Đề thi mô phỏng chuẩn quốc tế với chấm điểm tự động và phân tích chi tiết
          </p>
        </div>

        <!-- Feature 3: Personalized Learning -->
        <div
          class="p-8 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg hover:scale-105 transition-all duration-300 group cursor-pointer"
        >
          <div
            class="w-16 h-16 bg-blue-100 rounded-xl flex items-center justify-center mb-4 group-hover:bg-blue-200 transition-colors duration-300"
          >
            <svg
              class="w-8 h-8 text-blue-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M13 10V3L4 14h7v7l9-11h-7z"
              />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-gray-900 mb-2">Lộ trình Học Cá nhân</h3>
          <p class="text-gray-600">
            Hệ thống thích ứng tự động điều chỉnh độ khó dựa trên tiến độ học tập của bạn
          </p>
        </div>
      </div>

      <!-- Additional Features -->
      <div class="mt-16 grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- Feature 4: Video Learning -->
        <div class="flex gap-4">
          <div class="shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-lg bg-blue-500">
              <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 mb-2">Học qua Video HD</h3>
            <p class="text-gray-600">
              Các video bài giảng chất lượng cao từ các giáo viên chuyên nghiệp
            </p>
          </div>
        </div>

        <!-- Feature 5: Progress Tracking -->
        <div class="flex gap-4">
          <div class="shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-lg bg-green-500">
              <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                />
              </svg>
            </div>
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 mb-2">Theo Dõi Tiến Độ</h3>
            <p class="text-gray-600">
              Theo dõi chi tiết tiến độ học tập với biểu đồ và thống kê chi tiết
            </p>
          </div>
        </div>

        <!-- Feature 6: Community -->
        <div class="flex gap-4">
          <div class="shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-lg bg-blue-500">
              <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 4.354a4 4 0 110 5.292M15 12H9m6 0a6 6 0 11-12 0 6 6 0 0112 0z"
                />
              </svg>
            </div>
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 mb-2">Cộng Đồng Học Tập</h3>
            <p class="text-gray-600">
              Kết nối với hàng ngàn học viên khác để trao đổi kinh nghiệm và hỗ trợ
            </p>
          </div>
        </div>

        <!-- Feature 7: Offline Mode -->
        <div class="flex gap-4">
          <div class="shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-lg bg-yellow-500">
              <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                />
              </svg>
            </div>
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 mb-2">Chế độ Offline</h3>
            <p class="text-gray-600">Tải xuống bài học để học mà không cần kết nối internet</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
// Features highlights section - no state needed
</script>
