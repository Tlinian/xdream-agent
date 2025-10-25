package com.xdream.knowledge.service.support;

import com.xdream.knowledge.config.KnowledgeProperties;
import com.xdream.knowledge.model.DocumentChunk;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 文本切分工具，将原始文档拆分成带重叠的片段。
 */
@Component
@RequiredArgsConstructor
public class DocumentChunkService {

    private final KnowledgeProperties knowledgeProperties;

    /**
     * 按配置拆分文本。
     *
     * @param content 原始文本
     * @return 切片列表
     */
    public List<DocumentChunk> split(String content) {
        if (StringUtils.isBlank(content)) {
            return List.of();
        }

        int chunkSize = knowledgeProperties.getDocument().getChunkSize();
        int overlap = knowledgeProperties.getDocument().getChunkOverlap();

        List<DocumentChunk> chunks = new ArrayList<>();
        int index = 0;
        int start = 0;
        int length = content.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            String piece = content.substring(start, end);
            int tokenCount = estimateTokens(piece);
            chunks.add(DocumentChunk.builder()
                    .index(index)
                    .content(piece)
                    .tokenCount(tokenCount)
                    .build());

            if (end >= length) {
                break;
            }
            start = end - overlap;
            if (start < 0) {
                start = 0;
            }
            index++;
        }
        return chunks;
    }

    /**
     * 粗略估算 token 数，使用字符数除以 2 的经验值。
     */
    private int estimateTokens(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        return Math.max(1, text.length() / 2);
    }
}

