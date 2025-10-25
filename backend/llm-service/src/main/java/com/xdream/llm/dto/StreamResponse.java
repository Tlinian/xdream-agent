package com.xdream.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "娴佸紡鍝嶅簲")
@Builder
public class StreamResponse {

  @Schema(description = "娴佸紡鍝嶅簲ID")
  private String streamId;

  @Schema(description = "浣跨敤鐨勬ā鍨?, example = "gpt-3.5-turbo")
  private String modelType;

  @Schema(description = "娴佸紡鍐呭", example = "澶氱嚎绋?)
  private String content;

  @Schema(description = "鏄惁瀹屾垚", example = "false")
  private Boolean finished;

  @Schema(description = "瀹屾垚鍘熷洜", example = "")
  private String finishReason;

  @Schema(description = "token浣跨敤閲?, example = "0")
  private Integer tokenUsage;
}
