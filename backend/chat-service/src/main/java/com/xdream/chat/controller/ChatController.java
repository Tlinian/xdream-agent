package com.xdream.chat.controller;

import com.xdream.chat.dto.*;
import com.xdream.chat.service.ChatService;
import com.xdream.common.dto.ApiResponse;
import com.xdream.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "对话管理", description = "对话管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

  private final ChatService chatService;

  @PostMapping("/conversations")
  @Operation(summary = "创建对话", description = "创建新的对话会话")
  public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
      @RequestHeader("X-User-Id") String userId,
      @Valid @RequestBody CreateConversationRequest request) {
    ConversationResponse conversation = chatService.createConversation(userId, request);
    return ResponseEntity.ok(ApiResponse.success(conversation));
  }

  @GetMapping("/conversations")
  @Operation(summary = "获取对话列表", description = "获取用户的对话列表")
  public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> getConversations(
      @RequestHeader("X-User-Id") String userId, @PageableDefault(size = 20) Pageable pageable) {
    PageResponse<ConversationResponse> conversations =
        chatService.getConversations(userId, pageable);
    return ResponseEntity.ok(ApiResponse.success(conversations));
  }

  @GetMapping("/conversations/{conversationId}")
  @Operation(summary = "获取对话详情", description = "获取指定对话的详细信息")
  public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "对话ID") @PathVariable String conversationId) {
    ConversationResponse conversation = chatService.getConversation(userId, conversationId);
    return ResponseEntity.ok(ApiResponse.success(conversation));
  }

  @PutMapping("/conversations/{conversationId}")
  @Operation(summary = "更新对话", description = "更新对话信息")
  public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "对话ID") @PathVariable String conversationId,
      @Valid @RequestBody UpdateConversationRequest request) {
    ConversationResponse conversation =
        chatService.updateConversation(userId, conversationId, request);
    return ResponseEntity.ok(ApiResponse.success(conversation));
  }

  @DeleteMapping("/conversations/{conversationId}")
  @Operation(summary = "删除对话", description = "删除指定对话")
  public ResponseEntity<ApiResponse<Void>> deleteConversation(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "对话ID") @PathVariable String conversationId) {
    chatService.deleteConversation(userId, conversationId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/conversations/{conversationId}/messages")
  @Operation(summary = "发送消息", description = "在对话中发送消息")
  public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "对话ID") @PathVariable String conversationId,
      @Valid @RequestBody SendMessageRequest request) {
    MessageResponse message = chatService.sendMessage(userId, conversationId, request);
    return ResponseEntity.ok(ApiResponse.success(message));
  }

  @GetMapping("/conversations/{conversationId}/messages")
  @Operation(summary = "获取消息列表", description = "获取对话中的消息列表")
  public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getMessages(
      @RequestHeader("X-User-Id") String userId,
      @Parameter(description = "对话ID") @PathVariable String conversationId,
      @PageableDefault(size = 50) Pageable pageable) {
    PageResponse<MessageResponse> messages =
        chatService.getMessages(userId, conversationId, pageable);
    return ResponseEntity.ok(ApiResponse.success(messages));
  }

  @PostMapping("/stream")
  @Operation(summary = "流式对话", description = "使用流式方式获取AI回复")
  public ResponseEntity<ApiResponse<ChatStreamResponse>> streamChat(
      @RequestHeader("X-User-Id") String userId, @Valid @RequestBody ChatRequest request) {
    ChatStreamResponse response = chatService.streamChat(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/simple")
  @Operation(summary = "简单对话", description = "简单的单次对话请求")
  public ResponseEntity<ApiResponse<ChatResponse>> simpleChat(
      @RequestHeader("X-User-Id") String userId, @Valid @RequestBody ChatRequest request) {
    ChatResponse response = chatService.simpleChat(userId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/ai-personality")
  @Operation(summary = "获取AI角色设定", description = "获取当前AI角色的设定信息")
  public ResponseEntity<ApiResponse<String>> getAIPersonalityInfo() {
    String personalityInfo = chatService.getAIPersonalityInfo();
    return ResponseEntity.ok(ApiResponse.success(personalityInfo));
  }
}
