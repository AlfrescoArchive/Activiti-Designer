package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteLaneFeature extends DefaultDeleteFeature {

  private int laneHeight;
  private int laneY;
  
  public DeleteLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
  public boolean canDelete(IDeleteContext context) {
	  PictogramElement pictogramElement = context.getPictogramElement();
	  Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
	  if (bo instanceof Lane) {
	    return true;
	  } else {
	    return false;
	  }
  }
	
  @Override
  public void preDelete(IDeleteContext context) {
    Shape laneShape = (Shape) context.getPictogramElement();
    
    laneHeight = laneShape.getGraphicsAlgorithm().getHeight();
    laneY = laneShape.getGraphicsAlgorithm().getY();
  }

  protected void deleteBusinessObject(Object bo) {
		if (bo instanceof Lane) {
		  Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
		  Lane lane = (Lane) bo;
		  
		  Pool parentPool = null;
		  for (Pool pool : model.getPools()) {
		    if(pool.getProcessRef().equalsIgnoreCase(lane.getParentProcess().getId())) {
		      parentPool = pool;
		      break;
		    }
		  }
		  
		  Process laneProcess = model.getProcess(parentPool.getId());
		  
		  if (parentPool == null || laneProcess == null) return;
		  
		  if(laneProcess.getLanes().size() == 1) {
        Process process = model.getProcess(parentPool.getId());
        model.getProcesses().remove(process);
        model.getPools().remove(parentPool);
        PictogramElement poolElement = getFeatureProvider().getPictogramElementForBusinessObject(parentPool);
        IRemoveContext poolRc = new RemoveContext(poolElement);
        IRemoveFeature poolRemoveFeature = getFeatureProvider().getRemoveFeature(poolRc);
        if (poolRemoveFeature != null) {
          poolRemoveFeature.remove(poolRc);
        }
        
		  } else {
		    Shape poolShape = (Shape) getFeatureProvider().getPictogramElementForBusinessObject(parentPool);
  		  ResizeShapeContext resizeContext = new ResizeShapeContext(poolShape);
  	    resizeContext.setSize(poolShape.getGraphicsAlgorithm().getWidth(), poolShape.getGraphicsAlgorithm().getHeight() - laneHeight);
  	    resizeContext.setLocation(poolShape.getGraphicsAlgorithm().getX(), poolShape.getGraphicsAlgorithm().getY());
  	    resizeContext.setDirection(ResizeShapeContext.DIRECTION_NORTH);
  	    resizeContext.putProperty("org.activiti.designer.lane.create", true);
  	    getFeatureProvider().getResizeShapeFeature(resizeContext).execute(resizeContext);
		  }
		  
		  for (Lane otherLane : lane.getParentProcess().getLanes()) {
        if(otherLane.equals(lane)) continue;
        
        Shape otherLaneShape = (Shape) getFeatureProvider().getPictogramElementForBusinessObject(otherLane);
        if(otherLaneShape.getGraphicsAlgorithm().getY() > laneY) {
          otherLaneShape.getGraphicsAlgorithm().setY(otherLaneShape.getGraphicsAlgorithm().getY() - laneHeight);
        }
      }
		  
		  List<FlowElement> toDeleteElements = new ArrayList<FlowElement>();
		  for (String flowRef : lane.getFlowReferences()) {
		    for (FlowElement flowElement : lane.getParentProcess().getFlowElements()) {
		      if(flowRef.equalsIgnoreCase(flowElement.getId())) {
		        toDeleteElements.add(flowElement);
		      }
		    }
      }
		  for (FlowElement subFlowElement : toDeleteElements) {
		    if(subFlowElement instanceof FlowNode) {
          deleteSequenceFlows((FlowNode) subFlowElement);
        }
		    removeElement(subFlowElement);
      }
		  lane.getParentProcess().getLanes().remove(lane);
		}
	}
	
	private void removeElement(BaseElement element) {
  	List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getProcesses();
    for (Process process : processes) {
      process.getFlowElements().remove(element);
      removeElementInProcess(element, process.getFlowElements());
    }
	}
	
	private void removeElementInProcess(BaseElement element, List<FlowElement> elementList) {
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.getFlowElements().remove(element);
        removeElementInProcess(element, subProcess.getFlowElements());
      }
    }
  }
	
	private void deleteSequenceFlows(FlowNode flowNode) {
	  List<SequenceFlow> toDeleteSequenceFlows = new ArrayList<SequenceFlow>();
    for (SequenceFlow incomingSequenceFlow : flowNode.getIncoming()) {
      toDeleteSequenceFlows.add(incomingSequenceFlow);
    }
    for (SequenceFlow outgoingSequenceFlow : flowNode.getOutgoing()) {
      toDeleteSequenceFlows.add(outgoingSequenceFlow);
    }
    for (SequenceFlow deleteObject : toDeleteSequenceFlows) {
      IRemoveContext rc = new RemoveContext(getFeatureProvider().getPictogramElementForBusinessObject(deleteObject));
      IFeatureProvider featureProvider = getFeatureProvider();
      IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
      if (removeFeature != null) {
        removeFeature.remove(rc);
      }
      deleteObject.getTargetRef().getIncoming().remove(deleteObject);
      deleteObject.getSourceRef().getOutgoing().remove(deleteObject);
      removeElement(deleteObject);
    }
	}
}
