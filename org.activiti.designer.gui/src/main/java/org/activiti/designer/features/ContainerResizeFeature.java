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
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.PluginImage;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
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
    if(height < 55) {
      height = 55;
    }
    if(width < 105) {
      width = 105;
    }
    
    Shape shape = context.getShape();
    int oldX = shape.getGraphicsAlgorithm().getX();
    int oldY = shape.getGraphicsAlgorithm().getY();
    int deltaWidth = width - shape.getGraphicsAlgorithm().getWidth();
    int deltaHeight = height - shape.getGraphicsAlgorithm().getHeight();
    
    setSize(shape, width, height);
    
    Object bo = getBusinessObjectForPictogramElement(shape);
    if(bo instanceof Lane || bo instanceof Pool) {
      centerText((ContainerShape) shape);
    }
    
    if (bo instanceof Lane) {
      Lane lane = (Lane) bo;
      ContainerShape poolShape = shape.getContainer();
      setSize(poolShape, poolShape.getGraphicsAlgorithm().getWidth() + deltaWidth, 
              poolShape.getGraphicsAlgorithm().getHeight() + deltaHeight);
      centerText(poolShape);
      
      int laneY = shape.getGraphicsAlgorithm().getY();
      
      for (Lane otherLane : lane.getParentProcess().getLanes()) {
        
        if(lane.equals(otherLane)) continue;
        
        ContainerShape otherLaneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(otherLane);
        setSize(otherLaneShape, otherLaneShape.getGraphicsAlgorithm().getWidth() + deltaWidth, 
                otherLaneShape.getGraphicsAlgorithm().getHeight());
        
        centerText(otherLaneShape);
        
        if(laneY < otherLaneShape.getGraphicsAlgorithm().getY()) {
          otherLaneShape.getGraphicsAlgorithm().setY(otherLaneShape.getGraphicsAlgorithm().getY() + deltaHeight);
        }
      }
      
      
    } else if (bo instanceof Pool) {
      
      if(context.getProperty("org.activiti.designer.lane.create") == null) {
        BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
        Pool pool = (Pool) bo;
        Process process = model.getBpmnModel().getProcess(pool.getId());
        if (process != null) {
          int deltaLaneHeight = deltaHeight / process.getLanes().size();
          List<Lane> sortedLanes = sortLanesByHorizontalOrder(process.getLanes());
          int yShift = 0;
          for (Lane lane : sortedLanes) {
            ContainerShape laneShape = (ContainerShape) getFeatureProvider().getPictogramElementForBusinessObject(lane);
            setSize(laneShape, laneShape.getGraphicsAlgorithm().getWidth() + deltaWidth, 
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
    
    if (bo instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) bo;
      for (BoundaryEvent boundaryEvent : subProcess.getBoundaryEvents()) {
        PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
        boundaryElement.getGraphicsAlgorithm().setX(boundaryElement.getGraphicsAlgorithm().getX() + newX - oldX);
        if (newY - oldY == 0) {
          boundaryElement.getGraphicsAlgorithm().setY(boundaryElement.getGraphicsAlgorithm().getY() + deltaHeight);
        }
      }
      for (FlowElement flowElement : subProcess.getFlowElements()) {
        if (flowElement instanceof Activity) {
          Activity activity = (Activity) flowElement;
          for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
            if (oldX != newX) {
              PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
              boundaryElement.getGraphicsAlgorithm().setX(boundaryElement.getGraphicsAlgorithm().getX() + newX - oldX);
            }
            
            if (oldY != newY) {
              PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
              boundaryElement.getGraphicsAlgorithm().setY(boundaryElement.getGraphicsAlgorithm().getY() + newY - oldY);
            }
          }
        }
      }
      
      List<Shape> childShapes = ((ContainerShape) context.getShape()).getChildren();
      for (Shape childShape : childShapes) {
        if (childShape.getGraphicsAlgorithm() != null) {
          if (childShape.getGraphicsAlgorithm() instanceof Image) {
            Image image = (Image) childShape.getGraphicsAlgorithm();
            
            if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey()) || 
                    image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey())) {
              
              final int iconWidthAndHeight = 12;
              final int xPos = (context.getShape().getGraphicsAlgorithm().getWidth() / 2) - (iconWidthAndHeight / 2);
              final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight;

              image.setX(xPos);
              image.setY(yPos);
            } 
          }
        }
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
  
  protected void setSize(Shape shape, int width, int height) {
    shape.getGraphicsAlgorithm().setHeight(height);
    shape.getGraphicsAlgorithm().setWidth(width);
    for (GraphicsAlgorithm graphicsAlgorithm : shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
      graphicsAlgorithm.setHeight(height);
      graphicsAlgorithm.setWidth(width);
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
        if(sortedLaneShape.getGraphicsAlgorithm().getY() > laneShape.getGraphicsAlgorithm().getY()) {
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
}
