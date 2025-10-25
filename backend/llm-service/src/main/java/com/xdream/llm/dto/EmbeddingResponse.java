package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "宓屽叆鍝嶅簲")
public class EmbeddingResponse {

  @Schema(description = "鍝嶅簲ID")
  private String id;

  @Schema(description = "浣跨敤鐨勬ā鍨?, example = "text-embedding-ada-002")
  private String modelType;

  @Schema(description = "宓屽叆鍚戦噺", example = "[0.1, 0.2, 0.3, ...]")
  private List<Float> embedding;

  @Schema(description = "鍚戦噺缁村害", example = "1536")
  private Integer dimensions;

  @Schema(description = "token浣跨敤閲?, example = "10")
  private Integer tokenUsage;

  @Schema(description = "鍒涘缓鏃堕棿")
  private LocalDateTime createdAt;
}
