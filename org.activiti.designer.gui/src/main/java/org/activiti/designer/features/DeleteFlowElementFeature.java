package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.Collection;
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
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
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

		if (bo instanceof SubProcess || bo instanceof Task) {
		  if(((Activity) bo).getBoundaryEvents() != null) {
		    for (BoundaryEvent boundaryEvent : ((Activity) bo).getBoundaryEvents()) {
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
	
	private void removeElement(BaseElement element) {
  	List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeFlowElement(element.getId());
      removeElementInLanes(element.getId(), process.getLanes());
      removeElementInProcess(element, process);
    }
	}
	
	private void removeElementInLanes(String elementId, List<Lane> laneList) {
    for (Lane lane : laneList) {
      lane.getFlowReferences().remove(elementId);
    }
  }
	
	private void removeElementInProcess(BaseElement element, BaseElement parentElement) {
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
	
	private void deleteSequenceFlows(FlowNode flowNode) {
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
	
	private void deletedConnectingFlows(SequenceFlow sequenceFlow) {
	  BpmnModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel();
	  FlowElement sourceElement = model.getFlowElement(sequenceFlow.getSourceRef());
	  FlowElement targetElement = model.getFlowElement(sequenceFlow.getTargetRef());
	  if (sourceElement != null) {
      ((FlowNode) sourceElement).getOutgoingFlows().remove(sequenceFlow);
    }
    if (targetElement != null) {
      ((FlowNode) targetElement).getIncomingFlows().remove(sequenceFlow);
    }
	}
	
	private void deleteAssociations(FlowNode flowNode) {
    List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      removeAssociation(process.getArtifacts(), flowNode);
      removeAssociationInProcess(process, flowNode);
    }
  }
  
  private void removeAssociationInProcess(FlowElementsContainer parentElement, FlowNode flowNode) {
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
  
  private void deletedConnectingFlows(Association association) {
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
  
  private void removeArtifact(Artifact element) {
    List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeArtifact(element.getId());
      removeArtifactInProcess(element, process);
    }
  }
  
  private void removeArtifactInProcess(Artifact element, FlowElementsContainer parentElement) {
    Collection<FlowElement> elementList = parentElement.getFlowElements();
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeArtifact(element.getId());
        removeArtifactInProcess(element, subProcess);
      }
    }
  }

	private FlowElement getFlowElement(FlowElement flowElement) {
	  Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	  for (Process process : model.getBpmnModel().getProcesses()) {
	    FlowElement processElement = getFlowElementInProcess(flowElement, process.getFlowElements());
	    if(processElement != null) return processElement;
    }
		return null;
	}
	
	private FlowElement getFlowElementInProcess(FlowElement flowElement, Collection<FlowElement> elementList) {
	  for (FlowElement element : elementList) {
      
      if (element.getId().equals(flowElement.getId())) {
        return element;
      }
      
      if (element instanceof SubProcess) {
        FlowElement subFlowElement = getFlowElementInProcess(flowElement, ((SubProcess) element).getFlowElements());
        if(subFlowElement != null) return subFlowElement;
      }
    }
	  return null;
	}

}
