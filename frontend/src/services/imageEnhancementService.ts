import { ImageEnhancementRequest, ImageEnhancementResponse, ImageEnhancementTask } from '@interfaces/multimodal'
import request from '@utils/request'

/**
 * 图像增强服务类
 */
class ImageEnhancementService {
  /**
   * 增强图像
   * @param enhancementRequest 图像增强请求参数
   * @returns 图像增强响应
   */
  async enhanceImage(enhancementRequest: ImageEnhancementRequest): Promise<ImageEnhancementResponse> {
    try {
      const response = await request.post('/api/v1/multimodal/image-enhancement/enhance', enhancementRequest)
      return response.data
    } catch (error) {
      console.error('图像增强失败:', error)
      throw error
    }
  }

  /**
   * 获取增强任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<ImageEnhancementTask> {
    try {
      const response = await request.get(`/api/v1/multimodal/image-enhancement/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('获取任务状态失败:', error)
      throw error
    }
  }

  /**
   * 获取增强历史
   * @param page 页码
   * @param pageSize 每页数量
   * @returns 增强历史列表
   */
  async getEnhancementHistory(page: number = 1, pageSize: number = 20): Promise<{
    items: ImageEnhancementTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-enhancement/history', {
        params: { page, pageSize }
      })
      return response.data
    } catch (error) {
      console.error('获取增强历史失败:', error)
      throw error
    }
  }

  /**
   * 获取可用的增强模型列表
   * @returns 模型列表
   */
  async getAvailableModels(): Promise<{
    models: Array<{
      id: string
      name: string
      description: string
      type: string
      version: string
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-enhancement/models')
      return response.data
    } catch (error) {
      console.error('获取模型列表失败:', error)
      throw error
    }
  }

  /**
   * 上传图片
   * @param file 图片文件
   * @returns 上传结果
   */
  async uploadImage(file: File): Promise<{
    url: string
    filename: string
    size: number
    type: string
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await request.post('/api/v1/multimodal/image-enhancement/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('上传图片失败:', error)
      throw error
    }
  }

  /**
   * 删除增强任务
   * @param taskId 任务ID
   * @returns 删除结果
   */
  async deleteTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.delete(`/api/v1/multimodal/image-enhancement/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('删除任务失败:', error)
      throw error
    }
  }

  /**
   * 取消增强任务
   * @param taskId 任务ID
   * @returns 取消结果
   */
  async cancelTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.post(`/api/v1/multimodal/image-enhancement/tasks/${taskId}/cancel`)
      return response.data
    } catch (error) {
      console.error('取消任务失败:', error)
      throw error
    }
  }

  /**
   * 获取增强统计信息
   * @returns 统计信息
   */
  async getEnhancementStats(): Promise<{
    totalEnhancements: number
    todayEnhancements: number
    averageEnhancementTime: number
    successRate: number
    popularEnhancementTypes: Array<{
      type: string
      count: number
      percentage: number
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-enhancement/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量增强
   * @param requests 批量增强请求
   * @returns 批量增强响应
   */
  async batchEnhance(requests: ImageEnhancementRequest[]): Promise<{
    taskIds: string[]
    totalTasks: number
    estimatedTime: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/image-enhancement/batch', { requests })
      return response.data
    } catch (error) {
      console.error('批量增强失败:', error)
      throw error
    }
  }

  /**
   * 比较增强效果
   * @param originalImageUrl 原始图片URL
   * @param enhancedImageUrl 增强图片URL
   * @returns 比较结果
   */
  async compareEnhancement(originalImageUrl: string, enhancedImageUrl: string): Promise<{
    similarity: number
    qualityImprovement: number
    enhancementMetrics: {
      sharpness: number
      contrast: number
      saturation: number
      noiseLevel: number
    }
    recommendations: string[]
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/image-enhancement/compare', {
        originalImageUrl,
        enhancedImageUrl
      })
      return response.data
    } catch (error) {
      console.error('比较增强效果失败:', error)
      throw error
    }
  }
}

export const imageEnhancementService = new ImageEnhancementService()