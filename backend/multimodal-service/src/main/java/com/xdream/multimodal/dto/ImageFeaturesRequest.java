package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像特征提取请求DTO */
@Data
@Schema(description = "图像特征提取请求")
public class ImageFeaturesRequest {

  @NotNull(message = "图像文件不能为空")
  @Schema(description = "图像文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "特征类型",
      example = "CNN",
      allowableValues = {"CNN", "SIFT", "ORB", "HOG", "COLOR", "TEXTURE"})
  private String featureType = "CNN";

  @Schema(description = "特征维度", example = "512")
  private Integer featureDimension = 512;

  @Schema(description = "是否归一化", example = "true")
  private Boolean normalize = true;

  @Schema(description = "提取区域")
  private ExtractionRegion region;

  @Data
  @Schema(description = "提取区域")
  public static class ExtractionRegion {
    @Schema(description = "左上角X坐标")
    private Integer x;

    @Schema(description = "左上角Y坐标")
    private Integer y;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;
  }
}
