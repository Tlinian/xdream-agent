import { request } from '../utils/request';
import type { 
  KnowledgeBase, 
  Document, 
  CreateKnowledgeBaseRequest, 
  UploadDocumentRequest,
  UpdateDocumentRequest
} from '../interfaces/knowledge';

class KnowledgeService {
  private baseUrl = '/api/knowledge';

  /** 获取知识库列表 */
  async getKnowledgeBases(userId: string): Promise<KnowledgeBase[]> {
    try {
      const response = await request.get<any>(`${this.baseUrl}/bases`, null, {
        headers: { 'X-User-Id': userId }
      })
      // 后端返回的是PageResponse<KnowledgeBaseResponse>格式，需要从data.items中获取知识库列表
      return ((response.data as any).data.items) as KnowledgeBase[]
    } catch (error) {
      console.error('获取知识库列表失败:', error)
      throw error
    }
  }

  /** 创建知识库 */
  async createKnowledgeBase(userId: string, data: CreateKnowledgeBaseRequest): Promise<KnowledgeBase> {
    try {
      const response = await request.post<KnowledgeBase>(`${this.baseUrl}/bases`, data, {
        headers: { 'X-User-Id': userId }
      })
      return (response.data as any).data
    } catch (error) {
      console.error('创建知识库失败:', error)
      throw error
    }
  }

  /** 获取知识库详情 */
  async getKnowledgeBaseById(userId: string, baseId: string): Promise<KnowledgeBase> {
    try {
      const response = await request.get<KnowledgeBase>(`${this.baseUrl}/bases/${baseId}`, null, {
        headers: { 'X-User-Id': userId }
      })
      return (response.data as any).data
    } catch (error) {
      console.error('获取知识库详情失败:', error)
      throw error
    }
  }

  /** 更新知识库 */
  async updateKnowledgeBase(userId: string, baseId: string, data: Partial<KnowledgeBase>): Promise<KnowledgeBase> {
    try {
      const response = await request.put<KnowledgeBase>(`${this.baseUrl}/bases/${baseId}`, data, {
        headers: { 'X-User-Id': userId }
      })
      return (response.data as any).data
    } catch (error) {
      console.error('更新知识库失败:', error)
      throw error
    }
  }

  /** 删除知识库 */
  async deleteKnowledgeBase(userId: string, baseId: string): Promise<void> {
    try {
      await request.delete(`${this.baseUrl}/bases/${baseId}`, {
        headers: { 'X-User-Id': userId }
      })
    } catch (error) {
      console.error('删除知识库失败:', error)
      throw error
    }
  }

  /** 获取知识库文档列表 */
  async getDocuments(userId: string, baseId: string): Promise<Document[]> {
    try {
      const response = await request.get<any>(`${this.baseUrl}/bases/${baseId}/documents`, null, {
        headers: { 'X-User-Id': userId }
      })
      // 后端返回的是ApiResponse<PageResponse<DocumentResponse>>，列表在 data.content
      return ((response.data as any).data.content) as Document[]
    } catch (error) {
      console.error('获取文档列表失败:', error)
      throw error
    }
  }

  /** 获取文档详情 */
  async getDocument(userId: string, baseId: string, documentId: string): Promise<Document> {
    try {
      const response = await request.get<Document>(`${this.baseUrl}/bases/${baseId}/documents/${documentId}`, null, {
        headers: { 'X-User-Id': userId }
      })
      return (response.data as any).data
    } catch (error) {
      console.error('获取文档详情失败:', error)
      throw error
    }
  }

  /** 上传文档到知识库 */
  async uploadDocument(userId: string, baseId: string, data: UploadDocumentRequest): Promise<Document> {
    try {
      // 后端在 @Valid 验证前会校验 knowledgeBaseId，因此需要在请求体中携带
      const payload: any = { ...data, knowledgeBaseId: baseId }
      const response = await request.post<any>(`${this.baseUrl}/bases/${baseId}/documents`, payload, {
        headers: { 'X-User-Id': userId }
      })
      // 后端返回的是ApiResponse<DocumentResponse>格式，需要从data中获取文档对象
      return ((response.data as any).data) as Document
    } catch (error) {
      console.error('上传文档失败:', error)
      throw error
    }
  }

  /** 更新文档 */
  async updateDocument(userId: string, baseId: string, documentId: string, data: UpdateDocumentRequest): Promise<Document> {
    try {
      const response = await request.put<Document>(`${this.baseUrl}/bases/${baseId}/documents/${documentId}`, data, {
        headers: { 'X-User-Id': userId }
      })
      return (response.data as any).data
    } catch (error) {
      console.error('更新文档失败:', error)
      throw error
    }
  }

  /** 删除文档 */
  async deleteDocument(userId: string, baseId: string, documentId: string): Promise<void> {
    try {
      await request.delete(`${this.baseUrl}/bases/${baseId}/documents/${documentId}`, {
        headers: { 'X-User-Id': userId }
      })
    } catch (error) {
      console.error('删除文档失败:', error)
      throw error
    }
  }

  /** 获取知识库统计信息 */
  async getKnowledgeStats(userId: string): Promise<any> {
    try {
      const response = await request.get<any>(`${this.baseUrl}/stats`, null, {
        headers: { 'X-User-Id': userId }
      })
      // 后端返回的是ApiResponse<KnowledgeStatsResponse>格式，需要从data中获取统计信息
      return ((response.data as any).data) as any
    } catch (error) {
      console.error('获取知识库统计信息失败:', error)
      throw error
    }
  }
}

export const knowledgeService = new KnowledgeService()