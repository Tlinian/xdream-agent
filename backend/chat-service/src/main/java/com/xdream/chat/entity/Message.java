package com.xdream.chat.entity;

import com.xdream.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "messages")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Message extends BaseEntity {

  @Column(name = "conversation_id", nullable = false)
  private String conversationId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "message_type", length = 20)
  @Builder.Default
  private String messageType = "TEXT";

  @Column(name = "role", length = 20)
  private String role; // USER, ASSISTANT, SYSTEM

  @Column(name = "parent_message_id")
  private String parentMessageId;

  @Column(name = "model_type", length = 50)
  private String modelType;

  @Column(name = "token_usage")
  private Integer tokenUsage;

  @Column(name = "finish_reason", length = 50)
  private String finishReason;
}
