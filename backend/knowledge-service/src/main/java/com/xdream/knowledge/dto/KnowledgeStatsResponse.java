package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "知识库统计响应")
public class KnowledgeStatsResponse {
    
    @Schema(description = "总文档数量")
    private Long totalDocuments;
    
    @Schema(description = "已处理文档数量")
    private Long processedDocuments;
    
    @Schema(description = "总向量数量")
    private Long totalVectors;
    
    @Schema(description = "分类数量")
    private Integer categoryCount;
    
    @Schema(description = "文档类型统计")
    private Map<String, Long> documentTypeStats;
    
    @Schema(description = "分类文档数量统计")
    private Map<String, Long> categoryDocumentStats;
    
    @Schema(description = "最近7天文档增长趋势")
    private Map<String, Long> weeklyGrowthTrend;
    
    @Schema(description = "存储大小（字节）")
    private Long storageSize;
    
    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdated;
}