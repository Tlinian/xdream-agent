package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 文本转语音请求DTO */
@Data
@Schema(description = "文本转语音请求")
public class TextToSpeechRequest {

  @Schema(description = "输入文本", required = true)
  private String text;

  @Schema(description = "语音模型")
  private String model = "default";

  @Schema(description = "语音类型")
  private String voice = "default";

  @Schema(description = "语言代码")
  private String language = "zh-CN";

  @Schema(description = "语速")
  private Double speed = 1.0;

  @Schema(description = "音调")
  private Double pitch = 1.0;

  @Schema(description = "音量")
  private Double volume = 1.0;

  @Schema(description = "情感类型")
  private String emotion = "neutral";

  @Schema(description = "情感强度")
  private Double emotionStrength = 0.5;

  @Schema(description = "说话风格")
  private String speakingStyle = "normal";

  @Schema(description = "音频格式")
  private String outputFormat = "mp3";

  @Schema(description = "采样率")
  private Integer sampleRate = 22050;

  @Schema(description = "音频质量")
  private String audioQuality = "high";

  @Schema(description = "语音合成参数")
  private SynthesisParameters synthesisParams;

  @Schema(description = "语音克隆参数")
  private VoiceCloneParameters voiceClone;

  @Schema(description = "参考音频")
  private MultipartFile referenceAudio;

  @Schema(description = "参考文本")
  private String referenceText;

  @Schema(description = "SSML标记")
  private Boolean useSSML = false;

  @Schema(description = "断句参数")
  private BreakParameters breakParams;

  @Schema(description = "发音参数")
  private PronunciationParameters pronunciation;

  @Schema(description = "音频效果")
  private AudioEffects audioEffects;

  @Data
  @Schema(description = "合成参数")
  public static class SynthesisParameters {
    @Schema(description = "最大音频长度（秒）")
    private Integer maxAudioLength = 300;

    @Schema(description = "文本分块大小")
    private Integer textChunkSize = 500;

    @Schema(description = "重叠长度（字符）")
    private Integer overlapLength = 50;

    @Schema(description = "停顿时间（毫秒）")
    private Integer pauseDuration = 500;

    @Schema(description = "句子停顿时间（毫秒）")
    private Integer sentencePause = 800;

    @Schema(description = "段落停顿时间（毫秒）")
    private Integer paragraphPause = 1200;

    @Schema(description = "音频归一化")
    private Boolean normalizeAudio = true;

    @Schema(description = "音频淡入淡出")
    private Boolean fadeInOut = true;

    @Schema(description = "背景噪音抑制")
    private Boolean noiseSuppression = true;

    @Schema(description = "音频压缩")
    private Boolean audioCompression = false;

    @Schema(description = "压缩比率")
    private Double compressionRatio = 0.0;

    @Schema(description = "音频编码器")
    private String audioEncoder = "libmp3lame";

    @Schema(description = "音频比特率")
    private String audioBitrate = "128k";

    @Schema(description = "音频通道")
    private Integer audioChannels = 1;

    @Schema(description = "音频位深度")
    private Integer audioBitDepth = 16;
  }

  @Data
  @Schema(description = "语音克隆参数")
  public static class VoiceCloneParameters {
    @Schema(description = "启用语音克隆")
    private Boolean enabled = false;

    @Schema(description = "克隆强度")
    private Double cloneStrength = 0.8;

    @Schema(description = "相似度阈值")
    private Double similarityThreshold = 0.7;

    @Schema(description = "克隆模型")
    private String cloneModel = "default";

    @Schema(description = "参考音频长度（秒）")
    private Integer referenceAudioLength = 30;

    @Schema(description = "最小参考音频长度（秒）")
    private Integer minReferenceAudioLength = 10;

    @Schema(description = "语音特征提取")
    private Boolean featureExtraction = true;

    @Schema(description = "语音适应")
    private Boolean voiceAdaptation = true;

    @Schema(description = "说话人验证")
    private Boolean speakerVerification = false;

