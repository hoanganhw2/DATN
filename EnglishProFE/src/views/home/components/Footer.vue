<template>
  <footer class="bg-gray-900 text-gray-300">
    <!-- Main Footer Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
        <!-- Company Info -->
        <div>
          <div class="flex items-center gap-2 mb-4">
            <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold">EP</span>
            </div>
            <span class="font-bold text-white text-lg">EnglishPro</span>
          </div>
          <p class="text-sm mb-4">
            Nền tảng học tiếng Anh trực tuyến với công nghệ SRS Flashcard và luyện thi TOEIC/IELTS
            chuẩn quốc tế
          </p>
          <!-- Social Links -->
          <div class="flex gap-4">
            <a href="#" class="hover:text-blue-400 transition-colors" title="Facebook">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
                />
              </svg>
            </a>
            <a href="#" class="hover:text-blue-400 transition-colors" title="Twitter">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"
                />
              </svg>
            </a>
            <a href="#" class="hover:text-blue-400 transition-colors" title="YouTube">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"
                />
              </svg>
            </a>
          </div>
        </div>

        <!-- Product Links -->
        <div>
          <h3 class="font-semibold text-white mb-4">Sản Phẩm</h3>
          <ul class="space-y-2 text-sm">
            <li><a href="#" class="hover:text-blue-400 transition-colors">Khóa Học</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Flashcards</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Luyện Thi</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Blog</a></li>
          </ul>
        </div>

        <!-- Company Links -->
        <div>
          <h3 class="font-semibold text-white mb-4">Công Ty</h3>
          <ul class="space-y-2 text-sm">
            <li><a href="#" class="hover:text-blue-400 transition-colors">Về Chúng Tôi</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Liên Hệ</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Tuyển Dụng</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Blog</a></li>
          </ul>
        </div>

        <!-- Legal Links -->
        <div>
          <h3 class="font-semibold text-white mb-4">Pháp Lý</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <a href="#" class="hover:text-blue-400 transition-colors">Điều Khoản Sử Dụng</a>
            </li>
            <li>
              <a href="#" class="hover:text-blue-400 transition-colors">Chính Sách Riêng Tư</a>
            </li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Chính Sách Cookie</a></li>
            <li><a href="#" class="hover:text-blue-400 transition-colors">Cài Đặt Cookie</a></li>
          </ul>
        </div>
      </div>

      <!-- Bottom Footer -->
      <div
        class="border-t border-gray-700 pt-8 flex flex-col md:flex-row justify-between items-center"
      >
        <p class="text-sm text-gray-400 mb-4 md:mb-0">
          © 2024 EnglishPro. Tất cả quyền được bảo lưu.
        </p>
        <div class="flex gap-6 text-sm">
          <a href="#" class="text-gray-400 hover:text-blue-400 transition-colors">
            Cài Đặt Cookie
          </a>
          <a href="#" class="text-gray-400 hover:text-blue-400 transition-colors">
            Tuân Thủ GDPR
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// Footer component - no state needed
</script>
