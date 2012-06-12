package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.EventSubProcess;
import org.activiti.designer.bpmn2.model.FlowElement;
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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * This move feature takes care, that for all activity types, attached boundary events will move 
 * when the activity itself is moved.
 * 
 * @author bardioc
 */
public class MoveActivityFeature extends DefaultMoveShapeFeature {

	/** Creates the feature */
	public MoveActivityFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	

	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
	  Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getPictogramElement());
	  if (bo instanceof EventSubProcess) {
	    
	    if (context.getTargetContainer() instanceof Diagram)
	      return false;

	    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
	    if (parentObject instanceof SubProcess == true) {
	      return true;
	    }
	    
	    return false;
	    
	  } else {
	    return true;
	  }
  }

  /**
	 * Makes sure attached boundary events will be moved too, in case the shape itself is moved. 
	 * Determines the amount of pixels the shape moved, finds out all boundary events of the shape 
	 * and moves them the same delta.
	 * 
	 * @param context the context of the move
	 */
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		final Shape shape = context.getShape();

		// get the delta in both directions, the activity has been moved
		final int deltaX = context.getDeltaX();
		final int deltaY = context.getDeltaY();

		// get the activity itself to determine its boundary events
		final Activity activity = (Activity) getBusinessObjectForPictogramElement(shape);
		moveActivityChilds(activity, deltaX, deltaY);
		
		Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		
		if (context.getSourceContainer() != context.getTargetContainer()) {
		  if (context.getSourceContainer() instanceof Diagram == false) {
		    Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getSourceContainer());
		    if (containerBo instanceof SubProcess) {
		      SubProcess subProcess = (SubProcess) containerBo;
		      subProcess.getFlowElements().remove(activity);
		      for (SequenceFlow flow : activity.getOutgoing()) {
		        subProcess.getFlowElements().remove(flow);
		      }
		    } else if (containerBo instanceof Lane) {
		      Lane lane = (Lane) containerBo;
          lane.getFlowReferences().remove(activity.getId());
          lane.getParentProcess().getFlowElements().remove(activity);
          for (SequenceFlow flow : activity.getOutgoing()) {
            lane.getParentProcess().getFlowElements().remove(flow);
          }
        }
		  } else {
		    model.getMainProcess().getFlowElements().remove(activity);
		    for (SequenceFlow flow : activity.getOutgoing()) {
		      model.getMainProcess().getFlowElements().remove(flow);
        }
		  }
		  
		  if (context.getTargetContainer() instanceof Diagram == false) {
        Object containerBo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
        if (containerBo instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) containerBo;
          subProcess.getFlowElements().add(activity);
          for (SequenceFlow flow : activity.getOutgoing()) {
            subProcess.getFlowElements().add(flow);
          }
        } else if (containerBo instanceof Lane) {
          Lane lane = (Lane) containerBo;
          lane.getFlowReferences().add(activity.getId());
          lane.getParentProcess().getFlowElements().add(activity);
          for (SequenceFlow flow : activity.getOutgoing()) {
            lane.getParentProcess().getFlowElements().add(flow);
          }
        }
      } else {
        model.getMainProcess().getFlowElements().add(activity);
        for (SequenceFlow flow : activity.getOutgoing()) {
          model.getMainProcess().getFlowElements().add(flow);
        }
      }
		}
	}
	
	private void moveActivityChilds(Activity activity, int deltaX, int deltaY) {
		// get all boundary events of the activity
		final List<BoundaryEvent> boundaryEvents = activity.getBoundaryEvents();
		moveBoundaryEvents(boundaryEvents, deltaX, deltaY);
		
		// also move all boundary events in the sub process
		if(activity instanceof SubProcess) {
			for (FlowElement subElement : ((SubProcess) activity).getFlowElements()) {
	      if(subElement instanceof Activity) {
	      	moveActivityChilds((Activity) subElement, deltaX, deltaY);
	      }
      }
		}
	}
	
	private void moveBoundaryEvents(final List<BoundaryEvent> boundaryEvents, int deltaX, int deltaY) {
		final IGaService gaService = Graphiti.getGaService();
		for (final BoundaryEvent boundaryEvent : boundaryEvents) {
			
			// get all pictogram elements. Actually this should be only a single element, however
			// Graphiti allows multiple elements. The loop itself will not really harm in this case
			final PictogramElement picto = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
			
			// get the current position of the boundary event 
			int x = picto.getGraphicsAlgorithm().getX();
			int y = picto.getGraphicsAlgorithm().getY();

			// move it the same delta in both directions, the activity itself has been moved
			gaService.setLocation(picto.getGraphicsAlgorithm(), x + deltaX, y + deltaY);
		}
	}
}