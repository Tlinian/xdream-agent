package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "重排序请求")
public class RerankRequest {

  @Schema(description = "检索查询", requiredMode = Schema.RequiredMode.REQUIRED)
  private String query;

  @Schema(description = "候选文本集合", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> documents;

  @Schema(description = "模型标识", example = "BAAI/bge-reranker-v2-m3")
  private String modelType;

  @Schema(description = "保留前K条")
  private Integer topK;
}
