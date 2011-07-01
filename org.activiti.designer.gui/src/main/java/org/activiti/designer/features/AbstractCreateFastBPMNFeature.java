/**
 * 
 */
package org.activiti.designer.features;

import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractCreateFastBPMNFeature extends AbstractCreateBPMNFeature {
	
	private static final String CONNECTION_ATTRIBUTE = "org.activiti.designer.connectionContext";

  public AbstractCreateFastBPMNFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }
  
  protected void addGraphicalContent(BaseElement targetElement, ICreateContext context) {
  	setLocation(targetElement, (CreateContext) context);
		PictogramElement element = addGraphicalRepresentation(context, targetElement);
		createConnectionIfNeeded(element, context);
  }
  
  private void setLocation(BaseElement targetElement, CreateContext context) {
  	if(context.getProperty(CONNECTION_ATTRIBUTE) != null) {
  		
  		CreateConnectionContext connectionContext = (CreateConnectionContext) 
					context.getProperty(CONNECTION_ATTRIBUTE);
  		PictogramElement sourceElement = connectionContext.getSourcePictogramElement();
  		EObject sourceObject = sourceElement.getLink().getBusinessObjects().get(0);
  		if(sourceObject instanceof Event && targetElement instanceof Task) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 80, 
  					sourceElement.getGraphicsAlgorithm().getY() - 10);
  		
  		} else if(sourceObject instanceof Event && targetElement instanceof Gateway) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 80, 
  					sourceElement.getGraphicsAlgorithm().getY() - 3);
  			
  		} else if(sourceObject instanceof Gateway && targetElement instanceof Event) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 85, 
  					sourceElement.getGraphicsAlgorithm().getY() + 3);
  		
  		} else if(sourceObject instanceof Gateway && targetElement instanceof Task) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 85, 
  					sourceElement.getGraphicsAlgorithm().getY() - 7);
  		
  		} else if(sourceObject instanceof Task && targetElement instanceof Gateway) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 160, 
  					sourceElement.getGraphicsAlgorithm().getY() + 7);
  		
  		} else if(sourceObject instanceof Task && targetElement instanceof Event) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 160, 
  					sourceElement.getGraphicsAlgorithm().getY() + 10);
  		}
  	}
  }

  private void createConnectionIfNeeded(PictogramElement element, ICreateContext context) {
  	if(context.getProperty(CONNECTION_ATTRIBUTE) != null) {
  		
			CreateConnectionContext connectionContext = (CreateConnectionContext) 
					context.getProperty(CONNECTION_ATTRIBUTE);
			connectionContext.setTargetPictogramElement(element);
			connectionContext.setTargetAnchor(Graphiti.getPeService().getChopboxAnchor((AnchorContainer) element));
			CreateSequenceFlowFeature sequenceFeature = new CreateSequenceFlowFeature(getFeatureProvider());
			sequenceFeature.create(connectionContext);
		}
  }
  

}
