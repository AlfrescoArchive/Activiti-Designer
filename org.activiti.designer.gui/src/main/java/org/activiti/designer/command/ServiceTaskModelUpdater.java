package org.activiti.designer.command;

import org.activiti.bpmn.model.ServiceTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ServiceTaskModelUpdater extends BpmnProcessModelUpdater {

  public ServiceTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ServiceTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected ServiceTask cloneBusinessObject(Object businessObject) {
    return ((ServiceTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((ServiceTask) targetObject).setValues(((ServiceTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ServiceTaskModelUpdater(featureProvider);
  }
}
