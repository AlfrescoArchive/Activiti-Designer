package org.activiti.designer.command;

import org.activiti.bpmn.model.Lane;
import org.eclipse.graphiti.features.IFeatureProvider;

public class LaneModelUpdater extends BpmnProcessModelUpdater {

  public LaneModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Lane) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Lane cloneBusinessObject(Object businessObject) {
    return ((Lane) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Lane) targetObject).setValues(((Lane) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new LaneModelUpdater(featureProvider);
  }
}
