// 知识库相关接口类型定义

// 知识库基础类型
export interface KnowledgeBase {
  id: string
  name: string
  description: string
  status: string
  documentCount: number
  segmentCount: number
  lastIndexedAt?: string
  createdAt: string
}

// 知识库列表响应
export interface KnowledgeBaseListResponse {
  data: KnowledgeBase[]
  total: number
}

// 创建知识库请求
export interface CreateKnowledgeBaseRequest {
  name: string
  description?: string
}

// 知识库统计信息
export interface KnowledgeStats {
  documentCount: number
  segmentCount: number
  totalSize: number
  lastIndexedAt?: string
}

// 文档类型
export interface Document {
  documentId: string
  title: string
  content: string
  description?: string
  documentType: string
  category?: string
  tags: string[]
  fileSize: number
  status: string
  processStatus: string
  processMessage?: string
  vectorCount: number
  createdBy: string
  createdAt: string
  updatedAt: string
  processedAt?: string
}

// 上传文档请求
export interface UploadDocumentRequest {
  title: string
  content: string
  description?: string
  documentType: string
  category?: string
  tags?: string | string[]
  autoProcess?: boolean
}

// 更新文档请求
export interface UpdateDocumentRequest {
  title?: string
  content?: string
  description?: string
  category?: string
  tags?: string[]
}

// 文档处理响应
export interface ProcessResponse {
  documentId: string
  status: string
  message: string
  chunkCount: number
  vectorCount: number
  processingTime: number
}

// 搜索请求
export interface SearchRequest {
  knowledgeBaseId: string
  query: string
  category?: string
  similarityThreshold?: number
  topK?: number
  rerankTopK?: number
  useRerank?: boolean
  appendCitations?: boolean
}

// 搜索结果项
export interface SearchResult {
  documentId: string
  knowledgeBaseId: string
  title: string
  content: string
  chunkIndex: number
  category?: string
  tags: string[]
  similarityScore: number
  rerankScore?: number
  matchType: string
  highlight?: string
  citation: string
}

// 搜索响应
export interface SearchResponse {
  results: SearchResult[]
  totalCount: number
  searchTime: number
  query: string
  fromVectorStore: boolean
  rerankApplied: boolean
}

// 分类响应
export interface Category {
  name: string
  count: number
}

// 分类列表响应
export interface CategoryListResponse {
  categories: Category[]
}

// 分页响应
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}