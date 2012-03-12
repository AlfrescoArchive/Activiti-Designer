package org.activiti.designer.util.editor;

import java.util.HashMap;
import java.util.Map;

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Process;
import org.eclipse.graphiti.features.IFeatureProvider;

public class Bpmn2MemoryModel {

	protected Process process;
	protected IFeatureProvider featureProvider;
	protected Map<String, GraphicInfo> locationMap = new HashMap<String, GraphicInfo>();

	public Bpmn2MemoryModel(IFeatureProvider featureProvider, String name) {
		this.featureProvider = featureProvider;
		process = new Process();
		process.setName(name);
		process.setId(name);
	}
	
	public IFeatureProvider getFeatureProvider() {
  	return featureProvider;
  }

	public void setFeatureProvider(IFeatureProvider featureProvider) {
  	this.featureProvider = featureProvider;
  }

	public Process getProcess() {
  	return process;
  }

	public void setProcess(Process process) {
  	this.process = process;
  }
	
	public void addFlowElement(FlowElement flowElement) {
		process.getFlowElements().add(flowElement);
	}
	
	public void addGraphicInfo(String key, GraphicInfo graphicInfo) {
		locationMap.put(key, graphicInfo);
	}
	
	public GraphicInfo getGraphicInfo(String key) {
		return locationMap.get(key);
	}
	
	public Map<String, GraphicInfo> getLocationMap() {
		return locationMap;
	}
}
