package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "知识搜索请求")
public class SearchRequest {
    
    @NotBlank(message = "搜索查询不能为空")
    @Size(max = 1000, message = "搜索查询不能超过1000个字符")
    @Schema(description = "搜索查询", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;
    
    @Size(max = 100, message = "分类不能超过100个字符")
    @Schema(description = "分类过滤")
    private String category;
    
    @Schema(description = "相似度阈值，0-1之间")
    private Float similarityThreshold = 0.7f;
    
    @Schema(description = "返回结果数量，默认10")
    private Integer limit = 10;
    
    @Schema(description = "是否启用语义搜索")
    private Boolean semanticSearch = true;
    
    @Schema(description = "是否启用全文搜索")
    private Boolean fullTextSearch = true;
}