package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 音频转文本响应DTO */
@Data
@Schema(description = "音频转文本响应")
public class AudioTranscriptionResponse {

  @Schema(description = "转录结果ID")
  private String resultId;

  @Schema(description = "转录文本")
  private String text;

  @Schema(description = "转录段落")
  private List<TranscriptionSegment> segments;

  @Schema(description = "说话人信息")
  private List<SpeakerInfo> speakers;

  @Schema(description = "语言信息")
  private LanguageInfo language;

  @Schema(description = "转录统计")
  private TranscriptionStats stats;

  @Schema(description = "置信度信息")
  private ConfidenceInfo confidence;

  @Schema(description = "音频信息")
  private AudioInfo audio;

  @Schema(description = "转录引擎信息")
  private EngineInfo engine;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "额外信息")
  private Map<String, Object> metadata;

  @Data
  @Schema(description = "转录段落")
  public static class TranscriptionSegment {
    @Schema(description = "段落ID")
    private String segmentId;

    @Schema(description = "开始时间（秒）")
    private Double startTime;

    @Schema(description = "结束时间（秒）")
    private Double endTime;

    @Schema(description = "持续时间（秒）")
    private Double duration;

    @Schema(description = "文本内容")
    private String text;

    @Schema(description = "说话人ID")
    private String speakerId;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "词语信息")
    private List<WordInfo> words;
  }

  @Data
  @Schema(description = "词语信息")
  public static class WordInfo {
    @Schema(description = "词语")
    private String word;

    @Schema(description = "开始时间（秒）")
    private Double startTime;

    @Schema(description = "结束时间（秒）")
    private Double endTime;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "是否标点符号", example = "false")
    private Boolean isPunctuation = false;
  }

  @Data
  @Schema(description = "说话人信息")
  public static class SpeakerInfo {
    @Schema(description = "说话人ID")
    private String speakerId;

    @Schema(description = "说话人标签")
    private String speakerLabel;

    @Schema(description = "说话时长（秒）")
    private Double speakingDuration;

    @Schema(description = "说话占比")
    private Double speakingRatio;

    @Schema(description = "平均语速（每分钟字数）")
    private Double averageSpeakingRate;

    @Schema(description = "语言特征")
    private LanguageFeatures languageFeatures;
  }

  @Data
  @Schema(description = "语言特征")
  public static class LanguageFeatures {
    @Schema(description = "主要语言")
    private String primaryLanguage;

    @Schema(description = "语言置信度")
    private Double languageConfidence;

    @Schema(description = "词汇多样性")
    private Double vocabularyDiversity;

    @Schema(description = "平均句子长度")
    private Double averageSentenceLength;
  }

  @Data
  @Schema(description = "语言信息")
  public static class LanguageInfo {
    @Schema(description = "检测到的语言")
    private String detectedLanguage;

    @Schema(description = "语言置信度")
    private Double languageConfidence;

    @Schema(description = "语言分布")
    private Map<String, Double> languageDistribution;

    @Schema(description = "支持的语言列表")
    private List<String> supportedLanguages;
  }

  @Data
  @Schema(description = "转录统计")
  public static class TranscriptionStats {
    @Schema(description = "总字符数")
    private Integer totalCharacters;

    @Schema(description = "总词数")
    private Integer totalWords;

    @Schema(description = "总句数")
    private Integer totalSentences;

    @Schema(description = "总段落数")
    private Integer totalSegments;

    @Schema(description = "说话人数量")
    private Integer speakerCount;

    @Schema(description = "平均语速（每分钟字数）")
    private Double averageSpeakingRate;

    @Schema(description = "平均置信度")
    private Double averageConfidence;

    @Schema(description = "词汇多样性")
    private Double vocabularyDiversity;

    @Schema(description = "关键词列表")
    private List<String> keywords;

    @Schema(description = "主题词列表")
    private List<String> topics;
  }

  @Data
  @Schema(description = "置信度信息")
  public static class ConfidenceInfo {
    @Schema(description = "整体置信度")
    private Double overallConfidence;

    @Schema(description = "段落级置信度分布")
    private List<Double> segmentConfidences;

    @Schema(description = "词级置信度分布")
    private List<Double> wordConfidences;

    @Schema(description = "置信度阈值")
    private Double confidenceThreshold;

    @Schema(description = "低置信度段落数量")
    private Integer lowConfidenceSegments;

    @Schema(description = "高置信度段落数量")
    private Integer highConfidenceSegments;
  }

  @Data
  @Schema(description = "音频信息")
  public static class AudioInfo {
    @Schema(description = "音频ID")
    private String audioId;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "格式")
    private String format;

    @Schema(description = "时长（秒）")
    private Double duration;

    @Schema(description = "采样率（Hz）")
    private Integer sampleRate;

    @Schema(description = "通道数")
    private Integer channels;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "编码格式")
    private String codec;
  }

  @Data
  @Schema(description = "转录引擎信息")
  public static class EngineInfo {
    @Schema(description = "引擎名称")
    private String name;

    @Schema(description = "引擎版本")
    private String version;

    @Schema(description = "引擎参数")
    private Map<String, Object> parameters;

    @Schema(description = "支持的语言")
    private List<String> supportedLanguages;

    @Schema(description = "最大音频时长（秒）")
    private Integer maxAudioDuration;

    @Schema(description = "支持的音频格式")
    private List<String> supportedFormats;
  }
}
