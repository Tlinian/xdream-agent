package com.xdream.knowledge.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 知识切片记录，用于向量表操作。
 */
@Data
@Builder
public class KnowledgeSegmentRecord {

    /** 主键 ID */
    private String id;

    /** 知识库 ID */
    private String knowledgeBaseId;

    /** 文档 ID */
    private String documentId;

    /** 分片序号 */
    private Integer chunkIndex;

    /** 分片内容 */
    private String content;

    /** 分片的 token 粗略数量 */
    private Integer tokenCount;

    /** 文本嵌入向量 */
    private float[] embedding;

    /** 相似度分数（查询时使用） */
    private Double similarity;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}

