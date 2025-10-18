import React, { useEffect, useMemo, useRef, useState } from 'react'
import {
  App,
  Avatar,
  Button,
  Card,
  Divider,
  Empty,
  Input,
  Radio,
  Space,
  Spin,
  Switch,
  Tag,
  Tooltip,
  Typography,
} from 'antd'
import {
  BulbOutlined,
  CheckCircleOutlined,
  ClearOutlined,
  CopyOutlined,
  LoadingOutlined,
  ReloadOutlined,
  RobotOutlined,
  SendOutlined,
  SettingOutlined,
  ThunderboltOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { chatService, StreamResponse } from '@services/chatService'
import './index.scss'

const { TextArea } = Input
const { Paragraph, Text } = Typography

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

interface QuickPrompt {
  title: string
  description: string
  content: string
}

const modelOptions = [
  { label: 'GPT-4 Turbo', value: 'gpt-4' },
  { label: 'GPT-4o Mini', value: 'gpt-4o-mini' },
  { label: 'Claude Haiku', value: 'claude-haiku' },
]

const quickPrompts: QuickPrompt[] = [
  {
    title: '市场方案',
    description: '梳理上线目标与推广节奏',
    content:
      '请为 Dream Agent 的新品发布制定一份市场进入计划，包含目标用户画像、核心渠道策略以及未来四周的执行里程碑。',
  },
  {
    title: '知识总结',
    description: '提炼长文档中的重点洞察',
    content:
      '我将上传一份项目复盘文档，请整理关键结论、遗留风险，并输出三条优先级最高的后续行动。',
  },
  {
    title: '代码助手',
    description: '定位并修复性能问题',
    content:
      '请审查以下 React 组件，找出可能的性能瓶颈，并给出可以立即落地的优化建议。',
  },
]

const formatTime = (timestamp: number) =>
  new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

const formatDateTime = (timestamp: number) => new Date(timestamp).toLocaleString()

const ChatPage: React.FC = () => {
  const { message: messageApi } = App.useApp()
  const [messages, setMessages] = useState<Message[]>([])
  const [inputValue, setInputValue] = useState('')
  const [loading, setLoading] = useState(false)
  const [useReAct, setUseReAct] = useState(false)
  const [selectedModel, setSelectedModel] = useState<string>(modelOptions[0].value)
  const [insightCollapsed, setInsightCollapsed] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const conversationStats = useMemo(() => {
    if (messages.length === 0) {
      return {
        turns: 0,
        userMessages: 0,
        assistantMessages: 0,
        lastUpdatedLabel: '--',
      }
    }

    const userMessages = messages.filter((msg) => msg.role === 'user').length
    const assistantMessages = messages.filter((msg) => msg.role === 'assistant').length
    const lastUpdatedLabel = formatDateTime(messages[messages.length - 1].timestamp)

    return {
      turns: Math.max(userMessages, assistantMessages),
      userMessages,
      assistantMessages,
      lastUpdatedLabel,
    }
  }, [messages])

  const conversationTitle = useMemo(() => {
    const firstUserMessage = messages.find((msg) => msg.role === 'user')
    if (!firstUserMessage) {
      return 'Dream Agent 对话工作台'
    }

    return firstUserMessage.content.length > 22
      ? `${firstUserMessage.content.slice(0, 22)}...`
      : firstUserMessage.content
  }, [messages])

  const sendMessage = async (rawContent?: string) => {
    const trimmedContent = (rawContent ?? inputValue).trim()
    if (!trimmedContent || loading) return

    const timestamp = Date.now()
    const userMessage: Message = {
      id: `user-${timestamp}`,
      role: 'user',
      content: trimmedContent,
      timestamp,
    }

    const historyForRequest = [...messages, userMessage]

    setMessages((prev) => [...prev, userMessage])
    setInputValue('')
    setLoading(true)

    try {
      const requestData = {
        message: trimmedContent,
        messages: historyForRequest.map((msg) => ({
          role: msg.role,
          content: msg.content,
        })),
        modelType: selectedModel,
        useReAct,
        stream: true,
      }

      const assistantTimestamp = Date.now()
      const assistantMessage: Message = {
        id: `assistant-${assistantTimestamp}`,
        role: 'assistant',
        content: '',
        timestamp: assistantTimestamp,
        isStreaming: true,
        isThinking: useReAct,
        thinkingContent: useReAct ? '' : undefined,
        finalContent: '',
        isThinkingExpanded: false,
      }
      setMessages((prev) => [...prev, assistantMessage])

      let currentContent = ''
      let thinkingContent = ''
      let isInThinkingPhase = useReAct
      let thinkingStarted = false
      let answerStarted = !useReAct

      chatService.streamChat(
        requestData,
        (response: StreamResponse) => {
          if (response.finished) {
            setLoading(false)
            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.isStreaming = false
                lastMessage.isThinking = false
              }
              return newMessages
            })
            return
          }

          if (response.content === '[THINKING_START]') {
            thinkingStarted = true
            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.isThinking = true
                lastMessage.thinkingContent = ''
              }
              return newMessages
            })
            return
          }

          if (response.content === '[THINKING_END]') {
            isInThinkingPhase = false
            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.isThinking = false
                lastMessage.isThinkingExpanded = false
              }
              return newMessages
            })
            return
          }

          if (response.content === '[ANSWER_START]') {
            answerStarted = true
            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.finalContent = ''
                lastMessage.isThinkingExpanded = false
              }
              return newMessages
            })
            return
          }

          if (isInThinkingPhase && thinkingStarted) {
            thinkingContent += response.content

            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.thinkingContent = thinkingContent
                lastMessage.content = `思考中... ${thinkingContent}`
              }
              return newMessages
            })
            return
          }

          if (answerStarted || !isInThinkingPhase) {
            currentContent += response.content
            const finalContent = currentContent
              .replace(/Final Answer:\s*/i, '')
              .replace(/Thought: I now know the final answer\s*/i, '')
              .replace(/最终答案[:：]?\s*/i, '')
              .trim()

            setMessages((prev) => {
              const newMessages = [...prev]
              const lastMessage = newMessages[newMessages.length - 1]
              if (lastMessage?.isStreaming) {
                lastMessage.finalContent = finalContent
                lastMessage.content = finalContent
                lastMessage.isThinking = false
              }
              return newMessages
            })
          }
        },
        (error: Error) => {
          setLoading(false)
          messageApi.error(error.message || '流式响应发生错误')
          setMessages((prev) => prev.filter((msg) => !msg.isStreaming))
        },
        () => {
          setLoading(false)
        }
      )
    } catch (error) {
      console.error('send message failed', error)
      const errorMessage =
        error && typeof error === 'object' && 'message' in error
          ? (error as { message?: string }).message || '发送失败，请稍后重试'
          : '发送失败，请稍后重试'
      messageApi.error(errorMessage)
      setLoading(false)
    }
  }

  const handleSendMessage = () => {
    void sendMessage()
  }

  const handleQuickPrompt = (prompt: QuickPrompt) => {
    if (loading) {
      return
    }

    messageApi.success(`已载入「${prompt.title}」提示词`)
    void sendMessage(prompt.content)
  }

  const handleClearMessages = () => {
    if (messages.length === 0) {
      return
    }

    const confirmed = window.confirm('确定清空当前对话吗？')
    if (confirmed) {
      setMessages([])
      messageApi.success('对话已清空')
    }
  }

  const toggleThinkingExpanded = (messageId: string) => {
    setMessages((prev) =>
      prev.map((msg) =>
        msg.id === messageId
          ? { ...msg, isThinkingExpanded: !msg.isThinkingExpanded }
          : msg
      )
    )
  }

  const handleKeyPress = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      handleSendMessage()
    }
  }

  const handleCopyMessage = async (content: string) => {
    try {
      await navigator.clipboard.writeText(content)
      messageApi.success('内容已复制')
    } catch (err) {
      console.error('copy failed', err)
      messageApi.warning('浏览器阻止复制，请手动选择文本')
    }
  }

  const handleRegenerate = (assistantMessageId: string) => {
    const assistantIndex = messages.findIndex((msg) => msg.id === assistantMessageId)
    if (assistantIndex <= 0) {
      messageApi.warning('暂无可重试的提问')
      return
    }

    const previousUserMessage = [...messages]
      .slice(0, assistantIndex)
      .reverse()
      .find((msg) => msg.role === 'user')

    if (!previousUserMessage) {
      messageApi.warning('未找到上一条用户问题')
      return
    }

    messageApi.info('正在重新生成回答...')
    void sendMessage(previousUserMessage.content)
  }

  const testChatService = async () => {
    try {
      const response = await fetch('http://localhost:3003/direct-test?message=hello', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      })

      if (response.ok) {
        messageApi.success('直连测试成功')
      } else {
        messageApi.error('直连测试失败，请检查代理配置')
      }
    } catch (error) {
      console.error('direct test failed', error)
      messageApi.error('直连测试失败，请检查代理配置')
    }
  }

  return (
    <div className="chat-page">
      <header className="chat-header">
        <div className="chat-header__title">
          <RobotOutlined className="chat-header__icon" />
          <div>
            <h1>{conversationTitle}</h1>
            <Text type="secondary">把复杂的目标交给 Dream Agent，自主规划并交付结果</Text>
          </div>
        </div>
        <div className="chat-header__meta">
          <Tag color="geekblue">
            模型：{modelOptions.find((item) => item.value === selectedModel)?.label}
          </Tag>
          <Tag color="cyan">轮次：{conversationStats.turns}</Tag>
          <Tag color="green">最近更新：{conversationStats.lastUpdatedLabel}</Tag>
        </div>
        <div className="chat-header__actions">
          <Tooltip title="清空当前对话">
            <Button
              icon={<ClearOutlined />}
              onClick={handleClearMessages}
              disabled={messages.length === 0}
            />
          </Tooltip>
          <Tooltip title={insightCollapsed ? '展开智能概览' : '收起智能概览'}>
            <Button
              icon={<SettingOutlined />}
              onClick={() => setInsightCollapsed((prev) => !prev)}
            />
          </Tooltip>
        </div>
      </header>

      <div className="chat-body">
        <section className="conversation-panel">
          <div className="messages-container">
            {messages.length === 0 ? (
              <div className="empty-state">
                <Empty description="还没有对话记录" />
                <div className="empty-prompts">
                  <Text type="secondary">试试以下提示，快速开启对话：</Text>
                  {quickPrompts.map((prompt) => (
                    <Card
                      size="small"
                      key={prompt.title}
                      className="empty-prompts__card"
                      onClick={() => handleQuickPrompt(prompt)}
                    >
                      <Space direction="vertical" size={4}>
                        <Space align="center">
                          <BulbOutlined />
                          <Text strong>{prompt.title}</Text>
                        </Space>
                        <Text type="secondary">{prompt.description}</Text>
                      </Space>
                    </Card>
                  ))}
                </div>
              </div>
            ) : (
              <div className="messages-list">
                {messages.map((message) => {
                  const isAssistant = message.role === 'assistant'
                  const displayContent =
                    message.finalContent && !message.isStreaming
                      ? message.finalContent
                      : message.content

                  return (
                    <div key={message.id} className={`message-item ${message.role}`}>
                      <div className="message-avatar">
                        {isAssistant ? (
                          <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#1890ff' }} />
                        ) : (
                          <Avatar icon={<UserOutlined />} />
                        )}
                      </div>
                      <div className="message-bubble">
                        <div className="message-header">
                          <span className="message-author">
                            {isAssistant ? 'Dream Agent' : '你'}
                          </span>
                          <span className="message-time">{formatTime(message.timestamp)}</span>
                        </div>

                        {isAssistant && message.thinkingContent && !message.isStreaming ? (
                          <>
                            <Paragraph className="message-text">{displayContent}</Paragraph>
                            <button
                              type="button"
                              className="toggle-thinking-btn"
                              onClick={() => toggleThinkingExpanded(message.id)}
                            >
                              {message.isThinkingExpanded ? '收起思考过程' : '展开思考过程'}
                            </button>
                            {message.isThinkingExpanded && (
                              <div className="thinking-panel">
                                <div className="thinking-panel__title">
                                  <ThunderboltOutlined />
                                  <span>思考轨迹</span>
                                </div>
                                <pre className="thinking-panel__content">
                                  {message.thinkingContent.split('\n').map((line, index) => (
                                    <span key={`${message.id}-thought-${index}`}>{line}</span>
                                  ))}
                                </pre>
                              </div>
                            )}
                          </>
                        ) : isAssistant && message.isThinking ? (
                          <div className="thinking-panel live">
                            <div className="thinking-panel__title">
                              <LoadingOutlined spin />
                              <span>Dream Agent 正在思考...</span>
                            </div>
                            <pre className="thinking-panel__content">
                              {(message.thinkingContent ?? '').split('\n').map((line, index, arr) => (
                                <span
                                  key={`${message.id}-live-${index}`}
                                  className={index === arr.length - 1 ? 'active' : ''}
                                >
                                  {line}
                                </span>
                              ))}
                            </pre>
                          </div>
                        ) : (
                          <Paragraph className="message-text">{displayContent}</Paragraph>
                        )}

                        {isAssistant && message.isStreaming && !message.isThinking && (
                          <div className="streaming-indicator">
                            <Spin size="small" />
                            <span>正在生成答案...</span>
                          </div>
                        )}

                        {isAssistant && !message.isStreaming && message.finalContent && (
                          <div className="completion-indicator">
                            <CheckCircleOutlined />
                            <span>回答完成</span>
                          </div>
                        )}

                        <div className="message-footer">
                          <Space size={8}>
                            <Tooltip title="复制内容">
                              <Button
                                size="small"
                                type="text"
                                icon={<CopyOutlined />}
                                onClick={() => handleCopyMessage(displayContent)}
                              />
                            </Tooltip>
                            {isAssistant && !message.isStreaming && (
                              <Tooltip title="重新生成此回答">
                                <Button
                                  size="small"
                                  type="text"
                                  icon={<ReloadOutlined />}
                                  onClick={() => handleRegenerate(message.id)}
                                  disabled={loading}
                                />
                              </Tooltip>
                            )}
                          </Space>
                        </div>
                      </div>
                    </div>
                  )
                })}
                {loading && (
                  <div className="message-item assistant">
                    <div className="message-avatar">
                      <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#1890ff' }} />
                    </div>
                    <div className="message-bubble">
                      <div className="thinking-panel live">
                        <div className="thinking-panel__title">
                          <LoadingOutlined spin />
                          <span>Dream Agent 正在整理回答...</span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
                <div ref={messagesEndRef} />
              </div>
            )}
          </div>

          <div className="composer">
            <div className="composer__input">
              <TextArea
                value={inputValue}
                onChange={(event) => setInputValue(event.target.value)}
                onKeyDown={handleKeyPress}
                placeholder="描述你的目标、上下文或期望产出，Dream Agent 会帮你拆解并执行"
                autoSize={{ minRows: 1, maxRows: 6 }}
                disabled={loading}
              />
              <Button
                type="primary"
                icon={<SendOutlined />}
                onClick={handleSendMessage}
                loading={loading}
                disabled={!inputValue.trim() || loading}
              >
                发送
              </Button>
            </div>

            <div className="composer__toolbar">
              <Space size="middle">
                <Space>
                  <Switch
                    checked={useReAct}
                    onChange={(checked) => setUseReAct(checked)}
                    size="small"
                  />
                  <span>agent</span>
                </Space>
                <Tag color="blue">消息数：{messages.length}</Tag>
                <Tag color="purple">助手回复数：{conversationStats.assistantMessages}</Tag>
              </Space>
            </div>
          </div>
        </section>

        <aside className={`insight-panel ${insightCollapsed ? 'collapsed' : ''}`}>
          <div className="insight-panel__toggle">
            <Button
              type="text"
              size="small"
              icon={<SettingOutlined />}
              onClick={() => setInsightCollapsed((prev) => !prev)}
            >
              {insightCollapsed ? '展开概览' : '收起概览'}
            </Button>
          </div>
          <div className="insight-panel__content">
            <Card title="对话概览" size="small" bordered={false}>
              <Space direction="vertical" size={8}>
                <div className="stat-row">
                  <Text type="secondary">对话轮次</Text>
                  <Text strong>{conversationStats.turns}</Text>
                </div>
                <div className="stat-row">
                  <Text type="secondary">用户消息</Text>
                  <Text strong>{conversationStats.userMessages}</Text>
                </div>
                <div className="stat-row">
                  <Text type="secondary">助手回复</Text>
                  <Text strong>{conversationStats.assistantMessages}</Text>
                </div>
                <div className="stat-row">
                  <Text type="secondary">最近更新</Text>
                  <Text strong>{conversationStats.lastUpdatedLabel}</Text>
                </div>
              </Space>
            </Card>

            <Card
              title={
                <Space>
                  <BulbOutlined />
                  <span>灵感提示</span>
                </Space>
              }
              size="small"
              bordered={false}
            >
              <Space direction="vertical" size={12} className="prompt-list">
                {quickPrompts.map((prompt) => (
                  <Button
                    key={`prompt-${prompt.title}`}
                    block
                    onClick={() => handleQuickPrompt(prompt)}
                    disabled={loading}
                  >
                    <div className="prompt-item">
                      <Text strong>{prompt.title}</Text>
                      <Text type="secondary">{prompt.description}</Text>
                    </div>
                  </Button>
                ))}
              </Space>
            </Card>

            <Card
              title={
                <Space>
                  <SettingOutlined />
                  <span>会话设置</span>
                </Space>
              }
              size="small"
              bordered={false}
            >
              <Divider orientation="left">模型选择</Divider>
              <Radio.Group
                value={selectedModel}
                onChange={(event) => setSelectedModel(event.target.value)}
                className="model-selector"
              >
                <Space direction="vertical">
                  {modelOptions.map((option) => (
                    <Radio key={option.value} value={option.value}>
                      {option.label}
                    </Radio>
                  ))}
                </Space>
              </Radio.Group>

              <Divider orientation="left">诊断工具</Divider>
              <Button block onClick={testChatService} icon={<ThunderboltOutlined />}>
                测试服务连通性
              </Button>
            </Card>
          </div>
        </aside>
      </div>
    </div>
  )
}

export default ChatPage
