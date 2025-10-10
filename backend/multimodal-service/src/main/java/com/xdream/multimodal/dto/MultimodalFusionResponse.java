package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 多模态内容融合响应DTO */
@Data
@Schema(description = "多模态内容融合响应")
public class MultimodalFusionResponse {

  @Schema(description = "融合结果ID")
  private String fusionId;

  @Schema(description = "融合结果")
  private FusionResult result;

  @Schema(description = "融合特征")
  private FusionFeatures features;

  @Schema(description = "输入内容信息")
  private List<InputContentInfo> inputContents;

  @Schema(description = "融合统计信息")
  private FusionStatistics statistics;

  @Schema(description = "置信度信息")
  private ConfidenceInfo confidence;

  @Schema(description = "任务信息")
  private TaskInfo taskInfo;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "额外信息")
  private Map<String, Object> metadata;

  @Data
  @Schema(description = "融合结果")
  public static class FusionResult {
    @Schema(description = "融合向量")
    private List<Double> embedding;

    @Schema(description = "融合文本")
    private String text;

    @Schema(description = "融合特征")
    private Map<String, Object> features;

    @Schema(description = "预测标签")
    private List<String> predictedLabels;

    @Schema(description = "预测概率")
    private Map<String, Double> predictedProbabilities;

    @Schema(description = "相似度分数")
    private Map<String, Double> similarityScores;
  }

  @Data
  @Schema(description = "融合特征")
  public static class FusionFeatures {
    @Schema(description = "文本特征")
    private List<Double> textFeatures;

    @Schema(description = "图像特征")
    private List<Double> imageFeatures;

    @Schema(description = "音频特征")
    private List<Double> audioFeatures;

    @Schema(description = "视频特征")
    private List<Double> videoFeatures;

    @Schema(description = "融合权重")
    private Map<String, Double> fusionWeights;

    @Schema(description = "特征重要性")
    private Map<String, Double> featureImportance;
  }

  @Data
  @Schema(description = "输入内容信息")
  public static class InputContentInfo {
    @Schema(description = "内容ID")
    private String contentId;

    @Schema(description = "内容类型")
    private String contentType;

    @Schema(description = "内容权重")
    private Double weight;

    @Schema(description = "内容优先级")
    private Integer priority;

    @Schema(description = "内容质量分数")
    private Double qualityScore;

    @Schema(description = "内容特征")
    private Map<String, Object> contentFeatures;

    @Schema(description = "处理状态")
    private String processingStatus;

    @Schema(description = "错误信息")
    private String errorMessage;
  }

  @Data
  @Schema(description = "融合统计信息")
  public static class FusionStatistics {
    @Schema(description = "输入内容数量")
    private Integer inputContentCount;

    @Schema(description = "成功处理的内容数量")
    private Integer successfulContentCount;

    @Schema(description = "失败的内容数量")
    private Integer failedContentCount;

    @Schema(description = "融合类型")
    private String fusionType;

    @Schema(description = "输出维度")
    private Integer outputDimension;

    @Schema(description = "特征提取耗时（毫秒）")
    private Long featureExtractionTime;

    @Schema(description = "融合计算耗时（毫秒）")
    private Long fusionComputationTime;

    @Schema(description = "总处理耗时（毫秒）")
    private Long totalProcessingTime;

    @Schema(description = "内存使用量（MB）")
    private Double memoryUsage;
  }

  @Data
  @Schema(description = "置信度信息")
  public static class ConfidenceInfo {
    @Schema(description = "整体置信度")
    private Double overallConfidence;

    @Schema(description = "内容类型置信度")
    private Map<String, Double> contentTypeConfidence;

    @Schema(description = "融合质量置信度")
    private Double fusionQualityConfidence;

    @Schema(description = "预测置信度")
    private Double predictionConfidence;

    @Schema(description = "可靠性指标")
    private ReliabilityMetrics reliability;
  }

  @Data
  @Schema(description = "可靠性指标")
  public static class ReliabilityMetrics {
    @Schema(description = "稳定性分数")
    private Double stabilityScore;

    @Schema(description = "一致性分数")
    private Double consistencyScore;

    @Schema(description = "鲁棒性分数")
    private Double robustnessScore;

    @Schema(description = "可解释性分数")
    private Double interpretabilityScore;
  }

  @Data
  @Schema(description = "任务信息")
  public static class TaskInfo {
    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务状态")
    private String taskStatus;

    @Schema(description = "任务开始时间")
    private String startTime;

    @Schema(description = "任务结束时间")
    private String endTime;

    @Schema(description = "任务耗时（毫秒）")
    private Long taskDuration;

    @Schema(description = "使用的模型")
    private String modelUsed;

    @Schema(description = "模型版本")
    private String modelVersion;

    @Schema(description = "模型参数")
    private Map<String, Object> modelParameters;
  }
}
