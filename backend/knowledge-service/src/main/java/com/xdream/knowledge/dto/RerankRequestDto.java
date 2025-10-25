package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 重排序请求体。
 */
@Data
@Schema(description = "重排序请求")
public class RerankRequestDto {

    @Schema(description = "检索查询", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;

    @Schema(description = "候选文本集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> documents;

    @Schema(description = "模型标识", example = "BAAI/bge-reranker-v2-m3")
    private String modelType;

    @Schema(description = "返回前 K 条结果")
    private Integer topK;
}

