package org.activiti.designer.bpmn2.model;

public class CustomProperty extends BaseElement {

  protected String name;
  protected String simpleValue;
  protected ComplexDataType complexValue;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getSimpleValue() {
    return simpleValue;
  }
  public void setSimpleValue(String simpleValue) {
    this.simpleValue = simpleValue;
  }
  public ComplexDataType getComplexValue() {
    return complexValue;
  }
  public void setComplexValue(ComplexDataType complexValue) {
    this.complexValue = complexValue;
  }
}
