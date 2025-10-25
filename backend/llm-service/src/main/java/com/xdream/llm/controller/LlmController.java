package com.xdream.llm.controller;

import com.xdream.common.dto.ApiResponse;
import com.xdream.llm.dto.*;
import com.xdream.llm.service.LlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/llm")
@RequiredArgsConstructor
@Tag(name = "AI模型服务", description = "AI大模型相关接口")
public class LlmController {

  private final LlmService llmService;

  @PostMapping("/chat")
  @Operation(summary = "简单对话", description = "简单的单次对话请求")
  @Deprecated
  public ResponseEntity<ApiResponse<ChatResponse>> chat(
      @RequestHeader("X-User-Id") String userId, @Valid @RequestBody ChatRequest request) {
    ChatResponse response = llmService.chat(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @Operation(summary = "流式对话", description = "使用流式方式获取AI回复")
  public Flux<StreamResponse> streamChat(
      @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
      @RequestParam(value = "userId", required = false) String userIdParam,
      @Parameter(description = "用户消息") @RequestParam(required = false, defaultValue = "hello")
          String message,
      @Parameter(description = "模型类型") @RequestParam(required = false, defaultValue = "gpt-4")
          String modelType,
      @Parameter(description = "温度参数") @RequestParam(required = false, defaultValue = "0.7")
          Double temperature,
      @Parameter(description = "最大token数") @RequestParam(required = false, defaultValue = "2048")
          Integer maxTokens,
      @Parameter(description = "系统提示词") @RequestParam(required = false) String systemPrompt,
      @Parameter(description = "是否启用ReAct模式")
          @RequestParam(required = false, defaultValue = "false")
          Boolean useReAct) {

    // 优先使用请求头的用户ID，如果没有则使用URL参数的用户ID，如果都没有则使用默认值
    String userId =
        userIdHeader != null
            ? userIdHeader
            : (userIdParam != null ? userIdParam : "anonymous-user");

    ChatRequest request = new ChatRequest();
    request.setMessage(message);
    request.setModelType(modelType);
    request.setTemperature(temperature);
    request.setMaxTokens(maxTokens);
    request.setUseReAct(useReAct);

    systemPrompt = "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。无论是学习、工作、生活还是娱乐相关的问题，你都会尽力提供清晰、有用的信息。";
    request.setSystemPrompt(systemPrompt);

      ChatRequest.Message systemMessage = new ChatRequest.Message();
      systemMessage.setRole("system");
      systemMessage.setContent(systemPrompt);
      request.setMessages(List.of(systemMessage));

      return llmService.streamChat(userId, request);
  }


  @PostMapping("/rerank")
  @Operation(summary = "閲嶆帓搴?, description = "璋冪敤閲嶆帓搴忔ā鍨嬪鍊欓€夋枃鏈繘琛屾墦鍒?)
  public ResponseEntity<ApiResponse<RerankResponse>> rerank(
      @RequestHeader("X-User-Id") String userId,
      @Valid @RequestBody RerankRequest request) {
    RerankResponse response = llmService.rerank(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/embeddings")
  @Operation(summary = "生成嵌入", description = "生成文本的嵌入向量")
  public ResponseEntity<ApiResponse<EmbeddingResponse>> generateEmbeddings(
      @RequestHeader("X-User-Id") String userId, @Valid @RequestBody EmbeddingRequest request) {
    EmbeddingResponse response = llmService.generateEmbeddings(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/images/generate")
  @Operation(summary = "生成图片", description = "根据提示词生成图片")
  public ResponseEntity<ApiResponse<ImageGenerationResponse>> generateImage(
      @RequestHeader("X-User-Id") String userId,
      @Valid @RequestBody ImageGenerationRequest request) {
    ImageGenerationResponse response = llmService.generateImage(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/models")
  @Operation(summary = "获取模型列表", description = "获取可用的AI模型列表")
  public ResponseEntity<ApiResponse<ModelListResponse>> getModels(
      @RequestHeader("X-User-Id") String userId) {
    ModelListResponse response = llmService.getModels(userId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/models/{modelId}")
  @Operation(summary = "获取模型详情", description = "获取指定AI模型的详细信息")
  public ResponseEntity<ApiResponse<ModelInfoResponse>> getModelInfo(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "模型ID") @PathVariable String modelId) {
    ModelInfoResponse response = llmService.getModelInfo(userId, modelId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/usage")
  @Operation(summary = "获取使用情况", description = "获取用户的AI服务使用情况")
  public ResponseEntity<ApiResponse<UsageResponse>> getUsage(
      @RequestHeader("X-User-Id") String userId,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {
    UsageResponse response = llmService.getUsage(userId, startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
