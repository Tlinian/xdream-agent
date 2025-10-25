package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 知识库列表响应。
 */
@Data
@Schema(description = "知识库列表响应")
public class KnowledgeBaseListResponse {

    @Schema(description = "知识库列表")
    private List<KnowledgeBaseResponse> items;
}

