package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class ActivitiListener extends BaseElement {

  protected String event;
  protected String implementationType;
  protected String implementation;
  protected String runAs;
  protected String scriptProcessor;
  protected List<FieldExtension> fieldExtensions = new ArrayList<FieldExtension>();

  public String getEvent() {
    return event;
  }
  public void setEvent(String event) {
    this.event = event;
  }
  public String getImplementationType() {
    return implementationType;
  }
  public void setImplementationType(String implementationType) {
    this.implementationType = implementationType;
  }
  public String getImplementation() {
    return implementation;
  }
  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }
  public List<FieldExtension> getFieldExtensions() {
    return fieldExtensions;
  }
  public void setFieldExtensions(List<FieldExtension> fieldExtensions) {
    this.fieldExtensions = fieldExtensions;
  }
  public String getRunAs() {
    return runAs;
  }
  public void setRunAs(String runAs) {
    this.runAs = runAs;
  }
  public String getScriptProcessor() {
    return scriptProcessor;
  }
  public void setScriptProcessor(String scriptProcessor) {
    this.scriptProcessor = scriptProcessor;
  }
}
