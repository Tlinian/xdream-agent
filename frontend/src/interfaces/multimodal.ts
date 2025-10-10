/**
 * 多模态服务相关接口定义
 */

// 图像生成相关接口
export interface ImageGenerationRequest {
  prompt: string
  negativePrompt?: string
  width?: number
  height?: number
  numImages?: number
  steps?: number
  guidanceScale?: number
  seed?: number
  model?: string
  sampler?: string
  outputFormat?: 'png' | 'jpg' | 'webp'
  quality?: number
  parameters?: {
    enableHighRes?: boolean
    highResScale?: number
    highResSteps?: number
    enableFaceRestore?: boolean
    enableColorCorrection?: boolean
  }
  controlNet?: {
    enabled?: boolean
    module?: string
    model?: string
    weight?: number
    guidanceStart?: number
    guidanceEnd?: number
    image?: string
  }
  lora?: {
    enabled?: boolean
    model?: string
    weight?: number
  }
}

export interface ImageGenerationResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  images?: string[]
  error?: string
  progress?: number
  generationTime?: number
  parameters?: ImageGenerationRequest
  createdAt: string
  updatedAt: string
}

export interface ImageGenerationTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  prompt: string
  negativePrompt?: string
  parameters: ImageGenerationRequest
  result?: {
    images: string[]
    generationTime: number
    seed: number
  }
  error?: string
  progress: number
  priority: number
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 视频生成相关接口
export interface VideoGenerationRequest {
  prompt: string
  negativePrompt?: string
  duration?: number
  fps?: number
  width?: number
  height?: number
  model?: string
  parameters?: {
    motionIntensity?: number
    cameraMovement?: string
    style?: string
    aspectRatio?: string
  }
  referenceImage?: string
  audio?: string
}

export interface VideoGenerationResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  video?: string
  preview?: string
  error?: string
  progress?: number
  generationTime?: number
  parameters?: VideoGenerationRequest
  createdAt: string
  updatedAt: string
}

export interface VideoGenerationTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  prompt: string
  negativePrompt?: string
  parameters: VideoGenerationRequest
  result?: {
    video: string
    preview: string
    generationTime: number
  }
  error?: string
  progress: number
  priority: number
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 音频生成相关接口
export interface AudioGenerationRequest {
  prompt: string
  duration?: number
  model?: string
  parameters?: {
    genre?: string
    mood?: string
    tempo?: number
    key?: string
    timeSignature?: string
    instruments?: string[]
  }
  referenceAudio?: string
}

export interface AudioGenerationResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  audio?: string
  error?: string
  progress?: number
  generationTime?: number
  parameters?: AudioGenerationRequest
  createdAt: string
  updatedAt: string
}

export interface AudioGenerationTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  prompt: string
  parameters: AudioGenerationRequest
  result?: {
    audio: string
    generationTime: number
  }
  error?: string
  progress: number
  priority: number
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 图像增强相关接口
export interface ImageEnhancementRequest {
  imageUrl: string
  enhancementType: 'super_resolution' | 'denoise' | 'enhance_color' | 'sharpen' | 'restore' | 'style_enhance' | 'auto_enhance'
  model?: string
  parameters?: {
    scale?: number // 放大倍数 (超分辨率)
    strength?: number // 增强强度 0-1
    preserveDetails?: boolean // 保持细节
    reduceNoise?: boolean // 降噪
    enhanceContrast?: boolean // 增强对比度
    adjustSaturation?: number // 饱和度调整 -100 到 100
    sharpenAmount?: number // 锐化程度
    denoiseLevel?: number // 降噪级别
    faceEnhance?: boolean // 人脸增强
    colorCorrection?: boolean // 色彩校正
    exposureCorrection?: number // 曝光校正 -100 到 100
    highlightRecovery?: number // 高光恢复 0-100
    shadowRecovery?: number // 阴影恢复 0-100
  }
  outputFormat?: 'png' | 'jpg' | 'webp'
  outputQuality?: number // 输出质量 1-100
  enablePreview?: boolean // 启用预览
  previewScale?: number // 预览缩放比例
}

export interface ImageEnhancementResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  enhancedImage?: string
  previewImage?: string
  originalImage?: string
  error?: string
  progress?: number
  enhancementTime?: number
  parameters?: ImageEnhancementRequest
  enhancementMetrics?: {
    qualityScore: number // 质量评分 0-100
    sharpnessImprovement: number // 锐度改善程度
    noiseReduction: number // 降噪程度
    colorEnhancement: number // 色彩增强程度
    similarity: number // 与原图相似度 0-1
  }
  createdAt: string
  updatedAt: string
}

