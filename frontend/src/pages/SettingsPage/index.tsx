import React from 'react'
import { Card, Form, Switch, Select, Button, Space, message } from 'antd'
import { SaveOutlined, ReloadOutlined } from '@ant-design/icons'
import './index.scss'

const SettingsPage: React.FC = () => {
  const [form] = Form.useForm()

  // 初始化表单值
  React.useEffect(() => {
    form.setFieldsValue({
      theme: 'light',
      language: 'zh-CN'
    })
  }, [form])

  // 保存设置
  const handleSave = async () => {
    try {
      const values = await form.validateFields()
      
      // 保存主题设置
      localStorage.setItem('theme', values.theme)
      
      // 保存语言设置
      localStorage.setItem('language', values.language)
      
      message.success('设置已保存')
    } catch (error) {
      message.error('保存设置失败')
      console.error('保存设置失败:', error)
    }
  }

  // 重置设置
  const handleReset = () => {
    form.setFieldsValue({
      theme: 'light',
      language: 'zh-CN'
    })
  }

  return (
    <div className="settings-page">
      <div className="settings-header">
        <h1 className="settings-title">系统设置</h1>
        <p className="settings-subtitle">自定义您的使用体验</p>
      </div>

      <div className="settings-content">
        <Card title="外观设置" className="settings-card">
          <Form
            form={form}
            layout="vertical"
            initialValues={{
              theme: 'light',
              language: 'zh-CN'
            }}
          >
            <Form.Item
              label="主题模式"
              name="theme"
              help="选择您偏好的界面主题"
            >
              <Select>
                <Select.Option value="light">浅色主题</Select.Option>
                <Select.Option value="dark">深色主题</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="语言设置"
              name="language"
              help="选择界面显示语言"
            >
              <Select>
                <Select.Option value="zh-CN">简体中文</Select.Option>
                <Select.Option value="en-US">English</Select.Option>
              </Select>
            </Form.Item>
          </Form>
        </Card>

        <Card title="功能设置" className="settings-card">
          <Form layout="vertical">
            <Form.Item
              label="自动保存聊天记录"
              help="关闭后聊天记录将不会被保存"
            >
              <Switch defaultChecked />
            </Form.Item>

            <Form.Item
              label="启用消息通知"
              help="接收系统和消息通知"
            >
              <Switch defaultChecked />
            </Form.Item>

            <Form.Item
              label="显示实时预览"
              help="在生成内容时显示实时预览"
            >
              <Switch defaultChecked />
            </Form.Item>
          </Form>
        </Card>

        <Card title="高级设置" className="settings-card">
          <Form layout="vertical">
            <Form.Item
              label="API 超时时间"
              help="设置API请求的超时时间（秒）"
            >
              <Select defaultValue="30">
                <Select.Option value="10">10秒</Select.Option>
                <Select.Option value="30">30秒</Select.Option>
                <Select.Option value="60">60秒</Select.Option>
                <Select.Option value="120">120秒</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="最大重试次数"
              help="API请求失败时的最大重试次数"
            >
              <Select defaultValue="3">
                <Select.Option value="1">1次</Select.Option>
                <Select.Option value="3">3次</Select.Option>
                <Select.Option value="5">5次</Select.Option>
              </Select>
            </Form.Item>
          </Form>
        </Card>

        <div className="settings-actions">
          <Space>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={handleSave}
            >
              保存设置
            </Button>
            <Button
              icon={<ReloadOutlined />}
              onClick={handleReset}
            >
              重置默认
            </Button>
          </Space>
        </div>
      </div>
    </div>
  )
}

export default SettingsPage