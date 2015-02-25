package org.activiti.designer.controller;

import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
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
public class ExclusiveGatewayShapeController extends AbstractBusinessObjectShapeController {
  
  public ExclusiveGatewayShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof ExclusiveGateway) {
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
    width = 40;
    height = 40;
    //width = width <= 0 ? 40 : width;
    //height = height <= 0 ? 40 : height;
    
    int xy[] = new int[] { 0, 20, 20, 0, 40, 20, 20, 40, 0, 20 };
    final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
    invisiblePolygon.setFilled(false);
    invisiblePolygon.setLineVisible(false);
    gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height);

    // create and set visible circle inside invisible circle
    Polygon polygon = gaService.createPolygon(invisiblePolygon, xy);
    polygon.setParentGraphicsAlgorithm(invisiblePolygon);
    polygon.setStyle(StyleUtil.getStyleForEvent(diagram));
    gaService.setLocationAndSize(polygon, 0, 0, width, height);
    
    Shape shape = peCreateService.createShape(containerShape, false);
      
    Polyline polyline = gaService.createPolyline(shape, new int[] { width - 10, 10, 10, height - 10 });
    polyline.setLineWidth(5);
    polyline.setStyle(StyleUtil.getStyleForEvent(diagram));
    
    shape = peCreateService.createShape(containerShape, false);
    polyline = gaService.createPolyline(shape, new int[] { 10, 10, width - 10, height - 10});
    polyline.setLineWidth(5);
    polyline.setStyle(StyleUtil.getStyleForEvent(diagram));

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(0.51);
    boxAnchor.setRelativeHeight(0.10);
    boxAnchor.setReferencedGraphicsAlgorithm(polygon);
    Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    // add a another chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);
    boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(0.51);
    boxAnchor.setRelativeHeight(0.93);
    boxAnchor.setReferencedGraphicsAlgorithm(polygon);
    ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    return containerShape;
  }
}
