package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddManualTaskFeature extends AddTaskFeature {

  public AddManualTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {
    return PluginImage.IMG_MANUALTASK.getImageKey();
  }
}
