package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class UserTask extends Task {

	protected String assignee;
	protected Integer priority;
	protected String formKey;
	protected String dueDate;
	protected List<String> candidateUsers = new ArrayList<String>();
	protected List<String> candidateGroups = new ArrayList<String>();
	protected List<FormProperty> formProperties = new ArrayList<FormProperty>();
	protected List<ActivitiListener> taskListeners = new ArrayList<ActivitiListener>();
	
	public String getAssignee() {
  	return assignee;
  }
	public void setAssignee(String assignee) {
  	this.assignee = assignee;
  }
	public Integer getPriority() {
  	return priority;
  }
	public void setPriority(Integer priority) {
  	this.priority = priority;
  }
	public String getFormKey() {
  	return formKey;
  }
	public void setFormKey(String formKey) {
  	this.formKey = formKey;
  }
	public String getDueDate() {
  	return dueDate;
  }
	public void setDueDate(String dueDate) {
  	this.dueDate = dueDate;
  }
	public List<String> getCandidateUsers() {
  	return candidateUsers;
  }
	public void setCandidateUsers(List<String> candidateUsers) {
  	this.candidateUsers = candidateUsers;
  }
	public List<String> getCandidateGroups() {
  	return candidateGroups;
  }
	public void setCandidateGroups(List<String> candidateGroups) {
  	this.candidateGroups = candidateGroups;
  }
	public List<FormProperty> getFormProperties() {
  	return formProperties;
  }
	public void setFormProperties(List<FormProperty> formProperties) {
  	this.formProperties = formProperties;
  }
	public List<ActivitiListener> getTaskListeners() {
  	return taskListeners;
  }
	public void setTaskListeners(List<ActivitiListener> taskListeners) {
  	this.taskListeners = taskListeners;
  }
}
