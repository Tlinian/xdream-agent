import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { 
  Card, 
  Button, 
  Table, 
  Modal, 
  Form, 
  Input, 
  message, 
  Space, 
  Popconfirm, 
  Typography, 
  Tag, 
  Tooltip, 
  Row, 
  Col, 
  Statistic,
  Empty
} from 'antd'
import { 
  PlusOutlined, 
  DeleteOutlined, 
  BookOutlined, 
  DatabaseOutlined, 
  EyeOutlined,
  FileTextOutlined
} from '@ant-design/icons'
import { knowledgeService } from '../../services';
import type { KnowledgeBase } from '../../interfaces/knowledge'
import './index.scss'

const { Title, Text, Paragraph } = Typography
const { TextArea } = Input

const KnowledgePage: React.FC = () => {
  const navigate = useNavigate()
  const [knowledgeBases, setKnowledgeBases] = useState<KnowledgeBase[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()

  // 获取知识库列表
  const fetchKnowledgeBases = async () => {
    setLoading(true)
    try {
      const userId = 'test-user-001' // 临时使用测试用户ID
      const response = await knowledgeService.getKnowledgeBases(userId)
      setKnowledgeBases(response)
    } catch (error) {
      message.error('获取知识库列表失败')
    } finally {
      setLoading(false)
    }
  }

  // 创建知识库
  const handleCreateKnowledgeBase = async (values: { name: string; description: string }) => {
    try {
      const userId = 'test-user-001' // 临时使用测试用户ID
      await knowledgeService.createKnowledgeBase(userId, values)
      message.success('知识库创建成功')
      setModalVisible(false)
      form.resetFields()
      fetchKnowledgeBases()
    } catch (error) {
      message.error('创建知识库失败')
    }
  }

  // 删除知识库
  const handleDeleteKnowledgeBase = async (id: string) => {
    try {
      const userId = 'test-user-001' // 临时使用测试用户ID
      await knowledgeService.deleteKnowledgeBase(userId, id)
      message.success('知识库删除成功')
      fetchKnowledgeBases()
    } catch (error) {
      message.error('删除知识库失败')
    }
  }

  // 查看知识库详情
  const handleViewKnowledgeBase = (id: string) => {
    navigate(`/knowledge/${id}`)
  }

  // 管理文档
  const handleManageDocuments = (id: string) => {
    navigate(`/knowledge/${id}/documents/upload`)
  }

  useEffect(() => {
    fetchKnowledgeBases()
  }, [])

  // 表格列定义
  const columns = [
    {
      title: '知识库名称',
      dataIndex: 'name',
      key: 'name',
      render: (text: string) => (
        <div>
          <BookOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          <Text strong>{text}</Text>
        </div>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      render: (text: string) => (
        <Text ellipsis={{ tooltip: text }} style={{ maxWidth: 200 }}>
          {text || '-'}
        </Text>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const color = status === 'active' ? 'green' : status === 'processing' ? 'orange' : 'default'
        const text = status === 'active' ? '活跃' : status === 'processing' ? '处理中' : '未知'
        return <Tag color={color}>{text}</Tag>
      },
    },
    {
      title: '文档数量',
      dataIndex: 'documentCount',
      key: 'documentCount',
      render: (count: number) => (
        <div>
          <FileTextOutlined style={{ marginRight: 8 }} />
          <Text>{count}</Text>
        </div>
      ),
    },
    {
      title: '向量数量',
      dataIndex: 'segmentCount',
      key: 'segmentCount',
      render: (count: number) => (
        <div>
          <DatabaseOutlined style={{ marginRight: 8 }} />
          <Text>{count}</Text>
        </div>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => (
        <Text>{new Date(date).toLocaleDateString()}</Text>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: KnowledgeBase) => (
        <Space size="middle">
          <Tooltip title="查看详情">
            <Button 
              type="link" 
              icon={<EyeOutlined />} 
              onClick={() => handleViewKnowledgeBase(record.id)}
            />
          </Tooltip>
          <Tooltip title="管理文档">
            <Button 
              type="link" 
              icon={<FileTextOutlined />} 
              onClick={() => handleManageDocuments(record.id)}
            />
          </Tooltip>
          <Popconfirm
            title="确定要删除这个知识库吗？"
            onConfirm={() => handleDeleteKnowledgeBase(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Tooltip title="删除">
              <Button type="link" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div className="knowledge-page">
      <div className="page-header">
        <Title level={2}>知识库管理</Title>
        <Paragraph>管理和组织您的知识库，上传文档并进行智能检索</Paragraph>
        <Button 
          type="primary" 
          icon={<PlusOutlined />} 
          onClick={() => setModalVisible(true)}
        >
          创建知识库
        </Button>
      </div>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="知识库总数"
              value={knowledgeBases.length}
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="文档总数"
              value={knowledgeBases.reduce((sum, kb) => sum + kb.documentCount, 0)}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="向量总数"
              value={knowledgeBases.reduce((sum, kb) => sum + kb.segmentCount, 0)}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="活跃知识库"
              value={knowledgeBases.filter(kb => kb.status === 'active').length}
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <Table
          columns={columns}
          dataSource={knowledgeBases}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 个知识库`,
          }}
          locale={{
            emptyText: <Empty 
              description="暂无知识库" 
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          }}
        />
      </Card>

      <Modal
        title="创建知识库"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false)
          form.resetFields()
        }}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateKnowledgeBase}
        >
          <Form.Item
            name="name"
            label="知识库名称"
            rules={[{ required: true, message: '请输入知识库名称' }]}
          >
            <Input placeholder="请输入知识库名称" />
          </Form.Item>
          <Form.Item
            name="description"
            label="知识库描述"
          >
            <TextArea rows={4} placeholder="请输入知识库描述" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                创建
              </Button>
              <Button 
                onClick={() => {
                  setModalVisible(false)
                  form.resetFields()
                }}
              >
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default KnowledgePage