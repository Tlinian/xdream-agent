package com.xdream.llm.agent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ReActAgent {

  private String agentId;
  private String name;
  private String description;
  private List<Tool> availableTools;
  private String systemPrompt;
  private List<ThoughtAction> thoughtProcess;

  public ReActAgent() {
    this.thoughtProcess = new ArrayList<>();
    this.availableTools = new ArrayList<>();
  }

  @Data
  public static class Tool {
    private String name;
    private String description;
    private String parameterSchema;
    private ToolExecutor executor;

    public interface ToolExecutor {
      String execute(String parameters) throws Exception;
    }
  }

  @Data
  public static class ThoughtAction {
    private int step;
    private LocalDateTime timestamp;
    private String thought;
    private String action;
    private String actionInput;
    private String observation;
    private boolean finalAnswer;

    public ThoughtAction() {
      this.timestamp = LocalDateTime.now();
    }
  }

  public void addThought(String thought) {
    ThoughtAction thoughtAction = new ThoughtAction();
    thoughtAction.setStep(thoughtProcess.size() + 1);
    thoughtAction.setThought(thought);
    thoughtProcess.add(thoughtAction);
  }

  public void addAction(String action, String actionInput) {
    if (!thoughtProcess.isEmpty()) {
      ThoughtAction last = thoughtProcess.get(thoughtProcess.size() - 1);
      last.setAction(action);
      last.setActionInput(actionInput);
    }
  }

  public void addObservation(String observation) {
    if (!thoughtProcess.isEmpty()) {
      ThoughtAction last = thoughtProcess.get(thoughtProcess.size() - 1);
      last.setObservation(observation);
    }
  }

  public void markFinalAnswer() {
    if (!thoughtProcess.isEmpty()) {
      ThoughtAction last = thoughtProcess.get(thoughtProcess.size() - 1);
      last.setFinalAnswer(true);
    }
  }

  public Tool getTool(String name) {
    return availableTools.stream()
        .filter(tool -> tool.getName().equals(name))
        .findFirst()
        .orElse(null);
  }
}
