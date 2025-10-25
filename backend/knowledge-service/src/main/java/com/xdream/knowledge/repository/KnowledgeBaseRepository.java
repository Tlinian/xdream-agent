package com.xdream.knowledge.repository;

import com.xdream.knowledge.entity.KnowledgeBaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识库存储仓库。
 */
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseEntity, String> {

    /**
     * 根据用户筛选知识库列表。
     *
     * @param ownerId 用户 ID
     * @return 知识库集合
     */
    List<KnowledgeBaseEntity> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    /**
     * 查询名称是否重复。
     *
     * @param ownerId 用户 ID
     * @param name    知识库名称
     * @return 匹配数量
     */
    long countByOwnerIdAndNameIgnoreCase(String ownerId, String name);
}

