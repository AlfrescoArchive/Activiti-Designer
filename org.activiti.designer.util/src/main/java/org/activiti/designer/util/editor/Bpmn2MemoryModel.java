package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class Bpmn2MemoryModel {
  
	protected IFeatureProvider featureProvider;
	protected IFile modelFile;
	protected List<FlowElement> clipboard = new ArrayList<FlowElement>();
	protected BpmnModel bpmnModel;

	public Bpmn2MemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
		this.featureProvider = featureProvider;
		this.modelFile = modelFile;
	}
	
	public void addMainProcess() {
	  Process process = new Process();
    process.setName("My process");
    process.setId("myProcess");
    bpmnModel.addProcess(process);
	}
	
	public FlowElement getFlowElement(String ref) {
	  FlowElement element = null;
    if (bpmnModel != null && StringUtils.isNotEmpty(ref)) {
      element = bpmnModel.getFlowElement(ref);
    }
    return element;
	}
	
	public Artifact getArtifact(String ref) {
    Artifact artifact = null;
    if (bpmnModel != null && StringUtils.isNotEmpty(ref)) {
      artifact = bpmnModel.getArtifact(ref);
    }
    return artifact;
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
  
  public BpmnModel getBpmnModel() {
    return bpmnModel;
  }

  public void setBpmnModel(BpmnModel bpmnModel) {
    this.bpmnModel = bpmnModel;
  }
}
