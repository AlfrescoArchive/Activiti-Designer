package org.activiti.designer.features;

import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;

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
      if(pictogramElement.getLink() == null) continue;
      Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == false) {
        return false;
      }
    }
    return true;
  }

  public void execute(ICustomContext context) {
    if(context.getPictogramElements() == null) return;
    ILinkService linkService = Graphiti.getLinkService();
    for (final PictogramElement pictogramElement : context.getPictogramElements()) {
      if(pictogramElement.getLink() == null) continue;
      final Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == true) {
        final SequenceFlow sequenceFlow = (SequenceFlow) boObject;
        for(Shape shape : getDiagram().getChildren()) {
          FlowElement flowElement = (FlowElement) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
          
          if(flowElement instanceof SubProcess) {
            List<PictogramElement> pictoList = linkService.getPictogramElements(getDiagram(), flowElement);
            if(pictoList != null && pictoList.size() > 0) {
              ContainerShape parent = (ContainerShape) pictoList.get(0);
              for(Shape subShape : parent.getChildren()) {
                for(FlowElement subFlowElement : ((SubProcess) flowElement).getFlowElements()) {
                  removeAnchors(sequenceFlow, subFlowElement, subShape);
                }
              }
              ((SubProcess) flowElement).getFlowElements().remove(sequenceFlow);
            }
          } else {
            removeAnchors(sequenceFlow, flowElement, shape);
          }
        }
        
        getDiagram().getPictogramLinks().remove(pictogramElement.getLink());
        getDiagram().getConnections().remove(pictogramElement);
        if(sequenceFlow.getSourceRef() != null) {
          sequenceFlow.getSourceRef().getOutgoing().remove(sequenceFlow);
        }
        if(sequenceFlow.getTargetRef() != null) {
          sequenceFlow.getTargetRef().getIncoming().remove(sequenceFlow);
        }
        getDiagram().eResource().getContents().remove(sequenceFlow);
      }
    }
  }
  
  private void removeAnchors(SequenceFlow sequenceFlow, FlowElement flowElement, Shape shape) {
    if(flowElement.getId().equals(sequenceFlow.getSourceRef().getId())) {
      EList<Anchor> anchorList = shape.getAnchors();
      for (Anchor anchor : anchorList) {
        if(anchor instanceof ChopboxAnchor) {
          Connection toDeletedConnection = null;
          for (Connection connection : anchor.getOutgoingConnections()) {
            SequenceFlow outFlow = (SequenceFlow) getBusinessObjectForPictogramElement(connection);
            if(outFlow.getId().equals(sequenceFlow.getId())) {
              toDeletedConnection = connection;
            }
          }
          if(toDeletedConnection != null) {
            anchor.getOutgoingConnections().remove(toDeletedConnection);
          }
        }
      }
    }
    if(flowElement.getId().equals(sequenceFlow.getTargetRef().getId())) {
      EList<Anchor> anchorList = shape.getAnchors();
      for (Anchor anchor : anchorList) {
        if(anchor instanceof ChopboxAnchor) {
          Connection toDeletedConnection = null;
          for (Connection connection : anchor.getIncomingConnections()) {
            SequenceFlow outFlow = (SequenceFlow) getBusinessObjectForPictogramElement(connection);
            if(outFlow.getId().equals(sequenceFlow.getId())) {
              toDeletedConnection = connection;
            }
          }
          if(toDeletedConnection != null) {
            anchor.getIncomingConnections().remove(toDeletedConnection);
          }
        }
      }
    }
  }
}
