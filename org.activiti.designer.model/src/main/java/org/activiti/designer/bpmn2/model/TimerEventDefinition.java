package org.activiti.designer.bpmn2.model;


public class TimerEventDefinition extends EventDefinition {

	protected String timeDate;
	protected String timeDuration;
	protected String timeCycle;
	
	public String getTimeDate() {
  	return timeDate;
  }
	public void setTimeDate(String timeDate) {
  	this.timeDate = timeDate;
  }
	public String getTimeDuration() {
  	return timeDuration;
  }
	public void setTimeDuration(String timeDuration) {
  	this.timeDuration = timeDuration;
  }
	public String getTimeCycle() {
  	return timeCycle;
  }
	public void setTimeCycle(String timeCycle) {
  	this.timeCycle = timeCycle;
  }
}
