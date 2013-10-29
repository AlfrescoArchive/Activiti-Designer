package org.activiti.designer.command;

import org.activiti.bpmn.model.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class UserTaskModelUpdater extends BpmnProcessModelUpdater<UserTask> {

  public UserTaskModelUpdater(UserTask businessObject, PictogramElement pictogramElement,
      IFeatureProvider featureProvider) {
    super(businessObject, pictogramElement, featureProvider);
  }

  @Override
  protected UserTask cloneBusinessObject(UserTask businessObject) {
    return businessObject.clone();
  }

  @Override
  protected void performUpdates(UserTask valueObject, UserTask targetObject) {
    targetObject.setValues(valueObject);
  }


}
