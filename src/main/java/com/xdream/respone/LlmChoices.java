package com.xdream.respone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlmChoices {
    private int index;
    private Map<String,String> delta;
    private String finish_reason;
}
