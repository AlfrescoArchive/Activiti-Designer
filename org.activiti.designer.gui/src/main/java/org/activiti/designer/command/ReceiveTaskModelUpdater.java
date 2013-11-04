package org.activiti.designer.command;

import org.activiti.bpmn.model.ReceiveTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ReceiveTaskModelUpdater extends BpmnProcessModelUpdater {

  public ReceiveTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ReceiveTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected ReceiveTask cloneBusinessObject(Object businessObject) {
    return ((ReceiveTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((ReceiveTask) targetObject).setValues(((ReceiveTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ReceiveTaskModelUpdater(featureProvider);
  }
}
