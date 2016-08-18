/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.controller;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CancelEventDefinition;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.designer.PluginImage;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link Task} objects.
 *  
 * @author Tijs Rademakers
 */
public class BoundaryEventShapeController extends AbstractBusinessObjectShapeController {
  
  private static final int IMAGE_SIZE = 20;
  private static final int EVENT_SIZE = 30;
  
  public BoundaryEventShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof BoundaryEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    final BoundaryEvent addedEvent = (BoundaryEvent) context.getNewObject();
    ContainerShape parent = context.getTargetContainer();
    int x = context.getX();
    int y = context.getY();

    ILocation shapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(parent);
    x += shapeLocation.getX();
    y += shapeLocation.getY();
    
    parent = diagram;
    
    final ContainerShape containerShape = peCreateService.createContainerShape(parent, true);

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 0 ? EVENT_SIZE : width;
    height = height <= 0 ? EVENT_SIZE : height;

    final Ellipse invisibleCircle = gaService.createEllipse(containerShape);
    invisibleCircle.setFilled(false);
    invisibleCircle.setLineVisible(false);
    gaService.setLocationAndSize(invisibleCircle, x, y, width, height);

    // create and set visible circle inside invisible circle
    Ellipse circle = gaService.createEllipse(invisibleCircle);
    circle.setParentGraphicsAlgorithm(invisibleCircle);
    circle.setStyle(StyleUtil.getStyleForEvent(diagram));
    if (addedEvent.isCancelActivity() == false) {
      circle.setLineStyle(LineStyle.DOT);
    }
    gaService.setLocationAndSize(circle, 0, 0, width, height);

    Ellipse secondCircle = gaService.createEllipse(circle);
    secondCircle.setParentGraphicsAlgorithm(circle);
    secondCircle.setStyle(StyleUtil.getStyleForEvent(diagram));
    if (addedEvent.isCancelActivity() == false) {
      secondCircle.setLineStyle(LineStyle.DOT);
    }
    gaService.setLocationAndSize(secondCircle, 3, 3, width - 6, height - 6);
    
    String imageKey = getImageKey(addedEvent);
    if (imageKey != null) {
      final Shape shape = peCreateService.createShape(containerShape, false);
      final Image image = gaService.createImage(shape, imageKey);
      image.setWidth(IMAGE_SIZE);
      image.setHeight(IMAGE_SIZE);
      gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
    }

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);

    return containerShape;
  }
  
  protected String getImageKey(BoundaryEvent event) {
    String imageKey = null;
    if (event.getEventDefinitions().size() > 0) {
      EventDefinition eventDefinition = event.getEventDefinitions().get(0);
      if (eventDefinition instanceof TimerEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_TIMER.getImageKey();
      } else if (eventDefinition instanceof MessageEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_MESSAGE.getImageKey();
      } else if (eventDefinition instanceof ErrorEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_ERROR.getImageKey();
      } else if (eventDefinition instanceof SignalEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_SIGNAL.getImageKey();
      } else if (eventDefinition instanceof CancelEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_CANCEL.getImageKey();
      } else if (eventDefinition instanceof CompensateEventDefinition) {
        imageKey = PluginImage.IMG_EVENT_COMPENSATION.getImageKey();
      }
    }
    return imageKey;
  }
}
