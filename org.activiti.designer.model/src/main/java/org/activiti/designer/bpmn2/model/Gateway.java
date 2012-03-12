package org.activiti.designer.bpmn2.model;

public class Gateway extends FlowNode {

	protected SequenceFlow defaultFlow;

	public SequenceFlow getDefaultFlow() {
  	return defaultFlow;
  }

	public void setDefaultFlow(SequenceFlow defaultFlow) {
  	this.defaultFlow = defaultFlow;
  }
}
