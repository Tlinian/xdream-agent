package com.xdream.knowledge.service;

import com.xdream.common.dto.PageResponse;
import com.xdream.knowledge.dto.*;
import org.springframework.data.domain.Pageable;

public interface KnowledgeService {
    
    DocumentResponse uploadDocument(String userId, UploadDocumentRequest request);
    
    PageResponse<DocumentResponse> getDocuments(String userId, String category, String keyword, Pageable pageable);
    
    DocumentResponse getDocument(String userId, String documentId);
    
    DocumentResponse updateDocument(String userId, String documentId, UpdateDocumentRequest request);
    
    void deleteDocument(String userId, String documentId);
    
    ProcessResponse processDocument(String userId, String documentId);
    
    SearchResponse searchKnowledge(String userId, SearchRequest request);
    
    CategoryListResponse getCategories(String userId);
    
    CategoryResponse createCategory(String userId, CreateCategoryRequest request);
    
    KnowledgeStatsResponse getStats(String userId);
}