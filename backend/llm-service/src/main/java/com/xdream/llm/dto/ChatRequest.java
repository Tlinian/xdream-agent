package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "\u5bf9\u8bdd\u8bf7\u6c42")
public class ChatRequest {

  @NotBlank(message = "\u6d88\u606f\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a")
  @Size(max = 10000, message = "\u6d88\u606f\u5185\u5bb9\u4e0d\u80fd\u8d85\u8fc710000\u4e2a\u5b57\u7b26")
  @Schema(description = "\u6d88\u606f\u5185\u5bb9", example = "\u8bf7\u5e2e\u6211\u89e3\u91ca\u4ec0\u4e48\u662f\u591a\u7ebf\u7a0b\uff1f")
  private String message;

  @Schema(description = "\u5bf9\u8bdd\u5386\u53f2")
  private List<Message> messages = new ArrayList<>();

  @Schema(description = "AI\u6a21\u578b\u7c7b\u578b", example = "gpt-3.5-turbo")
  private String modelType = "gpt-3.5-turbo";

  @Schema(description = "\u7cfb\u7edf\u63d0\u793a\u8bcd", example = "\u4f60\u662f\u4e00\u4e2aJava\u7f16\u7a0b\u4e13\u5bb6")
  private String systemPrompt;

  @Schema(description = "\u6e29\u5ea6\u53c2\u6570", example = "0.7")
  private Double temperature = 0.7;

  @Schema(description = "\u6700\u5927token\u6570", example = "1000")
  private Integer maxTokens = 1000;

  @Schema(description = "Top P\u53c2\u6570", example = "0.9")
  private Double topP = 0.9;

  @Schema(description = "\u9891\u7387\u60e9\u7f5a", example = "0.0")
  private Double frequencyPenalty = 0.0;

  @Schema(description = "\u5b58\u5728\u60e9\u7f5a", example = "0.0")
  private Double presencePenalty = 0.0;

  @Schema(description = "\u662f\u5426\u6d41\u5f0f\u54cd\u5e94", example = "false")
  private Boolean stream = false;

  @Schema(description = "\u662f\u5426\u4f7f\u7528ReAct\u6a21\u5f0f", example = "false")
  private Boolean useReAct = false;

  @Schema(description = "\u77e5\u8bc6\u68c0\u7d22\u914d\u7f6e")
  private KnowledgeConfig knowledge;

  @Schema(description = "\u68c0\u7d22\u5230\u7684\u77e5\u8bc6\u7247\u6bb5\uff0c\u4ec5\u5185\u90e8\u4f7f\u7528")
  private transient List<KnowledgeSnippet> knowledgeSnippets;

  @Data
  public static class Message {
    @Schema(description = "\u6d88\u606f\u89d2\u8272", example = "user")
    private String role;

    @Schema(description = "\u6d88\u606f\u5185\u5bb9", example = "\u4f60\u597d")
    private String content;
  }

  @Data
  public static class KnowledgeConfig {
    private Boolean enabled;
    private String knowledgeBaseId;
    private Integer topK;
    private Double similarityThreshold;
    private Boolean useRerank;
    private Integer rerankTopK;
    private Boolean appendCitations;
  }

  @Data
  public static class KnowledgeSnippet {
    private String title;
    private String citation;
    private String content;
  }
}
