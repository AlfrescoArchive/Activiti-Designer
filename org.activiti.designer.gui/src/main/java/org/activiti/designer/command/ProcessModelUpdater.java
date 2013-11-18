package org.activiti.designer.command;

import org.activiti.bpmn.model.Process;
import org.eclipse.graphiti.features.IFeatureProvider;

public class ProcessModelUpdater extends BpmnProcessModelUpdater {

  public ProcessModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Process) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected Process cloneBusinessObject(Object businessObject) {
    return ((Process) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((Process) targetObject).setValues(((Process) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new ProcessModelUpdater(featureProvider);
  }
}
