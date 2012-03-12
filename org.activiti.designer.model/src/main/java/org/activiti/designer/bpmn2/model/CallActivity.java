package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends Activity {

	protected String calledElement;
	protected List<IOParameter> inParameters = new ArrayList<IOParameter>();
	protected List<IOParameter> outParameters = new ArrayList<IOParameter>();
	
	public String getCalledElement() {
  	return calledElement;
  }
	public void setCalledElement(String calledElement) {
  	this.calledElement = calledElement;
  }
	public List<IOParameter> getInParameters() {
  	return inParameters;
  }
	public void setInParameters(List<IOParameter> inParameters) {
  	this.inParameters = inParameters;
  }
	public List<IOParameter> getOutParameters() {
  	return outParameters;
  }
	public void setOutParameters(List<IOParameter> outParameters) {
  	this.outParameters = outParameters;
  }
}
