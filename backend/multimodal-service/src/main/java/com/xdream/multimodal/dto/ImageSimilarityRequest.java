package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像相似度检测请求DTO */
@Data
@Schema(description = "图像相似度检测请求")
public class ImageSimilarityRequest {

  @NotNull(message = "第一张图像文件不能为空")
  @Schema(description = "第一张图像文件", required = true)
  private MultipartFile file1;

  @NotNull(message = "第二张图像文件不能为空")
  @Schema(description = "第二张图像文件", required = true)
  private MultipartFile file2;

  @Schema(
      description = "相似度算法",
      example = "SSIM",
      allowableValues = {"SSIM", "MSE", "PSNR", "HASH", "CNN"})
  private String algorithm = "SSIM";

  @Schema(description = "相似度阈值", example = "0.7", minimum = "0.0", maximum = "1.0")
  private Double similarityThreshold = 0.7;

  @Schema(description = "是否预处理", example = "true")
  private Boolean preprocess = true;

  @Schema(description = "预处理参数")
  private PreprocessingParams preprocessing;

  @Data
  @Schema(description = "预处理参数")
  public static class PreprocessingParams {
    @Schema(description = "是否调整大小", example = "true")
    private Boolean resize = true;

    @Schema(description = "目标宽度")
    private Integer targetWidth = 224;

    @Schema(description = "目标高度")
    private Integer targetHeight = 224;

    @Schema(description = "是否灰度化", example = "false")
    private Boolean grayscale = false;

    @Schema(description = "是否归一化", example = "true")
    private Boolean normalize = true;
  }
}
