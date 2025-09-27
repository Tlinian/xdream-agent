package com.xdream.tools;

import lombok.Data;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ToolUtil {
    // 通过类加载方式，获取所有的ToolInterface实现类的ToolDescription描述
    
    /**
     * 获取所有ToolInterface实现类的ToolDescription描述
     * @return ToolDescription列表
     */
    public static List<ToolDescription> getAllToolDescriptions() {
        List<ToolDescription> descriptions = new ArrayList<>();
        
        try {
            // 扫描com.dream.tools包下的所有类
            Reflections reflections = new Reflections("com.dream.tools");
            
            // 获取所有实现了ToolInterface的类
            Set<Class<? extends ToolInterface>> toolClasses = reflections.getSubTypesOf(ToolInterface.class);
            
            // 为每个实现类创建实例并获取ToolDescription
            for (Class<? extends ToolInterface> toolClass : toolClasses) {
                try {
                    // 创建实例
                    ToolInterface toolInstance = toolClass.getDeclaredConstructor().newInstance();
                    // 获取ToolDescription并添加到列表
                    descriptions.add(toolInstance.toolDescription());
                } catch (Exception e) {
                    // 处理实例化失败的情况
                    System.err.println("无法实例化工具类: " + toolClass.getName() + ", 错误: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return descriptions;
    }

    public static String getToolName(ToolDescription toolDescription,List<Object> params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 通过toolDescription获取到相应的工具并运行该工具
        ToolInterface toolInstance = toolDescription.getToolClass().getDeclaredConstructor().newInstance();
        return toolInstance.run(params);
    }
}
