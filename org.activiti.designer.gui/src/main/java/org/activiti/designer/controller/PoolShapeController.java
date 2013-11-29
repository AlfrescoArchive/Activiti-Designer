package org.activiti.designer.controller;

import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
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
public class PoolShapeController extends AbstractBusinessObjectShapeController {
  
  public PoolShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Pool) {
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
    final Pool addedPool = (Pool) context.getNewObject();

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 0 ? 500 : width;
    height = height <= 0 ? 150 : height;

    // create invisible outer rectangle expanded by
    // the width needed for the anchor
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width, height);

    // create and set visible rectangle inside invisible rectangle
    Rectangle rectangle = gaService.createRectangle(invisibleRectangle);
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    rectangle.setStyle(StyleUtil.getStyleForPool(diagram));
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);

    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final Text text = gaService.createDefaultText(diagram, shape, addedPool.getName());
    text.setStyle(StyleUtil.getStyleForEvent(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_MIDDLE);
    gaService.setLocationAndSize(text, 0, 0, 20, height);
    text.setAngle(-90);
    Font font = null;
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      font = gaService.manageFont(diagram, text.getFont().getName(), 11, false, true);
    } else {
      font = gaService.manageDefaultFont(diagram, false, true);
    }
    text.setFont(font);

    // create link and wire it
    getFeatureProvider().link(shape, addedPool);

    return containerShape;
  }
}
