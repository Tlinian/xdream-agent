package com.xdream.llm.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ImageGenerationResponse {

  private String id = UUID.randomUUID().toString(); // 鍝嶅簲ID

  private String modelType; // 浣跨敤鐨勬ā鍨嬬被鍨?

  private LocalDateTime createdAt = LocalDateTime.now(); // 鍒涘缓鏃堕棿

  private List<ImageData> images; // 鐢熸垚鐨勫浘鐗囧垪琛?

  @Data
  public static class ImageData {
    private String url; // 鍥剧墖URL
    private String base64; // 鍥剧墖鐨刡ase64缂栫爜
    private String finishReason; // 鐢熸垚缁撴潫鐨勫師鍥?
  }
}
