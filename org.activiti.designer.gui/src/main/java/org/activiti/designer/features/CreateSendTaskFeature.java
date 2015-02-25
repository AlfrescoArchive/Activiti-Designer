package org.activiti.designer.features;

import org.activiti.bpmn.model.SendTask;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateSendTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "sendtask";

  public CreateSendTaskFeature(IFeatureProvider fp) {
    super(fp, "SendTask", "Add send task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    SendTask newSendTask = new SendTask();
    addObjectToContainer(context, newSendTask, "Send Task");

    return new Object[] { newSendTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SENDTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
