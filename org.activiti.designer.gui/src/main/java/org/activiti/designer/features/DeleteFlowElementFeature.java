package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFlowElementFeature extends DefaultDeleteFeature {

	public DeleteFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected void deleteBusinessObject(Object bo) {
		if (bo instanceof Task || bo instanceof Gateway || bo instanceof Event || bo instanceof SubProcess || bo instanceof CallActivity) {
		  deleteSequenceFlows((FlowNode) bo);
		  deleteAssociations((FlowNode) bo);
		}
		
		if (bo instanceof SequenceFlow) {
		  deletedConnectingFlows((SequenceFlow) bo);
		}

		if (bo instanceof Activity) {
		  Activity activity = (Activity) bo;
		  if (activity.getBoundaryEvents() != null) {
		    for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
		      IRemoveContext rc = new RemoveContext(getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent));
		      IFeatureProvider featureProvider = getFeatureProvider();
		      IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
		      if (removeFeature != null) {
		        removeFeature.remove(rc);
		        // Bug 347421: Set hasDoneChanges flag only after first modification
		        setDoneChanges(true);
		      }
		      removeElement(boundaryEvent);
        }
		  }
		}
		
		if (bo instanceof BoundaryEvent) {
      if(((BoundaryEvent) bo).getAttachedToRef() != null) {
        ((BoundaryEvent) bo).getAttachedToRef().getBoundaryEvents().remove(bo);
      }
    }
		
		if (bo instanceof SubProcess) {
		  SubProcess subProcess = (SubProcess) bo;
		  List<FlowElement> toDeleteElements = new ArrayList<FlowElement>();
		  for (FlowElement subFlowElement : subProcess.getFlowElements()) {
		    toDeleteElements.add(subFlowElement);
      }
		  for (FlowElement subFlowElement : toDeleteElements) {
		    if(subFlowElement instanceof FlowNode) {
          deleteSequenceFlows((FlowNode) subFlowElement);
        }
		    removeElement(subFlowElement);
      }
		  subProcess.getFlowElements().clear();
		}

		removeElement((BaseElement) bo);
	}
	
	protected void removeElement(BaseElement element) {
  	List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeFlowElement(element.getId());
      removeElementInLanes(element.getId(), process.getLanes());
      removeElementInProcess(element, process);
    }
	}
	
	protected void removeElementInLanes(String elementId, List<Lane> laneList) {
    for (Lane lane : laneList) {
      lane.getFlowReferences().remove(elementId);
    }
  }
	
	protected void removeElementInProcess(BaseElement element, BaseElement parentElement) {
	  Collection<FlowElement> elementList = null;
    if (parentElement instanceof Process) {
      elementList = ((Process) parentElement).getFlowElements();
    } else if (parentElement instanceof SubProcess) {
      elementList = ((SubProcess) parentElement).getFlowElements();
    }
	  
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeFlowElement(element.getId());
        removeElementInProcess(element, subProcess);
      }
    }
  }
	
	protected void deleteSequenceFlows(FlowNode flowNode) {
	  List<SequenceFlow> toDeleteSequenceFlows = new ArrayList<SequenceFlow>();
    for (SequenceFlow incomingSequenceFlow : flowNode.getIncomingFlows()) {
      SequenceFlow toDeleteObject = (SequenceFlow) getFlowElement(incomingSequenceFlow);
      if (toDeleteObject != null) {
        toDeleteSequenceFlows.add(toDeleteObject);
      }
    }
    for (SequenceFlow outgoingSequenceFlow : flowNode.getOutgoingFlows()) {
      SequenceFlow toDeleteObject = (SequenceFlow) getFlowElement(outgoingSequenceFlow);
      if (toDeleteObject != null) {
        toDeleteSequenceFlows.add(toDeleteObject);
      }
    }
    for (SequenceFlow deleteObject : toDeleteSequenceFlows) {
      deletedConnectingFlows(deleteObject);
      removeElement(deleteObject);
    }
	}
	
	protected void deletedConnectingFlows(SequenceFlow sequenceFlow) {
	  BpmnModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();
	  FlowElement sourceElement = model.getFlowElement(sequenceFlow.getSourceRef());
	  FlowElement targetElement = model.getFlowElement(sequenceFlow.getTargetRef());
	  if (sourceElement != null) {
	    deleteSequenceFlowFromFlows(sequenceFlow.getId(), ((FlowNode) sourceElement).getOutgoingFlows());
    }
    if (targetElement != null) {
      deleteSequenceFlowFromFlows(sequenceFlow.getId(), ((FlowNode) targetElement).getIncomingFlows());
    }
	}
	
	protected void deleteAssociations(FlowNode flowNode) {
    List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      removeAssociation(process.getArtifacts(), flowNode);
      removeAssociationInProcess(process, flowNode);
    }
  }
  
	protected void removeAssociationInProcess(FlowElementsContainer parentElement, FlowNode flowNode) {
    Collection<FlowElement> elementList = parentElement.getFlowElements();
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        removeAssociation(subProcess.getArtifacts(), flowNode);
        removeAssociationInProcess(subProcess, flowNode);
      }
    }
  }
  
  protected void removeAssociation(Collection<Artifact> artifacts, FlowNode flowNode) {
    List<Association> toDeleteAssociations = new ArrayList<Association>();
    for (Artifact artifact : artifacts) {
      if (artifact instanceof Association) {
        Association association = (Association) artifact;
        if (association.getSourceRef().equals(flowNode.getId()) || association.getTargetRef().equals(flowNode.getId())) {
          toDeleteAssociations.add(association);
        }
      }
    }
    
    for (Association deleteObject : toDeleteAssociations) {
      deletedConnectingFlows(deleteObject);
      removeArtifact(deleteObject);
    }
  }
  
  protected void deletedConnectingFlows(Association association) {
    BpmnModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();
    FlowElement sourceElement = model.getFlowElement(association.getSourceRef());
    FlowElement targetElement = model.getFlowElement(association.getTargetRef());
    if (sourceElement != null) {
      ((FlowNode) sourceElement).getOutgoingFlows().remove(association);
    }
    if (targetElement != null) {
      ((FlowNode) targetElement).getIncomingFlows().remove(association);
    }
  }
  
  protected void removeArtifact(Artifact element) {
    List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeArtifact(element.getId());
      removeArtifactInProcess(element, process);
    }
  }
  
  protected void removeArtifactInProcess(Artifact element, FlowElementsContainer parentElement) {
    Collection<FlowElement> elementList = parentElement.getFlowElements();
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeArtifact(element.getId());
        removeArtifactInProcess(element, subProcess);
      }
    }
  }

  protected FlowElement getFlowElement(FlowElement flowElement) {
	  BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	  return model.getFlowElement(flowElement.getId());
	}
  
  protected void deleteSequenceFlowFromFlows(String elementId, List<SequenceFlow> flows) {
    Iterator<SequenceFlow> flowIterator = flows.iterator();
    while (flowIterator.hasNext()) {
      SequenceFlow flow = flowIterator.next();
      if (flow.getId().equals(elementId)) {
        flowIterator.remove();
      }
    }
  }
}
