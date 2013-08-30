package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class KickstartFormMemoryModel {

  protected IFeatureProvider featureProvider;
  protected IFile modelFile;
  protected FormDefinition formDefinition;
  protected Map<String, Object> objectMap;
  protected List<KickstartFormModelListener> modelListeners;
  protected boolean initialized = false;

  public KickstartFormMemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
    this.featureProvider = featureProvider;
    this.modelFile = modelFile;
    objectMap = new HashMap<String, Object>();
    formDefinition = new FormDefinition();
    modelListeners = new ArrayList<KickstartFormMemoryModel.KickstartFormModelListener>();
  }
  
  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }
  
  /**
   * @return true, if the model can be used to be updated. If false is returned,
   * no changes should be made to this model and an exception will be thrown when
   * the {@link FormDefinition} is accessed.
   */
  public boolean isInitialized() {
    return initialized;
  }
  
  public void addModelListener(KickstartFormModelListener listener) {
    if(!modelListeners.contains(listener)) {
      modelListeners.add(listener);
    }
  }
  
  public void removeModelListener(KickstartFormModelListener listener) {
    modelListeners.remove(listener);
  }
 
  /**
   * Should be called when an object, that is part of this form-model, has been
   * updated.
   * @param modelObject the updated object
   */
  public void modelObjectUpdated(Object modelObject) {
    if(modelObject != null) {
      if(modelObject instanceof FormPropertyDefinition) {
        if(!modelListeners.isEmpty()) {
          // Create a copy of the listener-list, to prevent ConcurrentModificationExcepcions
          // when iterating the listeners and a listener is added/removed during this process
          List<KickstartFormModelListener> safeListeners = new ArrayList<KickstartFormMemoryModel.KickstartFormModelListener>(modelListeners);
          for(KickstartFormModelListener listener : safeListeners) {
            listener.objectUpdated(modelObject);
          }
        }
            
      } else {
        throw new IllegalArgumentException("Unsupported model object type: " + modelObject.getClass());
      }
    }
  }

  public IFeatureProvider getFeatureProvider() {
    return featureProvider;
  }

  public void setFeatureProvider(IFeatureProvider featureProvider) {
    this.featureProvider = featureProvider;
  }

  public IFile getModelFile() {
    return modelFile;
  }

  public FormDefinition getFormDefinition() {
    if(!initialized) {
      throw new IllegalStateException("The model is currently being initialized");
    }
    return formDefinition;
  }

  public void setFormDefinition(FormDefinition formDefinition) {
    this.formDefinition = formDefinition;
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
   * an update is done to the form model.
   *  
   * @author Frederik Heremans
   */
  public interface KickstartFormModelListener {
    void objectUpdated(Object object);
  }
}