    @Schema(description = "克隆质量评估")
    private Boolean qualityAssessment = true;
  }

  @Data
  @Schema(description = "断句参数")
  public static class BreakParameters {
    @Schema(description = "自动断句")
    private Boolean autoBreak = true;

    @Schema(description = "断句规则")
    private List<String> breakRules;

    @Schema(description = "断句长度阈值")
    private Integer breakLengthThreshold = 50;

    @Schema(description = "断句停顿时间（毫秒）")
    private Map<String, Integer> breakDurations;

    @Schema(description = "智能断句")
    private Boolean smartBreak = true;

    @Schema(description = "语义断句")
    private Boolean semanticBreak = true;

    @Schema(description = "语法断句")
    private Boolean grammarBreak = true;
  }

  @Data
  @Schema(description = "发音参数")
  public static class PronunciationParameters {
    @Schema(description = "自定义发音词典")
    private Map<String, String> customPronunciations;

    @Schema(description = "发音规则")
    private List<PronunciationRule> pronunciationRules;

    @Schema(description = "数字读法")
    private String numberPronunciation = "auto";

    @Schema(description = "缩写读法")
    private String abbreviationPronunciation = "auto";

    @Schema(description = "外来词读法")
    private String foreignWordPronunciation = "auto";

    @Schema(description = "专有名词读法")
    private String properNounPronunciation = "auto";

    @Schema(description = "多音字处理")
    private String polyphoneHandling = "auto";

    @Schema(description = "拼音标注")
    private Boolean pinyinAnnotation = false;

    @Schema(description = "音标标注")
    private Boolean phoneticAnnotation = false;
  }

  @Data
  @Schema(description = "发音规则")
  public static class PronunciationRule {
    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "匹配模式")
    private String pattern;

