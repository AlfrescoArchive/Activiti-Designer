package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class FormProperty extends BaseElement {

  protected String name;
  protected String value;
  protected String expression;
  protected String variable;
  protected String type;
  protected String datePattern;
  protected Boolean readable;
  protected Boolean writeable;
  protected Boolean required;
  protected List<FormValue> formValues = new ArrayList<FormValue>();

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public String getExpression() {
    return expression;
  }
  public void setExpression(String expression) {
    this.expression = expression;
  }
  public String getVariable() {
    return variable;
  }
  public void setVariable(String variable) {
    this.variable = variable;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getDatePattern() {
    return datePattern;
  }
  public void setDatePattern(String datePattern) {
    this.datePattern = datePattern;
  }
  public Boolean getReadable() {
    return readable;
  }
  public void setReadable(Boolean readable) {
    this.readable = readable;
  }
  public Boolean getWriteable() {
    return writeable;
  }
  public void setWriteable(Boolean writeable) {
    this.writeable = writeable;
  }
  public Boolean getRequired() {
    return required;
  }
  public void setRequired(Boolean required) {
    this.required = required;
  }
  public List<FormValue> getFormValues() {
    return formValues;
  }
  public void setFormValues(List<FormValue> formValues) {
    this.formValues = formValues;
  }
}
