package com.xdream.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @Column(name = "id", length = 36)
  private String id;

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "preferences", columnDefinition = "TEXT")
  private String preferences;

  @Column(name = "role", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Role role = Role.USER;

  @Column(name = "enabled", nullable = false)
  @Builder.Default
  private Boolean enabled = true;

  @Column(name = "account_non_expired", nullable = false)
  @Builder.Default
  private Boolean accountNonExpired = true;

  @Column(name = "credentials_non_expired", nullable = false)
  @Builder.Default
  private Boolean credentialsNonExpired = true;

  @Column(name = "account_non_locked", nullable = false)
  @Builder.Default
  private Boolean accountNonLocked = true;

  public enum Role {
    USER,
    ADMIN
  }
}
