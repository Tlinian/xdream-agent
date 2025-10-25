package com.xdream.knowledge.service;

import com.xdream.common.dto.PageResponse;
import com.xdream.knowledge.dto.CreateKnowledgeBaseRequest;
import com.xdream.knowledge.dto.DocumentResponse;
import com.xdream.knowledge.dto.KnowledgeBaseListResponse;
import com.xdream.knowledge.dto.KnowledgeBaseResponse;
import com.xdream.knowledge.dto.KnowledgeStatsResponse;
import com.xdream.knowledge.dto.ProcessResponse;
import com.xdream.knowledge.dto.SearchRequest;
import com.xdream.knowledge.dto.SearchResponse;
import com.xdream.knowledge.dto.UpdateDocumentRequest;
import com.xdream.knowledge.dto.UploadDocumentRequest;
import org.springframework.data.domain.Pageable;

public interface KnowledgeService {

    KnowledgeBaseResponse createKnowledgeBase(String userId, CreateKnowledgeBaseRequest request);

    KnowledgeBaseListResponse listKnowledgeBases(String userId);

    DocumentResponse uploadDocument(String userId, UploadDocumentRequest request);

    PageResponse<DocumentResponse> getDocuments(String userId, String knowledgeBaseId, String category, String keyword, Pageable pageable);

    DocumentResponse getDocument(String userId, String knowledgeBaseId, String documentId);

    DocumentResponse updateDocument(String userId, String knowledgeBaseId, String documentId, UpdateDocumentRequest request);

    void deleteDocument(String userId, String knowledgeBaseId, String documentId);

    ProcessResponse processDocument(String userId, String knowledgeBaseId, String documentId);

    SearchResponse searchKnowledge(String userId, SearchRequest request);

    KnowledgeStatsResponse getStats(String userId, String knowledgeBaseId);
}

