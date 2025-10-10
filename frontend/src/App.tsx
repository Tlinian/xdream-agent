import { Routes, Route } from 'react-router-dom'
import { ConfigProvider, App as AntApp } from 'antd'
import { HomePage, ChatPage, SettingsPage, NotFoundPage } from './pages'
import { MultimodalPage, ImageGenerationPage, VideoGenerationPage, AudioGenerationPage, AudioAnalysisPage, AudioTranscriptionPage, MultimodalFusionPage, ContentUnderstandingPage, ImageEnhancementPage, VideoAnalysisPage, TextToSpeechPage } from './pages'
import MainLayout from './layouts/MainLayout'
import './App.scss'

// 个人资料页面组件
const ProfilePage = () => (
  <div style={{ padding: '24px' }}>
    <h1>个人资料</h1>
    <p>个人资料页面功能开发中...</p>
  </div>
)

const App: React.FC = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
          borderRadius: 8,
        }
      }}
    >
      <AntApp>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="chat" element={<ChatPage />} />
            <Route path="settings" element={<SettingsPage />} />
            
            {/* 个人资料路由 */}
            <Route path="profile" element={<ProfilePage />} />
            
            {/* 多模态服务路由 */}
            <Route path="multimodal" element={<MultimodalPage />} />
            <Route path="multimodal/image-generation" element={<ImageGenerationPage />} />
            <Route path="multimodal/image-enhancement" element={<ImageEnhancementPage />} />
            <Route path="multimodal/video-generation" element={<VideoGenerationPage />} />
            <Route path="multimodal/video-analysis" element={<VideoAnalysisPage />} />
            <Route path="multimodal/audio-generation" element={<AudioGenerationPage />} />
            <Route path="multimodal/audio-analysis" element={<AudioAnalysisPage />} />
            <Route path="multimodal/audio-transcription" element={<AudioTranscriptionPage />} />
            <Route path="multimodal/text-to-speech" element={<TextToSpeechPage />} />
            <Route path="multimodal/fusion" element={<MultimodalFusionPage />} />
            <Route path="multimodal/understanding" element={<ContentUnderstandingPage />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </AntApp>
    </ConfigProvider>
  )
}

export default App