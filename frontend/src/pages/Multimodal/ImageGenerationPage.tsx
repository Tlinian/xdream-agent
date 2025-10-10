import React, { useState } from 'react'
import { Card, Input, Button, Row, Col, message, Typography, Space, Tag, Slider } from 'antd'
import { PictureOutlined, DownloadOutlined, ClearOutlined } from '@ant-design/icons'
import './ImageGenerationPage.scss'

const { Title, Paragraph } = Typography
const { TextArea } = Input

const ImageGenerationPage: React.FC = () => {
  const [prompt, setPrompt] = useState('')
  const [generatedImages, setGeneratedImages] = useState<string[]>([])
  const [isGenerating, setIsGenerating] = useState(false)
  const [settings, setSettings] = useState({
    width: 512,
    height: 512,
    steps: 20,
    guidance: 7.5
  })

  // 生成图像
  const handleGenerate = async () => {
    if (!prompt.trim()) {
      message.warning('请输入图像描述')
      return
    }

    setIsGenerating(true)
    try {
      // 调用实际的后端API
      // 注意：在实际应用中，用户ID应该从认证系统获取
      const userId = 'test-user'; // 临时用户ID，实际应从认证系统获取
      // 注意：根据vite.config.ts中的代理配置，后端API请求需要添加/api前缀
      const response = await fetch('/api/llm/images/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': userId
        },
        body: JSON.stringify({
          prompt: prompt,
          size: `${settings.width}x${settings.height}`,
          n: 4, // 生成4张图片
          responseFormat: 'url',
          model: 'Qwen/Qwen-Image',
          user: userId
        })
      })

      if (!response.ok) {
        const errorText = await response.text();
        console.error(`API调用失败: 状态码 ${response.status}, 响应体: ${errorText}`);
        message.error(`图像生成失败: 服务器返回错误 ${response.status}`);
        return;
      }

      try {
        const data = await response.json();
        
        // 处理API返回的图像URL
        // 注意：后端返回的code字段是字符串"SUCCESS"而不是数字200
        if ((data.code === 200 || data.code === 'SUCCESS') && data.success !== false && data.data && data.data.images) {
          // 从返回的images列表中提取所有图片URL
        const imageUrls = data.data.images.map((image: any) => image.url);
          setGeneratedImages(imageUrls);
          message.success('图像生成成功！');
          console.log('图像生成成功，返回的数据:', data);
        } else {
          console.error('图像生成失败: 响应数据格式不正确', data);
          message.error(data.message || '图像生成失败');
        }
      } catch (jsonError) {
        console.error('图像生成失败: 响应解析错误', jsonError);
        message.error('图像生成失败: 无法解析服务器响应');
      }
    } catch (error) {
      console.error('图像生成过程中发生错误:', error);
      message.error('图像生成失败');
    } finally {
      setIsGenerating(false)
    }
  }

  // 清空结果
  const handleClear = () => {
    setGeneratedImages([])
    setPrompt('')
  }

  // 下载图像
  const handleDownload = (imageUrl: string, index: number) => {
    const link = document.createElement('a')
    link.href = imageUrl
    link.download = `generated-image-${index + 1}.jpg`
    link.click()
    message.success('图像下载成功！')
  }

  // 预设提示词
  const presetPrompts = [
    '一个美丽的日落海滩，金色的阳光洒在波浪上',
    '未来科技城市，霓虹灯光闪烁，飞行汽车穿梭',
    '神秘的森林，阳光透过树叶洒下，小鹿在溪边饮水',
    '可爱的猫咪戴着皇冠，坐在宝座上，油画风格'
  ]

  return (
    <div className="image-generation-page">
      <div className="page-header">
        <Title level={2}>
          <PictureOutlined /> AI图像生成
        </Title>
        <Paragraph type="secondary">
          输入文本描述，让AI为您创造独特的视觉艺术作品
        </Paragraph>
      </div>

      <div className="generation-content">
        <Row gutter={[24, 24]}>
          {/* 左侧控制面板 */}
          <Col xs={24} lg={8}>
            <Card title="生成设置" className="control-panel">
              <div className="form-section">
                <Title level={5}>图像描述</Title>
                <TextArea
                  rows={4}
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  placeholder="描述您想要生成的图像内容，越详细越好..."
                  maxLength={500}
                  showCount
                />
              </div>

              <div className="preset-prompts">
                <Title level={5}>快速提示</Title>
                <Space direction="vertical" style={{ width: '100%' }}>
                  {presetPrompts.map((preset, index) => (
                    <Tag
                      key={index}
                      style={{ cursor: 'pointer', marginBottom: 8 }}
                      onClick={() => setPrompt(preset)}
                    >
                      {preset}
                    </Tag>
                  ))}
                </Space>
              </div>

              <div className="generation-settings">
                <Title level={5}>高级设置</Title>
                
                <div className="setting-item">
                  <label>图像宽度: {settings.width}px</label>
                  <Slider
                    min={256}
                    max={1024}
                    step={64}
                    value={settings.width}
                    onChange={(value) => setSettings(prev => ({ ...prev, width: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>图像高度: {settings.height}px</label>
                  <Slider
                    min={256}
                    max={1024}
                    step={64}
                    value={settings.height}
                    onChange={(value) => setSettings(prev => ({ ...prev, height: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>生成步数: {settings.steps}</label>
                  <Slider
                    min={10}
                    max={50}
                    step={5}
                    value={settings.steps}
                    onChange={(value) => setSettings(prev => ({ ...prev, steps: value }))}
                  />
                </div>

                <div className="setting-item">
                  <label>引导系数: {settings.guidance}</label>
                  <Slider
                    min={1}
                    max={20}
                    step={0.5}
                    value={settings.guidance}
                    onChange={(value) => setSettings(prev => ({ ...prev, guidance: value }))}
                  />
                </div>
              </div>

              <div className="action-buttons">
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button
                    type="primary"
                    size="large"
                    loading={isGenerating}
                    onClick={handleGenerate}
                    icon={<PictureOutlined />}
                    block
                  >
                    生成图像
                  </Button>
                  <Button
                    danger
                    icon={<ClearOutlined />}
                    onClick={handleClear}
                    block
                  >
                    清空结果
                  </Button>
                </Space>
              </div>
            </Card>
          </Col>

          {/* 右侧结果展示 */}
          <Col xs={24} lg={16}>
            <Card title="生成结果" className="results-panel">
              {generatedImages.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">
                    <PictureOutlined style={{ fontSize: 64, color: '#ccc' }} />
                  </div>
                  <Paragraph type="secondary">
                    输入描述并点击生成按钮，AI将为您创造独特的图像
                  </Paragraph>
                </div>
              ) : (
                <Row gutter={[16, 16]}>
                  {generatedImages.map((image, index) => (
                    <Col xs={24} sm={12} key={index}>
                      <Card
                        className="generated-image-card"
                        cover={
                          <img
                            alt={`生成的图像 ${index + 1}`}
                            src={image}
                            style={{ width: '100%', height: 'auto' }}
                          />
                        }
                        actions={[
                          <Button
                            key="download"
                            type="link"
                            icon={<DownloadOutlined />}
                            onClick={() => handleDownload(image, index)}
                          >
                            下载
                          </Button>
                        ]}
                      >
                        <Card.Meta
                          title={`图像 ${index + 1}`}
                          description={prompt}
                        />
                      </Card>
                    </Col>
                  ))}
                </Row>
              )}
            </Card>

            {/* 使用提示 */}
            <Card title="使用提示" className="tips-panel" size="small">
              <ul className="tips-list">
                <li>详细描述您想要的图像内容，包括风格、色彩、构图等</li>
                <li>可以使用艺术风格词汇，如"油画风格"、"赛博朋克"、"水彩画"等</li>
                <li>调整高级设置可以获得不同质量的生成结果</li>
                <li>生成过程可能需要一些时间，请耐心等待</li>
              </ul>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default ImageGenerationPage