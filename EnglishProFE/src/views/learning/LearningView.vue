<template>
  <div class="learning-page">
    <!-- ══ TOP HEADER BAR ══ -->
    <header class="learning-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack" title="Quay lại">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M15 19l-7-7 7-7"
            />
          </svg>
        </button>
        <div class="course-title-bar" v-if="course">
          <h1 class="course-title-text">{{ course.title }}</h1>
        </div>
      </div>
      <div class="header-right">
        <div class="progress-pill" v-if="overallProgress !== null">
          <div class="progress-pill-fill" :style="{ width: overallProgress + '%' }"></div>
          <span class="progress-pill-text">{{ Math.round(overallProgress) }}% hoàn thành</span>
        </div>
      </div>
    </header>

    <!-- ══ LOADING ══ -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải khóa học...</p>
    </div>

    <!-- ══ ERROR ══ -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">😕</div>
      <h2>{{ error }}</h2>
      <button class="retry-btn" @click="loadCourse">Thử lại</button>
    </div>

    <!-- ══ ACCESS DENIED ══ -->
    <div v-else-if="accessDenied" class="access-denied-state">
      <div class="denied-card">
        <div class="denied-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z"
            />
          </svg>
        </div>
        <h2 class="denied-title">Bạn chưa có quyền truy cập</h2>
        <p class="denied-message">{{ accessDeniedMessage }}</p>
        <div class="denied-actions">
          <button class="denied-btn primary" @click="goToCourseDetail">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z"
              />
            </svg>
            Mua khóa học ngay
          </button>
          <button class="denied-btn secondary" @click="goBack">← Quay lại</button>
        </div>
      </div>
    </div>

    <!-- ══ MAIN CONTENT ══ -->
    <div v-else-if="course" class="learning-body">
      <!-- ── LEFT: Sidebar curriculum ── -->
      <aside class="curriculum-sidebar" :class="{ collapsed: sidebarCollapsed }">
        <div class="sidebar-toggle-wrap">
          <button class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                :d="sidebarCollapsed ? 'M13 5l7 7-7 7' : 'M11 19l-7-7 7-7'"
              />
            </svg>
          </button>
        </div>

        <div class="sidebar-content" v-show="!sidebarCollapsed">
          <div class="sidebar-header">
            <h3>Nội dung khóa học</h3>
            <span class="lesson-counter">{{ completedCount }}/{{ totalLessonCount }} bài</span>
          </div>

          <div class="chapter-list">
            <div v-for="(chapter, cIdx) in course.chapters" :key="chapter.id" class="chapter-block">
              <!-- Chapter Header -->
              <button
                class="chapter-header"
                @click="toggleChapter(cIdx)"
                :class="{ expanded: expandedChapters[cIdx] }"
              >
                <div class="chapter-left">
                  <svg class="chevron-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                  <span class="chapter-title">Chương {{ cIdx + 1 }}: {{ chapter.title }}</span>
                </div>
                <span class="chapter-progress-badge">
                  {{ getChapterCompletedCount(chapter) }}/{{ chapter.lessons.length }}
                </span>
              </button>

              <!-- Lessons -->
              <div v-if="expandedChapters[cIdx]" class="lesson-list">
                <button
                  v-for="lesson in chapter.lessons"
                  :key="lesson.id"
                  class="lesson-item"
                  :class="{
                    active: currentLesson?.id === lesson.id,
                    completed: completedLessonIds.has(lesson.id),
                  }"
                  @click="selectLesson(lesson, chapter)"
                >
                  <!-- Completion indicator -->
                  <div class="lesson-status">
                    <svg
                      v-if="completedLessonIds.has(lesson.id)"
                      class="check-icon"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                        clip-rule="evenodd"
                      />
                    </svg>
                    <div
                      v-else
                      class="unchecked-circle"
                      :class="{ playing: currentLesson?.id === lesson.id }"
                    ></div>
                  </div>

                  <div class="lesson-info">
                    <span class="lesson-title">{{ lesson.title }}</span>
                    <div class="lesson-meta">
                      <span class="content-type-badge">
                        {{
                          lesson.contentType === 'VIDEO'
                            ? '🎬'
                            : lesson.contentType === 'TEXT'
                              ? '📖'
                              : lesson.contentType === 'DOCUMENT'
                                ? '📎'
                                : '📝'
                        }}
                        {{
                          lesson.contentType === 'VIDEO'
                            ? 'Video'
                            : lesson.contentType === 'TEXT'
                              ? 'Bài đọc'
                              : lesson.contentType === 'DOCUMENT'
                                ? 'Tài liệu'
                                : 'Quiz'
                        }}
                      </span>
                      <span v-if="lesson.duration" class="lesson-duration">{{
                        formatDuration(lesson.duration)
                      }}</span>
                    </div>
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- ── RIGHT: Content viewer ── -->
      <main class="content-viewer">
        <!-- No lesson selected -->
        <div v-if="!currentLesson" class="empty-viewer">
          <div class="empty-icon">📚</div>
          <h2>Chọn một bài học để bắt đầu</h2>
          <p>Nhấn vào bài học trong danh mục bên trái để xem nội dung</p>
        </div>

        <!-- Lesson content -->
        <div v-else class="lesson-viewer">
          <!-- Video Player -->
          <div v-if="currentLesson.contentType === 'VIDEO'" class="video-container">
            <div
              v-if="currentLesson.contentUrl && isYoutubeUrl(currentLesson.contentUrl)"
              class="video-wrapper"
            >
              <iframe
                :src="getYoutubeEmbedUrl(currentLesson.contentUrl)"
                frameborder="0"
                allow="
                  accelerometer;
                  autoplay;
                  clipboard-write;
                  encrypted-media;
                  gyroscope;
                  picture-in-picture;
                "
                allowfullscreen
                class="video-iframe"
              ></iframe>
            </div>
            <div v-else-if="currentLesson.contentUrl" class="video-wrapper">
              <video
                ref="videoPlayer"
                :src="getContentUrl(currentLesson.contentUrl)"
                controls
                class="video-native"
                @ended="onVideoEnded"
              ></video>
            </div>
            <div v-else class="no-content">
              <p>Video chưa được tải lên cho bài học này.</p>
            </div>
          </div>

          <!-- Reading Content -->
          <div v-else-if="currentLesson.contentType === 'TEXT'" class="reading-container">
            <!-- Chỉ hiển thị Iframe nếu có contentUrl và KHÔNG phải là link video/youtube (tránh hiện khung xám lỗi) -->
            <div
              class="reading-content"
              v-if="currentLesson.contentUrl && !isYoutubeUrl(currentLesson.contentUrl)"
            >
              <iframe :src="currentLesson.contentUrl" class="reading-iframe"></iframe>
            </div>

            <!-- Custom text description for TEXT lessons -->
            <div v-if="currentLesson.description" class="lesson-text-description">
              {{ currentLesson.description }}
            </div>

            <!-- Chỉ hiện thông báo "Chưa sẵn sàng" nếu thực sự không có cả link tài liệu lẫn nội dung chữ -->
            <div
              v-if="
                !currentLesson.description &&
                (!currentLesson.contentUrl || isYoutubeUrl(currentLesson.contentUrl))
              "
              class="no-content"
            >
              <p>Nội dung bài đọc chưa sẵn sàng.</p>
            </div>
          </div>

          <!-- Audio Content -->
          <div
            v-else-if="currentLesson.contentType === 'AUDIO'"
            class="audio-container flex-1 flex flex-col"
          >
            <!-- Hidden native audio element -->
            <audio
              v-if="currentLesson.contentUrl"
              ref="audioRef"
              :src="getContentUrl(currentLesson.contentUrl)"
              @timeupdate="onAudioTimeUpdate"
              @loadedmetadata="onAudioLoadedMetadata"
              @ended="onAudioEnded"
              @play="isAudioPlaying = true"
              @pause="isAudioPlaying = false"
              preload="metadata"
            />

            <!-- Custom Player UI -->
            <div class="audio-player-card">
              <!-- Waveform / Album Art area -->
              <div class="audio-art">
                <div class="audio-rings">
                  <div class="ring ring-1" :class="{ 'ring-active': isAudioPlaying }"></div>
                  <div class="ring ring-2" :class="{ 'ring-active': isAudioPlaying }"></div>
                  <div class="ring ring-3" :class="{ 'ring-active': isAudioPlaying }"></div>
                </div>
                <div class="audio-icon-center">
                  <svg class="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
                    <path
                      d="M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z"
                    />
                  </svg>
                </div>
              </div>

              <!-- Info -->
              <div class="audio-info">
                <span class="audio-type-label">🎧 Bài luyện nghe</span>
                <h2 class="audio-title">{{ currentLesson.title }}</h2>
              </div>

              <!-- No file -->
              <div v-if="!currentLesson.contentUrl" class="audio-no-file">
                <svg
                  class="w-8 h-8 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"
                  />
                </svg>
                <p>File audio chưa được tải lên</p>
              </div>

              <!-- Controls -->
              <div v-else class="audio-controls">
                <!-- Progress bar -->
                <div class="audio-progress-wrap">
                  <span class="audio-time">{{ fmtTime(audioCurrentTime) }}</span>
                  <div class="audio-progress-bar">
                    <input
                      type="range"
                      :min="0"
                      :max="audioDuration || 100"
                      :value="audioCurrentTime"
                      @input="seekAudio"
                      class="audio-range"
                    />
                    <div
                      class="audio-progress-fill"
                      :style="{
                        width: audioDuration
                          ? (audioCurrentTime / audioDuration) * 100 + '%'
                          : '0%',
                      }"
                    ></div>
                  </div>
                  <span class="audio-time">{{ fmtTime(audioDuration) }}</span>
                </div>

                <!-- Main controls row -->
                <div class="audio-buttons">
                  <!-- Rewind 10s -->
                  <button
                    class="audio-ctrl-btn"
                    @click="
                      () => {
                        if (audioRef) audioRef.currentTime = Math.max(0, audioCurrentTime - 10)
                      }
                    "
                    title="Tua lại 10 giây"
                  >
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path
                        d="M12 5V1L7 6l5 5V7c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6H4c0 4.42 3.58 8 8 8s8-3.58 8-8-3.58-8-8-8z"
                      />
                    </svg>
                    <span class="text-xs">10</span>
                  </button>

                  <!-- Play/Pause -->
                  <button class="audio-play-btn" @click="toggleAudioPlay">
                    <svg
                      v-if="!isAudioPlaying"
                      class="w-8 h-8"
                      fill="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path d="M8 5v14l11-7z" />
                    </svg>
                    <svg v-else class="w-8 h-8" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z" />
                    </svg>
                  </button>

                  <!-- Forward 10s -->
                  <button
                    class="audio-ctrl-btn"
                    @click="
                      () => {
                        if (audioRef)
                          audioRef.currentTime = Math.min(audioDuration, audioCurrentTime + 10)
                      }
                    "
                    title="Tua đến 10 giây"
                  >
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path
                        d="M18 13c0 3.31-2.69 6-6 6s-6-2.69-6-6 2.69-6 6-6v4l5-5-5-5v4c-4.42 0-8 3.58-8 8s3.58 8 8 8 8-3.58 8-8h-2z"
                      />
                    </svg>
                    <span class="text-xs">10</span>
                  </button>
                </div>

                <!-- Bottom: volume & playback speed -->
                <div class="audio-extras">
                  <!-- Volume -->
                  <div class="audio-vol">
                    <button
                      class="audio-icon-btn"
                      @click="toggleMute"
                      :title="isAudioMuted ? 'Bật âm' : 'Tắt âm'"
                    >
                      <svg
                        v-if="isAudioMuted || audioVolume === 0"
                        class="w-5 h-5"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z"
                        />
                      </svg>
                      <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                        <path
                          d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02z"
                        />
                      </svg>
                    </button>
                    <input
                      type="range"
                      min="0"
                      max="1"
                      step="0.05"
                      :value="isAudioMuted ? 0 : audioVolume"
                      @input="setAudioVolume"
                      class="audio-vol-range"
                    />
                  </div>

                  <!-- Playback speed -->
                  <div class="audio-speed">
                    <span class="audio-speed-label">Tốc độ:</span>
                    <button
                      v-for="rate in [0.75, 1, 1.25, 1.5, 2]"
                      :key="rate"
                      class="audio-speed-btn"
                      :class="{ active: audioPlaybackRate === rate }"
                      @click="setPlaybackRate(rate)"
                    >
                      {{ rate }}x
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Description below player -->
            <div v-if="currentLesson.description" class="audio-description">
              <h3 class="font-semibold text-gray-700 mb-2 text-sm uppercase tracking-wide">
                Nội dung bài học
              </h3>
              <div class="lesson-text-description">{{ currentLesson.description }}</div>
            </div>
          </div>

          <!-- Document Content -->
          <div
            v-else-if="currentLesson.contentType === 'DOCUMENT'"
            class="document-preview-container flex-1 bg-gray-100 flex flex-col overflow-hidden"
          >
            <!-- Toolbar -->
            <div
              class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shadow-sm z-10"
            >
              <div class="flex items-center gap-3">
                <div class="p-2 bg-blue-50 text-blue-600 rounded-lg">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                    ></path>
                  </svg>
                </div>
                <div>
                  <h3 class="font-bold text-gray-900 text-sm leading-tight">
                    {{ currentLesson.title }}
                  </h3>
                  <p class="text-xs text-gray-500">Bản xem trước tài liệu</p>
                </div>
              </div>
              <a
                v-if="currentLesson.contentUrl"
                :href="getContentUrl(currentLesson.contentUrl)"
                target="_blank"
                download
                class="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-bold text-sm transition-all shadow-sm"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
                  ></path>
                </svg>
                Tải xuống
              </a>
            </div>

            <!-- Preview Area -->
            <div class="flex-1 relative bg-gray-500 overflow-hidden">
              <template v-if="currentLesson.contentUrl">
                <iframe
                  v-if="currentLesson.contentUrl.toLowerCase().endsWith('.pdf')"
                  :src="getContentUrl(currentLesson.contentUrl) + '#view=FitH&toolbar=0'"
                  class="w-full h-full border-none bg-white"
                  title="Document Preview"
                ></iframe>
                <div
                  v-else
                  class="absolute inset-0 flex items-center justify-center bg-white text-gray-800 flex-col gap-6 p-8 text-center"
                >
                  <div
                    class="w-20 h-20 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center shadow-sm"
                  >
                    <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                      ></path>
                    </svg>
                  </div>
                  <div>
                    <h3 class="text-xl font-bold mb-3 text-gray-900">
                      Định dạng không hỗ trợ xem trước
                    </h3>
                    <p class="text-gray-600 max-w-md mx-auto leading-relaxed">
                      Trình duyệt không hỗ trợ hiển thị trực tiếp tài liệu dạng Word/Excel. Vui lòng
                      sử dụng nút <strong class="text-blue-600">Tải xuống</strong> ở góc trên bên
                      phải để xem tài liệu này trên máy tính của bạn.
                    </p>
                  </div>
                </div>
              </template>
              <div
                v-else
                class="absolute inset-0 flex items-center justify-center bg-white text-gray-500 flex-col gap-4"
              >
                <svg
                  class="w-16 h-16 text-gray-200"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                  ></path>
                </svg>
                <p>Không có tệp để xem trước.</p>
              </div>
            </div>

            <!-- Bottom Info -->
            <div
              v-if="currentLesson.description"
              class="bg-white border-t border-gray-200 px-6 py-4 z-10"
            >
              <p class="text-sm text-gray-600 italic leading-relaxed">
                {{ currentLesson.description }}
              </p>
            </div>
          </div>

          <!-- Quiz Content -->
          <div v-else class="quiz-container flex-1 bg-slate-50 flex flex-col overflow-y-auto">
            <LessonQuiz :lessonId="currentLesson.id" @completed="handleQuizCompleted" />
          </div>

          <!-- Lesson Info & Actions -->
          <div class="lesson-footer">
            <div class="lesson-header-info">
              <div class="lesson-header-left">
                <span class="current-chapter-label">{{ currentChapter?.title }}</span>
                <h2 class="current-lesson-title">{{ currentLesson.title }}</h2>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="lesson-actions">
              <button class="action-btn secondary" :disabled="!prevLesson" @click="goToPrevLesson">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
                Bài trước
              </button>

              <button
                v-if="!completedLessonIds.has(currentLesson.id)"
                class="action-btn complete"
                :disabled="isMarking"
                @click="markComplete"
              >
                <svg v-if="isMarking" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                  />
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                {{ isMarking ? 'Đang lưu...' : 'Hoàn thành bài học' }}
              </button>
              <div v-else class="completed-badge">
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                    clip-rule="evenodd"
                  />
                </svg>
                Đã hoàn thành
              </div>

              <button class="action-btn primary" :disabled="!nextLesson" @click="goToNextLesson">
                Bài tiếp theo
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5l7 7-7 7"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToastStore } from '@/stores/toast'
import { getCourseDetail, type CourseDetail, type Chapter, type Lesson } from '@/api/courses'
import {
  getCourseProgress,
  markLessonComplete,
  verifyLearningAccess,
  type CourseProgressDetail,
} from '@/api/learning'
import LessonQuiz from './LessonQuiz.vue'
import { issueCertificate } from '@/api/certificates'

