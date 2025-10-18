package com.xdream.llm.agent;

import com.xdream.llm.client.BaseLlmClient;
import com.xdream.llm.config.LlmProperties;
import com.xdream.llm.dto.ChatRequest;
import com.xdream.llm.dto.ChatResponse;
import com.xdream.llm.dto.StreamResponse;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReActAgentService {

  private final BaseLlmClient client;
  private final LlmProperties llmProperties;

  private final Map<String, ReActAgent> agents = new HashMap<>();
  private static final int MAX_REACT_ITERATIONS = 5; // 最大迭代次数，防止无限循环

  // 使用Lombok的@RequiredArgsConstructor，不需要自定义构造函数

  @PostConstruct
  public void init() {
    initializeAgents();
  }

  private void initializeAgents() {
    // 创建通用助手Agent
    ReActAgent generalAgent = new ReActAgent();
    generalAgent.setAgentId("general-assistant");
    generalAgent.setName("通用助手");
    generalAgent.setDescription("一个智能的通用助手，能够回答各种问题并提供帮助");

    // 添加可用工具
    generalAgent.getAvailableTools().add(createSearchTool());
    generalAgent.getAvailableTools().add(createCalculatorTool());
    generalAgent.getAvailableTools().add(createTimeTool());
    generalAgent.getAvailableTools().add(createWeatherTool());

    // 设置系统提示词
    generalAgent.setSystemPrompt(buildSystemPrompt());

    agents.put(generalAgent.getAgentId(), generalAgent);
  }

  private ReActAgent.Tool createSearchTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("search");
    tool.setDescription("搜索互联网获取信息");
    tool.setParameterSchema("{\"query\": \"搜索关键词\"}");
    tool.setExecutor(
        parameters -> {
          // 模拟搜索功能
          log.info("执行搜索: {}", parameters);
          return "搜索到相关信息：根据最新数据，" + parameters + "的相关内容包括...";
        });
    return tool;
  }

  private ReActAgent.Tool createCalculatorTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("calculator");
    tool.setDescription("执行数学计算");
    tool.setParameterSchema("{\"expression\": \"数学表达式\"}");
    tool.setExecutor(
        parameters -> {
          log.info("执行计算: {}", parameters);
          try {
            // 简单的表达式计算
            if (parameters.contains("+")) {
              String[] parts = parameters.split("\\+");
              double a = Double.parseDouble(parts[0].trim());
              double b = Double.parseDouble(parts[1].trim());
              return String.valueOf(a + b);
            } else if (parameters.contains("-")) {
              String[] parts = parameters.split("-");
              double a = Double.parseDouble(parts[0].trim());
              double b = Double.parseDouble(parts[1].trim());
              return String.valueOf(a - b);
            } else if (parameters.contains("*")) {
              String[] parts = parameters.split("\\*");
              double a = Double.parseDouble(parts[0].trim());
              double b = Double.parseDouble(parts[1].trim());
              return String.valueOf(a * b);
            } else if (parameters.contains("/")) {
              String[] parts = parameters.split("/");
              double a = Double.parseDouble(parts[0].trim());
              double b = Double.parseDouble(parts[1].trim());
              if (b != 0) {
                return String.valueOf(a / b);
              } else {
                return "错误：除数不能为0";
              }
            }
            return "无法解析表达式：" + parameters;
          } catch (Exception e) {
            return "计算错误：" + e.getMessage();
          }
        });
    return tool;
  }

  private ReActAgent.Tool createTimeTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("get_current_time");
    tool.setDescription("获取当前时间");
    tool.setParameterSchema("{}");
    tool.setExecutor(
        parameters -> {
          log.info("获取当前时间");
          // 使用更友好的中文时间格式，避免在前端处理时出现问题
          LocalDateTime now = LocalDateTime.now();
          return String.format(
              "当前时间是：%d年%d月%d日 %d:%02d:%02d",
              now.getYear(),
              now.getMonthValue(),
              now.getDayOfMonth(),
              now.getHour(),
              now.getMinute(),
              now.getSecond());
        });
    return tool;
  }

  private ReActAgent.Tool createWeatherTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("get_weather");
    tool.setDescription("获取天气信息");
    tool.setParameterSchema("{\"location\": \"地点\"}");
    tool.setExecutor(
        parameters -> {
          log.info("获取天气信息: {}", parameters);
          return "天气信息：" + parameters + "今天晴朗，温度22°C，适合外出。";
        });
    return tool;
  }

  private String buildSystemPrompt() {
    return """
            你是一个智能助手，使用ReAct（Reasoning and Acting）模式来解决问题。

            ReAct模式的工作流程：
            1. Thought（思考）：分析问题，确定下一步行动
            2. Action（行动）：选择合适的工具并执行
            3. Observation（观察）：观察工具执行结果
            4. 重复以上步骤直到得到最终答案

            可用工具：
            - search: 搜索互联网获取信息
            - calculator: 执行数学计算
            - get_current_time: 获取当前时间
            - get_weather: 获取天气信息

            请以以下格式回复：
            Thought: 你的思考过程
            Action: 工具名称
            Action Input: 工具输入参数

            如果你已经有了最终答案，请使用以下格式：
            Thought: 我现在知道最终答案了
            Final Answer: 最终答案
            """;
  }

  public ChatResponse processWithReAct(String userId, String agentId, ChatRequest request, FluxSink<StreamResponse> sink) {
    ReActAgent agent = agents.get(agentId);
    if (agent == null) {
      throw new IllegalArgumentException("Agent不存在: " + agentId);
    }

    log.info("ReAct Agent处理请求 - User: {}, Agent: {}, Message: {}", userId, agentId, request.getMessage());

    // 清除之前的思考过程
    agent.getThoughtProcess().clear();

    String finalAnswer = null;
    int iteration = 0;

    // ReAct循环迭代过程
    while (finalAnswer == null && iteration < MAX_REACT_ITERATIONS) {
      iteration++;
      log.info("ReAct迭代 {} 开始", iteration);

      // 构建ReAct提示词
      String reactPrompt = buildReActPrompt(agent, request.getMessage());
      log.debug("构建的ReAct提示词: {}", reactPrompt);

      // 创建聊天请求
      ChatRequest chatRequest = new ChatRequest();
      chatRequest.setMessage(reactPrompt);
      chatRequest.setModelType(llmProperties.getChat().getModel());
      chatRequest.setSystemPrompt(agent.getSystemPrompt());
      chatRequest.setMaxTokens(llmProperties.getChat().getMaxTokens());
      chatRequest.setTemperature(llmProperties.getChat().getTemperature()); // 降低温度以获得更稳定的推理

      try {
        // 调用SiliconFlow API获取思考和行动
        ChatResponse llmResponse = client.chat(userId, chatRequest);
        String rawResponse = llmResponse.getResponse();
        StreamResponse streamResponse = StreamResponse.builder()
            .streamId(llmResponse.getId())
            .modelType(llmResponse.getModelType())
            .content(rawResponse)
            .finished(false)
            .finishReason(llmResponse.getFinishReason())
            .tokenUsage(llmResponse.getTokenUsage())
            .build();
        sink.next(streamResponse);
        log.debug("LLM原始响应: {}", rawResponse);

        // 解析LLM响应，检查是否直接给出了最终答案
        finalAnswer = extractFinalAnswer(rawResponse);
        if (finalAnswer != null) {
          log.info("获取到最终答案: {}", finalAnswer);
          continue;
        }

        // 解析思考
        String thought = extractThought(rawResponse);
        if (thought != null) {
          agent.addThought(thought);
          log.info("迭代 {} - 思考: {}", iteration, thought);
        }

        // 解析行动和参数
        String action = extractAction(rawResponse);
        String actionInput = extractActionInput(rawResponse);

        if (action != null && !action.isEmpty()) {
          agent.addAction(action, actionInput);
          log.info("迭代 {} - 行动: {}, 参数: {}", iteration, action, actionInput);

          // 执行工具并获取观察结果
          String observation = executeTool(agent, action, actionInput);
          agent.addObservation(observation);
          log.info("迭代 {} - 观察: {}", iteration, observation);
        } else {
          // 如果没有有效的行动，结束循环并使用原始响应作为最终答案
          finalAnswer = rawResponse;
          log.info("未找到有效的行动，使用原始响应作为最终答案");
        }

      } catch (Exception e) {
        log.error("ReAct迭代过程中发生错误: {}", e.getMessage(), e);
        // 发生错误时，使用错误信息作为最终答案
        finalAnswer = "处理请求时发生错误: " + e.getMessage();
      }
    }

    // 如果达到最大迭代次数但仍未得到最终答案，使用当前信息生成一个答案
    if (finalAnswer == null) {
      finalAnswer = generateFallbackAnswer(userMessage, agent);
      log.info("达到最大迭代次数，生成回退答案: {}", finalAnswer);
    }

    // 构建响应
    ChatResponse response = new ChatResponse();
    response.setId(UUID.randomUUID().toString());
    response.setResponse(finalAnswer);
    response.setModelType(llmProperties.getChat().getModel());
    response.setTokenUsage(estimateTokenUsage(userMessage, finalAnswer));
    response.setFinishReason("stop");
    response.setResponseTime(LocalDateTime.now());
    response.setCreatedAt(LocalDateTime.now());

    return response;
  }

  private String extractFinalAnswer(String response) {
    if (response.contains("Final Answer:")) {
      String[] parts = response.split("Final Answer:");
      if (parts.length > 1) {
        return parts[1].trim();
      }
    }
    return null;
  }

  private String extractThought(String response) {
    if (response.contains("Thought: ")) {
      String startTag = "Thought: ";
      int startIndex = response.indexOf(startTag) + startTag.length();
      int endIndex = response.indexOf("\n", startIndex);
      if (endIndex > startIndex) {
        return response.substring(startIndex, endIndex).trim();
      } else {
        return response.substring(startIndex).trim();
      }
    }
    return null;
  }

  private String extractAction(String response) {
    if (response.contains("Action: ")) {
      String startTag = "Action: ";
      int startIndex = response.indexOf(startTag) + startTag.length();
      int endIndex = response.indexOf("\n", startIndex);
      if (endIndex > startIndex) {
        return response.substring(startIndex, endIndex).trim();
      } else {
        return response.substring(startIndex).trim();
      }
    }
    return null;
  }

  private String extractActionInput(String response) {
    if (response.contains("Action Input: ")) {
      String startTag = "Action Input: ";
      int startIndex = response.indexOf(startTag) + startTag.length();
      int endIndex = response.indexOf("\n", startIndex);
      if (endIndex > startIndex) {
        return response.substring(startIndex, endIndex).trim();
      } else {
        return response.substring(startIndex).trim();
      }
    }
    return null;
  }

  private String executeTool(ReActAgent agent, String action, String actionInput) {
    try {
      ReActAgent.Tool tool = agent.getTool(action);
      if (tool == null) {
        return "错误：未知的工具名称: " + action;
      }
      return tool.getExecutor().execute(actionInput);
    } catch (Exception e) {
      log.error("执行工具 {} 时发生错误: {}", action, e.getMessage(), e);
      return "执行工具时发生错误: " + e.getMessage();
    }
  }

  private String generateFallbackAnswer(String userMessage, ReActAgent agent) {
    StringBuilder fallbackAnswer = new StringBuilder();
    fallbackAnswer.append("根据我的分析，以下是对问题的回答：");
    fallbackAnswer.append("\n\n用户问题：").append(userMessage);
    fallbackAnswer.append("\n\n分析过程：");

    for (ReActAgent.ThoughtAction thoughtAction : agent.getThoughtProcess()) {
      if (thoughtAction.getThought() != null) {
        fallbackAnswer.append("\n思考: ").append(thoughtAction.getThought());
      }
      if (thoughtAction.getAction() != null) {
        fallbackAnswer.append("\n行动: ").append(thoughtAction.getAction());
        fallbackAnswer.append("\n行动参数: ").append(thoughtAction.getActionInput());
      }
      if (thoughtAction.getObservation() != null) {
        fallbackAnswer.append("\n观察结果: ").append(thoughtAction.getObservation());
      }
      fallbackAnswer.append("\n");
    }

    fallbackAnswer.append("\n由于时间限制，以上是我的分析结果。");
    return fallbackAnswer.toString();
  }

  private int estimateTokenUsage(String prompt, String response) {
    // 简单的令牌估算
    return (prompt.length() + response.length()) / 4; // 假设平均每个令牌4个字符
  }

  private String buildReActPrompt(ReActAgent agent, String userMessage) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("请使用ReAct模式解决以下问题：\n");
    prompt.append("问题：").append(userMessage).append("\n\n");

    // 添加工具说明
    prompt.append("可用工具：\n");
    for (ReActAgent.Tool tool : agent.getAvailableTools()) {
      prompt
          .append("- ")
          .append(tool.getName())
          .append(": ")
          .append(tool.getDescription())
          .append("\n")
          .append("  参数格式: ")
          .append(tool.getParameterSchema())
          .append("\n");
    }
    prompt.append("\n");

    // 添加当前思考过程历史
    if (!agent.getThoughtProcess().isEmpty()) {
      prompt.append("思考过程历史：\n");
      for (ReActAgent.ThoughtAction thoughtAction : agent.getThoughtProcess()) {
        if (thoughtAction.getThought() != null) {
          prompt.append("Thought: ").append(thoughtAction.getThought()).append("\n");
        }
        if (thoughtAction.getAction() != null) {
          prompt.append("Action: ").append(thoughtAction.getAction()).append("\n");
          prompt.append("Action Input: ").append(thoughtAction.getActionInput()).append("\n");
        }
        if (thoughtAction.getObservation() != null) {
          prompt.append("Observation: ").append(thoughtAction.getObservation()).append("\n");
        }
        prompt.append("\n");
      }
    }

    prompt.append("请根据问题和已有的思考过程历史，继续思考并提供下一步行动，或者给出最终答案。");

    return prompt.toString();
  }

  public List<ReActAgent> getAvailableAgents() {
    return new ArrayList<>(agents.values());
  }

  public ReActAgent getAgent(String agentId) {
    return agents.get(agentId);
  }
}
