package org.activiti.designer.bpmn2.model;

public class Gateway extends FlowNode {

  protected String defaultFlow;

  public String getDefaultFlow() {
    return defaultFlow;
  }

  public void setDefaultFlow(String defaultFlow) {
    this.defaultFlow = defaultFlow;
  }
}
