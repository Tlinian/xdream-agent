package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 图像特征提取响应DTO */
@Data
@Schema(description = "图像特征提取响应")
public class ImageFeaturesResponse {

  @Schema(description = "特征ID")
  private String featureId;

  @Schema(description = "特征类型")
  private String featureType;

  @Schema(description = "特征向量")
  private List<Double> features;

  @Schema(description = "特征维度")
  private Integer dimension;

  @Schema(description = "特征统计信息")
  private FeatureStatistics statistics;

  @Schema(description = "特征描述信息")
  private FeatureDescription description;

  @Schema(description = "提取耗时（毫秒）")
  private Long extractionTime;

  @Schema(description = "图像信息")
  private ImageInfo image;

  @Data
  @Schema(description = "特征统计信息")
  public static class FeatureStatistics {
    @Schema(description = "平均值")
    private Double mean;

    @Schema(description = "标准差")
    private Double stdDev;

    @Schema(description = "最小值")
    private Double min;

    @Schema(description = "最大值")
    private Double max;

    @Schema(description = "L2范数")
    private Double l2Norm;
  }

  @Data
  @Schema(description = "特征描述信息")
  public static class FeatureDescription {
    @Schema(description = "特征重要性分数")
    private Map<String, Double> importanceScores;

    @Schema(description = "特征标签")
    private List<String> labels;

    @Schema(description = "特征类别")
    private List<String> categories;
  }

  @Data
  @Schema(description = "图像信息")
  public static class ImageInfo {
    @Schema(description = "图像ID")
    private String imageId;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "通道数")
    private Integer channels;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;
  }
}
