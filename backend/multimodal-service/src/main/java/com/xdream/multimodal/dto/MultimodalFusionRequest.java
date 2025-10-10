package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 多模态内容融合请求DTO */
@Data
@Schema(description = "多模态内容融合请求")
public class MultimodalFusionRequest {

  @NotEmpty(message = "输入内容不能为空")
  @Schema(description = "输入内容列表", required = true)
  private List<@Valid MultimodalContent> contents;

  @Schema(
      description = "融合类型",
      example = "WEIGHTED",
      allowableValues = {"WEIGHTED", "ATTENTION", "CONCATENATION", "ENSEMBLE"})
  private String fusionType = "WEIGHTED";

  @Schema(description = "融合参数")
  private FusionParameters fusionParameters;

  @Schema(
      description = "输出格式",
      example = "JSON",
      allowableValues = {"JSON", "EMBEDDING", "FEATURES", "TEXT"})
  private String outputFormat = "JSON";

  @Schema(description = "输出维度", example = "512", minimum = "1", maximum = "4096")
  private Integer outputDimension = 512;

  @Schema(description = "是否归一化", example = "true")
  private Boolean normalize = true;

  @Schema(
      description = "任务类型",
      example = "CLASSIFICATION",
      allowableValues = {"CLASSIFICATION", "RETRIEVAL", "GENERATION", "ANALYSIS"})
  private String taskType = "CLASSIFICATION";

  @Schema(description = "上下文信息")
  private Map<String, Object> context;

  @Data
  @Schema(description = "多模态内容")
  public static class MultimodalContent {
    @NotNull(message = "内容类型不能为空")
    @Schema(
        description = "内容类型",
        required = true,
        allowableValues = {"TEXT", "IMAGE", "AUDIO", "VIDEO"})
    private String contentType;

    @Schema(description = "文本内容")
    private String text;

    @Schema(description = "图像数据（Base64）")
    private String imageData;

    @Schema(description = "音频数据（Base64）")
    private String audioData;

    @Schema(description = "视频数据（Base64）")
    private String videoData;

    @Schema(description = "内容ID")
    private String contentId;

    @Schema(description = "权重", example = "1.0", minimum = "0.0", maximum = "10.0")
    private Double weight = 1.0;

    @Schema(description = "优先级", example = "1", minimum = "1", maximum = "10")
    private Integer priority = 1;

    @Schema(description = "元数据")
    private Map<String, Object> metadata;
  }

  @Data
  @Schema(description = "融合参数")
  public static class FusionParameters {
    @Schema(description = "权重配置")
    private Map<String, Double> weights;

    @Schema(description = "注意力头数", example = "8", minimum = "1", maximum = "32")
    private Integer attentionHeads = 8;

    @Schema(description = "注意力维度", example = "64", minimum = "1", maximum = "512")
    private Integer attentionDimension = 64;

    @Schema(description = "dropout率", example = "0.1", minimum = "0.0", maximum = "1.0")
    private Double dropout = 0.1;

    @Schema(description = "温度参数", example = "1.0", minimum = "0.1", maximum = "10.0")
    private Double temperature = 1.0;

    @Schema(description = "融合层数", example = "2", minimum = "1", maximum = "10")
    private Integer fusionLayers = 2;

    @Schema(
        description = "激活函数",
        example = "RELU",
        allowableValues = {"RELU", "GELU", "TANH", "SIGMOID"})
    private String activationFunction = "RELU";
  }
}