const route = useRoute()
const router = useRouter()
const toastStore = useToastStore()

const courseId = Number(route.params.courseId)

// State
const course = ref<CourseDetail | null>(null)
const progress = ref<CourseProgressDetail | null>(null)
const isLoading = ref(true)
const error = ref<string | null>(null)
const sidebarCollapsed = ref(false)
const expandedChapters = ref<Record<number, boolean>>({})
const currentLesson = ref<Lesson | null>(null)
const currentChapter = ref<Chapter | null>(null)
const completedLessonIds = ref<Set<number>>(new Set())
const isMarking = ref(false)
const accessDenied = ref(false)
const accessDeniedMessage = ref('')

// Computed
const totalLessonCount = computed(() => {
  if (!course.value?.chapters) return 0
  return course.value.chapters.reduce((sum, ch) => sum + (ch.lessons?.length || 0), 0)
})

const completedCount = computed(() => completedLessonIds.value.size)

const overallProgress = computed(() => {
  if (totalLessonCount.value === 0) return 0
  return (completedCount.value / totalLessonCount.value) * 100
})

// Flatten lessons for prev/next navigation
const flatLessons = computed(() => {
  if (!course.value?.chapters) return []
  const result: { lesson: Lesson; chapter: Chapter }[] = []
  for (const ch of course.value.chapters) {
    for (const lesson of ch.lessons || []) {
      result.push({ lesson, chapter: ch })
    }
  }
  return result
})

