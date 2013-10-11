package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * Feature that prevent moving shapes to any arbitrary position. Rather, the actual
 * position is calculated by the parent layout. 
 * 
 * @author Tijs Rademakers
 */
public class MoveStepDefinitionFeature extends DefaultMoveShapeFeature implements ICustomUndoableFeature {

  public MoveStepDefinitionFeature(KickstartProcessFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
      getProcessLayouter().moveShape(
          context.getTargetContainer(), context.getSourceContainer(), context.getShape(),
          context.getX(), context.getY());
  }
  
  @Override
  public boolean canRedo(IContext context) {
    return true;
  }
  
  @Override
  public boolean canUndo(IContext context) {
    return true;
  }

  @Override
  public void undo(IContext context) {
    // Since the model is updated by the layout based on the actual shape order,
    // it's sufficient to force a re-layout at this point
    getProcessLayouter().relayout(((IMoveShapeContext)context).getTargetContainer());
    getProcessLayouter().relayout(((IMoveShapeContext)context).getSourceContainer());
  }
  
  @Override
  public void redo(IContext context) {
    // Since the model is updated by the layout based on the actual shape order,
    // it's sufficient to force a re-layout at this point
    getProcessLayouter().relayout(((IMoveShapeContext)context).getTargetContainer());
    getProcessLayouter().relayout(((IMoveShapeContext)context).getSourceContainer());
  }
  
  protected KickstartProcessLayouter getProcessLayouter() {
    return ((KickstartProcessFeatureProvider)getFeatureProvider()).getProcessLayouter();
  }

}
