package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.Event;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.bpmn2.model.Task;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.EObject;
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
		}

		if (bo instanceof SubProcess || bo instanceof Task) {
		  if(((Activity) bo).getBoundaryEvents() != null) {
		    for (BoundaryEvent boundaryEvent : ((Activity) bo).getBoundaryEvents()) {
		      removeElement(boundaryEvent);
        }
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
      SequenceFlow toDeleteObject = (SequenceFlow) getFlowElement(incomingSequenceFlow);
      if (toDeleteObject != null) {
        toDeleteSequenceFlows.add(toDeleteObject);
      }
    }
    for (SequenceFlow outgoingSequenceFlow : flowNode.getOutgoing()) {
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
	  for (EObject diagramObject : getDiagram().eResource().getContents()) {
  	  if(diagramObject instanceof FlowNode) {
        SequenceFlow foundIncoming = null;
        SequenceFlow foundOutgoing = null;
        for(SequenceFlow flow : ((FlowNode) diagramObject).getIncoming()) {
          if(flow.getId().equals(sequenceFlow.getId())) {
            foundIncoming = flow;
          }
        }
        for(SequenceFlow flow : ((FlowNode) diagramObject).getOutgoing()) {
          if(flow.getId().equals(sequenceFlow.getId())) {
            foundOutgoing = flow;
          }
        }
        if(foundIncoming != null) {
          ((FlowNode) diagramObject).getIncoming().remove(foundIncoming);
        }
        if(foundOutgoing != null) {
          ((FlowNode) diagramObject).getOutgoing().remove(foundOutgoing);
        }
      }
	  }
	}

	private FlowElement getFlowElement(FlowElement flowElement) {
	  Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
	  for (Process process : model.getProcesses()) {
	    FlowElement processElement = getFlowElementInProcess(flowElement, process.getFlowElements());
	    if(processElement != null) return processElement;
    }
		return null;
	}
	
	private FlowElement getFlowElementInProcess(FlowElement flowElement, List<FlowElement> elementList) {
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
