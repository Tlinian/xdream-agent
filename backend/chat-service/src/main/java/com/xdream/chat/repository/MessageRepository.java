package com.xdream.chat.repository;

import com.xdream.chat.entity.Message;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

  Page<Message> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

  Page<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId, Pageable pageable);

  List<Message> findByConversationIdOrderByCreatedAtDesc(String conversationId);

  List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);

  Optional<Message> findByIdAndConversationId(String id, String conversationId);

  long countByConversationId(String conversationId);

  @Query(
      "SELECT m FROM Message m WHERE m.conversationId = :conversationId AND m.parentMessageId = :parentMessageId")
  List<Message> findByConversationIdAndParentMessageId(
      @Param("conversationId") String conversationId,
      @Param("parentMessageId") String parentMessageId);

  @Query(
      "SELECT m FROM Message m WHERE m.conversationId = :conversationId AND m.role = 'USER' ORDER BY m.createdAt DESC")
  List<Message> findUserMessagesByConversationId(@Param("conversationId") String conversationId);

  @Query(
      "SELECT m FROM Message m WHERE m.conversationId = :conversationId AND m.role = 'ASSISTANT' ORDER BY m.createdAt DESC")
  List<Message> findAssistantMessagesByConversationId(
      @Param("conversationId") String conversationId);

  @Query(
      "SELECT COUNT(m) FROM Message m WHERE m.conversationId = :conversationId AND m.role = :role")
  long countByConversationIdAndRole(
      @Param("conversationId") String conversationId, @Param("role") String role);

  void deleteByConversationId(String conversationId);
}
