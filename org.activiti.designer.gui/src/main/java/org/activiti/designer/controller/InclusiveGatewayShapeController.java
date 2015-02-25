package org.activiti.designer.controller;

import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link Task} objects.
 *  
 * @author Tijs Rademakers
 */
public class InclusiveGatewayShapeController extends AbstractBusinessObjectShapeController {
  
  public InclusiveGatewayShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof InclusiveGateway) {
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
    
    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    //width = width <= 0 ? 40 : width;
    //height = height <= 0 ? 40 : height;
    width = 40;
    height = 40;
    
    int xy[] = new int[] { 0, 20, 20, 0, 40, 20, 20, 40, 0, 20 };
    final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
    invisiblePolygon.setFilled(false);
    invisiblePolygon.setLineVisible(false);
    gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height);

    // create and set visible polygon inside invisible polygon
    Polygon polygon = gaService.createPolygon(invisiblePolygon, xy);
    polygon.setParentGraphicsAlgorithm(invisiblePolygon);
    polygon.setStyle(StyleUtil.getStyleForEvent(diagram));
    gaService.setLocationAndSize(polygon, 0, 0, width, height);

    // This draws the circle inside the rhombus
    
    // create and set the circle inside the polygon
    final Ellipse circle = gaService.createEllipse(polygon);
    circle.setStyle(StyleUtil.getStyleForEvent(diagram));
    circle.setLineWidth(3);
    circle.setParentGraphicsAlgorithm(polygon);
    gaService.setLocationAndSize(circle, 10, 10, 20, 20);
    
    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(0.51);
    boxAnchor.setRelativeHeight(0.10);
    boxAnchor.setReferencedGraphicsAlgorithm(polygon);
    Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    // add a another chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    final BoxRelativeAnchor boxAnchor2 = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor2.setRelativeWidth(0.51);
    boxAnchor2.setRelativeHeight(0.93);
    boxAnchor2.setReferencedGraphicsAlgorithm(polygon);
    ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor2, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    return containerShape;
  }
}
