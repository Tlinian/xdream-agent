package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "消息响应")
public class MessageResponse {

  @Schema(description = "消息ID")
  private String id;

  @Schema(description = "对话ID")
  private String conversationId;

  @Schema(description = "消息内容", example = "请帮我解释什么是多线程？")
  private String content;

  @Schema(description = "消息类型", example = "TEXT")
  private String messageType;

  @Schema(description = "消息角色", example = "USER")
  private String role;

  @Schema(description = "父消息ID", example = "")
  private String parentMessageId;

  @Schema(description = "模型类型", example = "GPT-3.5")
  private String modelType;

  @Schema(description = "token使用量", example = "150")
  private Integer tokenUsage;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  @Schema(description = "更新时间")
  private LocalDateTime updatedAt;
}
