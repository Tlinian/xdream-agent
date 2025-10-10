package com.xdream.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

  private OpenAiConfig openai;
  private AnthropicConfig anthropic;
  private SiliconFlowConfig siliconflow;
  private DefaultConfig defaultConfig;

  @Data
  public static class OpenAiConfig {
    private String apiKey;
    private String baseUrl;
    private int timeout = 30000;
    private int maxRetries = 3;
  }

  @Data
  public static class AnthropicConfig {
    private String apiKey;
    private String baseUrl;
    private int timeout = 30000;
    private int maxRetries = 3;
  }

  @Data
  public static class SiliconFlowConfig {
    private String apiKey;
    private String baseUrl;
    private String interfaceUrl;
    private String model;
    private int maxTokens = 16384;
    private int timeout = 30000;
    private int maxRetries = 3;
  }

  @Data
  public static class DefaultConfig {
    private String chatModel;
    private String embeddingModel;
    private int maxTokens = 1000;
    private double temperature = 0.7;
  }
}
