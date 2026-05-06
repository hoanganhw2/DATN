<template>
  <DashboardLayout>
    <div>
      <!-- Action Bar -->
      <div class="flex justify-end mb-6">
        <button @click="openCreatePage" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
          </svg>
          Tạo Đề Thi Mới
        </button>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="overflow-x-auto custom-scrollbar">
          <table class="w-full text-left border-collapse whitespace-nowrap">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100 text-gray-500 text-sm">
                <th class="p-4 font-medium">Tiêu đề</th>
                <th class="p-4 font-medium">Loại</th>
                <th class="p-4 font-medium">Trình độ</th>
                <th class="p-4 font-medium">Thời gian</th>
                <th class="p-4 font-medium">Điểm tối đa</th>
                <th class="p-4 font-medium">Trạng thái</th>
                <th class="p-4 font-medium text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-if="loading">
                <td colspan="7" class="p-4 text-center text-gray-400 py-8 animate-pulse">Đang tải dữ liệu...</td>
              </tr>
              <tr v-else-if="!exams.length">
                <td colspan="7" class="p-12 text-center text-gray-500">
                  <div class="flex flex-col items-center">
                    <svg class="w-12 h-12 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                    </svg>
                    <p>Chưa có đề thi nào</p>
                  </div>
                </td>
              </tr>
              <tr v-else v-for="exam in exams" :key="exam.id" class="hover:bg-blue-50/40 transition-colors">
                <td class="p-4 font-medium text-gray-900 max-w-xs truncate">{{ exam.title }}</td>
                <td class="p-4">
                  <span class="px-2 py-1 bg-indigo-100 text-indigo-700 text-xs rounded-lg font-medium">{{ exam.examType }}</span>
                </td>
                <td class="p-4 text-gray-600 text-sm">{{ levelLabel(exam.level) }}</td>
                <td class="p-4 text-gray-600 text-sm">{{ exam.duration / 60 }} phút</td>
                <td class="p-4 text-gray-600 text-sm">{{ exam.totalScore }}</td>
                <td class="p-4">
                  <span :class="exam.isPublished ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'" class="px-2.5 py-1 text-xs rounded-full font-medium">
                    {{ exam.isPublished ? 'Đã xuất bản' : 'Bản nháp' }}
                  </span>
                </td>
                <td class="p-4 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <!-- View -->
                    <button @click="openDetail(exam)" class="p-2 text-gray-500 hover:bg-gray-100 rounded-lg transition" title="Xem chi tiết">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                    </button>
                    <!-- Edit -->
                    <button @click="openEdit(exam)" class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition" title="Sửa">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/></svg>
                    </button>
                    <!-- Toggle publish -->
                    <button @click="togglePublish(exam)" class="p-2 rounded-lg transition" :class="exam.isPublished ? 'text-orange-500 hover:bg-orange-50' : 'text-green-600 hover:bg-green-50'" :title="exam.isPublished ? 'Hủy xuất bản' : 'Xuất bản'">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                    </button>
                    <!-- Delete -->
                    <button @click="confirmDelete(exam)" class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition" title="Xóa">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      <!-- Pagination -->
      <div class="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/50">
        <div class="flex items-center gap-3">
          <p class="text-sm text-gray-500">
            Hiển thị
            <span class="font-semibold text-gray-700">{{ displayStart }}–{{ displayEnd }}</span>
            trong <span class="font-semibold text-gray-700">{{ totalElements }}</span> đề thi
          </p>
          <select
            v-model.number="pageSize"
            @change="() => { currentPage = 0; fetchExams() }"
            class="py-1.5 px-3 text-sm rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500/20 bg-white transition"
          >
            <option :value="10">10 / trang</option>
            <option :value="20">20 / trang</option>
            <option :value="50">50 / trang</option>
          </select>
        </div>
        <div class="flex items-center gap-1.5">
          <button
            :disabled="currentPage === 0"
            @click="goToPage(0)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >«</button>
          <button
            :disabled="currentPage === 0"
            @click="goToPage(currentPage - 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >‹</button>
          <button
            v-for="p in pageNumbers"
            :key="p"
            @click="goToPage(p)"
            :class="[
              'w-10 h-10 rounded-xl text-sm font-semibold transition border',
              p === currentPage
                ? 'bg-blue-600 text-white border-blue-600 shadow'
                : 'border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300',
            ]"
          >{{ p + 1 }}</button>
          <button
            :disabled="isLastPage"
            @click="goToPage(currentPage + 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >›</button>
          <button
            :disabled="isLastPage"
            @click="goToPage(totalPages - 1)"
            class="w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 text-gray-600 hover:bg-white hover:border-blue-300 disabled:opacity-30 disabled:cursor-not-allowed transition font-bold"
          >»</button>
        </div>
      </div>
    </div>

    <!-- ===== DETAIL MODAL ===== -->
    <Teleport to="body">
      <div v-if="showDetail" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showDetail = false">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden flex flex-col">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 bg-gradient-to-r from-indigo-600 to-blue-500 flex-shrink-0">
            <div>
              <h2 class="text-white font-bold text-lg leading-tight">{{ selectedExam?.title }}</h2>
              <p class="text-indigo-200 text-sm mt-0.5">Chi tiết đề thi</p>
            </div>
            <button @click="showDetail = false" class="text-white/80 hover:text-white p-1">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
          </div>

          <div class="overflow-y-auto flex-1">
            <!-- Loading spinner -->
            <div v-if="detailLoading" class="flex items-center justify-center py-20">
              <div class="w-10 h-10 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
            </div>

            <div v-else-if="selectedExam" class="p-6 space-y-6">
              <!-- Info summary -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                <div class="bg-indigo-50 rounded-xl p-3 text-center">
                  <p class="text-xs text-indigo-500 font-medium uppercase">Loại</p>
                  <p class="font-bold text-indigo-800 mt-1">{{ selectedExam.examType }}</p>
                </div>
                <div class="bg-blue-50 rounded-xl p-3 text-center">
                  <p class="text-xs text-blue-500 font-medium uppercase">Thời gian</p>
                  <p class="font-bold text-blue-800 mt-1">{{ selectedExam.duration / 60 }} phút</p>
                </div>
                <div class="bg-emerald-50 rounded-xl p-3 text-center">
                  <p class="text-xs text-emerald-500 font-medium uppercase">Điểm tối đa</p>
                  <p class="font-bold text-emerald-800 mt-1">{{ selectedExam.totalScore }}</p>
                </div>
                <div class="bg-amber-50 rounded-xl p-3 text-center">
                  <p class="text-xs text-amber-500 font-medium uppercase">Số lần thi</p>
                  <p class="font-bold text-amber-800 mt-1">{{ selectedExam.maxAttempts || '∞' }}</p>
                </div>
              </div>

              <!-- Description -->
              <div v-if="selectedExam.description" class="bg-gray-50 rounded-xl p-4 text-sm text-gray-700">
                <p class="text-xs font-semibold text-gray-500 uppercase mb-1">Mô tả</p>
                {{ selectedExam.description }}
              </div>

              <!-- Sections & Questions -->
              <div>
                <div class="flex items-center gap-2 mb-3">
                  <h3 class="font-bold text-gray-900">Cấu trúc đề thi</h3>
                  <span class="px-2 py-0.5 bg-indigo-100 text-indigo-700 text-xs rounded-full font-medium">
                    {{ selectedExam.sections?.length || 0 }} phần
                  </span>
                  <span class="px-2 py-0.5 bg-blue-100 text-blue-700 text-xs rounded-full font-medium">
                    {{ totalQuestionCount }} câu hỏi
                  </span>
                </div>

                <div v-if="!selectedExam.sections?.length" class="text-center py-8 text-gray-400 border border-dashed border-gray-200 rounded-xl">
                  Chưa có phần thi nào
                </div>

                <div v-else class="space-y-4">
                  <div v-for="(section, si) in selectedExam.sections" :key="section.id" class="border border-gray-200 rounded-xl overflow-hidden">
                    <!-- Section header -->
                    <button
                      @click="toggleSection(section.id)"
                      class="w-full flex items-center justify-between p-4 bg-gray-50 hover:bg-gray-100 transition text-left"
                    >
                      <div class="flex items-center gap-3">
                        <span class="w-7 h-7 rounded-full bg-indigo-600 text-white text-xs font-bold flex items-center justify-center flex-shrink-0">{{ si + 1 }}</span>
                        <div>
                          <p class="font-semibold text-gray-900">{{ section.sectionName }}</p>
                          <p class="text-xs text-gray-500 mt-0.5">{{ section.questions?.length || 0 }} câu hỏi{{ section.duration ? ` · ${section.duration / 60} phút` : '' }}</p>
                        </div>
                      </div>
                      <svg class="w-4 h-4 text-gray-400 transition-transform" :class="expandedSections.includes(section.id) ? 'rotate-90' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
                    </button>

                    <!-- Questions list -->
                    <div v-if="expandedSections.includes(section.id)" class="divide-y divide-gray-50">
                      <div v-if="!section.questions?.length" class="p-4 text-sm text-gray-400 text-center">Chưa có câu hỏi</div>
                      <div v-for="(q, qi) in section.questions" :key="q.id" class="p-4">
                        <div class="flex gap-3">
                          <span class="w-6 h-6 rounded-full bg-blue-100 text-blue-700 text-xs font-bold flex items-center justify-center flex-shrink-0 mt-0.5">{{ qi + 1 }}</span>
                          <div class="flex-1 min-w-0">
                            <!-- Question text -->
                            <p class="text-sm text-gray-900 font-medium">{{ q.questionText }}</p>

                            <!-- Media preview -->
                            <div v-if="q.mediaUrl" class="mt-2">
                              <img v-if="isImage(q.mediaUrl)" :src="q.mediaUrl" class="max-h-32 rounded-lg border border-gray-200 object-contain" alt="Media" />
                              <audio v-else-if="isAudio(q.mediaUrl)" :src="q.mediaUrl" controls class="w-full mt-1 h-8"></audio>
                              <a v-else :href="q.mediaUrl" target="_blank" class="text-xs text-blue-600 hover:underline">Xem media</a>
                            </div>

                            <!-- Options -->
                            <div v-if="q.options?.length" class="mt-2 space-y-1">
                              <div v-for="(opt, oi) in q.options" :key="oi"
                                :class="opt === q.correctAnswer ? 'bg-green-50 border-green-300 text-green-800' : 'bg-gray-50 border-gray-200 text-gray-700'"
                                class="flex items-center gap-2 px-3 py-1.5 rounded-lg border text-xs"
                              >
                                <span class="font-bold w-4 flex-shrink-0">{{ String.fromCharCode(65 + oi) }}.</span>
                                <span class="flex-1">{{ opt }}</span>
                                <svg v-if="opt === q.correctAnswer" class="w-3.5 h-3.5 text-green-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/></svg>
                              </div>
                            </div>

                            <!-- Fill in blank answer -->
                            <div v-else-if="q.correctAnswer" class="mt-2 flex items-center gap-2">
                              <span class="text-xs text-gray-500">Đáp án:</span>
                              <span class="px-2 py-0.5 bg-green-100 text-green-800 text-xs rounded font-medium">{{ q.correctAnswer }}</span>
                            </div>

                            <!-- Explanation -->
                            <p v-if="q.explanation" class="mt-2 text-xs text-gray-500 italic">💡 {{ q.explanation }}</p>

                            <!-- Points badge -->
                            <div class="mt-2">
                              <span class="text-xs text-gray-400">{{ q.points || 1 }} điểm · {{ q.questionType === 'MULTIPLE_CHOICE' ? 'Trắc nghiệm' : 'Điền từ' }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="px-6 py-4 bg-gray-50 flex justify-end gap-3 border-t border-gray-100 flex-shrink-0">
            <button @click="gotoBuilder(selectedExam.id)" class="px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium hover:bg-indigo-700 transition flex items-center gap-1.5">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/></svg>
              Sửa câu hỏi
            </button>
            <button @click="openEdit(selectedExam); showDetail = false" class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition">Chỉnh sửa</button>
            <button @click="showDetail = false" class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition">Đóng</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ===== EDIT MODAL ===== -->
    <Teleport to="body">
      <div v-if="showEdit" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showEdit = false">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-xl overflow-hidden">
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
            <h2 class="font-bold text-gray-900 text-lg">Chỉnh sửa đề thi</h2>
            <button @click="showEdit = false" class="text-gray-400 hover:text-gray-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
          </div>
          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Tiêu đề <span class="text-red-500">*</span></label>
              <input v-model="editForm.title" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
              <textarea v-model="editForm.description" rows="3" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none resize-none"></textarea>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Thời lượng (phút)</label>
                <input v-model.number="editDurationMinutes" type="number" min="1" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Điểm tối đa</label>
                <input v-model.number="editForm.totalScore" type="number" min="1" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Ngưỡng đạt (%)</label>
                <input v-model.number="editForm.passingScore" type="number" min="0" max="100" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Số lần thi tối đa</label>
                <input v-model.number="editForm.maxAttempts" type="number" min="1" placeholder="Không giới hạn" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
            </div>
            <div class="flex items-center gap-3 py-2">
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" v-model="editForm.isPublished" class="sr-only peer" />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"></div>
              </label>
              <span class="text-sm font-medium text-gray-700">{{ editForm.isPublished ? 'Đã xuất bản' : 'Bản nháp' }}</span>
            </div>
          </div>
          <div class="px-6 py-4 bg-gray-50 flex justify-end gap-3 border-t border-gray-100">
            <button @click="submitEdit" :disabled="isSaving" class="px-5 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition flex items-center gap-2 disabled:opacity-60">
              <span v-if="isSaving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              Lưu thay đổi
            </button>
            <button @click="showEdit = false" class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition">Hủy</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ===== DELETE CONFIRM MODAL ===== -->
    <Teleport to="body">
      <div v-if="showDeleteConfirm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showDeleteConfirm = false">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="p-6">
            <div class="flex items-center gap-4 mb-4">
              <div class="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-900">Xóa đề thi</h3>
                <p class="text-sm text-gray-500 mt-1">Hành động này không thể hoàn tác.</p>
              </div>
            </div>
            <p class="text-gray-700">Bạn có chắc muốn xóa đề thi <strong>{{ examToDelete?.title }}</strong>?</p>
            <div class="mt-3 bg-red-50 border border-red-100 rounded-lg p-3 text-sm text-red-700">
              Toàn bộ cấu trúc phần thi và câu hỏi bên trong sẽ bị xóa vĩnh viễn.
            </div>
          </div>
          <div class="px-6 py-4 bg-gray-50 flex justify-end gap-3 border-t border-gray-100">
            <button @click="executeDelete" :disabled="isSaving" class="px-5 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition flex items-center gap-2 disabled:opacity-60">
              <span v-if="isSaving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              Xóa vĩnh viễn
            </button>
            <button @click="showDeleteConfirm = false" class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition">Hủy</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'
import DashboardLayout from '@/components/dashboard/DashboardLayout.vue'
import { getMyExams, getExamById, updateExam, deleteExam } from '@/api/exam'

// ── Shared helper component (inline) ──────────────────────────────────────────
const InfoCard = {
  props: ['label', 'value', 'full'],
  template: `<div :class="full ? 'col-span-2' : 'col-span-1'">
    <p class="text-xs text-gray-500 mb-0.5">{{ label }}</p>
    <p class="text-sm font-medium text-gray-900 break-words">{{ value }}</p>
  </div>`
}

const authStore = useAuthStore()
const router = useRouter()
const toastStore = useToastStore()
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

// ── List State ─────────────────────────────────────────────────────────────────
const loading = ref(false)
const exams = ref([])
const currentPage = ref(0)
const totalPages = ref(1)
const totalElements = ref(0)
const pageSize = ref(10)

// ── Computed pagination ────────────────────────────────────────────────────────
const isLastPage = computed(() => (currentPage.value + 1) * pageSize.value >= totalElements.value)
const displayStart = computed(() =>
  totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1,
)
const displayEnd = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, totalElements.value),
)
const pageNumbers = computed(() => {
  const total = totalPages.value, current = currentPage.value, delta = 2
  const range = []
  for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++)
    range.push(i)
  return range
})

