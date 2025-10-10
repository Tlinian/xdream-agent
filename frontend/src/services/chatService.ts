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
   * 流式聊天接口（支持ReAct模式思考过程展示）
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
        modelType: requestData.modelType || 'gpt-4', // 使用可用的gpt-4模型作为默认
        temperature: String(requestData.temperature || 0.7),
        maxTokens: String(requestData.maxTokens || 2048),
        userId: 'test-user-001'  // 添加用户ID参数
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
          'X-User-Id': 'test-user-001'  // 同时在header中添加用户ID
        },
        signal: abortController.signal,
        credentials: 'include'
      }).then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        console.log('[SSE] 连接成功，状态码:', response.status)
        console.log('[SSE] 响应头:', response.headers.get('Content-Type'))
        
        eventSource.readyState = 1; // OPEN
        if (eventSource.onopen) {
          eventSource.onopen({ type: 'open' });
        }
        
        const reader = response.body!.getReader();
        const decoder = new TextDecoder();

        let buffer = '';
        
        function readStream(): Promise<any> {
          return reader.read().then(({ done, value }) => {
            if (done) {
              console.log('[SSE] 连接已关闭，缓冲区剩余:', buffer);
              eventSource.readyState = 2; // CLOSED
              if (eventSource._completeHandler) {
                eventSource._completeHandler();
              }
              return;
            }
            
            // 将新数据添加到缓冲区
            const chunk = decoder.decode(value, { stream: true });
            console.log('[SSE] 收到原始数据块:', chunk);
            console.log('[SSE] 数据块长度:', chunk.length);
            // 在浏览器环境中使用TextEncoder替代Buffer
            const encoder = new TextEncoder();
            const uint8Array = encoder.encode(chunk);
            const hexString = Array.from(uint8Array).map(b => b.toString(16).padStart(2, '0')).join('');
            console.log('[SSE] 数据块编码:', hexString.substring(0, 100)); // 显示前100个十六进制字符
            buffer += chunk;
            
            // 检查是否是JSON格式或文本格式
            try {
              // 尝试直接解析整个缓冲区
              if (buffer.trimStart().startsWith('{')) {
                console.log('[SSE] 尝试直接解析JSON:', buffer);
                const data = JSON.parse(buffer);
                console.log('[SSE] 直接解析成功:', data);
                if (eventSource.onmessage) {
                    console.log('[SSE] 发送解析后的数据:', data);
                    eventSource.onmessage({ data: JSON.stringify(data) });
                  }
                buffer = ''; // 清空缓冲区
              } else {
                // 按SSE规范处理（event: message\ndata: xxx\n\n 格式）
                // 检查是否包含完整的消息边界
                const messageBoundary = '\n\n';
                let messageEndIndex = buffer.indexOf(messageBoundary);
                
                while (messageEndIndex !== -1) {
                  const message = buffer.substring(0, messageEndIndex);
                  buffer = buffer.substring(messageEndIndex + messageBoundary.length);
                  
                  // 提取data字段
                  const dataMatch = message.match(/^data:\s*(.*)$/m);
                  if (dataMatch && dataMatch[1]) {
                    const dataStr = dataMatch[1].trim();
                    console.log('[SSE] 提取到data字段:', dataStr);
                    
                    try {
                      const data = JSON.parse(dataStr);
                      console.log('[SSE] 解析data成功:', data);
                      if (eventSource.onmessage) {
                        eventSource.onmessage({ data: JSON.stringify(data) });
                      }
                    } catch (e) {
                      console.error('[SSE] 解析data字段失败:', e, '数据:', dataStr);
                    }
                  } else if (message.trim() !== '') {
                    console.log('[SSE] 非标准SSE消息:', message);
                    // 尝试将整个消息作为data处理
                    try {
                      const data = JSON.parse(message.trim());
                      console.log('[SSE] 尝试将整个消息作为JSON解析成功:', data);
                      if (eventSource.onmessage) {
                        eventSource.onmessage({ data: JSON.stringify(data) });
                      }
                    } catch (e) {
                      console.error('[SSE] 解析整个消息失败:', e, '数据:', message);
                      // 直接作为纯文本消息发送
                      const data = {
                        content: message.trim(),
                        streamId: 'temp-' + Date.now(),
                        modelType: requestData.modelType || 'gpt-4', // 使用可用的gpt-4模型作为默认
                        finished: false
                      };
                      console.log('[SSE] 创建纯文本消息:', data);
                      if (eventSource.onmessage) {
                        eventSource.onmessage({ data: JSON.stringify(data) });
                      }
                    }
                  }
                  
                  messageEndIndex = buffer.indexOf(messageBoundary);
                }
              }
            } catch (e) {
              console.error('[SSE] 数据处理异常:', e, '当前缓冲区:', buffer);
            }
            
            return readStream();
          }).catch(readError => {
            console.error('[SSE] 流读取错误:', readError);
            if (onError) {
              onError(readError as Error);
            }
            eventSource.close();
          });
        }

        // 开始读取流
        readStream();
      }).catch(error => {
        console.error('[SSE] 连接失败:', error);
        eventSource.readyState = 2; // CLOSED
        if (eventSource.onerror) {
          eventSource.onerror({ type: 'error', error: error });
        }
      });
      
      // 设置事件处理器
      eventSource.onmessage = (event: any) => {
        try {
          const data = JSON.parse(event.data);
          console.log('[SSE] 处理消息:', data);
          onMessage(data);
          
          // 检查是否完成
          if (data.finished && eventSource._completeHandler) {
            eventSource._completeHandler();
          }
        } catch (error) {
          console.error('[SSE] 解析消息失败:', error, '原始数据:', event.data);
          if (onError) {
            onError(new Error('解析SSE消息失败'));
          }
        }
      };

      eventSource.onerror = (error: any) => {
        console.error('[SSE] 连接错误:', error);
        if (onError) {
          onError(new Error('SSE连接错误'));
        }
        eventSource.close();
      };

      // 定期发送心跳包防止连接断开
      eventSource.heartbeatInterval = setInterval(() => {
        if (eventSource.readyState === 1) { // OPEN
          console.log('[SSE] 心跳检测');
        }
      }, 30000);

      // 监听完成事件
      const originalOnComplete = onComplete;
      const completeHandler = () => {
        console.log('[SSE] 连接完成');
        if (eventSource.heartbeatInterval) {
          clearInterval(eventSource.heartbeatInterval);
        }
        if (originalOnComplete) {
          originalOnComplete();
        }
      };

      // 监听流结束
      eventSource.addEventListener('complete', completeHandler);

      return eventSource as EventSource;
    } catch (error) {
      console.error('[SSE] 创建连接失败:', error);
      if (onError) {
        onError(error as Error);
      }
      throw error;
    }
  }

  /**
   * 获取对话历史
   */
  async getConversationHistory(conversationId: string): Promise<ChatResponse[]> {
    try {
      const response = await request.get<ChatResponse[]>(`${this.baseUrl}/conversation/${conversationId}/history`)
      return response.data
    } catch (error) {
      console.error('获取对话历史失败:', error)
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

  /**
   * 获取可用模型列表
   */
  async getModels(): Promise<any[]> {
    try {
      const response = await request.get<any[]>(`${this.baseUrl}/models`)
      return response.data
    } catch (error) {
      console.error('获取模型列表失败:', error)
      throw error
    }
  }
}

export const chatService = new ChatService()