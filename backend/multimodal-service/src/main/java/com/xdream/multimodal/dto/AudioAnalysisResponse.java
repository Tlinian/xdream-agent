package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/** 音频分析响应DTO */
@Data
@Schema(description = "音频分析响应")
public class AudioAnalysisResponse {

  @Schema(description = "音频ID")
  private String audioId;

  @Schema(description = "音频基本信息")
  private AudioInfo audioInfo;

  @Schema(description = "语音分析结果")
  private SpeechAnalysis speechAnalysis;

  @Schema(description = "音乐分析结果")
  private MusicAnalysis musicAnalysis;

  @Schema(description = "噪声分析结果")
  private NoiseAnalysis noiseAnalysis;

  @Schema(description = "质量评估结果")
  private QualityAssessment qualityAssessment;

  @Schema(description = "特征统计信息")
  private FeatureStatistics featureStatistics;

  @Schema(description = "分段分析结果")
  private List<SegmentAnalysis> segmentAnalysis;

  @Schema(description = "内容标签")
  private List<String> contentTags;

  @Schema(description = "分析耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "置信度分数")
  private Double confidenceScore;

  @Data
  @Schema(description = "音频基本信息")
  public static class AudioInfo {
    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "格式")
    private String format;

    @Schema(description = "时长（秒）")
    private Double duration;

    @Schema(description = "采样率（Hz）")
    private Integer sampleRate;

    @Schema(description = "位深度")
    private Integer bitDepth;

    @Schema(description = "通道数")
    private Integer channels;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "编码格式")
    private String codec;

