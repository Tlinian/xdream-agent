import { ContentUnderstandingRequest, ContentUnderstandingResponse, ContentUnderstandingTask } from '@interfaces/multimodal'
import { multimodalRequest } from '@utils/request'

/**
 * 内容理解服务类
 */
class ContentUnderstandingService {
  /**
   * 分析内容
   * @param contentRequest 内容理解请求参数
   * @returns 内容理解响应
   */
  async analyzeContent(contentRequest: ContentUnderstandingRequest): Promise<ContentUnderstandingResponse> {
    try {
      const response = await multimodalRequest.post('/api/v1/multimodal/understanding/analyze', contentRequest)
      return response.data
    } catch (error) {
      console.error('分析内容失败:', error)
      throw error
    }
  }

  /**
   * 获取任务状态
   * @param taskId 任务ID
   * @returns 任务状态
   */
  async getTaskStatus(taskId: string): Promise<ContentUnderstandingTask> {
    try {
      const response = await multimodalRequest.get(`/api/v1/multimodal/understanding/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('获取任务状态失败:', error)
      throw error
    }
  }

  /**
   * 获取分析历史
   * @param page 页码
   * @param pageSize 每页数量
   * @returns 分析历史列表
   */
  async getAnalysisHistory(page: number = 1, pageSize: number = 20): Promise<{
    items: ContentUnderstandingTask[]
    total: number
    page: number
    pageSize: number
  }> {
    try {
      const response = await multimodalRequest.get(`/api/v1/multimodal/understanding/history?page=${page}&pageSize=${pageSize}`)
      return response.data
    } catch (error) {
      console.error('获取分析历史失败:', error)
      throw error
    }
  }

  /**
   * 上传文件
   * @param file 文件
   * @param type 文件类型
   * @returns 上传结果
   */
  async uploadFile(file: File, type: 'image' | 'video' | 'audio' | 'text'): Promise<{
    url: string
    filename: string
    size: number
    type: string
    duration?: number
    width?: number
    height?: number
    pages?: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('type', type)
      
      const response = await multimodalRequest.post('/api/v1/multimodal/understanding/upload', formData, {
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
   * 删除分析任务
   * @param taskId 任务ID
   * @returns 删除结果
   */
  async deleteTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await multimodalRequest.delete(`/api/v1/multimodal/understanding/tasks/${taskId}`)
      return response.data
    } catch (error) {
      console.error('删除任务失败:', error)
      throw error
    }
  }

  /**
   * 取消分析任务
   * @param taskId 任务ID
   * @returns 取消结果
   */
  async cancelTask(taskId: string): Promise<{ success: boolean }> {
    try {
      const response = await multimodalRequest.post(`/api/v1/multimodal/understanding/tasks/${taskId}/cancel`)
      return response.data
    } catch (error) {
      console.error('取消任务失败:', error)
      throw error
    }
  }

  /**
   * 下载分析结果
   * @param taskId 任务ID
   * @returns 下载链接
   */
  async downloadResult(taskId: string): Promise<void> {
    try {
      const response = await multimodalRequest.get(`/api/v1/multimodal/understanding/tasks/${taskId}/download`, {
        responseType: 'blob'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `analysis-result-${taskId}.json`)
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
   * 获取分析统计信息
   * @returns 统计信息
   */
  async getAnalysisStats(): Promise<{
    totalAnalyses: number
    todayAnalyses: number
    averageAnalysisTime: number
    successRate: number
    popularTypes: Array<{
      type: string
      count: number
      percentage: number
    }>
    usageByLanguage: Array<{
      language: string
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
      const response = await multimodalRequest.get('/api/v1/multimodal/understanding/stats')
      return response.data
    } catch (error) {
      console.error('获取统计信息失败:', error)
      throw error
    }
  }

  /**
   * 批量分析
   * @param requests 批量分析请求
   * @returns 批量分析任务ID
   */
  async batchAnalyze(requests: ContentUnderstandingRequest[]): Promise<{
    batchId: string
    taskIds: string[]
    totalCount: number
  }> {
    try {
      const response = await multimodalRequest.post('/api/v1/multimodal/understanding/batch', {
        requests
      })
      return response.data
    } catch (error) {
      console.error('批量分析失败:', error)
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
    tasks: ContentUnderstandingTask[]
  }> {
    try {
      const response = await multimodalRequest.get(`/api/v1/multimodal/understanding/batch/${batchId}`)
      return response.data
    } catch (error) {
      console.error('获取批量任务状态失败:', error)
      throw error
    }
  }

  /**
   * 获取支持的模型列表
   * @returns 模型列表
   */
  async getSupportedModels(): Promise<{
    models: Array<{
      id: string
      name: string
      type: string
      description: string
      supportedLanguages: string[]
      maxFileSize: number
      maxDuration: number
      isActive: boolean
    }>
  }> {
    try {
      const response = await multimodalRequest.get('/api/v1/multimodal/understanding/models')
      return response.data
    } catch (error) {
      console.error('获取模型列表失败:', error)
      throw error
    }
  }

  /**
   * 实时分析（WebSocket）
   * @param request 实时分析请求
   * @param onProgress 进度回调
   * @returns WebSocket连接
   */
  async analyzeRealtime(
    request: ContentUnderstandingRequest,
    onProgress: (progress: { status: string; progress: number; result?: any }) => void
  ): Promise<WebSocket> {
    return new Promise((resolve, reject) => {
      try {
        const ws = new WebSocket(`ws://localhost:8080/api/v1/multimodal/understanding/realtime`)
        
        ws.onopen = () => {
          ws.send(JSON.stringify(request))
          resolve(ws)
        }
        
        ws.onmessage = (event) => {
          const data = JSON.parse(event.data)
          onProgress(data)
        }
        
        ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          reject(error)
        }
        
        ws.onclose = () => {
          console.log('WebSocket连接关闭')
        }
      } catch (error) {
        console.error('创建WebSocket连接失败:', error)
        reject(error)
      }
    })
  }

  /**
   * 内容预处理
   * @param file 文件
   * @param options 预处理选项
   * @returns 预处理结果
   */
  async preprocessContent(file: File, options: {
    enhance?: boolean
    denoise?: boolean
    normalize?: boolean
    resize?: boolean
    format?: string
    quality?: number
  }): Promise<{
    url: string
    filename: string
    originalSize: number
    processedSize: number
    duration?: number
    width?: number
    height?: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('options', JSON.stringify(options))
      
      const response = await multimodalRequest.post('/api/v1/multimodal/understanding/preprocess', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('内容预处理失败:', error)
      throw error
    }
  }

  /**
   * 获取文件信息
   * @param file 文件
   * @returns 文件信息
   */
  async getFileInfo(file: File): Promise<{
    type: string
    size: number
    duration?: number
    width?: number
    height?: number
    format: string
    bitrate?: number
    sampleRate?: number
    pages?: number
  }> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await multimodalRequest.post('/api/v1/multimodal/understanding/info', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('获取文件信息失败:', error)
      throw error
    }
  }
}

export const contentUnderstandingService = new ContentUnderstandingService()