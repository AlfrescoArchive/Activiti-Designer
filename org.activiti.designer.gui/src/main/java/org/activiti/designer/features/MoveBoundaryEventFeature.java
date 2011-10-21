package org.activiti.designer.features;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.emf.ecore.EObject;
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
    
    Shape shape = context.getShape();
    BoundaryEvent event = (BoundaryEvent) getBusinessObjectForPictogramElement(shape);
    
    ILinkService linkService = Graphiti.getLinkService();
    List<PictogramElement> pictoList = linkService.getPictogramElements(getDiagram(), event.getAttachedToRef());
    
    SubProcess parentInSubProcess = null;
    ContainerShape parent = null;
    if(pictoList != null && pictoList.size() > 0) {
      parent = (ContainerShape) pictoList.get(0);
      BaseElement element = (BaseElement) getBusinessObjectForPictogramElement(parent);
      parentInSubProcess = inSubProcess(element.getId());
    }
    
    if(parentInSubProcess == null && source instanceof Diagram && source.getClass() != target.getClass()) {
      return false;
    }
    if(parentInSubProcess != null && target.equals(parent) == false) {
    	return false;
    }
    
    int x = shape.getGraphicsAlgorithm().getX();
    int y = shape.getGraphicsAlgorithm().getY();
    x += context.getDeltaX();
    y += context.getDeltaY();
    
    if(parent != null) {
      
      if(parentInSubProcess != null) {
        int parentWidth = parent.getGraphicsAlgorithm().getWidth();
        int parentHeight = parent.getGraphicsAlgorithm().getHeight();
        
        if((x + 28) > 0 && x < (parentWidth - 2) &&
        		(y + 28) > 0 && y < (parentHeight - 2)) {
        	return true;
        } else {
        	return false;
        }
      }
      
      int parentX = parent.getGraphicsAlgorithm().getX();
      int parentY = parent.getGraphicsAlgorithm().getY();
      int parentWidth = parent.getGraphicsAlgorithm().getWidth();
      int parentHeight = parent.getGraphicsAlgorithm().getHeight();
      
      if((x + 28) > parentX && x < (parentX + parentWidth - 2) &&
          (y + 28) > parentY && y < (parentY + parentHeight - 2)) {   
  	    return true;
  	  }
    }
    return false;
  }
  
  private SubProcess inSubProcess(String id) {
  	for(EObject object : getDiagram().eResource().getContents()) {
  		if(object instanceof BaseElement) {
  			BaseElement element = (BaseElement) object;
  			
  			if(element instanceof SubProcess) {
  				SubProcess subProcess = (SubProcess) element;
  				for(FlowElement subElement : subProcess.getFlowElements()) {
  					if(subElement.getId().equals(id)) {
  	  				return subProcess;
  	  			}
  				}
  			}
  			
  			if(element.getId().equals(id)) {
  				return null;
  			}
  		}
  	}
  	return null;
  }

}
