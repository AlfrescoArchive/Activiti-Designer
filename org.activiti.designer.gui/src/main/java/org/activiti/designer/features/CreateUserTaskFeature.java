package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateUserTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "usertask";

  public CreateUserTaskFeature(IFeatureProvider fp) {
    super(fp, "UserTask", "Add user task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    UserTask newUserTask = new UserTask();
    addObjectToContainer(context, newUserTask, "User Task");

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newUserTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_USERTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
