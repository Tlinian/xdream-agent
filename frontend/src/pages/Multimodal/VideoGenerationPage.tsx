import React, { useState } from 'react'
import { Card, Input, Button, Row, Col, message, Typography, Space, Tag, Slider, Select } from 'antd'
import { VideoCameraOutlined, DownloadOutlined, ClearOutlined, PlayCircleOutlined } from '@ant-design/icons'
import './VideoGenerationPage.scss'

const { Title, Paragraph } = Typography
const { TextArea } = Input
const { Option } = Select

const VideoGenerationPage: React.FC = () => {
  const [prompt, setPrompt] = useState('')
  const [generatedVideos, setGeneratedVideos] = useState<string[]>([])
  const [isGenerating, setIsGenerating] = useState(false)
  const [settings, setSettings] = useState({
    duration: 5,
    fps: 24,
    resolution: '720p',
    style: 'realistic'
  })

  // 生成视频
  const handleGenerate = async () => {
    if (!prompt.trim()) {
      message.warning('请输入视频描述')
      return
    }

    setIsGenerating(true)
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 3000))
      
      // 模拟生成的视频URL（使用图片作为演示）
      const mockVideos = [
        `https://picsum.photos/1280/720?random=video1`,
        `https://picsum.photos/1280/720?random=video2`,
        `https://picsum.photos/1280/720?random=video3`
      ]
      
      setGeneratedVideos(mockVideos)
      message.success('视频生成成功！')
    } catch (error) {
      message.error('视频生成失败')
      console.error('视频生成失败:', error)
    } finally {
      setIsGenerating(false)
    }
  }

  // 清空结果
  const handleClear = () => {
    setGeneratedVideos([])
    setPrompt('')
  }

  // 下载视频
  const handleDownload = (videoUrl: string, index: number) => {
    const link = document.createElement('a')
    link.href = videoUrl
    link.download = `generated-video-${index + 1}.mp4`
    link.click()
    message.success('视频下载成功！')
  }

  // 播放视频
  const handlePlay = (videoUrl: string) => {
    window.open(videoUrl, '_blank')
  }

  // 预设提示词
  const presetPrompts = [
    '夕阳下的海滩，海浪轻柔地拍打着沙滩，海鸥在天空中飞翔',
    '繁华的城市夜景，霓虹灯闪烁，车流如织，延时摄影效果',
    '春天花园，花朵绽放，蝴蝶飞舞，微风轻拂',
    '宇宙星空，星云旋转，流星划过，神秘而壮观'
  ]

  const videoStyles = [
    { value: 'realistic', label: '写实风格' },
    { value: 'cartoon', label: '卡通风格' },
    { value: 'anime', label: '动漫风格' },
    { value: 'artistic', label: '艺术风格' },
    { value: 'cinematic', label: '电影风格' }
  ]

  const resolutions = [
    { value: '480p', label: '480p (854x480)' },
    { value: '720p', label: '720p (1280x720)' },
    { value: '1080p', label: '1080p (1920x1080)' },
    { value: '4k', label: '4K (3840x2160)' }
  ]

  return (
    <div className="video-generation-page">
      <div className="page-header">
        <Title level={2}>
          <VideoCameraOutlined /> AI视频生成
        </Title>
        <Paragraph type="secondary">
          输入文本描述，让AI为您创造精彩的视频内容
        </Paragraph>
      </div>

      <div className="generation-content">
        <Row gutter={[24, 24]}>
          {/* 左侧控制面板 */}
          <Col xs={24} lg={8}>
            <Card title="生成设置" className="control-panel">
              <div className="form-section">
                <Title level={5}>视频描述</Title>
                <TextArea
                  rows={4}
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  placeholder="描述您想要生成的视频内容，包括场景、动作、氛围等..."
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
                <Title level={5}>高级设置</Title>
                
                <div className="setting-item">
                  <label>视频风格</label>
                  <Select
                    value={settings.style}
                    onChange={(value) => setSettings(prev => ({ ...prev, style: value }))}
                    style={{ width: '100%' }}
                  >
                    {videoStyles.map(style => (
                      <Option key={style.value} value={style.value}>
                        {style.label}
                      </Option>
                    ))}
                  </Select>
                </div>

                <div className="setting-item">
                  <label>分辨率: {settings.resolution}</label>
                  <Select
                    value={settings.resolution}
                    onChange={(value) => setSettings(prev => ({ ...prev, resolution: value }))}
                    style={{ width: '100%' }}
                  >
                    {resolutions.map(res => (
                      <Option key={res.value} value={res.value}>
                        {res.label}
                      </Option>
                    ))}
                  </Select>
                </div>

                <div className="setting-item">
                  <label>视频时长: {settings.duration}秒</label>
                  <Slider
                    min={3}
                    max={30}
                    step={1}
                    value={settings.duration}
                    onChange={(value) => setSettings(prev => ({ ...prev, duration: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>帧率: {settings.fps}fps</label>
                  <Slider
                    min={12}
                    max={60}
                    step={6}
                    value={settings.fps}
                    onChange={(value) => setSettings(prev => ({ ...prev, fps: value }))}
                  />
                </div>
              </div>

              <div className="action-buttons">
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button
                    type="primary"
                    size="large"
                    loading={isGenerating}
                    onClick={handleGenerate}
                    icon={<VideoCameraOutlined />}
                    block
                  >
                    生成视频
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
              {generatedVideos.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">
                    <VideoCameraOutlined style={{ fontSize: 64, color: '#ccc' }} />
                  </div>
                  <Paragraph type="secondary">
                    输入描述并点击生成按钮，AI将为您创造精彩的视频内容
                  </Paragraph>
                </div>
              ) : (
                <Row gutter={[16, 16]}>
                  {generatedVideos.map((video, index) => (
                    <Col xs={24} sm={12} key={index}>
                      <Card
                        className="generated-video-card"
                        cover={
                          <div className="video-thumbnail">
                            <img
                              alt={`生成的视频 ${index + 1}`}
                              src={video}
                              style={{ width: '100%', height: 'auto' }}
                            />
                            <div className="video-overlay">
                              <Button
                                type="primary"
                                shape="circle"
                                size="large"
                                icon={<PlayCircleOutlined />}
                                onClick={() => handlePlay(video)}
                              />
                            </div>
                          </div>
                        }
                        actions={[
                          <Button
                            key="play"
                            type="link"
                            icon={<PlayCircleOutlined />}
                            onClick={() => handlePlay(video)}
                          >
                            播放
                          </Button>,
                          <Button
                            key="download"
                            type="link"
                            icon={<DownloadOutlined />}
                            onClick={() => handleDownload(video, index)}
                          >
                            下载
                          </Button>
                        ]}
                      >
                        <Card.Meta
                          title={`视频 ${index + 1}`}
                          description={prompt}
                        />
                        <div className="video-info">
                          <Tag color="blue">{settings.resolution}</Tag>
                          <Tag color="green">{settings.duration}秒</Tag>
                          <Tag color="orange">{settings.fps}fps</Tag>
                        </div>
                      </Card>
                    </Col>
                  ))}
                </Row>
              )}
            </Card>

            {/* 使用提示 */}
            <Card title="使用提示" className="tips-panel" size="small">
              <ul className="tips-list">
                <li>详细描述视频场景、动作和氛围，越具体越好</li>
                <li>选择合适的视频风格可以获得更好的视觉效果</li>
                <li>较长的视频需要更多生成时间，建议从短视频开始</li>
                <li>高分辨率和高帧率会显著增加生成时间</li>
                <li>生成过程可能需要几分钟，请耐心等待</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default VideoGenerationPage