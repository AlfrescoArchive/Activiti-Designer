package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.SingleColumnFormLayout;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Feature that prevent moving shapes to any arbitrary position. Rather, the actual
 * position is calculated by the parent layout. 
 * 
 * @author Frederik Heremans
 */
public class FormPropertyMoveFeature extends DefaultMoveShapeFeature {

  public FormPropertyMoveFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
    ContainerShape targetContainer = getFirstLayoutContainerShape(context.getTargetContainer());
    
    if(targetContainer != null) {
      // TODO: use shared instance for diagram?
      SingleColumnFormLayout layout = new SingleColumnFormLayout();
      layout.moveShape(context);
    }
  }
  
  protected ContainerShape getFirstLayoutContainerShape(ContainerShape containerShape) {
    if(containerShape instanceof Diagram) {
      return containerShape;
    } else if(containerShape.getContainer() != null) {
      // Go one level up the hierarchy to find a container that is able to do layout
      return getFirstLayoutContainerShape(containerShape.getContainer());
    }
    return null;
  }

}
