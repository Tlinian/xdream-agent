package com.xdream.context;

import com.xdream.config.YamlConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static com.xdream.config.YamlConfig.CONFIG_SYSTEM_PROMPT;

@AllArgsConstructor
@Data
@Builder
public class AgentContext {
    private final String userMessage;
    private final boolean isStream;
    private final String prompt = YamlConfig.getPrompts(CONFIG_SYSTEM_PROMPT);
}
