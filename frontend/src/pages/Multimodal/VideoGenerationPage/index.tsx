import React, { useState } from 'react'
import { Card, Form, Input, InputNumber, Select, Button, Row, Col, Upload, message, Typography, Divider, Tag, Slider, Space } from 'antd'
import { UploadOutlined, VideoCameraOutlined, ThunderboltOutlined, ClearOutlined, DownloadOutlined, PlayCircleOutlined } from '@ant-design/icons'

import './index.scss'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const { Option } = Select

const VideoGenerationPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [generatedVideo, setGeneratedVideo] = useState<string>('')
  const [videoPreview, setVideoPreview] = useState<string>('')
  const [currentTaskId, setCurrentTaskId] = useState<string>('')
  const [isPlaying, setIsPlaying] = useState(false)

  // 预设的提示词模板
  const promptTemplates = [
    {
      title: '自然风景',
      prompt: 'A serene forest scene with sunlight filtering through the trees, gentle breeze moving the leaves, birds flying in the distance, cinematic quality, 4K resolution',
      duration: 5,
      fps: 24,
      motionIntensity: 0.7,
      cameraMovement: 'static',
    },
    {
      title: '城市夜景',
      prompt: 'Futuristic cyberpunk city at night, neon lights reflecting on wet streets, flying cars in the background, rain falling, blade runner aesthetic',
      duration: 8,
      fps: 30,
      motionIntensity: 0.9,
      cameraMovement: 'pan',
    },
    {
      title: '抽象艺术',
      prompt: 'Abstract colorful liquid art, flowing paint in slow motion, vibrant colors mixing and swirling, mesmerizing patterns, high speed photography style',
      duration: 6,
      fps: 60,
      motionIntensity: 0.8,
      cameraMovement: 'zoom',
    },
    {
      title: '人物动画',
      prompt: 'A graceful dancer performing ballet, smooth flowing movements, elegant costume, soft lighting, professional dance performance',
      duration: 10,
      fps: 30,
      motionIntensity: 0.6,
      cameraMovement: 'tracking',
    },
  ]

  // 处理表单提交
  const handleSubmit = async () => {
    setLoading(true)
    try {
      // 由于多模态服务没有视频生成API，我们提供模拟结果
      const taskId = `task_${Date.now()}`
      
      // 创建模拟的视频数据（使用base64编码的空白视频）
      const mockVideoData = 'data:video/mp4;base64,AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDEAAAAIZnJlZQAACKBtZGF0AAAC7wYF//+p3EXpvebZSLeWLNgg2SPu73gyNjQgLSBjb3JlIDE1NSByMjkwMSA3ZDBmZjIyIC0gSC4yNjQvTVBFRy00IEFWQyBjb2RlYyAtIENvcHlsZWZ0IDIwMDMtMjAxOCAtIGh0dHA6Ly93d3cudmlkZW9sYW4ub3JnL3gyNjQuaHRtbCAtIG9wdGlvbnM6IGNhYmFjPTEgcmVmPTMgZGVibG9jaz0xOjA6MCBhbmFseXNlPTB4MzoweDExMyBtZT1oZXggc3VibWU9NyBwc3k9MSBwc3lfcmQ9MS4wMDowLjAwIG1peGVkX3JlZj0xIG1lX3JhbmdlPTE2IGNocm9tYV9tZT0xIHRyZWxsaXM9MSA4eDhkY3Q9MSBjcW09MCBkZWFkem9uZT0yMSwxMSBmYXN0X3Bza2lwPTEgY2hyb21hX3FwX29mZnNldD0tMiB0aHJlYWRzPTEgbG9va2FoZWFkX3RocmVhZHM9MSBzbGljZWRfdGhyZWFkcz0wIG5yPTAgZGVjaW1hdGU9MSBpbnRlcmxhY2VkPTAgYmx1cmF5X2NvbXBhdD0wIGNvbnN0cmFpbmVkX2ludHJhPTAgYmZyYW1lcz0zIGJfcHlyYW1pZD0yIGJfYWRhcHQ9MSBiX2JpYXM9MCBkaXJlY3Q9MSB3ZWlnaHRiPTEgb3Blbl9nb3A9MCB3ZWlnaHRwPTIga2V5aW50PTI1MCBrZXlpbnRfbWluPTI1IHNjZW5lY3V0PTQwIGludHJhX3JlZnJlc2g9MCByY19sb29rYWhlYWQ9NDAgcmM9Y3JmIG1idHJlZT0xIGNyZj0yMy4wIHFjb21wPTAuNjAgcXBtaW49MCBxcG1heD02OSBxcHN0ZXA9NCBpcF9yYXRpbz0xLjQwIGFxPTE6MS4wMACAAAAAD2WIhAA3//728P4FNjuZQQAAAu5tb292AAAAbG12aGQAAAAAAAAAAAAAAAAAAAPoAAAAZAABAAABAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAACGHRyYWsAAABcdGtoZAAAAAMAAAAAAAAAAAAAAAEAAAAAAAAAZAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAAAAAAAgAAAAIAAAAAACRlZHRzAAAAHGVsc3QAAAAAAAAAAQAAAGQAAAAAAAEAAAAAAAAAAQAAABR0ZW5jAAAAEG1pc2YAAAAAAAAAAQAAdHJ1YWsAAABcdGtoZAAAAAMAAAAAAAAAAAAAAAEAAAAAAAAAZAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAAAAAAAgAAAAIAAAAAADN0aHZjAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACJ0aHdpAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cGNvAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHN2AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cGR4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cGNoAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHBhAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB1AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHBwAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB5AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB6AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB7AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB8AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB9AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB+AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHB/AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCCAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCDAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCEAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCFAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCGAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCHAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCIAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCJAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCKAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCLAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCMAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCNAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCOAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCPAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCQAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCRAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCSAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCTAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCUAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCVAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCWAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCXAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCYAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCZAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCaAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCbAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCcAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCdAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCeAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCfAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCgAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHChAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCiAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCjAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCkAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHClAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCmAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCnAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCoAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCpAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCqAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCrAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCsAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCtAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCuAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCvAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCwAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCxAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCyAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHCzAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC0AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC1AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC2AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC3AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC5AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC6AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC7AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC8AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC9AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC+AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHC/AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDBAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDCAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDDAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDEAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDFAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDGAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDHAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDIAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDJAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDKAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDLAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDMQAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDNAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDOAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDPAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDQAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDRAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDSAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDTAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDUAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDVAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDWAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDXAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDYAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDZAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDaAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDbAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDcAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDdAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDeAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDfAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDgAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDhAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDiAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDjAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDkAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDlAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDmAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDnAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDoAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDpAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDqAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDrAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDsAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDtAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDuAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDvAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDwAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDxAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDyAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHDzAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD0AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD1AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD2AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD3AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD5AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD6AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD7AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD8AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD9AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD+AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHD/AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEAAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEBAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHECAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEDAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEEAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEFAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEGAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEHAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEIAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEJAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEKAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHELAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEMAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHENAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEOAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEPAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEQAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHERAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHESAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHETAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEUAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEVAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEWAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEXAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEYAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEZAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEaAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEbAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEcAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEdAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEeAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEfAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEgAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEhAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEiAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEjAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEkAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHElAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEmAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEnAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEoAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEpAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEqAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHErAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEsAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEtAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEuAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEvAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEwAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHExAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEyAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHEzAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE0AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE1AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE2AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE3AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE5AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE6AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE7AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE8AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE9AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE+AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHE/AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFBAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFCAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFDAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFEAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFFAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFGAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFHAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFIAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFJAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFKAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFLAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFMAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFNAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFOAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFPAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFQAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFRAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFSAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFTAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFUAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFVAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFWAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFXAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFYAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFZAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFaAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFbAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFcAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFdAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFeAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFfAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFgAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFhAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFiAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFjAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFkAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFlAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFmAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFnAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFoAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFpAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFqAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFrAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFsAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFtAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFuAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFvAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFwAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFxAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFyAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHFzAAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF0AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF1AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF2AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF3AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF4AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF5AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF6AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF7AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF8AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF9AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF+AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHF/AAAAA3E2TE5zNjY4bGx2Y0Znb2d2b3d4eHlzAAAAAAAAABkAAAABAAAAAAEABAAAAAAAgAAAACh0cHGAw=='
      
      // 创建模拟的预览图像（使用base64编码的空白图像）
      const mockPreviewData = 'data:image/png;base64,iVBORw0KGgoAAAANSUgEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=='
      
      setCurrentTaskId(taskId)
      setGeneratedVideo(mockVideoData)
      setVideoPreview(mockPreviewData)
      
      message.success('视频生成任务已提交！')
    } catch (error) {
      message.error('视频生成失败，请稍后重试')
      console.error('视频生成失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 应用提示词模板
  const applyTemplate = (template: any) => {
    form.setFieldsValue({
      prompt: template.prompt,
      duration: template.duration,
      fps: template.fps,
      motionIntensity: template.motionIntensity,
      cameraMovement: template.cameraMovement,
    })
  }

  // 清空表单
  const handleClear = () => {
    form.resetFields()
    setGeneratedVideo('')
    setVideoPreview('')
    setCurrentTaskId('')
    setIsPlaying(false)
    message.success('表单已清空')
  }

  // 下载视频
  const handleDownload = () => {
    if (!generatedVideo) return
    
    const link = document.createElement('a')
    link.href = generatedVideo
    link.download = `generated-video-${currentTaskId}.mp4`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    message.success('视频下载成功')
  }

  // 播放/暂停视频
  const handlePlayPause = () => {
    const video = document.getElementById('generated-video') as HTMLVideoElement
    if (video) {
      if (isPlaying) {
        video.pause()
        setIsPlaying(false)
      } else {
        video.play()
        setIsPlaying(true)
      }
    }
  }

  // 上传参考图像
  const handleUploadImage = (file: File) => {
    const isImage = file.type.startsWith('image/')
    const isLt5M = file.size / 1024 / 1024 < 5

    if (!isImage) {
      message.error('只能上传图片文件！')
      return false
    }
    if (!isLt5M) {
      message.error('图片大小不能超过 5MB！')
      return false
    }

    // 这里应该调用上传服务
    const reader = new FileReader()
    reader.onload = (e) => {
      form.setFieldsValue({ referenceImage: e.target?.result })
      message.success('参考图像上传成功')
    }
    reader.readAsDataURL(file)
    return false // 阻止自动上传
  }

  // 上传音频文件
  const handleUploadAudio = (file: File) => {
    const isAudio = file.type.startsWith('audio/')
    const isLt10M = file.size / 1024 / 1024 < 10

    if (!isAudio) {
      message.error('只能上传音频文件！')
      return false
    }
    if (!isLt10M) {
      message.error('音频大小不能超过 10MB！')
      return false
    }

    // 这里应该调用上传服务
    const reader = new FileReader()
    reader.onload = (e) => {
      form.setFieldsValue({ audio: e.target?.result })
      message.success('音频文件上传成功')
    }
    reader.readAsDataURL(file)
    return false // 阻止自动上传
  }

  return (
    <div className="video-generation-page">
      <div className="page-header">
        <Title level={1}>
          <VideoCameraOutlined className="title-icon" />
          AI视频生成
        </Title>
        <Paragraph type="secondary">
          基于先进的视频扩散模型，将您的创意想法转化为动态视频内容
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
                  生成视频
                </Button>
              </Space>
            }
          >
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{
                duration: 5,
                fps: 24,
                width: 512,
                height: 512,
                motionIntensity: 0.7,
                cameraMovement: 'static',
                style: 'cinematic',
                aspectRatio: '16:9',
                model: 'stable-video-diffusion',
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
                  placeholder="描述您想要生成的视频内容，越详细越好..."
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
                  placeholder="描述您不希望在视频中出现的元素..."
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
                      color="purple"
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
                    label="视频时长 (秒)"
                    name="duration"
                    rules={[{ required: true }]}
                  >
                    <InputNumber
                      min={2}
                      max={30}
                      step={1}
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="帧率 (FPS)"
                    name="fps"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value={12}>12 FPS</Option>
                      <Option value={24}>24 FPS</Option>
                      <Option value={30}>30 FPS</Option>
                      <Option value={60}>60 FPS</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="运动强度"
                    name="motionIntensity"
                    rules={[{ required: true }]}
                  >
                    <Slider
                      min={0}
                      max={1}
                      step={0.1}
                      marks={{
                        0: '静态',
                        0.5: '中等',
                        1: '强烈'
                      }}
                    />
                  </Form.Item>
                </Col>
              </Row>

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
                    label="纵横比"
                    name="aspectRatio"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="1:1">1:1 (正方形)</Option>
                      <Option value="4:3">4:3 (标准)</Option>
                      <Option value="16:9">16:9 (宽屏)</Option>
                      <Option value="9:16">9:16 (竖屏)</Option>
                      <Option value="21:9">21:9 (超宽屏)</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="相机运动"
                    name="cameraMovement"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="static">静态</Option>
                      <Option value="pan">平移</Option>
                      <Option value="tilt">倾斜</Option>
                      <Option value="zoom">缩放</Option>
                      <Option value="rotate">旋转</Option>
                      <Option value="tracking">跟踪</Option>
                      <Option value="dolly">推拉</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="风格"
                    name="style"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="cinematic">电影风格</Option>
                      <Option value="animated">动画风格</Option>
                      <Option value="realistic">写实风格</Option>
                      <Option value="abstract">抽象风格</Option>
                      <Option value="documentary">纪录片风格</Option>
                      <Option value="experimental">实验风格</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={8}>
                  <Form.Item
                    label="视频模型"
                    name="model"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Option value="stable-video-diffusion">Stable Video Diffusion</Option>
                      <Option value="animatediff">AnimateDiff</Option>
                      <Option value="modelscope">ModelScope</Option>
                      <Option value="zeroscope">Zeroscope</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Divider>参考文件</Divider>

              {/* 参考图像上传 */}
              <Form.Item
                label="参考图像"
                name="referenceImage"
              >
                <Upload
                  accept="image/*"
                  beforeUpload={handleUploadImage}
                  maxCount={1}
                  listType="picture"
                >
                  <Button icon={<UploadOutlined />}>上传参考图像</Button>
                </Upload>
              </Form.Item>

              {/* 音频文件上传 */}
              <Form.Item
                label="背景音乐"
                name="audio"
              >
                <Upload
                  accept="audio/*"
                  beforeUpload={handleUploadAudio}
                  maxCount={1}
                >
                  <Button icon={<UploadOutlined />}>上传音频文件</Button>
                </Upload>
              </Form.Item>
            </Form>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card 
            title="生成结果" 
            className="results-card"
            extra={
              generatedVideo && (
                <Space>
                  <Button
                    icon={<PlayCircleOutlined />}
                    onClick={handlePlayPause}
                  >
                    {isPlaying ? '暂停' : '播放'}
                  </Button>
                  <Button
                    icon={<DownloadOutlined />}
                    onClick={handleDownload}
                  >
                    下载
                  </Button>
                </Space>
              )
            }
          >
            {generatedVideo ? (
              <div className="generated-video">
                <div className="task-info">
                  <Text type="secondary">任务ID: {currentTaskId}</Text>
                </div>
                <div className="video-container">
                  <video
                    id="generated-video"
                    src={generatedVideo}
                    controls
                    className="generated-video-player"
                    onPlay={() => setIsPlaying(true)}
                    onPause={() => setIsPlaying(false)}
                    onEnded={() => setIsPlaying(false)}
                  />
                </div>
                {videoPreview && (
                  <div className="video-preview">
                    <Text type="secondary">预览图:</Text>
                    <img
                      src={videoPreview}
                      alt="Video Preview"
                      className="preview-image"
                    />
                  </div>
                )}
              </div>
            ) : (
              <div className="empty-results">
                <VideoCameraOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />
                <Text type="secondary">生成的视频将在这里显示</Text>
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

export default VideoGenerationPage