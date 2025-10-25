package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 知识搜索请求。
 */
@Data
@Schema(description = "知识搜索请求")
public class SearchRequest {

    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String knowledgeBaseId;

    @NotBlank(message = "搜索内容不能为空")
    @Size(max = 1000, message = "搜索内容不能超过1000个字符")
    @Schema(description = "搜索内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;

    @Schema(description = "分类过滤")
    private String category;

    @Schema(description = "相似度阈值，0-1之间")
    private Double similarityThreshold = 0.6;

    @Schema(description = "召回条数")
    private Integer topK = 8;

    @Schema(description = "重排序后保留条数")
    private Integer rerankTopK = 3;

    @Schema(description = "是否启用重排序模型")
    private Boolean useRerank = true;

    @Schema(description = "是否在回答中附带引用信息")
    private Boolean appendCitations = true;
}

