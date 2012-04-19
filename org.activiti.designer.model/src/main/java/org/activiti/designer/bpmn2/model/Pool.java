package org.activiti.designer.bpmn2.model;


public class Pool extends BaseElement {

  protected String name;
  protected String processRef;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getProcessRef() {
    return processRef;
  }
  public void setProcessRef(String processRef) {
    this.processRef = processRef;
  }
}
