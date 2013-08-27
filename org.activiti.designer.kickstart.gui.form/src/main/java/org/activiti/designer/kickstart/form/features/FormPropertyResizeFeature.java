package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * An {@link IResizeShapeFeature} that doesn't allow to resize any elements. 
 * @author Frederik Heremans
 */
public class FormPropertyResizeFeature extends DefaultResizeShapeFeature {

  public FormPropertyResizeFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return false;
  }

}
