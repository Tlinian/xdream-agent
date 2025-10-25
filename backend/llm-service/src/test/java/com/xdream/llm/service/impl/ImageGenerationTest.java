package com.xdream.llm.service.impl;

import com.xdream.llm.dto.ImageGenerationRequest;
import com.xdream.llm.dto.ImageGenerationResponse;
import com.xdream.llm.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ImageGenerationTest {

  @Autowired private LlmService llmService;

  @Test
  void testGenerateImage() {
    // 鍒涘缓鍥剧墖鐢熸垚璇锋眰
    ImageGenerationRequest request = new ImageGenerationRequest();
    request.setPrompt(
        "an island near sea, with seagulls, moon shining over the sea, light house, boats int he background, fish flying over the sea");
    request.setModel("Qwen/Qwen-Image-Edit-2509");
    request.setN(1);
    request.setSize("1024x1024");
    request.setResponseFormat("url");

    try {
      // 璋冪敤鍥剧墖鐢熸垚鏈嶅姟
      String userId = "test-user-001";
      ImageGenerationResponse response = llmService.generateImage(userId, request);

      // 杈撳嚭缁撴灉
      log.info("鍥剧墖鐢熸垚鎴愬姛锛屾ā鍨嬬被鍨? {}", response.getModelType());
      log.info("鍒涘缓鏃堕棿: {}", response.getCreatedAt());

      if (response.getImages() != null && !response.getImages().isEmpty()) {
        for (int i = 0; i < response.getImages().size(); i++) {
          ImageGenerationResponse.ImageData imageData = response.getImages().get(i);
          log.info("绗瑊}寮犲浘鐗嘦RL: {}", i + 1, imageData.getUrl());
          log.info("缁撴潫鍘熷洜: {}", imageData.getFinishReason());
        }
      }

    } catch (Exception e) {
      log.error("鍥剧墖鐢熸垚澶辫触: {}", e.getMessage(), e);
    }
  }
}
