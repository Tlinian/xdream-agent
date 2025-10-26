import { ChatRequest, ChatResponse } from '@interfaces/chat'
import { request } from '@utils/request'

export interface StreamResponse {
  streamId: string
  modelType: string
  content: string
  finished: boolean
  finishReason?: string
  tokenUsage?: number
}

class ChatService {
  private baseUrl = '/api';

  /**
   * 发送聊天消息
   */
  async sendMessage(requestData: ChatRequest): Promise<ChatResponse> {
    try {
      const response = await request.post<ChatResponse>(`${this.baseUrl}/llm/chat/simple`, requestData)
      return response.data
    } catch (error) {
      console.error('发送聊天消息失败:', error)
      throw error
    }
  }

  /**
   * 聊天接口（兼容ReAct模式）
   */
  async chat(requestData: ChatRequest): Promise<ChatResponse> {
    try {
      const response = await request.post<ChatResponse>(`${this.baseUrl}/chat`, requestData, {
        headers: {
          'X-User-Id': 'test-user-001' // 临时使用测试用户ID
        }
      })
      return response.data
    } catch (error) {
      console.error('聊天接口调用失败:', error)
      throw error
    }
  }

  /**
   * 流式聊天接口（支持ReAct模式思考过程展示与知识检索）
   */
  streamChat(
    requestData: ChatRequest,
    onMessage: (response: StreamResponse) => void,
    onError?: (error: Error) => void,
    onComplete?: () => void
  ): EventSource {
    console.log('[streamChat] 开始调用，参数:', requestData)
    try {
      // 构建URL参数
      const params = new URLSearchParams({
        message: requestData.message,
        modelType: requestData.modelType || 'gpt-4',
        temperature: String(requestData.temperature || 0.7),
        maxTokens: String(requestData.maxTokens || 2048),
        userId: 'test-user-001'
      })

      // 如果有系统提示词
      if (requestData.messages && requestData.messages.length > 0) {
        const systemMessage = requestData.messages.find(msg => msg.role === 'system')
        if (systemMessage) {
          params.append('systemPrompt', systemMessage.content)
        }
      }

      // 启用ReAct模式
      if (requestData.useReAct) {
        params.append('useReAct', 'true')
      }

      // 知识检索配置
      if (requestData.knowledge && requestData.knowledge.enabled) {
        const k = requestData.knowledge
        if (k?.knowledgeBaseIds && k.knowledgeBaseIds.length > 0) {
          params.append('knowledgeBaseIds', k.knowledgeBaseIds.join(','))
        } else if (k?.knowledgeBaseId) {
          params.append('knowledgeBaseId', k.knowledgeBaseId)
        }
        if (typeof k?.topK === 'number') params.append('topK', String(k.topK))
        if (typeof k?.similarityThreshold === 'number') params.append('similarityThreshold', String(k.similarityThreshold))
        if (typeof k?.useRerank === 'boolean') params.append('useRerank', k.useRerank ? 'true' : 'false')
        if (typeof k?.rerankTopK === 'number') params.append('rerankTopK', String(k.rerankTopK))
        if (typeof k?.appendCitations === 'boolean') params.append('appendCitations', k.appendCitations ? 'true' : 'false')
      }

      // 创建SSE连接 - 使用代理地址避免CORS问题
      const url = `/api/llm/chat/stream?${params.toString()}`
      
      console.log('[SSE] 发起请求:', url)
      console.log('[SSE] 请求参数:', Object.fromEntries(params))
      console.log('[SSE] 完整URL:', window.location.origin + url)
      
      // 测试：发送到捕获服务器来验证请求是否真的发出
      try {
        const testUrl = `http://localhost:3003/capture?${params.toString()}`
        console.log('[TEST] 发送到捕获服务器:', testUrl)
        fetch(testUrl, { 
          method: 'GET',
          headers: {
            'X-User-Id': 'test-user-001'
          }
        }).then(response => {
          console.log('[TEST] 捕获服务器响应:', response.status)
        }).catch(err => {
          console.log('[TEST] 捕获服务器请求失败:', err)
        })
      } catch (testErr) {
        console.log('[TEST] 捕获服务器测试失败:', testErr)
      }
      
      // 使用fetch API实现SSE连接，以支持代理
      const eventSource: any = {
        readyState: 0, // CONNECTING
        onopen: null,
        onmessage: null,
        onerror: null,
        close: function() {
          console.log('[SSE] 关闭连接');
          this.readyState = 2; // CLOSED
          if (this.abortController) {
            this.abortController.abort();
          }
          if (this.heartbeatInterval) {
            clearInterval(this.heartbeatInterval);
          }
        },
        addEventListener: function(eventType: string, handler: any) {
          if (eventType === 'complete') {
            this._completeHandler = handler;
          }
        },
        _completeHandler: null as any,
        abortController: null as AbortController | null,
        heartbeatInterval: null as any
      };
      
      const abortController = new AbortController();
      eventSource.abortController = abortController;
      
      fetch(url, {
        method: 'GET',
        headers: {
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
          'X-User-Id': 'test-user-001'
        },
        signal: abortController.signal,
      }).then(response => {
        if (!response.ok) {
          throw new Error(`SSE连接失败：${response.status}`)
        }
        console.log('[SSE] 连接成功，状态:', response.status)
        eventSource.readyState = 1 // OPEN

        const reader = response.body?.getReader()
        const decoder = new TextDecoder('utf-8')

        const processStream = async () => {
          if (!reader) return
          let buffer = ''
          while (true) {
            const { done, value } = await reader.read()
            if (done) {
              console.log('[SSE] 服务器已关闭连接')
              eventSource.readyState = 2 // CLOSED
              onComplete && onComplete()
              break
            }
            const chunk = decoder.decode(value, { stream: true })
            buffer += chunk
            const events = buffer.split('\n\n')
            buffer = events.pop() || ''

            for (const event of events) {
              const lines = event.split('\n')
              const dataLine = lines.find(line => line.startsWith('data:'))
              if (dataLine) {
                try {
                  const dataStr = dataLine.replace(/^data:\s*/, '')
                  const json = JSON.parse(dataStr)
                  onMessage(json)
                } catch (err) {
                  console.warn('[SSE] 数据解析失败:', err)
                }
              }
            }
          }
        }

        processStream().catch(err => {
          console.error('[SSE] 读取流失败:', err)
          onError && onError(err instanceof Error ? err : new Error(String(err)))
        })
      }).catch(err => {
        console.error('[SSE] 连接失败:', err)
        onError && onError(err instanceof Error ? err : new Error(String(err)))
      })

      return eventSource as EventSource
    } catch (error) {
      console.error('streamChat 调用失败:', error)
      if (onError) {
        onError(error as Error)
      }
      throw error
    }
  }

  /**
   * 获取对话列表
   */
  async getConversations(): Promise<any[]> {
    try {
      const response = await request.get<any[]>(`${this.baseUrl}/conversations`)
      return response.data
    } catch (error) {
      console.error('获取对话列表失败:', error)
      throw error
    }
  }

  /**
   * 删除对话
   */
  async deleteConversation(conversationId: string): Promise<void> {
    try {
      await request.delete(`${this.baseUrl}/conversation/${conversationId}`)
    } catch (error) {
      console.error('删除对话失败:', error)
      throw error
    }
  }

  /**
   * 清空对话
   */
  async clearConversation(conversationId: string): Promise<void> {
    try {
      await request.post(`${this.baseUrl}/conversation/${conversationId}/clear`)
    } catch (error) {
      console.error('清空对话失败:', error)
      throw error
    }
  }
}

export const chatService = new ChatService()