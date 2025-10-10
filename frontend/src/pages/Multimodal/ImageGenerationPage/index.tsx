import React, { useState } from 'react'
import { Card, Form, Input, InputNumber, Select, Button, Space, Row, Col, message, Typography, Divider, Tag, Slider, Switch } from 'antd'
import { PictureOutlined, ThunderboltOutlined, ClearOutlined, DownloadOutlined } from '@ant-design/icons'
import { imageGenerationService } from '@services/imageGenerationService'
import { ImageGenerationRequest } from '@interfaces/multimodal'
import './index.scss'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { Option } = Select

const ImageGenerationPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [generatedImages, setGeneratedImages] = useState<string[]>([])
  const [currentTaskId, setCurrentTaskId] = useState<string>('')

  // 预设的提示词模板
  const promptTemplates = [
    {
      title: '风景摄影',
      prompt: 'A breathtaking mountain landscape at sunrise, with golden light illuminating the peaks, misty valleys below, photorealistic, ultra high definition, 8K',
      negativePrompt: 'blurry, low quality, distorted, overexposed, underexposed',
    },
    {
      title: '人物肖像',
      prompt: 'A beautiful young woman with long flowing hair, soft natural lighting, professional portrait photography, shallow depth of field, bokeh background',
      negativePrompt: 'blurry, distorted, ugly, deformed, extra limbs, bad anatomy',
    },
    {
      title: '产品设计',
      prompt: 'Modern minimalist smartphone design, sleek metallic finish, clean background, professional product photography, studio lighting',
      negativePrompt: 'cluttered, messy background, low quality, blurry, distorted',
    },
    {
      title: '艺术创作',
      prompt: 'Surreal digital art featuring floating islands in the sky, vibrant colors, dreamlike atmosphere, highly detailed, artistic masterpiece',
      negativePrompt: 'blurry, low quality, pixelated, amateur, simple',
    },
  ]

  // 处理表单提交
  const handleSubmit = async (values: any) => {
    setLoading(true)
    try {
      const request: ImageGenerationRequest = {
        prompt: values.prompt,
        negativePrompt: values.negativePrompt,
        width: values.width || 512,
        height: values.height || 512,
        numImages: values.numImages || 1,
        steps: values.steps || 50,
        guidanceScale: values.guidanceScale || 7.5,
        seed: values.seed || -1,
        model: values.model || 'stable-diffusion-v1-5',
        sampler: values.sampler || 'DPM++ 2M Karras',
        outputFormat: values.outputFormat || 'png',
        quality: values.quality || 95,
        parameters: {
          enableHighRes: values.enableHighRes || false,
          highResScale: values.highResScale || 2,
          highResSteps: values.highResSteps || 20,
          enableFaceRestore: values.enableFaceRestore || false,
          enableColorCorrection: values.enableColorCorrection || false,
        },
        controlNet: {
          enabled: values.enableControlNet || false,
          module: values.controlNetModule || 'canny',
          model: values.controlNetModel || 'control_v11p_sd15_canny',
          weight: values.controlNetWeight || 1.0,
          guidanceStart: values.controlNetGuidanceStart || 0.0,
          guidanceEnd: values.controlNetGuidanceEnd || 1.0,
        },
        lora: {
          enabled: values.enableLoRA || false,
          model: values.loraModel || '',
          weight: values.loraWeight || 0.8,
        },
      }

      const response = await imageGenerationService.generateImage(request)
      setCurrentTaskId(response.taskId)
      setGeneratedImages(response.images || [])
      
      message.success('图像生成成功！')
    } catch (error) {
      message.error('图像生成失败，请稍后重试')
      console.error('图像生成失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 应用提示词模板
  const applyTemplate = (template: any) => {
    form.setFieldsValue({
      prompt: template.prompt,
      negativePrompt: template.negativePrompt,
    })
  }

  // 清空表单
  const handleClear = () => {
    form.resetFields()
    setGeneratedImages([])
    setCurrentTaskId('')
    message.success('表单已清空')
  }

  // 下载图片
  const handleDownload = (imageUrl: string, index: number) => {
    const link = document.createElement('a')
    link.href = imageUrl
    link.download = `generated-image-${currentTaskId}-${index + 1}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    message.success(`图片 ${index + 1} 下载成功`)
  }

  return (
    <div className="image-generation-page">
      <div className="page-header">
        <Title level={1}>
          <PictureOutlined className="title-icon" />
          AI图像生成
        </Title>
        <Paragraph type="secondary">
          基于先进的扩散模型，将您的创意想法转化为精美的图像作品
        </Paragraph>
      </div>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={16}>
          <Card 
            title="生成设置" 
            className="generation-card"
            extra={
              <Space>
                <Button
                  icon={<ClearOutlined />}
                  onClick={handleClear}
                >
                  清空
                </Button>
                <Button
                  type="primary"
                  icon={<ThunderboltOutlined />}
                  loading={loading}
                  onClick={() => form.submit()}
                >
                  生成图像
                </Button>
              </Space>
            }
          >
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{
                width: 512,
                height: 512,
                numImages: 1,
                steps: 50,
                guidanceScale: 7.5,
                seed: -1,
                model: 'stable-diffusion-v1-5',
                sampler: 'DPM++ 2M Karras',
                outputFormat: 'png',
                quality: 95,
                enableHighRes: false,
                highResScale: 2,
                highResSteps: 20,
                enableFaceRestore: false,
                enableColorCorrection: false,
                enableControlNet: false,
                enableLoRA: false,
              }}
            >
              {/* 提示词输入 */}
              <Form.Item
                label="提示词 (Prompt)"
                name="prompt"
                rules={[{ required: true, message: '请输入提示词' }]}
              >
                <TextArea
                  rows={4}
                  placeholder="描述您想要生成的图像内容，越详细越好..."
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              <Form.Item
                label="负面提示词 (Negative Prompt)"
                name="negativePrompt"
              >
                <TextArea
                  rows={2}
                  placeholder="描述您不希望在图像中出现的内容..."
                  maxLength={500}
                  showCount
                />
              </Form.Item>

              {/* 提示词模板 */}
              <div className="prompt-templates">
                <Text type="secondary" style={{ marginBottom: 8, display: 'block' }}>
                  快速模板：
                </Text>
                <Space wrap>
                  {promptTemplates.map((template, index) => (
                    <Tag
                      key={index}
                      color="blue"
                      style={{ cursor: 'pointer' }}
                      onClick={() => applyTemplate(template)}
                    >
                      {template.title}
                    </Tag>
                  ))}
                </Space>
              </div>

              <Divider />

              {/* 基础参数 */}
              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="宽度"
                    name="width"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value={256}>256px</Option>
                      <Option value={384}>384px</Option>
                      <Option value={512}>512px</Option>
                      <Option value={640}>640px</Option>
                      <Option value={768}>768px</Option>
                      <Option value={1024}>1024px</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="高度"
                    name="height"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value={256}>256px</Option>
                      <Option value={384}>384px</Option>
                      <Option value={512}>512px</Option>
                      <Option value={640}>640px</Option>
                      <Option value={768}>768px</Option>
                      <Option value={1024}>1024px</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="生成数量"
                    name="numImages"
                    rules={[{ required: true }]}
                  >
                    <InputNumber
                      min={1}
                      max={4}
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="采样步数"
                    name="steps"
                    rules={[{ required: true }]}
                  >
                    <Slider
                      min={10}
                      max={100}
                      step={5}
                      marks={{
                        10: '10',
                        50: '50',
                        100: '100'
                      }}
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="引导系数"
                    name="guidanceScale"
                    rules={[{ required: true }]}
                  >
                    <Slider
                      min={1}
                      max={20}
                      step={0.5}
                      marks={{
                        1: '1',
                        7.5: '7.5',
                        20: '20'
                      }}
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="随机种子"
                    name="seed"
                    tooltip="-1表示随机种子"
                  >
                    <InputNumber
                      min={-1}
                      max={999999}
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="模型"
                    name="model"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="stable-diffusion-v1-5">Stable Diffusion v1.5</Option>
                      <Option value="stable-diffusion-v2-1">Stable Diffusion v2.1</Option>
                      <Option value="stable-diffusion-xl">Stable Diffusion XL</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="采样器"
                    name="sampler"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="DPM++ 2M Karras">DPM++ 2M Karras</Option>
                      <Option value="Euler a">Euler a</Option>
                      <Option value="Euler">Euler</Option>
                      <Option value="Heun">Heun</Option>
                      <Option value="DPM2">DPM2</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="输出格式"
                    name="outputFormat"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="png">PNG</Option>
                      <Option value="jpg">JPG</Option>
                      <Option value="webp">WebP</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Divider>高级设置</Divider>

              {/* 高分辨率修复 */}
              <Form.Item
                label="高分辨率修复"
                name="enableHighRes"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="放大倍数"
                    name="highResScale"
                    dependencies={['enableHighRes']}
                  >
                    <Select disabled={!form.getFieldValue('enableHighRes')}>
                      <Option value={1.5}>1.5x</Option>
                      <Option value={2}>2x</Option>
                      <Option value={3}>3x</Option>
                      <Option value={4}>4x</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="高分辨率步数"
                    name="highResSteps"
                    dependencies={['enableHighRes']}
                  >
                    <InputNumber
                      min={10}
                      max={50}
                      style={{ width: '100%' }}
                      disabled={!form.getFieldValue('enableHighRes')}
                    />
                  </Form.Item>
                </Col>
              </Row>

              {/* ControlNet */}
              <Form.Item
                label="启用ControlNet"
                name="enableControlNet"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="ControlNet模块"
                    name="controlNetModule"
                    dependencies={['enableControlNet']}
                  >
                    <Select disabled={!form.getFieldValue('enableControlNet')}>
                      <Option value="canny">Canny</Option>
                      <Option value="openpose">OpenPose</Option>
                      <Option value="depth">Depth</Option>
                      <Option value="normal">Normal</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="ControlNet权重"
                    name="controlNetWeight"
                    dependencies={['enableControlNet']}
                  >
                    <Slider
                      min={0}
                      max={2}
                      step={0.1}
                      disabled={!form.getFieldValue('enableControlNet')}
                    />
                  </Form.Item>
                </Col>
              </Row>

              {/* LoRA */}
              <Form.Item
                label="启用LoRA"
                name="enableLoRA"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="LoRA模型"
                    name="loraModel"
                    dependencies={['enableLoRA']}
                  >
                    <Input
                      placeholder="输入LoRA模型名称"
                      disabled={!form.getFieldValue('enableLoRA')}
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="LoRA权重"
                    name="loraWeight"
                    dependencies={['enableLoRA']}
                  >
                    <Slider
                      min={0}
                      max={1}
                      step={0.1}
                      disabled={!form.getFieldValue('enableLoRA')}
                    />
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card title="生成结果" className="results-card">
            {generatedImages.length === 0 ? (
              <div className="empty-results">
                <PictureOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />
                <Text type="secondary">生成的图像将在这里显示</Text>
              </div>
            ) : (
              <div className="generated-images">
                <div className="task-info">
                  <Text type="secondary">任务ID: {currentTaskId}</Text>
                </div>
                <Row gutter={[16, 16]}>
                  {generatedImages.map((image, index) => (
                    <Col xs={24} sm={12} key={index}>
                      <div className="image-item">
                        <img
                          src={image}
                          alt={`Generated ${index + 1}`}
                          className="generated-image"
                        />
                        <div className="image-actions">
                          <Button
                            type="primary"
                            size="small"
                            icon={<DownloadOutlined />}
                            onClick={() => handleDownload(image, index)}
                          >
                            下载
                          </Button>
                        </div>
                      </div>
                    </Col>
                  ))}
                </Row>
              </div>
            )}
          </Card>

          {/* 生成历史 */}
          <Card title="生成历史" className="history-card">
            <div className="history-list">
              <Text type="secondary">暂无生成历史</Text>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default ImageGenerationPage