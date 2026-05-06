<template>
  <div class="overflow-x-auto rounded-lg border border-gray-200 shadow-sm">
    <table class="w-full divide-y divide-gray-200">
      <thead class="bg-gray-100 text-left text-sm font-semibold text-gray-900">
        <tr>
          <th v-for="column in columns" :key="column.key" class="px-6 py-4">
            <div class="flex items-center space-x-2">
              <span>{{ column.label }}</span>
              <button
                v-if="column.sortable"
                @click="toggleSort(column.key)"
                class="text-gray-600 hover:text-gray-900"
              >
                <ArrowsUpDownIcon class="w-4 h-4" />
              </button>
            </div>
          </th>
          <th v-if="actions.length > 0" class="px-6 py-4">Hành động</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-200">
        <tr v-for="(row, idx) in sortedRows" :key="idx" class="hover:bg-gray-50 transition">
          <td v-for="column in columns" :key="column.key" class="px-6 py-4 text-sm text-gray-700">
            <div v-if="column.render">
              <component :is="{ template: `<div>${column.render(row)}</div>` }" />
            </div>
            <div v-else-if="column.type === 'badge'">
              <span :class="getBadgeClass(row[column.key])">
                {{ row[column.key] }}
              </span>
            </div>
            <div v-else>{{ row[column.key] }}</div>
          </td>
          <td v-if="actions.length > 0" class="px-6 py-4">
            <div class="flex items-center space-x-2">
              <button
                v-for="action in actions"
                :key="action.label"
                @click="() => action.handler(row)"
                :title="action.label"
                :class="[
                  'p-2 rounded-lg transition',
                  action.color === 'red'
                    ? 'text-red-600 hover:bg-red-50'
                    : action.color === 'blue'
                      ? 'text-blue-600 hover:bg-blue-50'
                      : 'text-gray-600 hover:bg-gray-200',
                ]"
              >
                <component :is="action.icon" class="w-4 h-4" />
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Empty State -->
    <div v-if="rows.length === 0" class="text-center py-12">
      <p class="text-gray-500">{{ emptyMessage }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ArrowsUpDownIcon from './icons/ArrowsUpDownIcon.vue'

interface Column {
  key: string
  label: string
  type?: 'text' | 'badge' | 'date'
  sortable?: boolean
  render?: (row: any) => string
}

interface Action {
  label: string
  icon: any
  handler: (row: any) => void
  color?: 'red' | 'blue' | 'gray'
}

const props = withDefaults(
  defineProps<{
    columns: Column[]
    rows: any[]
    actions?: Action[]
    emptyMessage?: string
  }>(),
  {
    actions: () => [],
    emptyMessage: 'Không có dữ liệu',
  },
)

const sortKey = ref<string | null>(null)
const sortDirection = ref<'asc' | 'desc'>('asc')

const toggleSort = (key: string) => {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDirection.value = 'asc'
  }
}

const sortedRows = computed(() => {
  if (!sortKey.value) return props.rows

  return [...props.rows].sort((a, b) => {
    const aVal = a[sortKey.value!]
    const bVal = b[sortKey.value!]

    if (typeof aVal === 'string') {
      return sortDirection.value === 'asc' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
    }

    return sortDirection.value === 'asc' ? aVal - bVal : bVal - aVal
  })
})

const getBadgeClass = (value: string) => {
  const baseClass = 'inline-flex items-center px-3 py-1 rounded-full text-xs font-medium'

  if (value === 'Active' || value === 'Success' || value === 'active' || value === 'success') {
    return `${baseClass} bg-green-100 text-green-700`
  }
  if (value === 'Inactive' || value === 'Pending' || value === 'inactive' || value === 'pending') {
    return `${baseClass} bg-yellow-100 text-yellow-700`
  }
  if (value === 'Failed' || value === 'Rejected' || value === 'failed') {
    return `${baseClass} bg-red-100 text-red-700`
  }
  if (value === 'STUDENT') {
    return `${baseClass} bg-blue-100 text-blue-700`
  }
  if (value === 'TEACHER') {
    return `${baseClass} bg-blue-100 text-blue-700`
  }
  if (value === 'ADMIN') {
    return `${baseClass} bg-orange-100 text-orange-700`
  }

  return `${baseClass} bg-gray-100 text-gray-700`
}
</script>
