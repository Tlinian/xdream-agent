package com.xdream.knowledge.service.impl;

import com.xdream.common.dto.ApiResponse;
import com.xdream.common.dto.PageResponse;
import com.xdream.common.exception.BusinessException;
import com.xdream.common.exception.ErrorCode;
import com.xdream.common.utils.UuidUtils;
import com.xdream.knowledge.client.LlmServiceClient;
import com.xdream.knowledge.config.KnowledgeProperties;
import com.xdream.knowledge.dto.CreateKnowledgeBaseRequest;
import com.xdream.knowledge.dto.DocumentResponse;
import com.xdream.knowledge.dto.EmbeddingRequestDto;
import com.xdream.knowledge.dto.EmbeddingResponseDto;
import com.xdream.knowledge.dto.KnowledgeBaseListResponse;
import com.xdream.knowledge.dto.KnowledgeBaseResponse;
import com.xdream.knowledge.dto.KnowledgeStatsResponse;
import com.xdream.knowledge.dto.ProcessResponse;
import com.xdream.knowledge.dto.RerankRequestDto;
import com.xdream.knowledge.dto.RerankResponseDto;
import com.xdream.knowledge.dto.SearchRequest;
import com.xdream.knowledge.dto.SearchResponse;
import com.xdream.knowledge.dto.UpdateDocumentRequest;
import com.xdream.knowledge.dto.UploadDocumentRequest;
import com.xdream.knowledge.entity.KnowledgeBaseEntity;
import com.xdream.knowledge.entity.KnowledgeDocumentEntity;
import com.xdream.knowledge.mapper.KnowledgeMapper;
import com.xdream.knowledge.model.DocumentChunk;
import com.xdream.knowledge.model.KnowledgeSegmentRecord;
import com.xdream.knowledge.repository.KnowledgeBaseRepository;
import com.xdream.knowledge.repository.KnowledgeDocumentRepository;
import com.xdream.knowledge.service.KnowledgeService;
import com.xdream.knowledge.service.support.DocumentChunkService;
import com.xdream.knowledge.service.support.KnowledgeVectorStore;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.data.domain.Pageable;

