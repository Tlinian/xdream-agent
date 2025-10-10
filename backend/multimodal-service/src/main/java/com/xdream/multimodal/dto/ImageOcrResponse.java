package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 图像OCR识别响应DTO */
@Data
@Schema(description = "图像OCR识别响应")
public class ImageOcrResponse {

  @Schema(description = "识别结果ID")
  private String resultId;

  @Schema(description = "识别到的文本内容")
  private String text;

  @Schema(description = "识别到的文本块")
  private List<TextBlock> textBlocks;

  @Schema(description = "识别统计信息")
  private RecognitionStats stats;

  @Schema(description = "语言信息")
  private LanguageInfo language;

  @Schema(description = "置信度信息")
  private ConfidenceInfo confidence;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;

  @Schema(description = "OCR引擎信息")
  private EngineInfo engine;

  @Schema(description = "原始图像信息")
  private ImageInfo image;

  @Data
  @Schema(description = "文本块")
  public static class TextBlock {
    @Schema(description = "文本内容")
    private String text;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "字体信息")
    private FontInfo font;

    @Schema(description = "文本方向")
    private String orientation;

    @Schema(description = "行信息")
    private List<LineInfo> lines;
  }

  @Data
  @Schema(description = "边界框")
  public static class BoundingBox {
    @Schema(description = "左上角X坐标")
    private Integer x;

    @Schema(description = "左上角Y坐标")
    private Integer y;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;
  }

  @Data
  @Schema(description = "字体信息")
  public static class FontInfo {
    @Schema(description = "字体名称")
    private String name;

    @Schema(description = "字体大小")
    private Integer size;

    @Schema(description = "是否粗体")
    private Boolean bold;

    @Schema(description = "是否斜体")
    private Boolean italic;
  }

  @Data
  @Schema(description = "行信息")
  public static class LineInfo {
    @Schema(description = "行文本")
    private String text;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;

    @Schema(description = "词信息")
    private List<WordInfo> words;
  }

  @Data
  @Schema(description = "词信息")
  public static class WordInfo {
    @Schema(description = "词文本")
    private String text;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "边界框")
    private BoundingBox bbox;
  }

  @Data
  @Schema(description = "识别统计信息")
  public static class RecognitionStats {
    @Schema(description = "总字符数")
    private Integer totalCharacters;

    @Schema(description = "总词数")
    private Integer totalWords;

    @Schema(description = "总行数")
    private Integer totalLines;

    @Schema(description = "总块数")
    private Integer totalBlocks;

    @Schema(description = "平均置信度")
    private Double averageConfidence;

    @Schema(description = "最低置信度")
    private Double minConfidence;

    @Schema(description = "最高置信度")
    private Double maxConfidence;
  }

  @Data
  @Schema(description = "语言信息")
  public static class LanguageInfo {
    @Schema(description = "检测到的语言")
    private String detectedLanguage;

    @Schema(description = "语言置信度")
    private Double languageConfidence;

    @Schema(description = "支持的语言列表")
    private List<String> supportedLanguages;
  }

  @Data
  @Schema(description = "置信度信息")
  public static class ConfidenceInfo {
    @Schema(description = "整体置信度")
    private Double overallConfidence;

    @Schema(description = "字符级置信度分布")
    private Map<String, Integer> characterConfidenceDistribution;

    @Schema(description = "置信度阈值")
    private Double confidenceThreshold;
  }

  @Data
  @Schema(description = "OCR引擎信息")
  public static class EngineInfo {
    @Schema(description = "引擎名称")
    private String name;

    @Schema(description = "引擎版本")
    private String version;

    @Schema(description = "引擎参数")
    private Map<String, Object> parameters;
  }

  @Data
  @Schema(description = "图像信息")
  public static class ImageInfo {
    @Schema(description = "图像ID")
    private String imageId;

    @Schema(description = "宽度")
    private Integer width;

    @Schema(description = "高度")
    private Integer height;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "格式")
    private String format;
  }
}
