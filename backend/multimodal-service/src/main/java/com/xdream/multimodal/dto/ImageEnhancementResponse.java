package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 图像增强响应DTO */
@Data
@Schema(description = "图像增强响应")
public class ImageEnhancementResponse {

  @Schema(description = "增强任务ID")
  private String taskId;

  @Schema(description = "增强状态")
  private String status;

  @Schema(description = "原始图像信息")
  private OriginalImageInfo originalImage;

  @Schema(description = "增强后图像信息")
  private EnhancedImageInfo enhancedImage;

  @Schema(description = "增强操作信息")
  private EnhancementOperations operations;

  @Schema(description = "增强统计信息")
  private EnhancementStatistics statistics;

  @Schema(description = "质量评估结果")
  private QualityAssessment qualityAssessment;

  @Schema(description = "处理时间（毫秒）")
  private Long processingTime;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "警告信息")
  private List<String> warnings;

  @Schema(description = "额外信息")
  private Map<String, Object> metadata;

  @Data
  @Schema(description = "原始图像信息")
  public static class OriginalImageInfo {
    @Schema(description = "图像ID")
    private String imageId;

    @Schema(description = "图像URL")
    private String imageUrl;

    @Schema(description = "图像路径")
    private String imagePath;

    @Schema(description = "图像宽度")
    private Integer width;

    @Schema(description = "图像高度")
    private Integer height;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件格式")
    private String format;

    @Schema(description = "色彩模式")
    private String colorMode;

    @Schema(description = "位深度")
    private Integer bitDepth;

    @Schema(description = "DPI")
    private Integer dpi;

    @Schema(description = "原始质量分数")
    private Double originalQualityScore;

    @Schema(description = "噪声水平")
    private Double noiseLevel;

    @Schema(description = "模糊程度")
    private Double blurLevel;

    @Schema(description = "压缩伪影")
    private Double compressionArtifacts;
  }

  @Data
  @Schema(description = "增强后图像信息")
  public static class EnhancedImageInfo {
    @Schema(description = "增强图像ID")
    private String enhancedImageId;

    @Schema(description = "增强图像URL")
    private String enhancedImageUrl;

    @Schema(description = "增强图像路径")
    private String enhancedImagePath;

    @Schema(description = "图像宽度")
    private Integer width;

    @Schema(description = "图像高度")
    private Integer height;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件格式")
    private String format;

    @Schema(description = "色彩模式")
    private String colorMode;

    @Schema(description = "位深度")
    private Integer bitDepth;

    @Schema(description = "DPI")
    private Integer dpi;

    @Schema(description = "增强质量分数")
    private Double enhancedQualityScore;

    @Schema(description = "改进分数")
    private Double improvementScore;

    @Schema(description = "增强级别")
    private String enhancementLevel;

    @Schema(description = "增强效果评分")
    private EnhancementScores enhancementScores;
  }

  @Data
  @Schema(description = "增强评分")
  public static class EnhancementScores {
    @Schema(description = "整体质量改进")
    private Double overallQualityImprovement;

    @Schema(description = "清晰度改进")
    private Double sharpnessImprovement;

    @Schema(description = "对比度改进")
    private Double contrastImprovement;

    @Schema(description = "色彩改进")
    private Double colorImprovement;

    @Schema(description = "细节改进")
    private Double detailImprovement;

    @Schema(description = "噪声减少")
    private Double noiseReduction;

    @Schema(description = "伪影减少")
    private Double artifactReduction;
  }

  @Data
  @Schema(description = "增强操作信息")
  public static class EnhancementOperations {
    @Schema(description = "应用的增强操作")
    private List<String> appliedOperations;

    @Schema(description = "增强操作详情")
    private List<OperationDetail> operationDetails;

    @Schema(description = "增强参数")
    private Map<String, Object> enhancementParameters;

    @Schema(description = "AI模型使用信息")
    private AIModelUsage aiModelUsage;

    @Schema(description = "处理管道")
    private List<String> processingPipeline;
  }

  @Data
  @Schema(description = "操作详情")
  public static class OperationDetail {
    @Schema(description = "操作名称")
    private String operationName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作强度")
    private Double operationStrength;

    @Schema(description = "操作参数")
    private Map<String, Object> parameters;

    @Schema(description = "操作耗时（毫秒）")
    private Long processingTime;

    @Schema(description = "操作效果评分")
    private Double effectivenessScore;
  }

  @Data
  @Schema(description = "AI模型使用信息")
  public static class AIModelUsage {
    @Schema(description = "使用的模型")
    private String modelName;

    @Schema(description = "模型版本")
    private String modelVersion;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "推理时间（毫秒）")
    private Long inferenceTime;

    @Schema(description = "模型置信度")
    private Double modelConfidence;

    @Schema(description = "模型参数")
    private Map<String, Object> modelParameters;
  }

  @Data
  @Schema(description = "增强统计信息")
  public static class EnhancementStatistics {
    @Schema(description = "总处理时间（毫秒）")
    private Long totalProcessingTime;

    @Schema(description = "预处理时间（毫秒）")
    private Long preprocessingTime;

    @Schema(description = "增强处理时间（毫秒）")
    private Long enhancementTime;

    @Schema(description = "后处理时间（毫秒）")
    private Long postProcessingTime;

    @Schema(description = "内存使用量（MB）")
    private Double memoryUsage;

    @Schema(description = "GPU使用量（%）")
    private Double gpuUsage;

    @Schema(description = "CPU使用量（%）")
    private Double cpuUsage;

    @Schema(description = "处理步骤数量")
    private Integer processingSteps;

    @Schema(description = "成功步骤数量")
    private Integer successfulSteps;

    @Schema(description = "失败步骤数量")
    private Integer failedSteps;
  }

  @Data
  @Schema(description = "质量评估结果")
  public static class QualityAssessment {
    @Schema(description = "整体质量分数")
    private Double overallQualityScore;

    @Schema(description = "清晰度分数")
    private Double sharpnessScore;

    @Schema(description = "对比度分数")
    private Double contrastScore;

    @Schema(description = "色彩分数")
    private Double colorScore;

    @Schema(description = "噪声分数")
    private Double noiseScore;

    @Schema(description = "细节分数")
    private Double detailScore;

    @Schema(description = "美学分数")
    private Double aestheticScore;

    @Schema(description = "技术质量分数")
    private Double technicalQualityScore;

    @Schema(description = "感知质量分数")
    private Double perceptualQualityScore;

    @Schema(description = "质量改进指标")
    private QualityImprovement qualityImprovement;
  }

  @Data
  @Schema(description = "质量改进指标")
  public static class QualityImprovement {
    @Schema(description = "整体质量改进")
    private Double overallImprovement;

    @Schema(description = "清晰度改进")
    private Double sharpnessImprovement;

    @Schema(description = "对比度改进")
    private Double contrastImprovement;

    @Schema(description = "色彩改进")
    private Double colorImprovement;

    @Schema(description = "噪声减少")
    private Double noiseReduction;

    @Schema(description = "细节增强")
    private Double detailEnhancement;

    @Schema(description = "感知质量改进")
    private Double perceptualImprovement;
  }
}
