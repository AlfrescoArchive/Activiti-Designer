package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;

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
    
    ContainerShape parent = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(event.getAttachedToRef());
    SubProcess parentInSubProcess = inSubProcess(event.getAttachedToRef().getId());
    
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
  	for(FlowElement element : ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getProcess().getFlowElements()) {
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
  	return null;
  }

}
