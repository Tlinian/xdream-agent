package com.xdream.llm.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ImageGenerationResponse {

  private String id = UUID.randomUUID().toString(); // 响应ID

  private String modelType; // 使用的模型类型

  private LocalDateTime createdAt = LocalDateTime.now(); // 创建时间

  private List<ImageData> images; // 生成的图片列表

  @Data
  public static class ImageData {
    private String url; // 图片URL
    private String base64; // 图片的base64编码
    private String finishReason; // 生成结束的原因
  }
}
