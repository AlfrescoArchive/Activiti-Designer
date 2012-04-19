package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.Event;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;


public class MoveEventFeature extends DefaultMoveShapeFeature {

	public MoveEventFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return true;
  }

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		final Shape shape = context.getShape();

		// get the event itself to determine its boundary events
		final Event event = (Event) getBusinessObjectForPictogramElement(shape);
		
		Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		
		if (context.getSourceContainer() != context.getTargetContainer()) {
		  if (context.getSourceContainer() instanceof Diagram == false) {
		    Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getSourceContainer());
		    if (containerBo instanceof SubProcess) {
		      SubProcess subProcess = (SubProcess) containerBo;
		      subProcess.getFlowElements().remove(event);
		      for (SequenceFlow flow : event.getOutgoing()) {
		        subProcess.getFlowElements().remove(flow);
		      }
		    } else if (containerBo instanceof Lane) {
		      Lane lane = (Lane) containerBo;
          lane.getFlowReferences().remove(event.getId());
          lane.getParentProcess().getFlowElements().remove(event);
          for (SequenceFlow flow : event.getOutgoing()) {
            lane.getParentProcess().getFlowElements().remove(flow);
          }
        }
		  } else {
		    model.getMainProcess().getFlowElements().remove(event);
		    for (SequenceFlow flow : event.getOutgoing()) {
		      model.getMainProcess().getFlowElements().remove(flow);
        }
		  }
		  
		  if (context.getTargetContainer() instanceof Diagram == false) {
        Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
        if (containerBo instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) containerBo;
          subProcess.getFlowElements().add(event);
          for (SequenceFlow flow : event.getOutgoing()) {
            subProcess.getFlowElements().add(flow);
          }
        } else if (containerBo instanceof Lane) {
          Lane lane = (Lane) containerBo;
          lane.getFlowReferences().add(event.getId());
          lane.getParentProcess().getFlowElements().add(event);
          for (SequenceFlow flow : event.getOutgoing()) {
            lane.getParentProcess().getFlowElements().add(flow);
          }
        }
      } else {
        model.getMainProcess().getFlowElements().add(event);
        for (SequenceFlow flow : event.getOutgoing()) {
          model.getMainProcess().getFlowElements().add(flow);
        }
      }
		}
	}
}