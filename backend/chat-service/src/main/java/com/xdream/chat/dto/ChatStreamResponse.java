package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "流式对话响应")
public class ChatStreamResponse {

  @Schema(description = "流式响应ID")
  private String streamId;

  @Schema(description = "使用的模型", example = "GPT-3.5")
  private String modelType;

  @Schema(description = "流式内容", example = "多线程")
  private String content;

  @Schema(description = "是否完成", example = "false")
  private Boolean finished;

  @Schema(description = "完成原因", example = "")
  private String finishReason;

  @Schema(description = "token使用量", example = "0")
  private Integer tokenUsage;
}
