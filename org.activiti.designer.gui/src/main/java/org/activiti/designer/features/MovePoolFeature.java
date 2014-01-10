package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * This move feature takes care, that for all activity types, attached boundary events will move 
 * when a pool is moved.
 * 
 * @author Tijs Rademakers
 */
public class MovePoolFeature extends DefaultMoveShapeFeature {

  protected ILocation shapeLocationBeforeMove;
  
	/** Creates the feature */
	public MovePoolFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
	  return true;
  }

	
	
  @Override
  protected void preMoveShape(IMoveShapeContext context) {
    final Shape shape = context.getShape();
    shapeLocationBeforeMove = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
    super.preMoveShape(context);
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

		// get the pool to determine its boundary events
		final Pool pool = (Pool) getBusinessObjectForPictogramElement(shape);
		ILocation shapeLocationAfterMove = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
		BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		movePoolChildBoundaryEvents(pool, model, shapeLocationAfterMove);
	}
	
	protected void movePoolChildBoundaryEvents(Pool pool, BpmnMemoryModel model, ILocation shapeLocationAfterMove) {
	  Process process = model.getBpmnModel().getProcess(pool.getId());
	  if (process == null) return;
	  
	  List<Association> associations = new ArrayList<Association>();
    for (Artifact artifact : process.getArtifacts()) {
      if (artifact instanceof TextAnnotation) {
        moveTextAnnotation((TextAnnotation) artifact, shapeLocationAfterMove);
      } else if (artifact instanceof Association) {
        associations.add((Association) artifact);
      }
    }
	  
		for (FlowElement subElement : process.getFlowElements()) {
		  if (subElement instanceof Activity) {
        moveActivityChildBoundaryEvents((Activity) subElement, shapeLocationAfterMove);
      }
		  moveFlowElementAssociations(subElement, associations, shapeLocationAfterMove);
		}
	}
	
	protected void moveActivityChildBoundaryEvents(Activity activity, ILocation shapeLocationAfterMove) {
    // get all boundary events of the activity
    final List<BoundaryEvent> boundaryEvents = activity.getBoundaryEvents();
    moveBoundaryEvents(boundaryEvents, shapeLocationAfterMove);
    
    // also move all boundary events in the sub process
    if (activity instanceof SubProcess) {
      for (FlowElement subElement : ((SubProcess) activity).getFlowElements()) {
        if (subElement instanceof Activity) {
          moveActivityChildBoundaryEvents((Activity) subElement, shapeLocationAfterMove);
        }
      }
    }
  }
	
	protected void moveFlowElementAssociations(FlowElement flowElement, List<Association> associations, ILocation shapeLocationAfterMove) {
    Iterator<Association> itAssociation = associations.iterator();
    while (itAssociation.hasNext()) {
      Association association = itAssociation.next();
      if (association.getSourceRef().equals(flowElement.getId()) || association.getTargetRef().equals(flowElement.getId())) {
        moveConnection((FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(association), shapeLocationAfterMove);
        itAssociation.remove();
      }
    }
    
    // also move all boundary events in the sub process
    if (flowElement instanceof SubProcess) {
      for (FlowElement subElement : ((SubProcess) flowElement).getFlowElements()) {
        moveFlowElementAssociations(subElement, associations, shapeLocationAfterMove);
      }
    }
  }
	
	protected void moveBoundaryEvents(final List<BoundaryEvent> boundaryEvents, ILocation shapeLocationAfterMove) {
		final IGaService gaService = Graphiti.getGaService();
		for (final BoundaryEvent boundaryEvent : boundaryEvents) {
		  
			final PictogramElement picto = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
			if (picto == null) return;
			
			// get the current position of the boundary event 
			int x = picto.getGraphicsAlgorithm().getX();
			int y = picto.getGraphicsAlgorithm().getY();
			
			int deltaX = shapeLocationAfterMove.getX() - shapeLocationBeforeMove.getX();
			int deltaY = shapeLocationAfterMove.getY() - shapeLocationBeforeMove.getY();

			// move it the same delta in both directions, the activity itself has been moved
			gaService.setLocation(picto.getGraphicsAlgorithm(), x + deltaX, y + deltaY);
			Graphiti.getPeService().sendToFront((Shape) picto);
			
			for (SequenceFlow flow : boundaryEvent.getOutgoingFlows()) {
			  moveConnection((FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(flow), shapeLocationAfterMove);
			}
		}
	}
	
	protected void moveTextAnnotation(final TextAnnotation annotation, ILocation shapeLocationAfterMove) {
    final PictogramElement picto = getFeatureProvider().getPictogramElementForBusinessObject(annotation);
    if (picto == null) return;
    
    if (picto.eContainer() instanceof Diagram == false) return;
    
    // get the current position of the boundary event 
    int x = picto.getGraphicsAlgorithm().getX();
    int y = picto.getGraphicsAlgorithm().getY();
    
    int deltaX = shapeLocationAfterMove.getX() - shapeLocationBeforeMove.getX();
    int deltaY = shapeLocationAfterMove.getY() - shapeLocationBeforeMove.getY();

    // move it the same delta in both directions, the activity itself has been moved
    final IGaService gaService = Graphiti.getGaService();
    gaService.setLocation(picto.getGraphicsAlgorithm(), x + deltaX, y + deltaY);
    Graphiti.getPeService().sendToFront((Shape) picto);
  }
	
	protected void moveConnection(final FreeFormConnection connection, ILocation shapeLocationAfterMove) {
	  
	  int deltaX = shapeLocationAfterMove.getX() - shapeLocationBeforeMove.getX();
    int deltaY = shapeLocationAfterMove.getY() - shapeLocationBeforeMove.getY();
	  
	  for (Point point : connection.getBendpoints()) {
	    point.setX(point.getX() + deltaX);
      point.setY(point.getY() + deltaY);
    }
  }
}