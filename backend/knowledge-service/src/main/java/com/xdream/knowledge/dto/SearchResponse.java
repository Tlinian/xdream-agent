package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 知识搜索响应。
 */
@Data
@Schema(description = "知识搜索响应")
public class SearchResponse {

    @Schema(description = "搜索结果列表")
    private List<SearchResult> results;

    @Schema(description = "总命中数量")
    private Long totalCount;

    @Schema(description = "搜索耗时（毫秒）")
    private Long searchTime;

    @Schema(description = "原始查询")
    private String query;

    @Schema(description = "是否来自向量检索")
    private Boolean fromVectorStore;

    @Schema(description = "是否执行了重排序")
    private Boolean rerankApplied;

    @Data
    @Schema(description = "搜索结果条目")
    public static class SearchResult {

        @Schema(description = "文档ID")
        private String documentId;

        @Schema(description = "知识库ID")
        private String knowledgeBaseId;

        @Schema(description = "文档标题")
        private String title;

        @Schema(description = "文档内容片段")
        private String content;

        @Schema(description = "片段序号")
        private Integer chunkIndex;

        @Schema(description = "分类")
        private String category;

        @Schema(description = "标签列表")
        private List<String> tags;

        @Schema(description = "相似度得分")
        private Double similarityScore;

        @Schema(description = "重排序得分")
        private Double rerankScore;

        @Schema(description = "匹配类型")
        private String matchType;

        @Schema(description = "高亮片段")
        private String highlight;

        @Schema(description = "引用标识")
        private String citation;
    }
}

