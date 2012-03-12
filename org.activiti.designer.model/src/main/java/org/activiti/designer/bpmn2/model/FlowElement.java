package org.activiti.designer.bpmn2.model;

public class FlowElement extends BaseElement {

	protected String name;
	protected String documentation;

	public String getName() {
  	return name;
  }

	public void setName(String name) {
  	this.name = name;
  }

	public String getDocumentation() {
  	return documentation;
  }

	public void setDocumentation(String documentation) {
  	this.documentation = documentation;
  }
}
