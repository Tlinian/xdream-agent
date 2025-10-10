// 聊天消息接口
export interface ChatMessage {
  id: string
  role: 'user' | 'assistant' | 'system'
  content: string
  timestamp: Date
  conversationId?: string
  model?: string
  tokens?: number
}

// 聊天请求接口
export interface ChatRequest {
  message: string
  conversationId?: string
  model?: string
  modelType?: string
  temperature?: number
  maxTokens?: number
  topP?: number
  frequencyPenalty?: number
  presencePenalty?: number
  stream?: boolean
  useReAct?: boolean
  messages?: Array<{
    role: string
    content: string
  }>
}

// 聊天响应接口
export interface ChatResponse {
  id: string
  content: string
  conversationId: string
  model: string
  tokens: {
    prompt: number
    completion: number
    total: number
  }
  timestamp: string
}

// 对话历史接口
export interface Conversation {
  id: string
  title: string
  lastMessage: string
  lastMessageTime: Date
  messageCount: number
  isPinned?: boolean
}

// 模型配置接口
export interface ModelConfig {
  model: string
  name: string
  description: string
  maxTokens: number
  temperature: number
  topP: number
  enabled: boolean
}

// 聊天设置接口
export interface ChatSettings {
  defaultModel: string
  temperature: number
  maxTokens: number
  enableStream: boolean
  enableHistory: boolean
  maxHistoryMessages: number
  systemPrompt?: string
}