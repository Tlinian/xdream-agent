import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, Upload, Progress, List, Tag, Space, Row, Col, Tabs, Modal, message, Switch } from 'antd'
import { SoundOutlined, DownloadOutlined, EyeOutlined, FileTextOutlined, TranslationOutlined, ClockCircleOutlined, DeleteOutlined } from '@ant-design/icons'
import './index.scss'

const { TextArea } = Input
const { Option } = Select
const { Dragger } = Upload

interface TranscriptionTask {
  id: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  progress: number
  audioFile: File
  audioUrl: string
  result?: {
    text: string
    segments: Array<{
      start: number
      end: number
      text: string
      confidence: number
    }>
    language: string
    duration: number
    summary?: string
  }
  createdAt: number
  metadata: {
    filename: string
    size: number
    duration?: number
  }
}

interface TranscriptionTemplate {
  id: string
  name: string
  description: string
  language: string
  enableSpeakerDiarization: boolean
  enablePunctuation: boolean
  enableWordTimestamps: boolean
}

const AudioTranscriptionPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [currentTask, setCurrentTask] = useState<TranscriptionTask | null>(null)
  const [taskList, setTaskList] = useState<TranscriptionTask[]>([])
  const [activeTab, setActiveTab] = useState('transcription')
  const [uploadedFile, setUploadedFile] = useState<File | null>(null)
  const [previewModal, setPreviewModal] = useState<{ visible: boolean; data: any }>({ visible: false, data: null })

  const transcriptionTemplates: TranscriptionTemplate[] = [
    {
      id: 'meeting',
      name: '会议记录',
      description: '适合会议录音转录，支持多人发言识别',
      language: 'zh',
      enableSpeakerDiarization: true,
      enablePunctuation: true,
      enableWordTimestamps: true
    },
    {
      id: 'interview',
      name: '采访记录',
      description: '适合采访录音转录，精确时间戳',
      language: 'zh',
      enableSpeakerDiarization: true,
      enablePunctuation: true,
      enableWordTimestamps: true
    },
    {
      id: 'lecture',
      name: '讲座转录',
      description: '适合讲座录音转录，支持长音频',
      language: 'zh',
      enablePunctuation: true,
      enableWordTimestamps: false,
      enableSpeakerDiarization: false
    },
    {
      id: 'podcast',
      name: '播客转录',
      description: '适合播客音频转录，自然对话',
      language: 'auto',
      enableSpeakerDiarization: true,
      enablePunctuation: true,
      enableWordTimestamps: true
    }
  ]

  useEffect(() => {
    const savedTasks = localStorage.getItem('audioTranscriptionTasks')
    if (savedTasks) {
      setTaskList(JSON.parse(savedTasks))
    }
  }, [])

  useEffect(() => {
    localStorage.setItem('audioTranscriptionTasks', JSON.stringify(taskList))
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
      const taskId = `transcription_${Date.now()}`
      const audioUrl = URL.createObjectURL(uploadedFile)
      
      const newTask: TranscriptionTask = {
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
        }
      }

      setCurrentTask(newTask)
      setTaskList(prev => [newTask, ...prev])

      // 模拟转录进度
      let progress = 0
      const progressInterval = setInterval(() => {
        progress += Math.random() * 15
        if (progress >= 100) {
          progress = 100
          clearInterval(progressInterval)
          
          // 模拟转录结果
          const mockResult = {
            text: `这是音频转录的模拟结果。音频文件 ${uploadedFile.name} 的内容已经被成功转录。转录过程包括语音识别、文本整理和时间戳生成。转录质量取决于音频的清晰度和语音的识别难度。`,
            segments: [
              { start: 0, end: 5, text: '这是音频转录的模拟结果。', confidence: 0.95 },
              { start: 5, end: 12, text: `音频文件 ${uploadedFile.name} 的内容已经被成功转录。`, confidence: 0.92 },
              { start: 12, end: 18, text: '转录过程包括语音识别、文本整理和时间戳生成。', confidence: 0.88 },
              { start: 18, end: 25, text: '转录质量取决于音频的清晰度和语音的识别难度。', confidence: 0.90 }
            ],
            language: (values as any).language || 'zh',
            duration: 180,
            summary: '音频转录完成，包含主要内容和关键信息。'
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
          
          message.success('音频转录完成！')
          setLoading(false)
        } else {
          setCurrentTask(prev => prev ? { ...prev, progress } : null)
        }
      }, 500)

    } catch (error) {
      message.error('转录失败，请重试！')
      setLoading(false)
    }
  }

  const handleTemplateSelect = (templateId: string) => {
    const template = transcriptionTemplates.find(t => t.id === templateId)
    if (template) {
      form.setFieldsValue({
        language: template.language,
        enableSpeakerDiarization: template.enableSpeakerDiarization,
        enablePunctuation: template.enablePunctuation,
        enableWordTimestamps: template.enableWordTimestamps
      })
      message.success(`已选择模板: ${template.name}`)
    }
  }

  const handlePreview = (task: TranscriptionTask) => {
    if (task.result) {
      setPreviewModal({ visible: true, data: task.result })
    }
  }

  const handleDownload = (task: TranscriptionTask) => {
    if (task.result) {
      const content = JSON.stringify(task.result, null, 2)
      const blob = new Blob([content], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `transcription_${task.id}.json`
      link.click()
      URL.revokeObjectURL(url)
    }
  }

  const handleDelete = (taskId: string) => {
    setTaskList(prev => prev.filter(task => task.id !== taskId))
    if (currentTask?.id === taskId) {
      setCurrentTask(null)
    }
    message.success('任务已删除')
  }

  const renderTranscriptionResult = (result: any) => {
    return (
      <div className="transcription-result">
        <div className="result-summary">
          <h4>转录摘要</h4>
          <p>{result.summary}</p>
          <div className="result-stats">
            <Tag icon={<ClockCircleOutlined />}>时长: {formatTime(result.duration)}</Tag>
            <Tag icon={<TranslationOutlined />}>语言: {result.language}</Tag>
            <Tag icon={<FileTextOutlined />}>段落: {result.segments.length}</Tag>
          </div>
        </div>
        
        <div className="result-text">
          <h4>完整文本</h4>
          <TextArea
            value={result.text}
            readOnly
            rows={8}
            className="transcription-text"
          />
        </div>
        
        <div className="result-segments">
          <h4>时间戳分段</h4>
          <List
            dataSource={result.segments}
            renderItem={(segment: any) => (
              <List.Item className="segment-item">
                <div className="segment-time">
                  {formatTime(segment.start)} - {formatTime(segment.end)}
                </div>
                <div className="segment-text">{segment.text}</div>
                <div className="segment-confidence">
                  <Tag color={segment.confidence > 0.9 ? 'success' : segment.confidence > 0.8 ? 'warning' : 'error'}>
                    {(segment.confidence * 100).toFixed(1)}%
                  </Tag>
                </div>
              </List.Item>
            )}
          />
        </div>
      </div>
    )
  }

  return (
    <div className="audio-transcription-page">
      <div className="page-header">
        <div className="header-content">
          <h1>音频转录</h1>
          <p>智能音频转录服务，支持多种语言和格式</p>
        </div>
      </div>

      <div className="page-content">
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={[
          {
            key: 'transcription',
            label: '音频转录',
            children: (
              <Row gutter={24}>
                <Col span={16}>
                  <Card title="转录设置" className="transcription-card">
                    <Form
                      form={form}
                      layout="vertical"
                      onFinish={handleSubmit}
                      initialValues={{
                        language: 'auto',
                        enableSpeakerDiarization: false,
                        enablePunctuation: true,
                        enableWordTimestamps: true
                      }}
                    >
                      <Form.Item
                        label="上传音频文件"
                        required
                      >
                        <Dragger
                          accept="audio/*,video/*"
                          beforeUpload={handleFileUpload}
                          maxCount={1}
                          fileList={uploadedFile ? [uploadedFile as any] : []}
                        >
                          <p className="ant-upload-drag-icon">
                            <SoundOutlined />
                          </p>
                          <p className="ant-upload-text">点击或拖拽音频文件到此处上传</p>
                          <p className="ant-upload-hint">
                            支持音频和视频文件，最大100MB
                          </p>
                        </Dragger>
                      </Form.Item>

                      <Row gutter={16}>
                        <Col span={12}>
                          <Form.Item
                            label="语言"
                            name="language"
                          >
                            <Select placeholder="选择语言">
                              <Option value="auto">自动检测</Option>
                              <Option value="zh">中文</Option>
                              <Option value="en">英文</Option>
                              <Option value="ja">日文</Option>
                              <Option value="ko">韩文</Option>
                              <Option value="es">西班牙文</Option>
                              <Option value="fr">法文</Option>
                              <Option value="de">德文</Option>
                            </Select>
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item
                            label="说话人分离"
                            name="enableSpeakerDiarization"
                            valuePropName="checked"
                          >
                            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                          </Form.Item>
                        </Col>
                      </Row>

                      <Row gutter={16}>
                        <Col span={12}>
                          <Form.Item
                            label="自动标点"
                            name="enablePunctuation"
                            valuePropName="checked"
                          >
                            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                          </Form.Item>
                        </Col>
                        <Col span={12}>
                          <Form.Item
                            label="词级时间戳"
                            name="enableWordTimestamps"
                            valuePropName="checked"
                          >
                            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                          </Form.Item>
                        </Col>
                      </Row>

                      <Form.Item>
                        <Space>
                          <Button
                            type="primary"
                            htmlType="submit"
                            loading={loading}
                            icon={<SoundOutlined />}
                          >
                            开始转录
                          </Button>
                          <Button
                            onClick={() => {
                              form.resetFields()
                              setUploadedFile(null)
                            }}
                          >
                            重置
                          </Button>
                        </Space>
                      </Form.Item>
                    </Form>
                  </Card>
                </Col>

                <Col span={8}>
                  <Card title="当前任务" className="task-card">
                    {currentTask ? (
                      <div className="task-info">
                        <div className="task-header">
                          <h4>音频转录</h4>
                          <Tag color={currentTask.status === 'completed' ? 'success' : currentTask.status === 'failed' ? 'error' : 'processing'}>
                            {currentTask.status}
                          </Tag>
                        </div>
                        
                        {currentTask.status === 'processing' && (
                          <div className="task-progress">
                            <Progress
                              percent={currentTask.progress}
                              status="active"
                              strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }}
                            />
                            <p className="progress-text">
                              {currentTask.progress}% 完成
                            </p>
                          </div>
                        )}

                        <div className="task-details">
                          <p><strong>文件名:</strong> {(currentTask as any).metadata.filename}</p>
                          <p><strong>文件大小:</strong> {((currentTask as any).metadata.size / 1024 / 1024).toFixed(2)}MB</p>
                          <p><strong>创建时间:</strong> {new Date(currentTask.createdAt).toLocaleString()}</p>
                        </div>

                        {currentTask.result && (
                          <div className="task-result">
                            <Button
                              type="primary"
                              icon={<EyeOutlined />}
                              onClick={() => handlePreview(currentTask)}
                              block
                            >
                              查看结果
                            </Button>
                          </div>
                        )}
                      </div>
                    ) : (
                      <div className="no-task">
                        <p>暂无进行中的任务</p>
                      </div>
                    )}
                  </Card>

                  <Card title="转录模板" className="template-card">
                    <List
                      dataSource={transcriptionTemplates}
                      renderItem={template => (
                        <List.Item
                          className="template-item"
                          onClick={() => handleTemplateSelect(template.id)}
                        >
                          <List.Item.Meta
                            title={template.name}
                            description={template.description}
                          />
                        </List.Item>
                      )}
                    />
                  </Card>
                </Col>
              </Row>
            )
          },
          {
            key: 'history',
            label: '历史记录',
            children: (
              <Card title="转录历史" className="history-card">
                <List
                  dataSource={taskList}
                  renderItem={task => (
                    <List.Item
                      actions={[
                        <Button
                          type="link"
                          icon={<EyeOutlined />}
                          onClick={() => handlePreview(task)}
                        >
                          查看
                        </Button>,
                        <Button
                          type="link"
                          icon={<DownloadOutlined />}
                          onClick={() => handleDownload(task)}
                        >
                          下载
                        </Button>,
                        <Button
                          type="link"
                          danger
                          icon={<DeleteOutlined />}
                          onClick={() => handleDelete(task.id)}
                        >
                          删除
                        </Button>
                      ]}
                    >
                      <List.Item.Meta
                        title={
                          <div className="task-title">
                            <span>{(task as any).metadata.filename}</span>
                            <Tag color={task.status === 'completed' ? 'success' : task.status === 'failed' ? 'error' : 'processing'}>
                              {task.status}
                            </Tag>
                          </div>
                        }
                        description={
                          <div className="task-desc">
                            <p>
                              <ClockCircleOutlined /> 创建时间: {new Date(task.createdAt).toLocaleString()}
                            </p>
                            <p>
                              <SoundOutlined /> 时长: {(task as any).metadata.duration ? formatTime((task as any).metadata.duration) : '未知'}
                            </p>
                            {task.result && (
                              <p>
                                <FileTextOutlined /> 文本长度: {task.result.text.length} 字符
                              </p>
                            )}
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
      </div>

      {/* 结果预览模态框 */}
      <Modal
        title="转录结果"
        visible={previewModal.visible}
        onCancel={() => setPreviewModal({ visible: false, data: null })}
        footer={null}
        width={800}
      >
        {previewModal.data && renderTranscriptionResult(previewModal.data)}
      </Modal>
    </div>
  )
}

export default AudioTranscriptionPage