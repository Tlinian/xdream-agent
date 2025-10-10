import React from 'react'
import { Card, Row, Col, Typography, Button, Divider, Tag } from 'antd'
import {
  ThunderboltOutlined,
  PictureOutlined,
  VideoCameraOutlined,
  AudioOutlined,
  SoundOutlined,
  ArrowRightOutlined,
  ExperimentOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import './index.scss'

const { Title, Paragraph, Text } = Typography

const MultimodalPage: React.FC = () => {
  const navigate = useNavigate()

  const services = [
    {
      key: 'fusion',
      title: '多模态内容融合',
      description: '将文本、图像、音频等多种模态内容进行智能融合，生成更加丰富和准确的内容理解结果',
      icon: <ThunderboltOutlined />,
      color: '#1890ff',
      path: '/multimodal',
      tags: ['文本', '图像', '音频', '融合'],
      features: [
        '跨模态内容理解',
        '智能特征提取',
        '语义对齐与融合',
        '多模态检索',
      ],
    },
    {
      key: 'image-generation',
      title: 'AI图像生成',
      description: '基于先进的扩散模型，将文本描述转换为高质量图像，支持多种风格和参数调节',
      icon: <PictureOutlined />,
      color: '#52c41a',
      path: '/multimodal/image-generation',
      tags: ['文本转图像', '扩散模型', '风格控制', '参数调节'],
      features: [
        '文本到图像生成',
        '多种艺术风格',
        '高分辨率输出',
        '负面提示词支持',
      ],
    },
    {
      key: 'image-enhancement',
      title: '图像增强处理',
      description: '智能图像增强和修复，包括超分辨率、去噪、色彩增强等多种图像处理技术',
      icon: <PictureOutlined />,
      color: '#faad14',
      path: '/multimodal/image-enhancement',
      tags: ['超分辨率', '去噪', '色彩增强', '图像修复'],
      features: [
        '图像超分辨率',
        '智能去噪处理',
        '色彩自动优化',
        '老照片修复',
      ],
    },
    {
      key: 'video-generation',
      title: 'AI视频生成',
      description: '基于先进的AI模型，将文本描述转换为高质量视频内容，支持多种视频风格和效果',
      icon: <VideoCameraOutlined />,
      color: '#f5222d',
      path: '/multimodal/video-generation',
      tags: ['文本转视频', 'AI视频', '风格控制', '动画生成'],
      features: [
        '文本到视频生成',
        '多种视频风格',
        '智能动画制作',
        '高质量渲染',
      ],
    },
    {
      key: 'video-analysis',
      title: '视频内容分析',
      description: '多模态视频内容理解和分析，提取关键帧、识别场景、分析情感和行为',
      icon: <VideoCameraOutlined />,
      color: '#722ed1',
      path: '/multimodal/video-analysis',
      tags: ['视频理解', '场景识别', '行为分析', '情感识别'],
      features: [
        '视频内容摘要',
        '关键帧提取',
        '场景识别分类',
        '行为模式分析',
      ],
    },
    {
      key: 'audio-analysis',
      title: '音频智能分析',
      description: '音频内容识别和分析，包括语音识别、情感分析、音乐分类和声纹识别',
      icon: <AudioOutlined />,
      color: '#eb2f96',
      path: '/multimodal/audio-analysis',
      tags: ['语音识别', '情感分析', '音乐分类', '声纹识别'],
      features: [
        '语音转文字',
        '说话人识别',
        '情感状态分析',
        '音频质量评估',
      ],
    },
    {
      key: 'text-to-speech',
      title: '文本转语音',
      description: '高质量文本转语音服务，支持多种语音、语言和情感表达，声音自然流畅',
      icon: <SoundOutlined />,
      color: '#13c2c2',
      path: '/multimodal/text-to-speech',
      tags: ['TTS', '多语言', '情感语音', '语音克隆'],
      features: [
        '多语言支持',
        '自然语音合成',
        '情感语音表达',
        '语音克隆定制',
      ],
    },
    {
      key: 'audio-transcription',
      title: '音频转录',
      description: '智能音频转录服务，支持多种音频格式的语音识别和文字转换，准确率高',
      icon: <AudioOutlined />,
      color: '#fa8c16',
      path: '/multimodal/audio-transcription',
      tags: ['语音识别', '音频转文字', '多语言', '实时转录'],
      features: [
        '多语言语音识别',
        '实时音频转录',
        '高准确率转换',
        '多种音频格式支持',
      ],
    },
  ]

  return (
    <div className="multimodal-page">
      {/* 页面标题 */}
      <div className="page-header">
        <div className="header-content">
          <Title level={1} className="page-title">
            <ExperimentOutlined className="title-icon" />
            多模态AI服务
          </Title>
          <Paragraph className="page-description" type="secondary">
            集成多种AI能力，提供全方位的多模态智能服务体验
          </Paragraph>
        </div>
      </div>

      {/* 服务概览 */}
      <div className="services-overview">
        <Title level={2} className="section-title">
          服务概览
        </Title>
        <Row gutter={[24, 24]}>
          {services.map((service) => (
            <Col xs={24} lg={12} key={service.key}>
              <Card
                className="service-card"
                hoverable
                onClick={() => navigate(service.path)}
                style={{ borderLeft: `4px solid ${service.color}` }}
              >
                <div className="service-content">
                  <div className="service-header">
                    <div className="service-icon" style={{ color: service.color }}>
                      {service.icon}
                    </div>
                    <div className="service-title-wrapper">
                      <Title level={3} className="service-title">
                        {service.title}
                      </Title>
                      <div className="service-tags">
                        {service.tags.map((tag) => (
                          <Tag key={tag} color={service.color} style={{ fontSize: '12px' }}>
                            {tag}
                          </Tag>
                        ))}
                      </div>
                    </div>
                  </div>
                  
                  <Paragraph className="service-description" type="secondary">
                    {service.description}
                  </Paragraph>

                  <div className="service-features">
                    <Title level={4} style={{ fontSize: '14px', marginBottom: '12px' }}>
                      核心功能
                    </Title>
                    <ul className="features-list">
                      {service.features.map((feature, index) => (
                        <li key={index} className="feature-item">
                          <Text type="secondary">{feature}</Text>
                        </li>
                      ))}
                    </ul>
                  </div>

                  <div className="service-actions">
                    <Button
                      type="primary"
                      icon={<ArrowRightOutlined />}
                      style={{ backgroundColor: service.color, borderColor: service.color }}
                    >
                      立即体验
                    </Button>
                  </div>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      </div>

      {/* 技术特点 */}
      <div className="tech-features">
        <Card className="tech-features-card">
          <Title level={2} className="section-title">
            技术特点
          </Title>
          <Row gutter={[32, 24]}>
            <Col xs={24} md={8}>
              <div className="tech-feature">
                <div className="tech-feature-icon">🚀</div>
                <Title level={3} style={{ fontSize: '18px' }}>高性能</Title>
                <Paragraph type="secondary">
                  采用最新的AI模型和优化算法，提供快速、准确的处理能力
                </Paragraph>
              </div>
            </Col>
            <Col xs={24} md={8}>
              <div className="tech-feature">
                <div className="tech-feature-icon">🎯</div>
                <Title level={3} style={{ fontSize: '18px' }}>高精度</Title>
                <Paragraph type="secondary">
                  经过大量数据训练和优化，确保处理结果的准确性和可靠性
                </Paragraph>
              </div>
            </Col>
            <Col xs={24} md={8}>
              <div className="tech-feature">
                <div className="tech-feature-icon">🔧</div>
                <Title level={3} style={{ fontSize: '18px' }}>易集成</Title>
                <Paragraph type="secondary">
                  提供标准化的API接口，支持快速集成到现有系统中
                </Paragraph>
              </div>
            </Col>
          </Row>
        </Card>
      </div>

      {/* 快速导航 */}
      <div className="quick-navigation">
        <Card className="quick-nav-card">
          <Title level={3}>快速导航</Title>
          <Paragraph>选择您想要使用的多模态AI服务</Paragraph>
          <Divider />
          <Row gutter={[16, 16]}>
            {services.slice(1).map((service) => (
              <Col xs={24} sm={12} md={8} key={service.key}>
                <Button
                  block
                  size="large"
                  icon={service.icon}
                  style={{ 
                    color: service.color,
                    borderColor: service.color,
                    backgroundColor: 'transparent'
                  }}
                  onClick={() => navigate(service.path)}
                >
                  {service.title}
                </Button>
              </Col>
            ))}
          </Row>
        </Card>
      </div>
    </div>
  )
}

export default MultimodalPage