package org.activiti.designer.kickstart.form.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * An {@link IResizeShapeFeature} that doesn't allow to resize any elements. 
 * @author Frederik Heremans
 */
public class FormPropertyResizeFeature extends DefaultResizeShapeFeature {

  public FormPropertyResizeFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return false;
  }

}
