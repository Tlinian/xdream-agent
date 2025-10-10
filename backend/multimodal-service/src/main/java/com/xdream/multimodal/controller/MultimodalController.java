package com.xdream.multimodal.controller;

import com.xdream.common.dto.ApiResponse;
import com.xdream.multimodal.dto.*;
import com.xdream.multimodal.service.MultimodalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** 多模态服务控制器 提供图像、视频、音频等多模态内容的处理和分析接口 */
@RestController
@RequestMapping("/api/v1/multimodal")
@RequiredArgsConstructor
@Tag(name = "多模态服务", description = "多模态内容处理和分析接口")
public class MultimodalController {

  private final MultimodalService multimodalService;

  /** 图像内容分析 */
  @PostMapping(value = "/image/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "图像内容分析", description = "分析上传的图像内容，提取特征信息")
  public ApiResponse<ImageAnalysisResponse> analyzeImage(
      @Parameter(description = "图像文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "分析类型")
          @RequestParam(value = "analysisType", required = false, defaultValue = "ALL")
          String analysisType) {
    ImageAnalysisRequest request = new ImageAnalysisRequest();
    request.setFile(file);
    request.setAnalysisType(analysisType);
    return ApiResponse.success(multimodalService.analyzeImage(request));
  }

  /** 图像特征提取 */
  @PostMapping(value = "/image/features", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "图像特征提取", description = "提取图像的视觉特征向量")
  public ApiResponse<ImageFeaturesResponse> extractImageFeatures(
      @Parameter(description = "图像文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "特征类型")
          @RequestParam(value = "featureType", required = false, defaultValue = "CNN")
          String featureType) {
    ImageFeaturesRequest request = new ImageFeaturesRequest();
    request.setFile(file);
    request.setFeatureType(featureType);
    return ApiResponse.success(multimodalService.extractImageFeatures(request));
  }

  /** 图像相似度检测 */
  @PostMapping(value = "/image/similarity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "图像相似度检测", description = "比较两张图像的相似度")
  public ApiResponse<ImageSimilarityResponse> compareImageSimilarity(
      @Parameter(description = "第一张图像", required = true) @RequestPart("file1") MultipartFile file1,
      @Parameter(description = "第二张图像", required = true) @RequestPart("file2") MultipartFile file2,
      @Parameter(description = "相似度算法")
          @RequestParam(value = "algorithm", required = false, defaultValue = "SSIM")
          String algorithm) {
    ImageSimilarityRequest request = new ImageSimilarityRequest();
    request.setFile1(file1);
    request.setFile2(file2);
    request.setAlgorithm(algorithm);
    return ApiResponse.success(multimodalService.compareImageSimilarity(request));
  }

  /** 图像OCR识别 */
  @PostMapping(value = "/image/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "图像OCR识别", description = "从图像中提取文本内容")
  public ApiResponse<ImageOcrResponse> extractImageText(
      @Parameter(description = "图像文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "语言类型")
          @RequestParam(value = "language", required = false, defaultValue = "zh-CN")
          String language) {
    ImageOcrRequest request = new ImageOcrRequest();
    request.setFile(file);
    request.setLanguage(language);
    return ApiResponse.success(multimodalService.extractImageText(request));
  }

  /** 视频内容分析 */
  @PostMapping(value = "/video/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "视频内容分析", description = "分析上传的视频内容")
  public ApiResponse<VideoAnalysisResponse> analyzeVideo(
      @Parameter(description = "视频文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "分析类型")
          @RequestParam(value = "analysisType", required = false, defaultValue = "BASIC")
          String analysisType) {
    VideoAnalysisRequest request = new VideoAnalysisRequest();
    request.setFile(file);
    request.setAnalysisType(analysisType);
    return ApiResponse.success(multimodalService.analyzeVideo(request));
  }

  /** 视频关键帧提取 */
  @PostMapping(value = "/video/keyframes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "视频关键帧提取", description = "从视频中提取关键帧")
  public ApiResponse<VideoKeyframesResponse> extractVideoKeyframes(
      @Parameter(description = "视频文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "提取模式")
          @RequestParam(value = "mode", required = false, defaultValue = "AUTO")
          String mode,
      @Parameter(description = "帧数量")
          @RequestParam(value = "frameCount", required = false, defaultValue = "10")
          int frameCount) {
    VideoKeyframesRequest request = new VideoKeyframesRequest();
    request.setFile(file);
    request.setMode(mode);
    request.setFrameCount(frameCount);
    return ApiResponse.success(multimodalService.extractVideoKeyframes(request));
  }

  /** 音频内容分析 */
  @PostMapping(value = "/audio/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "音频内容分析", description = "分析上传的音频内容")
  public ApiResponse<AudioAnalysisResponse> analyzeAudio(
      @Parameter(description = "音频文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "分析类型")
          @RequestParam(value = "analysisType", required = false, defaultValue = "BASIC")
          String analysisType) {
    AudioAnalysisRequest request = new AudioAnalysisRequest();
    request.setFile(file);
    request.setAnalysisType(analysisType);
    return ApiResponse.success(multimodalService.analyzeAudio(request));
  }

  /** 音频转文本 */
  @PostMapping(value = "/audio/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "音频转文本", description = "将音频内容转换为文本")
  public ApiResponse<AudioTranscriptionResponse> transcribeAudio(
      @Parameter(description = "音频文件", required = true) @RequestPart("file") MultipartFile file,
      @Parameter(description = "语言类型")
          @RequestParam(value = "language", required = false, defaultValue = "zh-CN")
          String language) {
    AudioTranscriptionRequest request = new AudioTranscriptionRequest();
    request.setFile(file);
    request.setLanguage(language);
    return ApiResponse.success(multimodalService.transcribeAudio(request));
  }

  /** 多模态内容融合 */
  @PostMapping("/fusion")
  @Operation(summary = "多模态内容融合", description = "融合多种模态的内容信息")
  public ApiResponse<MultimodalFusionResponse> fuseMultimodalContent(
      @Valid @RequestBody MultimodalFusionRequest request) {
    return ApiResponse.success(multimodalService.fuseMultimodalContent(request));
  }

  /** 获取处理状态 */
  @GetMapping("/status/{taskId}")
  @Operation(summary = "获取处理状态", description = "获取异步处理任务的状态")
  public ApiResponse<TaskStatusResponse> getTaskStatus(
      @Parameter(description = "任务ID", required = true) @PathVariable String taskId) {
    return ApiResponse.success(multimodalService.getTaskStatus(taskId));
  }

  /** 批量处理 */
  @PostMapping("/batch/process")
  @Operation(summary = "批量处理", description = "批量处理多模态内容")
  public ApiResponse<BatchProcessResponse> batchProcess(
      @Valid @RequestBody BatchProcessRequest request) {
    return ApiResponse.success(multimodalService.batchProcess(request));
  }
}
