package org.activiti.designer.command;

import org.activiti.bpmn.model.ManualTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ManualTaskModelUpdater extends BpmnProcessModelUpdater {

  public ManualTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ManualTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected ManualTask cloneBusinessObject(Object businessObject) {
    return ((ManualTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((ManualTask) targetObject).setValues(((ManualTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ManualTaskModelUpdater(featureProvider);
  }
}
