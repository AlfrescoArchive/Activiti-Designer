package org.activiti.designer.command;

import org.activiti.bpmn.model.ThrowEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ThrowEventModelUpdater extends BpmnProcessModelUpdater {

  public ThrowEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ThrowEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected ThrowEvent cloneBusinessObject(Object businessObject) {
    return ((ThrowEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((ThrowEvent) targetObject).setValues(((ThrowEvent) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ThrowEventModelUpdater(featureProvider);
  }
}