const goToPage = (p) => {
  if (p < 0 || p >= totalPages.value) return
  currentPage.value = p
  fetchExams()
}

// ── Modal State ────────────────────────────────────────────────────────────────
const showDetail = ref(false)
const showEdit = ref(false)
const showDeleteConfirm = ref(false)
const selectedExam = ref(null)
const examToDelete = ref(null)
const isSaving = ref(false)
const detailLoading = ref(false)
const expandedSections = ref([])

// ── Computed ───────────────────────────────────────────────────────────────────
const totalQuestionCount = computed(() =>
  (selectedExam.value?.sections || []).reduce((sum, s) => sum + (s.questions?.length || 0), 0)
)

// ── Edit Form ──────────────────────────────────────────────────────────────────
const editForm = ref({
  title: '', description: '', isPublished: false,
  totalScore: 990, passingScore: 50, maxAttempts: null
})
const editDurationMinutes = ref(120)

// ── Helpers ────────────────────────────────────────────────────────────────────
const levelLabel = (level) => {
  const map = { BEGINNER: 'Cơ bản', INTERMEDIATE: 'Trung cấp', ADVANCED: 'Nâng cao' }
  return map[level] || 'Không phân loại'
}

// ── Fetch ──────────────────────────────────────────────────────────────────────
const fetchExams = async () => {
  loading.value = true
  try {
    const res = await getMyExams({ page: currentPage.value, size: pageSize.value })
    if (res.status === 200) {
      exams.value = res.data.content
      totalPages.value = res.data.page.totalPages
      totalElements.value = res.data.page.totalElements || res.data.content.length
    }
  } catch (e) {
    toastStore.error('Lỗi khi lấy danh sách đề thi')
  } finally {
    loading.value = false
  }
}

