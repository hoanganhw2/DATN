<template>
  <section
    class="relative min-h-screen flex items-center justify-center px-4 sm:px-6 lg:px-8 py-20 bg-linear-to-br from-blue-50 via-white to-blue-50 overflow-hidden"
  >
    <!-- Two Column Layout -->
    <div class="max-w-6xl w-full grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
      <!-- Left Column: Text Content -->
      <div class="flex flex-col justify-center animate-fade-in-up">
        <!-- Main Headline -->
        <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold text-gray-900 mb-6 leading-tight">
          Làm chủ tiếng Anh,
          <span class="text-blue-600">mở cửa tương lai</span>
        </h1>

        <!-- Sub Headline -->
        <p class="text-lg sm:text-xl text-gray-600 mb-8 leading-relaxed">
          Học tập qua video, luyện tập với Flashcard SRS và thi thử TOEIC chuẩn quốc tế
        </p>

        <!-- CTA Button -->
        <div class="flex flex-col sm:flex-row gap-4 mb-12">
          <router-link to="/login">
            <BaseButton variant="primary" size="lg" class="w-full sm:w-auto px-8">
              Bắt đầu học ngay
            </BaseButton>
          </router-link>
          <button
            class="px-8 py-3 text-lg rounded-xl border-2 border-blue-600 text-blue-600 hover:bg-blue-50 font-medium transition-colors w-full sm:w-auto"
          >
            Xem demo
          </button>
        </div>

        <!-- Stats Quick View -->
        <div class="grid grid-cols-3 gap-4 sm:gap-8 pt-8 border-t border-gray-200">
          <div>
            <div class="text-2xl sm:text-3xl font-bold text-blue-600">100+</div>
            <div class="text-sm sm:text-base text-gray-600">Khóa học</div>
          </div>
          <div>
            <div class="text-2xl sm:text-3xl font-bold text-blue-600">5000+</div>
            <div class="text-sm sm:text-base text-gray-600">Học viên</div>
          </div>
          <div>
            <div class="text-2xl sm:text-3xl font-bold text-blue-600">50+</div>
            <div class="text-sm sm:text-base text-gray-600">Giáo viên</div>
          </div>
        </div>
      </div>

      <!-- Right Column: Illustration -->
      <div class="hidden lg:flex justify-center items-center">
        <div class="relative w-full max-w-md animate-fade-in-up" style="animation-delay: 0.2s">
          <!-- Image Container -->
          <img
            src="https://images.unsplash.com/photo-1552664730-d307ca884978?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8ZW58MHx8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"
            alt="Online Learning Illustration"
            class="w-full rounded-2xl shadow-2xl object-cover aspect-square"
            loading="lazy"
          />
          <!-- Gradient Overlay -->
          <div
            class="absolute inset-0 rounded-2xl bg-gradient-to-tr from-blue-600/20 to-transparent"
          ></div>
        </div>
      </div>
    </div>

    <!-- Decoration Elements -->
    <div
      class="absolute top-10 right-10 w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"
    ></div>
    <div
      class="absolute bottom-10 left-10 w-72 h-72 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"
    ></div>
  </section>
</template>

<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
</script>

<style scoped>
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.8s ease-out forwards;
  opacity: 0;
}
</style>
