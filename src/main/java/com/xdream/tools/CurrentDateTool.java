package com.xdream.tools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
public class CurrentDateTool implements ToolInterface {
    @Override
    public String getToolName() {
        return toolDescription().getName();
    }

    @Override
    public String run(List<Object> param) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public ToolDescription toolDescription() {
        // 工具描述
        Map<String, Object> taskParam = new HashMap<>();
        // 入参描述


        Map<String, Object> parameters = new HashMap<>();
        // 返回类型
        parameters.put("type", "string");
        // 入参
        Map<String, Object> properties = new HashMap<>();

        // 入参描述

        parameters.put("properties", properties);
        parameters.put("required", Collections.emptyList());
        return new ToolDescription(CurrentDateTool.class.getName(), "获取当前日期","{}", CurrentDateTool.class);
    }
}
