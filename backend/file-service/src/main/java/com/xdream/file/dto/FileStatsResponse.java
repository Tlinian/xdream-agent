package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "文件统计响应")
public class FileStatsResponse {
    
    @Schema(description = "总文件数量")
    private Long totalFiles;
    
    @Schema(description = "总文件大小（字节）")
    private Long totalSize;
    
    @Schema(description = "文件类型统计")
    private Map<String, Long> fileTypeStats;
    
    @Schema(description = "文件大小分布统计")
    private Map<String, Long> sizeDistribution;
    
    @Schema(description = "最近7天上传趋势")
    private Map<String, Long> weeklyUploadTrend;
    
    @Schema(description = "本月上传数量")
    private Long monthlyUploadCount;
    
    @Schema(description = "本月上传大小（字节）")
    private Long monthlyUploadSize;
    
    @Schema(description = "最后上传时间")
    private LocalDateTime lastUploadTime;
}