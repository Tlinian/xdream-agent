package com.xdream.llm.service;

import com.xdream.llm.dto.*;
import reactor.core.publisher.Flux;

public interface LlmService {

  ChatResponse chat(String userId, ChatRequest request);

  Flux<StreamResponse> streamChat(String userId, ChatRequest request);

  EmbeddingResponse generateEmbeddings(String userId, EmbeddingRequest request);

  ImageGenerationResponse generateImage(String userId, ImageGenerationRequest request);

  ModelListResponse getModels(String userId);

  ModelInfoResponse getModelInfo(String userId, String modelId);

  UsageResponse getUsage(String userId, String startDate, String endDate);
}
