import React, { useState } from 'react'
import { Card, Input, Button, Row, Col, Upload, message, Typography, Space, Tag, Select, Divider } from 'antd'
import { UploadOutlined, MergeCellsOutlined, DownloadOutlined, ClearOutlined, EyeOutlined, SoundOutlined } from '@ant-design/icons'
import './MultimodalFusionPage.scss'

const { Title, Paragraph } = Typography
const { TextArea } = Input
const { Option } = Select

const MultimodalFusionPage: React.FC = () => {
  const [textInput, setTextInput] = useState('')
  const [imageFiles, setImageFiles] = useState<File[]>([])
  const [audioFiles, setAudioFiles] = useState<File[]>([])
  const [fusionResult, setFusionResult] = useState<any>(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [fusionMode, setFusionMode] = useState('text-image')

  // 处理文件上传
  const handleImageUpload = (file: File) => {
    setImageFiles(prev => [...prev, file])
    return false // 阻止自动上传
  }

  const handleAudioUpload = (file: File) => {
    setAudioFiles(prev => [...prev, file])
    return false // 阻止自动上传
  }

  // 移除文件
  const removeImage = (index: number) => {
    setImageFiles(prev => prev.filter((_, i) => i !== index))
  }

  const removeAudio = (index: number) => {
    setAudioFiles(prev => prev.filter((_, i) => i !== index))
  }

  // 多模态融合处理
  const handleFusion = async () => {
    if (!textInput.trim() && imageFiles.length === 0 && audioFiles.length === 0) {
      message.warning('请至少提供一种模态的输入数据')
      return
    }

    setIsProcessing(true)
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 3000))
      
      // 模拟融合结果
      const mockResult = {
        type: fusionMode,
        description: '融合结果描述：基于提供的多模态数据，生成了综合性的内容理解和分析结果。',
        confidence: 0.95,
        details: {
          textAnalysis: '文本内容分析完成，识别出关键主题和情感倾向。',
          imageAnalysis: '图像内容分析完成，识别出主要物体和场景特征。',
          audioAnalysis: '音频内容分析完成，识别出语音内容和音调特征。',
          fusionScore: 0.89
        },
        recommendations: [
          '建议增加更多文本描述以获得更准确的分析结果',
          '图像质量可以进一步提升，建议使用更高分辨率的图片',
          '音频清晰度良好，可以继续添加更多音频样本'
        ]
      }
      
      setFusionResult(mockResult)
      message.success('多模态融合分析完成！')
    } catch (error) {
      message.error('融合处理失败')
      console.error('融合处理失败:', error)
    } finally {
      setIsProcessing(false)
    }
  }

  // 清空所有输入
  const handleClear = () => {
    setTextInput('')
    setImageFiles([])
    setAudioFiles([])
    setFusionResult(null)
  }

  // 下载结果
  const handleDownload = () => {
    if (fusionResult) {
      const dataStr = JSON.stringify(fusionResult, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'multimodal-fusion-result.json'
      link.click()
      URL.revokeObjectURL(url)
      message.success('融合结果下载成功！')
    }
  }

  const fusionModes = [
    { value: 'text-image', label: '文本+图像', description: '结合文本描述和图像内容进行综合分析' },
    { value: 'text-audio', label: '文本+音频', description: '结合文本内容和音频信息进行语义理解' },
    { value: 'image-audio', label: '图像+音频', description: '结合视觉和听觉信息进行场景分析' },
    { value: 'all', label: '全模态融合', description: '整合文本、图像和音频所有模态数据' }
  ]

  return (
    <div className="multimodal-fusion-page">
      <div className="page-header">
        <Title level={2}>
          <MergeCellsOutlined /> 多模态融合
        </Title>
        <Paragraph type="secondary">
          整合文本、图像、音频等多种模态数据，实现综合性的智能分析和理解
        </Paragraph>
      </div>

      <div className="fusion-content">
        <Row gutter={[24, 24]}>
          {/* 左侧输入区域 */}
          <Col xs={24} lg={12}>
            <Card title="输入数据" className="input-panel">
              <div className="fusion-mode-section">
                <Title level={5}>融合模式</Title>
                <Select
                  value={fusionMode}
                  onChange={setFusionMode}
                  style={{ width: '100%' }}
                >
                  {fusionModes.map(mode => (
                    <Option key={mode.value} value={mode.value}>
                      {mode.label}
                    </Option>
                  ))}
                </Select>
                <Paragraph type="secondary" style={{ marginTop: 8 }}>
                  {fusionModes.find(m => m.value === fusionMode)?.description}
                </Paragraph>
              </div>

              <Divider />

              {/* 文本输入 */}
              {(fusionMode === 'text-image' || fusionMode === 'text-audio' || fusionMode === 'all') && (
                <div className="input-section">
                  <Title level={5}>
                    <SoundOutlined /> 文本输入
                  </Title>
                  <TextArea
                    rows={4}
                    value={textInput}
                    onChange={(e) => setTextInput(e.target.value)}
                    placeholder="输入文本内容，描述您的需求或提供相关信息..."
                    maxLength={2000}
                    showCount
                  />
                </div>
              )}

              {/* 图像上传 */}
              {(fusionMode === 'text-image' || fusionMode === 'image-audio' || fusionMode === 'all') && (
                <div className="input-section">
                  <Title level={5}>
                    <EyeOutlined /> 图像输入
                  </Title>
                  <Upload
                    multiple
                    accept="image/*"
                    beforeUpload={handleImageUpload}
                    showUploadList={false}
                  >
                    <Button icon={<UploadOutlined />}>上传图像</Button>
                  </Upload>
                  
                  {imageFiles.length > 0 && (
                    <div className="file-list">
                      <Title level={5}>已上传的图像:</Title>
                      <Space direction="vertical" style={{ width: '100%' }}>
                        {imageFiles.map((file, index) => (
                          <Card key={index} size="small" className="file-item">
                            <div className="file-info">
                              <span>{file.name}</span>
                              <Button
                                type="link"
                                danger
                                size="small"
                                onClick={() => removeImage(index)}
                              >
                                移除
                              </Button>
                            </div>
                          </Card>
                        ))}
                      </Space>
                    </div>
                  )}
                </div>
              )}

              {/* 音频上传 */}
              {(fusionMode === 'text-audio' || fusionMode === 'image-audio' || fusionMode === 'all') && (
                <div className="input-section">
                  <Title level={5}>
                    <SoundOutlined /> 音频输入
                  </Title>
                  <Upload
                    multiple
                    accept="audio/*"
                    beforeUpload={handleAudioUpload}
                    showUploadList={false}
                  >
                    <Button icon={<UploadOutlined />}>上传音频</Button>
                  </Upload>
                  
                  {audioFiles.length > 0 && (
                    <div className="file-list">
                      <Title level={5}>已上传的音频:</Title>
                      <Space direction="vertical" style={{ width: '100%' }}>
                        {audioFiles.map((file, index) => (
                          <Card key={index} size="small" className="file-item">
                            <div className="file-info">
                              <span>{file.name}</span>
                              <Button
                                type="link"
                                danger
                                size="small"
                                onClick={() => removeAudio(index)}
                              >
                                移除
                              </Button>
                            </div>
                          </Card>
                        ))}
                      </Space>
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
                    onClick={handleFusion}
                    icon={<MergeCellsOutlined />}
                  >
                    开始融合
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
            <Card title="融合结果" className="result-panel">
              {!fusionResult ? (
                <div className="empty-state">
                  <div className="empty-icon">
                    <MergeCellsOutlined style={{ fontSize: 64, color: '#ccc' }} />
                  </div>
                  <Paragraph type="secondary">
                    上传多模态数据并点击融合按钮，系统将为您提供综合分析结果
                  </Paragraph>
                </div>
              ) : (
                <div className="fusion-result">
                  <div className="result-header">
                    <Title level={4}>融合分析结果</Title>
                    <Tag color="green" style={{ marginLeft: 'auto' }}>
                      置信度: {(fusionResult.confidence * 100).toFixed(1)}%
                    </Tag>
                  </div>

                  <div className="result-section">
                    <Title level={5}>综合描述</Title>
                    <Paragraph>{fusionResult.description}</Paragraph>
                  </div>

                  <div className="result-section">
                    <Title level={5}>详细分析</Title>
                    <div className="analysis-details">
                      {fusionResult.details.textAnalysis && (
                        <div className="analysis-item">
                          <Tag color="blue">文本分析</Tag>
                          <Paragraph>{fusionResult.details.textAnalysis}</Paragraph>
                        </div>
                      )}
                      {fusionResult.details.imageAnalysis && (
                        <div className="analysis-item">
                          <Tag color="green">图像分析</Tag>
                          <Paragraph>{fusionResult.details.imageAnalysis}</Paragraph>
                        </div>
                      )}
                      {fusionResult.details.audioAnalysis && (
                        <div className="analysis-item">
                          <Tag color="orange">音频分析</Tag>
                          <Paragraph>{fusionResult.details.audioAnalysis}</Paragraph>
                        </div>
                      )}
                      <div className="analysis-item">
                        <Tag color="purple">融合评分</Tag>
                        <Paragraph>融合效果评分: {(fusionResult.details.fusionScore * 100).toFixed(1)}%</Paragraph>
                      </div>
                    </div>
                  </div>

                  <div className="result-section">
                    <Title level={5}>优化建议</Title>
                    <ul className="recommendations-list">
                      {fusionResult.recommendations.map((rec: string, index: number) => (
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
                <li>选择适合的融合模式，根据您的数据类型进行选择</li>
                <li>上传或输入对应模态的数据内容</li>
                <li>支持同时处理多种格式的文件</li>
                <li>融合过程可能需要一些时间，请耐心等待</li>
                <li>结果包含综合分析、详细信息和优化建议</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default MultimodalFusionPage