package com.xdream.llm.knowledge;

import java.util.List;
import lombok.Data;

@Data
public class KnowledgeSearchResponse {

  private List<Item> results;
  private Long totalCount;
  private Long searchTime;
  private String query;
  private Boolean rerankApplied;

  @Data
  public static class Item {
    private String documentId;
    private String knowledgeBaseId;
    private String title;
    private String content;
    private Integer chunkIndex;
    private String category;
    private List<String> tags;
    private Double similarityScore;
    private Double rerankScore;
    private String highlight;
    private String citation;
  }
}