onMounted(fetchExams)

// ── Navigation ─────────────────────────────────────────────────────────────────
const openCreatePage = () => {
  router.push(isAdmin.value ? '/dashboard/admin/exams/create' : '/dashboard/teacher/exams/create')
}

// ── Detail Modal ───────────────────────────────────────────────────────────────
const openDetail = async (exam) => {
  selectedExam.value = exam
  showDetail.value = true
  detailLoading.value = true
  expandedSections.value = []
  try {
    const data = await getExamById(exam.id)
    selectedExam.value = data
    // Auto expand first section
    if (data.sections?.length) {
      expandedSections.value = [data.sections[0].id]
    }
  } catch {
    // fallback to list data already set
  } finally {
    detailLoading.value = false
  }
}

const toggleSection = (sectionId) => {
  const idx = expandedSections.value.indexOf(sectionId)
  if (idx > -1) expandedSections.value.splice(idx, 1)
  else expandedSections.value.push(sectionId)
}

const isImage = (url) => /\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i.test(url)
const isAudio = (url) => /\.(mp3|ogg|wav|m4a)(\?.*)?$/i.test(url)

// ── Edit Modal ─────────────────────────────────────────────────────────────────
const openEdit = (exam) => {
  selectedExam.value = exam
  editForm.value = {
    title: exam.title,
    description: exam.description || '',
    isPublished: exam.isPublished || false,
    totalScore: exam.totalScore,
    passingScore: Number(exam.passingScore) || 50,
    maxAttempts: exam.maxAttempts || null,
  }
  editDurationMinutes.value = Math.round((exam.duration || 7200) / 60)
  showEdit.value = true
}

