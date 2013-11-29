package org.activiti.designer.command;

import org.activiti.bpmn.model.Association;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AssociationModelUpdater extends BpmnProcessModelUpdater {

  public AssociationModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Association) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Association cloneBusinessObject(Object businessObject) {
    return ((Association) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Association) targetObject).setValues(((Association) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new AssociationModelUpdater(featureProvider);
  }
}
