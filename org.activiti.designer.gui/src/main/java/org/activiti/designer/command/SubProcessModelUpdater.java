package org.activiti.designer.command;

import org.activiti.bpmn.model.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;

public class SubProcessModelUpdater extends BpmnProcessModelUpdater {

  public SubProcessModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof SubProcess) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected SubProcess cloneBusinessObject(Object businessObject) {
    return ((SubProcess) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((SubProcess) targetObject).setValues(((SubProcess) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new SubProcessModelUpdater(featureProvider);
  }
}
