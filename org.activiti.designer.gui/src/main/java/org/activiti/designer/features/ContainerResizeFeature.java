/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Transaction;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;


/**
 * @author Tijs Rademakers
 */
public class ContainerResizeFeature extends DefaultResizeShapeFeature {
  
  public ContainerResizeFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    boolean canResize = super.canResizeShape(context);

    // perform further check only if move allowed by default feature
    if (canResize == true) {
      // don't allow resize if the class name has the length of 1
      Shape shape = context.getShape();
      Object bo = getBusinessObjectForPictogramElement(shape);
      if (bo instanceof SubProcess || bo instanceof Pool || bo instanceof Lane) {
        canResize = true;
      } else {
        canResize = false;
      }
    }
    return canResize;
  }

  @Override
  public void resizeShape(IResizeShapeContext context) {
    int height = context.getHeight();
    int width = context.getWidth();
    if (height < 55) {
      height = 55;
    }
    if (width < 105) {
      width = 105;
    }
    
    Shape shape = context.getShape();
    int oldX = shape.getGraphicsAlgorithm().getX();
    int oldY = shape.getGraphicsAlgorithm().getY();
    int deltaWidth = width - shape.getGraphicsAlgorithm().getWidth();
    int deltaHeight = height - shape.getGraphicsAlgorithm().getHeight();
    
    Object bo = getBusinessObjectForPictogramElement(shape);
    setSize(shape, bo, width, height);
    
    if (bo instanceof Lane || bo instanceof Pool) {
      centerText((ContainerShape) shape);
    }
    
    if (bo instanceof Lane) {
      Lane lane = (Lane) bo;
      List<Lane> sortedLanes = sortLanesByHorizontalOrder(lane.getParentProcess().getLanes());
      List<String> sortedLaneIds = createLaneOrderedIdList(sortedLanes);
      int laneIndex = sortedLaneIds.indexOf(lane.getId());
      
      ContainerShape poolShape = shape.getContainer();
      int newWidth = poolShape.getGraphicsAlgorithm().getWidth() + deltaWidth;
      int newHeight = poolShape.getGraphicsAlgorithm().getHeight() + deltaHeight;
      
      if ((deltaHeight > 0 && context.getDirection() == IResizeShapeContext.DIRECTION_NORTH) || 
          (deltaHeight < 0 && context.getDirection() == IResizeShapeContext.DIRECTION_SOUTH)) {
        
        Graphiti.getGaService().setLocationAndSize(poolShape.getGraphicsAlgorithm(), poolShape.getGraphicsAlgorithm().getX(), 
            poolShape.getGraphicsAlgorithm().getY() - deltaHeight, newWidth, newHeight);
        ((ResizeShapeContext) context).setY(oldY);
      
      } else if (deltaHeight != 0 || deltaWidth != 0) {
        Graphiti.getGaService().setSize(poolShape.getGraphicsAlgorithm(), newWidth, newHeight);
        if (context.getDirection() == IResizeShapeContext.DIRECTION_NORTH) {
          ((ResizeShapeContext) context).setY(oldY);
        }
      }
      
      for (GraphicsAlgorithm childGraphicsAlgorithm : poolShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
        int newChildWidth = childGraphicsAlgorithm.getWidth() + deltaWidth;
        int newChildHeight = childGraphicsAlgorithm.getHeight() + deltaHeight;
        Graphiti.getGaService().setSize(childGraphicsAlgorithm, newChildWidth, newChildHeight);
      }
      
      for (Lane lanePositionObject : lane.getParentProcess().getLanes()) {
        ContainerShape laneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(lanePositionObject);
        if (lanePositionObject.getId().equals(lane.getId()) == false && sortedLaneIds.indexOf(lanePositionObject.getId()) > laneIndex) {
          Graphiti.getGaService().setLocation(laneShape.getGraphicsAlgorithm(), laneShape.getGraphicsAlgorithm().getX(), 
              laneShape.getGraphicsAlgorithm().getY() + deltaHeight);
        }
        
        if (deltaWidth != 0 && lanePositionObject.getId().equals(lane.getId()) == false) {
          Graphiti.getGaService().setWidth(laneShape.getGraphicsAlgorithm(), laneShape.getGraphicsAlgorithm().getWidth() + deltaWidth);
          for (GraphicsAlgorithm childGraphicsAlgorithm : laneShape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
            Graphiti.getGaService().setWidth(childGraphicsAlgorithm, childGraphicsAlgorithm.getWidth() + deltaWidth);
          }
        }
      }
        
      centerText(poolShape);
      
      List<Association> associations = new ArrayList<Association>();
      findAllAssociations(lane.getParentProcess(), associations);
      Map<String, List<Association>> associationMap = new HashMap<String, List<Association>>();
      for (Association association : associations) {
        List<Association> sourceAssociations = null;
        if (associationMap.containsKey(association.getSourceRef()) == false) {
          sourceAssociations = new ArrayList<Association>();
        } else {
          sourceAssociations = associationMap.get(association.getSourceRef());
        }
        sourceAssociations.add(association);
        associationMap.put(association.getSourceRef(), sourceAssociations);
      }
      
      List<String> flowReferences = new ArrayList<String>();
      if ((deltaHeight > 0 && context.getDirection() == IResizeShapeContext.DIRECTION_NORTH) || 
          (deltaHeight < 0 && context.getDirection() == IResizeShapeContext.DIRECTION_SOUTH)) {
        
        for (int i = 0; i < laneIndex + 1; i++) {
          Lane laneFlowReferences = sortedLanes.get(i);
          flowReferences.addAll(laneFlowReferences.getFlowReferences());
        }
        moveChildElements(lane.getParentProcess().getFlowElements(), flowReferences, associationMap, deltaWidth, -deltaHeight);
      
      } else if ((deltaHeight < 0 && context.getDirection() == IResizeShapeContext.DIRECTION_NORTH) || 
          (deltaHeight > 0 && context.getDirection() == IResizeShapeContext.DIRECTION_SOUTH)) {
        
        for (int i = laneIndex + 1; i < sortedLanes.size(); i++) {
          Lane laneFlowReferences = sortedLanes.get(i);
          flowReferences.addAll(laneFlowReferences.getFlowReferences());
        }
        moveChildElements(lane.getParentProcess().getFlowElements(), flowReferences, associationMap, deltaWidth, deltaHeight);
      }
      
    } else if (bo instanceof Pool) {
      
      if (context.getProperty("org.activiti.designer.lane.create") == null) {
        BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
        Pool pool = (Pool) bo;
        Process process = model.getBpmnModel().getProcess(pool.getId());
        if (process != null) {
          int deltaLaneHeight = deltaHeight / process.getLanes().size();
          List<Lane> sortedLanes = sortLanesByHorizontalOrder(process.getLanes());
          int yShift = 0;
          for (Lane lane : sortedLanes) {
            ContainerShape laneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(lane);
            setSize(laneShape, bo, laneShape.getGraphicsAlgorithm().getWidth() + deltaWidth, 
                    laneShape.getGraphicsAlgorithm().getHeight() + deltaLaneHeight);
            centerText(laneShape);
            laneShape.getGraphicsAlgorithm().setY(laneShape.getGraphicsAlgorithm().getY() + yShift);
            yShift += deltaLaneHeight;
          }
        }
      }
    }
    
    super.resizeShape(context);
    
    int newX = shape.getGraphicsAlgorithm().getX();
    int newY = shape.getGraphicsAlgorithm().getY();
    boolean enableMultiDiagram = PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ENABLE_MULTI_DIAGRAM, ActivitiPlugin.getDefault());
    
    if (bo instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) bo;
      for (BoundaryEvent boundaryEvent : subProcess.getBoundaryEvents()) {
        PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
        boundaryElement.getGraphicsAlgorithm().setX(boundaryElement.getGraphicsAlgorithm().getX() + newX - oldX);
        if (newY - oldY == 0) {
          boundaryElement.getGraphicsAlgorithm().setY(boundaryElement.getGraphicsAlgorithm().getY() + deltaHeight);
        }
      }

    if (enableMultiDiagram) {
      context.getShape().getGraphicsAlgorithm().setHeight(height);
      context.getShape().getGraphicsAlgorithm().setWidth(width);
    } else {
      moveSubProcessElements(subProcess, oldX, oldY, newX, newY);
    }

    List<Shape> childShapes = ((ContainerShape) context.getShape()).getChildren();
    for (Shape childShape : childShapes) {
      if (childShape.getGraphicsAlgorithm() != null) {
        if (childShape.getGraphicsAlgorithm() instanceof Image) {
          Image image = (Image) childShape.getGraphicsAlgorithm();
          boolean isSubprocess = image.getId().endsWith(PluginImage.IMG_SUBPROCESS_EXPANDED.getImageKey());

          if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey()) 
              || image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey()) 
              || (enableMultiDiagram && isSubprocess)) {

            final int iconWidthAndHeight = 12;
            final int xPos = (context.getShape().getGraphicsAlgorithm().getWidth() / 2) - (iconWidthAndHeight / 2);
            if (isSubprocess) {
              final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight-2;
              image.setY(yPos);
            } else {
              final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight;
              image.setY(yPos);
            }

            image.setX(xPos);

          } else if (image.getId().endsWith(PluginImage.IMG_ACTIVITY_COMPENSATION.getImageKey())) {

            final int iconWidthAndHeight = 12;
            final int xPos = (context.getShape().getGraphicsAlgorithm().getWidth() / 2) - (iconWidthAndHeight / 2) + iconWidthAndHeight + 5;
            final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight;

            image.setX(xPos);
            image.setY(yPos);
          } 
        } else if (enableMultiDiagram && childShape.getGraphicsAlgorithm() instanceof Text) {
          Text text = (Text)childShape.getGraphicsAlgorithm();
          Graphiti.getGaService().setLocationAndSize(text, 0, 0, width, 20);
        }
      }
    }
  }
}

  protected void moveSubProcessElements(SubProcess subProcess, int oldX, int oldY, int newX, int newY) {
    int deltaX = 0;
    int deltaY = 0;
    if (oldX != newX) {
      deltaX = newX - oldX;
    }
    
    if (oldY != newY) {
      deltaY = newY - oldY;
    }
    
    for (FlowElement flowElement : subProcess.getFlowElements()) {
      if (flowElement instanceof Activity) {
        Activity activity = (Activity) flowElement;
        for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
          if (oldX != newX) {
            PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
            boundaryElement.getGraphicsAlgorithm().setX(boundaryElement.getGraphicsAlgorithm().getX() + deltaX);
          }
          
          if (oldY != newY) {
            PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
            boundaryElement.getGraphicsAlgorithm().setY(boundaryElement.getGraphicsAlgorithm().getY() + deltaY);
          }
        }
        
        if (flowElement instanceof SubProcess) {
          moveSubProcessElements((SubProcess) flowElement, oldX, oldY, newX, newY);
        }
      
      } else if (flowElement instanceof SequenceFlow) {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        
        FreeFormConnection freeFormConnection = (FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(sequenceFlow);
        moveBendpoints(freeFormConnection, deltaX, deltaY);
      }
    }
    
    for (Artifact artifact : subProcess.getArtifacts()) {
      if (artifact instanceof Association) {
        FreeFormConnection freeFormConnection = (FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(artifact);
        moveBendpoints(freeFormConnection, deltaX, deltaY);
      }
    }
  }
  
  protected void moveChildElements(Collection<FlowElement> flowElements, List<String> flowReferences, 
      Map<String, List<Association>> associationMap, int deltaWidth, int deltaHeight) {
    
    for (FlowElement flowElement : flowElements) {
      
      if (flowReferences.contains(flowElement.getId()) && associationMap.containsKey(flowElement.getId())) {
        List<Association> associations = associationMap.get(flowElement.getId());
        for (Association association : associations) {
          FreeFormConnection freeFormConnection = (FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(association);
          moveBendpoints(freeFormConnection, deltaWidth, deltaHeight);
        }
        associationMap.remove(flowElement.getId());
      }
      
      if (flowElement instanceof Activity) {
        if (flowReferences.contains(flowElement.getId()) == false) continue;
        
        Activity activity = (Activity) flowElement;
        for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
          PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
          GraphicsAlgorithm boundaryGraphics = boundaryElement.getGraphicsAlgorithm();
          Graphiti.getGaService().setLocation(boundaryGraphics, boundaryGraphics.getX() + deltaWidth, 
              boundaryGraphics.getY() + deltaHeight);
        }
        
        if (flowElement instanceof FlowElementsContainer) {
          moveChildElements(((FlowElementsContainer) flowElement).getFlowElements(), flowReferences, 
              associationMap, deltaWidth, deltaHeight);
        }
      
      } else if (flowElement instanceof SequenceFlow) {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        
        if (flowReferences.contains(sequenceFlow.getSourceRef()) == false) continue;
        
        FreeFormConnection freeFormConnection = (FreeFormConnection) getFeatureProvider().getPictogramElementForBusinessObject(sequenceFlow);
        moveBendpoints(freeFormConnection, deltaWidth, deltaHeight);
      }
    }
  }
  
  protected void moveBendpoints(FreeFormConnection freeFormConnection, int deltaWidth, int deltaHeight) {
    if (freeFormConnection.getBendpoints() != null && freeFormConnection.getBendpoints().size() > 0) {
      for (Point point : freeFormConnection.getBendpoints()) {
        point.setX(point.getX() + deltaWidth);
        point.setY(point.getY() + deltaHeight);
      }
    }
  }
  
  protected void centerText(ContainerShape shape) {
    for (Shape shapeChild : shape.getChildren()) {
      if (shapeChild.getGraphicsAlgorithm() instanceof Text) {
        Text text = (Text) shapeChild.getGraphicsAlgorithm();
        Graphiti.getGaService().setLocationAndSize(text, 0, 0, 20, shape.getGraphicsAlgorithm().getHeight());
      }
    }
  }
  
  protected void setSizeOfObject(Shape shape, int width, int height) {
    shape.getGraphicsAlgorithm().setHeight(height);
    shape.getGraphicsAlgorithm().setWidth(width);
  }
  
  protected void setSize(Shape shape, Object bo, int width, int height) {
    int originalWidth = shape.getGraphicsAlgorithm().getWidth();
    shape.getGraphicsAlgorithm().setHeight(height);
    shape.getGraphicsAlgorithm().setWidth(width);
    for (GraphicsAlgorithm graphicsAlgorithm : shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
      // check if it's the transaction inner rectangle
      if (bo instanceof Transaction && graphicsAlgorithm.getWidth() + 1 < originalWidth) {
        graphicsAlgorithm.setHeight(height - 4);
        graphicsAlgorithm.setWidth(width - 4);
      } else {
        graphicsAlgorithm.setHeight(height);
        graphicsAlgorithm.setWidth(width);
      }
    }
  }
  
  protected List<Lane> sortLanesByHorizontalOrder(List<Lane> lanes) {
    List<Lane> sortedLanes = new ArrayList<Lane>();
    for (Lane lane : lanes) {
      int index = -1;
      ContainerShape laneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(lane);
      for (int i = 0; i < sortedLanes.size(); i++) {
        Lane sortedLane = sortedLanes.get(i);
        ContainerShape sortedLaneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(sortedLane);
        if (sortedLaneShape.getGraphicsAlgorithm().getY() > laneShape.getGraphicsAlgorithm().getY()) {
          index = i;
          break;
        }
      }
      
      if (index == -1) {
        sortedLanes.add(lane);
      } else {
        sortedLanes.add(index, lane);
      }
    }
    return sortedLanes;
  }
  
  protected List<String> createLaneOrderedIdList(List<Lane> sortedLanes) {
    List<String> laneIds = new ArrayList<String>(sortedLanes.size());
    for (Lane lane : sortedLanes) {
      laneIds.add(lane.getId());
    }
    return laneIds;
  }
  
  protected void findAllAssociations(FlowElementsContainer container, List<Association> resultAssociations) {
    for (Artifact artifact : container.getArtifacts()) {
      if (artifact instanceof Association) {
        resultAssociations.add((Association) artifact);
      }
    }
    
    for (FlowElement flowElement : container.getFlowElements()) {
      if (flowElement instanceof SubProcess) {
        findAllAssociations((SubProcess) flowElement, resultAssociations);
      }
    }
  }
}
