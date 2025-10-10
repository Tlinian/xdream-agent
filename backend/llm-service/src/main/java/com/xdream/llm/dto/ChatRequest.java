package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "对话请求")
public class ChatRequest {

  @NotBlank(message = "消息内容不能为空")
  @Size(max = 10000, message = "消息内容不能超过10000个字符")
  @Schema(description = "消息内容", example = "请帮我解释什么是多线程？")
  private String message;

  @Schema(description = "对话历史")
  private List<Message> messages;

  @Schema(description = "AI模型类型", example = "gpt-3.5-turbo")
  private String modelType = "gpt-3.5-turbo";

  @Schema(description = "系统提示词", example = "你是一个Java编程专家")
  private String systemPrompt;

  @Schema(description = "温度参数", example = "0.7")
  private Double temperature = 0.7;

  @Schema(description = "最大token数", example = "1000")
  private Integer maxTokens = 1000;

  @Schema(description = "Top P参数", example = "0.9")
  private Double topP = 0.9;

  @Schema(description = "频率惩罚", example = "0.0")
  private Double frequencyPenalty = 0.0;

  @Schema(description = "存在惩罚", example = "0.0")
  private Double presencePenalty = 0.0;

  @Schema(description = "是否流式响应", example = "false")
  private Boolean stream = false;

  @Schema(description = "是否使用ReAct模式", example = "false")
  private Boolean useReAct = false;

  @Data
  public static class Message {
    @Schema(description = "消息角色", example = "user")
    private String role;

    @Schema(description = "消息内容", example = "你好")
    private String content;
  }
}
