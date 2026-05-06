<template>
  <DashboardLayout>
    <div class="max-w-4xl">
      <div class="flex items-center mb-6">
        <router-link to="/dashboard/teacher/courses" class="text-gray-600 hover:text-gray-900">
          ← Quay lại
        </router-link>
      </div>

      <div class="bg-white border border-gray-200 rounded-lg p-6">
        <h2 class="text-2xl font-bold text-gray-900 mb-2">Tạo khóa học mới</h2>
        <p class="text-sm text-amber-600 mb-6">
          Khóa học sau khi tạo sẽ ở trạng thái chờ duyệt. Chỉ admin mới có quyền phát hành.
        </p>

        <form @submit.prevent="saveCourse" class="space-y-6">
          <!-- Course Title -->
          <FormInput
            v-model="formData.title"
            label="Tiêu đề khóa học"
            placeholder="Nhập tiêu đề khóa học"
            required
            :error="errors.title"
          />

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Mô tả khóa học
              <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="formData.description"
              placeholder="Nhập mô tả chi tiết về khóa học"
              rows="4"
              class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
            ></textarea>
            <span v-if="errors.description" class="text-sm text-red-400">{{
              errors.description
            }}</span>
          </div>

          <!-- Category and Level -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Danh mục
                <span class="text-red-500">*</span>
              </label>
              <select
                v-model="formData.category"
                class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900 focus:outline-none focus:border-blue-500"
              >
                <option value="Grammar">Grammar</option>
                <option value="Speaking">Speaking</option>
                <option value="IELTS">IELTS</option>
                <option value="TOEIC">TOEIC</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Cấp độ
                <span class="text-red-500">*</span>
              </label>
              <select
                v-model="formData.level"
                class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 text-gray-900 focus:outline-none focus:border-blue-500"
              >
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
              </select>
            </div>
          </div>

          <!-- Price -->
          <FormInput
            v-model="formData.price"
            label="Giá khóa học (đồng)"
            type="number"
            placeholder="0"
            required
            :error="errors.price"
          />

          <!-- Thumbnail URL -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Ảnh bìa
            </label>
            <FormInput
              v-model="formData.thumbnailUrl"
              placeholder="https://example.com/thumbnail.jpg"
            />
          </div>

          <!-- Actions -->
          <div class="flex gap-4 pt-6 border-t border-gray-200">
            <router-link to="/dashboard/teacher/courses" class="flex-1">
              <Button variant="secondary" class="w-full"> Hủy </Button>
            </router-link>
            <Button variant="primary" type="submit" class="flex-1">
              Tạo khóa học
            </Button>
          </div>
        </form>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'
import { createCourse, type CourseLevel } from '@/api/courses'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import FormInput from '@/components/dashboard/FormInput.vue'
import Button from '@/components/dashboard/Button.vue'

const router = useRouter()
const toastStore = useToastStore()

const formData = ref({
  title: '',
  description: '',
  category: 'Grammar',
  level: 'BEGINNER' as CourseLevel,
  price: 0,
  thumbnailUrl: '',
})

const errors = ref({
  title: '',
  description: '',
  price: '',
})

const normalizeSlug = (value: string) => {
  return value
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
}

const validateForm = (): boolean => {
  errors.value = { title: '', description: '', price: '' }

  if (!formData.value.title.trim()) {
    errors.value.title = 'Tiêu đề không được trống'
  }

  if (!formData.value.description.trim()) {
    errors.value.description = 'Mô tả không được trống'
  }

  if (formData.value.price <= 0) {
    errors.value.price = 'Giá phải lớn hơn 0'
  }

  return Object.values(errors.value).every((e) => e === '')
}

const saveCourse = async () => {
  if (!validateForm()) {
    toastStore.error('Vui lòng kiểm tra lại các trường')
    return
  }

  try {
    // TODO: Call API to save course
    await createCourse({
      title: formData.value.title.trim(),
      slug: normalizeSlug(formData.value.title),
      description: formData.value.description.trim(),
      level: formData.value.level,
      category: formData.value.category,
      price: Number(formData.value.price),
      thumbnailUrl: formData.value.thumbnailUrl?.trim() || undefined,
    })
    toastStore.success('Tạo khóa học thành công, đang chờ admin duyệt')
    await router.push('/dashboard/teacher/courses')
  } catch (error) {
    toastStore.error('Không thể tạo khóa học')
  }
}
</script>
