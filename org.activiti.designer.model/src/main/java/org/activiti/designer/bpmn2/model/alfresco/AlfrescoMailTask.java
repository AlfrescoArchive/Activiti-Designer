package org.activiti.designer.bpmn2.model.alfresco;

import org.activiti.designer.bpmn2.model.MailTask;

public class AlfrescoMailTask extends MailTask {

	protected String runAs;
	protected String scriptProcessor;
	protected String toMany;
	protected String template;
	protected String templateModel;
	
	public String getRunAs() {
  	return runAs;
  }
	public void setRunAs(String runAs) {
  	this.runAs = runAs;
  }
	public String getScriptProcessor() {
  	return scriptProcessor;
  }
	public void setScriptProcessor(String scriptProcessor) {
  	this.scriptProcessor = scriptProcessor;
  }
	public String getToMany() {
  	return toMany;
  }
	public void setToMany(String toMany) {
  	this.toMany = toMany;
  }
	public String getTemplate() {
  	return template;
  }
	public void setTemplate(String template) {
  	this.template = template;
  }
	public String getTemplateModel() {
  	return templateModel;
  }
	public void setTemplateModel(String templateModel) {
  	this.templateModel = templateModel;
  }
}
