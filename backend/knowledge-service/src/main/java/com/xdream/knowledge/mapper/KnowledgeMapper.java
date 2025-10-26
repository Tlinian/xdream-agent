package com.xdream.knowledge.mapper;

import com.xdream.knowledge.dto.DocumentResponse;
import com.xdream.knowledge.dto.KnowledgeBaseResponse;
import com.xdream.knowledge.entity.KnowledgeBaseEntity;
import com.xdream.knowledge.entity.KnowledgeDocumentEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 实体与 DTO 映射工具。
 */
@Component
public class KnowledgeMapper {

    /**
     * 映射知识库实体。
     */
    public KnowledgeBaseResponse toBaseResponse(KnowledgeBaseEntity entity) {
        if (entity == null) {
            return null;
        }
        KnowledgeBaseResponse response = new KnowledgeBaseResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setDocumentCount(entity.getDocumentCount());
        response.setSegmentCount(entity.getSegmentCount());
        response.setLastIndexedAt(entity.getLastIndexedAt());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    /**
     * 映射文档实体。
     */
    public DocumentResponse toDocumentResponse(KnowledgeDocumentEntity entity) {
        if (entity == null) {
            return null;
        }
        DocumentResponse response = new DocumentResponse();
        response.setDocumentId(entity.getId());
//        response.setKnowledgeBaseId(entity.getKnowledgeBaseId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setContent(entity.getContent());
        response.setDocumentType(entity.getSourceType());
        response.setCategory(entity.getCategory());
        response.setTags(parseTags(entity.getTags()));
        response.setFileSize(entity.getFileSize());
        response.setStatus(entity.getStatus());
        response.setProcessStatus(entity.getProcessStatus());
        response.setProcessMessage(entity.getProcessMessage());
        response.setVectorCount(entity.getVectorCount());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setProcessedAt(entity.getProcessedAt());
        return response;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}