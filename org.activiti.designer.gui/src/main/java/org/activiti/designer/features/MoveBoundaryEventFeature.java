package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class MoveBoundaryEventFeature extends DefaultMoveShapeFeature {

  public MoveBoundaryEventFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    ContainerShape sourceContainer = context.getSourceContainer();
    ContainerShape targetContainer = context.getTargetContainer();
    
  	Shape shape = context.getShape();
    BoundaryEvent event = (BoundaryEvent) getBusinessObjectForPictogramElement(shape);
    
    ContainerShape parent = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(event.getAttachedToRef());
    ContainerShape secondParent = parent.getContainer();
    
    int x = 0, y = 0;
    boolean translateNecessary = false;
    if(targetContainer.equals(parent)) {
    	translateNecessary = true;
    	ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
      x = shapeLocation.getX();
      y = shapeLocation.getY();
      x += context.getDeltaX();
      y += context.getDeltaY();
      ILocation parentLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(parent);
      x += parentLocation.getX();
      y += parentLocation.getY();
    
    } else if (targetContainer.equals(sourceContainer)) {
    	ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
      x = shapeLocation.getX();
      y = shapeLocation.getY();
      x += context.getDeltaX();
      y += context.getDeltaY();
    
    } else if (targetContainer.equals(secondParent)) {
    	translateNecessary = true;
    	ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
      x = shapeLocation.getX();
      y = shapeLocation.getY();
      x += context.getDeltaX();
      y += context.getDeltaY();
      ILocation parentLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(secondParent);
      x += parentLocation.getX();
      y += parentLocation.getY();
 
    } else {
    	// not valid
    	return false;
    }
    
    if(parent != null) {
      
    	ILocation parentLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(parent);
    	int parentX = parentLocation.getX();
    	int parentY = parentLocation.getY();
    	
      int parentWidth = parent.getGraphicsAlgorithm().getWidth();
      int parentHeight = parent.getGraphicsAlgorithm().getHeight();
      
      int EVENT_MAX_OVERLAP = 28;
      int EVENT_MIN_OVERLAP = 2;
      
      if((x + EVENT_MAX_OVERLAP) > parentX && 
      		x < (parentX + parentWidth - EVENT_MIN_OVERLAP) &&
          (y + EVENT_MAX_OVERLAP) > parentY &&
          y < (parentY + parentHeight - EVENT_MIN_OVERLAP)) {
      	
      	if(translateNecessary) {
      		MoveShapeContext moveContext = ((MoveShapeContext) context);
      		moveContext.setX(x);
      		moveContext.setY(y);
      		moveContext.setDeltaX(0);
      		moveContext.setDeltaY(0);
      		moveContext.setTargetContainer(context.getSourceContainer());
      	}
      	
  	    return true;
  	  }
    }
    
    return false;
  }

}
