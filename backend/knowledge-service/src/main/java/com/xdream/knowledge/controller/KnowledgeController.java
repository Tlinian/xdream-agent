package com.xdream.knowledge.controller;

import com.xdream.common.dto.ApiResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "参考 Dify 的 RAG 能力，实现知识库维护与检索接口")
@SecurityRequirement(name = "Bearer Authentication")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping("/bases")
    @Operation(summary = "创建知识库", description = "新增一个知识库容器")
    public ResponseEntity<ApiResponse<KnowledgeBaseResponse>> createKnowledgeBase(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateKnowledgeBaseRequest request) {
        KnowledgeBaseResponse response = knowledgeService.createKnowledgeBase(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/bases")
    @Operation(summary = "知识库列表", description = "查询当前用户的全部知识库")
    public ResponseEntity<ApiResponse<KnowledgeBaseListResponse>> listKnowledgeBases(
            @RequestHeader("X-User-Id") String userId) {
        KnowledgeBaseListResponse response = knowledgeService.listKnowledgeBases(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bases/{baseId}/documents")
    @Operation(summary = "上传文档", description = "为指定知识库新增文档并可选择自动处理")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @Valid @RequestBody UploadDocumentRequest request) {
        request.setKnowledgeBaseId(baseId);
        DocumentResponse document = knowledgeService.uploadDocument(userId, request);
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    @GetMapping("/bases/{baseId}/documents")
    @Operation(summary = "文档列表", description = "分页查询知识库中的文档")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getDocuments(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<DocumentResponse> documents = knowledgeService.getDocuments(userId, baseId, category, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/bases/{baseId}/documents/{documentId}")
    @Operation(summary = "文档详情", description = "获取单条文档的完整信息")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @PathVariable("documentId") String documentId) {
        DocumentResponse document = knowledgeService.getDocument(userId, baseId, documentId);
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    @PutMapping("/bases/{baseId}/documents/{documentId}")
    @Operation(summary = "更新文档", description = "修改文档的元数据或正文")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @PathVariable("documentId") String documentId,
            @Valid @RequestBody UpdateDocumentRequest request) {
        DocumentResponse document = knowledgeService.updateDocument(userId, baseId, documentId, request);
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    @DeleteMapping("/bases/{baseId}/documents/{documentId}")
    @Operation(summary = "删除文档", description = "移除文档及其向量切片")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @PathVariable("documentId") String documentId) {
        knowledgeService.deleteDocument(userId, baseId, documentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/bases/{baseId}/documents/{documentId}/process")
    @Operation(summary = "处理文档", description = "执行文本切分与向量化，生成可检索的切片")
    public ResponseEntity<ApiResponse<ProcessResponse>> processDocument(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @PathVariable("documentId") String documentId) {
        ProcessResponse response = knowledgeService.processDocument(userId, baseId, documentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bases/{baseId}/search")
    @Operation(summary = "知识检索", description = "根据查询语句执行语义召回与重排序")
    public ResponseEntity<ApiResponse<SearchResponse>> searchKnowledge(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId,
            @Valid @RequestBody SearchRequest request) {
        request.setKnowledgeBaseId(baseId);
        SearchResponse response = knowledgeService.searchKnowledge(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/bases/{baseId}/stats")
    @Operation(summary = "知识库统计", description = "查看文档数量、向量数量等核心指标")
    public ResponseEntity<ApiResponse<KnowledgeStatsResponse>> getStats(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("baseId") String baseId) {
        KnowledgeStatsResponse stats = knowledgeService.getStats(userId, baseId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

