import React, { useState } from 'react'
import { Card, Input, Button, Row, Col, message, Typography, Space, Tag, Select, Slider } from 'antd'
import { AudioOutlined, DownloadOutlined, ClearOutlined, PlayCircleOutlined, SoundOutlined } from '@ant-design/icons'
import './AudioGenerationPage.scss'

const { Title, Paragraph } = Typography
const { TextArea } = Input
const { Option } = Select

const AudioGenerationPage: React.FC = () => {
  const [prompt, setPrompt] = useState('')
  const [generatedAudios, setGeneratedAudios] = useState<{url: string, title: string, duration: string}[]>([])
  const [isGenerating, setIsGenerating] = useState(false)
  const [settings, setSettings] = useState({
    voice: 'zh-female',
    speed: 1.0,
    pitch: 1.0,
    volume: 1.0,
    format: 'mp3'
  })

  // 生成音频
  const handleGenerate = async () => {
    if (!prompt.trim()) {
      message.warning('请输入音频描述或文本内容')
      return
    }

    setIsGenerating(true)
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 2500))
      
      // 模拟生成的音频数据
      const mockAudios = [
        {
          url: 'data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBSuBzvLZiTYIG2m98OScTgwOUarm7blmGgU7k9n1unEiBC13yO/eizEIHWq+8+OWT',
          title: `音频 1`,
          duration: '0:15'
        },
        {
          url: 'data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBSuBzvLZiTYIG2m98OScTgwOUarm7blmGgU7k9n1unEiBC13yO/eizEIHWq+8+OWT',
          title: `音频 2`,
          duration: '0:20'
        }
      ]
      
      setGeneratedAudios(mockAudios)
      message.success('音频生成成功！')
    } catch (error) {
      message.error('音频生成失败')
      console.error('音频生成失败:', error)
    } finally {
      setIsGenerating(false)
    }
  }

  // 清空结果
  const handleClear = () => {
    setGeneratedAudios([])
    setPrompt('')
  }

  // 播放音频
  const handlePlay = (audioUrl: string) => {
    const audio = new Audio(audioUrl)
    audio.play().catch(error => {
      message.error('音频播放失败')
      console.error('音频播放失败:', error)
    })
  }

  // 下载音频
  const handleDownload = (audio: {url: string, title: string}, index: number) => {
    const link = document.createElement('a')
    link.href = audio.url
    link.download = `${audio.title.replace(/\s+/g, '-').toLowerCase()}-${index + 1}.${settings.format}`
    link.click()
    message.success('音频下载成功！')
  }

  // 预设提示词
  const presetPrompts = [
    '欢迎来到我们的AI助手平台，我是您的智能助手小助手。',
    '在这个美好的日子里，让我们一起探索人工智能的奇妙世界。',
    '科技改变生活，AI创造未来。让我们一起见证这个伟大的时代。',
    '感谢您的收听，希望这段音频能为您带来愉快的体验。'
  ]

  const voiceOptions = [
    { value: 'zh-female', label: '中文女声' },
    { value: 'zh-male', label: '中文男声' },
    { value: 'en-female', label: '英文女声' },
    { value: 'en-male', label: '英文男声' }
  ]

  const formatOptions = [
    { value: 'mp3', label: 'MP3格式' },
    { value: 'wav', label: 'WAV格式' },
    { value: 'aac', label: 'AAC格式' }
  ]

  return (
    <div className="audio-generation-page">
      <div className="page-header">
        <Title level={2}>
          <AudioOutlined /> AI音频生成
        </Title>
        <Paragraph type="secondary">
          输入文本内容，让AI为您生成自然流畅的语音音频
        </Paragraph>
      </div>

      <div className="generation-content">
        <Row gutter={[24, 24]}>
          {/* 左侧控制面板 */}
          <Col xs={24} lg={8}>
            <Card title="生成设置" className="control-panel">
              <div className="form-section">
                <Title level={5}>文本内容</Title>
                <TextArea
                  rows={4}
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  placeholder="输入要转换为语音的文本内容..."
                  maxLength={1000}
                  showCount
                />
              </div>

              <div className="preset-prompts">
                <Title level={5}>快速提示</Title>
                <Space direction="vertical" style={{ width: '100%' }}>
                  {presetPrompts.map((preset, index) => (
                    <Tag
                      key={index}
                      style={{ cursor: 'pointer', marginBottom: 8 }}
                      onClick={() => setPrompt(preset)}
                    >
                      {preset}
                    </Tag>
                  ))}
                </Space>
              </div>

              <div className="generation-settings">
                <Title level={5}>语音设置</Title>
                
                <div className="setting-item">
                  <label>声音选择</label>
                  <Select
                    value={settings.voice}
                    onChange={(value) => setSettings(prev => ({ ...prev, voice: value }))}
                    style={{ width: '100%' }}
                  >
                    {voiceOptions.map(option => (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                </div>

                <div className="setting-item">
                  <label>语速: {settings.speed}x</label>
                  <Slider
                    min={0.5}
                    max={2.0}
                    step={0.1}
                    value={settings.speed}
                    onChange={(value) => setSettings(prev => ({ ...prev, speed: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>音调: {settings.pitch}x</label>
                  <Slider
                    min={0.5}
                    max={1.5}
                    step={0.1}
                    value={settings.pitch}
                    onChange={(value) => setSettings(prev => ({ ...prev, pitch: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>音量: {Math.round(settings.volume * 100)}%</label>
                  <Slider
                    min={0.1}
                    max={1.0}
                    step={0.1}
                    value={settings.volume}
                    onChange={(value) => setSettings(prev => ({ ...prev, volume: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>输出格式</label>
                  <Select
                    value={settings.format}
                    onChange={(value) => setSettings(prev => ({ ...prev, format: value }))}
                    style={{ width: '100%' }}
                  >
                    {formatOptions.map(option => (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                </div>
              </div>

              <div className="action-buttons">
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button
                    type="primary"
                    size="large"
                    loading={isGenerating}
                    onClick={handleGenerate}
                    icon={<SoundOutlined />}
                    block
                  >
                    生成音频
                  </Button>
                  <Button
                    danger
                    icon={<ClearOutlined />}
                    onClick={handleClear}
                    block
                  >
                    清空结果
                  </Button>
                </Space>
              </div>
            </Card>
          </Col>

          {/* 右侧结果展示 */}
          <Col xs={24} lg={16}>
            <Card title="生成结果" className="results-panel">
              {generatedAudios.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">
                    <AudioOutlined style={{ fontSize: 64, color: '#ccc' }} />
                  </div>
                  <Paragraph type="secondary">
                    输入文本内容并点击生成按钮，AI将为您生成自然流畅的语音
                  </Paragraph>
                </div>
              ) : (
                <div className="audio-results">
                  {generatedAudios.map((audio, index) => (
                    <Card key={index} className="audio-card" size="small">
                      <div className="audio-item">
                        <div className="audio-info">
                          <div className="audio-title">
                            <AudioOutlined />
                            <span>{audio.title}</span>
                          </div>
                          <div className="audio-meta">
                            <Tag color="blue">{audio.duration}</Tag>
                            <Tag color="green">{settings.voice}</Tag>
                            <Tag color="orange">{settings.format.toUpperCase()}</Tag>
                          </div>
                        </div>
                        <div className="audio-actions">
                          <Space>
                            <Button
                              type="primary"
                              shape="circle"
                              icon={<PlayCircleOutlined />}
                              onClick={() => handlePlay(audio.url)}
                            />
                            <Button
                              icon={<DownloadOutlined />}
                              onClick={() => handleDownload(audio, index)}
                            >
                              下载
                            </Button>
                          </Space>
                        </div>
                      </div>
                    </Card>
                  ))}
                </div>
              )}
            </Card>

            {/* 使用提示 */}
            <Card title="使用提示" className="tips-panel" size="small">
              <ul className="tips-list">
                <li>支持中英文文本输入，系统会自动识别语言</li>
                <li>调整语速、音调和音量可以获得不同的语音效果</li>
                <li>选择合适的语音类型，让音频更符合您的需求</li>
                <li>较长的文本可能需要更多生成时间</li>
                <li>生成的音频支持在线播放和下载保存</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default AudioGenerationPage