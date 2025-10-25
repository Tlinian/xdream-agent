package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调用 LLM 服务的嵌入请求。
 */
@Data
@Schema(description = "嵌入请求")
public class EmbeddingRequestDto {

    @Schema(description = "待向量化的文本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;

    @Schema(description = "使用的嵌入模型标识", example = "BAAI/bge-large-zh-v1.5")
    private String modelType;
}

