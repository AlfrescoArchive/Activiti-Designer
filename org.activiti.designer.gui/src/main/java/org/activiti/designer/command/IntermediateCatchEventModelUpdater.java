package org.activiti.designer.command;

import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.eclipse.graphiti.features.IFeatureProvider;

public class IntermediateCatchEventModelUpdater extends BpmnProcessModelUpdater {

  public IntermediateCatchEventModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof IntermediateCatchEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected IntermediateCatchEvent cloneBusinessObject(Object businessObject) {
    return ((IntermediateCatchEvent) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((IntermediateCatchEvent) targetObject).setValues(((IntermediateCatchEvent) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new IntermediateCatchEventModelUpdater(featureProvider);
  }
}
