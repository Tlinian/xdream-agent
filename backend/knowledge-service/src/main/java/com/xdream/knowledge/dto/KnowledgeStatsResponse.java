package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

/**
 * 知识库统计信息响应。
 */
@Data
@Schema(description = "知识库统计响应")
public class KnowledgeStatsResponse {

    @Schema(description = "总文档数量")
    private Long totalDocuments;

    @Schema(description = "已完成处理的文档数量")
    private Long processedDocuments;

    @Schema(description = "向量条目总数")
    private Long totalVectors;

    @Schema(description = "分类数量")
    private Integer categoryCount;

    @Schema(description = "文档类型分布统计")
    private Map<String, Long> documentTypeStats;

    @Schema(description = "分类文档数量统计")
    private Map<String, Long> categoryDocumentStats;

    @Schema(description = "最近七天文档增长趋势")
    private Map<String, Long> weeklyGrowthTrend;

    @Schema(description = "存储体积（字节）")
    private Long storageSize;

    @Schema(description = "统计生成时间")
    private LocalDateTime lastUpdated;
}

