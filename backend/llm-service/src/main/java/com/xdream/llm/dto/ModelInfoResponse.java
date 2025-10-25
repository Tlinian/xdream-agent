package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "模型信息响应")
public class ModelInfoResponse {

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

  @Schema(description = "上下文长度", example = "4096")
  private Integer contextLength;

  @Schema(description = "训练数据截止时间", example = "2023-09")
  private String trainingDataCutoff;

  @Schema(description = "支持的参数")
  private SupportedParameters supportedParameters;

  @Schema(description = "价格信息")
  private Pricing pricing;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  @Schema(description = "更新时间")
  private LocalDateTime updatedAt;

  @Data
  public static class SupportedParameters {
    @Schema(description = "支持温度参数", example = "true")
    private Boolean temperature;

    @Schema(description = "支持top_p参数", example = "true")
    private Boolean topP;

    @Schema(description = "支持频率惩罚", example = "true")
    private Boolean frequencyPenalty;

    @Schema(description = "支持存在惩罚", example = "true")
    private Boolean presencePenalty;

    @Schema(description = "支持最大token数", example = "true")
    private Boolean maxTokens;

    @Schema(description = "支持流式响应", example = "true")
    private Boolean stream;
  }

  @Data
  public static class Pricing {
    @Schema(description = "输入价格（每1K tokens）", example = "0.0015")
    private Double inputPrice;

    @Schema(description = "输出价格（每1K tokens）", example = "0.002")
    private Double outputPrice;

    @Schema(description = "货币单位", example = "USD")
    private String currency;

    @Schema(description = "计费方式", example = "per_token")
    private String billingMethod;
  }
}
