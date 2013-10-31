package org.activiti.designer.controller;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.designer.PluginImage;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link Task} objects.
 *  
 * @author Tijs Rademakers
 */
public class EventShapeController extends AbstractBusinessObjectShapeController {
  
  private static final int IMAGE_SIZE = 20;
  
  public EventShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof StartEvent || businessObject instanceof EndEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    final Event addedEvent = (Event) businessObject;
    
    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 35 ? 35 : width;
    height = height <= 35 ? 35 : height;

    final Ellipse invisibleCircle = gaService.createEllipse(containerShape);
    invisibleCircle.setFilled(false);
    invisibleCircle.setLineVisible(false);
    gaService.setLocationAndSize(invisibleCircle, context.getX(), context.getY(), width, height);

    // create and set visible circle inside invisible circle
    Ellipse circle = gaService.createEllipse(invisibleCircle);
    circle.setParentGraphicsAlgorithm(invisibleCircle);
    circle.setStyle(StyleUtil.getStyleForEvent(diagram));
    if (addedEvent instanceof EndEvent == true) {
      circle.setLineWidth(3);
    }
    gaService.setLocationAndSize(circle, 0, 0, width, height);

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    if (!(addedEvent instanceof EndEvent)) {

      // create an additional box relative anchor at middle-right
      final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
      boxAnchor.setRelativeWidth(1.0);
      boxAnchor.setRelativeHeight(0.51);
      boxAnchor.setReferencedGraphicsAlgorithm(circle);
      final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
      gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);
    }

    if (addedEvent.getEventDefinitions().size() > 0) {
      EventDefinition eventDefinition = addedEvent.getEventDefinitions().get(0);
      final Shape shape = peCreateService.createShape(containerShape, false);
      Image image = null;
      if (eventDefinition instanceof TimerEventDefinition) {
        image = gaService.createImage(shape, PluginImage.IMG_EVENT_TIMER.getImageKey());
      } else if (eventDefinition instanceof MessageEventDefinition) {
        image = gaService.createImage(shape, PluginImage.IMG_EVENT_MESSAGE.getImageKey());
      } else if (eventDefinition instanceof ErrorEventDefinition) {
        image = gaService.createImage(shape, PluginImage.IMG_EVENT_ERROR.getImageKey());
      } else if (eventDefinition instanceof SignalEventDefinition) {
        image = gaService.createImage(shape, PluginImage.IMG_EVENT_SIGNAL.getImageKey());
      }
      
      if (image != null) {
        image.setWidth(IMAGE_SIZE);
        image.setHeight(IMAGE_SIZE);
  
        gaService.setLocationAndSize(image, (width - IMAGE_SIZE) / 2, (height - IMAGE_SIZE) / 2, IMAGE_SIZE, IMAGE_SIZE);
      }
    }

    return containerShape;
  }
  
  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    // nothing to do
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    return false;
  }
}
