package org.activiti.designer.command;

import org.activiti.bpmn.model.BusinessRuleTask;
import org.eclipse.graphiti.features.IFeatureProvider;

public class BusinessRuleTaskModelUpdater extends BpmnProcessModelUpdater {

  public BusinessRuleTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof BusinessRuleTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected BusinessRuleTask cloneBusinessObject(Object businessObject) {
    return ((BusinessRuleTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((BusinessRuleTask) targetObject).setValues(((BusinessRuleTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new BusinessRuleTaskModelUpdater(featureProvider);
  }
}