    @Schema(description = "比特率（bps）")
    private Integer bitrate;
  }

  @Data
  @Schema(description = "语音分析")
  public static class SpeechAnalysis {
    @Schema(description = "是否有语音")
    private Boolean hasSpeech;

    @Schema(description = "语音时长（秒）")
    private Double speechDuration;

    @Schema(description = "语音占比")
    private Double speechRatio;

    @Schema(description = "语音片段数量")
    private Integer speechSegments;

    @Schema(description = "平均语音时长（秒）")
    private Double averageSpeechDuration;

    @Schema(description = "语言类型")
    private String language;

    @Schema(description = "说话人数量")
    private Integer speakerCount;

    @Schema(description = "语音质量")
    private Double speechQuality;

    @Schema(description = "语音清晰度")
    private Double speechClarity;
  }

  @Data
  @Schema(description = "音乐分析")
  public static class MusicAnalysis {
    @Schema(description = "是否有音乐")
    private Boolean hasMusic;

    @Schema(description = "音乐时长（秒）")
    private Double musicDuration;

    @Schema(description = "音乐占比")
    private Double musicRatio;

    @Schema(description = "音乐片段数量")
    private Integer musicSegments;

    @Schema(description = "音乐类型")
    private String musicType;

    @Schema(description = "节奏信息")
    private RhythmInfo rhythm;

    @Schema(description = "音调信息")
    private PitchInfo pitch;

    @Schema(description = "音色信息")
    private TimbreInfo timbre;
  }

  @Data
  @Schema(description = "节奏信息")
  public static class RhythmInfo {
    @Schema(description = "BPM（每分钟节拍数）")
    private Double bpm;

    @Schema(description = "节拍强度")
    private Double beatStrength;

    @Schema(description = "节拍位置")
    private List<Double> beatPositions;

    @Schema(description = "节奏类型")
    private String rhythmType;
  }

  @Data
  @Schema(description = "音调信息")
  public static class PitchInfo {
    @Schema(description = "主频率（Hz）")
    private Double mainFrequency;

    @Schema(description = "音调轮廓")
    private List<Double> pitchContour;

    @Schema(description = "音调范围（Hz）")
    private Double pitchRange;

    @Schema(description = "音调稳定性")
    private Double pitchStability;
  }

  @Data
  @Schema(description = "音色信息")
  public static class TimbreInfo {
    @Schema(description = "频谱质心（Hz）")
    private Double spectralCentroid;

    @Schema(description = "频谱滚降点（Hz）")
    private Double spectralRolloff;

    @Schema(description = "频谱通量")
    private Double spectralFlux;

    @Schema(description = "零交叉率")
    private Double zeroCrossingRate;

    @Schema(description = "MFCC特征")
    private List<Double> mfcc;
  }

  @Data
  @Schema(description = "噪声分析")
  public static class NoiseAnalysis {
    @Schema(description = "噪声水平")
    private Double noiseLevel;

    @Schema(description = "信噪比（dB）")
    private Double signalToNoiseRatio;

    @Schema(description = "噪声类型")
    private String noiseType;

    @Schema(description = "背景噪声强度")
    private Double backgroundNoiseIntensity;

    @Schema(description = "脉冲噪声数量")
    private Integer impulseNoiseCount;

    @Schema(description = "频谱噪声密度")
    private Double spectralNoiseDensity;
  }

  @Data
  @Schema(description = "质量评估")
  public static class QualityAssessment {
    @Schema(description = "整体质量分数")
    private Double overallQuality;

    @Schema(description = "清晰度分数")
    private Double clarity;

    @Schema(description = "自然度分数")
    private Double naturalness;

    @Schema(description = "完整性分数")
    private Double completeness;

    @Schema(description = "失真程度")
    private Double distortionLevel;

    @Schema(description = "质量等级")
    private String qualityLevel;
  }

  @Data
  @Schema(description = "特征统计")
  public static class FeatureStatistics {
    @Schema(description = "MFCC特征统计")
    private MFCCStatistics mfccStats;

    @Schema(description = "频谱特征统计")
    private SpectralStatistics spectralStats;

    @Schema(description = "时域特征统计")
    private TemporalStatistics temporalStats;

    @Schema(description = "能量特征统计")
    private EnergyStatistics energyStats;
  }

  @Data
  @Schema(description = "MFCC统计")
  public static class MFCCStatistics {
    @Schema(description = "MFCC均值")
    private List<Double> mean;

    @Schema(description = "MFCC标准差")
    private List<Double> stdDev;

    @Schema(description = "MFCC范围")
    private List<Double> range;
  }

  @Data
  @Schema(description = "频谱统计")
  public static class SpectralStatistics {
    @Schema(description = "频谱质心均值（Hz）")
    private Double spectralCentroidMean;

    @Schema(description = "频谱滚降点均值（Hz）")
    private Double spectralRolloffMean;

    @Schema(description = "频谱通量均值")
    private Double spectralFluxMean;

    @Schema(description = "频谱带宽均值（Hz）")
    private Double spectralBandwidthMean;
  }

  @Data
  @Schema(description = "时域统计")
  public static class TemporalStatistics {
    @Schema(description = "零交叉率均值")
    private Double zeroCrossingRateMean;

    @Schema(description = "自相关峰值均值")
    private Double autocorrelationPeakMean;

    @Schema(description = "时域质心均值")
    private Double temporalCentroidMean;
  }

  @Data
  @Schema(description = "能量统计")
  public static class EnergyStatistics {
    @Schema(description = "能量均值")
    private Double energyMean;

    @Schema(description = "能量标准差")
    private Double energyStdDev;

    @Schema(description = "能量范围")
    private Double energyRange;

    @Schema(description = "静音比例")
    private Double silenceRatio;
  }

  @Data
  @Schema(description = "分段分析")
  public static class SegmentAnalysis {
    @Schema(description = "段开始时间（秒）")
    private Double startTime;

    @Schema(description = "段结束时间（秒）")
    private Double endTime;

    @Schema(description = "段持续时间（秒）")
    private Double duration;

    @Schema(description = "段类型")
    private String segmentType;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "特征向量")
    private List<Double> features;
  }
}
