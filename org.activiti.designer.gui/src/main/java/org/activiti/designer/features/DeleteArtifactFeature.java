package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteArtifactFeature extends DefaultDeleteFeature {

	public DeleteArtifactFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected void deleteBusinessObject(Object bo) {
		if (bo instanceof TextAnnotation) {
		  deleteAssociations((TextAnnotation) bo);
		}
		
		if (bo instanceof Association) {
      deletedConnectingFlows((Association) bo);
    }

		removeElement((BaseElement) bo);
	}
	
	private void removeElement(BaseElement element) {
  	List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
    for (Process process : processes) {
      process.removeArtifact(element.getId());
      removeElementInProcess(element, process);
    }
	}
	
	private void removeElementInProcess(BaseElement element, FlowElementsContainer parentElement) {
	  Collection<FlowElement> elementList = parentElement.getFlowElements();
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeArtifact(element.getId());
        removeElementInProcess(element, subProcess);
      }
    }
  }
	
	private void deleteAssociations(TextAnnotation annotation) {
	  List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
	  for (Process process : processes) {
	    removeAssociation(process.getArtifacts(), annotation);
      removeAssociationInProcess(process, annotation);
	  }
	}
	
	private void removeAssociationInProcess(FlowElementsContainer parentElement, TextAnnotation annotation) {
    Collection<FlowElement> elementList = parentElement.getFlowElements();
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        removeAssociation(subProcess.getArtifacts(), annotation);
        removeAssociationInProcess(subProcess, annotation);
      }
    }
  }
	
	protected void removeAssociation(Collection<Artifact> artifacts, TextAnnotation annotation) {
	  List<Association> toDeleteAssociations = new ArrayList<Association>();
	  for (Artifact artifact : artifacts) {
      if (artifact instanceof Association) {
        Association association = (Association) artifact;
        if (association.getSourceRef().equals(annotation.getId()) || association.getTargetRef().equals(annotation.getId())) {
          toDeleteAssociations.add(association);
        }
      }
    }
	  
	  for (Association deleteObject : toDeleteAssociations) {
	    deletedConnectingFlows(deleteObject);
      removeElement(deleteObject);
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
}
