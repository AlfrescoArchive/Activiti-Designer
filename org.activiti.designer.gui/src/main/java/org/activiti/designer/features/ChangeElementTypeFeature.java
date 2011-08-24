package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ChangeElementTypeFeature extends AbstractCustomFeature {
	
	private String newType;
	
	public ChangeElementTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public ChangeElementTypeFeature(IFeatureProvider fp, String newType) {
		super(fp);
		this.newType = newType;
	}
	
	@Override
  public boolean canExecute(ICustomContext context) {
	  return true;
  }

	@Override
  public void execute(ICustomContext context) {
	  PictogramElement element = (PictogramElement) context.getProperty("org.activiti.designer.changetype.pictogram");
	  GraphicsAlgorithm elementGraphics = element.getGraphicsAlgorithm();
	  int x = elementGraphics.getX();
	  int y = elementGraphics.getY();
	  
	  CreateContext taskContext = new CreateContext();
	  ContainerShape targetContainer = (ContainerShape) element.eContainer();
  	taskContext.setTargetContainer(targetContainer);
  	taskContext.setLocation(x, y);
  	
  	FlowElement oldObject = (FlowElement) element.getLink().getBusinessObjects().get(0);
	  String objectId = oldObject.getId();
	  
	  List<SequenceFlow> sourceList = new ArrayList<SequenceFlow>();
	  List<SequenceFlow> targetList = new ArrayList<SequenceFlow>();
	  for(EObject eObject : targetContainer.eResource().getContents()) {
	  	if(eObject instanceof SequenceFlow) {
	  		SequenceFlow sequenceFlow = (SequenceFlow) eObject;
	  		if(sequenceFlow.getSourceRef().getId().equals(objectId)) {
	  			sourceList.add(sequenceFlow);
	  		}
	  		if(sequenceFlow.getTargetRef().getId().equals(objectId)) {
	  			targetList.add(sequenceFlow);
	  		}
	  	}
	  }
	  taskContext.putProperty("org.activiti.designer.changetype.sourceflows", sourceList);
	  taskContext.putProperty("org.activiti.designer.changetype.targetflows", targetList);
	  taskContext.putProperty("org.activiti.designer.changetype.name", oldObject.getName());
	  
	  Anchor elementAnchor = null;
	  for (Shape shape : targetContainer.getChildren()) {
      FlowNode flowNode = (FlowNode) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
      if(flowNode == null || flowNode.getId() == null) continue;
      if(flowNode.getId().equals(objectId)) {
        EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
        for (Anchor anchor : anchorList) {
          if(anchor instanceof ChopboxAnchor) {
          	elementAnchor = anchor;
            break;
          }
        }
      }
    }
	  
	  List<Connection> sourceConnections = new ArrayList<Connection>();
	  List<Connection> targetConnections = new ArrayList<Connection>();
	  for(Connection connection : getDiagram().getConnections()) {
	  	if(connection.getStart().equals(elementAnchor)) {
	  		sourceConnections.add(connection);
	  	}
	  	if(connection.getEnd().equals(elementAnchor)) {
	  		targetConnections.add(connection);
	  	}
	  }
	  taskContext.putProperty("org.activiti.designer.changetype.sourceconnections", sourceConnections);
	  taskContext.putProperty("org.activiti.designer.changetype.targetconnections", targetConnections);
	  
	  List<PictogramLink> toDeleteLinks = new ArrayList<PictogramLink>();
	  for (PictogramLink link : getDiagram().getPictogramLinks()) {
	  	BaseElement flowNode = (BaseElement) getBusinessObjectForPictogramElement(link.getPictogramElement());
	  	if(flowNode.getId().equals(objectId)) {
	  		toDeleteLinks.add(link);
	  	}
    }
	  
	  List<Shape> toDeleteShapes = new ArrayList<Shape>();
	  for(Shape shape : targetContainer.getChildren()) {
	  	BaseElement flowNode = (BaseElement) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
	  	if(flowNode.getId().equals(objectId)) {
	  		toDeleteShapes.add(shape);
	  	}
	  }
	  for (PictogramLink link : toDeleteLinks) {
	  	getDiagram().getPictogramLinks().remove(link);
    }
	  for (Shape shape : toDeleteShapes) {
	  	targetContainer.getChildren().remove(shape);
	  }
	  EcoreUtil.delete(oldObject, true);
	  
	  if("servicetask".equals(newType)) {
	  	new CreateServiceTaskFeature(getFeatureProvider()).create(taskContext);
	  
	  } else if("businessruletask".equals(newType)) {
	  	new CreateBusinessRuleTaskFeature(getFeatureProvider()).create(taskContext);

	  } else if("mailtask".equals(newType)) {
	  	new CreateMailTaskFeature(getFeatureProvider()).create(taskContext);
	  
	  } else if("manualtask".equals(newType)) {
	  	new CreateManualTaskFeature(getFeatureProvider()).create(taskContext);
	  	
	  } else if("receivetask".equals(newType)) {
	  	new CreateReceiveTaskFeature(getFeatureProvider()).create(taskContext);
	  
	  } else if("scripttask".equals(newType)) {
	  	new CreateScriptTaskFeature(getFeatureProvider()).create(taskContext);
	  	
	  } else if("usertask".equals(newType)) {
	  	new CreateUserTaskFeature(getFeatureProvider()).create(taskContext);
	  
	  } else if("exclusivegateway".equals(newType)) {
	  	new CreateExclusiveGatewayFeature(getFeatureProvider()).create(taskContext);
	 
	  } else if("inclusivegateway".equals(newType)) {
        new CreateInclusiveGatewayFeature(getFeatureProvider()).create(taskContext);
     
      } else if("parallelgateway".equals(newType)) {
	  	new CreateParallelGatewayFeature(getFeatureProvider()).create(taskContext);
	  }
  }
}
