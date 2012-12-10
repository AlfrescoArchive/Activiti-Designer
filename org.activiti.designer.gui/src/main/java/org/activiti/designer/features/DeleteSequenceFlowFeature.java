package org.activiti.designer.features;

import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class DeleteSequenceFlowFeature extends AbstractCustomFeature {

  public DeleteSequenceFlowFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Delete sequence flow"; //$NON-NLS-1$
  }

  @Override
  public String getDescription() {
    return "Delete sequence flow"; //$NON-NLS-1$
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    if(context.getPictogramElements() == null) return false;
    for (PictogramElement pictogramElement : context.getPictogramElements()) {
      if(getBusinessObjectForPictogramElement(pictogramElement) == null) continue;
      Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == false) {
        return false;
      }
    }
    return true;
  }

  public void execute(ICustomContext context) {
    if(context.getPictogramElements() == null) return;
    
    for (final PictogramElement pictogramElement : context.getPictogramElements()) {
      if(getBusinessObjectForPictogramElement(pictogramElement) == null) continue;
      final Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == true) {
        final SequenceFlow sequenceFlow = (SequenceFlow) boObject;
        
        getDiagram().getPictogramLinks().remove(pictogramElement.getLink());
        getDiagram().getConnections().remove(pictogramElement);
        
        Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
        FlowElement sourceElement = null;
        String sourceRef = sequenceFlow.getSourceRef();
        if (StringUtils.isNotEmpty(sourceRef)) {
          sourceElement = model.getBpmnModel().getFlowElement(sourceRef);
        }
        
        FlowElement targetElement = null;
        String targetRef = sequenceFlow.getTargetRef();
        if (StringUtils.isNotEmpty(targetRef)) {
          targetElement = model.getBpmnModel().getFlowElement(targetRef);
        }
        
        if (sourceElement != null) {
          sourceElement.getOutgoingFlows().remove(sequenceFlow);
        }
        
        if (targetElement != null) {
          targetElement.getIncomingFlows().remove(sequenceFlow);
        }
        
        List<Process> processes = model.getBpmnModel().getProcesses();
        for (Process process : processes) {
          process.removeFlowElement(sequenceFlow.getId());
          removeFlow(sequenceFlow, process);
        }
      }
    }
  }
  
  private void removeFlow(SequenceFlow sequenceFlow, BaseElement parentElement) {
    Collection<FlowElement> elementList = null;
    if (parentElement instanceof Process) {
      elementList = ((Process) parentElement).getFlowElements();
    } else if (parentElement instanceof SubProcess) {
      elementList = ((SubProcess) parentElement).getFlowElements();
    }
    
    for (FlowElement flowElement : elementList) {
      if(flowElement instanceof SubProcess) {
        SubProcess subProcess = (SubProcess) flowElement;
        subProcess.removeFlowElement(sequenceFlow.getId());
        removeFlow(sequenceFlow, subProcess);
      }
    }
  }
}
