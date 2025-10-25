package com.xdream.knowledge.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文档切片信息，包含内容与基础长度统计。
 */
@Data
@Builder
public class DocumentChunk {

    /** 分片序号 */
    private int index;

    /** 分片内容 */
    private String content;

    /** 简单 token 数估算 */
    private int tokenCount;
}

