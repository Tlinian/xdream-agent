package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文档处理响应")
public class ProcessResponse {
    
    @Schema(description = "文档ID")
    private String documentId;
    
    @Schema(description = "处理状态")
    private String status;
    
    @Schema(description = "处理信息")
    private String message;
    
    @Schema(description = "提取的文本数量")
    private Integer textCount;
    
    @Schema(description = "生成的向量数量")
    private Integer vectorCount;
    
    @Schema(description = "处理开始时间")
    private LocalDateTime processStartTime;
    
    @Schema(description = "处理结束时间")
    private LocalDateTime processEndTime;
    
    @Schema(description = "处理耗时（秒）")
    private Long processDuration;
}