package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class SequenceFlow extends FlowElement {

	protected String conditionExpression;
	protected FlowNode sourceRef;
	protected FlowNode targetRef;
	protected List<ActivitiListener> executionListeners = new ArrayList<ActivitiListener>();
	
	public String getConditionExpression() {
  	return conditionExpression;
  }
	public void setConditionExpression(String conditionExpression) {
  	this.conditionExpression = conditionExpression;
  }
	public FlowNode getSourceRef() {
  	return sourceRef;
  }
	public void setSourceRef(FlowNode sourceRef) {
  	this.sourceRef = sourceRef;
  }
	public FlowNode getTargetRef() {
  	return targetRef;
  }
	public void setTargetRef(FlowNode targetRef) {
  	this.targetRef = targetRef;
  }
	public List<ActivitiListener> getExecutionListeners() {
  	return executionListeners;
  }
	public void setExecutionListeners(List<ActivitiListener> executionListeners) {
  	this.executionListeners = executionListeners;
  }
}
