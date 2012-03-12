package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class FlowElementsContainer extends BaseElement {

	protected List<FlowElement> flowElements = new ArrayList<FlowElement>();

	public List<FlowElement> getFlowElements() {
  	return flowElements;
  }

	public void setFlowElements(List<FlowElement> flowElements) {
  	this.flowElements = flowElements;
  }
}