const currentFlatIndex = computed(() => {
  if (!currentLesson.value) return -1
  return flatLessons.value.findIndex((f) => f.lesson.id === currentLesson.value!.id)
})

const prevLesson = computed(() => {
  const idx = currentFlatIndex.value
  return idx > 0 ? flatLessons.value[idx - 1] : null
})

const nextLesson = computed(() => {
  const idx = currentFlatIndex.value
  return idx >= 0 && idx < flatLessons.value.length - 1 ? flatLessons.value[idx + 1] : null
})

// Methods
const loadCourse = async () => {
  try {
    isLoading.value = true
    error.value = null
    accessDenied.value = false

    // Bước 1: Kiểm tra quyền truy cập (backend chặn nếu chưa mua)
    try {
      await verifyLearningAccess(courseId)
    } catch (accessErr: any) {
      const status = accessErr?.response?.status
      const message = accessErr?.response?.data?.message
      if (status === 403) {
        accessDenied.value = true
        accessDeniedMessage.value =
          message || 'Bạn chưa mua khóa học này. Vui lòng mua khóa học để truy cập nội dung.'
        return
      }
      // 401 = chưa đăng nhập
      if (status === 401) {
        accessDenied.value = true
        accessDeniedMessage.value = 'Bạn cần đăng nhập để truy cập khóa học này.'
        return
      }
      throw accessErr
    }

    // Bước 2: Load course detail
    const data = await getCourseDetail(String(courseId))
    course.value = data

    // Mở chương đầu tiên
    if (data.chapters?.length) {
      expandedChapters.value[0] = true
    }

    // Bước 3: Tải tiến độ
    await loadProgress()

    // Auto-select bài học đầu tiên chưa hoàn thành
    autoSelectLesson()
  } catch (err: any) {
    console.error('Error loading course:', err)
    error.value = 'Không thể tải khóa học. Vui lòng thử lại.'
  } finally {
    isLoading.value = false
  }
}

