package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateManualTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "manualtask";

  public CreateManualTaskFeature(IFeatureProvider fp) {
    super(fp, "ManualTask", "Add manual task");
  }

  @Override
  public Object[] create(ICreateContext context) {
    ManualTask newManualTask = new ManualTask();
    addObjectToContainer(context, newManualTask, "Manual Task");

    return new Object[] { newManualTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_MANUALTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
