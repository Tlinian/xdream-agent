package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "嵌入请求")
public class EmbeddingRequest {

  @NotBlank(message = "文本内容不能为空")
  @Size(max = 8192, message = "文本内容不能超过8192个字符")
  @Schema(description = "文本内容", example = "这是一个需要生成嵌入向量的文本")
  private String text;

  @Schema(description = "模型类型", example = "text-embedding-ada-002")
  private String modelType = "text-embedding-ada-002";

  @Schema(description = "用户标识", example = "user123")
  private String user;
}
