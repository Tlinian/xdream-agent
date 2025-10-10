import React, { useState, useRef } from 'react'
import { Card, Form, Input, Select, Button, Space, Slider, Switch, Radio, Divider, message, Progress, Tag } from 'antd'
import { SoundOutlined, DownloadOutlined, PlayCircleOutlined, PauseCircleOutlined } from '@ant-design/icons'
import './index.scss'

const { TextArea } = Input
const { Option } = Select

interface TTSConfig {
  text: string
  voice: string
  language: string
  speed: number
  pitch: number
  volume: number
  format: string
  quality: string
  enableSsml: boolean
  style: string
  styleDegree: number
}

const TextToSpeechPage: React.FC = () => {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [playing, setPlaying] = useState(false)
  const [audioUrl, setAudioUrl] = useState<string>('')
  const [progress, setProgress] = useState(0)
  const audioRef = useRef<HTMLAudioElement>(null)

  const voiceOptions = [
    { value: 'zh-CN-XiaoxiaoNeural', label: '晓晓 (女声)', language: 'zh-CN', gender: 'female' },
    { value: 'zh-CN-XiaoyiNeural', label: '晓艺 (女声)', language: 'zh-CN', gender: 'female' },
    { value: 'zh-CN-YunjianNeural', label: '云健 (男声)', language: 'zh-CN', gender: 'male' },
    { value: 'zh-CN-YunxiNeural', label: '云希 (男声)', language: 'zh-CN', gender: 'male' },
    { value: 'zh-CN-YunxiaNeural', label: '云夏 (男声)', language: 'zh-CN', gender: 'male' },
    { value: 'zh-CN-liaoning-XiaobeiNeural', label: '晓北 (东北话女声)', language: 'zh-CN', gender: 'female' },
    { value: 'en-US-AriaNeural', label: 'Aria (女声)', language: 'en-US', gender: 'female' },
    { value: 'en-US-JennyNeural', label: 'Jenny (女声)', language: 'en-US', gender: 'female' },
    { value: 'en-US-GuyNeural', label: 'Guy (男声)', language: 'en-US', gender: 'male' },
    { value: 'ja-JP-NanamiNeural', label: '七美 (女声)', language: 'ja-JP', gender: 'female' },
    { value: 'ko-KR-SunHiNeural', label: '善熙 (女声)', language: 'ko-KR', gender: 'female' },
    { value: 'fr-FR-DeniseNeural', label: 'Denise (女声)', language: 'fr-FR', gender: 'female' },
  ]

  const languageOptions = [
    { value: 'zh-CN', label: '中文 (简体)' },
    { value: 'zh-TW', label: '中文 (繁体)' },
    { value: 'en-US', label: '英语 (美国)' },
    { value: 'en-GB', label: '英语 (英国)' },
    { value: 'ja-JP', label: '日语' },
    { value: 'ko-KR', label: '韩语' },
    { value: 'fr-FR', label: '法语' },
    { value: 'de-DE', label: '德语' },
    { value: 'es-ES', label: '西班牙语' },
    { value: 'ru-RU', label: '俄语' },
    { value: 'ar-SA', label: '阿拉伯语' },
  ]

  const styleOptions = [
    { value: 'general', label: '通用' },
    { value: 'assistant', label: '助手' },
    { value: 'chat', label: '聊天' },
    { value: 'customerservice', label: '客服' },
    { value: 'newscast', label: '新闻' },
    { value: 'affectionate', label: '亲切' },
    { value: 'angry', label: '愤怒' },
    { value: 'cheerful', label: '愉快' },
    { value: 'sad', label: '悲伤' },
    { value: 'excited', label: '兴奋' },
    { value: 'friendly', label: '友好' },
    { value: 'hopeful', label: '希望' },
    { value: 'shouting', label: '呼喊' },
    { value: 'terrified', label: '恐惧' },
    { value: 'unfriendly', label: '不友好' },
  ]

  const handleSubmit = async (values: TTSConfig) => {
    if (!values.text.trim()) {
      message.error('请输入要转换的文本')
      return
    }

    setLoading(true)
    setProgress(0)

    try {
      // 模拟文本转语音API调用
      const formData = new FormData()
      formData.append('text', values.text)
      formData.append('voice', values.voice)
      formData.append('language', values.language)
      formData.append('speed', values.speed.toString())
      formData.append('pitch', values.pitch.toString())
      formData.append('volume', values.volume.toString())
      formData.append('format', values.format)
      formData.append('quality', values.quality)
      formData.append('enableSsml', values.enableSsml.toString())
      formData.append('style', values.style)
      formData.append('styleDegree', values.styleDegree.toString())

      // 模拟进度
      const progressInterval = setInterval(() => {
        setProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval)
            return 90
          }
          return prev + 10
        })
      }, 200)

      // 模拟API延迟
      await new Promise(resolve => setTimeout(resolve, 2000))
      clearInterval(progressInterval)
      setProgress(100)

      // 创建模拟音频URL
      const mockAudioData = 'data:audio/wav;base64,UklGRiQAAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YQAAAAA='
      setAudioUrl(mockAudioData)
      
      message.success('文本转语音转换成功！')
    } catch (error) {
      message.error('转换失败，请重试')
    } finally {
      setLoading(false)
    }
  }

  const handlePlay = () => {
    if (audioUrl && audioRef.current) {
      if (playing) {
        audioRef.current.pause()
        setPlaying(false)
      } else {
        audioRef.current.play()
        setPlaying(true)
      }
    }
  }

  const handleDownload = () => {
    if (audioUrl) {
      const link = document.createElement('a')
      link.href = audioUrl
      link.download = `tts_${Date.now()}.wav`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      message.success('音频文件下载成功！')
    }
  }

  const handleVoiceChange = (voice: string) => {
    const selectedVoice = voiceOptions.find(v => v.value === voice)
    if (selectedVoice) {
      form.setFieldsValue({ language: selectedVoice.language })
    }
  }

  const sampleTexts = [
    '欢迎使用文本转语音服务，这是中文语音合成的示例。',
    'Welcome to the text-to-speech service. This is an English speech synthesis example.',
    'テキスト読み上げサービスへようこそ。これは日本語の音声合成の例です。',
    '안녕하세요, 텍스트 음성 변환 서비스입니다. 이것은 한국어 음성 합성의 예입니다.',
  ]

  const insertSampleText = (index: number) => {
    form.setFieldsValue({ text: sampleTexts[index] })
  }

  return (
    <div className="text-to-speech-page">
      <div className="page-header">
        <div className="header-content">
          <h1 className="page-title">
            <SoundOutlined />
            文本转语音
          </h1>
          <p className="page-description">
            高质量文本转语音服务，支持多种语音、语言和情感表达，声音自然流畅
          </p>
        </div>
      </div>

      <div className="tts-container">
        <div className="tts-form-section">
          <Card title="文本输入" className="text-input-card">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{
                voice: 'zh-CN-XiaoxiaoNeural',
                language: 'zh-CN',
                speed: 1.0,
                pitch: 1.0,
                volume: 1.0,
                format: 'wav',
                quality: 'high',
                enableSsml: false,
                style: 'general',
                styleDegree: 1.0,
              }}
            >
              <Form.Item
                name="text"
                label="输入文本"
                rules={[{ required: true, message: '请输入要转换的文本' }]}
              >
                <TextArea
                  rows={6}
                  placeholder="请输入要转换为语音的文本内容..."
                  maxLength={5000}
                  showCount
                />
              </Form.Item>

              <div className="sample-texts">
                <span className="sample-label">示例文本：</span>
                <Space>
                  <Button size="small" onClick={() => insertSampleText(0)}>中文</Button>
                  <Button size="small" onClick={() => insertSampleText(1)}>English</Button>
                  <Button size="small" onClick={() => insertSampleText(2)}>日本語</Button>
                  <Button size="small" onClick={() => insertSampleText(3)}>한국어</Button>
                </Space>
              </div>

              <Divider />

              <div className="form-sections">
                <div className="voice-section">
                  <h3>语音设置</h3>
                  <Form.Item name="voice" label="选择声音">
                    <Select
                      style={{ width: '100%' }}
                      onChange={handleVoiceChange}
                      placeholder="选择声音"
                    >
                      {voiceOptions.map(voice => (
                        <Option key={voice.value} value={voice.value}>
                          <div className="voice-option">
                            <span className="voice-name">{voice.label}</span>
                            <span className="voice-lang">({voice.language})</span>
                            <Tag color={voice.gender === 'female' ? 'pink' : 'blue'}>
                              {voice.gender === 'female' ? '女' : '男'}
                            </Tag>
                          </div>
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>

                  <Form.Item name="language" label="语言">
                    <Select style={{ width: '100%' }} placeholder="选择语言">
                      {languageOptions.map(lang => (
                        <Option key={lang.value} value={lang.value}>
                          {lang.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </div>

                <div className="prosody-section">
                  <h3>韵律设置</h3>
                  <Form.Item name="speed" label="语速">
                    <Slider
                      min={0.5}
                      max={2.0}
                      step={0.1}
                      marks={{
                        0.5: '很慢',
                        1.0: '正常',
                        1.5: '较快',
                        2.0: '很快'
                      }}
                    />
                  </Form.Item>

                  <Form.Item name="pitch" label="音调">
                    <Slider
                      min={0.5}
                      max={1.5}
                      step={0.1}
                      marks={{
                        0.5: '低沉',
                        1.0: '正常',
                        1.5: '高亢'
                      }}
                    />
                  </Form.Item>

                  <Form.Item name="volume" label="音量">
                    <Slider
                      min={0.1}
                      max={1.0}
                      step={0.1}
                      marks={{
                        0.1: '很轻',
                        0.5: '较轻',
                        1.0: '正常'
                      }}
                    />
                  </Form.Item>
                </div>

                <div className="style-section">
                  <h3>风格设置</h3>
                  <Form.Item name="style" label="说话风格">
                    <Select style={{ width: '100%' }} placeholder="选择风格">
                      {styleOptions.map(style => (
                        <Option key={style.value} value={style.value}>
                          {style.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>

                  <Form.Item name="styleDegree" label="风格强度">
                    <Slider
                      min={0.1}
                      max={2.0}
                      step={0.1}
                      marks={{
                        0.1: '轻微',
                        1.0: '标准',
                        2.0: '强烈'
                      }}
                    />
                  </Form.Item>
                </div>

                <div className="output-section">
                  <h3>输出设置</h3>
                  <Form.Item name="format" label="音频格式">
                    <Radio.Group>
                      <Radio value="wav">WAV</Radio>
                      <Radio value="mp3">MP3</Radio>
                      <Radio value="m4a">M4A</Radio>
                    </Radio.Group>
                  </Form.Item>

                  <Form.Item name="quality" label="音质">
                    <Radio.Group>
                      <Radio value="low">低</Radio>
                      <Radio value="medium">中</Radio>
                      <Radio value="high">高</Radio>
                    </Radio.Group>
                  </Form.Item>

                  <Form.Item name="enableSsml" label="启用SSML" valuePropName="checked">
                    <Switch />
                  </Form.Item>
                </div>
              </div>

              <Form.Item>
                <Space>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={loading}
                    icon={<SoundOutlined />}
                  >
                    开始转换
                  </Button>
                  <Button
                    onClick={() => form.resetFields()}
                  >
                    重置
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </div>

        <div className="tts-result-section">
          <Card title="转换结果" className="result-card">
            {loading && (
              <div className="loading-section">
                <div className="progress-container">
                  <Progress
                    percent={progress}
                    status="active"
                    strokeColor={{
                      '0%': '#108ee9',
                      '100%': '#87d068',
                    }}
                  />
                  <p className="loading-text">正在转换文本到语音...</p>
                </div>
              </div>
            )}

            {audioUrl && !loading && (
              <div className="result-section">
                <div className="audio-player">
                  <audio
                    ref={audioRef}
                    src={audioUrl}
                    onEnded={() => setPlaying(false)}
                    onPause={() => setPlaying(false)}
                  />
                  <div className="player-controls">
                    <Button
                      type="primary"
                      size="large"
                      icon={playing ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
                      onClick={handlePlay}
                    >
                      {playing ? '暂停' : '播放'}
                    </Button>
                    <Button
                      icon={<DownloadOutlined />}
                      size="large"
                      onClick={handleDownload}
                    >
                      下载音频
                    </Button>
                  </div>
                </div>
              </div>
            )}

            {!audioUrl && !loading && (
              <div className="empty-result">
                <SoundOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />
                <p>暂无转换结果</p>
                <p className="hint">输入文本并开始转换以生成语音</p>
              </div>
            )}
          </Card>

          <div className="feature-highlights">
            <Card title="功能特色" className="features-card">
              <div className="features-grid">
                <div className="feature-item">
                  <h4>🎭 多种声音</h4>
                  <p>支持多种语言和声音选择，满足不同场景需求</p>
                </div>
                <div className="feature-item">
                  <h4>🎨 丰富风格</h4>
                  <p>支持多种说话风格，包括新闻、客服、聊天等</p>
                </div>
                <div className="feature-item">
                  <h4>🎚️ 灵活调节</h4>
                  <p>可调节语速、音调、音量等参数，自定义语音效果</p>
                </div>
                <div className="feature-item">
                  <h4>📱 多格式输出</h4>
                  <p>支持WAV、MP3、M4A等多种音频格式输出</p>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </div>
  )
}

export default TextToSpeechPage