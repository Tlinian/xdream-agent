import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: number
}

interface ChatState {
  messages: Message[]
  currentSessionId: string
  isLoading: boolean
  addMessage: (message: Message) => void
  setLoading: (loading: boolean) => void
  clearMessages: () => void
  setCurrentSession: (sessionId: string) => void
}

export const useChatStore = create<ChatState>()(
  persist(
    (set) => ({
      messages: [],
      currentSessionId: '',
      isLoading: false,
      addMessage: (message) => set((state) => ({ 
        messages: [...state.messages, message] 
      })),
      setLoading: (isLoading) => set({ isLoading }),
      clearMessages: () => set({ messages: [] }),
      setCurrentSession: (currentSessionId) => set({ currentSessionId })
    }),
    {
      name: 'chat-store'
    }
  )
)