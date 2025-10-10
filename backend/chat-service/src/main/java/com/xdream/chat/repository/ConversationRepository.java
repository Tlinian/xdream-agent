package com.xdream.chat.repository;

import com.xdream.chat.entity.Conversation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

  Page<Conversation> findByUserIdOrderByLastActivityDesc(String userId, Pageable pageable);

  Page<Conversation> findByUserIdAndTitleContainingOrderByLastActivityDesc(
      String userId, String title, Pageable pageable);

  List<Conversation> findByUserIdOrderByLastActivityDesc(String userId);

  Optional<Conversation> findByIdAndUserId(String id, String userId);

  long countByUserId(String userId);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Conversation c SET c.messageCount = c.messageCount + 1, c.lastActivity = :now WHERE c.id = :conversationId")
  void incrementMessageCount(
      @Param("conversationId") String conversationId, @Param("now") LocalDateTime now);

  @Modifying
  @Transactional
  @Query("UPDATE Conversation c SET c.lastActivity = :now WHERE c.id = :conversationId")
  void updateLastActivity(
      @Param("conversationId") String conversationId, @Param("now") LocalDateTime now);

  @Query(
      "SELECT c FROM Conversation c WHERE c.userId = :userId AND c.pinned = true ORDER BY c.lastActivity DESC")
  List<Conversation> findPinnedConversations(@Param("userId") String userId);

  boolean existsByIdAndUserId(String id, String userId);
}
