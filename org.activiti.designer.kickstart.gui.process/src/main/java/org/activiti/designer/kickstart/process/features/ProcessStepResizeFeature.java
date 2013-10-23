package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * An {@link IResizeShapeFeature} that doesn't allow to resize any elements. 
 * @author Frederik Heremans
 */
public class ProcessStepResizeFeature extends DefaultResizeShapeFeature {

  public ProcessStepResizeFeature(KickstartProcessFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return false;
  }

}
