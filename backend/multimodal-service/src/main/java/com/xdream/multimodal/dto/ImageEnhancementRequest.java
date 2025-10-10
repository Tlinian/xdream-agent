package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像增强请求DTO */
@Data
@Schema(description = "图像增强请求")
public class ImageEnhancementRequest {

  @Schema(description = "输入图像文件", required = true)
  private MultipartFile image;

  @Schema(description = "增强类型")
  private String enhancementType = "general";

  @Schema(description = "增强强度")
  private Double enhancementStrength = 1.0;

  @Schema(description = "目标宽度")
  private Integer targetWidth;

  @Schema(description = "目标高度")
  private Integer targetHeight;

  @Schema(description = "保持宽高比")
  private Boolean maintainAspectRatio = true;

  @Schema(description = "增强参数")
  private EnhancementParameters parameters;

  @Schema(description = "超分辨率参数")
  private SuperResolutionParameters superResolution;

  @Schema(description = "去噪参数")
  private DenoiseParameters denoise;

  @Schema(description = "锐化参数")
  private SharpenParameters sharpen;

  @Schema(description = "色彩调整参数")
  private ColorAdjustmentParameters colorAdjustment;

  @Schema(description = "对比度参数")
  private ContrastParameters contrast;

  @Schema(description = "亮度参数")
  private BrightnessParameters brightness;

  @Schema(description = "饱和度参数")
  private SaturationParameters saturation;

  @Schema(description = "输出格式")
  private String outputFormat = "JPEG";

  @Schema(description = "输出质量")
  private Integer outputQuality = 95;

  @Schema(description = "输出模式")
  private String outputMode = "enhanced";

  @Data
  @Schema(description = "增强参数")
  public static class EnhancementParameters {
    @Schema(description = "自动增强")
    private Boolean autoEnhance = true;

    @Schema(description = "AI增强")
    private Boolean aiEnhance = false;

    @Schema(description = "面部增强")
    private Boolean faceEnhance = false;

    @Schema(description = "细节增强")
    private Boolean detailEnhance = true;

    @Schema(description = "纹理增强")
    private Boolean textureEnhance = false;

    @Schema(description = "边缘增强")
    private Boolean edgeEnhance = false;

    @Schema(description = "噪声抑制")
    private Boolean noiseSuppression = true;

    @Schema(description = "压缩伪影去除")
    private Boolean compressionArtifactRemoval = true;

    @Schema(description = "增强算法")
    private String algorithm = "default";

    @Schema(description = "处理模式")
    private String processingMode = "balanced";
  }

  @Data
  @Schema(description = "超分辨率参数")
  public static class SuperResolutionParameters {
    @Schema(description = "启用超分辨率")
    private Boolean enabled = false;

    @Schema(description = "放大倍数")
    private Double scale = 2.0;

    @Schema(description = "目标宽度")
    private Integer targetWidth;

    @Schema(description = "目标高度")
    private Integer targetHeight;

    @Schema(description = "超分辨率模型")
    private String model = "Real-ESRGAN";

    @Schema(description = "模型权重")
    private Double modelWeight = 1.0;

    @Schema(description = "细节增强")
    private Boolean detailEnhancement = true;

    @Schema(description = "纹理增强")
    private Boolean textureEnhancement = false;

    @Schema(description = "边缘保持")
    private Boolean edgePreservation = true;

    @Schema(description = "噪声抑制")
    private Boolean noiseSuppression = true;

    @Schema(description = "处理模式")
    private String processingMode = "quality";
  }

  @Data
  @Schema(description = "去噪参数")
  public static class DenoiseParameters {
    @Schema(description = "启用去噪")
    private Boolean enabled = false;

    @Schema(description = "去噪强度")
    private Double strength = 0.5;

    @Schema(description = "去噪算法")
    private String algorithm = "NLM";

    @Schema(description = "滤波器大小")
    private Integer filterSize = 7;

    @Schema(description = "搜索窗口大小")
    private Integer searchWindowSize = 21;

    @Schema(description = "噪声水平")
    private Double noiseLevel = 10.0;

    @Schema(description = "细节保持")
    private Boolean preserveDetails = true;
  }

  @Data
  @Schema(description = "锐化参数")
  public static class SharpenParameters {
    @Schema(description = "启用锐化")
    private Boolean enabled = false;

