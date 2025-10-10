package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 视频关键帧提取请求DTO */
@Data
@Schema(description = "视频关键帧提取请求")
public class VideoKeyframesRequest {

  @NotNull(message = "视频文件不能为空")
  @Schema(description = "视频文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "提取模式",
      example = "AUTO",
      allowableValues = {"AUTO", "UNIFORM", "SHOT_BASED", "MOTION_BASED", "QUALITY_BASED"})
  private String mode = "AUTO";

  @Schema(description = "帧数量", example = "10", minimum = "1", maximum = "1000")
  private Integer frameCount = 10;

  @Schema(description = "开始时间（秒）", example = "0.0", minimum = "0.0")
  private Double startTime = 0.0;

  @Schema(description = "结束时间（秒）", example = "-1.0")
  private Double endTime = -1.0; // -1表示提取到视频结束

  @Schema(description = "最小帧间隔（秒）", example = "0.5", minimum = "0.1", maximum = "60.0")
  private Double minFrameInterval = 0.5;

  @Schema(description = "质量阈值", example = "0.7", minimum = "0.0", maximum = "1.0")
  private Double qualityThreshold = 0.7;

  @Schema(description = "目标分辨率")
  private TargetResolution targetResolution;

  @Schema(
      description = "输出格式",
      example = "JPEG",
      allowableValues = {"JPEG", "PNG", "WEBP"})
  private String outputFormat = "JPEG";

  @Schema(description = "输出质量", example = "85", minimum = "1", maximum = "100")
  private Integer outputQuality = 85;

  @Data
  @Schema(description = "目标分辨率")
  public static class TargetResolution {
    @Schema(description = "宽度")
    private Integer width = 640;

    @Schema(description = "高度")
    private Integer height = 480;

    @Schema(description = "保持宽高比", example = "true")
    private Boolean keepAspectRatio = true;
  }
}
