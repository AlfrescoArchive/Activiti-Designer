package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFlowElementFeature extends DefaultDeleteFeature {

	public DeleteFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected void deleteBusinessObject(Object bo) {
		if (bo instanceof Task || bo instanceof Gateway || bo instanceof Event || bo instanceof SubProcess) {
		  deleteSequenceFlows((FlowNode) bo);
		}

		if (bo instanceof EObject) {

			// If this is a custom service task, all of the linked custom
			// properties should also be removed
			if (bo instanceof ServiceTask && ExtensionUtil.isCustomServiceTask((EObject) bo)) {

				final List<EObject> toDeleteCustomProperties = new ArrayList<EObject>();

				final ServiceTask serviceTask = (ServiceTask) bo;
				for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
					toDeleteCustomProperties.add(customProperty);
				}

				for (final EObject deleteObject : toDeleteCustomProperties) {
					EcoreUtil.delete(deleteObject, true);
				}
			}
			
			if (bo instanceof SubProcess || bo instanceof Task) {
			  if(((Activity) bo).getBoundaryEventRefs() != null) {
			    for (BoundaryEvent boundaryEvent : ((Activity) bo).getBoundaryEventRefs()) {
			      EObject toDeleteEvent = getFlowElement(boundaryEvent);
			      if(toDeleteEvent != null) {
			        EcoreUtil.delete(toDeleteEvent, true);
			      }
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
          EcoreUtil.delete(subFlowElement, true);
        }
			  subProcess.getFlowElements().clear();
			}

			EcoreUtil.delete((EObject) bo, true);
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
      EcoreUtil.delete(deleteObject, true);
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

	private EObject getFlowElement(FlowElement flowElement) {
		for (EObject diagramObject : getDiagram().eResource().getContents()) {
		  
		  if(diagramObject instanceof FlowElement == false) continue;
		  
			if (((FlowElement) diagramObject).getId().equals(flowElement.getId())) {

				return diagramObject;
			}
			
			if (diagramObject instanceof SubProcess) {
			  for (FlowElement subFlowElement : ((SubProcess) diagramObject).getFlowElements()) {
			    if (subFlowElement.getId().equals(flowElement.getId())) {

	          return subFlowElement;
	        }
        }
			}
		}
		return null;
	}

}
