package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "嵌入响应")
public class EmbeddingResponse {

  @Schema(description = "响应ID")
  private String id;

  @Schema(description = "使用的模型", example = "text-embedding-ada-002")
  private String modelType;

  @Schema(description = "嵌入向量", example = "[0.1, 0.2, 0.3, ...]")
  private List<Float> embedding;

  @Schema(description = "向量维度", example = "1536")
  private Integer dimensions;

  @Schema(description = "token使用量", example = "10")
  private Integer tokenUsage;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;
}
