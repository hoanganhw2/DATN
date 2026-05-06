<script setup lang="ts">
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { Send, MessageCircle, X, Plus, Bot, LogIn } from 'lucide-vue-next'
import { marked } from 'marked'

const chatStore = useChatStore()
const authStore = useAuthStore()
const router = useRouter()
const isOpen = ref(false)
const messageInput = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

const isAuthenticated = computed(() => authStore.isAuthenticated)

onMounted(async () => {
  if (isAuthenticated.value) {
    await chatStore.fetchSessions()
  }
})

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && isAuthenticated.value) {
    scrollToBottom()
  }
}

const goToLogin = () => {
  isOpen.value = false
  router.push('/login')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(
  () => chatStore.messages.length,
  () => {
    if (isOpen.value) {
      scrollToBottom()
    }
  },
)

const handleSend = async () => {
  if (!messageInput.value.trim() || chatStore.isTyping) return
  const text = messageInput.value
  messageInput.value = ''
  await chatStore.sendMessage(text)
}

const createNewSession = () => {
  chatStore.createNewSession()
}

const formatMarkdown = (text: string) => {
  if (!text) return ''
  try {
    // marked v12+ returns a promise if not configured correctly, 
    // but usually it's sync. We force it to be a string.
    const html = marked.parse(text)
    return typeof html === 'string' ? html : text.replace(/\n/g, '<br/>')
  } catch (e) {
    console.error('Markdown parse error:', e)
    return text.replace(/\n/g, '<br/>')
  }
}
</script>

<template>
  <div class="fixed bottom-6 right-6 z-[9999] flex flex-col items-end font-sans">
    <!-- Chat Window -->
    <div
      v-show="isOpen"
      class="w-[360px] sm:w-[420px] h-[580px] max-h-[85vh] bg-[#f8fafc] rounded-[24px] shadow-[0_12px_40px_rgba(0,0,0,0.12)] flex flex-col mb-4 overflow-hidden border border-gray-200/50 transition-all duration-300 origin-bottom-right ring-1 ring-black/5"
    >
      <!-- Header -->
      <div
        class="bg-gradient-to-r from-indigo-600 via-blue-600 to-blue-500 px-5 py-4 flex items-center justify-between text-white relative overflow-hidden"
      >
        <!-- Decorative background elements -->
        <div
          class="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full blur-2xl -mr-10 -mt-10"
        ></div>
        <div
          class="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full blur-xl -ml-10 -mb-10"
        ></div>

        <div class="relative z-10 flex items-center space-x-3">
          <div
            class="w-11 h-11 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center border border-white/30 shadow-sm relative"
          >
            <Bot class="w-6 h-6 text-white drop-shadow-sm" />
            <span
              v-if="isAuthenticated"
              class="absolute bottom-0 right-0 w-3 h-3 bg-green-400 border-2 border-blue-600 rounded-full"
            ></span>
          </div>
          <div>
            <h3 class="font-bold text-[16px] tracking-wide drop-shadow-sm">EnglishPro AI</h3>
            <p
              class="text-[12px] text-blue-50 font-medium opacity-90 flex items-center gap-1.5 mt-0.5"
            >
              <span v-if="isAuthenticated" class="relative flex h-2 w-2">
                <span
                  class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-300 opacity-75"
                ></span>
                <span class="relative inline-flex rounded-full h-2 w-2 bg-green-400"></span>
              </span>
              {{ isAuthenticated ? 'Đang hỗ trợ trực tuyến' : 'Cần đăng nhập' }}
            </p>
          </div>
        </div>
        <div class="relative z-10 flex items-center space-x-1">
          <button
            v-if="isAuthenticated"
            @click="createNewSession"
            class="p-2 hover:bg-white/20 rounded-xl transition-all duration-200"
            title="Đoạn chat mới"
          >
            <Plus class="w-[18px] h-[18px] text-white" />
          </button>
          <button
            @click="toggleChat"
            class="p-2 hover:bg-white/20 rounded-xl transition-all duration-200"
          >
            <X class="w-[20px] h-[20px] text-white" />
          </button>
        </div>
      </div>

      <!-- Not Authenticated State -->
      <div
        v-if="!isAuthenticated"
        class="flex-1 flex flex-col items-center justify-center p-8 space-y-5 bg-white relative"
      >
        <div
          class="absolute inset-0 bg-[radial-gradient(#e5e7eb_1px,transparent_1px)] [background-size:16px_16px] opacity-30"
        ></div>

        <div
          class="relative w-20 h-20 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-full flex items-center justify-center shadow-inner border border-blue-100"
        >
          <Bot class="w-10 h-10 text-indigo-500" />
        </div>
        <div class="text-center space-y-2 relative z-10">
          <h4 class="font-bold text-gray-800 text-[16px]">AI Tutor đang chờ bạn</h4>
          <p class="text-gray-500 text-[13px] leading-relaxed px-2">
            Đăng nhập ngay để mở khóa trợ lý AI cá nhân, giải đáp mọi thắc mắc tiếng Anh của bạn.
          </p>
        </div>
        <button
          @click="goToLogin"
          class="relative z-10 mt-2 flex items-center space-x-2 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white px-6 py-3 rounded-2xl text-[14px] font-semibold transition-all shadow-[0_4px_14px_rgba(79,70,229,0.3)] hover:shadow-[0_6px_20px_rgba(79,70,229,0.4)] active:scale-95"
        >
          <LogIn class="w-4 h-4" />
          <span>Đăng nhập để Chat</span>
        </button>
      </div>

      <!-- Authenticated: Messages Area -->
      <div
        v-else
        class="flex-1 overflow-y-auto p-5 space-y-5 bg-[#f8fafc] relative scroll-smooth"
        ref="messagesContainer"
      >
        <!-- Grid background for a modern feel -->
        <div
          class="absolute inset-0 bg-[radial-gradient(#cbd5e1_1px,transparent_1px)] [background-size:20px_20px] opacity-20 pointer-events-none"
        ></div>

        <div
          v-if="chatStore.messages.length === 0"
          class="flex flex-col items-center justify-center h-full text-gray-500 space-y-4 opacity-90 relative z-10"
        >
          <div
            class="w-16 h-16 bg-white rounded-2xl flex items-center justify-center shadow-sm border border-gray-100 rotate-3 transition-transform hover:rotate-6"
          >
            <Bot class="w-8 h-8 text-indigo-500" />
          </div>
          <div
            class="bg-white/80 backdrop-blur-sm px-5 py-3 rounded-2xl shadow-sm border border-gray-100/50"
          >
            <p class="text-center text-[13px] font-medium text-gray-600">
              Xin chào! 👋 Cần giúp đỡ với ngữ pháp, dịch thuật hay bài tập IELTS?
            </p>
          </div>
        </div>

        <div
          v-for="msg in chatStore.messages"
          :key="msg.id"
          :class="[
            'flex w-full relative z-10',
            msg.role === 'user' ? 'justify-end' : 'justify-start',
          ]"
        >
          <div :class="['flex max-w-[85%]', msg.role === 'user' ? 'flex-row-reverse' : 'flex-row']">
            <!-- Bot Avatar aligned to the bottom of the bubble -->
            <div
              v-if="msg.role === 'model'"
              class="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center mr-2.5 bg-gradient-to-br from-indigo-100 to-blue-50 shadow-sm border border-indigo-100/50 self-end mb-1"
            >
              <Bot class="w-4 h-4 text-indigo-600" />
            </div>

            <div
              :class="[
                'px-4 py-3 shadow-sm text-[14px] leading-[1.6] break-words styled-markdown',
                msg.role === 'user'
                  ? 'bg-gradient-to-br from-indigo-600 to-blue-600 text-white rounded-[20px] rounded-br-[4px] ml-12'
                  : 'bg-white border border-gray-100 text-gray-800 rounded-[20px] rounded-bl-[4px] shadow-[0_2px_8px_rgba(0,0,0,0.04)]',
              ]"
              v-html="
                msg.role === 'model'
                  ? formatMarkdown(msg.content || '')
                  : (msg.content || '').replace(/\n/g, '<br/>')
              "
            ></div>
          </div>
        </div>

        <!-- Typing indicator -->
        <div v-if="chatStore.isTyping" class="flex w-full justify-start relative z-10">
          <div class="flex flex-row max-w-[85%]">
            <div
              class="flex-shrink-0 w-8 h-8 rounded-full bg-gradient-to-br from-indigo-100 to-blue-50 flex items-center justify-center mr-2.5 shadow-sm border border-indigo-100/50 self-end mb-1"
            >
              <Bot class="w-4 h-4 text-indigo-600" />
            </div>
            <div
              class="px-5 py-3.5 bg-white border border-gray-100 text-gray-800 rounded-[20px] rounded-bl-[4px] shadow-[0_2px_8px_rgba(0,0,0,0.04)] flex space-x-1.5 items-center"
            >
              <div
                class="w-1.5 h-1.5 bg-indigo-400 rounded-full animate-bounce"
                style="animation-delay: 0s"
              ></div>
              <div
                class="w-1.5 h-1.5 bg-indigo-400 rounded-full animate-bounce"
                style="animation-delay: 0.15s"
              ></div>
              <div
                class="w-1.5 h-1.5 bg-indigo-400 rounded-full animate-bounce"
                style="animation-delay: 0.3s"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area (only when authenticated) -->
      <div
        v-if="isAuthenticated"
        class="bg-white p-4 border-t border-gray-100 z-10 rounded-b-[24px]"
      >
        <form @submit.prevent="handleSend" class="flex items-end space-x-2.5">
          <div
            class="flex-1 bg-[#f1f5f9] rounded-[20px] flex items-center px-4 py-2 border-2 border-transparent focus-within:border-indigo-100 focus-within:bg-white focus-within:shadow-[0_0_0_4px_rgba(79,70,229,0.05)] transition-all duration-200"
          >
            <textarea
              v-model="messageInput"
              @keydown.enter.exact.prevent="handleSend"
              rows="1"
              class="focus:outline-none w-full bg-transparent border-none focus:ring-0 resize-none py-1.5 text-[14px] text-gray-800 placeholder-gray-400 scrollbar-hide"
              placeholder="Nhắn tin cho AI Tutor..."
              style="min-height: 40px; max-height: 100px"
            ></textarea>
          </div>
          <button
            type="submit"
            :disabled="!messageInput.trim() || chatStore.isTyping"
            class="p-2.5 rounded-full bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-50 disabled:bg-gray-300 transition-all shadow-[0_2px_8px_rgba(79,70,229,0.3)] active:scale-95 flex-shrink-0 flex items-center justify-center h-[42px] w-[42px] mb-[2px]"
          >
            <Send class="w-[18px] h-[18px] ml-0.5" />
          </button>
        </form>
        <!-- Footnote -->
        <div class="text-center mt-2">
          <span class="text-[10px] text-gray-400"
            >AI có thể mắc lỗi. Hãy kiểm tra thông tin quan trọng.</span
          >
        </div>
      </div>
    </div>

    <!-- Floating Button -->
    <button
      @click="toggleChat"
      :class="[
        'w-14 h-14 rounded-full flex items-center justify-center text-white shadow-[0_8px_20px_rgba(0,0,0,0.15)] transition-all duration-300 hover:scale-110 hover:shadow-[0_12px_25px_rgba(79,70,229,0.4)] active:scale-95 z-50',
        isOpen
          ? 'bg-gray-800 rotate-90'
          : 'bg-gradient-to-tr from-indigo-600 via-blue-600 to-blue-500 animate-pulse-glow rotate-0',
      ]"
    >
      <X v-if="isOpen" class="w-6 h-6" />
      <MessageCircle v-else class="w-6 h-6 fill-white opacity-90" />
    </button>
  </div>
