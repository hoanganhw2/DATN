<template>
  <div
    class="h-full flex flex-col rounded-xl bg-white border border-gray-200 overflow-hidden p-4 gap-4"
  >
    <!-- Skeleton Thumbnail -->
    <div class="w-full aspect-video bg-gray-200 rounded-lg animate-pulse"></div>

    <!-- Skeleton Content -->
    <div class="flex-1 flex flex-col gap-3">
      <!-- Title skeleton -->
      <div class="space-y-2">
        <div class="h-5 bg-gray-200 rounded-lg animate-pulse"></div>
        <div class="h-5 bg-gray-200 rounded-lg animate-pulse w-3/4"></div>
      </div>

      <!-- Teacher skeleton -->
      <div class="h-4 bg-gray-200 rounded-lg animate-pulse w-1/2"></div>

      <!-- Price skeleton -->
      <div class="mt-auto pt-3 border-t border-gray-200">
        <div class="h-6 bg-gray-200 rounded-lg animate-pulse w-1/3"></div>
      </div>

      <!-- Button skeleton -->
      <div class="h-10 bg-gray-200 rounded-xl animate-pulse"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No props needed for skeleton
</script>
