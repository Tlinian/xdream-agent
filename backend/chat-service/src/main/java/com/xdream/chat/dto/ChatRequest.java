package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "对话请求")
public class ChatRequest {

  @NotBlank(message = "消息内容不能为空")
  @Size(max = 10000, message = "消息内容不能超过10000个字符")
  @Schema(description = "消息内容", example = "请帮我解释什么是多线程？")
  private String message;

  @Schema(description = "AI模型类型", example = "GPT-3.5")
  private String modelType;

  @Schema(description = "系统提示词", example = "你是一个Java编程专家")
  private String systemPrompt;

  @Schema(description = "对话历史", example = "")
  private String conversationHistory;

  @Schema(description = "温度参数", example = "0.7")
  private Double temperature = 0.7;

  @Schema(description = "最大token数", example = "1000")
  private Integer maxTokens = 1000;

  @Schema(description = "是否流式响应", example = "false")
  private Boolean stream = false;
}
