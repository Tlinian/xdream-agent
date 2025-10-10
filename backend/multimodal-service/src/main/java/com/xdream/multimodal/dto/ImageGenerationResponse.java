package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 图像生成响应DTO */
@Data
@Schema(description = "图像生成响应")
public class ImageGenerationResponse {

  @Schema(description = "生成任务ID")
  private String taskId;

  @Schema(description = "生成状态")
  private String status;

  @Schema(description = "生成的图像列表")
  private List<GeneratedImage> images;

  @Schema(description = "生成参数")
  private GenerationInfo generationInfo;

  @Schema(description = "模型信息")
  private ModelInfo modelInfo;

  @Schema(description = "生成统计")
  private GenerationStatistics statistics;

  @Schema(description = "处理时间")
  private Long processingTime;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "警告信息")
  private List<String> warnings;

  @Data
  @Schema(description = "生成的图像")
  public static class GeneratedImage {
    @Schema(description = "图像ID")
    private String imageId;

    @Schema(description = "图像URL")
    private String imageUrl;

    @Schema(description = "图像Base64")
    private String imageBase64;

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

    @Schema(description = "生成参数")
    private Map<String, Object> parameters;

    @Schema(description = "图像质量分数")
    private Double qualityScore;

    @Schema(description = "美学分数")
    private Double aestheticScore;

    @Schema(description = "技术质量分数")
    private Double technicalScore;

    @Schema(description = "内容安全分数")
    private Double safetyScore;

    @Schema(description = "生成时间")
    private LocalDateTime generationTime;

    @Schema(description = "种子值")
    private Long seed;

    @Schema(description = "使用的LoRA模型")
    private List<String> usedLoraModels;

    @Schema(description = "控制网络信息")
    private ControlNetInfo controlNetInfo;

    @Schema(description = "后处理应用")
    private List<String> postProcessingApplied;
  }

  @Data
  @Schema(description = "生成信息")
  public static class GenerationInfo {
    @Schema(description = "原始提示词")
    private String originalPrompt;

    @Schema(description = "处理后的提示词")
    private String processedPrompt;

    @Schema(description = "负面提示词")
    private String negativePrompt;

    @Schema(description = "采样步数")
    private Integer steps;

    @Schema(description = "引导系数")
    private Double guidanceScale;

    @Schema(description = "采样器")
    private String sampler;

    @Schema(description = "图像尺寸")
    private ImageSize imageSize;

    @Schema(description = "生成数量")
    private Integer numImages;

    @Schema(description = "风格类型")
    private String style;

    @Schema(description = "风格预设")
    private String stylePreset;
  }

  @Data
  @Schema(description = "图像尺寸")
  public static class ImageSize {
    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;
  }

  @Data
  @Schema(description = "模型信息")
  public static class ModelInfo {
    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型版本")
    private String modelVersion;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "模型哈希")
    private String modelHash;

    @Schema(description = "模型大小")
    private Long modelSize;

    @Schema(description = "模型加载时间")
    private Long modelLoadTime;

    @Schema(description = "使用的VAE")
    private String vaeUsed;

    @Schema(description = "使用的CLIP模型")
    private String clipModel;
  }

  @Data
  @Schema(description = "生成统计")
  public static class GenerationStatistics {
    @Schema(description = "总生成时间（毫秒）")
    private Long totalGenerationTime;

    @Schema(description = "平均单张图像生成时间（毫秒）")
    private Long averageImageGenerationTime;

    @Schema(description = "模型推理时间（毫秒）")
    private Long modelInferenceTime;

    @Schema(description = "后处理时间（毫秒）")
    private Long postProcessingTime;

    @Schema(description = "内存使用量（MB）")
    private Double memoryUsage;

    @Schema(description = "GPU使用量（%）")
    private Double gpuUsage;

    @Schema(description = "CPU使用量（%）")
    private Double cpuUsage;

    @Schema(description = "平均质量分数")
    private Double averageQualityScore;

    @Schema(description = "平均美学分数")
    private Double averageAestheticScore;

    @Schema(description = "成功生成数量")
    private Integer successfulGenerations;

    @Schema(description = "失败生成数量")
    private Integer failedGenerations;
  }

  @Data
  @Schema(description = "控制网络信息")
  public static class ControlNetInfo {
    @Schema(description = "是否使用控制网络")
    private Boolean controlNetUsed;

    @Schema(description = "控制网络模型")
    private String controlNetModel;

    @Schema(description = "控制强度")
    private Double controlWeight;

    @Schema(description = "控制模式")
    private String controlMode;

    @Schema(description = "预处理器")
    private String preprocessor;
  }
}
