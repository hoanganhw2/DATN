<template>
  <section class="py-16 sm:py-20 px-4 sm:px-6 lg:px-8 bg-white">
    <div class="max-w-7xl mx-auto">
      <h2 class="text-3xl sm:text-4xl font-bold text-gray-900 text-center mb-4">
        Những con số ấn tượng
      </h2>
      <p class="text-center text-gray-600 mb-12 max-w-2xl mx-auto">
        Hơn 5000 học viên đã tin tưởng EnglishPro để nâng cao kỹ năng tiếng Anh của mình
      </p>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Stat 1: Courses -->
        <div
          class="text-center p-8 rounded-xl bg-linear-to-br from-blue-50 to-blue-100 hover:shadow-lg transition-shadow"
        >
          <div class="text-5xl sm:text-6xl font-bold text-blue-600 mb-2">100+</div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Khóa Học</h3>
          <p class="text-gray-600">
            Hàng trăm khóa học chất lượng cao từ các giảng viên kinh nghiệm
          </p>
        </div>

        <!-- Stat 2: Students -->
        <div
          class="text-center p-8 rounded-xl bg-linear-to-br from-green-50 to-green-100 hover:shadow-lg transition-shadow"
        >
          <div class="text-5xl sm:text-6xl font-bold text-green-600 mb-2">5000+</div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Học Viên</h3>
          <p class="text-gray-600">Hàng ngàn học viên từ khắp nơi đang học và phát triển kỹ năng</p>
        </div>

        <!-- Stat 3: Teachers -->
        <div
          class="text-center p-8 rounded-xl bg-linear-to-br from-blue-50 to-blue-100 hover:shadow-lg transition-shadow"
        >
          <div class="text-5xl sm:text-6xl font-bold text-blue-600 mb-2">50+</div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Giáo Viên</h3>
          <p class="text-gray-600">Đội ngũ giáo viên chuyên nghiệp và tận tâm</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
// Stats section component - no state needed
</script>
