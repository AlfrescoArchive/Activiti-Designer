package org.activiti.designer.bpmn2.model.alfresco;

import org.activiti.designer.bpmn2.model.ScriptTask;

public class AlfrescoScriptTask extends ScriptTask {

	protected String runAs;
	protected String scriptProcessor;
	
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
}
