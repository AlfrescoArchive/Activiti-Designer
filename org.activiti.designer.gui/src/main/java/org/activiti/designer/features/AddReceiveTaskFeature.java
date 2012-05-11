package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddReceiveTaskFeature extends AddTaskFeature {

  public AddReceiveTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {
    return PluginImage.IMG_RECEIVETASK.getImageKey();
  }
}
