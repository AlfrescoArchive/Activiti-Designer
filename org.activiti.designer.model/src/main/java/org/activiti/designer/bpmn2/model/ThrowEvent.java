package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class ThrowEvent extends Event {

  protected List<ActivitiListener> executionListeners = new ArrayList<ActivitiListener>();

  public List<ActivitiListener> getExecutionListeners() {
    return executionListeners;
  }

  public void setExecutionListeners(List<ActivitiListener> executionListeners) {
    this.executionListeners = executionListeners;
  }
}
