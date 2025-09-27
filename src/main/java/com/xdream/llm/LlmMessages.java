package com.xdream.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class LlmMessages {
    private List<Map<Object,Object>> messages;

    private List<String> tools;

    public void setSystemPrompt(String systemPrompt) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(Map.of("role", "system", "content", systemPrompt));
    }

    public void setUserPrompt(String userPrompt) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(Map.of("role", "user", "content", userPrompt));
    }

    public void clear() {
        messages.clear();
    }

}
