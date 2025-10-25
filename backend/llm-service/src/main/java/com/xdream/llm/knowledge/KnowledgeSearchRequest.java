package com.xdream.llm.knowledge;

import lombok.Data;

@Data
public class KnowledgeSearchRequest {
  private String query;
  private Integer topK;
  private Double similarityThreshold;
  private Boolean useRerank;
  private Integer rerankTopK;
  private Boolean appendCitations;
}
