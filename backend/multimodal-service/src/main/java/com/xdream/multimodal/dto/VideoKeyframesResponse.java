package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 视频关键帧提取响应DTO */
@Data
@Schema(description = "视频关键帧提取响应")
public class VideoKeyframesResponse {

  @Schema(description = "任务ID")
  private String taskId;

  @Schema(description = "视频信息")
  private VideoInfo videoInfo;

  @Schema(description = "提取的关键帧")
  private List<KeyFrame> keyframes;

  @Schema(description = "提取统计信息")
  private ExtractionStats stats;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "使用的算法")
  private String algorithm;

  @Schema(description = "提取参数")
  private ExtractionParameters parameters;

  @Data
  @Schema(description = "视频信息")
  public static class VideoInfo {
    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "格式")
    private String format;

    @Schema(description = "时长（秒）")
    private Double duration;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "帧率")
    private Double frameRate;

    @Schema(description = "总帧数")
    private Long totalFrames;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;
  }

  @Data
  @Schema(description = "关键帧")
  public static class KeyFrame {
    @Schema(description = "帧ID")
    private String frameId;

    @Schema(description = "时间戳（秒）")
    private Double timestamp;

    @Schema(description = "帧号")
    private Long frameNumber;

    @Schema(description = "图像数据（Base64）")
    private String imageData;

    @Schema(description = "图像URL")
    private String imageUrl;

    @Schema(description = "质量分数")
    private Double qualityScore;

    @Schema(description = "重要性分数")
    private Double importanceScore;

    @Schema(description = "差异度分数")
    private Double differenceScore;

    @Schema(description = "帧特征")
    private FrameFeatures features;

    @Schema(description = "提取原因")
    private String extractionReason;
  }

  @Data
  @Schema(description = "帧特征")
  public static class FrameFeatures {
    @Schema(description = "颜色直方图")
    private List<Double> colorHistogram;

    @Schema(description = "边缘特征")
    private List<Double> edgeFeatures;

    @Schema(description = "纹理特征")
    private List<Double> textureFeatures;

    @Schema(description = "运动向量")
    private List<Double> motionVectors;
  }

  @Data
  @Schema(description = "提取统计信息")
  public static class ExtractionStats {
    @Schema(description = "提取的关键帧数量")
    private Integer extractedFrames;

    @Schema(description = "目标帧数量")
    private Integer targetFrames;

    @Schema(description = "实际提取率")
    private Double extractionRate;

    @Schema(description = "平均质量分数")
    private Double averageQualityScore;

    @Schema(description = "平均重要性分数")
    private Double averageImportanceScore;

    @Schema(description = "质量分数分布")
    private ScoreDistribution qualityScoreDistribution;

    @Schema(description = "重要性分数分布")
    private ScoreDistribution importanceScoreDistribution;
  }

  @Data
  @Schema(description = "分数分布")
  public static class ScoreDistribution {
    @Schema(description = "最小值")
    private Double min;

    @Schema(description = "最大值")
    private Double max;

    @Schema(description = "平均值")
    private Double mean;

    @Schema(description = "中位数")
    private Double median;

    @Schema(description = "标准差")
    private Double stdDev;

    @Schema(description = "分位数")
    private Map<String, Double> quantiles;
  }

  @Data
  @Schema(description = "提取参数")
  public static class ExtractionParameters {
    @Schema(description = "提取模式")
    private String mode;

    @Schema(description = "目标帧数量")
    private Integer frameCount;

    @Schema(description = "质量阈值")
    private Double qualityThreshold;

    @Schema(description = "最小帧间隔（秒）")
    private Double minFrameInterval;

    @Schema(description = "目标分辨率")
    private String targetResolution;

    @Schema(description = "输出格式")
    private String outputFormat;

    @Schema(description = "输出质量")
    private Integer outputQuality;
  }
}
