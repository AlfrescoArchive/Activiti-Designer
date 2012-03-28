package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;

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
			List<SequenceFlow> flowList = targetElement.getOutgoing();
			boolean found = false;
			for (SequenceFlow sequenceFlow : flowList) {
	      if(sequenceFlow.equals(flow)) {
	      	found = true;
	      }
      }
			
			if(found == false) {
				
				// remove old source
				flow.getSourceRef().getOutgoing().remove(flow);
				
				targetElement.getOutgoing().add(flow);
				flow.setSourceRef(targetElement);
			}
		}
	  
	  super.postReconnect(context);
	}
}
