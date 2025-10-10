package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像OCR识别请求DTO */
@Data
@Schema(description = "图像OCR识别请求")
public class ImageOcrRequest {

  @NotNull(message = "图像文件不能为空")
  @Schema(description = "图像文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "语言类型",
      example = "zh-CN",
      allowableValues = {"zh-CN", "en-US", "ja-JP", "ko-KR", "fr-FR", "de-DE", "es-ES"})
  private String language = "zh-CN";

  @Schema(
      description = "OCR引擎",
      example = "DEFAULT",
      allowableValues = {"DEFAULT", "TESSERACT", "EASYOCR", "PADDLEOCR"})
  private String engine = "DEFAULT";

  @Schema(
      description = "检测模式",
      example = "AUTO",
      allowableValues = {"AUTO", "FAST", "ACCURATE"})
  private String detectionMode = "AUTO";

  @Schema(description = "是否预处理", example = "true")
  private Boolean preprocess = true;

  @Schema(description = "预处理参数")
  private PreprocessingParams preprocessing;

  @Schema(
      description = "输出格式",
      example = "TEXT",
      allowableValues = {"TEXT", "JSON", "XML", "HOCR"})
  private String outputFormat = "TEXT";

  @Data
  @Schema(description = "预处理参数")
  public static class PreprocessingParams {
    @Schema(description = "是否去噪", example = "true")
    private Boolean denoise = true;

    @Schema(description = "是否二值化", example = "true")
    private Boolean binarize = true;

    @Schema(description = "是否锐化", example = "false")
    private Boolean sharpen = false;

    @Schema(description = "对比度增强", example = "1.0", minimum = "0.1", maximum = "3.0")
    private Double contrast = 1.0;

    @Schema(description = "亮度调整", example = "0.0", minimum = "-1.0", maximum = "1.0")
    private Double brightness = 0.0;
  }
}
