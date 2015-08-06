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

import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;


/**
 * @author Tijs Rademakers
 */
public class ActivityResizeFeature extends DefaultResizeShapeFeature {
  
  public ActivityResizeFeature(IFeatureProvider fp) {
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
      if (bo instanceof Task || bo instanceof CallActivity) {
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
    Shape boShape = context.getShape();
    int oldX = boShape.getGraphicsAlgorithm().getX();
    int oldY = boShape.getGraphicsAlgorithm().getY();
    int deltaHeight = height - boShape.getGraphicsAlgorithm().getHeight();
    
    super.resizeShape(context);
    
    int newX = boShape.getGraphicsAlgorithm().getX();
    int newY = boShape.getGraphicsAlgorithm().getY();
    
    Object bo = getBusinessObjectForPictogramElement(boShape);
    
    Activity activity = (Activity) bo;
    for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
      PictogramElement boundaryElement = getFeatureProvider().getPictogramElementForBusinessObject(boundaryEvent);
      boundaryElement.getGraphicsAlgorithm().setX(boundaryElement.getGraphicsAlgorithm().getX() + newX - oldX);
      if (newY - oldY == 0) {
        boundaryElement.getGraphicsAlgorithm().setY(boundaryElement.getGraphicsAlgorithm().getY() + deltaHeight);
      }
    }
    
    context.getShape().getGraphicsAlgorithm().setHeight(height);
    context.getShape().getGraphicsAlgorithm().setWidth(width);
    for (GraphicsAlgorithm graphicsAlgorithm : context.getShape().getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
      graphicsAlgorithm.setHeight(height);
      graphicsAlgorithm.setWidth(width);
    }
    
    List<Shape> shapes = ((ContainerShape) context.getShape()).getChildren();
		for (Shape shape : shapes) {
      if (shape.getGraphicsAlgorithm() != null) {
      	if(shape.getGraphicsAlgorithm() instanceof MultiText) {
	      	MultiText text = (MultiText) shape.getGraphicsAlgorithm();
	      	text.setHeight(height - 25);
	      	text.setWidth(width);
      	} else if (shape.getGraphicsAlgorithm() instanceof Image) {
      		Image image = (Image) shape.getGraphicsAlgorithm();
      		
      		if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey()) || 
      		        image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey())) {
      		  
      		  final int iconWidthAndHeight = 12;
            final int xPos = (context.getShape().getGraphicsAlgorithm().getWidth() / 2) - (iconWidthAndHeight / 2);
            final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight - 2;

            image.setX(xPos);
            image.setY(yPos);
            
      		} else if (image.getId().endsWith(PluginImage.IMG_ACTIVITY_COMPENSATION.getImageKey())) {
              
            final int iconWidthAndHeight = 12;
            final int xPos = (context.getShape().getGraphicsAlgorithm().getWidth() / 2) - (iconWidthAndHeight / 2) + iconWidthAndHeight + 5;
            final int yPos = context.getShape().getGraphicsAlgorithm().getHeight() - iconWidthAndHeight - 2;
  
            image.setX(xPos);
            image.setY(yPos);
          
          } else {
      		
        		int imageX = image.getX();
	      		if(imageX > 20) {
	      			image.setX(width - 20);
	      		}
          }
      		
      	} else if(shape.getGraphicsAlgorithm() instanceof Text) {
      		Text text = (Text) shape.getGraphicsAlgorithm();
	      	text.setHeight(height - 25);
	      	text.setWidth(width);
      	}
      }
    }
  }
}
