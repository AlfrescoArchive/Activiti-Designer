/**
 * 
 */
package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.Event;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.Task;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.0
 * 
 */
public abstract class AbstractCreateBPMNFeature extends AbstractCreateFeature {

	private static final String CONNECTION_ATTRIBUTE = "org.activiti.designer.connectionContext";
	
  public AbstractCreateBPMNFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  protected abstract Class<? extends FlowElement> getFeatureClass();

  protected String getNextId() {
    return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getDiagram());
  }
  
  protected void addObjectToContainer(ICreateContext context, FlowNode flowNode, String name) {
  	flowNode.setId(getNextId());
  	flowNode.setName(name);
  	ContainerShape targetContainer = context.getTargetContainer();
		if(targetContainer instanceof Diagram) {
			ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(flowNode);
		
		} else {
			Object parentObject = getBusinessObjectForPictogramElement(targetContainer);
			if(parentObject instanceof SubProcess) {
				((SubProcess) parentObject).getFlowElements().add(flowNode);
			}
		}
		addGraphicalContent(context, flowNode);
  }
  
  @SuppressWarnings("unchecked")
  protected void addGraphicalContent(ICreateContext context, BaseElement targetElement) {
  	setLocation(targetElement, (CreateContext) context);
		PictogramElement element = addGraphicalRepresentation(context, targetElement);
		createConnectionIfNeeded(element, context);
		
		if(context.getProperty("org.activiti.designer.changetype.sourceflows") != null) {
  		List<SequenceFlow> sourceFlows = (List<SequenceFlow>) context.getProperty("org.activiti.designer.changetype.sourceflows");
  		for (SequenceFlow sourceFlow : sourceFlows) {
  			sourceFlow.setSourceRef((FlowNode) targetElement);
      }
  		List<SequenceFlow> targetFlows = (List<SequenceFlow>) context.getProperty("org.activiti.designer.changetype.targetflows");
  		for (SequenceFlow targetFlow : targetFlows) {
  			targetFlow.setTargetRef((FlowNode) targetElement);
      }
  		
  		Anchor elementAnchor = null;
      for (Shape shape : context.getTargetContainer().getChildren()) {
        FlowNode flowNode = (FlowNode) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
        if(flowNode == null || flowNode.getId() == null) continue;
        if(flowNode.getId().equals(targetElement.getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
            	elementAnchor = anchor;
              break;
            }
          }
        }
      }
      
      List<Connection> sourceConnections = (List<Connection>) context.getProperty("org.activiti.designer.changetype.sourceconnections");
      for (Connection connection : sourceConnections) {
      	connection.setStart(elementAnchor);
      	elementAnchor.getOutgoingConnections().add(connection);
      }
      List<Connection> targetConnections = (List<Connection>) context.getProperty("org.activiti.designer.changetype.targetconnections");
      for (Connection connection : targetConnections) {
      	connection.setEnd(elementAnchor);
      	elementAnchor.getIncomingConnections().add(connection);
      }
		}
  }
  
  protected void setName(String defaultName, FlowElement targetElement, ICreateContext context) {
  	if(context.getProperty("org.activiti.designer.changetype.name") != null) {
  		targetElement.setName(context.getProperty("org.activiti.designer.changetype.name").toString());
  	} else {
  		targetElement.setName(defaultName);
  	}
  }
  
  private void setLocation(BaseElement targetElement, CreateContext context) {
  	if(context.getProperty(CONNECTION_ATTRIBUTE) != null) {
  		
  		CreateConnectionContext connectionContext = (CreateConnectionContext) 
					context.getProperty(CONNECTION_ATTRIBUTE);
  		PictogramElement sourceElement = connectionContext.getSourcePictogramElement();
  		Object sourceObject = getBusinessObjectForPictogramElement(sourceElement);
  		if(sourceObject instanceof Event && (targetElement instanceof Task || targetElement instanceof CallActivity)) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 80, 
  					sourceElement.getGraphicsAlgorithm().getY() - 10);
  		
  		} else if(sourceObject instanceof Event && targetElement instanceof Gateway) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 80, 
  					sourceElement.getGraphicsAlgorithm().getY() - 3);
  			
  		} else if(sourceObject instanceof Gateway && targetElement instanceof Event) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 85, 
  					sourceElement.getGraphicsAlgorithm().getY() + 3);
  		
  		} else if(sourceObject instanceof Gateway && (targetElement instanceof Task || targetElement instanceof CallActivity)) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 85, 
  					sourceElement.getGraphicsAlgorithm().getY() - 7);
  		
  		} else if((sourceObject instanceof Task || sourceObject instanceof CallActivity) && targetElement instanceof Gateway) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 160, 
  					sourceElement.getGraphicsAlgorithm().getY() + 7);
  		
  		} else if((sourceObject instanceof Task || sourceObject instanceof CallActivity) && targetElement instanceof Event) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 160, 
  					sourceElement.getGraphicsAlgorithm().getY() + 10);
  		
  		} else if((sourceObject instanceof Task || sourceObject instanceof CallActivity) && (targetElement instanceof Task || targetElement instanceof CallActivity)) {
  			context.setLocation(sourceElement.getGraphicsAlgorithm().getX() + 160, 
  					sourceElement.getGraphicsAlgorithm().getY());
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
