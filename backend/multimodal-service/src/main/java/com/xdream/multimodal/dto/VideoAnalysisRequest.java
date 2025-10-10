package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 视频分析请求DTO */
@Data
@Schema(description = "视频分析请求")
public class VideoAnalysisRequest {

  @NotNull(message = "视频文件不能为空")
  @Schema(description = "视频文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "分析类型",
      example = "BASIC",
      allowableValues = {"BASIC", "DETAILED", "SCENE", "OBJECT", "MOTION", "AUDIO"})
  private String analysisType = "BASIC";

  @Schema(description = "采样间隔（秒）", example = "1.0", minimum = "0.1", maximum = "60.0")
  private Double samplingInterval = 1.0;

  @Schema(description = "开始时间（秒）", example = "0.0", minimum = "0.0")
  private Double startTime = 0.0;

  @Schema(description = "结束时间（秒）", example = "-1.0")
  private Double endTime = -1.0; // -1表示分析到视频结束

  @Schema(description = "最大帧数", example = "1000", minimum = "1", maximum = "10000")
  private Integer maxFrames = 1000;

  @Schema(description = "目标分辨率")
  private TargetResolution targetResolution;

  @Schema(description = "置信度阈值", example = "0.5", minimum = "0.0", maximum = "1.0")
  private Double confidenceThreshold = 0.5;

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
