import React from 'react'
import { Card, Row, Col, Statistic, Button, Space, Typography, Divider } from 'antd'
import {
  ThunderboltOutlined,
  PictureOutlined,
  VideoCameraOutlined,
  AudioOutlined,
  SoundOutlined,
  ArrowRightOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import './index.scss'

const { Title, Paragraph } = Typography

const HomePage: React.FC = () => {
  const navigate = useNavigate()

  const features = [
    {
      title: '智能对话',
      description: '基于大语言模型的智能对话系统，支持多轮对话和上下文理解',
      icon: <ThunderboltOutlined className="feature-icon" />,
      color: '#1890ff',
      path: '/chat',
    },
    {
      title: '图像生成',
      description: 'AI驱动的图像生成服务，支持文本到图像的转换',
      icon: <PictureOutlined className="feature-icon" />,
      color: '#52c41a',
      path: '/multimodal/image-generation',
    },
    {
      title: '图像增强',
      description: '智能图像增强和修复，提升图像质量和清晰度',
      icon: <PictureOutlined className="feature-icon" />,
      color: '#faad14',
      path: '/multimodal/image-enhancement',
    },
    {
      title: '视频分析',
      description: '多模态视频内容分析和理解，提取关键信息',
      icon: <VideoCameraOutlined className="feature-icon" />,
      color: '#722ed1',
      path: '/multimodal/video-analysis',
    },
    {
      title: '音频分析',
      description: '音频内容识别和分析，支持语音识别和情感分析',
      icon: <AudioOutlined className="feature-icon" />,
      color: '#eb2f96',
      path: '/multimodal/audio-analysis',
    },
    {
      title: '文本转语音',
      description: '高质量文本转语音服务，支持多种语音和语言',
      icon: <SoundOutlined className="feature-icon" />,
      color: '#13c2c2',
      path: '/multimodal/text-to-speech',
    },
  ]

  const stats = [
    {
      title: '总对话数',
      value: 2847,
      precision: 0,
    },
    {
      title: '图像生成数',
      value: 1523,
      precision: 0,
    },
    {
      title: '音频处理数',
      value: 892,
      precision: 0,
    },
    {
      title: '用户满意度',
      value: 9.2,
      precision: 1,
      suffix: '/ 10',
    },
  ]

  return (
    <div className="home-page">
      {/* 欢迎区域 */}
      <div className="welcome-section">
        <div className="welcome-content">
          <Title level={1} className="welcome-title">
            欢迎使用 xdream-agent
          </Title>
          <Paragraph className="welcome-description" type="secondary">
            基于多模态AI技术的智能助手平台，为您提供全方位的AI服务能力
          </Paragraph>
          <Space size="large">
            <Button
              type="primary"
              size="large"
              icon={<ThunderboltOutlined />}
              onClick={() => navigate('/chat')}
            >
              开始对话
            </Button>
            <Button
              size="large"
              icon={<ArrowRightOutlined />}
              onClick={() => navigate('/multimodal')}
            >
              探索更多
            </Button>
          </Space>
        </div>
      </div>

      {/* 功能特性 */}
      <div className="features-section">
        <Title level={2} className="section-title">
          核心功能
        </Title>
        <Row gutter={[24, 24]}>
          {features.map((feature, index) => (
            <Col xs={24} sm={12} lg={8} key={index}>
              <Card
                className="feature-card"
                hoverable
                onClick={() => navigate(feature.path)}
                style={{ borderTop: `4px solid ${feature.color}` }}
              >
                <div className="feature-content">
                  <div className="feature-icon-wrapper" style={{ color: feature.color }}>
                    {feature.icon}
                  </div>
                  <Title level={3} className="feature-title">
                    {feature.title}
                  </Title>
                  <Paragraph className="feature-description" type="secondary">
                    {feature.description}
                  </Paragraph>
                  <Button
                    type="link"
                    icon={<ArrowRightOutlined />}
                    className="feature-button"
                    style={{ color: feature.color }}
                  >
                    立即体验
                  </Button>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      </div>

      {/* 统计数据 */}
      <div className="stats-section">
        <Title level={2} className="section-title">
          平台数据
        </Title>
        <Row gutter={[24, 24]}>
          {stats.map((stat, index) => (
            <Col xs={24} sm={12} lg={6} key={index}>
              <Card className="stat-card">
                <Statistic
                  title={stat.title}
                  value={stat.value}
                  precision={stat.precision}
                  suffix={stat.suffix}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
          ))}
        </Row>
      </div>

      {/* 快速开始 */}
      <div className="quick-start-section">
        <Card className="quick-start-card">
          <Title level={3}>快速开始</Title>
          <Paragraph>
            选择您想要体验的功能，我们将为您提供最佳的AI服务体验
          </Paragraph>
          <Divider />
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={8}>
              <Button
                type="primary"
                block
                size="large"
                icon={<ThunderboltOutlined />}
                onClick={() => navigate('/chat')}
              >
                AI对话
              </Button>
            </Col>
            <Col xs={24} sm={12} md={8}>
              <Button
                block
                size="large"
                icon={<PictureOutlined />}
                onClick={() => navigate('/multimodal/image-generation')}
              >
                图像生成
              </Button>
            </Col>
            <Col xs={24} sm={12} md={8}>
              <Button
                block
                size="large"
                icon={<SoundOutlined />}
                onClick={() => navigate('/multimodal/text-to-speech')}
              >
                文本转语音
              </Button>
            </Col>
          </Row>
        </Card>
      </div>
    </div>
  )
}

export default HomePage