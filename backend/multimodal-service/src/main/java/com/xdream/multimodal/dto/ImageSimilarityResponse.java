package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

/** 图像相似度检测响应DTO */
@Data
@Schema(description = "图像相似度检测响应")
public class ImageSimilarityResponse {

  @Schema(description = "相似度分数（0-1）")
  private Double similarityScore;

  @Schema(description = "是否相似")
  private Boolean isSimilar;

  @Schema(description = "使用的算法")
  private String algorithm;

  @Schema(description = "算法详细信息")
  private AlgorithmInfo algorithmInfo;

  @Schema(description = "图像信息")
  private ImageInfo image1;

  @Schema(description = "图像信息")
  private ImageInfo image2;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "额外信息")
  private Map<String, Object> metadata;

  @Data
  @Schema(description = "算法信息")
  public static class AlgorithmInfo {
    @Schema(description = "算法名称")
    private String name;

    @Schema(description = "算法版本")
    private String version;

    @Schema(description = "算法参数")
    private Map<String, Object> parameters;

    @Schema(description = "算法描述")
    private String description;
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

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "格式")
    private String format;
  }
}
