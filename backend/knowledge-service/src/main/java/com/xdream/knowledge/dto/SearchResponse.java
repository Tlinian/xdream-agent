package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "知识搜索响应")
public class SearchResponse {
    
    @Schema(description = "搜索结果列表")
    private List<SearchResult> results;
    
    @Schema(description = "总结果数量")
    private Long totalCount;
    
    @Schema(description = "搜索耗时（毫秒）")
    private Long searchTime;
    
    @Schema(description = "搜索查询")
    private String query;
    
    @Data
    @Schema(description = "搜索结果项")
    public static class SearchResult {
        
        @Schema(description = "文档ID")
        private String documentId;
        
        @Schema(description = "文档标题")
        private String title;
        
        @Schema(description = "文档内容片段")
        private String content;
        
        @Schema(description = "分类")
        private String category;
        
        @Schema(description = "标签列表")
        private List<String> tags;
        
        @Schema(description = "相似度分数")
        private Float similarityScore;
        
        @Schema(description = "匹配类型（semantic, fulltext）")
        private String matchType;
        
        @Schema(description = "高亮片段")
        private String highlight;
    }
}