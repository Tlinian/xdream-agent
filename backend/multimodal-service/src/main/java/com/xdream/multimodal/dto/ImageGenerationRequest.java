package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 图像生成请求DTO */
@Data
@Schema(description = "图像生成请求")
public class ImageGenerationRequest {

  @Schema(description = "文本提示")
  private String prompt;

  @Schema(description = "负面提示")
  private String negativePrompt;

  @Schema(description = "图像宽度")
  private Integer width = 512;

  @Schema(description = "图像高度")
  private Integer height = 512;

  @Schema(description = "采样步数")
  private Integer steps = 20;

  @Schema(description = "引导系数")
  private Double guidanceScale = 7.5;

  @Schema(description = "采样器")
  private String sampler = "DDIM";

  @Schema(description = "随机种子")
  private Long seed = -1L;

  @Schema(description = "生成数量")
  private Integer numImages = 1;

  @Schema(description = "参考图像")
  private MultipartFile referenceImage;

  @Schema(description = "参考图像强度")
  private Double referenceImageStrength = 0.5;

  @Schema(description = "生成模型")
  private String model = "stable-diffusion";

  @Schema(description = "风格类型")
  private String style = "realistic";

  @Schema(description = "风格预设")
  private String stylePreset;

  @Schema(description = "生成参数")
  private GenerationParameters parameters;

  @Schema(description = "控制网络参数")
  private ControlNetParameters controlNet;

  @Schema(description = "LoRA模型列表")
  private List<LoRAConfig> loraModels;

  @Schema(description = "输出格式")
  private String outputFormat = "PNG";

  @Schema(description = "输出质量")
  private Integer outputQuality = 95;

  @Data
  @Schema(description = "生成参数")
  public static class GenerationParameters {
    @Schema(description = "CFG缩放")
    private Double cfgScale = 7.5;

    @Schema(description = "去噪强度")
    private Double denoisingStrength = 0.7;

    @Schema(description = "噪声调度器")
    private String noiseSchedule = "linear";

    @Schema(description = "图像到图像强度")
    private Double imageToImageStrength = 0.5;

    @Schema(description = "掩码模糊")
    private Integer maskBlur = 4;

    @Schema(description = "修复模式")
    private String inpaintingMode = "fill";

    @Schema(description = "修复填充")
    private Integer inpaintingFill = 1;

    @Schema(description = "修复遮罩权重")
    private Double inpaintingMaskWeight = 1.0;

    @Schema(description = "高清修复")
    private Boolean highResolutionFix = false;

    @Schema(description = "高清修复放大倍数")
    private Double hrScale = 2.0;

    @Schema(description = "高清修复采样步数")
    private Integer hrSteps = 10;

    @Schema(description = "高清修复引导系数")
    private Double hrGuidanceScale = 7.5;

    @Schema(description = "高清修复重绘幅度")
    private Double hrDenoisingStrength = 0.5;
  }

  @Data
  @Schema(description = "控制网络参数")
  public static class ControlNetParameters {
    @Schema(description = "启用控制网络")
    private Boolean enabled = false;

    @Schema(description = "控制网络模型")
    private String model;

    @Schema(description = "控制图像")
    private MultipartFile controlImage;

    @Schema(description = "控制强度")
    private Double controlWeight = 1.0;

    @Schema(description = "控制开始步数")
    private Double controlStartStep = 0.0;

    @Schema(description = "控制结束步数")
    private Double controlEndStep = 1.0;

    @Schema(description = "控制模式")
    private String controlMode = "balanced";

    @Schema(description = "预处理器")
    private String preprocessor;

    @Schema(description = "预处理器参数")
    private Map<String, Object> preprocessorParams;
  }

  @Data
  @Schema(description = "LoRA配置")
  public static class LoRAConfig {
    @Schema(description = "LoRA模型名称")
    private String name;

    @Schema(description = "LoRA模型文件")
    private MultipartFile modelFile;

    @Schema(description = "LoRA权重")
    private Double weight = 1.0;

    @Schema(description = "触发词")
    private String triggerWord;

    @Schema(description = "LoRA类型")
    private String type = "character";
  }
}
