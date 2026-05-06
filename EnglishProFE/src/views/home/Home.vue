<template>
  <div class="flex flex-col min-h-screen bg-white">
    <!-- Navbar -->
    <Navbar />

    <!-- Main Content -->
    <main class="flex-1">
      <!-- Hero Section -->
      <HeroSection />

      <!-- Stats Section -->
      <StatsSection />

      <!-- Featured Courses Section -->
      <FeaturedCourses />

      <!-- Features Highlights Section -->
      <FeaturesHighlights />
    </main>

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from './components/Navbar.vue'
import HeroSection from './components/HeroSection.vue'
import StatsSection from './components/StatsSection.vue'
import FeaturedCourses from './components/FeaturedCourses.vue'
import FeaturesHighlights from './components/FeaturesHighlights.vue'
import Footer from './components/Footer.vue'
</script>

<style scoped>
/* Smooth scrolling behavior */
:deep(html) {
  scroll-behavior: smooth;
}
</style>
