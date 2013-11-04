package org.activiti.designer.command;

import org.activiti.bpmn.model.CallActivity;
import org.eclipse.graphiti.features.IFeatureProvider;

public class CallActivityModelUpdater extends BpmnProcessModelUpdater {

  public CallActivityModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof CallActivity) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected CallActivity cloneBusinessObject(Object businessObject) {
    return ((CallActivity) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((CallActivity) targetObject).setValues(((CallActivity) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new CallActivityModelUpdater(featureProvider);
  }
}
