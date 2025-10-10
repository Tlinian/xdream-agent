package com.xdream.chat.mapper;

import com.xdream.chat.dto.*;
import com.xdream.chat.entity.Conversation;
import com.xdream.chat.entity.Message;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @org.mapstruct.Builder(buildMethod = "build"))
public interface ChatMapper {

  Conversation toConversation(CreateConversationRequest request);

  ConversationResponse toConversationResponse(Conversation conversation);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateConversationFromRequest(
      UpdateConversationRequest request, @MappingTarget Conversation conversation);

  MessageResponse toMessageResponse(Message message);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Message toMessage(SendMessageRequest request);
}
