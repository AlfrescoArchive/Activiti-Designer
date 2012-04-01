package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class StartEvent extends Event {

  protected String initiator;
  protected String formKey;
  protected List<FormProperty> formProperties = new ArrayList<FormProperty>();

  public String getInitiator() {
    return initiator;
  }
  public void setInitiator(String initiator) {
    this.initiator = initiator;
  }
  public String getFormKey() {
    return formKey;
  }
  public void setFormKey(String formKey) {
    this.formKey = formKey;
  }
  public List<FormProperty> getFormProperties() {
    return formProperties;
  }
  public void setFormProperties(List<FormProperty> formProperties) {
    this.formProperties = formProperties;
  }
}
