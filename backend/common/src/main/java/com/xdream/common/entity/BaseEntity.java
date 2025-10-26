package com.xdream.common.entity;

import java.time.LocalDateTime;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {

  @Id private String id;

  @CreatedDate 
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate 
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  @Builder.Default private Boolean deleted = false;
}
