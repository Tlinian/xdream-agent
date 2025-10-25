package com.xdream.llm.knowledge;

import com.xdream.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "knowledge-service",
    contextId = "llmKnowledgeClient",
    url = "${knowledge.base-url:}",
    path = "/api/knowledge")
public interface KnowledgeSearchClient {

  @PostMapping("/bases/{baseId}/search")
  ApiResponse<KnowledgeSearchResponse> search(
      @RequestHeader("X-User-Id") String userId,
      @PathVariable("baseId") String baseId,
      @RequestBody KnowledgeSearchRequest request);
}