    @Schema(description = "替换发音")
    private String replacement;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "启用状态")
    private Boolean enabled;
  }

  @Data
  @Schema(description = "音频效果")
  public static class AudioEffects {
    @Schema(description = "启用音频效果")
    private Boolean enabled = false;

    @Schema(description = "混响效果")
    private ReverbEffect reverb;

    @Schema(description = "均衡器设置")
    private EqualizerSettings equalizer;

    @Schema(description = "压缩器设置")
    private CompressorSettings compressor;

    @Schema(description = "限制器设置")
    private LimiterSettings limiter;

    @Schema(description = "噪声门设置")
    private NoiseGateSettings noiseGate;

    @Schema(description = "合唱效果")
    private ChorusEffect chorus;

    @Schema(description = "延迟效果")
    private DelayEffect delay;

    @Schema(description = "失真效果")
    private DistortionEffect distortion;

    @Schema(description = "滤波器设置")
    private FilterSettings filter;
  }

  @Data
  @Schema(description = "混响效果")
  public static class ReverbEffect {
    @Schema(description = "启用混响")
    private Boolean enabled = false;

    @Schema(description = "混响类型")
    private String reverbType = "room";

    @Schema(description = "混响强度")
    private Double intensity = 0.3;

    @Schema(description = "房间大小")
    private Double roomSize = 0.5;

    @Schema(description = "衰减时间")
    private Double decayTime = 1.0;

    @Schema(description = "预延迟")
    private Double preDelay = 0.0;

    @Schema(description = "高频衰减")
    private Double highFreqDamp = 0.5;
  }

  @Data
  @Schema(description = "均衡器设置")
  public static class EqualizerSettings {
    @Schema(description = "启用均衡器")
    private Boolean enabled = false;

    @Schema(description = "预设类型")
    private String preset = "flat";

    @Schema(description = "频段设置")
    private List<EqualizerBand> bands;

    @Schema(description = "低频增益")
    private Double lowFreqGain = 0.0;

    @Schema(description = "中频增益")
    private Double midFreqGain = 0.0;

    @Schema(description = "高频增益")
    private Double highFreqGain = 0.0;
  }

  @Data
  @Schema(description = "均衡器频段")
  public static class EqualizerBand {
    @Schema(description = "频率（Hz）")
    private Double frequency;

    @Schema(description = "增益（dB）")
    private Double gain;

    @Schema(description = "Q值")
    private Double q;

    @Schema(description = "滤波器类型")
    private String filterType;

    @Schema(description = "启用状态")
    private Boolean enabled;
  }

  @Data
  @Schema(description = "压缩器设置")
  public static class CompressorSettings {
    @Schema(description = "启用压缩器")
    private Boolean enabled = false;

    @Schema(description = "阈值（dB）")
    private Double threshold = -18.0;

    @Schema(description = "比率")
    private Double ratio = 4.0;

    @Schema(description = "攻击时间（ms）")
    private Double attack = 10.0;

    @Schema(description = "释放时间（ms）")
    private Double release = 100.0;

    @Schema(description = "补偿增益（dB）")
    private Double makeupGain = 0.0;

    @Schema(description = "拐点")
    private Double knee = 2.0;
  }

  @Data
  @Schema(description = "限制器设置")
  public static class LimiterSettings {
    @Schema(description = "启用限制器")
    private Boolean enabled = false;

    @Schema(description = "阈值（dB）")
    private Double threshold = -0.1;

    @Schema(description = "释放时间（ms）")
    private Double release = 50.0;

    @Schema(description = "前瞻时间（ms）")
    private Double lookahead = 5.0;
  }

  @Data
  @Schema(description = "噪声门设置")
  public static class NoiseGateSettings {
    @Schema(description = "启用噪声门")
    private Boolean enabled = false;

    @Schema(description = "阈值（dB）")
    private Double threshold = -40.0;

    @Schema(description = "攻击时间（ms）")
    private Double attack = 10.0;

    @Schema(description = "保持时间（ms）")
    private Double hold = 100.0;

    @Schema(description = "释放时间（ms）")
    private Double release = 200.0;

    @Schema(description = "衰减（dB）")
    private Double reduction = -20.0;
  }

  @Data
  @Schema(description = "合唱效果")
  public static class ChorusEffect {
    @Schema(description = "启用合唱")
    private Boolean enabled = false;

    @Schema(description = "速率（Hz）")
    private Double rate = 1.0;

    @Schema(description = "深度")
    private Double depth = 0.5;

    @Schema(description = "延迟（ms）")
    private Double delay = 20.0;

    @Schema(description = "反馈")
    private Double feedback = 0.3;

    @Schema(description = "混合")
    private Double mix = 0.5;
  }

  @Data
  @Schema(description = "延迟效果")
  public static class DelayEffect {
    @Schema(description = "启用延迟")
    private Boolean enabled = false;

    @Schema(description = "延迟时间（ms）")
    private Double delayTime = 300.0;

    @Schema(description = "反馈")
    private Double feedback = 0.3;

    @Schema(description = "混合")
    private Double mix = 0.5;

    @Schema(description = "高通滤波（Hz）")
    private Double highPass = 100.0;

    @Schema(description = "低通滤波（Hz）")
    private Double lowPass = 8000.0;
  }

  @Data
  @Schema(description = "失真效果")
  public static class DistortionEffect {
    @Schema(description = "启用失真")
    private Boolean enabled = false;

    @Schema(description = "失真类型")
    private String distortionType = "tube";

    @Schema(description = "失真量")
    private Double amount = 0.5;

    @Schema(description = "音调")
    private Double tone = 0.5;

    @Schema(description = "混合")
    private Double mix = 0.5;
  }

  @Data
  @Schema(description = "滤波器设置")
  public static class FilterSettings {
    @Schema(description = "启用滤波器")
    private Boolean enabled = false;

    @Schema(description = "滤波器类型")
    private String filterType = "lowpass";

    @Schema(description = "截止频率（Hz）")
    private Double cutoffFrequency = 1000.0;

    @Schema(description = "共振")
    private Double resonance = 0.7;

    @Schema(description = "斜率（dB/oct）")
    private Double slope = 12.0;

    @Schema(description = "混合")
    private Double mix = 1.0;
  }
}
