package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像分析请求DTO */
@Data
@Schema(description = "图像分析请求")
public class ImageAnalysisRequest {

  @NotNull(message = "图像文件不能为空")
  @Schema(description = "图像文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "分析类型",
      example = "ALL",
      allowableValues = {"ALL", "OBJECTS", "SCENE", "TEXT", "FACE", "COLOR"})
  private String analysisType = "ALL";

  @Schema(description = "置信度阈值", example = "0.5", minimum = "0.0", maximum = "1.0")
  private Double confidenceThreshold = 0.5;

  @Schema(description = "最大检测数量", example = "100", minimum = "1", maximum = "1000")
  private Integer maxDetections = 100;
}
