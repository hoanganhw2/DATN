import { defineStore } from 'pinia'
import api from '@/api/axios'

interface ChatSession {
  id: number
  title: string
  createdAt: string
  updatedAt: string
}

interface ChatMessage {
  id: number
  sessionId: number
  role: 'user' | 'model'
  content: string
  createdAt: string
}

interface ChatState {
  sessions: ChatSession[]
  currentSessionId: number | null
  messages: ChatMessage[]
  isLoading: boolean
  isTyping: boolean
}

export const useChatStore = defineStore('chat', {
  state: (): ChatState => ({
    sessions: [],
    currentSessionId: null,
    messages: [],
    isLoading: false,
    isTyping: false
  }),
  
  actions: {
    async fetchSessions() {
      this.isLoading = true
      try {
        const response = await api.get('/chat/sessions')
        this.sessions = response.data.data ?? []
        
        // If there are existing sessions and no session is currently active, load the most recent one
        if (this.sessions.length > 0 && !this.currentSessionId) {
          await this.fetchMessages(this.sessions[0].id)
        }
      } catch (error) {
        console.error('Failed to fetch sessions', error)
      } finally {
        this.isLoading = false
      }
    },
    
    async fetchMessages(sessionId: number) {
      this.isLoading = true
      try {
        const response = await api.get(`/chat/sessions/${sessionId}/messages`)
        this.messages = response.data.data ?? []
        this.currentSessionId = sessionId
      } catch (error) {
        console.error('Failed to fetch messages', error)
      } finally {
        this.isLoading = false
      }
    },
    
    async sendMessage(text: string) {
      if (!text.trim()) return
      
      // Optimistic UI update
      const tempUserMessage: ChatMessage = {
        id: Date.now(),
        sessionId: this.currentSessionId || 0,
        role: 'user',
        content: text,
        createdAt: new Date().toISOString()
      }
      this.messages.push(tempUserMessage)
      
      this.isTyping = true
      
      try {
        const response = await api.post('/chat', {
          sessionId: this.currentSessionId,
          message: text
        })
        
        const responseData = response.data.data
        console.log('Backend responseData:', responseData)
        const botReply = responseData.reply || 'Dạ, hiện tại AI đang gặp chút gián đoạn. Bạn thử lại nhé!'
        const newSessionId = responseData.sessionId
        
        // If it was a new session, update state and refresh sessions list
        if (!this.currentSessionId || this.currentSessionId !== newSessionId) {
          this.currentSessionId = newSessionId
          await this.fetchSessions()
        }
        
        this.messages.push({
          id: Date.now() + 1,
          sessionId: this.currentSessionId!,
          role: 'model',
          content: botReply,
          createdAt: new Date().toISOString()
        })
      } catch (error) {
        console.error('Failed to send message', error)
        // Optionally remove the optimistically added message
        this.messages.pop()
      } finally {
        this.isTyping = false
      }
    },
    
    createNewSession() {
      this.currentSessionId = null
      this.messages = []
    }
  }
})
