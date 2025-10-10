import React, { useState } from 'react'
import { Card, Input, Button, Row, Col, Upload, message, Typography, Space, Tag, Select, Divider, Progress } from 'antd'
import { UploadOutlined, EyeOutlined, DownloadOutlined, ClearOutlined, SoundOutlined, PictureOutlined, FileTextOutlined } from '@ant-design/icons'
import './ContentUnderstandingPage.scss'

const { Title, Paragraph } = Typography
const { TextArea } = Input
const { Option } = Select

const ContentUnderstandingPage: React.FC = () => {
  const [inputText, setInputText] = useState('')
  const [inputImage, setInputImage] = useState<string | null>(null)
  const [inputAudio, setInputAudio] = useState<string | null>(null)
  const [understandingResult, setUnderstandingResult] = useState<any>(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [understandingMode, setUnderstandingMode] = useState('text')

  // 处理图像上传
  const handleImageUpload = (file: File) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      setInputImage(e.target?.result as string)
    }
    reader.readAsDataURL(file)
    return false // 阻止自动上传
  }

  // 处理音频上传
  const handleAudioUpload = (file: File) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      setInputAudio(e.target?.result as string)
    }
    reader.readAsDataURL(file)
    return false // 阻止自动上传
  }

  // 移除输入
  const removeImage = () => {
    setInputImage(null)
  }

  const removeAudio = () => {
    setInputAudio(null)
  }

  // 内容理解处理
  const handleUnderstanding = async () => {
    let hasInput = false
    if (understandingMode === 'text' && inputText.trim()) hasInput = true
    if (understandingMode === 'image' && inputImage) hasInput = true
    if (understandingMode === 'audio' && inputAudio) hasInput = true
    if (understandingMode === 'multimodal' && (inputText.trim() || inputImage || inputAudio)) hasInput = true

    if (!hasInput) {
      message.warning(`请提供${understandingMode === 'multimodal' ? '至少一种' : ''}输入数据`)
      return
    }

    setIsProcessing(true)
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 3000))
      
      // 模拟理解结果
      const mockResult = {
        mode: understandingMode,
        summary: '内容理解分析完成，成功识别出关键信息和语义特征。',
        confidence: Math.random() * 0.3 + 0.7, // 0.7-1.0
        keyPoints: [
          '识别出主要主题和核心概念',
          '提取出关键实体和关系',
          '分析出情感倾向和语义特征',
          '生成了结构化的知识表示'
        ],
        entities: [
          { name: '实体1', type: '人物', confidence: 0.95 },
          { name: '实体2', type: '地点', confidence: 0.88 },
          { name: '实体3', type: '组织', confidence: 0.82 }
        ],
        sentiment: {
          label: '积极',
          score: 0.78
        },
        categories: ['科技', '教育', '娱乐'],
        language: 'zh-CN',
        processingTime: '2.3s',
        recommendations: [
          '建议提供更多上下文信息以获得更准确的理解',
          '可以结合其他模态的数据进行多模态分析',
          '考虑使用领域特定的模型以获得更好的效果'
        ]
      }
      
      setUnderstandingResult(mockResult)
      message.success('内容理解分析完成！')
    } catch (error) {
      message.error('理解处理失败')
      console.error('理解处理失败:', error)
    } finally {
      setIsProcessing(false)
    }
  }

  // 清空所有输入
  const handleClear = () => {
    setInputText('')
    setInputImage(null)
    setInputAudio(null)
    setUnderstandingResult(null)
  }

  // 下载结果
  const handleDownload = () => {
    if (understandingResult) {
      const dataStr = JSON.stringify(understandingResult, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'content-understanding-result.json'
      link.click()
      URL.revokeObjectURL(url)
      message.success('理解结果下载成功！')
    }
  }

  const understandingModes = [
    { value: 'text', label: '文本理解', icon: <FileTextOutlined />, description: '对文本内容进行语义分析和理解' },
    { value: 'image', label: '图像理解', icon: <PictureOutlined />, description: '对图像内容进行视觉识别和场景理解' },
    { value: 'audio', label: '音频理解', icon: <SoundOutlined />, description: '对音频内容进行语音识别和语义理解' },
    { value: 'multimodal', label: '多模态理解', icon: <EyeOutlined />, description: '综合多种模态数据进行深度理解' }
  ]

  return (
    <div className="content-understanding-page">
      <div className="page-header">
        <Title level={2}>
          <EyeOutlined /> 内容理解
        </Title>
        <Paragraph type="secondary">
          深度理解文本、图像、音频等多种内容形式，提取关键信息和语义特征
        </Paragraph>
      </div>

      <div className="understanding-content">
        <Row gutter={[24, 24]}>
          {/* 左侧输入区域 */}
          <Col xs={24} lg={12}>
            <Card title="输入内容" className="input-panel">
              <div className="mode-selection">
                <Title level={5}>理解模式</Title>
                <Select
                  value={understandingMode}
                  onChange={setUnderstandingMode}
                  style={{ width: '100%' }}
                >
                  {understandingModes.map(mode => (
                    <Option key={mode.value} value={mode.value}>
                      <Space>
                        {mode.icon}
                        {mode.label}
                      </Space>
                    </Option>
                  ))}
                </Select>
                <Paragraph type="secondary" style={{ marginTop: 8 }}>
                  {understandingModes.find(m => m.value === understandingMode)?.description}
                </Paragraph>
              </div>

              <Divider />

              {/* 文本输入 */}
              {(understandingMode === 'text' || understandingMode === 'multimodal') && (
                <div className="input-section">
                  <Title level={5}>
                    <FileTextOutlined /> 文本内容
                  </Title>
                  <TextArea
                    rows={4}
                    value={inputText}
                    onChange={(e) => setInputText(e.target.value)}
                    placeholder="输入需要理解的文本内容..."
                    maxLength={2000}
                    showCount
                  />
                </div>
              )}

              {/* 图像上传 */}
              {(understandingMode === 'image' || understandingMode === 'multimodal') && (
                <div className="input-section">
                  <Title level={5}>
                    <PictureOutlined /> 图像内容
                  </Title>
                  <Upload
                    accept="image/*"
                    beforeUpload={handleImageUpload}
                    showUploadList={false}
                  >
                    <Button icon={<UploadOutlined />}>上传图像</Button>
                  </Upload>
                  
                  {inputImage && (
                    <div className="uploaded-image">
                      <img src={inputImage} alt="上传的图像" />
                      <Button
                        type="link"
                        danger
                        size="small"
                        onClick={removeImage}
                        style={{ marginTop: 8 }}
                      >
                        移除图像
                      </Button>
                    </div>
                  )}
                </div>
              )}

              {/* 音频上传 */}
              {(understandingMode === 'audio' || understandingMode === 'multimodal') && (
                <div className="input-section">
                  <Title level={5}>
                    <SoundOutlined /> 音频内容
                  </Title>
                  <Upload
                    accept="audio/*"
                    beforeUpload={handleAudioUpload}
                    showUploadList={false}
                  >
                    <Button icon={<UploadOutlined />}>上传音频</Button>
                  </Upload>
                  
                  {inputAudio && (
                    <div className="uploaded-audio">
                      <audio controls src={inputAudio} style={{ width: '100%' }} />
                      <Button
                        type="link"
                        danger
                        size="small"
                        onClick={removeAudio}
                        style={{ marginTop: 8 }}
                      >
                        移除音频
                      </Button>
                    </div>
                  )}
                </div>
              )}

              <div className="action-buttons">
                <Space>
                  <Button
                    type="primary"
                    size="large"
                    loading={isProcessing}
                    onClick={handleUnderstanding}
                    icon={<EyeOutlined />}
                  >
                    开始理解
                  </Button>
                  <Button
                    danger
                    icon={<ClearOutlined />}
                    onClick={handleClear}
                  >
                    清空
                  </Button>
                </Space>
              </div>
            </Card>
          </Col>

          {/* 右侧结果展示 */}
          <Col xs={24} lg={12}>
            <Card title="理解结果" className="result-panel">
              {!understandingResult ? (
                <div className="empty-state">
                  <div className="empty-icon">
                    <EyeOutlined style={{ fontSize: 64, color: '#ccc' }} />
                  </div>
                  <Paragraph type="secondary">
                    提供内容并点击理解按钮，系统将为您进行深度分析和理解
                  </Paragraph>
                </div>
              ) : (
                <div className="understanding-result">
                  <div className="result-header">
                    <Title level={4}>内容理解分析</Title>
                    <div className="confidence-score">
                      <Progress
                        type="circle"
                        percent={Math.round(understandingResult.confidence * 100)}
                        size={60}
                        strokeColor={{
                          '0%': '#108ee9',
                          '100%': '#87d068',
                        }}
                      />
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>综合摘要</Title>
                    <Paragraph>{understandingResult.summary}</Paragraph>
                  </div>

                  <div className="result-section">
                    <Title level={5}>关键要点</Title>
                    <ul className="key-points-list">
                      {understandingResult.keyPoints.map((point: string, index: number) => (
                        <li key={index}>{point}</li>
                      ))}
                    </ul>
                  </div>

                  <div className="result-section">
                    <Title level={5}>实体识别</Title>
                    <div className="entities-grid">
                      {understandingResult.entities.map((entity: any, index: number) => (
                        <Card key={index} size="small" className="entity-card">
                          <div className="entity-name">{entity.name}</div>
                          <Tag color="blue">{entity.type}</Tag>
                          <div className="entity-confidence">
                            <Progress
                              percent={Math.round(entity.confidence * 100)}
                              size="small"
                              strokeColor="#52c41a"
                            />
                          </div>
                        </Card>
                      ))}
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>情感分析</Title>
                    <div className="sentiment-analysis">
                      <Space>
                        <Tag color={understandingResult.sentiment.label === '积极' ? 'green' : understandingResult.sentiment.label === '消极' ? 'red' : 'orange'}>
                          {understandingResult.sentiment.label}
                        </Tag>
                        <Progress
                          percent={Math.round(understandingResult.sentiment.score * 100)}
                          size="small"
                          style={{ width: 120 }}
                        />
                      </Space>
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>分类标签</Title>
                    <div className="categories">
                      <Space wrap>
                        {understandingResult.categories.map((category: string, index: number) => (
                          <Tag key={index} color="cyan">{category}</Tag>
                        ))}
                      </Space>
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>处理信息</Title>
                    <div className="processing-info">
                      <Space>
                        <Tag color="blue">语言: {understandingResult.language}</Tag>
                        <Tag color="green">耗时: {understandingResult.processingTime}</Tag>
                      </Space>
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>优化建议</Title>
                    <ul className="recommendations-list">
                      {understandingResult.recommendations.map((rec: string, index: number) => (
                        <li key={index}>{rec}</li>
                      ))}
                    </ul>
                  </div>

                  <div className="result-actions">
                    <Button
                      type="primary"
                      icon={<DownloadOutlined />}
                      onClick={handleDownload}
                      block
                    >
                      下载结果
                    </Button>
                  </div>
                </div>
              )}
            </Card>

            {/* 使用说明 */}
            <Card title="使用说明" className="help-panel" size="small">
              <ul className="help-list">
                <li>选择适合的理解模式，根据您的内容类型进行选择</li>
                <li>上传或输入对应类型的内容数据</li>
                <li>支持多种格式的文件上传和处理</li>
                <li>理解过程可能需要一些时间，请耐心等待</li>
                <li>结果包含综合分析、实体识别、情感分析等信息</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default ContentUnderstandingPage