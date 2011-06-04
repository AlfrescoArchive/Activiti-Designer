package org.activiti.designer.features;

import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;

public class MoveBoundaryEventFeature extends DefaultMoveShapeFeature {

  public MoveBoundaryEventFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    ContainerShape source = context.getSourceContainer();
    ContainerShape target = context.getTargetContainer();
    if(source instanceof Diagram == true && target instanceof Diagram == false) {
      return false;
    } else {
      Object sourceBO = getBusinessObjectForPictogramElement(source);
      Object targetBO = getBusinessObjectForPictogramElement(target);
      if(sourceBO instanceof SubProcess) {
        if(targetBO instanceof SubProcess == false) {
          return false;
        }
      }
    }
    Shape shape = context.getShape();
    BoundaryEvent event = (BoundaryEvent) getBusinessObjectForPictogramElement(shape);
    int x = shape.getGraphicsAlgorithm().getX();
    int y = shape.getGraphicsAlgorithm().getY();
    int width = shape.getGraphicsAlgorithm().getWidth();
    int height = shape.getGraphicsAlgorithm().getHeight();
    x += context.getDeltaX();
    y += context.getDeltaY();
    
    ILinkService linkService = Graphiti.getLinkService();
    List<PictogramElement> pictoList = linkService.getPictogramElements(getDiagram(), event.getAttachedToRef());
    if(pictoList != null && pictoList.size() > 0) {
      ContainerShape parent = (ContainerShape) pictoList.get(0);
      int parentX = parent.getGraphicsAlgorithm().getX();
      int parentY = parent.getGraphicsAlgorithm().getY();
      int parentWidth = parent.getGraphicsAlgorithm().getWidth();
      int parentHeight = parent.getGraphicsAlgorithm().getHeight();
      
      if(x > parentX && x < (parentX + parentWidth) &&
            (y + height - 5) > parentY && y < (parentY + parentHeight - 5)) {   
        return true;
      }
    }
    return false;
  }

}
