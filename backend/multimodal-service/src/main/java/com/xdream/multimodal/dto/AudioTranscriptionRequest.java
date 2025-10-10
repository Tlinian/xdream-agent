package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 音频转文本请求DTO */
@Data
@Schema(description = "音频转文本请求")
public class AudioTranscriptionRequest {

  @NotNull(message = "音频文件不能为空")
  @Schema(description = "音频文件", required = true)
  private MultipartFile file;

  @Schema(
      description = "语言类型",
      example = "zh-CN",
      allowableValues = {"zh-CN", "en-US", "ja-JP", "ko-KR", "fr-FR", "de-DE", "es-ES", "auto"})
  private String language = "zh-CN";

  @Schema(
      description = "转录引擎",
      example = "DEFAULT",
      allowableValues = {"DEFAULT", "WHISPER", "GOOGLE", "BAIDU", "TENCENT"})
  private String engine = "DEFAULT";

  @Schema(
      description = "输出格式",
      example = "TEXT",
      allowableValues = {"TEXT", "JSON", "SRT", "VTT", "ASS"})
  private String outputFormat = "TEXT";

  @Schema(description = "是否包含时间戳", example = "true")
  private Boolean includeTimestamps = true;

  @Schema(description = "是否包含置信度", example = "true")
  private Boolean includeConfidence = true;

  @Schema(description = "说话人分离", example = "false")
  private Boolean speakerDiarization = false;

  @Schema(description = "最大说话人数量", example = "10", minimum = "1", maximum = "100")
  private Integer maxSpeakers = 10;

  @Schema(description = "分段长度（秒）", example = "30.0", minimum = "1.0", maximum = "300.0")
  private Double segmentLength = 30.0;

  @Schema(description = "重叠长度（秒）", example = "5.0", minimum = "0.0", maximum = "30.0")
  private Double overlapLength = 5.0;

  @Schema(description = "预处理参数")
  private PreprocessingParams preprocessing;

  @Schema(description = "后处理参数")
  private PostprocessingParams postprocessing;

  @Data
  @Schema(description = "预处理参数")
  public static class PreprocessingParams {
    @Schema(description = "是否降噪", example = "true")
    private Boolean noiseReduction = true;

    @Schema(description = "是否音量归一化", example = "true")
    private Boolean volumeNormalization = true;

    @Schema(description = "是否去除静音", example = "false")
    private Boolean silenceRemoval = false;

    @Schema(description = "静音阈值（dB）", example = "-40.0", minimum = "-60.0", maximum = "-10.0")
    private Double silenceThreshold = -40.0;

    @Schema(description = "最小静音长度（秒）", example = "0.5", minimum = "0.1", maximum = "5.0")
    private Double minSilenceLength = 0.5;
  }

  @Data
  @Schema(description = "后处理参数")
  public static class PostprocessingParams {
    @Schema(description = "是否标点符号", example = "true")
    private Boolean punctuation = true;

    @Schema(description = "是否文本格式化", example = "true")
    private Boolean textFormatting = true;

    @Schema(description = "是否语言检测", example = "true")
    private Boolean languageDetection = true;

    @Schema(description = "是否关键词提取", example = "false")
    private Boolean keywordExtraction = false;

    @Schema(description = "是否摘要生成", example = "false")
    private Boolean summaryGeneration = false;
  }
}
