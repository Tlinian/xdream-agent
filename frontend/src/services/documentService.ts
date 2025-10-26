import { request } from '../utils/request';
import type { Document, UploadDocumentRequest } from '../interfaces/knowledge';

class DocumentService {
  private baseUrl = '/api/documents';

  // 获取文档列表
  async getDocuments(userId: string): Promise<Document[]> {
    try {
      const response = await request.get(this.baseUrl, {
        params: { userId }
      });
      return response.data;
    } catch (error) {
      console.error('获取文档列表失败:', error);
      throw error;
    }
  }

  // 根据ID获取文档
  async getDocumentById(userId: string, documentId: string): Promise<Document> {
    try {
      const response = await request.get(`${this.baseUrl}/${documentId}`, {
        params: { userId }
      });
      return response.data;
    } catch (error) {
      console.error('获取文档详情失败:', error);
      throw error;
    }
  }

  // 上传文档
  async uploadDocument(userId: string, data: UploadDocumentRequest): Promise<Document> {
    try {
      const response = await request.post(this.baseUrl, {
        ...data,
        userId
      });
      return response.data;
    } catch (error) {
      console.error('上传文档失败:', error);
      throw error;
    }
  }

  // 删除文档
  async deleteDocument(userId: string, documentId: string): Promise<void> {
    try {
      await request.delete(`${this.baseUrl}/${documentId}`, {
        params: { userId }
      });
    } catch (error) {
      console.error('删除文档失败:', error);
      throw error;
    }
  }

  // 更新文档
  async updateDocument(userId: string, documentId: string, data: Partial<Document>): Promise<Document> {
    try {
      const response = await request.put(`${this.baseUrl}/${documentId}`, {
        ...data,
        userId
      });
      return response.data;
    } catch (error) {
      console.error('更新文档失败:', error);
      throw error;
    }
  }
}

export const documentService = new DocumentService();