package org.activiti.designer.features;

import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;


public class MoveGatewayFeature extends DefaultMoveShapeFeature {

	public MoveGatewayFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		final Shape shape = context.getShape();

		// get the gateway itself to determine its boundary events
		final Gateway gateway = (Gateway) getBusinessObjectForPictogramElement(shape);
		
		BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		
		if (context.getSourceContainer() != context.getTargetContainer()) {
		  if (context.getSourceContainer() instanceof Diagram == false) {
		    Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getSourceContainer());
		    if (containerBo instanceof SubProcess) {
		      SubProcess subProcess = (SubProcess) containerBo;
		      subProcess.removeFlowElement(gateway.getId());
		      for (SequenceFlow flow : gateway.getOutgoingFlows()) {
		        subProcess.removeFlowElement(flow.getId());
		      }
		    } else if (containerBo instanceof Lane) {
		      Lane lane = (Lane) containerBo;
          lane.getFlowReferences().remove(gateway.getId());
          lane.getParentProcess().removeFlowElement(gateway.getId());
          for (SequenceFlow flow : gateway.getOutgoingFlows()) {
            lane.getParentProcess().removeFlowElement(flow.getId());
          }
        }
		  } else {
		    model.getBpmnModel().getMainProcess().removeFlowElement(gateway.getId());
		    for (SequenceFlow flow : gateway.getOutgoingFlows()) {
		      model.getBpmnModel().getMainProcess().removeFlowElement(flow.getId());
        }
		  }
		  
		  if (context.getTargetContainer() instanceof Diagram == false) {
        Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
        if (containerBo instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) containerBo;
          subProcess.addFlowElement(gateway);
          for (SequenceFlow flow : gateway.getOutgoingFlows()) {
            subProcess.addFlowElement(flow);
          }
        } else if (containerBo instanceof Lane) {
          Lane lane = (Lane) containerBo;
          lane.getFlowReferences().add(gateway.getId());
          lane.getParentProcess().addFlowElement(gateway);
          for (SequenceFlow flow : gateway.getOutgoingFlows()) {
            lane.getParentProcess().addFlowElement(flow);
          }
        }
      } else {
        model.getBpmnModel().getMainProcess().addFlowElement(gateway);
        for (SequenceFlow flow : gateway.getOutgoingFlows()) {
          model.getBpmnModel().getMainProcess().addFlowElement(flow);
        }
      }
		}
	}
}