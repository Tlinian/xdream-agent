package com.xdream.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
  private ChatProperties chat;
  private TextToImageProperties textToImage;

  @Data
  public static class ChatProperties {
    private String model;
    private Integer maxTokens;
    private Double temperature;
    private String baseUrl;
    private String interfaceUrl;
    private String apiKey;
    private Integer timeout;
  }

  @Data
  public static class TextToImageProperties {
    private String model;
    private String baseUrl;
    private String interfaceUrl;
    private String apiKey;
    private Integer timeout;
    private Integer maxTokens;
  }
}
