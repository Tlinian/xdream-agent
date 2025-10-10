import { MultimodalFusionRequest, MultimodalFusionResponse, MultimodalFusionTask } from '@interfaces/multimodal'
import request from '@utils/request'

/**
 * 多模态融合服务类
 */
class MultimodalFusionService {
  /**
   * 创建融合任务
   * @param fusionRequest 融合请求参数
   * @returns 融合响应
   */
  async createFusion(fusionRequest: MultimodalFusionRequest): Promise<MultimodalFusionResponse> {
    try {
      const response = await request.post('/api/v1/multimodal/fusion/create', fusionRequest)
      return response.data
    } catch (error) {
      console.error('创建融合任务失败:', error)
      throw error
    }
  }

  /**
   * 获取任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<MultimodalFusionTask> {
    try {
      const response = await request.get(`/api/v1/multimodal/fusion/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('获取任务状态失败:', error)
      throw error
    }
  }

  /**
   * 获取融合历史
   * @param page 页码
   * @param pageSize 每页数量
   * @returns 融合历史列表
   */
  async getFusionHistory(page: number = 1, pageSize: number = 20): Promise<{
    items: MultimodalFusionTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/fusion/history', {
        params: { page, pageSize }
      })
      return response.data
    } catch (error) {
      console.error('获取融合历史失败:', error)
      throw error
    }
  }

  /**
   * 获取可用的模板列表
   * @returns 模板列表
   */
  async getAvailableTemplates(): Promise<{
    templates: any[]
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/fusion/templates')
      return response.data
    } catch (error) {
      console.error('获取模板列表失败:', error)
      throw error
    }
  }

  /**
   * 上传文件
   * @param file 文件
   * @param type 文件类型
   * @returns 上传结果
   */
  async uploadFile(file: File, type: 'image' | 'video' | 'audio'): Promise<{
    url: string
    filename: string
    size: number
    type: string
    duration?: number
    width?: number
    height?: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('type', type)
      
      const response = await request.post('/api/v1/multimodal/fusion/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('上传文件失败:', error)
      throw error
    }
  }

  /**
   * 删除融合任务
   * @param taskId 任务ID
   * @returns 删除结果
   */
  async deleteTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.delete(`/api/v1/multimodal/fusion/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('删除任务失败:', error)
      throw error
    }
  }

  /**
   * 取消融合任务
   * @param taskId 任务ID
   * @returns 取消结果
   */
  async cancelTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.post(`/api/v1/multimodal/fusion/tasks/${taskId}/cancel`)
      return response.data
    } catch (error) {
      console.error('取消任务失败:', error)
      throw error
    }
  }

  /**
   * 下载结果
   * @param taskId 任务ID
   * @returns 下载链接
   */
  async downloadResult(taskId: string): Promise<void> {
    try {
      const response = await request.get(`/api/v1/multimodal/fusion/tasks/${taskId}/download`, {
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `fusion-result-${taskId}`)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    } catch (error) {
      console.error('下载结果失败:', error)
      throw error
    }
  }

  /**
   * 获取融合统计信息
   * @returns 统计信息
   */
  async getFusionStats(): Promise<{
    totalFusions: number
    todayFusions: number
    averageFusionTime: number
    successRate: number
    popularTemplates: Array<{
      template: string
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
  }> {
    try {
      const response = await request.get('/api/v1/multimodal/fusion/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量融合
   * @param requests 批量融合请求
   * @returns 批量融合任务ID
   */
  async batchFusion(requests: MultimodalFusionRequest[]): Promise<{
    batchId: string
    taskIds: string[]
    totalCount: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/fusion/batch', {
        requests
      })
      return response.data
    } catch (error) {
      console.error('批量融合失败:', error)
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
    tasks: MultimodalFusionTask[]
  }> {
    try {
      const response = await request.get(`/api/v1/multimodal/fusion/batch/${batchId}`)
      return response.data
    } catch (error) {
      console.error('获取批量任务状态失败:', error)
      throw error
    }
  }

  /**
   * 保存自定义模板
   * @param template 模板信息
   * @returns 保存结果
   */
  async saveCustomTemplate(template: Omit<any, 'id'>): Promise<{
    template: any
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/fusion/templates', template)
      return response.data
    } catch (error) {
      console.error('保存模板失败:', error)
      throw error
    }
  }

  /**
   * 删除自定义模板
   * @param templateId 模板ID
   * @returns 删除结果
   */
  async deleteCustomTemplate(templateId: string): Promise<{ success: boolean }> {
    try {
      const response = await request.delete(`/api/v1/multimodal/fusion/templates/${templateId}`)
      return response.data
    } catch (error) {
      console.error('删除模板失败:', error)
      throw error
    }
  }

  /**
   * 获取融合预览
   * @param fusionRequest 融合请求参数
   * @returns 预览结果
   */
  async getFusionPreview(fusionRequest: MultimodalFusionRequest): Promise<{
    preview: string
    confidence: number
    suggestions: string[]
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/fusion/preview', fusionRequest)
      return response.data
    } catch (error) {
      console.error('获取预览失败:', error)
      throw error
    }
  }

  /**
   * 优化融合参数
   * @param fusionRequest 融合请求参数
   * @returns 优化建议
   */
  async optimizeParameters(fusionRequest: MultimodalFusionRequest): Promise<{
    optimizedRequest: MultimodalFusionRequest
    improvements: string[]
    expectedQuality: number
  }> {
    try {
      const response = await request.post('/api/v1/multimodal/fusion/optimize', fusionRequest)
      return response.data
    } catch (error) {
      console.error('优化参数失败:', error)
      throw error
    }
  }
}

export const multimodalFusionService = new MultimodalFusionService()