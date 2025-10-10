package com.xdream.chat.service;

import com.xdream.chat.dto.*;
import com.xdream.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ChatService {

  ConversationResponse createConversation(String userId, CreateConversationRequest request);

  PageResponse<ConversationResponse> getConversations(String userId, Pageable pageable);

  ConversationResponse getConversation(String userId, String conversationId);

  ConversationResponse updateConversation(
      String userId, String conversationId, UpdateConversationRequest request);

  void deleteConversation(String userId, String conversationId);

  MessageResponse sendMessage(String userId, String conversationId, SendMessageRequest request);

  PageResponse<MessageResponse> getMessages(
      String userId, String conversationId, Pageable pageable);

  ChatStreamResponse streamChat(String userId, ChatRequest request);

  ChatResponse simpleChat(String userId, ChatRequest request);

  /**
   * 获取当前AI角色设定信息
   *
   * @return AI角色信息字符串
   */
  String getAIPersonalityInfo();
}
