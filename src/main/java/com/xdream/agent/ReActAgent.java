package com.xdream.agent;

import com.alibaba.fastjson.JSON;
import com.xdream.context.AgentContext;
import com.xdream.context.Message;
import com.xdream.llm.LLM;
import com.xdream.llm.LlmMessages;
import com.xdream.respone.LlmRespone;
import com.xdream.tools.ToolDescription;
import com.xdream.tools.ToolUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReActAgent {
    private LLM llm;
    private List<Message> messages = new ArrayList<>();

    // 初始化ReActAgent
    public ReActAgent() {
        // 初始化LLM
        llm = new LLM();
    }

    public String run(AgentContext context) throws ExecutionException, InterruptedException, JsonProcessingException {
        // 调用LLM
        while (true) {
            think(context);
            act(context);
            if (messages.get(messages.size() - 1).isFinished()) {
                break;
            }
        }

        return messages.get(messages.size() - 1).getContent();
    }

    private String replacePromptWithTools(){
        List<ToolDescription> allToolDescriptions = ToolUtil.getAllToolDescriptions();
        StringBuilder sb = new StringBuilder();
        Map<String, Object> functionMap = new HashMap<>();
        for (ToolDescription toolDescription : allToolDescriptions) {
            functionMap = new HashMap<>();
            functionMap.put("name", toolDescription.getName());
            functionMap.put("description", toolDescription.getDescription());
//            functionMap.put("parameters", ToolInterface.addFunctionNameParam(toolDescription.toParams(), toolDescription.getName()));
            sb.append(String.format("- `%s`\n```json %s ```\n", toolDescription.getName(), JSON.toJSONString(functionMap)));
            sb.append(toolDescription.getName()).append(": ").append(toolDescription.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public void think(AgentContext context) throws ExecutionException, InterruptedException, JsonProcessingException {
        // 调用LLM
        LlmMessages llmMessages = new LlmMessages();
        llmMessages.setSystemPrompt(context.getPrompt());
        llmMessages.setUserPrompt(context.getUserMessage());
        LlmRespone respone = llm.response(llmMessages);
        // 解析是否完成

        String thought = respone.getRespone().substring(respone.getRespone().indexOf("<thought>") + 9, respone.getRespone().indexOf("</thought>"));
        System.out.println("thought: " + thought);

        if (respone.getRespone().contains("<final_answer>")) {
            // 提取出<final_answer>
            String finalAnswer = respone.getRespone().substring(respone.getRespone().indexOf("<final_answer>") + 14, respone.getRespone().indexOf("</final_answer>"));
            messages.add(new Message("assistant", finalAnswer, true));
            return;
        }
        // 解析是否调用工具
        messages.add(new Message("assistant", respone.getRespone(), false));
    }

    public void act(AgentContext context) {
        if (messages.get(messages.size() - 1).isFinished()) {
            return;
        } else {
            throw new RuntimeException("not support");
            // 调用工具
//            return;
        }
    }
}
