package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class KickstartProcessMemoryModel {
  
    public static final String KICKSTART_PROCESS_CONTENT_TYPE = "org.activiti.designer.kickstart.editor.process.contenttype";
  
	protected IFeatureProvider featureProvider;
	protected IFile modelFile;
	protected List<FlowElement> clipboard = new ArrayList<FlowElement>();
	protected Map<String, Object> objectMap;
	protected List<KickstartProcessModelListener> modelListeners;
	protected WorkflowDefinition workflowDefinition;
	protected boolean initialized = false;

	public KickstartProcessMemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
		this.featureProvider = featureProvider;
		this.modelFile = modelFile;
		objectMap = new HashMap<String, Object>();
		workflowDefinition = new WorkflowDefinition();
		modelListeners = new ArrayList<KickstartProcessMemoryModel.KickstartProcessModelListener>();
	}
	
	public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }
  
  /**
   * @return true, if the model can be used to be updated. If false is returned,
   * no changes should be made to this model and an exception will be thrown when
   * the {@link WorkflowDefinition} is accessed.
   */
  public boolean isInitialized() {
    return initialized;
  }
  
  public void addModelListener(KickstartProcessModelListener listener) {
    if(!modelListeners.contains(listener)) {
      modelListeners.add(listener);
    }
  }
  
  public void removeModelListener(KickstartProcessModelListener listener) {
    modelListeners.remove(listener);
  }
  
  /**
   * Should be called when an object, that is part of this process model, has been updated.
   * @param modelObject the updated object
   */
  public void modelObjectUpdated(Object modelObject) {
    if (modelObject != null) {
      if (modelObject instanceof StepDefinition || modelObject instanceof WorkflowDefinition) {
        if (!modelListeners.isEmpty()) {
          // Create a copy of the listener-list, to prevent ConcurrentModificationExcepcions
          // when iterating the listeners and a listener is added/removed during this process
          List<KickstartProcessModelListener> safeListeners = new ArrayList<KickstartProcessMemoryModel.KickstartProcessModelListener>(modelListeners);
          for (KickstartProcessModelListener listener : safeListeners) {
            listener.objectUpdated(modelObject);
          }
        }
            
      } else {
        throw new IllegalArgumentException("Unsupported model object type: " + modelObject.getClass());
      }
    }
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
  
  /**
   * Interface which describes a listener which is notified when
   * an update is done to the process model.
   *  
   * @author Tijs Rademakers
   */
  public interface KickstartProcessModelListener {
    void objectUpdated(Object object);
  }
}
