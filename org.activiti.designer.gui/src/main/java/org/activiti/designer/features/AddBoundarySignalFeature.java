package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddBoundarySignalFeature extends AbstractAddBoundaryFeature {

  public AddBoundarySignalFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getImageKey() {
    return PluginImage.IMG_BOUNDARY_SIGNAL.getImageKey();
  }
}