package com.xdream.tools;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ToolDescription {
    private final String name;
    private final String description;
    private final Map<String,Object> params;
    private final Class<? extends ToolInterface> toolClass;
}
