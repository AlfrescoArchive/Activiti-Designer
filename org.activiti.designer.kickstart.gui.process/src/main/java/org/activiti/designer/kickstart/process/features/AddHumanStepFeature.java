package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddHumanStepFeature extends AddStepFeature {

  public AddHumanStepFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {
    return PluginImage.IMG_SERVICETASK.getImageKey();
  }
}