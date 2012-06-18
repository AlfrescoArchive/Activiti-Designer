package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
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
		Object connectionObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getConnection());
	  if(connectionObject instanceof SequenceFlow == false) return;
	  
	  SequenceFlow flow = (SequenceFlow) connectionObject;
	  
	  Object targetObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetPictogramElement());
	  if(targetObject instanceof FlowNode == false) return;
	  FlowNode targetElement = (FlowNode) targetObject;
		
		if(ReconnectionContext.RECONNECT_TARGET.equalsIgnoreCase(context.getReconnectType())) {
			List<SequenceFlow> flowList = targetElement.getIncoming();
			boolean found = false;
			for (SequenceFlow sequenceFlow : flowList) {
	      if(sequenceFlow.equals(flow)) {
	      	found = true;
	      }
      }
			
			if(found == false) {
				
				// remove old target
				flow.getTargetRef().getIncoming().remove(flow);
				
				targetElement.getIncoming().add(flow);
				flow.setTargetRef(targetElement);
			}
			
		} else if(ReconnectionContext.RECONNECT_SOURCE.equalsIgnoreCase(context.getReconnectType())) {
		  // targetElement is the source side of the sequence flow
			List<SequenceFlow> flowList = targetElement.getOutgoing();
			boolean found = false;
			for (SequenceFlow sequenceFlow : flowList) {
	      if(sequenceFlow.equals(flow)) {
	      	found = true;
	      }
      }
			
			if(found == false) {
				
			  ContainerShape sourceElement = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(flow.getSourceRef());
			  ContainerShape oldParentContainer = sourceElement.getContainer(); 
			  ContainerShape newParentContainer = ((ContainerShape) context.getTargetPictogramElement()).getContainer();
			  
			  if (oldParentContainer != newParentContainer) {
			    
			    if (oldParentContainer instanceof Diagram) {
			      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().remove(flow);

			    } else {
			      Object parentObject = getFeatureProvider().getBusinessObjectForPictogramElement(oldParentContainer);
			      if (parentObject instanceof SubProcess) {
			        ((SubProcess) parentObject).getFlowElements().remove(flow);

			      } else if (parentObject instanceof Lane) {
			        Lane lane = (Lane) parentObject;
			        lane.getParentProcess().getFlowElements().remove(flow);
			      }
			    }
			    
			    if (newParentContainer instanceof Diagram) {
			      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(flow);

			    } else {
			      Object parentObject = getBusinessObjectForPictogramElement(newParentContainer);
			      if (parentObject instanceof SubProcess) {
			        ((SubProcess) parentObject).getFlowElements().add(flow);

			      } else if (parentObject instanceof Lane) {
			        Lane lane = (Lane) parentObject;
			        lane.getParentProcess().getFlowElements().add(flow);
			      }
			    }
			  }
			  
				// remove old source
				flow.getSourceRef().getOutgoing().remove(flow);
				
				targetElement.getOutgoing().add(flow);
				flow.setSourceRef(targetElement);
				
			}
		}
	  
	  super.postReconnect(context);
	}
}
