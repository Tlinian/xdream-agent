import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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
  Empty,
  Progress,
  Select,
  Checkbox
} from 'antd';
import { 
  DeleteOutlined, 
  BookOutlined, 
  FileTextOutlined, 
  EyeOutlined,
  ArrowLeftOutlined,
  DownloadOutlined
} from '@ant-design/icons';
import { knowledgeService } from '../../services';
import type { KnowledgeBase, Document } from '../../interfaces/knowledge';
import './index.scss';

const { Title, Text, Paragraph } = Typography;
const { TextArea } = Input;

const KnowledgeDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [knowledgeBase, setKnowledgeBase] = useState<KnowledgeBase | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [loading, setLoading] = useState(false);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [form] = Form.useForm();
  
  const userId = 'test-user-001';

  useEffect(() => {
    if (id) {
      fetchKnowledgeBaseDetail();
      fetchDocuments();
    }
  }, [id]);

  const fetchKnowledgeBaseDetail = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const response = await knowledgeService.getKnowledgeBaseById(userId, id);
      setKnowledgeBase(response);
    } catch (error) {
      message.error('获取知识库详情失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDocuments = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const response = await knowledgeService.getDocuments(userId, id);
      setDocuments(response);
    } catch (error) {
      message.error('获取文档列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleUploadDocument = async (values: { title: string; description: string; content: string; documentType: string; autoProcess?: boolean }) => {
    if (!id) return;
    setUploading(true);
    setUploadProgress(0);
    
    try {
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return prev;
          }
          return prev + 10;
        });
      }, 200);
      
      await knowledgeService.uploadDocument(userId, id, {
        title: values.title,
        description: values.description,
        content: values.content,
        documentType: values.documentType,
        autoProcess: values.autoProcess
      });
      
      clearInterval(progressInterval);
      setUploadProgress(100);
      message.success('文档上传成功');
      setUploadModalVisible(false);
      form.resetFields();
      fetchDocuments();
      fetchKnowledgeBaseDetail();
    } catch (error) {
      message.error('文档上传失败');
    } finally {
      setUploading(false);
      setUploadProgress(0);
    }
  };

  const handleDeleteDocument = async (documentId: string) => {
    if (!id) return;
    try {
      await knowledgeService.deleteDocument(userId, id, documentId);
      message.success('文档删除成功');
      fetchDocuments();
      fetchKnowledgeBaseDetail();
    } catch (error) {
      message.error('删除文档失败');
    }
  };

  const documentColumns = [
    {
      title: '文档标题',
      dataIndex: 'title',
      key: 'title',
      render: (text: string) => (
        <div>
          <FileTextOutlined style={{ marginRight: 8, color: '#1890ff' }} />
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
      title: '处理状态',
      dataIndex: 'processStatus',
      key: 'processStatus',
      render: (status: string) => {
        const map: Record<string, { color: string; text: string }> = {
          pending: { color: 'default', text: '待处理' },
          processing: { color: 'orange', text: '处理中' },
          processed: { color: 'green', text: '已处理' },
        };
        const { color, text } = map[status] || { color: 'default', text: status || '-' };
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '向量数量',
      dataIndex: 'vectorCount',
      key: 'vectorCount',
      render: (count: number) => (
        <Text>{count || 0}</Text>
      ),
    },
    {
      title: '上传时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => (
        <Text>{new Date(date).toLocaleDateString()}</Text>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Document) => (
        <Space size="middle">
          <Tooltip title="查看详情">
            <Button 
              type="link" 
              icon={<EyeOutlined />} 
              onClick={() => {/* 查看详情逻辑 */}}
            />
          </Tooltip>
          <Tooltip title="下载文档">
            <Button 
              type="link" 
              icon={<DownloadOutlined />} 
              onClick={() => {/* 下载文档逻辑 */}}
            />
          </Tooltip>
          <Popconfirm
            title="确定要删除这个文档吗？"
            onConfirm={() => handleDeleteDocument(record.documentId)}
            okText="确定"
            cancelText="取消"
          >
            <Tooltip title="删除文档">
              <Button 
                type="link" 
                danger 
                icon={<DeleteOutlined />} 
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="knowledge-detail-page">
      <div className="page-header">
        <Button 
          type="text" 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/knowledge')}
          style={{ marginBottom: 16 }}
        >
          返回知识库列表
        </Button>
        <Title level={2}>
          {knowledgeBase?.name || '知识库详情'}
        </Title>
        <Paragraph type="secondary">
          {knowledgeBase?.description || '暂无描述'}
        </Paragraph>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="文档数量"
              value={documents.length}
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已处理文档"
              value={documents.filter(doc => doc.processStatus === 'processed').length}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="处理中"
              value={documents.filter(doc => doc.processStatus === 'processing').length}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待处理"
              value={documents.filter(doc => doc.processStatus === 'pending').length}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card
        title="文档列表"
        extra={
          <Button 
            type="primary" 
            onClick={() => setUploadModalVisible(true)}
          >
            上传文档
          </Button>
        }
      >
        {documents.length > 0 ? (
          <Table
            columns={documentColumns}
            dataSource={documents}
            rowKey="documentId"
            loading={loading}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条记录`,
            }}
          />
        ) : (
          <Empty 
            description="暂无文档" 
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button 
              type="primary" 
              onClick={() => setUploadModalVisible(true)}
            >
              上传第一个文档
            </Button>
          </Empty>
        )}
      </Card>

      <Modal
        title="上传文档"
        open={uploadModalVisible}
        onCancel={() => {
          setUploadModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleUploadDocument}
        >
          <Form.Item
            name="title"
            label="文档标题"
            rules={[{ required: true, message: '请输入文档标题' }]}
          >
            <Input placeholder="请输入文档标题" />
          </Form.Item>
          <Form.Item
            name="description"
            label="文档描述"
          >
            <TextArea rows={3} placeholder="请输入文档描述" />
          </Form.Item>
          <Form.Item
            name="documentType"
            label="文档类型"
            rules={[{ required: true, message: '请选择文档类型' }]}
            initialValue="text"
          >
            <Select>
              <Select.Option value="text">文本</Select.Option>
              <Select.Option value="pdf">PDF</Select.Option>
              <Select.Option value="word">Word文档</Select.Option>
              <Select.Option value="markdown">Markdown</Select.Option>
              <Select.Option value="html">HTML</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="content"
            label="文档内容"
            rules={[{ required: true, message: '请输入文档内容' }]}
          >
            <TextArea rows={6} placeholder="请输入文档内容" />
          </Form.Item>
          {uploading && (
            <Form.Item>
              <Progress percent={uploadProgress} status="active" />
            </Form.Item>
          )}
          <Form.Item name="autoProcess" valuePropName="checked">
            <Checkbox>上传后自动处理</Checkbox>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button 
                type="primary" 
                htmlType="submit" 
                loading={uploading}
              >
                {uploading ? '上传中...' : '上传'}
              </Button>
              <Button 
                onClick={() => {
                  setUploadModalVisible(false);
                  form.resetFields();
                }}
                disabled={uploading}
              >
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default KnowledgeDetailPage;