const goToCourseDetail = () => {
  router.push('/courses')
}

const loadProgress = async () => {
  try {
    const data = await getCourseProgress(courseId)
    progress.value = data

    // Use actual completed lesson IDs from backend instead of guessing
    const newSet = new Set<number>()
    if (data.chapters) {
      for (const chProgress of data.chapters) {
        if (chProgress.completedLessonIds && chProgress.completedLessonIds.length > 0) {
          chProgress.completedLessonIds.forEach((id: number) => newSet.add(id))
        }
      }
    }
    completedLessonIds.value = newSet
  } catch (err) {
    console.error('Error loading progress:', err)
  }
}

const autoSelectLesson = () => {
  // Chọn bài đầu tiên chưa hoàn thành
  for (const { lesson, chapter } of flatLessons.value) {
    if (!completedLessonIds.value.has(lesson.id)) {
      selectLesson(lesson, chapter)
      return
    }
  }
  // Nếu tất cả hoàn thành → chọn bài cuối
  if (flatLessons.value.length > 0) {
    const last = flatLessons.value[flatLessons.value.length - 1]
    selectLesson(last.lesson, last.chapter)
  }
}

const selectLesson = (lesson: Lesson, chapter: Chapter) => {
  currentLesson.value = lesson
  currentChapter.value = chapter

  // Mở chapter chứa lesson
  const cIdx = course.value?.chapters.findIndex((ch) => ch.id === chapter.id) ?? -1
  if (cIdx >= 0) {
    expandedChapters.value[cIdx] = true
  }
}

