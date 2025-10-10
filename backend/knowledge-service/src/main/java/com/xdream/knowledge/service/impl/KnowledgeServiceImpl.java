package com.xdream.knowledge.service.impl;

import com.xdream.common.dto.PageResponse;
import com.xdream.knowledge.dto.*;
import com.xdream.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeServiceImpl implements KnowledgeService {
    
    // 模拟数据存储
    private final Map<String, DocumentResponse> documents = new ConcurrentHashMap<>();
    private final Map<String, CategoryResponse> categories = new ConcurrentHashMap<>();
    private final Map<String, List<String>> documentCategories = new ConcurrentHashMap<>();
    
    @Override
    public DocumentResponse uploadDocument(String userId, UploadDocumentRequest request) {
        String documentId = UUID.randomUUID().toString();
        
        DocumentResponse document = new DocumentResponse();
        document.setDocumentId(documentId);
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setDescription(request.getDescription());
        document.setDocumentType(request.getDocumentType());
        document.setCategory(request.getCategory());
        document.setTags(parseTags(request.getTags()));
        document.setFileSize((long) request.getContent().getBytes().length);
        document.setStatus("active");
        document.setProcessStatus("pending");
        document.setVectorCount(0);
        document.setCreatedBy(userId);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        
        documents.put(documentId, document);
        
        if (request.getAutoProcess() != null && request.getAutoProcess()) {
            processDocument(userId, documentId);
        }
        
        log.info("Document uploaded: {} by user: {}", documentId, userId);
        return document;
    }
    
    @Override
    public PageResponse<DocumentResponse> getDocuments(String userId, String category, String keyword, Pageable pageable) {
        List<DocumentResponse> filteredDocuments = documents.values().stream()
                .filter(doc -> doc.getCreatedBy().equals(userId))
                .filter(doc -> category == null || category.equals(doc.getCategory()))
                .filter(doc -> keyword == null || 
                        (doc.getTitle() != null && doc.getTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                        (doc.getDescription() != null && doc.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .sorted(Comparator.comparing(DocumentResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredDocuments.size());
        
        List<DocumentResponse> pageContent = start >= filteredDocuments.size() ? 
                Collections.emptyList() : filteredDocuments.subList(start, end);
        
        return PageResponse.of(pageContent, filteredDocuments.size(), pageable.getPageNumber(), pageable.getPageSize());
    }
    
    @Override
    public DocumentResponse getDocument(String userId, String documentId) {
        DocumentResponse document = documents.get(documentId);
        if (document == null || !document.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Document not found or access denied");
        }
        return document;
    }
    
    @Override
    public DocumentResponse updateDocument(String userId, String documentId, UpdateDocumentRequest request) {
        DocumentResponse document = getDocument(userId, documentId);
        
        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            document.setContent(request.getContent());
            document.setFileSize((long) request.getContent().getBytes().length);
        }
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            document.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            document.setTags(parseTags(request.getTags()));
        }
        
        document.setUpdatedAt(LocalDateTime.now());
        
        log.info("Document updated: {} by user: {}", documentId, userId);
        return document;
    }
    
    @Override
    public void deleteDocument(String userId, String documentId) {
        DocumentResponse document = getDocument(userId, documentId);
        documents.remove(documentId);
        documentCategories.remove(documentId);
        log.info("Document deleted: {} by user: {}", documentId, userId);
    }
    
    @Override
    public ProcessResponse processDocument(String userId, String documentId) {
        DocumentResponse document = getDocument(userId, documentId);
        
        LocalDateTime startTime = LocalDateTime.now();
        
        // 模拟文档处理过程
        document.setProcessStatus("processing");
        
        try {
            // 模拟文本提取和向量化
            Thread.sleep(1000);
            
            String content = document.getContent();
            int textCount = content.length();
            int vectorCount = Math.max(1, textCount / 500); // 模拟向量数量
            
            document.setProcessStatus("completed");
            document.setVectorCount(vectorCount);
            document.setProcessedAt(LocalDateTime.now());
            
            ProcessResponse response = new ProcessResponse();
            response.setDocumentId(documentId);
            response.setStatus("completed");
            response.setMessage("Document processed successfully");
            response.setTextCount(textCount);
            response.setVectorCount(vectorCount);
            response.setProcessStartTime(startTime);
            response.setProcessEndTime(LocalDateTime.now());
            response.setProcessDuration(java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds());
            
            log.info("Document processed: {} by user: {}", documentId, userId);
            return response;
            
        } catch (Exception e) {
            document.setProcessStatus("failed");
            document.setProcessMessage(e.getMessage());
            
            ProcessResponse response = new ProcessResponse();
            response.setDocumentId(documentId);
            response.setStatus("failed");
            response.setMessage(e.getMessage());
            response.setProcessStartTime(startTime);
            response.setProcessEndTime(LocalDateTime.now());
            
            log.error("Document processing failed: {} by user: {}", documentId, userId, e);
            return response;
        }
    }
    
    @Override
    public SearchResponse searchKnowledge(String userId, SearchRequest request) {
        List<DocumentResponse> userDocuments = documents.values().stream()
                .filter(doc -> doc.getCreatedBy().equals(userId))
                .filter(doc -> request.getCategory() == null || request.getCategory().equals(doc.getCategory()))
                .collect(Collectors.toList());
        
        List<SearchResponse.SearchResult> results = new ArrayList<>();
        
        for (DocumentResponse doc : userDocuments) {
            float similarityScore = calculateSimilarity(request.getQuery(), doc.getContent());
            
            if (similarityScore >= request.getSimilarityThreshold()) {
                SearchResponse.SearchResult result = new SearchResponse.SearchResult();
                result.setDocumentId(doc.getDocumentId());
                result.setTitle(doc.getTitle());
                result.setContent(getContentSnippet(doc.getContent(), request.getQuery()));
                result.setCategory(doc.getCategory());
                result.setTags(doc.getTags());
                result.setSimilarityScore(similarityScore);
                result.setMatchType("semantic");
                result.setHighlight(createHighlight(doc.getContent(), request.getQuery()));
                
                results.add(result);
            }
        }
        
        // 按相似度排序
        results.sort((a, b) -> Float.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        
        // 限制结果数量
        if (results.size() > request.getLimit()) {
            results = results.subList(0, request.getLimit());
        }
        
        SearchResponse response = new SearchResponse();
        response.setResults(results);
        response.setTotalCount((long) results.size());
        response.setSearchTime(150L); // 模拟搜索耗时
        response.setQuery(request.getQuery());
        
        log.info("Knowledge search completed for user: {} with query: {} and {} results", userId, request.getQuery(), results.size());
        return response;
    }
    
    @Override
    public CategoryListResponse getCategories(String userId) {
        List<CategoryResponse> categoryList = new ArrayList<>(categories.values());
        
        CategoryListResponse response = new CategoryListResponse();
        response.setCategories(categoryList);
        response.setTotalCount((long) categoryList.size());
        
        return response;
    }
    
    @Override
    public CategoryResponse createCategory(String userId, CreateCategoryRequest request) {
        String categoryId = UUID.randomUUID().toString();
        
        CategoryResponse category = new CategoryResponse();
        category.setCategoryId(categoryId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());
        category.setDocumentCount(0);
        category.setCreatedBy(userId);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        categories.put(categoryId, category);
        
        log.info("Category created: {} by user: {}", categoryId, userId);
        return category;
    }
    
    @Override
    public KnowledgeStatsResponse getStats(String userId) {
        List<DocumentResponse> userDocuments = documents.values().stream()
                .filter(doc -> doc.getCreatedBy().equals(userId))
                .collect(Collectors.toList());
        
        long totalDocuments = userDocuments.size();
        long processedDocuments = userDocuments.stream()
                .filter(doc -> "completed".equals(doc.getProcessStatus()))
                .count();
        long totalVectors = userDocuments.stream()
                .mapToLong(doc -> doc.getVectorCount() != null ? doc.getVectorCount() : 0)
                .sum();
        
        Map<String, Long> documentTypeStats = userDocuments.stream()
                .collect(Collectors.groupingBy(
                        doc -> doc.getDocumentType() != null ? doc.getDocumentType() : "unknown",
                        Collectors.counting()
                ));
        
        Map<String, Long> categoryDocumentStats = userDocuments.stream()
                .collect(Collectors.groupingBy(
                        doc -> doc.getCategory() != null ? doc.getCategory() : "uncategorized",
                        Collectors.counting()
                ));
        
        // 模拟最近7天增长趋势
        Map<String, Long> weeklyGrowthTrend = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            long count = (long) (Math.random() * 10); // 模拟数据
            weeklyGrowthTrend.put(dateStr, count);
        }
        
        long storageSize = userDocuments.stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0)
                .sum();
        
        KnowledgeStatsResponse stats = new KnowledgeStatsResponse();
        stats.setTotalDocuments(totalDocuments);
        stats.setProcessedDocuments(processedDocuments);
        stats.setTotalVectors(totalVectors);
        stats.setCategoryCount(categories.size());
        stats.setDocumentTypeStats(documentTypeStats);
        stats.setCategoryDocumentStats(categoryDocumentStats);
        stats.setWeeklyGrowthTrend(weeklyGrowthTrend);
        stats.setStorageSize(storageSize);
        stats.setLastUpdated(LocalDateTime.now());
        
        return stats;
    }
    
    private List<String> parseTags(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tagsString.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
    
    private float calculateSimilarity(String query, String content) {
        // 简单的相似度计算（实际应用中应该使用更复杂的算法）
        if (query == null || content == null) {
            return 0.0f;
        }
        
        String lowerQuery = query.toLowerCase();
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.contains(lowerQuery)) {
            return 0.9f;
        }
        
        // 计算词频相似度
        Set<String> queryWords = new HashSet<>(Arrays.asList(lowerQuery.split("\\s+")));
        Set<String> contentWords = new HashSet<>(Arrays.asList(lowerContent.split("\\s+")));
        
        Set<String> intersection = new HashSet<>(queryWords);
        intersection.retainAll(contentWords);
        
        if (queryWords.isEmpty()) {
            return 0.0f;
        }
        
        return (float) intersection.size() / queryWords.size() * 0.8f;
    }
    
    private String getContentSnippet(String content, String query) {
        if (content == null || content.length() <= 200) {
            return content;
        }
        
        if (query != null && content.toLowerCase().contains(query.toLowerCase())) {
            int index = content.toLowerCase().indexOf(query.toLowerCase());
            int start = Math.max(0, index - 50);
            int end = Math.min(content.length(), index + query.length() + 150);
            return content.substring(start, end) + "...";
        }
        
        return content.substring(0, 200) + "...";
    }
    
    private String createHighlight(String content, String query) {
        if (content == null || query == null) {
            return content;
        }
        
        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        
        if (lowerContent.contains(lowerQuery)) {
            return content.replaceAll("(?i)" + query, "**" + query + "**");
        }
        
        return content;
    }
}