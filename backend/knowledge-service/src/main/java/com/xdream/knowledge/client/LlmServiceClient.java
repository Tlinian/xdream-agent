package com.xdream.knowledge.client;

import com.xdream.common.dto.ApiResponse;
import com.xdream.knowledge.dto.EmbeddingRequestDto;
import com.xdream.knowledge.dto.EmbeddingResponseDto;
import com.xdream.knowledge.dto.RerankRequestDto;
import com.xdream.knowledge.dto.RerankResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 对接 LLM 服务的 Feign Client，用于获取嵌入向量及重排序结果。
 */
@FeignClient(name = "llm-service", url = "http://localhost:8084", path = "/api/llm", contextId = "knowledgeLlmClient")
public interface LlmServiceClient {

    /**
     * 请求生成文本嵌入向量。
     */
    @PostMapping("/embeddings")
    ApiResponse<EmbeddingResponseDto> embedding(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody EmbeddingRequestDto request
    );

    /**
     * 请求执行重排序。
     */
    @PostMapping("/rerank")
    ApiResponse<RerankResponseDto> rerank(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody RerankRequestDto request
    );
}