const toggleChapter = (idx: number) => {
  expandedChapters.value[idx] = !expandedChapters.value[idx]
}

const getChapterCompletedCount = (chapter: Chapter) => {
  return chapter.lessons.filter((l) => completedLessonIds.value.has(l.id)).length
}

const markComplete = async () => {
  if (!currentLesson.value || isMarking.value) return

  isMarking.value = true
  try {
    const result = await markLessonComplete(currentLesson.value.id)
    completedLessonIds.value.add(currentLesson.value.id)
    // Force reactivity
    completedLessonIds.value = new Set(completedLessonIds.value)

    if (result.courseProgress?.completed) {
      toastStore.success('🎉 Chúc mừng! Bạn đã hoàn thành toàn bộ khóa học!')

      // Tự động cấp chứng chỉ
      try {
        await issueCertificate(courseId)
        toastStore.success('🏆 Chứng chỉ đã được cấp! Vào mục "Chứng chỉ" trong Dashboard để xem và tải xuống.')
      } catch (certErr) {
        console.warn('Could not auto-issue certificate:', certErr)
      }
    } else {
      toastStore.success('✅ Đã hoàn thành bài học!')
    }
  } catch (err: any) {
    toastStore.error(err?.response?.data?.message || 'Không thể đánh dấu hoàn thành')
  } finally {
    isMarking.value = false
  }
}

