package com.xdream.knowledge.controller;

import com.xdream.common.dto.ApiResponse;
import com.xdream.common.dto.PageResponse;
import com.xdream.knowledge.dto.*;
import com.xdream.knowledge.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "知识库管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class KnowledgeController {
    
    private final KnowledgeService knowledgeService;
    
    @PostMapping("/documents")
    @Operation(summary = "上传文档", description = "上传新的知识文档")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UploadDocumentRequest request) {
        DocumentResponse document = knowledgeService.uploadDocument(userId, request);
        return ResponseEntity.ok(ApiResponse.success(document));
    }
    
    @GetMapping("/documents")
    @Operation(summary = "获取文档列表", description = "获取知识库文档列表")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getDocuments(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<DocumentResponse> documents = knowledgeService.getDocuments(userId, category, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }
    
    @GetMapping("/documents/{documentId}")
    @Operation(summary = "获取文档详情", description = "获取指定文档的详细信息")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文档ID") @PathVariable String documentId) {
        DocumentResponse document = knowledgeService.getDocument(userId, documentId);
        return ResponseEntity.ok(ApiResponse.success(document));
    }
    
    @PutMapping("/documents/{documentId}")
    @Operation(summary = "更新文档", description = "更新文档信息")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文档ID") @PathVariable String documentId,
            @Valid @RequestBody UpdateDocumentRequest request) {
        DocumentResponse document = knowledgeService.updateDocument(userId, documentId, request);
        return ResponseEntity.ok(ApiResponse.success(document));
    }
    
    @DeleteMapping("/documents/{documentId}")
    @Operation(summary = "删除文档", description = "删除指定文档")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文档ID") @PathVariable String documentId) {
        knowledgeService.deleteDocument(userId, documentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/documents/{documentId}/process")
    @Operation(summary = "处理文档", description = "对文档进行文本提取和向量化处理")
    public ResponseEntity<ApiResponse<ProcessResponse>> processDocument(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文档ID") @PathVariable String documentId) {
        ProcessResponse response = knowledgeService.processDocument(userId, documentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/search")
    @Operation(summary = "搜索知识", description = "基于语义搜索知识库内容")
    public ResponseEntity<ApiResponse<SearchResponse>> searchKnowledge(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SearchRequest request) {
        SearchResponse response = knowledgeService.searchKnowledge(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/categories")
    @Operation(summary = "获取分类列表", description = "获取知识库文档分类")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(
            @RequestHeader("X-User-Id") String userId) {
        CategoryListResponse categories = knowledgeService.getCategories(userId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @PostMapping("/categories")
    @Operation(summary = "创建分类", description = "创建新的文档分类")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = knowledgeService.createCategory(userId, request);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取统计信息", description = "获取知识库统计信息")
    public ResponseEntity<ApiResponse<KnowledgeStatsResponse>> getStats(
            @RequestHeader("X-User-Id") String userId) {
        KnowledgeStatsResponse stats = knowledgeService.getStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}