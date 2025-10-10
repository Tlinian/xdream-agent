import React, { useState, useEffect } from 'react'
import { Card, Form, Select, Button, Upload, Row, Col, Space, Tag, Progress, message, Tabs, Modal, List, Descriptions, Badge, Switch } from 'antd'
import { UploadOutlined, EyeOutlined, DownloadOutlined, SearchOutlined, BarChartOutlined, DeleteOutlined, ClockCircleOutlined, TagOutlined } from '@ant-design/icons'
import './index.scss'

const { Option } = Select
const { TabPane } = Tabs
const { Dragger } = Upload

interface ContentUnderstandingTask {
  id: string
  userId: string
  status: string
  type: string
  contentType: string
  analysisType: string
  content: string
  parameters: any
  result?: any
  progress: number
  priority?: number
  createdAt: string
}

const ContentUnderstandingPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [currentTask, setCurrentTask] = useState<ContentUnderstandingTask | null>(null)
  const [taskList, setTaskList] = useState<ContentUnderstandingTask[]>([])
  const [activeTab, setActiveTab] = useState('understanding')
  const [previewModal, setPreviewModal] = useState({ visible: false, data: null as any })
  const [uploadedFile, setUploadedFile] = useState<File | null>(null)

  // 分析类型选项
  const analysisTypes = [
    { value: 'image_recognition', label: '图像识别', description: '识别图像中的物体、场景、文字等' },
    { value: 'video_analysis', label: '视频分析', description: '分析视频内容、动作、场景等' },
    { value: 'audio_transcription', label: '音频转录', description: '将音频内容转换为文本' },
    { value: 'text_sentiment', label: '文本情感', description: '分析文本的情感倾向' },
    { value: 'multimodal_fusion', label: '多模态融合', description: '综合分析多种模态内容' }
  ]

  // 预设分析模板
  const analysisTemplates = [
    {
      id: 'comprehensive_image',
      name: '综合图像分析',
      type: 'image_recognition',
      config: {
        detectObjects: true,
        recognizeText: true,
        classifyScene: true,
        analyzeColor: true,
        extractFeatures: true
      }
    },
    {
      id: 'video_summary',
      name: '视频内容摘要',
      type: 'video_analysis',
      config: {
        extractKeyframes: true,
        detectScenes: true,
        recognizeObjects: true,
        analyzeMotion: true,
        generateSummary: true
      }
    },
    {
      id: 'audio_content',
      name: '音频内容提取',
      type: 'audio_transcription',
      config: {
        transcribeSpeech: true,
        identifySpeakers: true,
        detectLanguage: true,
        extractKeywords: true,
        analyzeSentiment: true
      }
    }
  ]

  useEffect(() => {
    loadTaskHistory()
  }, [])

  // 加载任务历史
  const loadTaskHistory = async () => {
    try {
      // 使用本地存储代替不存在的API
      const savedTasks = localStorage.getItem('contentUnderstandingTasks')
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

  // 处理文件上传
  const handleFileUpload = (file: File) => {
    setUploadedFile(file)
    return false // 阻止自动上传
  }

  // 处理表单提交
  const handleSubmit = async (values: any) => {
    if (!uploadedFile) {
      message.error('请上传要分析的文件')
      return
    }

    setLoading(true)
    try {
      // 创建FormData对象
      const formData = new FormData()
      formData.append('file', uploadedFile)
      formData.append('analysisType', values.analysisType)
      
      // 直接调用多模态服务的API
      const response = await fetch('http://127.0.0.1:8085/api/v1/multimodal/content/analyze', {
        method: 'POST',
        body: formData
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      
      // 创建模拟分析结果
      const mockResult = {
        type: values.analysisType,
        summary: '这是一个模拟的分析结果，展示了内容理解功能。',
        confidence: 0.85,
        objects: [
          { name: 'object1', confidence: 0.9 },
          { name: 'object2', confidence: 0.8 }
        ],
        scene: 'indoor',
        text: '检测到的文本内容',
        colors: ['red', 'blue', 'green'],
        duration: 30,
        language: 'zh-CN',
        speakers: 2,
        sentiment: 'positive',
        emotions: [
          { name: 'joy', score: 0.7 },
          { name: 'surprise', score: 0.3 }
        ]
      }
      
      // 创建任务对象
      const task: ContentUnderstandingTask = {
        id: result.taskId || `task_${Date.now()}`,
        userId: '',
        status: 'completed',
        type: values.analysisType,
        contentType: (values as any).contentType || 'image',
        analysisType: values.analysisType,
        content: URL.createObjectURL(uploadedFile),
        parameters: {
          content: URL.createObjectURL(uploadedFile),
          contentType: (values as any).contentType || 'image',
          analysisType: values.analysisType,
          parameters: {
            language: (values as any).language || 'auto',
            detailLevel: (values as any).detailLevel || 'medium',
            maxTokens: (values as any).maxTokens || 1000,
            temperature: (values as any).temperature || 0.7
          }
        },
        result: mockResult,
        progress: 100,
        priority: 1,
        createdAt: new Date().toISOString()
      }
      
      // 保存到本地存储
      const savedTasks = localStorage.getItem('contentUnderstandingTasks')
      const tasks = savedTasks ? JSON.parse(savedTasks) : []
      tasks.unshift(task)
      localStorage.setItem('contentUnderstandingTasks', JSON.stringify(tasks))
      
      setCurrentTask(task)
      setTaskList(tasks)
      message.success('内容理解任务创建成功')
      
      // 简化的轮询（立即完成）
      setTimeout(() => {
        setCurrentTask(prev => prev ? {...prev, status: 'completed', progress: 100} : null)
      }, 1000)
      
    } catch (error) {
      console.error('创建分析任务失败:', error)
      message.error('创建分析任务失败')
    } finally {
      setLoading(false)
    }
  }

  // 处理模板选择
  const handleTemplateSelect = (templateId: string) => {
    const template = analysisTemplates.find(t => t.id === templateId)
    if (template) {
      form.setFieldsValue({
        templateId,
        analysisType: template.type,
        ...template.config
      })
    }
  }

  // 预览结果
  const handlePreview = (task: ContentUnderstandingTask) => {
    setPreviewModal({ visible: true, data: task.result })
  }

  // 下载结果
  const handleDownload = async (task: ContentUnderstandingTask) => {
    try {
      if (task.result) {
        // 从任务结果创建JSON文件并下载
        const dataStr = JSON.stringify(task.result, null, 2)
        const dataBlob = new Blob([dataStr], { type: 'application/json' })
        const url = URL.createObjectURL(dataBlob)
        const link = document.createElement('a')
        link.href = url
        link.download = `analysis-result-${task.id}.json`
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
      content: '确定要删除这个分析任务吗？',
      onOk: async () => {
        try {
          // 从本地存储删除任务
          const savedTasks = localStorage.getItem('contentUnderstandingTasks')
          if (savedTasks) {
            const tasks = JSON.parse(savedTasks)
            const updatedTasks = tasks.filter((task: ContentUnderstandingTask) => task.id !== taskId)
            localStorage.setItem('contentUnderstandingTasks', JSON.stringify(updatedTasks))
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

  // 渲染分析结果
  const renderAnalysisResult = (result: any) => {
    if (!result) return null
    
    switch (result.type || 'unknown') {
      case 'image_recognition':
        return (
          <div className="result-content">
            <Descriptions bordered column={2}>
              <Descriptions.Item label="识别到的物体">
                {result.objects?.map((obj: any) => (
                  <Tag key={obj.name} color="blue">{obj.name} ({(obj.confidence * 100).toFixed(1)}%)</Tag>
                ))}
              </Descriptions.Item>
              <Descriptions.Item label="场景分类">
                {result.scene && <Tag color="green">{result.scene}</Tag>}
              </Descriptions.Item>
              <Descriptions.Item label="识别的文字">
                {result.text && <span>{result.text}</span>}
              </Descriptions.Item>
              <Descriptions.Item label="颜色分析">
                {result.colors?.map((color: any) => (
                  <Tag key={color} color={color}>{color}</Tag>
                ))}
              </Descriptions.Item>
            </Descriptions>
            {result.summary && (
              <div className="result-summary">
                <h4>内容摘要</h4>
                <p>{result.summary}</p>
              </div>
            )}
          </div>
        )
      
      case 'video_analysis':
        return (
          <div className="result-content">
            <Descriptions bordered column={2}>
              <Descriptions.Item label="关键帧数量">
                {result.keyframes?.length || 0}
              </Descriptions.Item>
              <Descriptions.Item label="场景数量">
                {result.scenes?.length || 0}
              </Descriptions.Item>
              <Descriptions.Item label="检测到的物体">
                {result.objects?.map((obj: any) => (
                  <Tag key={obj.name} color="blue">{obj.name}</Tag>
                ))}
              </Descriptions.Item>
              <Descriptions.Item label="视频时长">
                {result.duration && `${result.duration}秒`}
              </Descriptions.Item>
            </Descriptions>
            {result.summary && (
              <div className="result-summary">
                <h4>视频摘要</h4>
                <p>{result.summary}</p>
              </div>
            )}
            {result.keyframes && result.keyframes.length > 0 && (
              <div className="result-keyframes">
                <h4>关键帧</h4>
                <Row gutter={8}>
                  {result.keyframes.map((frame: string, index: number) => (
                    <Col span={6} key={index}>
                      <img src={frame} alt={`关键帧 ${index + 1}`} style={{ width: '100%', borderRadius: '4px' }} />
                    </Col>
                  ))}
                </Row>
              </div>
            )}
          </div>
        )
      
      case 'audio_transcription':
        return (
          <div className="result-content">
            <Descriptions bordered column={1}>
              <Descriptions.Item label="音频时长">
                {result.duration && `${result.duration}秒`}
              </Descriptions.Item>
              <Descriptions.Item label="识别语言">
                {result.language && <Tag color="blue">{result.language}</Tag>}
              </Descriptions.Item>
              <Descriptions.Item label="说话人数量">
                {result.speakers && `${result.speakers}人`}
              </Descriptions.Item>
            </Descriptions>
            {result.transcription && (
              <div className="result-transcription">
                <h4>转录文本</h4>
                <div className="transcription-content">
                  {result.transcription}
                </div>
              </div>
            )}
            {result.keywords && result.keywords.length > 0 && (
              <div className="result-keywords">
                <h4>关键词</h4>
                {result.keywords?.map((keyword: any) => (
                  <Tag key={keyword} color="green">{keyword}</Tag>
                ))}
              </div>
            )}
          </div>
        )
      
      case 'text_sentiment':
        return (
          <div className="result-content">
            <Descriptions bordered column={2}>
              <Descriptions.Item label="情感倾向">
                <Badge
                  status={result.sentiment === 'positive' ? 'success' : result.sentiment === 'negative' ? 'error' : 'default'}
                  text={result.sentiment}
                />
              </Descriptions.Item>
              <Descriptions.Item label="置信度">
                {result.confidence && `${(result.confidence * 100).toFixed(1)}%`}
              </Descriptions.Item>
              <Descriptions.Item label="情感强度">
                {result.intensity && `${(result.intensity * 100).toFixed(1)}%`}
              </Descriptions.Item>
              <Descriptions.Item label="文本长度">
                {result.length && `${result.length}字符`}
              </Descriptions.Item>
            </Descriptions>
            {result.emotions && result.emotions.length > 0 && (
              <div className="result-emotions">
                <h4>情绪分析</h4>
                {result.emotions.map((emotion: any) => (
                  <Tag key={emotion.name} color={emotion.name === 'joy' ? 'orange' : emotion.name === 'anger' ? 'red' : emotion.name === 'sadness' ? 'blue' : 'default'}>
                    {emotion.name}: {(emotion.score * 100).toFixed(1)}%
                  </Tag>
                ))}
              </div>
            )}
          </div>
        )
      
      default:
        return (
          <div className="result-content">
            <pre>{JSON.stringify(result, null, 2)}</pre>
          </div>
        )
    }
  }

  return (
    <div className="content-understanding-page">
      <div className="page-header">
        <div className="header-content">
          <h1>内容理解与分析</h1>
          <p>智能分析多媒体内容，提取关键信息和洞察</p>
        </div>
      </div>

      <div className="page-content">
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="内容分析" key="understanding">
            <Row gutter={24}>
              <Col span={16}>
                <Card title="分析设置" className="analysis-card">
                  <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    initialValues={{
                      analysisType: 'image_recognition',
                      language: 'auto',
                      detailLevel: 'standard',
                      extractText: true,
                      detectObjects: true,
                      recognizeFaces: false,
                      analyzeSentiment: true,
                      extractKeywords: true,
                      generateSummary: true
                    }}
                  >
                    <Form.Item
                      label="上传文件"
                      required
                    >
                      <Dragger
                        accept="image/*,video/*,audio/*,.txt,.doc,.docx,.pdf"
                        beforeUpload={handleFileUpload}
                        maxCount={1}
                        fileList={uploadedFile ? [uploadedFile as any] : []}
                      >
                        <p className="ant-upload-drag-icon">
                          <UploadOutlined />
                        </p>
                        <p className="ant-upload-text">点击或拖拽文件到此处上传</p>
                        <p className="ant-upload-hint">
                          支持图像、视频、音频和文档文件
                        </p>
                      </Dragger>
                    </Form.Item>

                    <Form.Item
                      label="分析类型"
                      name="analysisType"
                      rules={[{ required: true }]}
                    >
                      <Select placeholder="选择分析类型">
                        {analysisTypes.map(type => (
                          <Option key={type.value} value={type.value}>
                            <div style={{ display: 'flex', flexDirection: 'column' }}>
                              <span>{type.label}</span>
                              <small style={{ color: '#999' }}>{type.description}</small>
                            </div>
                          </Option>
                        ))}
                      </Select>
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
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item
                          label="详细程度"
                          name="detailLevel"
                        >
                          <Select placeholder="选择详细程度">
                            <Option value="basic">基础</Option>
                            <Option value="standard">标准</Option>
                            <Option value="detailed">详细</Option>
                            <Option value="comprehensive">全面</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>

                    <Form.Item
                      label="分析选项"
                    >
                      <Row gutter={16}>
                        <Col span={8}>
                          <Form.Item name="extractText" valuePropName="checked">
                            <Switch checkedChildren="提取文本" unCheckedChildren="不提取" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item name="detectObjects" valuePropName="checked">
                            <Switch checkedChildren="检测物体" unCheckedChildren="不检测" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item name="recognizeFaces" valuePropName="checked">
                            <Switch checkedChildren="人脸识别" unCheckedChildren="不识别" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item name="analyzeSentiment" valuePropName="checked">
                            <Switch checkedChildren="情感分析" unCheckedChildren="不分析" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item name="extractKeywords" valuePropName="checked">
                            <Switch checkedChildren="提取关键词" unCheckedChildren="不提取" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item name="generateSummary" valuePropName="checked">
                            <Switch checkedChildren="生成摘要" unCheckedChildren="不生成" />
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
                          icon={<SearchOutlined />}
                        >
                          开始分析
                        </Button>
                        <Button>重置</Button>
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
                        <h4>{currentTask.type}</h4>
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
                        <p><strong>分析类型:</strong> {currentTask.type}</p>
                        <p><strong>文件大小:</strong> {(currentTask as any).metadata?.size && `${((currentTask as any).metadata.size / 1024 / 1024).toFixed(2)}MB`}</p>
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

                <Card title="分析模板" className="template-card">
                  <List
                    dataSource={analysisTemplates}
                    renderItem={template => (
                      <List.Item
                        className="template-item"
                        onClick={() => handleTemplateSelect(template.id)}
                      >
                        <List.Item.Meta
                          title={template.name}
                          description={`类型: ${template.type}`}
                        />
                      </List.Item>
                    )}
                  />
                </Card>
              </Col>
            </Row>
          </TabPane>

          <TabPane tab="历史记录" key="history">
            <Card title="分析历史" className="history-card">
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
                          <span>{task.type}</span>
                          <Tag color={task.status === 'completed' ? 'success' : task.status === 'failed' ? 'error' : 'processing'}>
                            {task.status}
                          </Tag>
                        </div>
                      }
                      description={
                        <div className="task-desc">
                          <p>
                            <BarChartOutlined /> 分析类型: {task.type}
                          </p>
                          <p>
                            <ClockCircleOutlined /> 创建时间: {new Date(task.createdAt).toLocaleString()}
                          </p>
                          {(task as any).metadata?.filename && (
                            <p>
                              <TagOutlined /> 文件名: {(task as any).metadata.filename}
                            </p>
                          )}
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            </Card>
          </TabPane>
        </Tabs>
      </div>

      {/* 结果预览模态框 */}
      <Modal
        title="分析结果"
        visible={previewModal.visible}
        onCancel={() => setPreviewModal({ visible: false, data: null })}
        footer={null}
        width={1000}
      >
        {previewModal.data && (
          <div className="preview-content">
            {renderAnalysisResult(previewModal.data)}
          </div>
        )}
      </Modal>
    </div>
  )
}

export default ContentUnderstandingPage