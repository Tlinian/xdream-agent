package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新对话请求")
public class UpdateConversationRequest {

  @Size(max = 100, message = "对话标题不能超过100个字符")
  @Schema(description = "对话标题", example = "更新后的标题")
  private String title;

  @Size(max = 500, message = "对话描述不能超过500个字符")
  @Schema(description = "对话描述", example = "更新后的描述")
  private String description;

  @Schema(description = "AI模型类型", example = "GPT-4")
  private String modelType;

  @Schema(description = "系统提示词", example = "更新后的系统提示词")
  private String systemPrompt;

  @Schema(description = "是否置顶", example = "true")
  private Boolean pinned;
}
