package com.xdream.knowledge.entity;

import com.xdream.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 知识库实体，对标 Dify 数据集的顶层概念。
 */
@Data
@Entity
@Table(name = "knowledge_bases")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class KnowledgeBaseEntity extends BaseEntity {

    /** 所属用户 ID */
    @Column(name = "owner_id", nullable = false, length = 64)
    private String ownerId;

    /** 知识库名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 知识库描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 状态：active/disabled */
    @Column(name = "status", length = 32)
    private String status;

    /** 文档数量 */
    @Column(name = "document_count")
    private Long documentCount;

    /** 切片总数（向量条目数） */
    @Column(name = "segment_count")
    private Long segmentCount;

    /** 最近一次向量化时间 */
    @Column(name = "last_indexed_at")
    private java.time.LocalDateTime lastIndexedAt;
}

