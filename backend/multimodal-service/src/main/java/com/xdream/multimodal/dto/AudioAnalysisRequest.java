package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 音频分析请求DTO */
@Data
@Schema(description = "音频分析请求")
public class AudioAnalysisRequest {

  @NotNull(message = "音频文件不能为空")
  @Schema(description = "音频文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "分析类型",
      example = "BASIC",
      allowableValues = {"BASIC", "DETAILED", "SPEECH", "MUSIC", "NOISE", "QUALITY"})
  private String analysisType = "BASIC";

  @Schema(
      description = "采样率（Hz）",
      example = "16000",
      allowableValues = {"8000", "16000", "22050", "44100", "48000"})
  private Integer sampleRate = 16000;

  @Schema(description = "开始时间（秒）", example = "0.0", minimum = "0.0")
  private Double startTime = 0.0;

  @Schema(description = "结束时间（秒）", example = "-1.0")
  private Double endTime = -1.0; // -1表示分析到音频结束

  @Schema(description = "分段长度（秒）", example = "30.0", minimum = "1.0", maximum = "300.0")
  private Double segmentLength = 30.0;

  @Schema(description = "重叠长度（秒）", example = "5.0", minimum = "0.0", maximum = "30.0")
  private Double overlapLength = 5.0;

  @Schema(description = "特征提取参数")
  private FeatureExtractionParams featureExtraction;

  @Schema(description = "置信度阈值", example = "0.5", minimum = "0.0", maximum = "1.0")
  private Double confidenceThreshold = 0.5;

  @Data
  @Schema(description = "特征提取参数")
  public static class FeatureExtractionParams {
    @Schema(description = "提取MFCC特征", example = "true")
    private Boolean extractMFCC = true;

    @Schema(description = "提取频谱特征", example = "true")
    private Boolean extractSpectral = true;

    @Schema(description = "提取时域特征", example = "true")
    private Boolean extractTemporal = true;

    @Schema(description = "提取音高特征", example = "true")
    private Boolean extractPitch = true;

    @Schema(description = "提取能量特征", example = "true")
    private Boolean extractEnergy = true;

    @Schema(description = "MFCC系数数量", example = "13", minimum = "1", maximum = "40")
    private Integer mfccCoefficients = 13;

    @Schema(description = "频谱带数量", example = "26", minimum = "1", maximum = "128")
    private Integer spectralBands = 26;
  }
}
