<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { useChatStore } from '@/stores/chat'
import { Send, Plus, MessageCircle, User, Bot } from 'lucide-vue-next'

const chatStore = useChatStore()
const messageInput = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

onMounted(async () => {
    await chatStore.fetchSessions()
})

const scrollToBottom = async () => {
    await nextTick()
    if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
}

watch(() => chatStore.messages.length, () => {
    scrollToBottom()
})

const handleSend = async () => {
    if (!messageInput.value.trim() || chatStore.isTyping) return
    const text = messageInput.value
    messageInput.value = ''
    await chatStore.sendMessage(text)
}

const selectSession = async (id: number) => {
    if (chatStore.currentSessionId === id) return
    await chatStore.fetchMessages(id)
}

const formatMarkdown = (text: string) => {
    // A very basic markdown faking for bold and pre/code
    if (!text) return ''
    let html = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    html = html.replace(/\*(.*?)\*/g, '<em>$1</em>')
    html = html.replace(/```([\s\S]*?)```/g, '<pre class="bg-gray-800 text-white p-3 rounded-md my-2 overflow-x-auto"><code>$1</code></pre>')
    html = html.replace(/\n/g, '<br/>')
    return html
}
</script>

<template>
    <div class="flex h-[calc(100vh-64px)] bg-gray-50 overflow-hidden">
        <!-- Sidebar -->
        <div class="w-64 bg-white border-r border-gray-200 flex flex-col hidden md:flex">
            <div class="p-4 border-b border-gray-200">
                <button @click="chatStore.createNewSession"
                    class="w-full flex items-center justify-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
                    <Plus class="w-5 h-5" />
                    <span>Đoạn chat mới</span>
                </button>
            </div>
            <div class="flex-1 overflow-y-auto p-3 space-y-2">
                <div v-for="session in chatStore.sessions" :key="session.id" @click="selectSession(session.id)"
                    :class="[
                        'flex items-center space-x-3 p-3 rounded-lg cursor-pointer transition-colors',
                        chatStore.currentSessionId === session.id ? 'bg-blue-50 text-blue-700' : 'hover:bg-gray-100 text-gray-700'
                    ]">
                    <MessageCircle class="w-5 h-5 shrink-0" />
                    <span class="truncate text-sm font-medium">{{ session.title }}</span>
                </div>
                <div v-if="chatStore.sessions.length === 0 && !chatStore.isLoading"
                    class="text-center text-gray-500 text-sm mt-4">
                    Chưa có lịch sử trò chuyện
                </div>
            </div>
        </div>

        <!-- Main Chat Area -->
        <div class="flex-1 flex flex-col h-full bg-white relative">
            <div class="flex-1 overflow-y-auto p-4 md:p-6 space-y-6" ref="messagesContainer">
                <div v-if="chatStore.messages.length === 0" class="flex flex-col items-center justify-center h-full text-gray-500 space-y-4">
                    <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
                        <Bot class="w-8 h-8 text-blue-600" />
                    </div>
                    <h2 class="text-xl font-semibold text-gray-800">EnglishPro AI Tutor</h2>
                    <p class="text-center max-w-md">Xin chào! Tôi có thể giúp bạn giải đáp các vấn đề ngữ pháp, từ vựng hoặc luyện tập tiếng Anh. Hãy bắt đầu bằng cách gửi một tin nhắn phía dưới.</p>
                </div>
                
                <div v-for="msg in chatStore.messages" :key="msg.id"
                    :class="['flex w-full', msg.role === 'user' ? 'justify-end' : 'justify-start']">
                    <div :class="['flex max-w-[85%] md:max-w-[75%]', msg.role === 'user' ? 'flex-row-reverse' : 'flex-row']">
                        <div :class="['flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center mx-2', msg.role === 'user' ? 'bg-gray-200' : 'bg-blue-100']">
                            <User v-if="msg.role === 'user'" class="w-5 h-5 text-gray-600" />
                            <Bot v-else class="w-5 h-5 text-blue-600" />
                        </div>
                        <div :class="['px-4 py-3 rounded-2xl shadow-sm text-[15px] leading-relaxed', msg.role === 'user' ? 'bg-blue-600 text-white rounded-tr-sm' : 'bg-gray-100 text-gray-800 rounded-tl-sm']"
                             v-html="msg.role === 'model' ? formatMarkdown(msg.content) : msg.content.replace(/\n/g, '<br/>')">
                        </div>
                    </div>
                </div>

                <div v-if="chatStore.isTyping" class="flex w-full justify-start">
                    <div class="flex flex-row max-w-[85%] md:max-w-[75%]">
                        <div class="flex-shrink-0 w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center mx-2">
                            <Bot class="w-5 h-5 text-blue-600" />
                        </div>
                        <div class="px-4 py-3 bg-gray-100 rounded-2xl rounded-tl-sm flex space-x-1 items-center h-10 shadow-sm">
                            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0s;"></div>
                            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.15s;"></div>
                            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.3s;"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Input Area -->
            <div class="border-t border-gray-200 bg-white p-4">
                <form @submit.prevent="handleSend" class="flex items-end space-x-2 max-w-4xl mx-auto relative">
                    <button type="button" @click="chatStore.createNewSession" class="md:hidden p-3 text-gray-500 hover:text-blue-600 bg-gray-100 rounded-xl">
                        <Plus class="w-6 h-6" />
                    </button>
                    <div class="flex-1 bg-gray-100 rounded-xl flex items-center px-4 py-2 border border-transparent focus-within:border-blue-400 focus-within:ring-2 focus-within:ring-blue-100 transition-all">
                        <textarea
                            v-model="messageInput"
                            @keydown.enter.exact.prevent="handleSend"
                            rows="1"
                            class="w-full bg-transparent border-none focus:ring-0 resize-none py-2 text-gray-700 placeholder-gray-500 max-h-32"
                            placeholder="Hỏi AI Tutor điều gì đó..."
                            style="min-height: 44px;"
                        ></textarea>
                    </div>
                    <button
                        type="submit"
                        :disabled="!messageInput.trim() || chatStore.isTyping"
                        class="p-3 rounded-xl bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-sm flex-shrink-0 flex items-center justify-center h-[44px] w-[44px]"
                    >
                        <Send class="w-5 h-5" />
                    </button>
                </form>
                <div class="text-center mt-2 text-xs text-gray-400">AI Tutor có thể mắc lỗi. Hãy kiểm tra lại những thông tin quan trọng.</div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* Scoped styles */
textarea::-webkit-scrollbar {
    width: 6px;
}
textarea::-webkit-scrollbar-thumb {
    background-color: #cbd5e1;
    border-radius: 4px;
}
</style>
