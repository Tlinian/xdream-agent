package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "模型列表响应")
public class ModelListResponse {

  @Schema(description = "可用模型列表")
  private List<ModelInfo> models;

  @Schema(description = "总数量", example = "5")
  private Integer total;

  @Data
  public static class ModelInfo {
    @Schema(description = "模型ID", example = "gpt-3.5-turbo")
    private String id;

    @Schema(description = "模型名称", example = "GPT-3.5 Turbo")
    private String name;

    @Schema(description = "模型类型", example = "chat")
    private String type;

    @Schema(description = "模型描述", example = "OpenAI GPT-3.5 Turbo模型")
    private String description;

    @Schema(description = "是否可用", example = "true")
    private Boolean available;

    @Schema(description = "最大token数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "价格信息")
    private Pricing pricing;
  }

  @Data
  public static class Pricing {
    @Schema(description = "输入价格（每1K tokens）", example = "0.0015")
    private Double inputPrice;

    @Schema(description = "输出价格（每1K tokens）", example = "0.002")
    private Double outputPrice;

    @Schema(description = "货币单位", example = "USD")
    private String currency;
  }
}
