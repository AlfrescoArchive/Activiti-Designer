package org.activiti.designer.bpmn2.model;

public class MultiInstanceLoopCharacteristics {

	protected String inputDataItem;
	protected String loopCardinality;
	protected String completionCondition;
	protected String elementVariable;
	protected boolean sequential;
	
	public String getInputDataItem() {
  	return inputDataItem;
  }
	public void setInputDataItem(String inputDataItem) {
  	this.inputDataItem = inputDataItem;
  }
	public String getLoopCardinality() {
  	return loopCardinality;
  }
	public void setLoopCardinality(String loopCardinality) {
  	this.loopCardinality = loopCardinality;
  }
	public String getCompletionCondition() {
  	return completionCondition;
  }
	public void setCompletionCondition(String completionCondition) {
  	this.completionCondition = completionCondition;
  }
	public String getElementVariable() {
  	return elementVariable;
  }
	public void setElementVariable(String elementVariable) {
  	this.elementVariable = elementVariable;
  }
	public boolean isSequential() {
  	return sequential;
  }
	public void setSequential(boolean sequential) {
  	this.sequential = sequential;
  }
}