</template>

<style>
/* Scoped styles applied via deep selector equivalent for v-html content */
.styled-markdown strong {
  color: inherit;
  font-weight: 700;
}
.styled-markdown em {
  font-style: italic;
  opacity: 0.9;
}
.styled-markdown pre {
  background-color: #1e293b !important;
  color: #f8fafc !important;
  padding: 10px 12px;
  border-radius: 12px;
  margin: 8px 0;
  overflow-x: auto;
  font-size: 13px;
  border: 1px solid #334155;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}
.styled-markdown code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}
.styled-markdown table {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  font-size: 13px;
}
.styled-markdown th {
  background-color: rgba(99, 102, 241, 0.1);
  color: #4f46e5;
  font-weight: 600;
  text-align: left;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
}
.styled-markdown td {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
}
.styled-markdown ul {
  list-style-type: disc;
  padding-left: 20px;
  margin: 8px 0;
}
.styled-markdown ol {
  list-style-type: decimal;
  padding-left: 20px;
  margin: 8px 0;
}
.styled-markdown p {
  margin-bottom: 8px;
}
.styled-markdown p:last-child {
  margin-bottom: 0;
}
</style>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  width: 4px;
}
.scrollbar-hide::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 4px;
}

@keyframes pulse-glow {
  0% {
    box-shadow: 0 0 0 0 rgba(79, 70, 229, 0.4);
  }
  70% {
    box-shadow: 0 0 0 15px rgba(79, 70, 229, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(79, 70, 229, 0);
  }
}

.animate-pulse-glow {
  animation: pulse-glow 2.5s infinite;
}

/* Base custom font mapping if available globally */
* {
  font-family:
    'Inter',
    system-ui,
    -apple-system,
    sans-serif;
}
</style>
