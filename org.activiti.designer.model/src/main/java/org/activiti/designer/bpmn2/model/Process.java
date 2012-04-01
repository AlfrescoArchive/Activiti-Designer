package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class Process extends FlowElementsContainer {

  protected String name;
  protected boolean executable;
  protected String namespace;
  protected String documentation;
  protected List<ActivitiListener> executionListeners = new ArrayList<ActivitiListener>();
  protected List<Signal> signals = new ArrayList<Signal>();

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
  public String getNamespace() {
    return namespace;
  }
  public void setNamespace(String namespace) {
    this.namespace = namespace;
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
  public List<Signal> getSignals() {
    return signals;
  }
  public void setSignals(List<Signal> signals) {
    this.signals = signals;
  }
}
