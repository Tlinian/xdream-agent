import React, { useState, useEffect } from 'react'
import { Card, Form, Select, Button, Upload, Progress, List, Tag, Space, Row, Col, Tabs, Modal, message, Switch, Slider } from 'antd'
import { SoundOutlined, DownloadOutlined, EyeOutlined, FileTextOutlined, BarChartOutlined, DeleteOutlined } from '@ant-design/icons'
import './index.scss'

const { Option } = Select
const { Dragger } = Upload

interface AudioAnalysisTask {
  id: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  progress: number
  audioFile: File
  audioUrl: string
  result?: {
    transcription?: {
      text: string
      segments: Array<{
        start: number
        end: number
        text: string
        confidence: number
      }>
    }
    sentiment?: {
      overall: 'positive' | 'negative' | 'neutral'
      confidence: number
      emotions: Array<{
        emotion: string
        confidence: number
      }>
    }
    speaker?: {
      count: number
      segments: Array<{
        speaker: number
        start: number
        end: number
        text: string
      }>
    }
    music?: {
      genre: string
      tempo: number
      mood: string
      instruments: string[]
    }
    quality?: {
      overall: number
      clarity: number
      noise: number
      volume: number
    }
    language: string
    duration: number
  }
  createdAt: number
  metadata: {
    filename: string
    size: number
    duration?: number
  }
  analysisType: string[]
  type: string
}

const AudioAnalysisPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [currentTask, setCurrentTask] = useState<AudioAnalysisTask | null>(null)
  const [taskList, setTaskList] = useState<AudioAnalysisTask[]>([])
  const [activeTab, setActiveTab] = useState('analysis')
  const [uploadedFile, setUploadedFile] = useState<File | null>(null)
  const [previewModal, setPreviewModal] = useState<{ visible: boolean; data: any }>({ visible: false, data: null })

  useEffect(() => {
    const savedTasks = localStorage.getItem('audioAnalysisTasks')
    if (savedTasks) {
      setTaskList(JSON.parse(savedTasks))
    }
  }, [])

  useEffect(() => {
    localStorage.setItem('audioAnalysisTasks', JSON.stringify(taskList))
  }, [taskList])

  const handleFileUpload = (file: File): boolean => {
    const isAudio = file.type.startsWith('audio/')
    const isVideo = file.type.startsWith('video/')
    
    if (!isAudio && !isVideo) {
      message.error('请上传音频或视频文件！')
      return false
    }

    const isLt100M = file.size / 1024 / 1024 < 100
    if (!isLt100M) {
      message.error('文件大小不能超过100MB！')
      return false
    }

    setUploadedFile(file)
    return false
  }

  const formatTime = (seconds: number): string => {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const secs = Math.floor(seconds % 60)
    
    if (hours > 0) {
      return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }
    return `${minutes}:${secs.toString().padStart(2, '0')}`
  }

  const handleSubmit = async (values: any) => {
    if (!uploadedFile) {
      message.error('请先上传音频文件！')
      return
    }

    setLoading(true)
    
    try {
      const taskId = `audio_analysis_${Date.now()}`
      const audioUrl = URL.createObjectURL(uploadedFile)
      
      const analysisTypes = []
      if (values.enableTranscription) analysisTypes.push('transcription')
      if (values.enableSentiment) analysisTypes.push('sentiment')
      if (values.enableSpeaker) analysisTypes.push('speaker')
      if (values.enableMusic) analysisTypes.push('music')
      if (values.enableQuality) analysisTypes.push('quality')
      
      const newTask: AudioAnalysisTask = {
        id: taskId,
        status: 'processing',
        progress: 0,
        audioFile: uploadedFile,
        audioUrl,
        createdAt: Date.now(),
        metadata: {
          filename: uploadedFile.name,
          size: uploadedFile.size,
          duration: 180
        },
        analysisType: analysisTypes,
        type: 'audio_analysis'
      }

      setCurrentTask(newTask)
      setTaskList(prev => [newTask, ...prev])

      // 模拟分析进度
      let progress = 0
      const progressInterval = setInterval(() => {
        progress += Math.random() * 12
        if (progress >= 100) {
          progress = 100
          clearInterval(progressInterval)
          
          // 模拟分析结果
          const mockResult = {
            transcription: {
              text: `这是音频智能分析的模拟结果。音频文件 ${uploadedFile.name} 的内容包含丰富的信息。通过语音识别技术，我们能够准确地将语音转换为文字。`,
              segments: [
                { start: 0, end: 5, text: '这是音频智能分析的模拟结果。', confidence: 0.95 },
                { start: 5, end: 12, text: `音频文件 ${uploadedFile.name} 的内容包含丰富的信息。`, confidence: 0.92 },
                { start: 12, end: 18, text: '通过语音识别技术，我们能够准确地将语音转换为文字。', confidence: 0.88 }
              ]
            },
            sentiment: {
              overall: 'positive' as const,
              confidence: 0.85,
              emotions: [
                { emotion: 'happy', confidence: 0.7 },
                { emotion: 'excited', confidence: 0.6 },
                { emotion: 'neutral', confidence: 0.3 }
              ]
            },
            speaker: {
              count: 2,
              segments: [
                { speaker: 1, start: 0, end: 8, text: '这是音频智能分析的模拟结果。音频文件的内容包含丰富的信息。' },
                { speaker: 2, start: 8, end: 18, text: '通过语音识别技术，我们能够准确地将语音转换为文字。' }
              ]
            },
            music: {
              genre: 'pop',
              tempo: 120,
              mood: 'upbeat',
              instruments: ['piano', 'guitar', 'drums']
            },
            quality: {
              overall: 85,
              clarity: 90,
              noise: 20,
              volume: 75
            },
            language: (values as any).language || 'zh',
            duration: 180
          }

          const completedTask = {
            ...newTask,
            status: 'completed' as const,
            progress: 100,
            result: mockResult
          }

          setCurrentTask(completedTask)
          setTaskList(prev => prev.map(task => 
            task.id === taskId ? completedTask : task
          ))
          
          message.success('音频智能分析完成！')
          setLoading(false)
        }
        
        setCurrentTask(prev => prev ? { ...prev, progress } : null)
      }, 200)
    } catch (error) {
      message.error('音频分析失败，请重试！')
      setLoading(false)
    }
  }

  const renderAnalysisResult = (task: AudioAnalysisTask) => {
    if (!task.result) return null

    const items = []

    if (task.result.transcription) {
      items.push({
        key: 'transcription',
        label: '语音识别',
        children: (
          <div className="result-section">
            <div className="result-header">
              <h4>转录文本</h4>
              <Tag color="blue">{task.result.language}</Tag>
            </div>
            <div className="transcription-text">
              {task.result.transcription.text}
            </div>
            <div className="segments-timeline">
              <h5>时间轴</h5>
              {task.result.transcription.segments.map((segment, index) => (
                <div key={index} className="segment-item">
                  <span className="segment-time">{formatTime(segment.start)} - {formatTime(segment.end)}</span>
                  <span className="segment-text">{segment.text}</span>
                  <Tag color="green">{Math.round(segment.confidence * 100)}%</Tag>
                </div>
              ))}
            </div>
          </div>
        )
      })
    }

    if (task.result.sentiment) {
      items.push({
        key: 'sentiment',
        label: '情感分析',
        children: (
          <div className="result-section">
            <div className="sentiment-overview">
              <h4>整体情感</h4>
              <div className="sentiment-tag">
                <Tag color={task.result.sentiment.overall === 'positive' ? 'green' : task.result.sentiment.overall === 'negative' ? 'red' : 'orange'}>
                  {task.result.sentiment.overall === 'positive' ? '积极' : task.result.sentiment.overall === 'negative' ? '消极' : '中性'}
                </Tag>
                <span className="confidence">置信度: {Math.round(task.result.sentiment.confidence * 100)}%</span>
              </div>
            </div>
            <div className="emotions-detailed">
              <h5>情感详情</h5>
              {task.result.sentiment.emotions.map((emotion, index) => (
                <div key={index} className="emotion-item">
                  <span className="emotion-name">{emotion.emotion}</span>
                  <Slider
                    value={emotion.confidence * 100}
                    disabled
                    style={{ width: 200, margin: '0 16px' }}
                  />
                  <span className="emotion-score">{Math.round(emotion.confidence * 100)}%</span>
                </div>
              ))}
            </div>
          </div>
        )
      })
    }

    if (task.result.speaker) {
      items.push({
        key: 'speaker',
        label: '说话人识别',
        children: (
          <div className="result-section">
            <div className="speaker-overview">
              <h4>检测到的说话人数量: {task.result.speaker.count}</h4>
            </div>
            <div className="speaker-segments">
              <h5>说话人分段</h5>
              {task.result.speaker.segments.map((segment, index) => (
                <div key={index} className="speaker-segment">
                  <Tag color="blue">说话人 {segment.speaker}</Tag>
                  <span className="segment-time">{formatTime(segment.start)} - {formatTime(segment.end)}</span>
                  <div className="segment-text">{segment.text}</div>
                </div>
              ))}
            </div>
          </div>
        )
      })
    }

    if (task.result.music) {
      items.push({
        key: 'music',
        label: '音乐识别',
        children: (
          <div className="result-section">
            <div className="music-info">
              <h4>音乐信息</h4>
              <div className="music-details">
                <div className="music-item">
                  <span className="label">风格:</span>
                  <Tag color="purple">{task.result.music.genre}</Tag>
                </div>
                <div className="music-item">
                  <span className="label">节拍:</span>
                  <span>{task.result.music.tempo} BPM</span>
                </div>
                <div className="music-item">
                  <span className="label">情绪:</span>
                  <Tag color="orange">{task.result.music.mood}</Tag>
                </div>
                <div className="music-item">
                  <span className="label">乐器:</span>
                  <div className="instruments">
                    {task.result.music.instruments.map((instrument, index) => (
                      <Tag key={index} color="green">{instrument}</Tag>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )
      })
    }

    if (task.result.quality) {
      items.push({
        key: 'quality',
        label: '质量评估',
        children: (
          <div className="result-section">
            <div className="quality-overview">
              <h4>整体质量评分</h4>
              <Progress
                type="circle"
                percent={task.result.quality.overall}
                width={80}
                format={percent => <span style={{ color: '#52c41a' }}>{percent}</span>}
              />
            </div>
            <div className="quality-details">
              <h5>详细指标</h5>
              <div className="quality-item">
                <span className="label">清晰度:</span>
                <Progress percent={task.result.quality.clarity} size="small" strokeColor="#52c41a" />
              </div>
              <div className="quality-item">
                <span className="label">噪音水平:</span>
                <Progress percent={task.result.quality.noise} size="small" strokeColor="#ff4d4f" />
              </div>
              <div className="quality-item">
                <span className="label">音量:</span>
                <Progress percent={task.result.quality.volume} size="small" strokeColor="#1890ff" />
              </div>
            </div>
          </div>
        )
      })
    }

    return <Tabs items={items} />
  }

  return (
    <div className="audio-analysis-page">
      <div className="page-header">
        <div className="header-content">
          <h1 className="page-title">
            <SoundOutlined />
            音频智能分析
          </h1>
          <p className="page-description">
            音频内容识别和分析，包括语音识别、情感分析、音乐分类和声纹识别
          </p>
        </div>
      </div>

      <Tabs activeKey={activeTab} onChange={setActiveTab} items={[
        {
          key: 'analysis',
          label: '音频分析',
          children: (
            <Row gutter={[24, 24]}>
              <Col xs={24} lg={12}>
                <Card title="分析设置" className="analysis-settings-card">
                  <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    initialValues={{
                      language: 'auto',
                      enableTranscription: true,
                      enableSentiment: true,
                      enableSpeaker: false,
                      enableMusic: false,
                      enableQuality: true
                    }}
                  >
                    <Form.Item label="音频文件">
                      <Dragger
                        accept="audio/*,video/*"
                        beforeUpload={handleFileUpload}
                        showUploadList={false}
                        className="audio-upload"
                      >
                        <p className="ant-upload-drag-icon">
                          <SoundOutlined style={{ fontSize: 48, color: '#1890ff' }} />
                        </p>
                        <p className="ant-upload-text">点击或拖拽音频文件到此处</p>
                        <p className="ant-upload-hint">
                          支持 MP3, WAV, M4A, FLAC 等格式，文件大小不超过 100MB
                        </p>
                      </Dragger>
                      {uploadedFile && (
                        <div className="uploaded-file-info">
                          <FileTextOutlined />
                          <span>{uploadedFile.name}</span>
                          <Tag color="blue">{(uploadedFile.size / 1024 / 1024).toFixed(2)} MB</Tag>
                        </div>
                      )}
                    </Form.Item>

                    <Form.Item name="language" label="识别语言">
                      <Select placeholder="选择识别语言">
                        <Option value="auto">自动检测</Option>
                        <Option value="zh">中文</Option>
                        <Option value="en">英文</Option>
                        <Option value="ja">日文</Option>
                        <Option value="ko">韩文</Option>
                        <Option value="fr">法文</Option>
                        <Option value="de">德文</Option>
                        <Option value="es">西班牙文</Option>
                      </Select>
                    </Form.Item>

                    <Form.Item label="分析类型">
                      <Row gutter={[16, 16]}>
                        <Col span={12}>
                          <Form.Item name="enableTranscription" valuePropName="checked" noStyle>
                            <Switch checkedChildren="语音识别" unCheckedChildren="语音识别" />
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item name="enableSentiment" valuePropName="checked" noStyle>
                            <Switch checkedChildren="情感分析" unCheckedChildren="情感分析" />
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item name="enableSpeaker" valuePropName="checked" noStyle>
                            <Switch checkedChildren="说话人识别" unCheckedChildren="说话人识别" />
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item name="enableMusic" valuePropName="checked" noStyle>
                            <Switch checkedChildren="音乐识别" unCheckedChildren="音乐识别" />
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item name="enableQuality" valuePropName="checked" noStyle>
                            <Switch checkedChildren="质量评估" unCheckedChildren="质量评估" />
                          </Form.Item>
                        </Col>
                      </Row>
                    </Form.Item>

                    <Form.Item>
                      <Space>
                        <Button
                          type="primary"
                          htmlType="submit"
                          loading={loading}
                          icon={<BarChartOutlined />}
                        >
                          开始分析
                        </Button>
                        <Button
                          onClick={() => {
                            form.resetFields()
                            setUploadedFile(null)
                            setCurrentTask(null)
                          }}
                        >
                          重置
                        </Button>
                      </Space>
                    </Form.Item>
                  </Form>
                </Card>
              </Col>

              <Col xs={24} lg={12}>
                <Card title="当前任务" className="current-task-card">
                  {currentTask ? (
                    <div className="task-info">
                      <div className="task-header">
                        <h4>{(currentTask as any).metadata.filename}</h4>
                        <Tag color={
                          currentTask.status === 'completed' ? 'green' :
                          currentTask.status === 'processing' ? 'blue' :
                          currentTask.status === 'failed' ? 'red' : 'orange'
                        }>
                          {currentTask.status === 'completed' ? '已完成' :
                           currentTask.status === 'processing' ? '分析中' :
                           currentTask.status === 'failed' ? '失败' : '等待中'}
                        </Tag>
                      </div>
                      
                      <div className="task-progress">
                        <Progress
                          percent={currentTask.progress}
                          status={currentTask.status === 'failed' ? 'exception' : 'active'}
                          strokeColor={{
                            '0%': '#108ee9',
                            '100%': '#87d068',
                          }}
                        />
                      </div>

                      <div className="task-details">
                        <div className="detail-item">
                          <span className="label">文件大小:</span>
                          <span>{((currentTask as any).metadata.size / 1024 / 1024).toFixed(2)} MB</span>
                        </div>
                        <div className="detail-item">
                          <span className="label">创建时间:</span>
                          <span>{new Date(currentTask.createdAt).toLocaleString()}</span>
                        </div>
                        <div className="detail-item">
                          <span className="label">分析类型:</span>
                          <div className="analysis-types">
                            {currentTask.analysisType.map((type) => (
                              <Tag key={type}>
                                {type === 'transcription' ? '语音识别' :
                                 type === 'sentiment' ? '情感分析' :
                                 type === 'speaker' ? '说话人识别' :
                                 type === 'music' ? '音乐识别' :
                                 type === 'quality' ? '质量评估' : type}
                              </Tag>
                            ))}
                          </div>
                        </div>
                      </div>

                      {currentTask.status === 'completed' && currentTask.result && (
                        <div className="task-actions">
                          <Button
                            type="primary"
                            icon={<EyeOutlined />}
                            onClick={() => setPreviewModal({ visible: true, data: currentTask })}
                          >
                            查看结果
                          </Button>
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="no-task">
                      <SoundOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />
                      <p>暂无分析任务</p>
                      <p className="hint">上传音频文件并开始分析</p>
                    </div>
                  )}
                </Card>
              </Col>
            </Row>
          )
        },
        {
          key: 'history',
          label: '历史记录',
          children: (
            <Card title="分析历史" className="history-card">
              <List
                dataSource={taskList}
                renderItem={(task) => (
                  <List.Item
                    className="history-item"
                    actions={[
                      <Button
                        key="view"
                        type="text"
                        icon={<EyeOutlined />}
                        onClick={() => setPreviewModal({ visible: true, data: task })}
                        disabled={task.status !== 'completed'}
                      >
                        查看
                      </Button>,
                      <Button
                        key="download"
                        type="text"
                        icon={<DownloadOutlined />}
                        disabled={task.status !== 'completed'}
                      >
                        下载
                      </Button>,
                      <Button
                        key="delete"
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => {
                          setTaskList(prev => prev.filter(t => t.id !== task.id))
                          message.success('已删除任务')
                        }}
                      >
                        删除
                      </Button>
                    ]}
                  >
                    <List.Item.Meta
                      title={(task as any).metadata.filename}
                      description={
                        <div className="history-item-desc">
                          <div className="item-info">
                            <span>创建时间: {new Date(task.createdAt).toLocaleString()}</span>
                            <span>文件大小: {((task as any).metadata.size / 1024 / 1024).toFixed(2)} MB</span>
                          </div>
                          <div className="item-status">
                            <Tag color={
                              task.status === 'completed' ? 'green' :
                              task.status === 'processing' ? 'blue' :
                              task.status === 'failed' ? 'red' : 'orange'
                            }>
                              {task.status === 'completed' ? '已完成' :
                               task.status === 'processing' ? '分析中' :
                               task.status === 'failed' ? '失败' : '等待中'}
                            </Tag>
                            {task.progress > 0 && <Progress percent={task.progress} size="small" />}
                          </div>
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            </Card>
          )
        }
      ]} />

      <Modal
        title="分析结果详情"
        open={previewModal.visible}
        onCancel={() => setPreviewModal({ visible: false, data: null })}
        footer={null}
        width={800}
      >
        {previewModal.data && renderAnalysisResult(previewModal.data)}
      </Modal>
    </div>
  )
}

export default AudioAnalysisPage