package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class SubProcess extends Activity {

	protected List<FlowElement> flowElements = new ArrayList<FlowElement>();

	public List<FlowElement> getFlowElements() {
  	return flowElements;
  }

	public void setFlowElements(List<FlowElement> flowElements) {
  	this.flowElements = flowElements;
  }
}
