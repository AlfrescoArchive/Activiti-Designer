package org.activiti.designer.features;

import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
//import org.activiti.designer.util.editor.BpmnMemoryModel;
//import org.activiti.designer.util.editor.ModelHandler;
//import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;

public class ReconnectAssociationFeature extends DefaultReconnectionFeature {

	public ReconnectAssociationFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public void postReconnect(IReconnectionContext context) {
		BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		Object connectionObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getConnection());
		
		if(connectionObject instanceof Association == false)
			return;
		boolean targetReConnection = ReconnectionContext.RECONNECT_TARGET.equalsIgnoreCase(context.getReconnectType());
		boolean sourceReConnection = ReconnectionContext.RECONNECT_SOURCE.equalsIgnoreCase(context.getReconnectType());
		Association association = (Association) connectionObject;
		Object targetObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetPictogramElement());
		if (!(targetObject instanceof Task || targetObject instanceof TextAnnotation)) {
			 FlowNode flowNode = null;
			 Artifact artifact = null;
			if(targetReConnection) {
				flowNode = (FlowNode) model.getBpmnModel().getFlowElement(association.getSourceRef());
				 if(flowNode == null) {
					 artifact = (Artifact) model.getBpmnModel().getArtifact(association.getSourceRef());
				 }
			} else if(sourceReConnection) {
				flowNode = (FlowNode) model.getBpmnModel().getFlowElement(association.getTargetRef());
				if(flowNode == null) {
					artifact = (Artifact) model.getBpmnModel().getArtifact(association.getTargetRef());
				}
			}
			if((flowNode instanceof Task || artifact instanceof TextAnnotation) == false) {
				return;
			}
		}
		BaseElement targetElement = (BaseElement) targetObject;
		if (targetReConnection) {
			association.setTargetRef(targetElement.getId());
		} else if (sourceReConnection) {
			association.setSourceRef(targetElement.getId());
		}
		super.postReconnect(context);
	}

}
