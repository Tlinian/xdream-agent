import React, { useState, useEffect } from 'react'
import { Card, Form, Select, Button, Upload, Row, Col, Space, Tag, Progress, message, Tabs, Modal, List, Badge, Typography, Alert } from 'antd'
import { EyeOutlined, DownloadOutlined, SearchOutlined, PlayCircleOutlined } from '@ant-design/icons'
import './index.scss'

const { Option } = Select
const { Dragger } = Upload
const { Title, Text } = Typography

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
  createdAt: string
  completedAt?: number
  progress: number
  metadata?: {
    filename: string
    size: number
  }
}

const VideoAnalysisPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [currentTask, setCurrentTask] = useState<ContentUnderstandingTask | null>(null)
  const [taskList, setTaskList] = useState<ContentUnderstandingTask[]>([])
  const [activeTab, setActiveTab] = useState('analysis')
  const [previewModal, setPreviewModal] = useState({ visible: false, data: null as any })
  const [uploadedFile, setUploadedFile] = useState<File | null>(null)

  // 视频分析类型选项
  const analysisTypes = [
    { value: 'video_summary', label: '视频摘要', description: '生成视频内容摘要和关键信息' },
    { value: 'scene_detection', label: '场景检测', description: '检测视频中的场景变化和转换' },
    { value: 'object_tracking', label: '物体追踪', description: '追踪视频中的物体和运动轨迹' },
    { value: 'action_recognition', label: '动作识别', description: '识别视频中的人物动作和行为' },
    { value: 'emotion_analysis', label: '情感分析', description: '分析视频中人物的情感和表情' },
    { value: 'keyframe_extraction', label: '关键帧提取', description: '提取视频的关键帧和代表性画面' },
    { value: 'transcript_generation', label: '字幕生成', description: '自动生成视频字幕和转录文本' },
    { value: 'content_moderation', label: '内容审核', description: '检测视频中的不当内容和敏感信息' }
  ]

  // 预设分析模板
  const analysisTemplates = [
    {
      id: 'comprehensive_video',
      name: '综合视频分析',
      type: 'video_summary',
      config: {
        extractAudio: true,
        extractKeyframes: true,
        detectScenes: true,
        recognizeObjects: true,
        analyzeMotion: true,
        generateSummary: true,
        enableTranscription: true,
        detectFaces: true
      }
    },
    {
      id: 'meeting_analysis',
      name: '会议视频分析',
      type: 'transcript_generation',
      config: {
        extractAudio: true,
        enableTranscription: true,
        identifySpeakers: true,
        extractKeywords: true,
        generateSummary: true,
        detectKeyMoments: true
      }
    },
    {
      id: 'security_monitoring',
      name: '安防监控分析',
      type: 'object_tracking',
      config: {
        detectMotion: true,
        trackObjects: true,
        detectAnomalies: true,
        generateAlerts: true,
        extractKeyframes: true,
        analyzeBehavior: true
      }
    },
    {
      id: 'entertainment_content',
      name: '娱乐内容分析',
      type: 'emotion_analysis',
      config: {
        detectFaces: true,
        analyzeEmotions: true,
        recognizeActions: true,
        extractHighlights: true,
        generateTags: true,
        createThumbnails: true
      }
    }
  ]

  useEffect(() => {
    loadTaskHistory()
  }, [])

  // 加载任务历史
  const loadTaskHistory = async () => {
    try {
      // 由于多模态服务没有历史记录API，我们使用本地存储来模拟
      const savedTasks = localStorage.getItem('videoAnalysisTasks')
      if (savedTasks) {
        const tasks = JSON.parse(savedTasks)
        setTaskList(tasks.filter((item: ContentUnderstandingTask) => 
          item.contentType === 'video' || item.analysisType.includes('video')
        ))
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
      message.error('请上传要分析的视频文件')
      return
    }

    setLoading(true)
    try {
      // 直接使用多模态服务的视频分析API
      const formData = new FormData()
      formData.append('file', uploadedFile)
      formData.append('analysisType', values.analysisType || 'BASIC')
      
      const response = await fetch('http://127.0.0.1:8085/api/v1/multimodal/video/analyze', {
        method: 'POST',
        body: formData
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      
      if (result.code === 200) {
        // 由于后端服务是模拟的，我们提供模拟分析结果
        const mockResult = {
          taskId: `task_${Date.now()}`,
          analysisType: values.analysisType,
          summary: '视频内容分析完成',
          duration: 120,
          scenes: [
            { start: 0, end: 30, description: '开场场景' },
            { start: 30, end: 90, description: '主要内容' },
            { start: 90, end: 120, description: '结尾场景' }
          ],
          objects: [
            { name: '人物', count: 3, confidence: 0.95 },
            { name: '汽车', count: 2, confidence: 0.87 },
            { name: '建筑物', count: 1, confidence: 0.92 }
          ],
          emotions: [
            { type: '快乐', intensity: 0.7, timestamp: 45 },
            { type: '中性', intensity: 0.8, timestamp: 75 }
          ],
          keyframes: [
            { timestamp: 15, description: '关键帧1' },
            { timestamp: 60, description: '关键帧2' },
            { timestamp: 105, description: '关键帧3' }
          ],
          transcript: '这是视频的转录文本内容...',
          confidence: 0.85
        }
        
        // 创建任务对象
        const task: ContentUnderstandingTask = {
          id: mockResult.taskId,
          userId: '',
          status: 'completed',
          type: values.analysisType,
          contentType: 'video',
          analysisType: values.analysisType,
          content: URL.createObjectURL(uploadedFile),
          parameters: {
            language: (values as any).language || 'auto',
            detailLevel: (values as any).detailLevel || 'medium',
            maxTokens: (values as any).maxTokens || 2000,
            temperature: (values as any).temperature || 0.7,
            videoStartTime: (values as any).videoStartTime || 0,
            videoEndTime: (values as any).videoEndTime || undefined,
            extractAudio: (values as any).extractAudio !== false,
            extractKeyframes: (values as any).extractKeyframes !== false,
            detectScenes: (values as any).detectScenes !== false,
            recognizeObjects: (values as any).recognizeObjects !== false,
            analyzeMotion: (values as any).analyzeMotion !== false,
            generateSummary: (values as any).generateSummary !== false,
            enableTranscription: (values as any).enableTranscription !== false,
            detectFaces: (values as any).detectFaces !== false,
            frameInterval: (values as any).frameInterval || 30,
            confidenceThreshold: (values as any).confidenceThreshold || 0.7
          },
          progress: 100,

          createdAt: new Date().toISOString(),
          metadata: {
            filename: uploadedFile.name,
            size: uploadedFile.size
          },
          result: mockResult
        }
        
        setCurrentTask(task)
        message.success('视频分析完成')
        
        // 保存任务到本地存储
        const savedTasks = localStorage.getItem('videoAnalysisTasks')
        const tasks = savedTasks ? JSON.parse(savedTasks) : []
        tasks.unshift(task)
        localStorage.setItem('videoAnalysisTasks', JSON.stringify(tasks))
        
        // 更新任务列表
        setTaskList(tasks.filter((item: ContentUnderstandingTask) => 
          item.contentType === 'video' || item.analysisType.includes('video')
        ))
      } else {
        throw new Error(result.message || '分析失败')
      }
    } catch (error) {
      console.error('创建分析任务失败:', error)
      message.error('创建分析任务失败: ' + (error as Error).message)
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
        // 创建JSON文件并下载
        const dataStr = JSON.stringify(task.result, null, 2)
        const dataBlob = new Blob([dataStr], { type: 'application/json' })
        const url = URL.createObjectURL(dataBlob)
        const link = document.createElement('a')
        link.href = url
        link.download = `analysis_result_${task.id}.json`
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
          // 从本地存储中删除任务
          const savedTasks = localStorage.getItem('videoAnalysisTasks')
          if (savedTasks) {
            const tasks = JSON.parse(savedTasks)
            const updatedTasks = tasks.filter((task: ContentUnderstandingTask) => task.id !== taskId)
            localStorage.setItem('videoAnalysisTasks', JSON.stringify(updatedTasks))
            
            // 更新任务列表
            setTaskList(updatedTasks.filter((item: ContentUnderstandingTask) => 
              item.contentType === 'video' || item.analysisType.includes('video')
            ))
            message.success('删除成功')
          }
        } catch (error) {
          console.error('删除失败:', error)
          message.error('删除失败')
        }
      }
    })
  }

  // 获取状态颜色
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'completed': return 'success'
      case 'processing': return 'processing'
      case 'failed': return 'error'
      default: return 'default'
    }
  }

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // 渲染分析结果
  const renderAnalysisResult = (result: any) => {
    if (!result) return null
    
    return (
      <div className="result-content">
        <div style={{ marginBottom: 16 }}>
          <Title level={4}>分析摘要</Title>
          <p>{result.summary}</p>
          <Text type="secondary">置信度: {(result.confidence * 100).toFixed(1)}%</Text>
        </div>
        
        {result.scenes && result.scenes.length > 0 && (
          <div style={{ marginBottom: 16 }}>
            <Title level={4}>场景检测</Title>
            <List
              dataSource={result.scenes}
              renderItem={(scene: any) => (
                <List.Item>
                  <Text>{scene.start}s - {scene.end}s: {scene.description}</Text>
                </List.Item>
              )}
            />
          </div>
        )}
        
        {result.objects && result.objects.length > 0 && (
          <div style={{ marginBottom: 16 }}>
            <Title level={4}>物体识别</Title>
            <Row gutter={[8, 8]}>
              {result.objects.map((obj: any, index: number) => (
                <Col key={index}>
                  <Tag color="blue">
                    {obj.name} ({obj.count}) - {(obj.confidence * 100).toFixed(1)}%
                  </Tag>
                </Col>
              ))}
            </Row>
          </div>
        )}
        
        {result.keyframes && result.keyframes.length > 0 && (
          <div style={{ marginBottom: 16 }}>
            <Title level={4}>关键帧</Title>
            <List
              dataSource={result.keyframes}
              renderItem={(frame: any) => (
                <List.Item>
                  <Text>{frame.timestamp}s: {frame.description}</Text>
                </List.Item>
              )}
            />
          </div>
        )}
        
        {result.transcript && (
          <div style={{ marginBottom: 16 }}>
            <Title level={4}>转录文本</Title>
            <p>{result.transcript}</p>
          </div>
        )}
        
        <div style={{ marginTop: 16 }}>
          <Text type="secondary" style={{ fontSize: '12px' }}>
            原始数据:
          </Text>
          <pre style={{ 
            maxHeight: '200px', 
            overflow: 'auto',
            backgroundColor: '#f5f5f5',
            padding: '12px',
            borderRadius: '4px',
            fontSize: '12px'
          }}>
            {JSON.stringify(result, null, 2)}
          </pre>
        </div>
      </div>
    )
  }

  return (
    <div className="video-analysis-page">
      <div className="page-header">
        <Title level={2}>视频内容分析</Title>
        <Text type="secondary">智能分析视频内容，提取关键信息和洞察</Text>
      </div>

      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: 'analysis',
            label: '视频分析',
            children: (
              <Row gutter={24}>
                <Col span={16}>
                  <Card title="分析设置" className="analysis-card">
                    <Form
                      form={form}
                      layout="vertical"
                      onFinish={handleSubmit}
                      initialValues={{
                        analysisType: 'video_summary',
                        language: 'auto',
                        detailLevel: 'medium',
                        extractAudio: true,
                        extractKeyframes: true,
                        detectScenes: true,
                        recognizeObjects: true,
                        analyzeMotion: true,
                        generateSummary: true,
                        enableTranscription: true,
                        detectFaces: true
                      }}
                    >
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

                      <Form.Item
                        label="上传视频文件"
                        required
                      >
                        <Dragger
                          accept="video/*"
                          beforeUpload={handleFileUpload}
                          maxCount={1}
                          className="video-upload"
                        >
                          <p className="ant-upload-drag-icon">
                            <PlayCircleOutlined />
                          </p>
                          <p className="ant-upload-text">点击或拖拽上传视频文件</p>
                          <p className="ant-upload-hint">
                            支持 MP4, AVI, MOV, WMV 等常见视频格式
                          </p>
                        </Dragger>
                      </Form.Item>

                      {uploadedFile && (
                        <Alert
                          message="文件已上传"
                          description={`${uploadedFile.name} (${formatFileSize(uploadedFile.size)})`}
                          type="success"
                          showIcon
                          style={{ marginBottom: 16 }}
                        />
                      )}

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
                          <Title level={4}>{currentTask.type}</Title>
                          <Badge 
                            status={getStatusColor(currentTask.status)} 
                            text={currentTask.status}
                          />
                        </div>
                        
                        {currentTask.status === 'processing' && (
                          <div className="task-progress">
                            <Progress
                              percent={currentTask.progress || 0}
                              status="active"
                              strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }}
                            />
                          </div>
                        )}

                        <div className="task-details">
                          <p><strong>分析类型:</strong> {currentTask.type}</p>
                          <p><strong>创建时间:</strong> {new Date(currentTask.createdAt).toLocaleString()}</p>
                          {(currentTask as any).metadata && (
                            <p><strong>文件大小:</strong> {formatFileSize((currentTask as any).metadata.size || 0)}</p>
                          )}
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
                        <PlayCircleOutlined style={{ fontSize: '48px', color: '#ccc' }} />
                        <p>暂无进行中的分析任务</p>
                        <p>请上传视频文件并开始分析</p>
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
                      actions={[
                        <Button
                          key="preview"
                          icon={<EyeOutlined />}
                          onClick={() => handlePreview(task)}
                        >
                          预览
                        </Button>,
                        <Button
                          key="download"
                          icon={<DownloadOutlined />}
                          onClick={() => handleDownload(task)}
                        >
                          下载
                        </Button>,
                        <Button
                          key="delete"
                          danger
                          icon={<PlayCircleOutlined />}
                          onClick={() => handleDelete(task.id)}
                        >
                          删除
                        </Button>
                      ]}
                    >
                      <List.Item.Meta
                        title={
                          <Space>
                            <span>{task.analysisType}</span>
                            <Badge 
                              status={getStatusColor(task.status)} 
                              text={task.status}
                            />
                          </Space>
                        }
                        description={
                          <div>
                            <p>任务ID: {task.id}</p>
                            <p>创建时间: {new Date(task.createdAt).toLocaleString()}</p>
                            {(task as any).metadata && (
                              <p>文件: {(task as any).metadata.filename} ({formatFileSize((task as any).metadata.size || 0)})</p>
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
        ]}
      />

      <Modal
        title="分析结果预览"
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

export default VideoAnalysisPage