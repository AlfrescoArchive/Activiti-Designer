package org.activiti.designer.command;


import org.eclipse.graphiti.features.IFeatureProvider;

import com.tuniu.nfbird.bpm.model.WorkformTask;

public class TuniuUserTaskModelUpdater extends BpmnProcessModelUpdater {

  public TuniuUserTaskModelUpdater(IFeatureProvider featureProvider) {
    super(featureProvider);
  }
  
  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof WorkformTask) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected WorkformTask cloneBusinessObject(Object businessObject) {
    return ((WorkformTask) businessObject).clone();
  }

  @Override
  protected void performUpdates(Object valueObject, Object targetObject) {
    ((WorkformTask) targetObject).setValues(((WorkformTask) valueObject));
  }
  
  @Override
  public BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider) {
    return new TuniuUserTaskModelUpdater(featureProvider);
  }
}
