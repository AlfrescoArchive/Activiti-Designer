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

import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
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
        Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
        Pool pool = (Pool) bo;
        Process process = model.getProcess(pool.getId());
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
  }
  
  private void centerText(ContainerShape shape) {
    for (Shape shapeChild : shape.getChildren()) {
      if (shapeChild.getGraphicsAlgorithm() instanceof Text) {
        Text text = (Text) shapeChild.getGraphicsAlgorithm();
        Graphiti.getGaService().setLocationAndSize(text, 0, 0, 20, shape.getGraphicsAlgorithm().getHeight());
      }
    }
  }
  
  private void setSize(Shape shape, int width, int height) {
    shape.getGraphicsAlgorithm().setHeight(height);
    shape.getGraphicsAlgorithm().setWidth(width);
    for (GraphicsAlgorithm graphicsAlgorithm : shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
      graphicsAlgorithm.setHeight(height);
      graphicsAlgorithm.setWidth(width);
    }
  }
  
  private List<Lane> sortLanesByHorizontalOrder(List<Lane> lanes) {
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
