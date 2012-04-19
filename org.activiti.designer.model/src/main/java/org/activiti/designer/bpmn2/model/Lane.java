package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;


public class Lane extends BaseElement {
  
  protected String name;
  protected Process parentProcess;
  protected List<String> flowReferences = new ArrayList<String>();
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Process getParentProcess() {
    return parentProcess;
  }
  
  public void setParentProcess(Process parentProcess) {
    this.parentProcess = parentProcess;
  }

  public List<String> getFlowReferences() {
    return flowReferences;
  }

  public void setFlowReferences(List<String> flowReferences) {
    this.flowReferences = flowReferences;
  }
}
