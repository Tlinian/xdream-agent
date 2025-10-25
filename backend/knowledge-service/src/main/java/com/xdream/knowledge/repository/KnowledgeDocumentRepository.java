package com.xdream.knowledge.repository;

import com.xdream.knowledge.entity.KnowledgeDocumentEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文档实体仓库。
 */
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, String> {

    /**
     * 分页查询文档列表。
     */
    Page<KnowledgeDocumentEntity> findByKnowledgeBaseIdAndCreatedBy(String knowledgeBaseId, String userId, Pageable pageable);

    /**
     * 按分类关键字过滤。
     */
    Page<KnowledgeDocumentEntity> findByKnowledgeBaseIdAndCreatedByAndCategoryContainingIgnoreCase(String knowledgeBaseId, String userId, String category, Pageable pageable);

    /**
     * 根据标题模糊匹配。
     */
    Page<KnowledgeDocumentEntity> findByKnowledgeBaseIdAndCreatedByAndTitleContainingIgnoreCase(String knowledgeBaseId, String userId, String keyword, Pageable pageable);

    /**
     * 根据知识库统计文档数量。
     */
    long countByKnowledgeBaseId(String knowledgeBaseId);

    /**
     * 批量查询文档。
     */
    List<KnowledgeDocumentEntity> findByKnowledgeBaseId(String knowledgeBaseId);
    /**
     * 查询指定知识库下的全部文档并按时间倒序排序。
     */
    List<KnowledgeDocumentEntity> findByKnowledgeBaseIdAndCreatedByOrderByCreatedAtDesc(String knowledgeBaseId, String userId);\n}

