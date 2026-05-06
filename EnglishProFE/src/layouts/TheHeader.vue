<template>
  <div
    class="fixed top-0 w-full z-50 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-100 dark:border-slate-800 shadow-sm dark:shadow-none flex justify-between items-center px-8 h-16 max-w-full mx-auto"
  >
    <div class="flex items-center gap-8">
      <span class="text-2xl font-bold text-blue-600 dark:text-blue-400 tracking-tight font-headline"
        >Azure Scholar</span
      >
      <div class="hidden md:flex gap-6 font-['Lexend'] font-medium text-sm">
        <a class="text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 pb-1" href="#"
          >Khóa học</a
        >
        <a
          class="text-slate-600 dark:text-slate-400 hover:text-blue-500 transition-colors duration-200"
          href="#"
          >TOEIC</a
        >
        <a
          class="text-slate-600 dark:text-slate-400 hover:text-blue-500 transition-colors duration-200"
          href="#"
          >IELTS</a
        >
        <a
          class="text-slate-600 dark:text-slate-400 hover:text-blue-500 transition-colors duration-200"
          href="#"
          >Luyện tập</a
        >
        <a
          class="text-slate-600 dark:text-slate-400 hover:text-blue-500 transition-colors duration-200"
          href="#"
          >Tài nguyên</a
        >
      </div>
    </div>
    <div class="flex items-center gap-4">
      <div
        class="hidden lg:flex items-center bg-surface-container rounded-full px-4 py-2 border border-outline-variant"
      >
        <span class="material-symbols-outlined text-outline mr-2 text-[20px]">search</span>
        <input
          class="bg-transparent border-none focus:ring-0 text-sm w-48 font-body"
          placeholder="Tìm kiếm khóa học..."
          type="text"
        />
      </div>
      <button
        class="text-slate-600 dark:text-slate-400 hover:text-blue-500 text-sm font-medium transition-colors duration-200"
      >
        Đăng nhập
      </button>
      <button
        class="bg-primary text-on-primary px-6 py-2 rounded-lg text-sm font-semibold shadow-blue-200 shadow-sm transition-all hover:opacity-90 active:scale-95"
      >
        Đăng ký
      </button>
    </div>
  </div>
</template>
<script setup></script>
<style lang=""></style>
