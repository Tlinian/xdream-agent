package com.xdream.knowledge.service.support;

import org.postgresql.util.PGobject;
import com.xdream.knowledge.model.KnowledgeSegmentRecord;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 鍩轰簬 PostgreSQL + pgvector 鐨勫悜閲忓瓨鍌ㄦ搷浣溿€?
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeVectorStore {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    private boolean vectorEnabled;

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbName = metaData.getDatabaseProductName();
            this.vectorEnabled = dbName != null && dbName.toLowerCase().contains("postgresql");
            if (vectorEnabled) {
                log.info("妫€娴嬪埌 PostgreSQL锛屽皾璇曞垵濮嬪寲 pgvector 鎵╁睍");
                jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS knowledge_segments (" +
                        "id VARCHAR(64) PRIMARY KEY," +
                        "knowledge_base_id VARCHAR(64) NOT NULL," +
                        "document_id VARCHAR(64) NOT NULL," +
                        "chunk_index INT NOT NULL," +
                        "content TEXT NOT NULL," +
                        "token_count INT," +
                        "embedding vector(1536)," +
                        "created_at TIMESTAMP DEFAULT NOW()," +
                        "updated_at TIMESTAMP DEFAULT NOW()" +
                        ")");
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_kn_segments_base ON knowledge_segments(knowledge_base_id)");
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_kn_segments_doc ON knowledge_segments(document_id)");
                try {
                    jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_kn_segments_vector ON knowledge_segments USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100)");
                } catch (Exception indexEx) {
                    log.warn("鍒涘缓鍚戦噺绱㈠紩澶辫触锛屽彲鎵嬪姩鍒涘缓 ivfflat 绱㈠紩: {}", indexEx.getMessage());
                }
            } else {
                log.warn("褰撳墠鏁版嵁搴撻潪 PostgreSQL锛岀煡璇嗗簱鍚戦噺妫€绱㈠姛鑳藉皢涓嶅彲鐢?);
            }
        } catch (SQLException ex) {
            log.error("妫€娴嬫暟鎹簱绫诲瀷澶辫触锛岀煡璇嗗簱鍚戦噺妫€绱㈠彲鑳戒笉鍙敤", ex);
            this.vectorEnabled = false;
        }
    }

    /**
     * 鎵归噺鍐欏叆鍚戦噺鍒囩墖銆?
     */
    public void saveSegments(List<KnowledgeSegmentRecord> segments) {
        if (!vectorEnabled) {
            log.warn("鏈惎鐢ㄥ悜閲忔绱紝璺宠繃鍚戦噺鍐欏叆");
            return;
        }
        if (segments == null || segments.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO knowledge_segments (id, knowledge_base_id, document_id, chunk_index, content, token_count, embedding, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                KnowledgeSegmentRecord segment = segments.get(i);
                ps.setString(1, segment.getId());
                ps.setString(2, segment.getKnowledgeBaseId());
                ps.setString(3, segment.getDocumentId());
                ps.setInt(4, segment.getChunkIndex());
                ps.setString(5, segment.getContent());
                ps.setObject(6, segment.getTokenCount());
                ps.setObject(7, createPgVector(segment.getEmbedding()));
                LocalDateTime now = LocalDateTime.now();
                ps.setObject(8, now);
                ps.setObject(9, now);
            }

            @Override
            public int getBatchSize() {
                return segments.size();
            }
        });
    }

    /**
     * 鏍规嵁鏂囨。 ID 鍒犻櫎宸叉湁鍒囩墖銆?
     */
    public void deleteByDocumentId(String documentId) {
        if (!vectorEnabled) {
            return;
        }
        jdbcTemplate.update("DELETE FROM knowledge_segments WHERE document_id = ?", documentId);
    }

    /**
     * 鎵ц鍚戦噺鐩镐技搴︽绱€?
     */
    public List<KnowledgeSegmentRecord> search(String knowledgeBaseId, float[] queryEmbedding, int topK) {
        if (!vectorEnabled) {
            return List.of();
        }
        String sql = "SELECT id, document_id, chunk_index, content, token_count, 1 - (embedding <=> ?) AS similarity " +
                "FROM knowledge_segments WHERE knowledge_base_id = ? ORDER BY embedding <=> ? ASC LIMIT ?";
        PGobject vector = createPgVector(queryEmbedding);
        return jdbcTemplate.query(sql, ps -> {
            ps.setObject(1, vector);
            ps.setString(2, knowledgeBaseId);
            ps.setObject(3, vector);
            ps.setInt(4, topK);
        }, this::mapRow);
    }

    private KnowledgeSegmentRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return KnowledgeSegmentRecord.builder()
                .id(rs.getString("id"))
                .documentId(rs.getString("document_id"))
                .chunkIndex(rs.getInt("chunk_index"))
                .content(rs.getString("content"))
                .tokenCount(rs.getObject("token_count") != null ? rs.getInt("token_count") : null)
                .similarity(rs.getDouble("similarity"))
                .build();
    }

    public boolean isVectorEnabled() {
        return vectorEnabled;
    }
    /**
     * 缁熻鎸囧畾鐭ヨ瘑搴撶殑鍚戦噺鏁伴噺銆?
     */
    public long countByKnowledgeBaseId(String knowledgeBaseId) {
        if (!vectorEnabled) {
            return 0L;
        }
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM knowledge_segments WHERE knowledge_base_id = ?",
                Long.class,
                knowledgeBaseId
        );
        return count == null ? 0L : count;
    }

    /**
     * 缁熻鏌愪釜鏂囨。鐨勫垏鐗囨暟閲忋€?
     */
    public long countByDocumentId(String documentId) {
        if (!vectorEnabled) {
            return 0L;
        }
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM knowledge_segments WHERE document_id = ?",
                Long.class,
                documentId
        );
        return count == null ? 0L : count;
    
    private PGobject createPgVector(float[] embedding) throws SQLException {
        if (embedding == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(embedding[i]);
        }
        sb.append(']');
        PGobject vector = new PGobject();
        vector.setType('vector');
        vector.setValue(sb.toString());
        return vector;
    }

}



