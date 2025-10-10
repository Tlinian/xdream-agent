package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 文本转语音响应DTO */
@Data
@Schema(description = "文本转语音响应")
public class TextToSpeechResponse {

  @Schema(description = "任务ID")
  private String taskId;

  @Schema(description = "转换状态")
  private String status;

  @Schema(description = "音频文件信息")
  private AudioFileInfo audioFile;

  @Schema(description = "文本信息")
  private TextInfo textInfo;

  @Schema(description = "语音信息")
  private VoiceInfo voiceInfo;

  @Schema(description = "合成参数")
  private SynthesisParameters synthesisParams;

  @Schema(description = "处理统计")
  private ProcessingStatistics statistics;

  @Schema(description = "质量评估")
  private QualityAssessment quality;

  @Schema(description = "处理时间（毫秒）")
  private Long processingTime;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "警告信息")
  private List<String> warnings;

  @Schema(description = "额外信息")
  private Map<String, Object> metadata;

  @Data
  @Schema(description = "音频文件信息")
  public static class AudioFileInfo {
    @Schema(description = "音频ID")
    private String audioId;

    @Schema(description = "音频URL")
    private String audioUrl;

    @Schema(description = "音频Base64")
    private String audioBase64;

    @Schema(description = "音频路径")
    private String audioPath;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "音频格式")
    private String format;

    @Schema(description = "采样率（Hz）")
    private Integer sampleRate;

    @Schema(description = "位深度")
    private Integer bitDepth;

    @Schema(description = "音频通道")
    private Integer channels;

    @Schema(description = "音频时长（秒）")
    private Double duration;

    @Schema(description = "音频比特率")
    private String bitrate;

    @Schema(description = "音频编码器")
    private String encoder;

    @Schema(description = "音频质量")
    private String quality;

    @Schema(description = "音频元数据")
    private AudioMetadata metadata;
  }

  @Data
  @Schema(description = "音频元数据")
  public static class AudioMetadata {
    @Schema(description = "标题")
    private String title;

    @Schema(description = "艺术家")
    private String artist;

    @Schema(description = "专辑")
    private String album;

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "流派")
    private String genre;

    @Schema(description = "评论")
    private String comment;

    @Schema(description = "音轨号")
    private Integer trackNumber;

    @Schema(description = "自定义标签")
    private Map<String, String> customTags;
  }

  @Data
  @Schema(description = "文本信息")
  public static class TextInfo {
    @Schema(description = "原始文本")
    private String originalText;

    @Schema(description = "处理后的文本")
    private String processedText;

    @Schema(description = "文本长度（字符）")
    private Integer textLength;

    @Schema(description = "文本语言")
    private String language;

    @Schema(description = "文本编码")
    private String encoding;

    @Schema(description = "断句信息")
    private List<SentenceInfo> sentences;

    @Schema(description = "分词信息")
    private List<WordInfo> words;

    @Schema(description = "SSML标记")
    private Boolean ssmlUsed;

    @Schema(description = "文本预处理")
    private TextPreprocessing preprocessing;
  }

  @Data
  @Schema(description = "句子信息")
  public static class SentenceInfo {
    @Schema(description = "句子ID")
    private String sentenceId;

    @Schema(description = "句子文本")
    private String text;

    @Schema(description = "开始位置")
    private Integer startPosition;

    @Schema(description = "结束位置")
    private Integer endPosition;

    @Schema(description = "句子长度")
    private Integer length;

    @Schema(description = "预期停顿时间（毫秒）")
    private Integer expectedPause;

    @Schema(description = "情感标签")
    private String emotion;

    @Schema(description = "情感强度")
    private Double emotionStrength;
  }

  @Data
  @Schema(description = "词语信息")
  public static class WordInfo {
    @Schema(description = "词语ID")
    private String wordId;

    @Schema(description = "词语文本")
    private String text;

    @Schema(description = "开始位置")
    private Integer startPosition;

    @Schema(description = "结束位置")
    private Integer endPosition;

    @Schema(description = "词性")
    private String partOfSpeech;

    @Schema(description = "发音")
    private String pronunciation;

    @Schema(description = "重音标记")
    private Boolean stressed;

    @Schema(description = "音素")
    private List<String> phonemes;
  }

  @Data
  @Schema(description = "文本预处理")
  public static class TextPreprocessing {
    @Schema(description = "文本清洗")
    private Boolean textCleaning;

    @Schema(description = "文本规范化")
    private Boolean textNormalization;

    @Schema(description = "数字转换")
    private Boolean numberConversion;

    @Schema(description = "缩写扩展")
    private Boolean abbreviationExpansion;

    @Schema(description = "标点处理")
    private Boolean punctuationHandling;

    @Schema(description = "多音字处理")
    private Boolean polyphoneHandling;

    @Schema(description = "外来词处理")
    private Boolean foreignWordHandling;
  }

  @Data
  @Schema(description = "语音信息")
  public static class VoiceInfo {
    @Schema(description = "语音模型")
    private String voiceModel;

    @Schema(description = "语音类型")
    private String voiceType;

    @Schema(description = "语音名称")
    private String voiceName;

    @Schema(description = "语音性别")
    private String voiceGender;

    @Schema(description = "语音年龄")
    private String voiceAge;

    @Schema(description = "语音语言")
    private String voiceLanguage;

    @Schema(description = "语音特征")
    private VoiceCharacteristics characteristics;

    @Schema(description = "语音克隆信息")
    private VoiceCloneInfo voiceClone;

    @Schema(description = "参考音频信息")
    private ReferenceAudioInfo referenceAudio;
  }

  @Data
  @Schema(description = "语音特征")
  public static class VoiceCharacteristics {
    @Schema(description = "语速")
    private Double speed;

    @Schema(description = "音调")
    private Double pitch;

    @Schema(description = "音量")
    private Double volume;

    @Schema(description = "情感类型")
    private String emotion;

    @Schema(description = "情感强度")
    private Double emotionStrength;

    @Schema(description = "说话风格")
    private String speakingStyle;

    @Schema(description = "音色特征")
    private TimbreFeatures timbre;

    @Schema(description = "韵律特征")
    private ProsodyFeatures prosody;
  }

  @Data
  @Schema(description = "音色特征")
  public static class TimbreFeatures {
    @Schema(description = "亮度")
    private Double brightness;

    @Schema(description = "粗糙度")
    private Double roughness;

    @Schema(description = "呼吸音")
    private Double breathiness;

    @Schema(description = "鼻音")
    private Double nasality;

    @Schema(description = "紧张度")
    private Double tension;
  }

  @Data
  @Schema(description = "韵律特征")
  public static class ProsodyFeatures {
    @Schema(description = "重音模式")
    private String stressPattern;

    @Schema(description = "语调模式")
    private String intonationPattern;

    @Schema(description = "节奏模式")
    private String rhythmPattern;

    @Schema(description = "停顿模式")
    private String pausePattern;

    @Schema(description = "语速变化")
    private Double speechRateVariation;
  }

  @Data
  @Schema(description = "语音克隆信息")
  public static class VoiceCloneInfo {
    @Schema(description = "是否使用语音克隆")
    private Boolean voiceCloneUsed;

    @Schema(description = "克隆模型")
    private String cloneModel;

    @Schema(description = "克隆强度")
    private Double cloneStrength;

    @Schema(description = "相似度分数")
    private Double similarityScore;

    @Schema(description = "克隆质量评估")
    private VoiceCloneQuality cloneQuality;
  }

  @Data
  @Schema(description = "语音克隆质量")
  public static class VoiceCloneQuality {
    @Schema(description = "整体质量分数")
    private Double overallQuality;

    @Schema(description = "相似度分数")
    private Double similarityScore;

    @Schema(description = "自然度分数")
    private Double naturalnessScore;

    @Schema(description = "清晰度分数")
    private Double clarityScore;

    @Schema(description = "一致性分数")
    private Double consistencyScore;
  }

  @Data
  @Schema(description = "参考音频信息")
  public static class ReferenceAudioInfo {
    @Schema(description = "参考音频ID")
    private String referenceAudioId;

    @Schema(description = "参考音频时长（秒）")
    private Double referenceAudioDuration;

    @Schema(description = "参考音频质量")
    private String referenceAudioQuality;

    @Schema(description = "参考文本匹配度")
    private Double textMatchingScore;
  }

  @Data
  @Schema(description = "合成参数")
  public static class SynthesisParameters {
    @Schema(description = "使用的模型")
    private String modelUsed;

    @Schema(description = "模型版本")
    private String modelVersion;

    @Schema(description = "合成算法")
    private String synthesisAlgorithm;

    @Schema(description = "声码器类型")
    private String vocoderType;

    @Schema(description = "音频特征提取")
    private String featureExtraction;

    @Schema(description = "参数设置")
    private Map<String, Object> parameterSettings;

    @Schema(description = "后处理设置")
    private PostProcessingSettings postProcessing;
  }

  @Data
  @Schema(description = "后处理设置")
  public static class PostProcessingSettings {
    @Schema(description = "音频归一化")
    private Boolean audioNormalization;

    @Schema(description = "音频压缩")
    private Boolean audioCompression;

    @Schema(description = "噪声抑制")
    private Boolean noiseSuppression;

    @Schema(description = "音频滤波")
    private Boolean audioFiltering;

    @Schema(description = "音频增强")
    private Boolean audioEnhancement;

    @Schema(description = "音频效果")
    private List<String> audioEffects;
  }

  @Data
  @Schema(description = "处理统计")
  public static class ProcessingStatistics {
    @Schema(description = "总处理时间（毫秒）")
    private Long totalProcessingTime;

    @Schema(description = "文本预处理时间（毫秒）")
    private Long textPreprocessingTime;

    @Schema(description = "语音合成时间（毫秒）")
    private Long synthesisTime;

    @Schema(description = "音频后处理时间（毫秒）")
    private Long audioPostProcessingTime;

    @Schema(description = "模型加载时间（毫秒）")
    private Long modelLoadTime;

    @Schema(description = "推理时间（毫秒）")
    private Long inferenceTime;

    @Schema(description = "音频编码时间（毫秒）")
    private Long audioEncodingTime;

    @Schema(description = "内存使用量（MB）")
    private Double memoryUsage;

    @Schema(description = "GPU使用量（%）")
    private Double gpuUsage;

    @Schema(description = "CPU使用量（%）")
    private Double cpuUsage;

    @Schema(description = "处理的文本字符数")
    private Integer processedCharacters;

    @Schema(description = "生成的音频帧数")
    private Integer generatedAudioFrames;

    @Schema(description = "音频文件大小（字节）")
    private Long audioFileSize;
  }

  @Data
  @Schema(description = "质量评估")
  public static class QualityAssessment {
    @Schema(description = "整体质量分数")
    private Double overallQualityScore;

    @Schema(description = "自然度分数")
    private Double naturalnessScore;

    @Schema(description = "清晰度分数")
    private Double intelligibilityScore;

    @Schema(description = "表达性分数")
    private Double expressivenessScore;

    @Schema(description = "一致性分数")
    private Double consistencyScore;

    @Schema(description = "音频质量分数")
    private Double audioQualityScore;

    @Schema(description = "语音质量指标")
    private VoiceQualityMetrics voiceQuality;

    @Schema(description = "质量评估详情")
    private QualityDetails qualityDetails;
  }

  @Data
  @Schema(description = "语音质量指标")
  public static class VoiceQualityMetrics {
    @Schema(description = "梅尔倒谱失真（MCD）")
    private Double melCepstralDistortion;

    @Schema(description = "fundamental frequency error (F0)")
    private Double fundamentalFrequencyError;

    @Schema(description = "语音清晰度指数")
    private Double speechIntelligibilityIndex;

    @Schema(description = "语音质量感知评估")
    private Double perceptualEvaluation;

    @Schema(description = "客观语音质量评估")
    private Double objectiveQualityScore;
  }

  @Data
  @Schema(description = "质量详情")
  public static class QualityDetails {
    @Schema(description = "发音准确性")
    private Double pronunciationAccuracy;

    @Schema(description = "韵律自然度")
    private Double prosodyNaturalness;

    @Schema(description = "语音平滑度")
    private Double speechSmoothness;

    @Schema(description = "音色一致性")
    private Double timbreConsistency;

    @Schema(description = "情感表达准确性")
    private Double emotionAccuracy;

    @Schema(description = "说话风格一致性")
    private Double speakingStyleConsistency;
  }
}
