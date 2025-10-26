package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建对话请求")
public class CreateConversationRequest {

  @NotBlank(message = "对话标题不能为空")
  @Size(max = 100, message = "对话标题不能超过100个字符")
  @Schema(description = "对话标题", example = "关于Java编程的问题")
  private String title;

  @Size(max = 500, message = "对话描述不能超过500个字符")
  @Schema(description = "对话描述", example = "讨论Java中的多线程编程")
  private String description;

  @Schema(description = "AI模型类型", example = "GPT-3.5")
  private String modelType;

  @Schema(description = "系统提示词", example = "你是一个Java编程专家")
  private String systemPrompt;

  @Schema(description = "关联的知识库ID", example = "kb_123456")
  private String knowledgeBaseId;
}
