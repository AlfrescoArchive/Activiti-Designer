package org.activiti.designer.eclipse.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;


public class GraphitiToBpmnDI {

  public void processGraphitiElements(Bpmn2MemoryModel model, IFeatureProvider featureProvider) throws Exception {
    
    for (Pool pool : model.getBpmnModel().getPools()) {
      updateFlowElement(pool, model, featureProvider);
      
      Process process = model.getBpmnModel().getProcess(pool.getId());
      if(process != null) {
        for (Lane lane : process.getLanes()) {
          updateFlowElement(lane, model, featureProvider);
        }
      }
    }
    
    for (Process process : model.getBpmnModel().getProcesses()) {
      loopThroughElements(process.getFlowElements(), model, featureProvider);
      loopThroughElements(process.getArtifacts(), model, featureProvider);
    }
  }
  
  protected void loopThroughElements(Collection<? extends BaseElement> elementList, 
          Bpmn2MemoryModel model, IFeatureProvider featureProvider) throws Exception {
    
    for (BaseElement element : elementList) {
      if (element instanceof SequenceFlow) {
        updateSequenceFlow((SequenceFlow) element, model, featureProvider);
      } else if (element instanceof FlowElement) {
        updateFlowElement(element, model, featureProvider);
        if(element instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) element;
          loopThroughElements(subProcess.getFlowElements(), model, featureProvider);
          loopThroughElements(subProcess.getArtifacts(), model, featureProvider);
        }
        if(element instanceof Activity) {
          Activity activity = (Activity) element;
          for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
            updateFlowElement(boundaryEvent, model, featureProvider);
          }
        }
      } else if (element instanceof Artifact) {
        if (element instanceof Association) {
          updateAssociation((Association) element, model, featureProvider);
        } else {
          updateFlowElement(element, model, featureProvider);
        }
      }
    }
  }
  
  protected void updateFlowElement(BaseElement flowElement, Bpmn2MemoryModel model, IFeatureProvider featureProvider) {
    PictogramElement picElement = featureProvider.getPictogramElementForBusinessObject(flowElement);
    if(picElement instanceof Shape) {
      Shape shape = (Shape) picElement;
      ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
      model.getBpmnModel().addGraphicInfo(flowElement.getId(), createGraphicInfo(shapeLocation.getX(), shapeLocation.getY(), 
              shape.getGraphicsAlgorithm().getWidth(), shape.getGraphicsAlgorithm().getHeight()));
    }
  }
  
  protected void updateSequenceFlow(SequenceFlow sequenceFlow, Bpmn2MemoryModel model, IFeatureProvider featureProvider) {
    Shape sourceShape = null;
    Shape targetShape = null;
    if(StringUtils.isNotEmpty(sequenceFlow.getSourceRef())) {
      sourceShape = (Shape) featureProvider.getPictogramElementForBusinessObject(model.getFlowElement(sequenceFlow.getSourceRef()));
    }
    if(StringUtils.isNotEmpty(sequenceFlow.getTargetRef())) {
      targetShape = (Shape) featureProvider.getPictogramElementForBusinessObject(model.getFlowElement(sequenceFlow.getTargetRef()));
    }
   
    if(sourceShape == null || targetShape == null) {
      return;
    }
    
    FreeFormConnection freeFormConnection = (FreeFormConnection) featureProvider.getPictogramElementForBusinessObject(sequenceFlow);
    
    if(freeFormConnection == null) 
      return;
    
    List<GraphicInfo> flowGraphicsList = createFlowGraphicInfoList(sourceShape, targetShape, freeFormConnection);
    model.getBpmnModel().addFlowGraphicInfoList(sequenceFlow.getId(), flowGraphicsList);
    
    EList<ConnectionDecorator> decoratorList = freeFormConnection.getConnectionDecorators();
    for (ConnectionDecorator decorator : decoratorList) {
      if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
        org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
        if(text.getHeight() > 0) {
          model.getBpmnModel().addLabelGraphicInfo(sequenceFlow.getId(), createGraphicInfo(
                  text.getX(), text.getY(), text.getWidth(), text.getHeight()));
          break;
        }
      }
    }
  }
  
  protected void updateAssociation(Association association, Bpmn2MemoryModel model, IFeatureProvider featureProvider) {
    Shape sourceShape = null;
    Shape targetShape = null;
    if(StringUtils.isNotEmpty(association.getSourceRef())) {
      BaseElement sourceElement = model.getFlowElement(association.getSourceRef());
      if (sourceElement == null) {
        sourceElement = model.getArtifact(association.getSourceRef());
      }
      sourceShape = (Shape) featureProvider.getPictogramElementForBusinessObject(sourceElement);
    }
    if(StringUtils.isNotEmpty(association.getTargetRef())) {
      BaseElement targetElement = model.getFlowElement(association.getTargetRef());
      if (targetElement == null) {
        targetElement = model.getArtifact(association.getTargetRef());
      }
      targetShape = (Shape) featureProvider.getPictogramElementForBusinessObject(targetElement);
    }
   
    if(sourceShape == null || targetShape == null) {
      return;
    }
    
    FreeFormConnection freeFormConnection = (FreeFormConnection) featureProvider.getPictogramElementForBusinessObject(association);
    
    if(freeFormConnection == null) 
      return;
    
    List<GraphicInfo> flowGraphicsList = createFlowGraphicInfoList(sourceShape, targetShape, freeFormConnection);
    model.getBpmnModel().addFlowGraphicInfoList(association.getId(), flowGraphicsList);
  }
  
  protected List<GraphicInfo> createFlowGraphicInfoList(Shape sourceElement, Shape targetElement, FreeFormConnection freeFormConnection) {
    ILocation sourceLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(sourceElement);
    int sourceX = sourceLocation.getX();
    int sourceY = sourceLocation.getY();
    int sourceWidth = sourceElement.getGraphicsAlgorithm().getWidth();
    int sourceHeight = sourceElement.getGraphicsAlgorithm().getHeight();
    int sourceMiddleX = sourceX + (sourceWidth / 2);
    int sourceMiddleY = sourceY + (sourceHeight / 2);
    int sourceBottomY = sourceY + sourceHeight;
    
    ILocation targetLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(targetElement);
    int targetX = targetLocation.getX();
    int targetY = targetLocation.getY();
    int targetWidth = targetElement.getGraphicsAlgorithm().getWidth();
    int targetHeight = targetElement.getGraphicsAlgorithm().getHeight();
    int targetMiddleX = targetX + (targetWidth / 2);
    int targetMiddleY = targetY + (targetHeight / 2);
    int targetBottomY = targetY + targetHeight;
    
    List<GraphicInfo> flowGraphicsList = new ArrayList<GraphicInfo>();
    
    if (sourceElement instanceof BoundaryEvent) {
      flowGraphicsList.add(createFlowGraphicInfo(sourceMiddleX, sourceY + sourceHeight));
    } else {
      
     if((freeFormConnection.getBendpoints() == null || freeFormConnection.getBendpoints().size() == 0)) {
    
       if((sourceBottomY + 11) < targetY) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceMiddleX, sourceY + sourceHeight));
        
       } else if((sourceY - 11) > (targetY + targetHeight)) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceMiddleX, sourceY));
      
       } else if(sourceX > targetX) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceX, sourceMiddleY));
      
       } else {
         flowGraphicsList.add(createFlowGraphicInfo(sourceX + sourceWidth, sourceMiddleY));
       }
    
     } else {
    
       org.eclipse.graphiti.mm.algorithms.styles.Point bendPoint = freeFormConnection.getBendpoints().get(0);
       if((sourceBottomY + 5) < bendPoint.getY()) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceMiddleX, sourceY + sourceHeight));
      
       } else if((sourceY - 5) > bendPoint.getY()) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceMiddleX, sourceY));
      
       } else if(sourceX > bendPoint.getX()) {
         flowGraphicsList.add(createFlowGraphicInfo(sourceX, sourceMiddleY));
      
       } else {
         flowGraphicsList.add(createFlowGraphicInfo(sourceX + sourceWidth, sourceMiddleY));
       }
     }
    }
    
    if(freeFormConnection.getBendpoints() != null && freeFormConnection.getBendpoints().size() > 0) {
      for (org.eclipse.graphiti.mm.algorithms.styles.Point point : freeFormConnection.getBendpoints()) {
        flowGraphicsList.add(createFlowGraphicInfo(point.getX(), point.getY()));
      }
    }
    
    int difference = 5;
  
    if((freeFormConnection.getBendpoints() == null || freeFormConnection.getBendpoints().size() == 0)) {
      difference = 11;
    }
  
    GraphicInfo lastGraphicInfo = flowGraphicsList.get(flowGraphicsList.size() - 1);
    
    if((targetBottomY + difference) < lastGraphicInfo.y) {
      flowGraphicsList.add(createFlowGraphicInfo(targetMiddleX, targetY + targetHeight));

    } else if((targetY - difference) > lastGraphicInfo.y) {
      flowGraphicsList.add(createFlowGraphicInfo(targetMiddleX, targetY));

    } else if(targetX > lastGraphicInfo.x) {
      flowGraphicsList.add(createFlowGraphicInfo(targetX, targetMiddleY));

    } else {
      flowGraphicsList.add(createFlowGraphicInfo(targetX + targetWidth, targetMiddleY));
    }
    
    return flowGraphicsList;
  }
  
  protected GraphicInfo createFlowGraphicInfo(int x, int y) {
    GraphicInfo graphicInfo = new GraphicInfo();
    graphicInfo.x = x;
    graphicInfo.y = y;
    return graphicInfo;
  }
  
  protected GraphicInfo createGraphicInfo(int x, int y, int width, int height) {
    GraphicInfo graphicInfo = new GraphicInfo();
    graphicInfo.x = x;
    graphicInfo.y = y;
    graphicInfo.width = width;
    graphicInfo.height = height;
    return graphicInfo;
  }
}
