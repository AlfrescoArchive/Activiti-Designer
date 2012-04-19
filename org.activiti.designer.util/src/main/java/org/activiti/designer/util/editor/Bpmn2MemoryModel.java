package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.Signal;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class Bpmn2MemoryModel {
  
	protected List<Process> processes = new ArrayList<Process>();
	protected IFeatureProvider featureProvider;
	protected IFile modelFile;
	protected List<FlowElement> clipboard = new ArrayList<FlowElement>();
	protected Map<String, GraphicInfo> locationMap = new HashMap<String, GraphicInfo>();
	protected List<Signal> signals = new ArrayList<Signal>();
	protected List<Pool> pools = new ArrayList<Pool>();
	protected String targetNamespace;

	public Bpmn2MemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
		this.featureProvider = featureProvider;
		this.modelFile = modelFile;
	}
	
	public IFeatureProvider getFeatureProvider() {
  	return featureProvider;
  }

	public void setFeatureProvider(IFeatureProvider featureProvider) {
  	this.featureProvider = featureProvider;
  }
	
	public Process getMainProcess() {
	  Process process = getProcess(null);
	  if(process == null) {
	    process = new Process();
	    process.setName("process1");
	    process.setId("process1");
      addProcess(process);
	  }
	  
	  return process;
	}

	public Process getProcess(String poolRef) {
	  for (Process process : processes) {
	    boolean foundPool = false;
	    for (Pool pool : pools) {
        if(pool.getProcessRef().equalsIgnoreCase(process.getId())) {
          
          if(poolRef != null) {
            if(pool.getId().equalsIgnoreCase(poolRef)) {
              foundPool = true;
            }
          } else {
            foundPool = true;
          }
        }
      }
	    
	    if(poolRef == null && foundPool == false) {
	      return process;
	    } else if(poolRef != null && foundPool == true) {
	      return process;
	    }
	  }
	  
	  return null;
  }
	
	public List<Process> getProcesses() {
	  return processes;
	}
	
	public void addProcess(Process process) {
	  processes.add(process);
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

	public List<FlowElement> getClipboard() {
  	return clipboard;
  }

	public void setClipboard(List<FlowElement> clipboard) {
  	this.clipboard = clipboard;
  }
	
	public IFile getModelFile() {
		return modelFile;
	}
	
	public List<Signal> getSignals() {
    return signals;
  }
	
  public void setSignals(List<Signal> signals) {
    this.signals = signals;
  }

  public List<Pool> getPools() {
    return pools;
  }
  
  public void setPools(List<Pool> pools) {
    this.pools = pools;
  }
  
  public String getTargetNamespace() {
    return targetNamespace;
  }

  public void setTargetNamespace(String targetNamespace) {
    this.targetNamespace = targetNamespace;
  }
}
