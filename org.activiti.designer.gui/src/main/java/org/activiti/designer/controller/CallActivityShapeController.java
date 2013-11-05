package org.activiti.designer.controller;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.PluginImage;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
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
public class CallActivityShapeController extends AbstractBusinessObjectShapeController {
  
  public CallActivityShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof CallActivity) {
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
    
    final CallActivity addedCallActivity = (CallActivity) context.getNewObject();

    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 0 ? 105 : width;
    height = height <= 0 ? 55 : height;

    // create invisible outer rectangle expanded by
    // the width needed for the anchor
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width, height);

    // create and set visible rectangle inside invisible rectangle
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, 5, 5);
    roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    roundedRectangle.setStyle(StyleUtil.getStyleForTask(diagram));
    roundedRectangle.setLineWidth(3);
    gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

    // create shape for text
    Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, addedCallActivity.getName());
    text.setStyle(StyleUtil.getStyleForTask(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    Font font = null;
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      font = gaService.manageFont(diagram, text.getFont().getName(), 11, false, true);
    } else {
      font = gaService.manageDefaultFont(diagram, false, true);
    }
    text.setFont(font);

    gaService.setLocationAndSize(text, 0, 20, width, 30);

    // create link and wire it
    getFeatureProvider().link(shape, addedCallActivity);

    // provide information to support direct-editing directly
    // after object creation (must be activated additionally)
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(shape);
    directEditingInfo.setGraphicsAlgorithm(text);
    
    shape = peCreateService.createShape(containerShape, false);
    Image image = gaService.createImage(shape, PluginImage.IMG_SUBPROCESS_COLLAPSED.getImageKey());

    // calculate position for icon
    final int iconWidthAndHeight = 10;
    final int padding = 5;
    final int xPos = (roundedRectangle.getWidth() / 2) - (iconWidthAndHeight / 2);
    final int yPos = roundedRectangle.getHeight() - padding - iconWidthAndHeight;

    gaService.setLocationAndSize(image, xPos, yPos, iconWidthAndHeight, iconWidthAndHeight);

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);

    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(1.0);
    boxAnchor.setRelativeHeight(0.51);
    boxAnchor.setReferencedGraphicsAlgorithm(roundedRectangle);
    final Ellipse ellipse = ActivitiUiUtil.createInvisibleEllipse(boxAnchor, gaService);
    gaService.setLocationAndSize(ellipse, 0, 0, 0, 0);

    return containerShape;
  }
  
  @Override
  public void updateShape(PictogramElement element, Object businessObject, int width, int height) {
    CallActivity task = (CallActivity) businessObject;
    
    // Update the label
    MultiText labelText = findNameMultiText(element);
    if (labelText != null) {
      labelText.setValue(getLabelTextValue(task));
    }
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(PictogramElement element) {
    return ((ContainerShape) element).getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public boolean isShapeUpdateNeeded(PictogramElement element, Object businessObject) {
    CallActivity task = (CallActivity) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, element);
    String newLabel = getLabelTextValue(task);
    if (!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
}
