package com.xdream.knowledge.entity;

import com.xdream.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 知识库文档实体，保存原始文本与处理状态。
 */
@Data
@Entity
@Table(name = "knowledge_documents")
@EqualsAndHashCode(callSuper = true, exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class KnowledgeDocumentEntity extends BaseEntity {

    /** 文档ID */
    @Id
    @Column(name = "id", nullable = false, length = 64)
    @Setter(AccessLevel.NONE)
    private String id;

    /** 所属知识库 ID */
    @Column(name = "knowledge_base_id", nullable = false, length = 64)
    private String knowledgeBaseId;

    /** 文档标题 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 文档描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 文档原始内容（纯文本） */
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 文档来源类型：manual、upload、api 等 */
    @Column(name = "source_type", length = 32)
    private String sourceType;

    /** 上传文件名 */
    @Column(name = "file_name", length = 255)
    private String fileName;

    /** 文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 文档标签，使用逗号拼接 */
    @Column(name = "tags", length = 512)
    private String tags;

    /** 文档分类 */
    @Column(name = "category", length = 100)
    private String category;

    /** 文档状态：active/archived */
    @Column(name = "status", length = 32)
    private String status;

    /** 处理状态：pending/processing/completed/failed */
    @Column(name = "process_status", length = 32)
    private String processStatus;

    /** 处理结果描述 */
    @Column(name = "process_message", length = 500)
    private String processMessage;

    /** 生成的向量条数 */
    @Column(name = "vector_count")
    private Integer vectorCount;

    /** 文档处理完成时间 */
    @Column(name = "processed_at")
    private java.time.LocalDateTime processedAt;
}