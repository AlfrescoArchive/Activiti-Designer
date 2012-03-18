package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.util.editor.ModelHandler;
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
        if(sequenceFlow.getSourceRef() != null) {
          sequenceFlow.getSourceRef().getOutgoing().remove(sequenceFlow);
        }
        if(sequenceFlow.getTargetRef() != null) {
          sequenceFlow.getTargetRef().getIncoming().remove(sequenceFlow);
        }
        ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getProcess().getFlowElements().remove(sequenceFlow);
      }
    }
  }
}
