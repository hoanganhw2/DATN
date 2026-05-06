<script setup>
import { RouterView } from 'vue-router'
import ChatWidget from '@/components/ChatWidget.vue'
import ToastContainer from '@/components/ToastContainer.vue'
import DialogContainer from '@/components/DialogContainer.vue'
</script>
<template>
  <RouterView />
  <ChatWidget />
  <ToastContainer />
  <DialogContainer />
</template>

<style scoped></style>
