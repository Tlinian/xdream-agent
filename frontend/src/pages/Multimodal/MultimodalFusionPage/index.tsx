import React, { useState, useEffect } from 'react'
import { Card, Form, Input, Select, Button, Upload, Row, Col, Space, Tag, Progress, message, Tabs, Modal, List, Avatar, Slider, Typography } from 'antd'

const { Text } = Typography
import { UploadOutlined, PlayCircleOutlined, DownloadOutlined, EyeOutlined } from '@ant-design/icons'

import './index.scss'

interface MultimodalFusionTask {
  id: string
  userId: string
  status: string
  fusionType: string
  inputs: any
  parameters: any
  result: any
  error: any
  progress: number
  priority: number
  createdAt: string
}

interface MultimodalFusionRequest {
  text?: string
  image?: string
  audio?: string
  video?: string
  fusionType: string
  outputFormat: string
  parameters: any
}

const { TextArea } = Input
const { Option } = Select

const MultimodalFusionPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [currentTask, setCurrentTask] = useState<MultimodalFusionTask | null>(null)
  const [taskList, setTaskList] = useState<MultimodalFusionTask[]>([])
  const [templates, setTemplates] = useState<any[]>([])
  const [activeTab, setActiveTab] = useState('fusion')
  const [previewModal, setPreviewModal] = useState({ visible: false, data: null as any })

  // 预设模板
  const defaultTemplates = [
    {
      id: 'video_audio',
      name: '视频+音频融合',
      description: '将视频内容与音频内容智能融合',
      type: 'video_audio',
      config: {
        videoWeight: 0.7,
        audioWeight: 0.3,
        syncMode: 'auto',
        quality: 'high'
      }
    },
    {
      id: 'image_text',
      name: '图像+文本融合',
      description: '将图像与文本内容融合生成新内容',
      type: 'image_text',
      config: {
        imageWeight: 0.6,
        textWeight: 0.4,
        style: 'creative',
        resolution: '1024x1024'
      }
    },
    {
      id: 'multimodal',
      name: '多模态综合融合',
      description: '整合多种模态内容生成综合结果',
      type: 'multimodal',
      config: {
        videoWeight: 0.4,
        audioWeight: 0.2,
        imageWeight: 0.3,
        textWeight: 0.1,
        fusionMode: 'advanced',
        quality: 'ultra'
      }
    }
  ]

  useEffect(() => {
    setTemplates(defaultTemplates)
    loadTaskHistory()
  }, [])

  // 加载任务历史
  const loadTaskHistory = async () => {
    try {
      // 使用本地存储代替不存在的API
      const savedTasks = localStorage.getItem('multimodalFusionTasks')
      if (savedTasks) {
        const tasks = JSON.parse(savedTasks)
        setTaskList(tasks)
      } else {
        setTaskList([])
      }
    } catch (error) {
      console.error('加载任务历史失败:', error)
      message.error('加载任务历史失败')
      setTaskList([])
    }
  }

  // 处理表单提交
  const handleSubmit = async (values: any) => {
    setLoading(true)
    try {
      const request: MultimodalFusionRequest = {
        text: values.text,
        image: values.image,
        audio: values.audio,
        video: values.video,
        fusionType: values.fusionType || 'text-image',
        outputFormat: values.outputFormat || 'text',
        parameters: {
          textWeight: values.textWeight || 0.5,
          imageWeight: values.imageWeight || 0.5,
          audioWeight: values.audioWeight || 0.5,
          videoWeight: values.videoWeight || 0.5,
          style: values.style || 'balanced',
          quality: values.quality || 0.8
        }
      }

      // 直接调用多模态服务的融合API
      const response = await fetch('http://127.0.0.1:8085/api/v1/multimodal/fusion', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      
      // 创建模拟融合结果
      const mockResult = {
        type: request.fusionType,
        summary: '这是一个模拟的多模态融合结果，展示了不同模态内容的智能融合。',
        confidence: 0.88,
        outputs: {
          text: '融合生成的文本内容',
          image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==',
          audio: 'data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBTGH0fPTgjMGHm7A7+OZURE',
          video: null
        },
        metadata: {
          processingTime: 2500,
          fusionScore: 0.91,
          qualityMetrics: {
            coherence: 0.89,
            relevance: 0.92,
            diversity: 0.85
          }
        }
      }
      
      // 创建任务对象
      const task: MultimodalFusionTask = {
        id: result.taskId || `fusion_task_${Date.now()}`,
        userId: '',
        status: 'completed',
        fusionType: request.fusionType,
        inputs: {
          text: request.text,
          image: request.image,
          audio: request.audio,
          video: request.video
        },
        parameters: request,
        result: mockResult,
        error: null,
        progress: 100,
        priority: 1,
        createdAt: new Date().toISOString()
      }
      
      // 保存到本地存储
      const savedTasks = localStorage.getItem('multimodalFusionTasks')
      const tasks = savedTasks ? JSON.parse(savedTasks) : []
      tasks.unshift(task)
      localStorage.setItem('multimodalFusionTasks', JSON.stringify(tasks))
      
      setCurrentTask(task)
      setTaskList(tasks)
      message.success('多模态融合任务创建成功')
      
      // 简化的轮询（立即完成）
      setTimeout(() => {
        setCurrentTask(prev => prev ? {...prev, status: 'completed', progress: 100} : null)
      }, 1000)
      
    } catch (error) {
      console.error('创建融合任务失败:', error)
      message.error('创建融合任务失败')
    } finally {
      setLoading(false)
    }
  }



  // 处理模板选择
  const handleTemplateSelect = (templateId: string) => {
    const template = templates.find(t => t.id === templateId)
    if (template) {
      form.setFieldsValue({
        templateId,
        fusionType: template.type,
        ...template.config
      })
    }
  }

  // 处理文件上传
  const handleFileUpload = (info: any, field: string) => {
    if (info.file.status === 'done') {
      message.success(`${info.file.name} 上传成功`)
      const currentFiles = form.getFieldValue(field) || []
      form.setFieldsValue({
        [field]: [...currentFiles, info.file.response.url]
      })
    } else if (info.file.status === 'error') {
      message.error(`${info.file.name} 上传失败`)
    }
  }

  // 预览结果
  const handlePreview = (task: MultimodalFusionTask) => {
    setPreviewModal({ visible: true, data: task.result })
  }

  // 下载结果
  const handleDownload = async (task: MultimodalFusionTask) => {
    try {
      if (task.result) {
        // 从任务结果创建JSON文件并下载
        const dataStr = JSON.stringify(task.result, null, 2)
        const dataBlob = new Blob([dataStr], { type: 'application/json' })
        const url = URL.createObjectURL(dataBlob)
        const link = document.createElement('a')
        link.href = url
        link.download = `fusion-result-${task.id}.json`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
        message.success('下载成功')
      } else {
        message.warning('没有可下载的结果')
      }
    } catch (error) {
      console.error('下载失败:', error)
      message.error('下载失败')
    }
  }

  // 删除任务
  const handleDelete = async (taskId: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个融合任务吗？',
      onOk: async () => {
        try {
          // 从本地存储中删除任务
          const savedTasks = localStorage.getItem('multimodalFusionTasks')
          if (savedTasks) {
            const tasks = JSON.parse(savedTasks)
            const updatedTasks = tasks.filter((task: MultimodalFusionTask) => task.id !== taskId)
            localStorage.setItem('multimodalFusionTasks', JSON.stringify(updatedTasks))
            setTaskList(updatedTasks)
            message.success('删除成功')
          }
        } catch (error) {
          console.error('删除失败:', error)
          message.error('删除失败')
        }
      }
    })
  }

  return (
    <div className="multimodal-fusion-page">
      <div className="page-header">
        <div className="header-content">
          <h1>多模态内容融合</h1>
          <p>将多种模态的内容智能融合，生成创新的多媒体内容</p>
        </div>
      </div>

      <div className="page-content">
        <Tabs activeKey={activeTab} onChange={setActiveTab} items={[
          {
            key: 'fusion',
            label: '内容融合',
            children: (
            <Row gutter={24}>
              <Col span={16}>
                <Card title="融合设置" className="fusion-card">
                  <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    initialValues={{
                      inputType: 'text',
                      outputType: 'video',
                      quality: 'high',
                      fusionMode: 'standard',
                      videoWeight: 0.5,
                      audioWeight: 0.5,
                      imageWeight: 0.5,
                      textWeight: 0.5
                    }}
                  >
                    <Form.Item
                      label="融合描述"
                      name="prompt"
                      rules={[{ required: true, message: '请输入融合描述' }]}
                    >
                      <TextArea
                        rows={4}
                        placeholder="描述您想要融合的内容和目标效果..."
                      />
                    </Form.Item>

                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item
                          label="输入类型"
                          name="inputType"
                          rules={[{ required: true }]}
                        >
                          <Select placeholder="选择输入类型">
                            <Option value="text">文本</Option>
                            <Option value="image">图像</Option>
                            <Option value="video">视频</Option>
                            <Option value="audio">音频</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item
                          label="输出类型"
                          name="outputType"
                          rules={[{ required: true }]}
                        >
                          <Select placeholder="选择输出类型">
                            <Option value="video">视频</Option>
                            <Option value="image">图像</Option>
                            <Option value="audio">音频</Option>
                            <Option value="text">文本</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>

                    <Form.Item
                      label="预设模板"
                      name="templateId"
                    >
                      <Select
                        placeholder="选择预设模板（可选）"
                        onChange={handleTemplateSelect}
                      >
                        {templates.map(template => (
                          <Option key={template.id} value={template.id}>
                            {template.name}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>

                    <Row gutter={16}>
                      <Col span={8}>
                        <Form.Item
                          label="视频权重"
                          name="videoWeight"
                        >
                          <Slider
                            min={0}
                            max={1}
                            step={0.1}
                            marks={{ 0: '0', 0.5: '0.5', 1: '1' }}
                          />
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item
                          label="音频权重"
                          name="audioWeight"
                        >
                          <Slider
                            min={0}
                            max={1}
                            step={0.1}
                            marks={{ 0: '0', 0.5: '0.5', 1: '1' }}
                          />
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item
                          label="图像权重"
                          name="imageWeight"
                        >
                          <Slider
                            min={0}
                            max={1}
                            step={0.1}
                            marks={{ 0: '0', 0.5: '0.5', 1: '1' }}
                          />
                        </Form.Item>
                      </Col>
                    </Row>

                    <Form.Item
                      label="输入文件"
                      name="inputFiles"
                    >
                      <Upload
                        multiple
                        accept="image/*,video/*,audio/*"
                        action="/api/v1/upload"
                        onChange={(info) => handleFileUpload(info, 'inputFiles')}
                      >
                        <Button icon={<UploadOutlined />}>上传输入文件</Button>
                      </Upload>
                    </Form.Item>

                    <Form.Item
                      label="参考文件"
                      name="referenceFiles"
                    >
                      <Upload
                        multiple
                        accept="image/*,video/*,audio/*"
                        action="/api/v1/upload"
                        onChange={(info) => handleFileUpload(info, 'referenceFiles')}
                      >
                        <Button icon={<UploadOutlined />}>上传参考文件</Button>
                      </Upload>
                    </Form.Item>

                    <Form.Item>
                      <Space>
                        <Button
                          type="primary"
                          htmlType="submit"
                          loading={loading}
                          icon={<PlayCircleOutlined />}
                        >
                          开始融合
                        </Button>
                        <Button>保存草稿</Button>
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
                        <h4>多模态融合任务</h4>
                        <Tag color={currentTask.status === 'completed' ? 'success' : currentTask.status === 'failed' ? 'error' : 'processing'}>
                          {currentTask.status}
                        </Tag>
                      </div>
                      
                      {currentTask.status === 'processing' && (
                        <div className="task-progress">
                          <Progress
                            percent={currentTask.progress || 0}
                            status="active"
                            strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }}
                          />
                          <p className="progress-text">
                            {currentTask.progress || 0}% 完成
                          </p>
                        </div>
                      )}

                      <div className="task-details">
                        <p><strong>创建时间:</strong> {new Date(currentTask.createdAt).toLocaleString()}</p>
                      </div>

                      {currentTask.result && (
                        <div className="task-result">
                          <Button
                            type="primary"
                            icon={<EyeOutlined />}
                            onClick={() => handlePreview(currentTask)}
                            block
                            disabled={currentTask.status !== 'completed'}
                          >
                            预览结果
                          </Button>
                          <Button
                            size="small"
                            danger
                            onClick={() => handleDelete(currentTask.id)}
                            disabled={currentTask.status === 'processing'}
                            style={{ marginTop: 8 }}
                            block
                          >
                            删除任务
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

                <Card title="预设模板" className="template-card">
                  <List
                    dataSource={templates}
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
            <Card title="融合历史" className="history-card">
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
                        预览
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
                        icon={<PlayCircleOutlined />}
                        onClick={() => handleDelete(task.id)}
                      >
                        删除
                      </Button>
                    ]}
                  >
                    <List.Item.Meta
                      avatar={<Avatar icon={<PlayCircleOutlined />} />}
                      title={`任务 ${task.id}`}
                      description={
                        <div>
                          <Text type="secondary">{new Date(task.createdAt).toLocaleString()}</Text>
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

      {/* 预览模态框 */}
      <Modal
        title="预览结果"
        visible={previewModal.visible}
        onCancel={() => setPreviewModal({ visible: false, data: null })}
        footer={null}
        width={800}
      >
        {previewModal.data && (
          <div className="preview-content">
            {previewModal.data.type === 'video' && (
              <video
                src={previewModal.data.url}
                controls
                style={{ width: '100%', maxHeight: '400px' }}
              />
            )}
            {previewModal.data.type === 'image' && (
              <img
                src={previewModal.data.url}
                alt="融合结果"
                style={{ width: '100%', maxHeight: '400px' }}
              />
            )}
            {previewModal.data.type === 'audio' && (
              <audio
                src={previewModal.data.url}
                controls
                style={{ width: '100%' }}
              />
            )}
          </div>
        )}
      </Modal>
    </div>
  )
}

export default MultimodalFusionPage