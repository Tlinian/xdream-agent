import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Modal, message, Space, Popconfirm, Form, Input, Select, Checkbox, Progress } from 'antd';
import { DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import { knowledgeService } from '../../services';
import type { Document } from '../../interfaces/knowledge';
import { useParams } from 'react-router-dom';
import './index.scss';

const { TextArea } = Input;
const { Option } = Select;

const DocumentManagePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [documents, setDocuments] = useState<Document[]>([]);
  const [loading, setLoading] = useState(false);
  const [viewModalVisible, setViewModalVisible] = useState(false);
  const [currentDocument, setCurrentDocument] = useState<Document | null>(null);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [form] = Form.useForm();
  
  // 临时使用固定用户ID，实际项目中应该从认证上下文获取
  const userId = 'test-user-001';

  useEffect(() => {
    if (id) {
      fetchDocuments();
    }
  }, [id]);

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

  const handleView = (document: Document) => {
    setCurrentDocument(document);
    setViewModalVisible(true);
  };

  const handleDelete = async (documentId: string) => {
    if (!id) return;
    try {
      await knowledgeService.deleteDocument(userId, id, documentId);
      message.success('删除成功');
      fetchDocuments();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleUploadDocument = async (values: { title: string; description?: string; content: string; documentType: string; category?: string; tags?: string[]; autoProcess?: boolean }) => {
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

      // 格式化请求参数以符合后端要求
      const payload: any = {
        title: values.title?.trim(),
        description: values.description?.trim(),
        content: values.content?.trim(),
        documentType: values.documentType,
        category: values.category?.trim(),
        // 后端要求 tags 为逗号分隔的字符串
        tags: Array.isArray(values.tags) ? values.tags.join(',') : values.tags,
        autoProcess: values.autoProcess
      };

      await knowledgeService.uploadDocument(userId, id, payload);

      clearInterval(progressInterval);
      setUploadProgress(100);
      message.success('文档上传成功');
      setUploadModalVisible(false);
      form.resetFields();
      fetchDocuments();
    } catch (error) {
      message.error('文档上传失败');
    } finally {
      setUploading(false);
      setUploadProgress(0);
    }
  };

  const columns = [
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '类型',
      dataIndex: 'documentType',
      key: 'documentType',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Document) => (
        <Space size="middle">
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            查看
          </Button>
          <Popconfirm
            title="确定要删除这个文档吗？"
            onConfirm={() => handleDelete(record.documentId)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="document-manage-page">
      <Card title="文档管理" extra={<Button type="primary" onClick={() => setUploadModalVisible(true)}>上传文档</Button>}>
        <Table
          columns={columns}
          dataSource={documents}
          rowKey="documentId"
          loading={loading}
        />
      </Card>

      <Modal
        title="文档详情"
        open={viewModalVisible}
        onCancel={() => setViewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setViewModalVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {currentDocument && (
          <div>
            <h3>{currentDocument.title}</h3>
            <p>{currentDocument.description}</p>
            <div dangerouslySetInnerHTML={{ __html: currentDocument.content }} />
          </div>
        )}
      </Modal>

      <Modal
        title="上传文档"
        open={uploadModalVisible}
        onCancel={() => {
          setUploadModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={700}
      >
        <Form form={form} layout="vertical" onFinish={handleUploadDocument}>
          <Form.Item name="title" label="文档标题" rules={[{ required: true, message: '请输入文档标题' }]}> 
            <Input placeholder="请输入文档标题" />
          </Form.Item>

          <Form.Item name="description" label="文档描述">
            <TextArea rows={3} placeholder="请输入文档描述" />
          </Form.Item>

          <Form.Item name="documentType" label="文档类型" rules={[{ required: true, message: '请选择文档类型' }]} initialValue="text">
            <Select>
              <Option value="text">文本</Option>
              <Option value="pdf">PDF</Option>
              <Option value="word">Word文档</Option>
              <Option value="markdown">Markdown</Option>
              <Option value="html">HTML</Option>
            </Select>
          </Form.Item>

          <Form.Item name="content" label="文档内容" rules={[{ required: true, message: '请输入文档内容' }]}> 
            <TextArea rows={6} placeholder="请输入文档内容" />
          </Form.Item>

          <Form.Item name="category" label="分类">
            <Input placeholder="请输入分类（可选）" />
          </Form.Item>

          <Form.Item name="tags" label="标签">
            <Select mode="tags" placeholder="输入后回车添加标签" />
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
              <Button type="primary" htmlType="submit" loading={uploading}>
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

export default DocumentManagePage;