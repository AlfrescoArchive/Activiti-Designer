package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class KickstartProcessMemoryModel {
  
	protected IFeatureProvider featureProvider;
	protected IFile modelFile;
	protected List<FlowElement> clipboard = new ArrayList<FlowElement>();
	protected WorkflowDefinition workflowDefinition;

	public KickstartProcessMemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
		this.featureProvider = featureProvider;
		this.modelFile = modelFile;
	}
	
	public StepDefinition getStepDefinition(String ref) {
	  StepDefinition definition = null;
    if (workflowDefinition != null && StringUtils.isNotEmpty(ref)) {
      for (StepDefinition step : workflowDefinition.getSteps()) {
        if (ref.equals(step.getId())) {
          definition = step;
          break;
        }
      }
    }
    return definition;
	}
	
	public IFeatureProvider getFeatureProvider() {
  	return featureProvider;
  }

	public void setFeatureProvider(IFeatureProvider featureProvider) {
  	this.featureProvider = featureProvider;
  }
	
	public List<FlowElement> getClipboard() {
    return clipboard;
  }

  public void setClipboard(List<FlowElement> clipboard) {
    this.clipboard = clipboard;
  }
	
	public IFile getModelFile() {
		return modelFile;
	}
  
  public WorkflowDefinition getWorkflowDefinition() {
    return workflowDefinition;
  }

  public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
    this.workflowDefinition = workflowDefinition;
  }
}
