package org.activiti.designer.command;

import org.activiti.bpmn.model.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class StartEventModelUpdater extends BpmnProcessModelUpdater {

  public StartEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof StartEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected StartEvent cloneBusinessObject(Object businessObject) {
    return ((StartEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((StartEvent) targetObject).setValues(((StartEvent) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new StartEventModelUpdater(featureProvider);
  }
}
