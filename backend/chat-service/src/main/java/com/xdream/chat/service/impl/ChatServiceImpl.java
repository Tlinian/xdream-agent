package com.xdream.chat.service.impl;

import com.xdream.chat.dto.*;
import com.xdream.chat.entity.Conversation;
import com.xdream.chat.entity.Message;
import com.xdream.chat.mapper.ChatMapper;
import com.xdream.chat.repository.ConversationRepository;
import com.xdream.chat.repository.MessageRepository;
import com.xdream.chat.service.ChatService;
import com.xdream.common.dto.PageResponse;
import com.xdream.common.exception.BusinessException;
import com.xdream.common.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;
  private final ChatMapper chatMapper;

  @Value("${ai.personality.system-prompt:}")
  private String systemPrompt;

  @Value("${ai.personality.name:}")
  private String aiName;

  @Value("${ai.personality.greeting:}")
  private String greetingMessage;

  @Value("${ai.personality.help-message:}")
  private String helpMessage;

  /** 初始化AI角色设定，如果Spring配置为空则从配置文件加载 */
  @jakarta.annotation.PostConstruct
  public void initAIPersonality() {
    if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
      try {
        Yaml yaml = new Yaml();
        ClassPathResource resource = new ClassPathResource("ai-personality.yml");
        if (resource.exists()) {
          java.util.Map<String, Object> config = yaml.load(resource.getInputStream());
          java.util.Map<String, Object> personality =
              (java.util.Map<String, Object>)
                  ((java.util.Map<String, Object>) config.get("ai")).get("personality");

          systemPrompt = (String) personality.get("system-prompt");
          aiName = (String) personality.get("name");
          greetingMessage = (String) personality.get("greeting");
          helpMessage = (String) personality.get("help-message");

          log.info("AI personality loaded from configuration file");
        }
      } catch (Exception e) {
        log.warn(
            "Failed to load AI personality from config file, using defaults: {}", e.getMessage());
        // 使用默认设定
        systemPrompt = "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。无论是学习、工作、生活还是娱乐相关的问题，你都会尽力提供清晰、有用的信息。";
        aiName = "小梦";
        greetingMessage = "你好！我是AI智能体小梦，很高兴为您服务。我能帮助解答问题、提供建议、协助创作或处理各种任务。有什么可以帮助您的吗？";
        helpMessage =
            "作为AI智能体小梦，我可以帮助您：\n1. 回答各种问题\n2. 提供建议和信息\n3. 协助文本创作\n4. 解释概念和原理\n5. 处理学习、工作、生活、娱乐相关的任务\n请告诉我您需要什么帮助！";
      }
    }
    log.info(
        "AI personality initialized - Name: {}, System prompt length: {}",
        aiName,
        systemPrompt.length());
  }

  @Override
  public ConversationResponse createConversation(String userId, CreateConversationRequest request) {
    log.info("Creating conversation for user: {}", userId);

    Conversation conversation = chatMapper.toConversation(request);
    conversation.setUserId(userId);
    conversation.setMessageCount(0);
    conversation.setLastActivity(LocalDateTime.now());

    Conversation savedConversation = conversationRepository.save(conversation);
    return chatMapper.toConversationResponse(savedConversation);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ConversationResponse> getConversations(String userId, Pageable pageable) {
    log.info("Getting conversations for user: {}", userId);

    Page<Conversation> conversations =
        conversationRepository.findByUserIdOrderByLastActivityDesc(userId, pageable);

    return PageResponse.of(
        conversations.getContent().stream().map(chatMapper::toConversationResponse).toList(),
        conversations.getNumber(),
        conversations.getSize(),
        conversations.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public ConversationResponse getConversation(String userId, String conversationId) {
    log.info("Getting conversation: {} for user: {}", conversationId, userId);

    Conversation conversation =
        conversationRepository
            .findByIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_NOT_FOUND));

    return chatMapper.toConversationResponse(conversation);
  }

  @Override
  public ConversationResponse updateConversation(
      String userId, String conversationId, UpdateConversationRequest request) {
    log.info("Updating conversation: {} for user: {}", conversationId, userId);

    Conversation conversation =
        conversationRepository
            .findByIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_NOT_FOUND));

    chatMapper.updateConversationFromRequest(request, conversation);
    conversation.setUpdatedAt(LocalDateTime.now());

    Conversation updatedConversation = conversationRepository.save(conversation);
    return chatMapper.toConversationResponse(updatedConversation);
  }

  @Override
  public void deleteConversation(String userId, String conversationId) {
    log.info("Deleting conversation: {} for user: {}", conversationId, userId);

    Conversation conversation =
        conversationRepository
            .findByIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_NOT_FOUND));

    // 删除对话相关的所有消息
    messageRepository.deleteByConversationId(conversationId);

    // 删除对话
    conversationRepository.delete(conversation);
  }

  @Override
  public MessageResponse sendMessage(
      String userId, String conversationId, SendMessageRequest request) {
    log.info("Sending message to conversation: {} for user: {}", conversationId, userId);

    // 验证对话是否存在且属于该用户
    Conversation conversation =
        conversationRepository
            .findByIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_NOT_FOUND));

    // 创建用户消息
    Message userMessage =
        Message.builder()
            .conversationId(conversationId)
            .content(request.getContent())
            .messageType(request.getMessageType())
            .role("USER")
            .parentMessageId(request.getParentMessageId())
            .build();

    Message savedUserMessage = messageRepository.save(userMessage);

    // 更新对话的最后活动时间和消息计数
    conversationRepository.incrementMessageCount(conversationId, LocalDateTime.now());

    // 获取对话历史上下文
    List<Message> conversationHistory =
        messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId);
    // 限制为最近的10条消息
    if (conversationHistory.size() > 10) {
      conversationHistory = conversationHistory.subList(0, 10);
    }
    String context = buildConversationContext(conversationHistory, request.getContent());

    // 调用AI服务获取智能回复
    String aiResponseContent;
    int tokenUsage;
    String finishReason;

    try {
      // 创建LLM服务请求
      ChatRequest llmRequest = new ChatRequest();
      llmRequest.setMessage(context);
      llmRequest.setModelType(conversation.getModelType());

      // 调用LLM服务获取智能回复
      ChatResponse llmResponse = simpleChat(userId, llmRequest);
      aiResponseContent = llmResponse.getResponse();
      tokenUsage = llmResponse.getTokenUsage();
      finishReason = llmResponse.getFinishReason();

      log.info("AI response generated for user: {}, tokens: {}", userId, tokenUsage);

    } catch (Exception e) {
      log.error("Failed to get AI response, using fallback: {}", e.getMessage());
      // 如果AI服务调用失败，使用智能备用回复
      aiResponseContent = generateSmartFallbackResponse(request.getContent());
      tokenUsage = estimateTokenUsage(request.getContent(), aiResponseContent);
      finishReason = "stop";
    }

    // 创建AI回复消息
    Message aiMessage =
        Message.builder()
            .conversationId(conversationId)
            .content(aiResponseContent)
            .messageType("TEXT")
            .role("ASSISTANT")
            .parentMessageId(savedUserMessage.getId())
            .modelType(conversation.getModelType())
            .tokenUsage(tokenUsage)
            .finishReason(finishReason)
            .build();

    Message savedAiMessage = messageRepository.save(aiMessage);

    // 更新对话的最后活动时间
    conversationRepository.updateLastActivity(conversationId, LocalDateTime.now());

    return chatMapper.toMessageResponse(savedAiMessage);
  }

  @Override
  public String getAIPersonalityInfo() {
    return String.format(
        "AI角色: %s\n系统提示: %s",
        aiName != null && !aiName.trim().isEmpty() ? aiName : "小梦",
        systemPrompt != null && !systemPrompt.trim().isEmpty() ? systemPrompt : "AI智能体助手");
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageResponse> getMessages(
      String userId, String conversationId, Pageable pageable) {
    log.info("Getting messages for conversation: {} for user: {}", conversationId, userId);

    // 验证对话是否存在且属于该用户
    if (!conversationRepository.existsByIdAndUserId(conversationId, userId)) {
      throw new BusinessException(ErrorCode.CHAT_NOT_FOUND);
    }

    Page<Message> messages =
        messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

    return PageResponse.of(
        messages.getContent().stream().map(chatMapper::toMessageResponse).toList(),
        messages.getNumber(),
        messages.getSize(),
        messages.getTotalElements());
  }

  @Override
  public ChatStreamResponse streamChat(String userId, ChatRequest request) {
    log.info("Stream chat for user: {}", userId);

    // TODO: 集成真实的AI服务
    // 这里返回一个模拟的流式响应
    ChatStreamResponse response = new ChatStreamResponse();
    response.setStreamId(UUID.randomUUID().toString());
    response.setModelType(request.getModelType());
    response.setContent("这是一个模拟的流式AI回复：" + request.getMessage());
    response.setFinished(true);
    response.setFinishReason("stop");
    response.setTokenUsage(75);

    return response;
  }

  @Override
  public ChatResponse simpleChat(String userId, ChatRequest request) {
    log.info("Simple chat for user: {}", userId);

    // TODO: 集成真实的AI服务
    // 这里返回一个模拟的回复
    ChatResponse response = new ChatResponse();
    response.setId(UUID.randomUUID().toString());
    
    // 检查是否是"你是"相关的问题
    if (request.getMessage() != null && 
        (request.getMessage().contains("你是") || 
         request.getMessage().contains("你是谁") || 
         request.getMessage().contains("介绍一下你自己") ||
         request.getMessage().contains("你叫什么"))) {
      // 回复符合系统提示词设定的自我介绍
      String aiNameText = aiName != null && !aiName.trim().isEmpty() ? aiName : "小梦";
      String systemPromptText = systemPrompt != null && !systemPrompt.trim().isEmpty() ? systemPrompt : "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。";
      
      // 提取系统提示中的角色描述
      String roleDescription = "一个友善、专业的AI助手，致力于为用户提供准确、有用的信息和帮助。";
      if (systemPromptText.contains("智能体")) {
        int startIndex = systemPromptText.indexOf("智能体") + 3;
        if (systemPromptText.contains("。")) {
          int endIndex = systemPromptText.indexOf("。");
          if (startIndex < endIndex) {
            roleDescription = systemPromptText.substring(startIndex).trim();
          }
        }
      }
      
      response.setResponse(String.format("我是%s，%s", aiNameText, roleDescription));
    } else {
      // 对于其他问题，返回模拟的回复
      response.setResponse("这是一个模拟的AI回复：" + request.getMessage());
    }
    
    response.setModelType(request.getModelType());
    response.setTokenUsage(100);
    response.setFinishReason("stop");
    response.setResponseTime(LocalDateTime.now());
    response.setCreatedAt(LocalDateTime.now());

    return response;
  }

  /** 构建对话上下文 */
  private String buildConversationContext(
      List<Message> conversationHistory, String currentMessage) {
    StringBuilder context = new StringBuilder();

    // 添加系统提示 - 使用配置的AI角色设定
    if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
      context.append(systemPrompt).append("\n\n");
    } else {
      // 后备默认设定
      context.append(
          "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。无论是学习、工作、生活还是娱乐相关的问题，你都会尽力提供清晰、有用的信息。\n\n");
    }

    // 添加历史消息（最近的10条，按时间顺序）
    if (conversationHistory != null && !conversationHistory.isEmpty()) {
      context.append("对话历史：\n");
      // 反转顺序，让最早的消息在前
      for (int i = conversationHistory.size() - 1; i >= 0; i--) {
        Message message = conversationHistory.get(i);
        if ("USER".equals(message.getRole())) {
          context.append("用户: ").append(message.getContent()).append("\n");
        } else if ("ASSISTANT".equals(message.getRole())) {
          context.append("助手: ").append(message.getContent()).append("\n");
        }
      }
      context.append("\n");
    }

    // 添加当前消息
    context.append("用户当前问题: ").append(currentMessage);

    return context.toString();
  }

  /** 生成智能备用回复 */
  private String generateSmartFallbackResponse(String userMessage) {
    String lowerMessage = userMessage.toLowerCase();

    if (lowerMessage.contains("你好")
        || lowerMessage.contains("hi")
        || lowerMessage.contains("hello")) {
      return greetingMessage != null && !greetingMessage.trim().isEmpty()
          ? greetingMessage
          : "你好！我是AI智能体小梦，很高兴为您服务。我能帮助解答问题、提供建议、协助创作或处理各种任务。有什么可以帮助您的吗？";
    } else if (lowerMessage.contains("帮助") || lowerMessage.contains("功能")) {
      return helpMessage != null && !helpMessage.trim().isEmpty()
          ? helpMessage
          : "作为AI智能体小梦，我可以帮助您：\n1. 回答各种问题\n2. 提供建议和信息\n3. 协助文本创作\n4. 解释概念和原理\n5. 处理学习、工作、生活、娱乐相关的任务\n请告诉我您需要什么帮助！";
    } else if (lowerMessage.contains("天气")) {
      return "我目前无法获取实时天气信息，但建议您查看天气预报应用或网站获取准确的天气信息。";
    } else if (lowerMessage.contains("时间") || lowerMessage.contains("几点")) {
      return "当前时间是：" + LocalDateTime.now().toString();
    } else if (lowerMessage.contains("谢谢") || lowerMessage.contains("感谢")) {
      return "不客气！很高兴能帮助到您。如果还有其他问题，随时告诉我。";
    } else if (lowerMessage.length() < 10) {
      return "您说：\""
          + userMessage
          + "\"，这是一个很有意思的问题。基于我的理解，我可以为您提供以下建议：\n\n1. 首先，建议您明确具体需求\n2. 然后，可以分步骤来解决问题\n3. 如果需要更详细的帮助，请告诉我更多背景信息\n\n我可以为您提供更具体的指导和帮助。";
    } else {
      return "我理解您的问题关于\""
          + userMessage.substring(0, Math.min(userMessage.length(), 20))
          + "...\"。这是一个很有深度的问题。让我为您提供一些思路：\n\n1. 从基础概念出发理解这个问题\n2. 分析问题的核心要素\n3. 提供实用的解决方案\n\n如果您需要更具体的解释或有其他相关问题，请随时提出。";
    }
  }

  /** 估算token使用量 */
  private int estimateTokenUsage(String input, String output) {
    // 简单的token估算：假设每个字符约等于0.5个token
    return (int) ((input.length() + output.length()) * 0.5);
  }
}
