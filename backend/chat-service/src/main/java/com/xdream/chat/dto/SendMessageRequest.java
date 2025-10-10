package com.xdream.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "发送消息请求")
public class SendMessageRequest {

  @NotBlank(message = "消息内容不能为空")
  @Size(max = 10000, message = "消息内容不能超过10000个字符")
  @Schema(description = "消息内容", example = "请帮我解释什么是多线程？")
  private String content;

  @Schema(description = "消息类型", example = "TEXT")
  private String messageType;

  @Schema(description = "父消息ID（用于回复）", example = "")
  private String parentMessageId;

  @Schema(description = "是否流式响应", example = "false")
  private Boolean stream = false;
}
