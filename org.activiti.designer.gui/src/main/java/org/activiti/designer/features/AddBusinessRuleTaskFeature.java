package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddBusinessRuleTaskFeature extends AddTaskFeature {

  public AddBusinessRuleTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {
    return PluginImage.IMG_BUSINESSRULETASK.getImageKey();
  }
}
