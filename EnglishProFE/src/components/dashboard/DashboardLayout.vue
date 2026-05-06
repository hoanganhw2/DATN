<template>
  <div class="flex h-screen bg-gray-50 text-gray-900 overflow-hidden">
    <!-- Sidebar -->
    <DashboardSidebar :is-collapsed="isCollapsed" @toggle="toggleSidebar" />

    <!-- Main content area -->
    <div class="flex-1 flex flex-col overflow-hidden transition-all duration-300">
      <!-- Header -->
      <DashboardHeader :is-collapsed="isCollapsed" />

      <!-- Content -->
      <main class="flex-1 overflow-y-auto bg-white p-6 custom-scrollbar">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DashboardSidebar from './DashboardSidebar.vue'
import DashboardHeader from './DashboardHeader.vue'

// Sidebar state
const isCollapsed = ref(localStorage.getItem('sidebar_collapsed') === 'true')

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('sidebar_collapsed', String(isCollapsed.value))
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #d1d5db;
}
</style>
