<template>
  <div class="min-h-screen flex">
    <!-- Left Side - Image Section (Hidden on Mobile) -->
    <div class="hidden lg:flex lg:w-1/2 relative overflow-hidden animate-fade-in">
      <!-- Background Image -->
      <img
        src="https://images.unsplash.com/photo-1522202176988-696ce0c50081?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8ZW58MHx8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"
        alt="Learning"
        class="w-full h-full object-cover"
        loading="lazy"
      />

      <!-- Gradient Overlay -->
      <div class="absolute inset-0 bg-gradient-to-r from-blue-900/60 to-transparent"></div>

      <!-- Content Overlay -->
      <div class="absolute inset-0 flex flex-col justify-center items-start px-12">
        <div class="max-w-md">
          <div class="flex items-center gap-3 mb-8">
            <div class="w-14 h-14 bg-white rounded-lg flex items-center justify-center">
              <span class="text-blue-600 font-bold text-2xl">EP</span>
            </div>
            <h1 class="text-4xl font-bold text-white">EnglishPro</h1>
          </div>

          <h2 class="text-4xl font-bold text-white mb-6 leading-tight">
            Học tiếng Anh,<br />
            <span class="text-blue-200">Thay đổi cuộc sống</span>
          </h2>

          <p class="text-lg text-white/90 mb-8 leading-relaxed">
            Tham gia hàng ngàn học viên đang nâng cao kỹ năng tiếng Anh qua video, flashcard SRS, và
            luyện thi quốc tế chuẩn.
          </p>

          <!-- Features List -->
          <div class="space-y-4">
            <div class="flex items-center gap-3">
              <svg
                class="w-6 h-6 text-blue-200 flex-shrink-0"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clip-rule="evenodd"
                />
              </svg>
              <span class="text-white/90 font-medium">Flashcard SRS thông minh</span>
            </div>
            <div class="flex items-center gap-3">
              <svg
                class="w-6 h-6 text-blue-200 flex-shrink-0"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clip-rule="evenodd"
                />
              </svg>
              <span class="text-white/90 font-medium">Luyện thi TOEIC/IELTS</span>
            </div>
            <div class="flex items-center gap-3">
              <svg
                class="w-6 h-6 text-blue-200 flex-shrink-0"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clip-rule="evenodd"
                />
              </svg>
              <span class="text-white/90 font-medium">Lộ trình cá nhân hóa</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Right Side - Form Section -->
    <div
      class="w-full lg:w-1/2 flex items-center justify-center px-4 sm:px-6 lg:px-8 py-12 lg:py-0 bg-gray-50 animate-fade-in-up"
    >
      <div class="w-full max-w-md">
        <!-- Card Container -->
        <div class="bg-white rounded-2xl shadow-xl p-8 sm:p-10">
          <!-- Logo Mobile -->
          <div class="lg:hidden flex items-center justify-center gap-3 mb-8">
            <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-lg">EP</span>
            </div>
            <span class="font-bold text-2xl text-gray-900">EnglishPro</span>
          </div>

          <!-- Slot for Form Content -->
          <slot />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// AuthLayout - container for auth pages (login/register)
</script>

<style scoped>
@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

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

.animate-fade-in {
  animation: fade-in 0.8s ease-out forwards;
}

.animate-fade-in-up {
  animation: fade-in-up 0.8s ease-out forwards;
  opacity: 0;
}
</style>
