package org.activiti.designer.command;

import org.activiti.bpmn.model.BoundaryEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class BoundaryEventModelUpdater extends BpmnProcessModelUpdater {

  public BoundaryEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof BoundaryEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected BoundaryEvent cloneBusinessObject(Object businessObject) {
    return ((BoundaryEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((BoundaryEvent) targetObject).setValues(((BoundaryEvent) valueObject));
  }

  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new BoundaryEventModelUpdater(featureProvider);
  }
}
