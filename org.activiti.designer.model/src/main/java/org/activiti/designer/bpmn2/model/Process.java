package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class Process extends FlowElementsContainer {

  protected String name;
  protected boolean executable;
  protected String documentation;
  protected List<ActivitiListener> executionListeners = new ArrayList<ActivitiListener>();
  protected List<Lane> lanes = new ArrayList<Lane>();

  public String getDocumentation() {
    return documentation;
  }
  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public boolean isExecutable() {
    return executable;
  }
  public void setExecutable(boolean executable) {
    this.executable = executable;
  }
  public List<ActivitiListener> getExecutionListeners() {
    return executionListeners;
  }
  public void setExecutionListeners(List<ActivitiListener> executionListeners) {
    this.executionListeners = executionListeners;
  }
  public List<Lane> getLanes() {
    return lanes;
  }
  public void setLanes(List<Lane> lanes) {
    this.lanes = lanes;
  }
}