    @Schema(description = "锐化强度")
    private Double strength = 0.5;

    @Schema(description = "锐化算法")
    private String algorithm = "unsharp_mask";

    @Schema(description = "半径")
    private Double radius = 1.0;

    @Schema(description = "阈值")
    private Double threshold = 0.0;

    @Schema(description = "边缘保护")
    private Boolean edgeProtection = true;

    @Schema(description = "噪声抑制")
    private Boolean noiseSuppression = true;
  }

  @Data
  @Schema(description = "色彩调整参数")
  public static class ColorAdjustmentParameters {
    @Schema(description = "启用色彩调整")
    private Boolean enabled = false;

    @Schema(description = "色温调整")
    private Double temperature = 0.0;

    @Schema(description = "色调调整")
    private Double tint = 0.0;

    @Schema(description = "色彩平衡")
    private ColorBalance colorBalance;

    @Schema(description = "色彩校正")
    private Boolean colorCorrection = false;

    @Schema(description = "白平衡")
    private String whiteBalance = "auto";

    @Schema(description = "色彩空间")
    private String colorSpace = "sRGB";
  }

  @Data
  @Schema(description = "色彩平衡")
  public static class ColorBalance {
    @Schema(description = "红色增益")
    private Double redGain = 1.0;

    @Schema(description = "绿色增益")
    private Double greenGain = 1.0;

    @Schema(description = "蓝色增益")
    private Double blueGain = 1.0;

    @Schema(description = "阴影调整")
    private ColorAdjustment shadows;

    @Schema(description = "中间调调整")
    private ColorAdjustment midtones;

    @Schema(description = "高光调整")
    private ColorAdjustment highlights;
  }

  @Data
  @Schema(description = "色彩调整")
  public static class ColorAdjustment {
    @Schema(description = "青色-红色")
    private Double cyanRed = 0.0;

    @Schema(description = "洋红-绿色")
    private Double magentaGreen = 0.0;

    @Schema(description = "黄色-蓝色")
    private Double yellowBlue = 0.0;
  }

  @Data
  @Schema(description = "对比度参数")
  public static class ContrastParameters {
    @Schema(description = "启用对比度调整")
    private Boolean enabled = false;

    @Schema(description = "对比度值")
    private Double contrast = 1.0;

    @Schema(description = "对比度算法")
    private String algorithm = "linear";

    @Schema(description = "对比度增强")
    private Double contrastEnhancement = 0.0;

    @Schema(description = "自动对比度")
    private Boolean autoContrast = false;

    @Schema(description = "对比度限制")
    private Double contrastLimit = 0.0;

    @Schema(description = "局部对比度")
    private Boolean localContrast = false;

    @Schema(description = "局部对比度半径")
    private Integer localContrastRadius = 5;
  }

  @Data
  @Schema(description = "亮度参数")
  public static class BrightnessParameters {
    @Schema(description = "启用亮度调整")
    private Boolean enabled = false;

    @Schema(description = "亮度值")
    private Double brightness = 0.0;

    @Schema(description = "亮度算法")
    private String algorithm = "linear";

    @Schema(description = "自动亮度")
    private Boolean autoBrightness = false;

    @Schema(description = "亮度校正")
    private Boolean brightnessCorrection = false;

    @Schema(description = "曝光补偿")
    private Double exposureCompensation = 0.0;

    @Schema(description = "伽马校正")
    private Double gammaCorrection = 1.0;
  }

  @Data
  @Schema(description = "饱和度参数")
  public static class SaturationParameters {
    @Schema(description = "启用饱和度调整")
    private Boolean enabled = false;

    @Schema(description = "饱和度值")
    private Double saturation = 1.0;

    @Schema(description = "饱和度算法")
    private String algorithm = "linear";

    @Schema(description = "自然饱和度")
    private Double vibrance = 0.0;

    @Schema(description = "自动饱和度")
    private Boolean autoSaturation = false;

    @Schema(description = "饱和度增强")
    private Double saturationEnhancement = 0.0;

    @Schema(description = "局部饱和度")
    private Boolean localSaturation = false;

    @Schema(description = "饱和度限制")
    private Double saturationLimit = 0.0;
  }
}
