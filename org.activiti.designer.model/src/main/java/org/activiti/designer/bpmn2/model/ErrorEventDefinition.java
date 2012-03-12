package org.activiti.designer.bpmn2.model;

public class ErrorEventDefinition extends EventDefinition {

	protected String errorRef;
	protected String errorCode;
	
	public String getErrorRef() {
  	return errorRef;
  }
	public void setErrorRef(String errorRef) {
  	this.errorRef = errorRef;
  }
	public String getErrorCode() {
  	return errorCode;
  }
	public void setErrorCode(String errorCode) {
  	this.errorCode = errorCode;
  }
}
