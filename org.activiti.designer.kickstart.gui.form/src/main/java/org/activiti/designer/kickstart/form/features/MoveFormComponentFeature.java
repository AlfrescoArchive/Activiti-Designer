package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.diagram.KickstartFormLayouter;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * Feature that prevent moving shapes to any arbitrary position. Rather, the actual
 * position is calculated by the parent layout. 
 * 
 * @author Frederik Heremans
 */
public class MoveFormComponentFeature extends DefaultMoveShapeFeature {

  public MoveFormComponentFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
      getFormLayouter().moveShape(
          context.getTargetContainer(), context.getSourceContainer(), context.getShape(),
          context.getX(), context.getY());
  }
  
  protected KickstartFormLayouter getFormLayouter() {
    return ((KickstartFormFeatureProvider)getFeatureProvider()).getFormLayouter(); 
  }

}
