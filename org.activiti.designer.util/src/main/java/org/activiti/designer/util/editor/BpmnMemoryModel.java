package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class BpmnMemoryModel {
  
	protected IFeatureProvider featureProvider;
	protected IFile modelFile;
	protected Map<String, Object> objectMap;
	protected List<BpmnModelListener> modelListeners;
	protected List<FlowElement> clipboard = new ArrayList<FlowElement>();
	protected BpmnModel bpmnModel;

	public BpmnMemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
		this.featureProvider = featureProvider;
		this.modelFile = modelFile;
		objectMap = new HashMap<String, Object>();
		modelListeners = new ArrayList<BpmnModelListener>();
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
  
  public void addModelListener(BpmnModelListener listener) {
    if(!modelListeners.contains(listener)) {
      modelListeners.add(listener);
    }
  }
  
  public void removeModelListener(BpmnModelListener listener) {
    modelListeners.remove(listener);
  }
  
  public String getKeyForBusinessObject(Object bo) {
    String result = null;
    // TODO: Don't use hashcode??
    if(bo != null) {
        result = String.valueOf(bo.hashCode());
        if(!objectMap.containsKey(result)) {
          objectMap.put(result, bo);
        }
    }
    return result;
  }
  
  public Object getBusinessObjectForKey(String key) {
    return objectMap.get(key);
  }
  
  public Map<String, Object> getObjectMap() {
    return objectMap;
  }
  
  public void setObjectMap(Map<String, Object> objectMap) {
    this.objectMap = objectMap;
  }
  
  public interface BpmnModelListener {
    void objectUpdated(Object object);
  }
}
