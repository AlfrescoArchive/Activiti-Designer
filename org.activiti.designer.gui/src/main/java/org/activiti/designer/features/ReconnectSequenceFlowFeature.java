package org.activiti.designer.features;

import java.util.List;

import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class ReconnectSequenceFlowFeature extends DefaultReconnectionFeature {

	public ReconnectSequenceFlowFeature(IFeatureProvider fp) {
	  super(fp);
  }
	
	@Override
	public void postReconnect(IReconnectionContext context) {
	  BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		Object connectionObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getConnection());
	  if(connectionObject instanceof SequenceFlow == false) return;
	  
	  SequenceFlow flow = (SequenceFlow) connectionObject;
	  
	  Object targetObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetPictogramElement());
	  if(targetObject instanceof FlowNode == false) return;
	  FlowNode targetElement = (FlowNode) targetObject;
		
		if(ReconnectionContext.RECONNECT_TARGET.equalsIgnoreCase(context.getReconnectType())) {
			List<SequenceFlow> flowList = targetElement.getIncomingFlows();
			boolean found = false;
			for (SequenceFlow sequenceFlow : flowList) {
	      if(sequenceFlow.getId().equals(flow.getId())) {
	      	found = true;
	      }
      }
			
			if(found == false) {
				
			  FlowNode targetFlowNode = (FlowNode) model.getBpmnModel().getFlowElement(flow.getTargetRef());
		   
			  if (targetFlowNode != null) {
  				// remove old target
			    targetFlowNode.getIncomingFlows().remove(flow);
			  }
				
				targetElement.getIncomingFlows().add(flow);
				flow.setTargetRef(targetElement.getId());
			}
			
		} else if(ReconnectionContext.RECONNECT_SOURCE.equalsIgnoreCase(context.getReconnectType())) {
		  // targetElement is the source side of the sequence flow
			List<SequenceFlow> flowList = targetElement.getOutgoingFlows();
			boolean found = false;
			for (SequenceFlow sequenceFlow : flowList) {
	      if(sequenceFlow.equals(flow)) {
	      	found = true;
	      }
      }
			
			if(found == false) {
				
			  FlowNode sourceFlowNode = (FlowNode) model.getBpmnModel().getFlowElement(flow.getSourceRef());
			  ContainerShape sourceElement = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(sourceFlowNode);
			  ContainerShape oldParentContainer = sourceElement.getContainer(); 
			  ContainerShape newParentContainer = ((ContainerShape) context.getTargetPictogramElement()).getContainer();
			  
			  if (oldParentContainer != newParentContainer) {
			    
			    if (oldParentContainer instanceof Diagram) {
			      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getMainProcess().removeFlowElement(flow.getId());

			    } else {
			      Object parentObject = getFeatureProvider().getBusinessObjectForPictogramElement(oldParentContainer);
			      if (parentObject instanceof SubProcess) {
			        ((SubProcess) parentObject).removeFlowElement(flow.getId());

			      } else if (parentObject instanceof Lane) {
			        Lane lane = (Lane) parentObject;
			        lane.getParentProcess().removeFlowElement(flow.getId());
			      }
			    }
			    
			    if (newParentContainer instanceof Diagram) {
			      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getMainProcess().addFlowElement(flow);

			    } else {
			      Object parentObject = getBusinessObjectForPictogramElement(newParentContainer);
			      if (parentObject instanceof SubProcess) {
			        ((SubProcess) parentObject).addFlowElement(flow);

			      } else if (parentObject instanceof Lane) {
			        Lane lane = (Lane) parentObject;
			        lane.getParentProcess().addFlowElement(flow);
			      }
			    }
			  }
			  
				// remove old source
			  if (sourceFlowNode != null) {
			    sourceFlowNode.getOutgoingFlows().remove(flow);
			  }
				
				targetElement.getOutgoingFlows().add(flow);
				flow.setSourceRef(targetElement.getId());
				
			}
		}
	  
	  super.postReconnect(context);
	}
}
