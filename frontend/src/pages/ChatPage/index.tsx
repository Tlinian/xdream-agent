import React, { useState, useRef, useEffect } from 'react'
import { Input, Button, Avatar, Spin, Empty, App } from 'antd'
import { SendOutlined, ClearOutlined, UserOutlined, RobotOutlined, LoadingOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { chatService, StreamResponse } from '@services/chatService'
import './index.scss'

const { TextArea } = Input

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: number
  isStreaming?: boolean
  isThinking?: boolean
  thinkingContent?: string
  finalContent?: string
  isThinkingExpanded?: boolean
}

const ChatPage: React.FC = () => {
  const { message } = App.useApp()
  const [messages, setMessages] = useState<Message[]>([])
  const [inputValue, setInputValue] = useState('')
  const [loading, setLoading] = useState(false)
  const [useReAct, setUseReAct] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // 滚动到底部
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  // 发送消息
  const handleSendMessage = async () => {
    if (!inputValue.trim() || loading) return

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: inputValue.trim(),
      timestamp: Date.now()
    }

    setMessages(prev => [...prev, userMessage])
    setInputValue('')
    setLoading(true)

    try {
      // 准备请求数据
      const requestData = {
        message: inputValue.trim(),
        messages: messages.map(msg => ({
          role: msg.role,
          content: msg.content
        })),
        modelType: 'gpt-4', // 使用可用的gpt-4模型替代有问题的DeepSeek模型
        useReAct: useReAct,
        stream: true // 启用流式响应
      }
      
      console.log('发送聊天请求:', requestData)
      console.log('请求数据:', JSON.stringify(requestData, null, 2))
      
      // 创建流式助手消息
      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: '',
        timestamp: Date.now(),
        isStreaming: true,
        isThinking: useReAct,
        thinkingContent: useReAct ? '' : undefined,
        finalContent: '',
        isThinkingExpanded: false // 默认收缩思考内容
      }
      setMessages(prev => [...prev, assistantMessage])
      
      // 使用SSE流式接收响应
      let currentContent = ''
      let thinkingContent = ''
      let isInThinkingPhase = useReAct
      let thinkingStarted = false
      let answerStarted = false
      
      chatService.streamChat(
        requestData,
        (response: StreamResponse) => {
          console.log('收到流式响应:', response)
          
          // 如果收到完成标记，结束流式响应
          if (response.finished) {
            console.log('流式响应完成')
            setLoading(false)
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.isStreaming = false
                lastMessage.isThinking = false
              }
              return newMessages
            })
            return
          }
          
          // 处理标记
          if (response.content === '[THINKING_START]') {
            thinkingStarted = true
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.isThinking = true
                lastMessage.thinkingContent = ''
              }
              return newMessages
            })
            return
          }
          
          if (response.content === '[THINKING_END]') {
            isInThinkingPhase = false
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.isThinking = false
                lastMessage.isThinkingExpanded = false // 思考完成后自动收缩
              }
              return newMessages
            })
            return
          }
          
          if (response.content === '[ANSWER_START]') {
            answerStarted = true
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.finalContent = ''
                lastMessage.isThinkingExpanded = false // 开始最终答案时收缩思考内容
              }
              return newMessages
            })
            return
          }
          
          if (isInThinkingPhase && thinkingStarted) {
            // 思考阶段
            thinkingContent += response.content
            
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.thinkingContent = thinkingContent
                lastMessage.content = `🤔 ${thinkingContent}`
              }
              return newMessages
            })
          } else if (answerStarted || !isInThinkingPhase) {
            // 最终回答阶段
            currentContent += response.content
            
            // 移除思考阶段的标记
            const finalContent = currentContent
              .replace(/Final Answer:\s*/i, '')
              .replace(/最终答案:\s*/i, '')
              .replace(/Thought: 我现在知道最终答案了\s*/i, '')
              .trim()
            
            setMessages(prev => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage && lastMessage.isStreaming) {
                lastMessage.finalContent = finalContent
                lastMessage.content = finalContent
                lastMessage.isThinking = false
                
                if (response.finished) {
                  lastMessage.isStreaming = false
                }
              }
              return newMessages
            })
          } else {
            // 兼容旧模式
            if (isInThinkingPhase) {
              // 思考阶段
              thinkingContent += response.content
              
              // 检测是否开始最终回答
            if ((thinkingContent + response.content).includes('Final Answer:') || 
                (thinkingContent + response.content).includes('最终答案:') ||
                (thinkingContent + response.content).includes('Thought: 我现在知道最终答案了')) {
              isInThinkingPhase = false
              setMessages(prev => {
                const newMessages = [...prev]
                const lastMessage = newMessages[newMessages.length - 1]
                if (lastMessage && lastMessage.isStreaming) {
                  lastMessage.isThinkingExpanded = false // 兼容旧模式：开始最终答案时收缩思考内容
                }
                return newMessages
              })
            }
              
              // 更新思考内容
              setMessages(prev => {
                const newMessages = [...prev]
                const lastMessage = newMessages[newMessages.length - 1]
                if (lastMessage && lastMessage.isStreaming) {
                  lastMessage.thinkingContent = thinkingContent
                  lastMessage.content = `🤔 ${thinkingContent}`
                }
                return newMessages
              })
            } else {
              // 最终回答阶段
              currentContent += response.content
              
              // 移除思考阶段的标记
              const finalContent = currentContent
                .replace(/Final Answer:\s*/i, '')
                .replace(/最终答案:\s*/i, '')
                .replace(/Thought: 我现在知道最终答案了\s*/i, '')
                .trim()
              
              setMessages(prev => {
                const newMessages = [...prev]
                const lastMessage = newMessages[newMessages.length - 1]
                if (lastMessage && lastMessage.isStreaming) {
                  lastMessage.finalContent = finalContent
                  lastMessage.content = finalContent
                  lastMessage.isThinking = false
                  
                  if (response.finished) {
                    lastMessage.isStreaming = false
                  }
                }
                return newMessages
              })
            }
          }
          

        },
        (error) => {
          console.error('流式响应错误:', error)
          message.error('流式响应失败，请重试')
          setLoading(false)
          
          // 更新错误消息
          setMessages(prev => {
            const newMessages = [...prev]
            const lastMessage = newMessages[newMessages.length - 1]
            if (lastMessage && lastMessage.isStreaming) {
              lastMessage.content = '抱歉，响应失败，请重试'
              lastMessage.isStreaming = false
              lastMessage.isThinking = false
            }
            return newMessages
          })
        },
        () => {
          console.log('流式响应连接关闭')
          setLoading(false)
        }
      )
      
      // 清理函数
      // 清理函数 - 注意：这里不应该立即调用cleanup，应该在组件卸载时调用
      
    } catch (error) {
      console.error('发送消息失败:', error)
      if (error && typeof error === 'object') {
        console.error('错误详情:', JSON.stringify(error, null, 2))
        console.error('错误类型:', (error as any)?.constructor?.name)
        if ('message' in error) {
          console.error('错误消息:', (error as any).message)
        }
        if ('stack' in error) {
          console.error('错误堆栈:', (error as any).stack)
        }
      }
      
      let errorMessage = '发送消息失败，请重试'
      if (error && typeof error === 'object' && 'message' in error) {
        errorMessage = `发送消息失败: ${error.message}`
      }
      
      message.error(errorMessage)
      setLoading(false)
    }
  }

  // 清空消息
  const handleClearMessages = () => {
    // 这里可以添加确认对话框
    if (window.confirm('确定要清空所有聊天记录吗？')) {
      setMessages([])
    }
  }

  // 切换思考内容的展开/收缩状态
  const toggleThinkingExpanded = (messageId: string) => {
    setMessages(prev => prev.map(msg => 
      msg.id === messageId 
        ? { ...msg, isThinkingExpanded: !msg.isThinkingExpanded }
        : msg
    ))
  }

  // 清理函数 - 预留用于组件卸载时清理资源
  // const cleanup = () => {
  //   // 清理逻辑 - 如关闭事件源连接等
  // }
  
  // useEffect(() => {
  //   return () => {
  //     cleanup();
  //   };
  // }, []);

  // 处理回车发送
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSendMessage()
    }
  }

  // 测试函数 - 用于调试请求问题
  const testChatService = async () => {
    console.log('=== 测试聊天服务 ===')
    console.log('测试函数被调用！')
    
    // 简单的直接测试，不依赖复杂逻辑
    try {
      console.log('开始直接测试fetch...')
      
      // 直接测试fetch API
      const response = await fetch('http://localhost:3003/direct-test?message=hello', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      console.log('直接fetch结果:', response.status)
      
      // 测试相对路径
      const relativeResponse = await fetch('/api/test', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      console.log('相对路径fetch结果:', relativeResponse.status)
      
    } catch (error) {
      console.error('直接测试失败:', error)
    }
  }

  return (
    <div className="chat-page">
      <div className="chat-header">
        <div className="chat-title">
          <RobotOutlined />
          <span>AI对话助手</span>
        </div>
        <div className="chat-actions">
          <Button
            icon={<ClearOutlined />}
            onClick={handleClearMessages}
            disabled={messages.length === 0}
          >
            清空
          </Button>
          <Button
            type={useReAct ? 'primary' : 'default'}
            onClick={() => setUseReAct(!useReAct)}
          >
            {useReAct ? 'ReAct模式开' : 'ReAct模式关'}
          </Button>
          <Button
            onClick={() => {/* 打开设置 */}}
          >
            设置
          </Button>
          <Button
            type="dashed"
            onClick={testChatService}
            disabled={loading}
          >
            测试服务
          </Button>
        </div>
      </div>

      <div className="chat-container">
        <div className="messages-container">
          {messages.length === 0 ? (
            <div className="empty-state">
              <Empty
                description="还没有对话记录"
                className="empty-message"
              >
                <p className="empty-tip">开始与AI助手的对话吧！</p>
              </Empty>
            </div>
          ) : (
            <div className="messages-list">
              {messages.map((message) => (
                <div
                  key={message.id}
                  className={`message-item ${message.role}`}
                >
                  <div className="message-avatar">
                    {message.role === 'user' ? (
                      <Avatar icon={<UserOutlined />} />
                    ) : (
                      <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#1890ff' }} />
                    )}
                  </div>
                  <div className="message-content">
                    {message.role === 'assistant' && message.thinkingContent && !message.isStreaming ? (
                      // 最终答案阶段，显示最终答案和可折叠的思考过程
                      <>
                        <div className="message-text">{message.content}</div>
                        <button 
                          className="toggle-thinking-btn"
                          onClick={() => toggleThinkingExpanded(message.id)}
                          style={{
                            fontSize: '12px',
                            color: '#1890ff',
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            padding: '4px 0',
                            display: 'flex',
                            alignItems: 'center',
                            marginTop: '4px'
                          }}
                        >
                          {message.isThinkingExpanded ? '收起思考过程 ▲' : '展开思考过程 ▼'}
                        </button>
                        {message.isThinkingExpanded && (
                          <div className="thinking-process collapsed">
                            <div className="thinking-header">
                              <span>AI的思考过程：</span>
                            </div>
                            <div className="thinking-content">
                              <pre className="thinking-text">
                                {message.thinkingContent?.split('\n').map((line, index) => (
                                  <span key={index} className="thinking-step">
                                    {line}
                                  </span>
                                ))}
                              </pre>
                            </div>
                          </div>
                        )}
                      </>
                    ) : message.role === 'assistant' && message.isThinking ? (
                      // 思考中阶段，实时显示思考过程
                      <div className="thinking-process">
                        <div className="thinking-header">
                          <LoadingOutlined spin style={{ color: '#1890ff', marginRight: 8 }} />
                          <span>AI正在思考中...</span>
                        </div>
                        <div className="thinking-content">
                          <pre className="thinking-text">
                            {message.thinkingContent?.split('\n').map((line, index) => {
                              const isActive = index === (message.thinkingContent?.split('\n').length || 0) - 1;
                              return (
                                <span key={index} className={`thinking-step ${isActive ? 'active' : ''}`}>
                                  {line}
                                </span>
                              );
                            })}
                          </pre>
                        </div>
                      </div>
                    ) : (
                      <div className="message-text">{message.content}</div>
                    )}
                    {message.role === 'assistant' && message.isStreaming && !message.isThinking && (
                      <div className="streaming-indicator">
                        <Spin size="small" />
                        <span style={{ marginLeft: 8, fontSize: '12px', color: '#999' }}>正在生成答案...</span>
                      </div>
                    )}
                    {message.role === 'assistant' && !message.isStreaming && message.finalContent && (
                      <div className="completion-indicator">
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 4 }} />
                        <span style={{ fontSize: '12px', color: '#52c41a' }}>回答完成</span>
                      </div>
                    )}
                    <div className="message-time">
                      {new Date(message.timestamp).toLocaleTimeString()}
                    </div>
                  </div>
                </div>
              ))}
              {loading && (
                <div className="message-item assistant">
                  <div className="message-avatar">
                    <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#1890ff' }} />
                  </div>
                  <div className="message-content">
                    <div className="message-text">
                      <Spin size="small" />
                      <span style={{ marginLeft: 8 }}>AI助手正在思考...</span>
                    </div>
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>
          )}
        </div>

        <div className="input-container">
          <div className="input-wrapper">
            <TextArea
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="开始与AI助手的对话吧！"
                autoSize={false}
                rows={1}
                disabled={loading}
              />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSendMessage}
              loading={loading}
              disabled={!inputValue.trim()}
            >
              发送
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ChatPage