const gotoBuilder = (id) => {
  showDetail.value = false
  const prefix = isAdmin.value ? '/dashboard/admin' : '/dashboard/teacher'
  router.push(`${prefix}/exams/${id}/edit?step=2`)
}

const submitEdit = async () => {
  if (!editForm.value.title.trim()) {
    toastStore.error('Tiêu đề không được để trống')
    return
  }
  isSaving.value = true
  try {
    const payload = {
      ...editForm.value,
      duration: editDurationMinutes.value * 60,
    }
    const res = await updateExam(selectedExam.value.id, payload)
    if (res.status === 200) {
      const idx = exams.value.findIndex(e => e.id === selectedExam.value.id)
      if (idx !== -1) exams.value[idx] = { ...exams.value[idx], ...res.data }
      showEdit.value = false
      toastStore.success('Cập nhật đề thi thành công!')
    }
  } catch (e) {
    toastStore.error(e.response?.data?.message || 'Có lỗi xảy ra khi cập nhật đề thi')
  } finally {
    isSaving.value = false
  }
}

// ── Toggle Publish ─────────────────────────────────────────────────────────────
const togglePublish = async (exam) => {
  const newState = !exam.isPublished
  const label = newState ? 'xuất bản' : 'hủy xuất bản'
  try {
    const payload = {
      title: exam.title,
      description: exam.description,
      isPublished: newState,
      duration: exam.duration,
      totalScore: exam.totalScore,
      passingScore: Number(exam.passingScore),
      maxAttempts: exam.maxAttempts,
    }
    const res = await updateExam(exam.id, payload)
    if (res.status === 200) {
      const idx = exams.value.findIndex(e => e.id === exam.id)
      if (idx !== -1) exams.value[idx] = { ...exams.value[idx], isPublished: newState }
      toastStore.success(newState ? 'Đã xuất bản đề thi!' : 'Đã hủy xuất bản đề thi!')
    }
  } catch (e) {
    toastStore.error(e.response?.data?.message || 'Có lỗi xảy ra')
  }
}

// ── Delete ─────────────────────────────────────────────────────────────────────
const confirmDelete = (exam) => { examToDelete.value = exam; showDeleteConfirm.value = true }

const executeDelete = async () => {
  isSaving.value = true
  try {
    await deleteExam(examToDelete.value.id)
    exams.value = exams.value.filter(e => e.id !== examToDelete.value.id)
    toastStore.success('Xóa đề thi thành công!')
    showDeleteConfirm.value = false
    examToDelete.value = null
    if (exams.value.length === 0 && currentPage.value > 0) {
      currentPage.value--; fetchExams()
    }
  } catch (e) {
    toastStore.error(e.response?.data?.message || 'Không thể xóa đề thi')
  } finally {
    isSaving.value = false
  }
}
</script>
