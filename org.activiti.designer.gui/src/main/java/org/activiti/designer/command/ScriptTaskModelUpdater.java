package org.activiti.designer.command;

import org.activiti.bpmn.model.ScriptTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ScriptTaskModelUpdater extends BpmnProcessModelUpdater {

  public ScriptTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ScriptTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected ScriptTask cloneBusinessObject(Object businessObject) {
    return ((ScriptTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((ScriptTask) targetObject).setValues(((ScriptTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ScriptTaskModelUpdater(featureProvider);
  }
}
