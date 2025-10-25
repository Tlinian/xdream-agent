package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文档处理响应，包含向量化信息。
 */
@Data
@Schema(description = "文档处理响应")
public class ProcessResponse {

    @Schema(description = "知识库ID")
    private String knowledgeBaseId;

    @Schema(description = "文档ID")
    private String documentId;

    @Schema(description = "处理状态")
    private String status;

    @Schema(description = "处理信息")
    private String message;

    @Schema(description = "生成的分片数量")
    private Integer chunkCount;

    @Schema(description = "生成的向量数量")
    private Integer vectorCount;

    @Schema(description = "处理开始时间")
    private LocalDateTime processStartTime;

    @Schema(description = "处理结束时间")
    private LocalDateTime processEndTime;

    @Schema(description = "处理耗时（毫秒）")
    private Long processDuration;
}