const goToPrevLesson = () => {
  if (prevLesson.value) {
    selectLesson(prevLesson.value.lesson, prevLesson.value.chapter)
  }
}

const goToNextLesson = () => {
  if (nextLesson.value) {
    selectLesson(nextLesson.value.lesson, nextLesson.value.chapter)
  }
}

const onVideoEnded = () => {
  if (currentLesson.value && !completedLessonIds.value.has(currentLesson.value.id)) {
    markComplete()
  }
}

const goBack = () => {
  router.push('/dashboard/student')
}

const handleQuizCompleted = () => {
  if (currentLesson.value && !completedLessonIds.value.has(currentLesson.value.id)) {
    markComplete()
  }
}

const isYoutubeUrl = (url: string) => {
  return url.includes('youtube.com') || url.includes('youtu.be')
}

const getYoutubeEmbedUrl = (url: string) => {
  const match = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&]+)/)
  return match ? `https://www.youtube.com/embed/${match[1]}` : url
}

// Resolve URL cho file local upload (audio/, videos/...)
const getContentUrl = (path: string | null | undefined): string => {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return `http://localhost:8888/api/v1/files/${path}`
}

// Audio player state
const audioRef = ref<HTMLAudioElement | null>(null)
const isAudioPlaying = ref(false)
const audioCurrentTime = ref(0)
const audioDuration = ref(0)
const audioVolume = ref(1)
const isAudioMuted = ref(false)
const audioPlaybackRate = ref(1)

const toggleAudioPlay = () => {
  if (!audioRef.value) return
  if (isAudioPlaying.value) {
    audioRef.value.pause()
  } else {
    audioRef.value.play()
  }
}

const onAudioTimeUpdate = () => {
  if (audioRef.value) audioCurrentTime.value = audioRef.value.currentTime
}
const onAudioLoadedMetadata = () => {
  if (audioRef.value) audioDuration.value = audioRef.value.duration
}
const onAudioEnded = () => {
  isAudioPlaying.value = false
  onVideoEnded()
}
const seekAudio = (e: Event) => {
  const val = Number((e.target as HTMLInputElement).value)
  if (audioRef.value) audioRef.value.currentTime = val
  audioCurrentTime.value = val
}
const setAudioVolume = (e: Event) => {
  const val = Number((e.target as HTMLInputElement).value)
  audioVolume.value = val
  if (audioRef.value) audioRef.value.volume = val
  isAudioMuted.value = val === 0
}
const toggleMute = () => {
  if (!audioRef.value) return
  isAudioMuted.value = !isAudioMuted.value
  audioRef.value.muted = isAudioMuted.value
}
const setPlaybackRate = (rate: number) => {
  audioPlaybackRate.value = rate
  if (audioRef.value) audioRef.value.playbackRate = rate
}
const fmtTime = (s: number) => {
  if (!s || isNaN(s)) return '0:00'
  const m = Math.floor(s / 60)
  const sec = Math.floor(s % 60)
  return `${m}:${sec.toString().padStart(2, '0')}`
}

