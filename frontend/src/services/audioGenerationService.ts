import { AudioGenerationRequest, AudioGenerationResponse, AudioGenerationTask } from '@interfaces/multimodal'
import request from '@utils/request'

/**
 * 音频生成服务类
 */
class AudioGenerationService {
  /**
   * 生成音频
   * @param audioRequest 音频生成请求参数
   * @returns 音频生成响应
   */
  async generateAudio(audioRequest: AudioGenerationRequest): Promise<AudioGenerationResponse> {
    try {
      const response = await request.post('/api/v1/multimodal/audio-generation/generate', audioRequest)
      return response.data
    } catch (error) {
      console.error('生成音频失败:', error)
      throw error
    }
  }

  /**
   * 获取生成任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<AudioGenerationTask> {
    try {
      const response = await request.get(`/api/v1/multimodal/audio-generation/tasks/${taskId}`)
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
    items: AudioGenerationTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/audio-generation/history', {
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
      supportedFormats: string[]
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/audio-generation/models')
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
  async uploadReferenceFile(file: File, type: 'audio'): Promise<{
    url: string
    filename: string
    size: number
    type: string
    duration: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('type', type)
      
      const response = await request.post('/api/v1/multimodal/audio-generation/upload', formData, {
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
      const response = await request.delete(`/api/v1/multimodal/audio-generation/tasks/${taskId}`)
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
      const response = await request.post(`/api/v1/multimodal/audio-generation/tasks/${taskId}/cancel`)
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
    usageByGenre: Array<{
      genre: string
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
      const response = await request.get('/api/v1/multimodal/audio-generation/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量生成音频
   * @param requests 批量生成请求
   * @returns 批量生成任务ID
   */
  async batchGenerateAudio(requests: AudioGenerationRequest[]): Promise<{
    batchId: string
    taskIds: string[]
    totalCount: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/audio-generation/batch', {
        requests
      })
      return response.data
    } catch (error) {
      console.error('批量生成音频失败:', error)
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
    tasks: AudioGenerationTask[]
  }> {
    try {
      const response = await request.get(`/api/v1/multimodal/audio-generation/batch/${batchId}`)
      return response.data
    } catch (error) {
      console.error('获取批量任务状态失败:', error)
      throw error
    }
  }

  /**
   * 音频预处理
   * @param file 音频文件
   * @param options 预处理选项
   * @returns 预处理结果
   */
  async preprocessAudio(file: File, options: {
    normalize?: boolean
    removeNoise?: boolean
    trimSilence?: boolean
    fadeIn?: number
    fadeOut?: number
    format?: string
    bitrate?: number
  }): Promise<{
    url: string
    filename: string
    originalSize: number
    processedSize: number
    duration: number
    format: string
    bitrate: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('options', JSON.stringify(options))
      
      const response = await request.post('/api/v1/multimodal/audio-generation/preprocess', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('音频预处理失败:', error)
      throw error
    }
  }

  /**
   * 获取音频信息
   * @param file 音频文件
   * @returns 音频信息
   */
  async getAudioInfo(file: File): Promise<{
    duration: number
    format: string
    size: number
    bitrate: number
    sampleRate: number
    channels: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await request.post('/api/v1/multimodal/audio-generation/info', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('获取音频信息失败:', error)
      throw error
    }
  }
}

export const audioGenerationService = new AudioGenerationService()