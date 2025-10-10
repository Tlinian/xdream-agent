import React from 'react'
import { Card, Row, Col, Button, Typography } from 'antd'
import { PictureOutlined, VideoCameraOutlined, AudioOutlined, MergeCellsOutlined, EyeOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import './MultimodalPage.scss'

const { Title, Paragraph } = Typography

const MultimodalPage: React.FC = () => {
  const navigate = useNavigate()

  const services = [
    {
      title: '图像生成',
      description: '基于文本描述生成高质量图像',
      icon: <PictureOutlined />,
      action: () => navigate('/multimodal/image-generation'),
      color: '#1890ff'
    },
    {
      title: '视频生成',
      description: '从文本或图像生成动态视频内容',
      icon: <VideoCameraOutlined />,
      action: () => navigate('/multimodal/video-generation'),
      color: '#52c41a'
    },
    {
      title: '音频生成',
      description: '文本转语音和音频内容创作',
      icon: <AudioOutlined />,
      action: () => navigate('/multimodal/audio-generation'),
      color: '#722ed1'
    },
    {
      title: '多模态融合',
      description: '整合文本、图像、音频等多模态数据',
      icon: <MergeCellsOutlined />,
      action: () => navigate('/multimodal/fusion'),
      color: '#fa8c16'
    },
    {
      title: '内容理解',
      description: '深度理解多模态内容的含义',
      icon: <EyeOutlined />,
      action: () => navigate('/multimodal/content-understanding'),
      color: '#eb2f96'
    }
  ]

  return (
    <div className="multimodal-page">
      <div className="multimodal-header">
        <Title level={2}>多模态AI服务</Title>
        <Paragraph type="secondary">
          体验先进的多模态人工智能技术，支持文本、图像、音频等多种媒体形式的智能处理
        </Paragraph>
      </div>

      <div className="multimodal-content">
        <Row gutter={[24, 24]}>
          {services.map((service, index) => (
            <Col xs={24} sm={12} lg={8} key={index}>
              <Card
                className="service-card"
                hoverable
                style={{ borderColor: service.color }}
              >
                <div className="service-icon" style={{ color: service.color }}>
                  {service.icon}
                </div>
                <Title level={4} className="service-title">
                  {service.title}
                </Title>
                <Paragraph className="service-description">
                  {service.description}
                </Paragraph>
                <Button
                  type="primary"
                  style={{ backgroundColor: service.color, borderColor: service.color }}
                  onClick={service.action}
                  block
                >
                  开始使用
                </Button>
              </Card>
            </Col>
          ))}
        </Row>
      </div>

      <div className="multimodal-features">
        <Title level={3} className="features-title">核心优势</Title>
        <Row gutter={[24, 24]}>
          <Col xs={24} sm={12} lg={6}>
            <Card className="feature-card" size="small">
              <div className="feature-content">
                <Title level={5}>🚀 高性能</Title>
                <Paragraph>基于最新的深度学习模型，提供快速准确的多模态处理能力</Paragraph>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card className="feature-card" size="small">
              <div className="feature-content">
                <Title level={5}>🎨 多样化</Title>
                <Paragraph>支持多种媒体格式的输入和输出，满足不同场景需求</Paragraph>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card className="feature-card" size="small">
              <div className="feature-content">
                <Title level={5}>🔧 易集成</Title>
                <Paragraph>提供简洁的API接口，方便集成到现有应用中</Paragraph>
              </div>
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card className="feature-card" size="small">
              <div className="feature-content">
                <Title level={5}>🛡️ 安全可靠</Title>
                <Paragraph>企业级安全保障，确保数据隐私和处理安全</Paragraph>
              </div>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default MultimodalPage