<script setup>
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
</script>
<template>
  <ChevronLeft v-if="!$attrs.collapsed" v-bind="$attrs" :size="20" />
  <ChevronRight v-else v-bind="$attrs" :size="20" />
</template>
