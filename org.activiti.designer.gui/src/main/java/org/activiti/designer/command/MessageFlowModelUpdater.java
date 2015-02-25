package org.activiti.designer.command;

import org.activiti.bpmn.model.MessageFlow;
import org.eclipse.graphiti.features.IFeatureProvider;

public class MessageFlowModelUpdater extends BpmnProcessModelUpdater {

  public MessageFlowModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof MessageFlow) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected MessageFlow cloneBusinessObject(Object businessObject) {
    return ((MessageFlow) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((MessageFlow) targetObject).setValues(((MessageFlow) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new MessageFlowModelUpdater(featureProvider);
  }
}
