package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "文档响应")
public class DocumentResponse {

    @Schema(description = "文档ID")
    private String documentId;

    @Schema(description = "文档标题")
    private String title;

    @Schema(description = "文档内容")
    private String content;

    @Schema(description = "文档描述")
    private String description;

    @Schema(description = "文档类型")
    private String documentType;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文档状态")
    private String status;

    @Schema(description = "处理状态")
    private String processStatus;

    @Schema(description = "处理信息")
    private String processMessage;

    @Schema(description = "向量数量")
    private Integer vectorCount;

    @Schema(description = "创建者ID")
    private String createdBy;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "处理时间")
    private LocalDateTime processedAt;
}