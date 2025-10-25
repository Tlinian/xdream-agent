package com.xdream.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Knowledge integration properties for RAG features.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knowledge")
public class KnowledgeIntegrationProperties {

  private boolean enabled = true;
  private String baseUrl;
  private int defaultTopK = 4;
  private double defaultSimilarityThreshold = 0.6;
  private int defaultRerankTopK = 3;
  private boolean appendCitationByDefault = true;
}
