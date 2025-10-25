package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 知识库返回模型。
 */
@Data
@Schema(description = "知识库响应")
public class KnowledgeBaseResponse {

    @Schema(description = "知识库 ID")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "文档数量")
    private Long documentCount;

    @Schema(description = "向量条数")
    private Long segmentCount;

    @Schema(description = "最近向量化时间")
    private LocalDateTime lastIndexedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

