package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "对话响应")
public class ConversationResponse {

  @Schema(description = "对话ID")
  private String id;

  @Schema(description = "用户ID")
  private String userId;

  @Schema(description = "对话标题", example = "关于Java编程的问题")
  private String title;

  @Schema(description = "对话描述", example = "讨论Java中的多线程编程")
  private String description;

  @Schema(description = "AI模型类型", example = "GPT-3.5")
  private String modelType;

  @Schema(description = "系统提示词", example = "你是一个Java编程专家")
  private String systemPrompt;

  @Schema(description = "是否置顶", example = "false")
  private Boolean pinned;

  @Schema(description = "消息数量", example = "10")
  private Integer messageCount;

  @Schema(description = "最后活动时间")
  private LocalDateTime lastActivity;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  @Schema(description = "更新时间")
  private LocalDateTime updatedAt;
}
