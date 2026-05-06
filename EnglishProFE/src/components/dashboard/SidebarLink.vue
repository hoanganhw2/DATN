<template>
  <RouterLink
    :to="to"
    :class="[
      'flex items-center space-x-3 px-3 py-2 rounded-lg transition-all duration-300',
      active ? 'bg-blue-600 text-white shadow-md' : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900',
      isCollapsed ? 'justify-center px-2' : '',
    ]"
    v-bind="$attrs"
  >
    <component :is="icon" class="w-5 h-5 shrink-0" />
    <span v-if="!isCollapsed" class="text-sm font-medium whitespace-nowrap overflow-hidden transition-all duration-300">
      {{ label }}
    </span>
  </RouterLink>
</template>

<script setup lang="ts">
defineProps<{
  to: string
  icon: any
  label: string
  active: boolean
  isCollapsed?: boolean
}>()
</script>
