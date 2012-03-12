package org.activiti.designer.bpmn2.model;

public class BoundaryEvent extends Event {

	protected Activity attachedToRef;
	protected boolean cancelActivity;
	
	public Activity getAttachedToRef() {
  	return attachedToRef;
  }
	public void setAttachedToRef(Activity attachedToRef) {
  	this.attachedToRef = attachedToRef;
  }
	public boolean isCancelActivity() {
  	return cancelActivity;
  }
	public void setCancelActivity(boolean cancelActivity) {
  	this.cancelActivity = cancelActivity;
  }
}
