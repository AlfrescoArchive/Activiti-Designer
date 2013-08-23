package org.activiti.designer.util.editor;

import java.util.HashMap;
import java.util.Map;

import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.features.IFeatureProvider;

public class KickstartFormMemoryModel {

  protected IFeatureProvider featureProvider;
  protected IFile modelFile;
  protected FormDefinition formDefinition;
  protected Map<String, Object> objectMap;

  public KickstartFormMemoryModel(IFeatureProvider featureProvider, IFile modelFile) {
    this.featureProvider = featureProvider;
    this.modelFile = modelFile;
    objectMap = new HashMap<String, Object>();
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
}
