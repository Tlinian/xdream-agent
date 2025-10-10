package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 视频分析响应DTO */
@Data
@Schema(description = "视频分析响应")
public class VideoAnalysisResponse {

  @Schema(description = "视频ID")
  private String videoId;

  @Schema(description = "视频基本信息")
  private VideoInfo videoInfo;

  @Schema(description = "场景分析结果")
  private SceneAnalysis sceneAnalysis;

  @Schema(description = "对象检测结果")
  private ObjectDetection objectDetection;

  @Schema(description = "运动分析结果")
  private MotionAnalysis motionAnalysis;

  @Schema(description = "音频分析结果")
  private AudioAnalysis audioAnalysis;

  @Schema(description = "质量评估结果")
  private QualityAssessment qualityAssessment;

  @Schema(description = "内容标签")
  private List<String> contentTags;

  @Schema(description = "分析耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "置信度分数")
  private Double confidenceScore;

  @Data
  @Schema(description = "视频基本信息")
  public static class VideoInfo {
    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "格式")
    private String format;

    @Schema(description = "时长（秒）")
    private Double duration;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "帧率")
    private Double frameRate;

    @Schema(description = "码率（bps）")
    private Integer bitrate;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "总帧数")
    private Long totalFrames;

    @Schema(description = "编码格式")
    private String codec;
  }

  @Data
  @Schema(description = "场景分析")
  public static class SceneAnalysis {
    @Schema(description = "场景列表")
    private List<Scene> scenes;

    @Schema(description = "场景变化次数")
    private Integer sceneChanges;

    @Schema(description = "主要场景类别")
    private String mainSceneCategory;

    @Schema(description = "场景分布")
    private Map<String, Double> sceneDistribution;
  }

  @Data
  @Schema(description = "场景信息")
  public static class Scene {
    @Schema(description = "场景ID")
    private String sceneId;

    @Schema(description = "开始时间（秒）")
    private Double startTime;

    @Schema(description = "结束时间（秒）")
    private Double endTime;

    @Schema(description = "持续时间（秒）")
    private Double duration;

    @Schema(description = "场景类别")
    private String category;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "关键帧时间戳")
    private Double keyframeTimestamp;
  }

  @Data
  @Schema(description = "对象检测")
  public static class ObjectDetection {
    @Schema(description = "检测到的对象")
    private List<DetectedObject> objects;

    @Schema(description = "总检测数量")
    private Integer totalDetections;

    @Schema(description = "对象类别分布")
    private Map<String, Integer> categoryDistribution;
  }

  @Data
  @Schema(description = "检测到的对象")
  public static class DetectedObject {
    @Schema(description = "对象ID")
    private String objectId;

    @Schema(description = "对象类别")
    private String category;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "出现时间（秒）")
    private Double startTime;

    @Schema(description = "消失时间（秒）")
    private Double endTime;

    @Schema(description = "轨迹信息")
    private List<TrajectoryPoint> trajectory;
  }

  @Data
  @Schema(description = "轨迹点")
  public static class TrajectoryPoint {
    @Schema(description = "时间戳（秒）")
    private Double timestamp;

    @Schema(description = "X坐标")
    private Double x;

    @Schema(description = "Y坐标")
    private Double y;

    @Schema(description = "宽度")
    private Double width;

    @Schema(description = "高度")
    private Double height;
  }

  @Data
  @Schema(description = "运动分析")
  public static class MotionAnalysis {
    @Schema(description = "运动强度")
    private Double motionIntensity;

    @Schema(description = "运动类型")
    private String motionType;

    @Schema(description = "相机运动类型")
    private String cameraMotion;

    @Schema(description = "运动区域")
    private List<MotionRegion> motionRegions;
  }

  @Data
  @Schema(description = "运动区域")
  public static class MotionRegion {
    @Schema(description = "区域ID")
    private String regionId;

    @Schema(description = "开始时间（秒）")
    private Double startTime;

    @Schema(description = "结束时间（秒）")
    private Double endTime;

    @Schema(description = "运动强度")
    private Double intensity;

    @Schema(description = "运动方向")
    private String direction;
  }

  @Data
  @Schema(description = "音频分析")
  public static class AudioAnalysis {
    @Schema(description = "音频存在")
    private Boolean hasAudio;

    @Schema(description = "音频质量")
    private Double audioQuality;

    @Schema(description = "音量级别")
    private Double volumeLevel;

    @Schema(description = "音频类型")
    private String audioType;

    @Schema(description = "语音信息")
    private SpeechInfo speechInfo;
  }

  @Data
  @Schema(description = "语音信息")
  public static class SpeechInfo {
    @Schema(description = "是否有语音")
    private Boolean hasSpeech;

    @Schema(description = "语音时长（秒）")
    private Double speechDuration;

    @Schema(description = "语音片段数量")
    private Integer speechSegments;

    @Schema(description = "语言类型")
    private String language;
  }

  @Data
  @Schema(description = "质量评估")
  public static class QualityAssessment {
    @Schema(description = "整体质量分数")
    private Double overallQuality;

    @Schema(description = "清晰度分数")
    private Double sharpness;

    @Schema(description = "对比度分数")
    private Double contrast;

    @Schema(description = "亮度分数")
    private Double brightness;

    @Schema(description = "色彩分数")
    private Double colorfulness;

    @Schema(description = "噪声水平")
    private Double noiseLevel;

    @Schema(description = "模糊程度")
    private Double blurLevel;
  }
}
