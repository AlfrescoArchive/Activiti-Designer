package org.activiti.designer.command;

import org.activiti.bpmn.model.SequenceFlow;
import org.eclipse.graphiti.features.IFeatureProvider;

public class SequenceFlowModelUpdater extends BpmnProcessModelUpdater {

  public SequenceFlowModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof SequenceFlow) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected SequenceFlow cloneBusinessObject(Object businessObject) {
    return ((SequenceFlow) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((SequenceFlow) targetObject).setValues(((SequenceFlow) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new SequenceFlowModelUpdater(featureProvider);
  }
}
