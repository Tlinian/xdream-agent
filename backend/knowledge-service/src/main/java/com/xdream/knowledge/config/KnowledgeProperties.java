package com.xdream.knowledge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 知识库相关配置，映射 application.yml 中的 knowledge 节点。
 */
@Data
@Component
@ConfigurationProperties(prefix = "knowledge")
public class KnowledgeProperties {

    /** 文档处理配置 */
    private DocumentProperties document = new DocumentProperties();

    /** 向量检索配置 */
    private VectorSearchProperties vectorSearch = new VectorSearchProperties();

    /** 检索增强生成（RAG）配置 */
    private RetrievalProperties retrieval = new RetrievalProperties();

    @Data
    public static class DocumentProperties {
        /** 单段切分最大字符数 */
        private int chunkSize = 1000;
        /** 相邻分片重叠字符数 */
        private int chunkOverlap = 200;
        /** 支持的文件类型 */
        private String supportedTypes = "txt,pdf,doc,docx,md";
    }

    @Data
    public static class VectorSearchProperties {
        /** 相似度阈值 */
        private double similarityThreshold = 0.6;
        /** 默认返回结果数量 */
        private int maxResults = 10;
        /** 向量维度 */
        private int indexDimension = 1536;
    }

    @Data
    public static class RetrievalProperties {
        /** 默认召回条数 */
        private int topK = 4;
        /** 重排序后保留数量 */
        private int rerankTopK = 3;
        /** 默认是否附加引用 */
        private boolean appendCitationByDefault = true;
    }
}