export interface ImageEnhancementTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  imageUrl: string
  enhancementType: string
  parameters: ImageEnhancementRequest
  result?: {
    enhancedImage: string
    previewImage?: string
    enhancementTime: number
    enhancementMetrics: ImageEnhancementResponse['enhancementMetrics']
  }
  error?: string
  progress: number
  priority: number
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 多模态融合相关接口
export interface MultimodalFusionRequest {
  text?: string
  image?: string
  audio?: string
  video?: string
  fusionType: 'text-image' | 'text-audio' | 'image-audio' | 'text-image-audio' | 'all'
  outputFormat?: 'text' | 'image' | 'audio' | 'video'
  parameters?: {
    textWeight?: number
    imageWeight?: number
    audioWeight?: number
    videoWeight?: number
    style?: string
    quality?: number
  }
}

export interface MultimodalFusionResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  result?: {
    text?: string
    image?: string
    audio?: string
    video?: string
  }
  error?: string
  progress?: number
  fusionTime?: number
  parameters?: MultimodalFusionRequest
  createdAt: string
  updatedAt: string
}

export interface MultimodalFusionTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  fusionType: string
  inputs: {
    text?: string
    image?: string
    audio?: string
    video?: string
  }
  parameters: MultimodalFusionRequest
  result?: {
    text?: string
    image?: string
    audio?: string
    video?: string
  }
  error?: string
  progress: number
  priority: number
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 内容理解相关接口
export interface ContentUnderstandingRequest {
  content: string
  contentType: 'image' | 'audio' | 'video' | 'text'
  analysisType: 'caption' | 'classification' | 'sentiment' | 'objects' | 'faces' | 'all'
  parameters?: {
    language?: string
    detailLevel?: 'low' | 'medium' | 'high'
    maxTokens?: number
    temperature?: number
  }
}

export interface ContentUnderstandingResponse {
  taskId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  result?: {
    caption?: string
    classification?: Array<{
      label: string
      confidence: number
    }>
    sentiment?: {
      label: string
      confidence: number
    }
    objects?: Array<{
      name: string
      confidence: number
      bbox: [number, number, number, number]
    }>
    faces?: Array<{
      confidence: number
      bbox: [number, number, number, number]
      landmarks?: Array<[number, number]>
    }>
    text?: string
  }
  error?: string
  progress?: number
  processingTime?: number
  parameters?: ContentUnderstandingRequest
  createdAt: string
  updatedAt: string
}

export interface ContentUnderstandingTask {
  id: string
  userId: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  type: string
  contentType: string
  analysisType: string
  content: string
  parameters: ContentUnderstandingRequest
  result?: {
    caption?: string
    classification?: Array<{
      label: string
      confidence: number
    }>
    sentiment?: {
      label: string
      confidence: number
    }
    objects?: Array<{
      name: string
      confidence: number
      bbox: [number, number, number, number]
    }>
    faces?: Array<{
      confidence: number
      bbox: [number, number, number, number]
      landmarks?: Array<[number, number]>
    }>
    text?: string
  }
  error?: string
  progress: number
  priority: number
  metadata?: {
    filename?: string
    size?: number
    type?: string
  }
  createdAt: string
  startedAt?: string
  completedAt?: string
}

// 模型信息接口
export interface ModelInfo {
  id: string
  name: string
  description: string
  type: 'image-generation' | 'video-generation' | 'audio-generation' | 'multimodal-fusion' | 'content-understanding'
  version: string
  tags: string[]
  capabilities: string[]
  maxInputSize?: number
  supportedFormats?: string[]
  isActive: boolean
  isFree: boolean
  pricing?: {
    perRequest?: number
    perToken?: number
    perSecond?: number
  }
  performance?: {
    averageResponseTime: number
    successRate: number
    maxConcurrentRequests: number
  }
  createdAt: string
  updatedAt: string
}

// 多模态服务统计接口
export interface MultimodalStats {
  totalRequests: number
  todayRequests: number
  totalImagesGenerated: number
  totalVideosGenerated: number
  totalAudioGenerated: number
  averageProcessingTime: number
  successRate: number
  popularModels: Array<{
    model: string
    count: number
    percentage: number
  }>
  usageByType: Array<{
    type: string
    count: number
    percentage: number
  }>
  recentActivity: Array<{
    timestamp: string
    type: string
    status: string
    processingTime: number
  }>
}