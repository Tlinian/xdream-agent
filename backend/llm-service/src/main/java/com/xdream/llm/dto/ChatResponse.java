package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "对话响应")
public class ChatResponse {

  @Schema(description = "响应ID")
  private String id;

  @Schema(description = "AI回复内容", example = "多线程是指程序中同时执行多个线程...")
  private String response;

  @Schema(description = "使用的模型", example = "gpt-3.5-turbo")
  private String modelType;

  @Schema(description = "token使用量", example = "150")
  private Integer tokenUsage;

  @Schema(description = "提示token数", example = "100")
  private Integer promptTokens;

  @Schema(description = "完成token数", example = "50")
  private Integer completionTokens;

  @Schema(description = "完成原因", example = "stop")
  private String finishReason;

  @Schema(description = "响应时间")
  private LocalDateTime responseTime;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;
}
