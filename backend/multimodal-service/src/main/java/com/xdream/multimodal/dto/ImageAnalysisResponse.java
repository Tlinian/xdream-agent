package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 图像分析响应DTO */
@Data
@Schema(description = "图像分析响应")
public class ImageAnalysisResponse {

  @Schema(description = "图像ID")
  private String imageId;

  @Schema(description = "图像尺寸")
  private ImageSize size;

  @Schema(description = "检测到的对象列表")
  private List<DetectedObject> objects;

  @Schema(description = "场景信息")
  private SceneInfo scene;

  @Schema(description = "颜色信息")
  private ColorInfo colors;

  @Schema(description = "文本信息")
  private TextInfo text;

  @Schema(description = "人脸信息")
  private FaceInfo faces;

  @Schema(description = "分析耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "置信度分数")
  private Double confidenceScore;

  @Data
  @Schema(description = "图像尺寸")
  public static class ImageSize {
    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "通道数")
    private Integer channels;
  }

  @Data
  @Schema(description = "检测到的对象")
  public static class DetectedObject {
    @Schema(description = "对象类别")
    private String category;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;

    @Schema(description = "属性信息")
    private Map<String, Object> attributes;
  }

  @Data
  @Schema(description = "边界框")
  public static class BoundingBox {
    @Schema(description = "左上角X坐标")
    private Double x;

    @Schema(description = "左上角Y坐标")
    private Double y;

    @Schema(description = "宽度")
    private Double width;

    @Schema(description = "高度")
    private Double height;
  }

  @Data
  @Schema(description = "场景信息")
  public static class SceneInfo {
    @Schema(description = "场景类别")
    private String category;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "场景描述")
    private String description;
  }

  @Data
  @Schema(description = "颜色信息")
  public static class ColorInfo {
    @Schema(description = "主色调列表")
    private List<ColorItem> dominantColors;

    @Schema(description = "颜色分布")
    private Map<String, Double> colorDistribution;
  }

  @Data
  @Schema(description = "颜色项")
  public static class ColorItem {
    @Schema(description = "颜色值（十六进制）")
    private String hex;

    @Schema(description = "RGB值")
    private List<Integer> rgb;

    @Schema(description = "占比")
    private Double percentage;
  }

  @Data
  @Schema(description = "文本信息")
  public static class TextInfo {
    @Schema(description = "检测到的文本")
    private List<TextItem> texts;

    @Schema(description = "总文本数量")
    private Integer totalTexts;
  }

  @Data
  @Schema(description = "文本项")
  public static class TextItem {
    @Schema(description = "文本内容")
    private String content;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;

    @Schema(description = "语言")
    private String language;
  }

  @Data
  @Schema(description = "人脸信息")
  public static class FaceInfo {
    @Schema(description = "检测到的人脸")
    private List<FaceItem> faces;

    @Schema(description = "总人脸数量")
    private Integer totalFaces;
  }

  @Data
  @Schema(description = "人脸项")
  public static class FaceItem {
    @Schema(description = "人脸ID")
    private String faceId;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;

    @Schema(description = "属性信息")
    private Map<String, Object> attributes;
  }
}
