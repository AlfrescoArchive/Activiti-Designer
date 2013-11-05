package org.activiti.designer.command;

import org.activiti.bpmn.model.Gateway;
import org.eclipse.graphiti.features.IFeatureProvider;

public class GatewayModelUpdater extends BpmnProcessModelUpdater {

  public GatewayModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Gateway) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Gateway cloneBusinessObject(Object businessObject) {
    return ((Gateway) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Gateway) targetObject).setValues(((Gateway) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new GatewayModelUpdater(featureProvider);
  }
}
