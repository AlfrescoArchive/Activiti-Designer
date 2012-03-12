package org.activiti.designer.bpmn2.model;

import java.util.Date;

public class TimerEventDefinition extends EventDefinition {

	protected Date timeDate;
	protected String timeDuration;
	protected String timeCycle;
	
	public Date getTimeDate() {
  	return timeDate;
  }
	public void setTimeDate(Date timeDate) {
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
