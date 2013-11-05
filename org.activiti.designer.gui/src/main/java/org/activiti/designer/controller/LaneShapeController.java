package org.activiti.designer.controller;

import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
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
public class LaneShapeController extends AbstractBusinessObjectShapeController {
  
  public LaneShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Lane) {
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
    final Lane addedLane = (Lane) context.getNewObject();

    int x = context.getX();
    int y = context.getY();

    // create invisible outer rectangle expanded by
    // the width needed for the anchor
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, x, y, width, height);

    // create and set visible rectangle inside invisible rectangle
    Rectangle rectangle = gaService.createRectangle(invisibleRectangle);
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    rectangle.setStyle(StyleUtil.getStyleForPool(diagram));
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);

    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final Text text = gaService.createDefaultText(diagram, shape, addedLane.getName());
    text.setStyle(StyleUtil.getStyleForEvent(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
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
    getFeatureProvider().link(shape, addedLane);

    return containerShape;
  }
  
  @Override
  public void updateShape(PictogramElement element, Object businessObject, int width, int height) {
    Lane lane = (Lane) businessObject;
    
    // Update the label
    MultiText labelText = findNameMultiText(element);
    if (labelText != null) {
      labelText.setValue(getLabelTextValue(lane));
    }
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(PictogramElement element) {
    return ((ContainerShape) element).getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public boolean isShapeUpdateNeeded(PictogramElement element, Object businessObject) {
    Lane lane = (Lane) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, element);
    String newLabel = getLabelTextValue(lane);
    if (!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
}
