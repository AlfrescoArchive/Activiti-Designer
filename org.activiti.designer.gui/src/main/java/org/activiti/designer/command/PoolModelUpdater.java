package org.activiti.designer.command;

import org.activiti.bpmn.model.Pool;
import org.eclipse.graphiti.features.IFeatureProvider;

public class PoolModelUpdater extends BpmnProcessModelUpdater {

  public PoolModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Pool) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Pool cloneBusinessObject(Object businessObject) {
    return ((Pool) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Pool) targetObject).setValues(((Pool) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new PoolModelUpdater(featureProvider);
  }
}
