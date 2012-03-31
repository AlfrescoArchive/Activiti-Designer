package org.activiti.designer.bpmn2.model;

public class ErrorEventDefinition extends EventDefinition {

	protected String errorCode;
	
	public String getErrorCode() {
  	return errorCode;
  }
	public void setErrorCode(String errorCode) {
  	this.errorCode = errorCode;
  }
}
