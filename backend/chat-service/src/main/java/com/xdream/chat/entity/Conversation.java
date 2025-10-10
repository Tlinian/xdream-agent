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
@Table(name = "conversations")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Conversation extends BaseEntity {

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "model_type", length = 50)
  private String modelType;

  @Column(name = "system_prompt", columnDefinition = "TEXT")
  private String systemPrompt;

  @Column(name = "is_pinned")
  @Builder.Default
  private Boolean pinned = false;

  @Column(name = "message_count")
  @Builder.Default
  private Integer messageCount = 0;

  @Column(name = "last_activity")
  private java.time.LocalDateTime lastActivity;
}
