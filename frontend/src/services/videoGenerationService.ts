import { VideoGenerationRequest, VideoGenerationResponse, VideoGenerationTask } from '@interfaces/multimodal'
import request from '@utils/request'

/**
 * 视频生成服务类
 */
class VideoGenerationService {
  /**
   * 生成视频
   * @param videoRequest 视频生成请求参数
   * @returns 视频生成响应
   */
  async generateVideo(videoRequest: VideoGenerationRequest): Promise<VideoGenerationResponse> {
    try {
      const response = await request.post('/api/v1/multimodal/video-generation/generate', videoRequest)
      return response.data
    } catch (error) {
      console.error('生成视频失败:', error)
      throw error
    }
  }

  /**
   * 获取生成任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<VideoGenerationTask> {
    try {
      const response = await request.get(`/api/v1/multimodal/video-generation/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('获取任务状态失败:', error)
      throw error
    }
  }

  /**
   * 获取生成历史
   * @param page 页码
   * @param pageSize 每页数量
   * @returns 生成历史列表
   */
  async getGenerationHistory(page: number = 1, pageSize: number = 20): Promise<{
    items: VideoGenerationTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/video-generation/history', {
        params: { page, pageSize }
      })
      return response.data
    } catch (error) {
      console.error('获取生成历史失败:', error)
      throw error
    }
  }

  /**
   * 获取可用的模型列表
   * @returns 模型列表
   */
  async getAvailableModels(): Promise<{
    models: Array<{
      id: string
      name: string
      description: string
      type: string
      version: string
      maxDuration: number
      maxResolution: string
      supportedFormats: string[]
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/video-generation/models')
      return response.data
    } catch (error) {
      console.error('获取模型列表失败:', error)
      throw error
    }
  }

  /**
   * 上传参考文件
   * @param file 参考文件
   * @param type 文件类型
   * @returns 上传结果
   */
  async uploadReferenceFile(file: File, type: 'image' | 'audio' | 'video'): Promise<{
    url: string
    filename: string
    size: number
    type: string
    duration?: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('type', type)
      
      const response = await request.post('/api/v1/multimodal/video-generation/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('上传参考文件失败:', error)
      throw error
    }
  }

  /**
   * 删除生成任务
   * @param taskId 任务ID
   * @returns 删除结果
   */
  async deleteTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.delete(`/api/v1/multimodal/video-generation/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('删除任务失败:', error)
      throw error
    }
  }

  /**
   * 取消生成任务
   * @param taskId 任务ID
   * @returns 取消结果
   */
  async cancelTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.post(`/api/v1/multimodal/video-generation/tasks/${taskId}/cancel`)
      return response.data
    } catch (error) {
      console.error('取消任务失败:', error)
      throw error
    }
  }

  /**
   * 获取生成统计信息
   * @returns 统计信息
   */
  async getGenerationStats(): Promise<{
    totalGenerations: number
    todayGenerations: number
    averageGenerationTime: number
    successRate: number
    popularModels: Array<{
      model: string
      count: number
      percentage: number
    }>
    usageByDuration: Array<{
      duration: string
      count: number
      percentage: number
    }>
    recentActivity: Array<{
      timestamp: string
      type: string
      status: string
      processingTime: number
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/video-generation/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量生成视频
   * @param requests 批量生成请求
   * @returns 批量生成任务ID
   */
  async batchGenerateVideos(requests: VideoGenerationRequest[]): Promise<{
    batchId: string
    taskIds: string[]
    totalCount: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/video-generation/batch', {
        requests
      })
      return response.data
    } catch (error) {
      console.error('批量生成视频失败:', error)
      throw error
    }
  }

  /**
   * 获取批量任务状态
   * @param batchId 批量任务ID
   * @returns 批量任务状态
   */
  async getBatchTaskStatus(batchId: string): Promise<{
    batchId: string
    status: string
    progress: number
    completedCount: number
    totalCount: number
    tasks: VideoGenerationTask[]
  }> {
    try {
      const response = await request.get(`/api/v1/multimodal/video-generation/batch/${batchId}`)
      return response.data
    } catch (error) {
      console.error('获取批量任务状态失败:', error)
      throw error
    }
  }

  /**
   * 视频预处理
   * @param file 视频文件
   * @param options 预处理选项
   * @returns 预处理结果
   */
  async preprocessVideo(file: File, options: {
    resize?: boolean
    width?: number
    height?: number
    fps?: number
    duration?: number
    format?: string
  }): Promise<{
    url: string
    filename: string
    originalSize: number
    processedSize: number
    duration: number
    fps: number
    resolution: string
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('options', JSON.stringify(options))
      
      const response = await request.post('/api/v1/multimodal/video-generation/preprocess', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('视频预处理失败:', error)
      throw error
    }
  }

  /**
   * 获取视频信息
   * @param file 视频文件
   * @returns 视频信息
   */
  async getVideoInfo(file: File): Promise<{
    duration: number
    fps: number
    width: number
    height: number
    format: string
    size: number
    bitrate: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await request.post('/api/v1/multimodal/video-generation/info', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('获取视频信息失败:', error)
      throw error
    }
  }
}

export const videoGenerationService = new VideoGenerationService()