/**
 * 知识库核心业务实现，负责文档管理、向量化与语义检索。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final String DEFAULT_BASE_NAME = "默认知识库";

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final KnowledgeMapper knowledgeMapper;
    private final DocumentChunkService documentChunkService;
    private final KnowledgeVectorStore knowledgeVectorStore;
    private final LlmServiceClient llmServiceClient;
    private final KnowledgeProperties knowledgeProperties;

    @Override
    @Transactional
    public KnowledgeBaseResponse createKnowledgeBase(String userId, CreateKnowledgeBaseRequest request) {
        if (knowledgeBaseRepository.countByOwnerIdAndNameIgnoreCase(userId, request.getName()) > 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "知识库名称已存在");
        }
        KnowledgeBaseEntity entity = KnowledgeBaseEntity.builder()
                .id(UuidUtils.generateUuidWithoutDashes())
                .ownerId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .status("active")
                .documentCount(0L)
                .segmentCount(0L)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        knowledgeBaseRepository.save(entity);
        return knowledgeMapper.toBaseResponse(entity);
    }

    @Override
    @Transactional
    public KnowledgeBaseListResponse listKnowledgeBases(String userId) {
        List<KnowledgeBaseEntity> bases = knowledgeBaseRepository.findByOwnerIdOrderByCreatedAtDesc(userId);
        if (CollectionUtils.isEmpty(bases)) {
            bases = List.of(createDefaultBase(userId));
        }
        KnowledgeBaseListResponse response = new KnowledgeBaseListResponse();
        response.setItems(bases.stream().map(knowledgeMapper::toBaseResponse).collect(Collectors.toList()));
        return response;
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(String userId, UploadDocumentRequest request) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, request.getKnowledgeBaseId());

        KnowledgeDocumentEntity entity = KnowledgeDocumentEntity.builder()
                .id(UuidUtils.generateUuidWithoutDashes())
                .knowledgeBaseId(base.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .sourceType(Optional.ofNullable(request.getSourceType()).orElse("manual"))
                .fileName(null)
                .fileSize((long) request.getContent().getBytes().length)
                .tags(request.getTags())
                .category(request.getCategory())
                .status("active")
                .processStatus(Boolean.TRUE.equals(request.getAutoProcess()) ? "processing" : "pending")
                .vectorCount(0)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        knowledgeDocumentRepository.save(entity);

        recalculateCounters(base.getId(), userId);

        if (Boolean.TRUE.equals(request.getAutoProcess())) {
            // 异步处理可以后续扩展，这里直接同步执行
            processDocument(userId, base.getId(), entity.getId());
            entity = knowledgeDocumentRepository.findById(entity.getId()).orElse(entity);
        }
        return knowledgeMapper.toDocumentResponse(entity);
    }

    @Override
    public PageResponse<DocumentResponse> getDocuments(String userId, String knowledgeBaseId, String category, String keyword, Pageable pageable) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, knowledgeBaseId);
        List<KnowledgeDocumentEntity> documents = knowledgeDocumentRepository
                .findByKnowledgeBaseIdAndCreatedByOrderByCreatedAtDesc(base.getId(), userId);

        List<KnowledgeDocumentEntity> filtered = documents.stream()
                .filter(doc -> category == null || Objects.equals(doc.getCategory(), category))
                .filter(doc -> keyword == null || containsIgnoreCase(doc, keyword))
                .collect(Collectors.toList());

        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int total = filtered.size();
        int fromIndex = Math.min(pageNumber * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<DocumentResponse> content = filtered.subList(fromIndex, toIndex).stream()
                .map(knowledgeMapper::toDocumentResponse)
                .collect(Collectors.toList());

        return PageResponse.of(content, pageNumber, pageSize, total);
    }

    @Override
    public DocumentResponse getDocument(String userId, String knowledgeBaseId, String documentId) {
        KnowledgeDocumentEntity document = loadDocument(userId, knowledgeBaseId, documentId);
        return knowledgeMapper.toDocumentResponse(document);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(String userId, String knowledgeBaseId, String documentId, UpdateDocumentRequest request) {
        KnowledgeDocumentEntity document = loadDocument(userId, knowledgeBaseId, documentId);
        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            document.setContent(request.getContent());
            document.setFileSize((long) request.getContent().getBytes().length);
            document.setProcessStatus("pending");
        }
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            document.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            document.setTags(request.getTags());
        }
        document.setUpdatedBy(userId);
        knowledgeDocumentRepository.save(document);
        return knowledgeMapper.toDocumentResponse(document);
    }

    @Override
    @Transactional
    public void deleteDocument(String userId, String knowledgeBaseId, String documentId) {
        KnowledgeDocumentEntity document = loadDocument(userId, knowledgeBaseId, documentId);
        knowledgeDocumentRepository.delete(document);
        knowledgeVectorStore.deleteByDocumentId(documentId);
        recalculateCounters(knowledgeBaseId, userId);
    }

    @Override
    @Transactional
    public ProcessResponse processDocument(String userId, String knowledgeBaseId, String documentId) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, knowledgeBaseId);
        KnowledgeDocumentEntity document = loadDocument(userId, knowledgeBaseId, documentId);
        LocalDateTime start = LocalDateTime.now();

        try {
            knowledgeVectorStore.deleteByDocumentId(documentId);
            List<DocumentChunk> chunks = documentChunkService.split(document.getContent());
            if (chunks.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_PARAMETER, "文档内容为空，无法向量化");
            }

            List<KnowledgeSegmentRecord> segments = new ArrayList<>();
            for (DocumentChunk chunk : chunks) {
                EmbeddingRequestDto embeddingRequest = new EmbeddingRequestDto();
                embeddingRequest.setText(chunk.getContent());
                embeddingRequest.setModelType(null);
                ApiResponse<EmbeddingResponseDto> embeddingResponse = llmServiceClient.embedding(userId, embeddingRequest);
                if (embeddingResponse == null || !embeddingResponse.isSuccess()) {
                    throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "调用嵌入服务失败: " +
                            (embeddingResponse != null ? embeddingResponse.getMessage() : "未知错误"));
                }
                EmbeddingResponseDto data = embeddingResponse.getData();
                float[] vector = toFloatArray(data.getEmbedding());
                segments.add(KnowledgeSegmentRecord.builder()
                        .id(UuidUtils.generateUuidWithoutDashes())
                        .knowledgeBaseId(base.getId())
                        .documentId(documentId)
                        .chunkIndex(chunk.getIndex())
                        .content(chunk.getContent())
                        .tokenCount(chunk.getTokenCount())
                        .embedding(vector)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            knowledgeVectorStore.saveSegments(segments);

            document.setProcessStatus("completed");
            document.setProcessMessage("向量化完成，生成" + segments.size() + "条切片");
            document.setVectorCount((int) knowledgeVectorStore.countByDocumentId(documentId));
            document.setProcessedAt(LocalDateTime.now());
            document.setUpdatedBy(userId);
            knowledgeDocumentRepository.save(document);

            recalculateCounters(base.getId(), userId);

            ProcessResponse response = new ProcessResponse();
            response.setKnowledgeBaseId(base.getId());
            response.setDocumentId(documentId);
            response.setStatus(document.getProcessStatus());
            response.setMessage(document.getProcessMessage());
            response.setChunkCount(segments.size());
            response.setVectorCount(document.getVectorCount());
            response.setProcessStartTime(start);
            response.setProcessEndTime(LocalDateTime.now());
            response.setProcessDuration(Duration.between(start, response.getProcessEndTime()).toMillis());
            return response;
        } catch (Exception ex) {
            log.error("处理知识库文档失败，doc={}", documentId, ex);
            document.setProcessStatus("failed");
            document.setProcessMessage("向量化失败: " + ex.getMessage());
            document.setProcessedAt(LocalDateTime.now());
            document.setUpdatedBy(userId);
            knowledgeDocumentRepository.save(document);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "文档处理失败: " + ex.getMessage());
        }
    }

    @Override
    public SearchResponse searchKnowledge(String userId, SearchRequest request) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, request.getKnowledgeBaseId());
        LocalDateTime start = LocalDateTime.now();

        EmbeddingRequestDto embeddingRequest = new EmbeddingRequestDto();
        embeddingRequest.setText(request.getQuery());
        embeddingRequest.setModelType(null);
        ApiResponse<EmbeddingResponseDto> embeddingResponse = llmServiceClient.embedding(userId, embeddingRequest);
        if (embeddingResponse == null || !embeddingResponse.isSuccess()) {
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "嵌入服务响应异常");
        }
        float[] queryVector = toFloatArray(embeddingResponse.getData().getEmbedding());

        int topK = Optional.ofNullable(request.getTopK()).orElse(knowledgeProperties.getRetrieval().getTopK());
        double threshold = Optional.ofNullable(request.getSimilarityThreshold()).orElse(knowledgeProperties.getVectorSearch().getSimilarityThreshold());

        List<KnowledgeSegmentRecord> segments = knowledgeVectorStore.search(base.getId(), queryVector, topK);

        Map<String, KnowledgeDocumentEntity> documentMap = knowledgeDocumentRepository
                .findByKnowledgeBaseIdAndCreatedByOrderByCreatedAtDesc(base.getId(), userId)
                .stream()
                .collect(Collectors.toMap(KnowledgeDocumentEntity::getId, it -> it));

        List<SearchResponse.SearchResult> results = new ArrayList<>();
        for (KnowledgeSegmentRecord segment : segments) {
            KnowledgeDocumentEntity doc = documentMap.get(segment.getDocumentId());
            if (doc == null) {
                continue;
            }
            double similarity = segment.getSimilarity() != null ? segment.getSimilarity() : 0.0;
            if (similarity < threshold) {
                continue;
            }
            SearchResponse.SearchResult result = new SearchResponse.SearchResult();
            result.setDocumentId(doc.getId());
            result.setKnowledgeBaseId(base.getId());
            result.setTitle(doc.getTitle());
            result.setContent(segment.getContent());
            result.setChunkIndex(segment.getChunkIndex());
            result.setCategory(doc.getCategory());
            result.setTags(knowledgeMapper.toDocumentResponse(doc).getTags());
            result.setSimilarityScore(similarity);
            result.setMatchType("semantic");
            result.setHighlight(buildHighlight(segment.getContent(), request.getQuery()));
            result.setCitation(doc.getTitle() + "-" + (segment.getChunkIndex() + 1));
            results.add(result);
        }

        boolean rerankApplied = false;
        if (Boolean.TRUE.equals(request.getUseRerank()) && results.size() > 1) {
            int rerankTopK = Optional.ofNullable(request.getRerankTopK()).orElse(knowledgeProperties.getRetrieval().getRerankTopK());
            RerankRequestDto rerankRequest = new RerankRequestDto();
            rerankRequest.setQuery(request.getQuery());
            rerankRequest.setDocuments(results.stream().map(SearchResponse.SearchResult::getContent).collect(Collectors.toList()));
            rerankRequest.setTopK(Math.min(rerankTopK, results.size()));

            try {
                ApiResponse<RerankResponseDto> rerankResponse = llmServiceClient.rerank(userId, rerankRequest);
                if (rerankResponse != null && rerankResponse.isSuccess() && rerankResponse.getData() != null) {
                    Map<Integer, Double> scoreMap = new HashMap<>();
                    rerankResponse.getData().getResults().forEach(item -> scoreMap.put(item.getIndex(), item.getScore()));
                    for (int i = 0; i < results.size(); i++) {
                        Double score = scoreMap.get(i);
                        results.get(i).setRerankScore(score);
                    }
                    results.sort(Comparator.comparing(SearchResponse.SearchResult::getRerankScore,
                            Comparator.nullsLast(Comparator.reverseOrder())));
                    if (results.size() > rerankRequest.getTopK()) {
                        results = new ArrayList<>(results.subList(0, rerankRequest.getTopK()));
                    }
                    rerankApplied = true;
                }
            } catch (Exception rerankEx) {
                log.warn("重排序请求失败，将按相似度返回: {}", rerankEx.getMessage());
            }
        }

        SearchResponse response = new SearchResponse();
        response.setResults(results);
        response.setTotalCount((long) results.size());
        response.setQuery(request.getQuery());
        response.setFromVectorStore(knowledgeVectorStore.isVectorEnabled());
        response.setRerankApplied(rerankApplied);
        response.setSearchTime(Duration.between(start, LocalDateTime.now()).toMillis());
        return response;
    }

    @Override
    public KnowledgeStatsResponse getStats(String userId, String knowledgeBaseId) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, knowledgeBaseId);
        List<KnowledgeDocumentEntity> documents = knowledgeDocumentRepository
                .findByKnowledgeBaseIdAndCreatedByOrderByCreatedAtDesc(base.getId(), userId);

        long totalDocuments = documents.size();
        long processedDocuments = documents.stream()
                .filter(doc -> "completed".equalsIgnoreCase(doc.getProcessStatus()))
                .count();
        long totalVectors = knowledgeVectorStore.countByKnowledgeBaseId(base.getId());

        Map<String, Long> documentTypeStats = documents.stream()
                .collect(Collectors.groupingBy(doc -> Optional.ofNullable(doc.getSourceType()).orElse("unknown"), Collectors.counting()));
        Map<String, Long> categoryStats = documents.stream()
                .collect(Collectors.groupingBy(doc -> Optional.ofNullable(doc.getCategory()).orElse("未分类"), Collectors.counting()));

        Map<String, Long> weeklyTrend = buildWeeklyTrend(documents);

        long storageSize = documents.stream()
                .mapToLong(doc -> Optional.ofNullable(doc.getFileSize()).orElse(0L))
                .sum();

        KnowledgeStatsResponse response = new KnowledgeStatsResponse();
        response.setTotalDocuments(totalDocuments);
        response.setProcessedDocuments(processedDocuments);
        response.setTotalVectors(totalVectors);
        response.setCategoryCount(categoryStats.size());
        response.setDocumentTypeStats(documentTypeStats);
        response.setCategoryDocumentStats(categoryStats);
        response.setWeeklyGrowthTrend(weeklyTrend);
        response.setStorageSize(storageSize);
        response.setLastUpdated(LocalDateTime.now());
        return response;
    }

    private KnowledgeBaseEntity ensureKnowledgeBase(String userId, String knowledgeBaseId) {
        Optional<KnowledgeBaseEntity> repository = knowledgeBaseRepository.findById(knowledgeBaseId);
        return repository
                .filter(base -> Objects.equals(base.getOwnerId(), userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "知识库不存在或无权访问"));
    }

    private KnowledgeDocumentEntity loadDocument(String userId, String knowledgeBaseId, String documentId) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, knowledgeBaseId);
        return knowledgeDocumentRepository.findById(documentId)
                .filter(doc -> Objects.equals(doc.getKnowledgeBaseId(), base.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "文档不存在"));
    }

    private void recalculateCounters(String knowledgeBaseId, String userId) {
        KnowledgeBaseEntity base = ensureKnowledgeBase(userId, knowledgeBaseId);
        base.setDocumentCount(knowledgeDocumentRepository.countByKnowledgeBaseId(base.getId()));
        base.setSegmentCount(knowledgeVectorStore.countByKnowledgeBaseId(base.getId()));
        base.setLastIndexedAt(LocalDateTime.now());
        base.setUpdatedBy(userId);
        knowledgeBaseRepository.save(base);
    }

    private KnowledgeBaseEntity createDefaultBase(String userId) {
        KnowledgeBaseEntity base = KnowledgeBaseEntity.builder()
                .id(UuidUtils.generateUuidWithoutDashes())
                .ownerId(userId)
                .name(DEFAULT_BASE_NAME)
                .description("系统自动创建的默认知识库，可用于快速测试检索增强对话")
                .status("active")
                .documentCount(0L)
                .segmentCount(0L)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        knowledgeBaseRepository.save(base);
        return base;
    }

    private boolean containsIgnoreCase(KnowledgeDocumentEntity doc, String keyword) {
        String lower = keyword.toLowerCase();
        return (doc.getTitle() != null && doc.getTitle().toLowerCase().contains(lower))
                || (doc.getDescription() != null && doc.getDescription().toLowerCase().contains(lower));
    }

    private float[] toFloatArray(List<Float> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "嵌入向量为空");
        }
        float[] array = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            array[i] = embedding.get(i);
        }
        return array;
    }

    private String buildHighlight(String content, String query) {
        if (content == null || query == null) {
            return content;
        }
        String escapedQuery = query.replaceAll("([\\\\*\\+\\?\\|\\{\\}\\(\\)\\[\\]\\^\\$\\.])", "\\\\$1");
        return content.replaceAll("(?i)" + escapedQuery, "<mark>" + query + "</mark>");
    }

    private Map<String, Long> buildWeeklyTrend(List<KnowledgeDocumentEntity> documents) {
        Map<String, Long> trend = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            trend.put(date.toString(), 0L);
        }
        documents.stream()
                .filter(doc -> doc.getCreatedAt() != null)
                .forEach(doc -> {
                    LocalDate date = doc.getCreatedAt().toLocalDate();
                    String key = date.toString();
                    if (trend.containsKey(key)) {
                        trend.computeIfPresent(key, (k, v) -> v + 1);
                    }
                });
        return trend;
    }
}


