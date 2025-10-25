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
  private static final int MAX_REACT_ITERATIONS = 5; // 鏈€澶ц凯浠ｆ鏁帮紝闃叉鏃犻檺寰幆

  // 浣跨敤Lombok鐨凘RequiredArgsConstructor锛屼笉闇€瑕佽嚜瀹氫箟鏋勯€犲嚱鏁?

  @PostConstruct
  public void init() {
    initializeAgents();
  }

  private void initializeAgents() {
    // 鍒涘缓閫氱敤鍔╂墜Agent
    ReActAgent generalAgent = new ReActAgent();
    generalAgent.setAgentId("general-assistant");
    generalAgent.setName("閫氱敤鍔╂墜");
    generalAgent.setDescription("涓€涓櫤鑳界殑閫氱敤鍔╂墜锛岃兘澶熷洖绛斿悇绉嶉棶棰樺苟鎻愪緵甯姪");

    // 娣诲姞鍙敤宸ュ叿
    generalAgent.getAvailableTools().add(createSearchTool());
    generalAgent.getAvailableTools().add(createCalculatorTool());
    generalAgent.getAvailableTools().add(createTimeTool());
    generalAgent.getAvailableTools().add(createWeatherTool());

    // 璁剧疆绯荤粺鎻愮ず璇?
    generalAgent.setSystemPrompt(buildSystemPrompt());

    agents.put(generalAgent.getAgentId(), generalAgent);
  }

  private ReActAgent.Tool createSearchTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("search");
    tool.setDescription("鎼滅储浜掕仈缃戣幏鍙栦俊鎭?);
    tool.setParameterSchema("{\"query\": \"鎼滅储鍏抽敭璇峔"}");
    tool.setExecutor(
        parameters -> {
          // 妯℃嫙鎼滅储鍔熻兘
          log.info("鎵ц鎼滅储: {}", parameters);
          return "鎼滅储鍒扮浉鍏充俊鎭細鏍规嵁鏈€鏂版暟鎹紝" + parameters + "鐨勭浉鍏冲唴瀹瑰寘鎷?..";
        });
    return tool;
  }

  private ReActAgent.Tool createCalculatorTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("calculator");
    tool.setDescription("鎵ц鏁板璁＄畻");
    tool.setParameterSchema("{\"expression\": \"鏁板琛ㄨ揪寮廫"}");
    tool.setExecutor(
        parameters -> {
          log.info("鎵ц璁＄畻: {}", parameters);
          try {
            // 绠€鍗曠殑琛ㄨ揪寮忚绠?
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
                return "閿欒锛氶櫎鏁颁笉鑳戒负0";
              }
            }
            return "鏃犳硶瑙ｆ瀽琛ㄨ揪寮忥細" + parameters;
          } catch (Exception e) {
            return "璁＄畻閿欒锛? + e.getMessage();
          }
        });
    return tool;
  }

  private ReActAgent.Tool createTimeTool() {
    ReActAgent.Tool tool = new ReActAgent.Tool();
    tool.setName("get_current_time");
    tool.setDescription("鑾峰彇褰撳墠鏃堕棿");
    tool.setParameterSchema("{}");
    tool.setExecutor(
        parameters -> {
          log.info("鑾峰彇褰撳墠鏃堕棿");
          // 浣跨敤鏇村弸濂界殑涓枃鏃堕棿鏍煎紡锛岄伩鍏嶅湪鍓嶇澶勭悊鏃跺嚭鐜伴棶棰?
          LocalDateTime now = LocalDateTime.now();
          return String.format(
              "褰撳墠鏃堕棿鏄細%d骞?d鏈?d鏃?%d:%02d:%02d",
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
    tool.setDescription("鑾峰彇澶╂皵淇℃伅");
    tool.setParameterSchema("{\"location\": \"鍦扮偣\"}");
    tool.setExecutor(
        parameters -> {
          log.info("鑾峰彇澶╂皵淇℃伅: {}", parameters);
          return "澶╂皵淇℃伅锛? + parameters + "浠婂ぉ鏅存湕锛屾俯搴?2掳C锛岄€傚悎澶栧嚭銆?;
        });
    return tool;
  }

  private String buildSystemPrompt() {
    return """
            浣犳槸涓€涓櫤鑳藉姪鎵嬶紝浣跨敤ReAct锛圧easoning and Acting锛夋ā寮忔潵瑙ｅ喅闂銆?

            ReAct妯″紡鐨勫伐浣滄祦绋嬶細
            1. Thought锛堟€濊€冿級锛氬垎鏋愰棶棰橈紝纭畾涓嬩竴姝ヨ鍔?
            2. Action锛堣鍔級锛氶€夋嫨鍚堥€傜殑宸ュ叿骞舵墽琛?
            3. Observation锛堣瀵燂級锛氳瀵熷伐鍏锋墽琛岀粨鏋?
            4. 閲嶅浠ヤ笂姝ラ鐩村埌寰楀埌鏈€缁堢瓟妗?

            鍙敤宸ュ叿锛?
            - search: 鎼滅储浜掕仈缃戣幏鍙栦俊鎭?
            - calculator: 鎵ц鏁板璁＄畻
            - get_current_time: 鑾峰彇褰撳墠鏃堕棿
            - get_weather: 鑾峰彇澶╂皵淇℃伅

            璇蜂互浠ヤ笅鏍煎紡鍥炲锛?
            Thought: 浣犵殑鎬濊€冭繃绋?
            Action: 宸ュ叿鍚嶇О
            Action Input: 宸ュ叿杈撳叆鍙傛暟

            濡傛灉浣犲凡缁忔湁浜嗘渶缁堢瓟妗堬紝璇蜂娇鐢ㄤ互涓嬫牸寮忥細
            Thought: 鎴戠幇鍦ㄧ煡閬撴渶缁堢瓟妗堜簡
            Final Answer: 鏈€缁堢瓟妗?
            """;
  }

  public ChatResponse processWithReAct(String userId, String agentId, ChatRequest request, FluxSink<StreamResponse> sink) {
    ReActAgent agent = agents.get(agentId);
    if (agent == null) {
      throw new IllegalArgumentException("Agent涓嶅瓨鍦? " + agentId);
    }

    log.info("ReAct Agent澶勭悊璇锋眰 - User: {}, Agent: {}, Message: {}", userId, agentId, request.getMessage());

    // 娓呴櫎涔嬪墠鐨勬€濊€冭繃绋?
    agent.getThoughtProcess().clear();

    String finalAnswer = null;
    int iteration = 0;

    // ReAct寰幆杩唬杩囩▼
    while (finalAnswer == null && iteration < MAX_REACT_ITERATIONS) {
      iteration++;
      log.info("ReAct杩唬 {} 寮€濮?, iteration);

      // 鏋勫缓ReAct鎻愮ず璇?
      String reactPrompt = buildReActPrompt(agent, request.getMessage());
      log.debug("鏋勫缓鐨凴eAct鎻愮ず璇? {}", reactPrompt);

      // 鍒涘缓鑱婂ぉ璇锋眰
      ChatRequest chatRequest = new ChatRequest();
      chatRequest.setMessage(reactPrompt);
      chatRequest.setModelType(llmProperties.getChat().getModel());
      chatRequest.setSystemPrompt(agent.getSystemPrompt());
      chatRequest.setMaxTokens(llmProperties.getChat().getMaxTokens());
      chatRequest.setTemperature(llmProperties.getChat().getTemperature()); // 闄嶄綆娓╁害浠ヨ幏寰楁洿绋冲畾鐨勬帹鐞?

      try {
        // 璋冪敤SiliconFlow API鑾峰彇鎬濊€冨拰琛屽姩
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
        log.debug("LLM鍘熷鍝嶅簲: {}", rawResponse);

        // 瑙ｆ瀽LLM鍝嶅簲锛屾鏌ユ槸鍚︾洿鎺ョ粰鍑轰簡鏈€缁堢瓟妗?
        finalAnswer = extractFinalAnswer(rawResponse);
        if (finalAnswer != null) {
          log.info("鑾峰彇鍒版渶缁堢瓟妗? {}", finalAnswer);
          continue;
        }

        // 瑙ｆ瀽鎬濊€?
        String thought = extractThought(rawResponse);
        if (thought != null) {
          agent.addThought(thought);
          log.info("杩唬 {} - 鎬濊€? {}", iteration, thought);
        }

        // 瑙ｆ瀽琛屽姩鍜屽弬鏁?
        String action = extractAction(rawResponse);
        String actionInput = extractActionInput(rawResponse);

        if (action != null && !action.isEmpty()) {
          agent.addAction(action, actionInput);
          log.info("杩唬 {} - 琛屽姩: {}, 鍙傛暟: {}", iteration, action, actionInput);

          // 鎵ц宸ュ叿骞惰幏鍙栬瀵熺粨鏋?
          String observation = executeTool(agent, action, actionInput);
          agent.addObservation(observation);
          log.info("杩唬 {} - 瑙傚療: {}", iteration, observation);
        } else {
          // 濡傛灉娌℃湁鏈夋晥鐨勮鍔紝缁撴潫寰幆骞朵娇鐢ㄥ師濮嬪搷搴斾綔涓烘渶缁堢瓟妗?
          finalAnswer = rawResponse;
          log.info("鏈壘鍒版湁鏁堢殑琛屽姩锛屼娇鐢ㄥ師濮嬪搷搴斾綔涓烘渶缁堢瓟妗?);
        }

      } catch (Exception e) {
        log.error("ReAct杩唬杩囩▼涓彂鐢熼敊璇? {}", e.getMessage(), e);
        // 鍙戠敓閿欒鏃讹紝浣跨敤閿欒淇℃伅浣滀负鏈€缁堢瓟妗?
        finalAnswer = "澶勭悊璇锋眰鏃跺彂鐢熼敊璇? " + e.getMessage();
      }
    }

    // 濡傛灉杈惧埌鏈€澶ц凯浠ｆ鏁颁絾浠嶆湭寰楀埌鏈€缁堢瓟妗堬紝浣跨敤褰撳墠淇℃伅鐢熸垚涓€涓瓟妗?
    if (finalAnswer == null) {
      finalAnswer = generateFallbackAnswer(userMessage, agent);
      log.info("杈惧埌鏈€澶ц凯浠ｆ鏁帮紝鐢熸垚鍥為€€绛旀: {}", finalAnswer);
    }

    // 鏋勫缓鍝嶅簲
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
        return "閿欒锛氭湭鐭ョ殑宸ュ叿鍚嶇О: " + action;
      }
      return tool.getExecutor().execute(actionInput);
    } catch (Exception e) {
      log.error("鎵ц宸ュ叿 {} 鏃跺彂鐢熼敊璇? {}", action, e.getMessage(), e);
      return "鎵ц宸ュ叿鏃跺彂鐢熼敊璇? " + e.getMessage();
    }
  }

  private String generateFallbackAnswer(String userMessage, ReActAgent agent) {
    StringBuilder fallbackAnswer = new StringBuilder();
    fallbackAnswer.append("鏍规嵁鎴戠殑鍒嗘瀽锛屼互涓嬫槸瀵归棶棰樼殑鍥炵瓟锛?);
    fallbackAnswer.append("\n\n鐢ㄦ埛闂锛?).append(userMessage);
    fallbackAnswer.append("\n\n鍒嗘瀽杩囩▼锛?);

    for (ReActAgent.ThoughtAction thoughtAction : agent.getThoughtProcess()) {
      if (thoughtAction.getThought() != null) {
        fallbackAnswer.append("\n鎬濊€? ").append(thoughtAction.getThought());
      }
      if (thoughtAction.getAction() != null) {
        fallbackAnswer.append("\n琛屽姩: ").append(thoughtAction.getAction());
        fallbackAnswer.append("\n琛屽姩鍙傛暟: ").append(thoughtAction.getActionInput());
      }
      if (thoughtAction.getObservation() != null) {
        fallbackAnswer.append("\n瑙傚療缁撴灉: ").append(thoughtAction.getObservation());
      }
      fallbackAnswer.append("\n");
    }

    fallbackAnswer.append("\n鐢变簬鏃堕棿闄愬埗锛屼互涓婃槸鎴戠殑鍒嗘瀽缁撴灉銆?);
    return fallbackAnswer.toString();
  }

  private int estimateTokenUsage(String prompt, String response) {
    // 绠€鍗曠殑浠ょ墝浼扮畻
    return (prompt.length() + response.length()) / 4; // 鍋囪骞冲潎姣忎釜浠ょ墝4涓瓧绗?
  }

  private String buildReActPrompt(ReActAgent agent, String userMessage) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("璇蜂娇鐢≧eAct妯″紡瑙ｅ喅浠ヤ笅闂锛歕n");
    prompt.append("闂锛?).append(userMessage).append("\n\n");

    // 娣诲姞宸ュ叿璇存槑
    prompt.append("鍙敤宸ュ叿锛歕n");
    for (ReActAgent.Tool tool : agent.getAvailableTools()) {
      prompt
          .append("- ")
          .append(tool.getName())
          .append(": ")
          .append(tool.getDescription())
          .append("\n")
          .append("  鍙傛暟鏍煎紡: ")
          .append(tool.getParameterSchema())
          .append("\n");
    }
    prompt.append("\n");

    // 娣诲姞褰撳墠鎬濊€冭繃绋嬪巻鍙?
    if (!agent.getThoughtProcess().isEmpty()) {
      prompt.append("鎬濊€冭繃绋嬪巻鍙诧細\n");
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

    prompt.append("璇锋牴鎹棶棰樺拰宸叉湁鐨勬€濊€冭繃绋嬪巻鍙诧紝缁х画鎬濊€冨苟鎻愪緵涓嬩竴姝ヨ鍔紝鎴栬€呯粰鍑烘渶缁堢瓟妗堛€?);

    return prompt.toString();
  }

  public List<ReActAgent> getAvailableAgents() {
    return new ArrayList<>(agents.values());
  }

  public ReActAgent getAgent(String agentId) {
    return agents.get(agentId);
  }
}
