package org.activiti.designer.command;

import org.activiti.bpmn.model.EndEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class EndEventModelUpdater extends BpmnProcessModelUpdater {

  public EndEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof EndEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected EndEvent cloneBusinessObject(Object businessObject) {
    return ((EndEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((EndEvent) targetObject).setValues(((EndEvent) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new EndEventModelUpdater(featureProvider);
  }
}
