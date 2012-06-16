package org.activiti.designer.bpmn2.model;


public enum AssociationDirection {
  NONE("None"),
  ONE("One"),
  BOTH("Both");
  
  String value;
  
  AssociationDirection(final String value)
  {
    this.value = value;
  }
  
  public String getValue() {
    return value;
  }
}
