package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class ServiceTask extends Task {

	protected String implementation;
	protected String implementationType;
	protected String resultVariableName;
	protected List<FieldExtension> fieldExtensions = new ArrayList<FieldExtension>();
	protected List<CustomProperty> customProperties = new ArrayList<CustomProperty>();
	
	public String getImplementation() {
  	return implementation;
  }
	public void setImplementation(String implementation) {
  	this.implementation = implementation;
  }
	public String getImplementationType() {
  	return implementationType;
  }
	public void setImplementationType(String implementationType) {
  	this.implementationType = implementationType;
  }
	public String getResultVariableName() {
  	return resultVariableName;
  }
	public void setResultVariableName(String resultVariableName) {
  	this.resultVariableName = resultVariableName;
  }
	public List<FieldExtension> getFieldExtensions() {
  	return fieldExtensions;
  }
	public void setFieldExtensions(List<FieldExtension> fieldExtensions) {
  	this.fieldExtensions = fieldExtensions;
  }
	public List<CustomProperty> getCustomProperties() {
  	return customProperties;
  }
	public void setCustomProperties(List<CustomProperty> customProperties) {
  	this.customProperties = customProperties;
  }
}