const formatDuration = (seconds?: number) => {
  if (!seconds) return ''
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  if (m < 60) return `${m}:${s.toString().padStart(2, '0')}`
  const h = Math.floor(m / 60)
  const rm = m % 60
  return `${h}:${rm.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

/* ── Globals ───────────────────────────────────────────────── */
.learning-page {
  font-family: 'Inter', sans-serif;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f8fafc;
  color: #1e293b;
  overflow: hidden;
}

/* ── Header ────────────────────────────────────────────────── */
.learning-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
  z-index: 20;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.back-btn:hover {
  background: #f1f5f9;
  color: #1e293b;
  border-color: #cbd5e1;
}

.course-title-text {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  flex-shrink: 0;
}

.progress-pill {
  position: relative;
  width: 180px;
  height: 28px;
  border-radius: 14px;
  background: #e2e8f0;
  border: 1px solid #cbd5e1;
  overflow: hidden;
}

.progress-pill-fill {
  position: absolute;
  inset: 2px;
  border-radius: 12px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  transition: width 0.5s ease;
}

.progress-pill-text {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 11px;
  font-weight: 600;
  color: #1e293b;
  text-shadow: none;
}

/* ── Loading / Error ───────────────────────────────────────── */
.loading-state,
.error-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: #64748b;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-icon {
  font-size: 48px;
}
.retry-btn {
  padding: 8px 24px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

/* ── Access Denied ─────────────────────────────────────────── */
.access-denied-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  padding: 24px;
}

.denied-card {
  max-width: 480px;
  width: 100%;
  text-align: center;
  padding: 48px 40px;
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.denied-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #fef2f2, #fee2e2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fecaca;
  animation: pulse-glow 2s infinite;
}

@keyframes pulse-glow {
  0%,
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.15);
  }
  50% {
    box-shadow: 0 0 20px 8px rgba(239, 68, 68, 0.08);
  }
}

.denied-icon svg {
  width: 36px;
  height: 36px;
  color: #ef4444;
}

.denied-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.denied-message {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 32px;
}

.denied-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.denied-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 28px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.25s ease;
  width: 100%;
  max-width: 280px;
}

.denied-btn.primary {
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
}
.denied-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.denied-btn.secondary {
  background: transparent;
  border: 1px solid #e2e8f0;
  color: #64748b;
}
.denied-btn.secondary:hover {
  background: #f1f5f9;
  color: #1e293b;
  border-color: #cbd5e1;
}

/* ── Body layout ───────────────────────────────────────────── */
.learning-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* ── Sidebar ───────────────────────────────────────────────── */
.curriculum-sidebar {
  width: 360px;
  background: #ffffff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
  position: relative;
}
.curriculum-sidebar.collapsed {
  width: 44px;
}

.sidebar-toggle-wrap {
  position: absolute;
  top: 8px;
  right: -16px;
  z-index: 10000;
}

.sidebar-toggle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.sidebar-toggle:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-header {
  padding: 16px 16px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e2e8f0;
}
.sidebar-header h3 {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}
.lesson-counter {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* ── Chapter list ──────────────────────────────────────────── */
.chapter-list {
  padding: 4px 0;
}

.chapter-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #1e293b;
  text-align: left;
  transition: background 0.15s;
  border-bottom: 1px solid #f1f5f9;
}
.chapter-header:hover {
  background: #f8fafc;
}

.chapter-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.chevron-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  transition: transform 0.2s;
  color: #94a3b8;
}
.chapter-header.expanded .chevron-icon {
  transform: rotate(90deg);
  color: #3b82f6;
}

.chapter-title {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chapter-progress-badge {
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
  flex-shrink: 0;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 10px;
}

/* ── Lesson items ──────────────────────────────────────────── */
.lesson-list {
  background: #fafbfc;
}

.lesson-item {
  width: 100%;
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 16px 10px 24px;
  border: none;
  background: transparent;
  cursor: pointer;
  text-align: left;
  color: #475569;
  transition: all 0.15s;
  border-left: 3px solid transparent;
}
.lesson-item:hover {
  background: #f1f5f9;
}
.lesson-item.active {
  background: rgba(59, 130, 246, 0.08);
  border-left-color: #3b82f6;
  color: #1e293b;
}
.lesson-item.completed {
  color: #94a3b8;
}

.lesson-status {
  flex-shrink: 0;
  margin-top: 2px;
}

.check-icon {
  width: 18px;
  height: 18px;
  color: #22c55e;
}

.unchecked-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid #cbd5e1;
  transition: all 0.2s;
}
.unchecked-circle.playing {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.lesson-info {
  flex: 1;
  min-width: 0;
}

.lesson-title {
  display: block;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 3px;
}

.content-type-badge {
  font-size: 11px;
  color: #94a3b8;
}

.lesson-duration {
  font-size: 11px;
  color: #94a3b8;
}

/* ── Content Viewer ────────────────────────────────────────── */
.content-viewer {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: #f8fafc;
}

.empty-viewer {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #94a3b8;
}
.empty-icon {
  font-size: 56px;
}
.empty-viewer h2 {
  color: #64748b;
  font-size: 20px;
}
.empty-viewer p {
  font-size: 14px;
}

.lesson-viewer {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

/* ── Video ─────────────────────────────────────────────────── */
.video-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f172a;
  min-height: 0;
}

.video-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.video-iframe,
.video-native {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* ── Reading ───────────────────────────────────────────────── */
.reading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: #ffffff;
}
.reading-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.reading-iframe {
  flex: 1;
  width: 100%;
  height: 100%;
  min-height: 600px;
  border: none;
  background: white;
}
.lesson-text-description {
  max-width: 900px;
  margin: 0 auto;
  padding: 48px 24px;
  background: transparent;
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  white-space: pre-wrap;
  word-break: break-word;
}

/* ── Audio Player ──────────────────────────────────────────── */
.audio-container {
  overflow-y: auto;
  background-color: #ffffff;
}

.audio-player-card {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64px 32px 40px;
  gap: 32px;
  width: 100%;
}

.audio-art {
  position: relative;
  width: 160px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-rings {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(52, 111, 219, 0.15);
  transition: all 0.3s;
}
.ring-1 {
  width: 160px;
  height: 160px;
}
.ring-2 {
  width: 120px;
  height: 120px;
  border-color: rgba(52, 111, 219, 0.2);
}
.ring-3 {
  width: 80px;
  height: 80px;
  border-color: rgba(52, 111, 219, 0.25);
}

.ring-active.ring-1 {
  animation: pulseRing 2s ease-in-out infinite;
  border-color: rgba(52, 111, 219, 0.4);
}
.ring-active.ring-2 {
  animation: pulseRing 2s ease-in-out 0.3s infinite;
  border-color: rgba(52, 111, 219, 0.5);
}
.ring-active.ring-3 {
  animation: pulseRing 2s ease-in-out 0.6s infinite;
  border-color: rgba(52, 111, 219, 0.6);
}

@keyframes pulseRing {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.08);
    opacity: 1;
  }
}

.audio-icon-center {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #346fdb, #1762d3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(52, 111, 219, 0.3);
  z-index: 1;
}

.audio-info {
  text-align: center;
}
.audio-type-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
}
.audio-title {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
  color: #1e293b;
  max-width: 560px;
}

.audio-no-file {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 14px;
}

.audio-controls {
  width: 100%;
  max-width: 560px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.audio-progress-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}
.audio-time {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  font-variant-numeric: tabular-nums;
  min-width: 40px;
  text-align: center;
}
.audio-progress-bar {
  flex: 1;
  position: relative;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: visible;
}
.audio-progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, #346fdb, #1762d3);
  border-radius: 4px;
  pointer-events: none;
  transition: width 0.1s linear;
}
.audio-range {
  position: absolute;
  inset: -8px 0;
  width: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
  height: 24px;
}
.audio-progress-bar:hover .audio-progress-fill {
  background: linear-gradient(90deg, #4b84ed, #2b74e2);
}

.audio-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.audio-ctrl-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 10px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}
.audio-ctrl-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.audio-play-btn {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #346fdb, #1762d3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(52, 111, 219, 0.4);
  transition: all 0.2s;
}
.audio-play-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 12px 32px rgba(52, 111, 219, 0.5);
}

.audio-extras {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.audio-vol {
  display: flex;
  align-items: center;
  gap: 8px;
}
.audio-icon-btn {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.audio-icon-btn:hover {
  color: #475569;
}

.audio-vol-range {
  width: 80px;
  accent-color: #346fdb;
  cursor: pointer;
}

.audio-speed {
  display: flex;
  align-items: center;
  gap: 8px;
}
.audio-speed-label {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}
.audio-speed-btn {
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  background: white;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.audio-speed-btn:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}
.audio-speed-btn.active {
  background: #eff6ff;
  border-color: #346fdb;
  color: #346fdb;
  font-weight: 600;
}

.audio-description {
  margin: 32px auto 0;
  padding-top: 32px;
  border-top: 1px solid #f1f5f9;
  width: 100%;
  max-width: 900px;
}
.audio-description h3 {
  color: #64748b;
  margin-bottom: 16px;
}
.audio-description .lesson-text-description {
  background: #f8fafc;
  color: #334155;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #e2e8f0;
}

.quiz-container {
  flex: 1;
  display: flex;
  overflow-y: auto;
}
.quiz-placeholder {
  text-align: center;
  color: #94a3b8;
}
.quiz-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.quiz-placeholder h3 {
  color: #64748b;
  font-size: 18px;
}

/* ── No content ────────────────────────────────────────────── */
.no-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 14px;
}

/* ── Lesson footer ─────────────────────────────────────────── */
.lesson-footer {
  flex-shrink: 0;
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
}

.lesson-header-info {
  margin-bottom: 12px;
}

.current-chapter-label {
  font-size: 12px;
  color: #3b82f6;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.current-lesson-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-top: 2px;
}

.lesson-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}
.action-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.action-btn.secondary {
  background: transparent;
  border-color: #e2e8f0;
  color: #64748b;
}
.action-btn.secondary:hover:not(:disabled) {
  background: #f1f5f9;
  color: #1e293b;
}

.action-btn.primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border-color: transparent;
}
.action-btn.primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.action-btn.complete {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
}
.action-btn.complete:hover:not(:disabled) {
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.completed-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f0fdf4;
  color: #16a34a;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid #bbf7d0;
}

/* ── Scrollbar ─────────────────────────────────────────────── */
.sidebar-content::-webkit-scrollbar {
  width: 4px;
}
.sidebar-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
.sidebar-content::-webkit-scrollbar-track {
  background: transparent;
}

/* ── Helper ────────────────────────────────────────────────── */
.w-4 {
  width: 16px;
  height: 16px;
}
.w-5 {
  width: 20px;
  height: 20px;
}
.animate-spin {
  animation: spin 1s linear infinite;
}

/* ── Responsive ────────────────────────────────────────────── */
@media (max-width: 768px) {
  .curriculum-sidebar {
    width: 280px;
  }
  .curriculum-sidebar.collapsed {
    width: 0;
    border-right: none;
  }
  .sidebar-content {
    display: block;
  }
  .progress-pill {
    width: 140px;
  }
  .lesson-actions {
    flex-wrap: wrap;
  }
}
</style>
