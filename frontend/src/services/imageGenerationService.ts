import { ImageGenerationRequest, ImageGenerationResponse, ImageGenerationTask } from '@interfaces/multimodal'
import request from '@utils/request'

/**
 * 图像生成服务类
 */
class ImageGenerationService {
  /**
   * 生成图像
   * @param imageRequest 图像生成请求参数
   * @returns 图像生成响应
   */
  async generateImage(imageRequest: ImageGenerationRequest): Promise<ImageGenerationResponse> {
    try {
      const response = await request.post('/api/v1/multimodal/image-generation/generate', imageRequest)
      return response.data
    } catch (error) {
      console.error('生成图像失败:', error)
      throw error
    }
  }

  /**
   * 获取生成任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<ImageGenerationTask> {
    try {
      const response = await request.get(`/api/v1/multimodal/image-generation/tasks/${taskId}`)
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
    items: ImageGenerationTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-generation/history', {
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
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-generation/models')
      return response.data
    } catch (error) {
      console.error('获取模型列表失败:', error)
      throw error
    }
  }

  /**
   * 获取可用的LoRA模型列表
   * @returns LoRA模型列表
   */
  async getAvailableLoRAModels(): Promise<{
    models: Array<{
      id: string
      name: string
      description: string
      baseModel: string
      version: string
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-generation/lora-models')
      return response.data
    } catch (error) {
      console.error('获取LoRA模型列表失败:', error)
      throw error
    }
  }

  /**
   * 获取可用的ControlNet模型列表
   * @returns ControlNet模型列表
   */
  async getAvailableControlNetModels(): Promise<{
    models: Array<{
      id: string
      name: string
      description: string
      module: string
      model: string
      version: string
      tags: string[]
      isActive: boolean
    }>
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-generation/controlnet-models')
      return response.data
    } catch (error) {
      console.error('获取ControlNet模型列表失败:', error)
      throw error
    }
  }

  /**
   * 上传参考图像
   * @param file 图像文件
   * @returns 上传结果
   */
  async uploadReferenceImage(file: File): Promise<{
    url: string
    filename: string
    size: number
    type: string
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await request.post('/api/v1/multimodal/image-generation/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('上传参考图像失败:', error)
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
      const response = await request.delete(`/api/v1/multimodal/image-generation/tasks/${taskId}`)
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
      const response = await request.post(`/api/v1/multimodal/image-generation/tasks/${taskId}/cancel`)
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
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/image-generation/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量生成图像
   * @param requests 批量生成请求
   * @returns 批量生成任务ID
   */
  async batchGenerateImages(requests: ImageGenerationRequest[]): Promise<{
    batchId: string
    taskIds: string[]
    totalCount: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/image-generation/batch', {
        requests
      })
      return response.data
    } catch (error) {
      console.error('批量生成图像失败:', error)
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
    tasks: ImageGenerationTask[]
  }> {
    try {
      const response = await request.get(`/api/v1/multimodal/image-generation/batch/${batchId}`)
      return response.data
    } catch (error) {
      console.error('获取批量任务状态失败:', error)
      throw error
    }
  }
}

export const imageGenerationService = new ImageGenerationService()