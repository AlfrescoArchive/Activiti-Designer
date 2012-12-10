package org.activiti.designer.features;

import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;


public class DeleteAssociationFeature extends AbstractCustomFeature {

  public DeleteAssociationFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  public String getDescription() {
    return "Delete Association";
  }

  @Override
  public String getName() {
    return "Delete Association";
  }
  
  @Override
  public boolean canExecute(ICustomContext context) {
    final PictogramElement[] pictogramElements = context.getPictogramElements();
    if (context.getPictogramElements() == null) {
      return false;
    }
    
    for (final PictogramElement pictogramElement : pictogramElements) {
      final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo == null) {
        continue;
      }
      if (!(bo instanceof Association)) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public void execute(ICustomContext context) {
    final PictogramElement[] pictogramElements = context.getPictogramElements();
    
    for (final PictogramElement pictogramElement : pictogramElements) {
      final Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo == null) {
        continue;
      }
      
      final Association association = (Association) bo;
      
      getDiagram().getPictogramLinks().remove(pictogramElement.getLink());
      getDiagram().getConnections().remove(pictogramElement);
      
      final List<Process> processes = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getBpmnModel().getProcesses();
      for (final Process process : processes) {
        process.getArtifacts().remove(association);
        
        removeArtifact(association, process);
      }
    }
  }

  private void removeArtifact(Association association, BaseElement parentElement) {
    Collection<FlowElement> elementList = null;
    if (parentElement instanceof Process) {
      elementList = ((Process) parentElement).getFlowElements();
    } else if (parentElement instanceof SubProcess) {
      elementList = ((SubProcess) parentElement).getFlowElements();
    }
    
    for (final FlowElement flowElement : elementList) {
      if (flowElement instanceof SubProcess) {
        final SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeArtifact(association.getId());
        
        removeArtifact(association, subProcess);
      }
    }
  }
}
