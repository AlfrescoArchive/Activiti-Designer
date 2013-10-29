package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddBoundaryTimerFeature extends AbstractAddBoundaryFeature {

  public AddBoundaryTimerFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getImageKey() {
    return PluginImage.IMG_BOUNDARY_TIMER.getImageKey();
  }
}