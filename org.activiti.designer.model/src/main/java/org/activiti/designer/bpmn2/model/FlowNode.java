package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class FlowNode extends FlowElement {

	protected List<SequenceFlow> incoming = new ArrayList<SequenceFlow>();
	protected List<SequenceFlow> outgoing = new ArrayList<SequenceFlow>();
	
	public List<SequenceFlow> getIncoming() {
  	return incoming;
  }
	public void setIncoming(List<SequenceFlow> incoming) {
  	this.incoming = incoming;
  }
	public List<SequenceFlow> getOutgoing() {
  	return outgoing;
  }
	public void setOutgoing(List<SequenceFlow> outgoing) {
  	this.outgoing = outgoing;
  }
}
