package com.alfresco.designer.gui.controller;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.PluginImage;
import org.activiti.designer.controller.AbstractBusinessObjectShapeController;
import org.activiti.designer.controller.BusinessObjectShapeController;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
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
public class AlfrescoStartEventShapeController extends AbstractBusinessObjectShapeController {
  
  private static final int IMAGE_SIZE = 16;
  
  public AlfrescoStartEventShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof AlfrescoStartEvent) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context) {
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
    // create an additional box relative anchor at middle-right
    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(1.0);
    boxAnchor.setRelativeHeight(0.51);
    boxAnchor.setReferencedGraphicsAlgorithm(circle);
    final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    Shape shape = peCreateService.createShape(containerShape, false);
    Image image = gaService.createImage(shape, PluginImage.IMG_ALFRESCO_LOGO.getImageKey());
    gaService.setLocationAndSize(image, 10, 10, IMAGE_SIZE, IMAGE_SIZE);

    return containerShape;
  }
}
