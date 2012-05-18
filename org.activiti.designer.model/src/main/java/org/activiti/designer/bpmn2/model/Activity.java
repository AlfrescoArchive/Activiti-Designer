package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class Activity extends FlowNode {

  protected boolean asynchronous;
  protected boolean notExclusive;
  protected String defaultFlow;
  protected MultiInstanceLoopCharacteristics loopCharacteristics;
  protected List<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
  protected List<ActivitiListener> executionListeners = new ArrayList<ActivitiListener>();

  public boolean isAsynchronous() {
    return asynchronous;
  }
  public void setAsynchronous(boolean asynchronous) {
    this.asynchronous = asynchronous;
  }
  public boolean isNotExclusive() {
    return notExclusive;
  }
  public void setNotExclusive(boolean notExclusive) {
    this.notExclusive = notExclusive;
  }
  public List<BoundaryEvent> getBoundaryEvents() {
    return boundaryEvents;
  }
  public void setBoundaryEvents(List<BoundaryEvent> boundaryEvents) {
    this.boundaryEvents = boundaryEvents;
  }
  public String getDefaultFlow() {
    return defaultFlow;
  }
  public void setDefaultFlow(String defaultFlow) {
    this.defaultFlow = defaultFlow;
  }
  public List<ActivitiListener> getExecutionListeners() {
    return executionListeners;
  }
  public void setExecutionListeners(List<ActivitiListener> executionListeners) {
    this.executionListeners = executionListeners;
  }
  public MultiInstanceLoopCharacteristics getLoopCharacteristics() {
    return loopCharacteristics;
  }
  public void setLoopCharacteristics(MultiInstanceLoopCharacteristics loopCharacteristics) {
    this.loopCharacteristics = loopCharacteristics;
  }
}
