package org.activiti.designer.controller;

import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
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
public class SubProcessShapeController extends AbstractBusinessObjectShapeController {
  
  public SubProcessShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof SubProcess && businessObject instanceof EventSubProcess == false) {
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
    final SubProcess addedSubProcess = (SubProcess) businessObject;
    
    // check whether the context has a size (e.g. from a create feature)
    // otherwise define a default size for the shape
    width = width <= 0 ? 205 : width;
    height = height <= 0 ? 205 : height;

    // create invisible outer rectangle expanded by
    // the width needed for the anchor
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width, height);

    // create and set visible rectangle inside invisible rectangle
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, 5, 5);
    roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    roundedRectangle.setStyle(StyleUtil.getStyleForEvent(diagram));
    gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

    // SHAPE WITH TEXT
    
    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final Text text = gaService.createDefaultText(diagram, shape, addedSubProcess.getName());
    text.setStyle(StyleUtil.getStyleForEvent(diagram));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    Font font = null;
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      font = gaService.manageFont(diagram, text.getFont().getName(), 11, false, true);
    } else {
      font = gaService.manageDefaultFont(diagram, false, true);
    }
    text.setFont(font);
    gaService.setLocationAndSize(text, 0, 0, width, 20);

    // provide information to support direct-editing directly
    // after object creation (must be activated additionally)
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(shape);
    directEditingInfo.setGraphicsAlgorithm(text);
    
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
}
