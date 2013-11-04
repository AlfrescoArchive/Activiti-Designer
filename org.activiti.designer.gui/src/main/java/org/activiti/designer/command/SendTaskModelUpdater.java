package org.activiti.designer.command;

import org.activiti.bpmn.model.SendTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class SendTaskModelUpdater extends BpmnProcessModelUpdater {

  public SendTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof SendTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected SendTask cloneBusinessObject(Object businessObject) {
    return ((SendTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((SendTask) targetObject).setValues(((SendTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new SendTaskModelUpdater(featureProvider);
  }
}
