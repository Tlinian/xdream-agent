package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 嵌入响应。
 */
@Data
@Schema(description = "嵌入响应")
public class EmbeddingResponseDto {

    @Schema(description = "响应 ID")
    private String id;

    @Schema(description = "模型标识")
    private String modelType;

    @Schema(description = "向量数组")
    private List<Float> embedding;

    @Schema(description = "维度")
    private Integer dimensions;

    @Schema(description = "token 使用量")
    private Integer tokenUsage;

    @Schema(description = "生成时间")
    private LocalDateTime createdAt;
}

