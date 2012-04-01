package org.activiti.designer.bpmn2.model;

public class SignalEventDefinition extends EventDefinition {

  protected String signalRef;

  public String getSignalRef() {
    return signalRef;
  }

  public void setSignalRef(String signalRef) {
    this.signalRef = signalRef;
  }
}
