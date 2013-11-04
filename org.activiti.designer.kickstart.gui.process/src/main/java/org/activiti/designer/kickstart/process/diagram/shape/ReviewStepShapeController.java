package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.util.StepDefinitionStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link AlfrescoReviewStepDefinition} objects.
 *  
 * @author Frederik Heremans
 */
public class ReviewStepShapeController extends AbstractBusinessObjectShapeController {
  
  public ReviewStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof AlfrescoReviewStepDefinition;
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    AlfrescoReviewStepDefinition definition = (AlfrescoReviewStepDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = StepDefinitionStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    height = StepDefinitionStyles.DEFAULT_LABEL_HEIGHT + StepDefinitionStyles.DEFAULT_PARALLEL_BOX_HEIGHT;

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setForeground(StepDefinitionStyles.getDefaultForegroundColor(diagram));
    rectangle.setBackground(StepDefinitionStyles.getDefaultBackgroundColor(diagram));
    rectangle.setLineWidth(1);
    rectangle.setFilled(true);
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);
    
    Rectangle childContainerRect = gaService.createRectangle(rectangle);
    childContainerRect.setForeground(StepDefinitionStyles.getSubtleForegroundColor(diagram));
    childContainerRect.setBackground(StepDefinitionStyles.getSubtleBackgroundColor(diagram));
    childContainerRect.setLineWidth(1);
    childContainerRect.setFilled(true);
    childContainerRect.setLineStyle(LineStyle.DASH);
    childContainerRect.setParentGraphicsAlgorithm(rectangle);
    gaService.setLocationAndSize(childContainerRect, 10, 30, width - 20, height - 40);
    
    // Add rejection label
    final MultiText rejectText = gaService.createDefaultMultiText(diagram, childContainerRect, "Rejected");
    rejectText.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    rejectText.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      rejectText.setFont(gaService.manageFont(getFeatureProvider().getDiagramTypeProvider().getDiagram(), rejectText.getFont().getName(), 11));
    }
    gaService.setLocationAndSize(rejectText, 0, 5, width - 20, StepDefinitionStyles.DEFAULT_LABEL_HEIGHT);
    
    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, getLabelTextValue(definition));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getFeatureProvider().getDiagramTypeProvider().getDiagram(), text.getFont().getName(), 11));
    }
    gaService.setLocationAndSize(text, 0, 5, width, StepDefinitionStyles.DEFAULT_LABEL_HEIGHT);
    
    // Add "process end" marker
    Rectangle endProcessMarker = gaService.createRectangle(childContainerRect);
    endProcessMarker.setLineVisible(false);
    endProcessMarker.setFilled(true);
    endProcessMarker.setBackground(StepDefinitionStyles.getSevereBackgroundColor(diagram));
    
    gaService.setLocationAndSize(endProcessMarker, 0, height - 45, width - 10, 5);
    
    getFeatureProvider().link(containerShape, new Object[] {definition});
    
    // Check if the shape has any children. If so, create shapes for them as well
    if(definition.getSteps() != null && !definition.getSteps().isEmpty()) {
      AddContext addContext = null;
      AreaContext areaContext = new AreaContext();
      areaContext.setX(-1);
      areaContext.setY(-1);
      
      StepDefinition child = null;
      for(int i = definition.getSteps().size() -1; i>=0; i--) {
        child = definition.getSteps().get(i);
        addContext = new AddContext(areaContext, child);
        addContext.setTargetContainer(containerShape);
        featureProvider.getAddFeature(addContext).add(addContext);
      }
    }
    
    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(containerShape);
    directEditingInfo.setGraphicsAlgorithm(text);
    directEditingInfo.setActive(true);
    return containerShape;
  }

  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    AlfrescoReviewStepDefinition step = (AlfrescoReviewStepDefinition) businessObject;
    // Update the label
    MultiText labelText = findNameMultiText(shape);
    if(labelText != null) {
      labelText.setValue(getLabelTextValue(step));
    }
    
    GraphicsAlgorithm endMarker = getEndProcessMarker(shape);
    
    if(step.isEndProcessOnReject() != endMarker.getFilled()) {
      endMarker.setFilled(step.isEndProcessOnReject());
    }
    
    boolean updateWidth = (width > 0 && width != shape.getGraphicsAlgorithm().getWidth());
    boolean updateHeight = (height > 0 && height != shape.getGraphicsAlgorithm().getHeight());
    // Check if width needs to be altered
    if(updateHeight || updateWidth) {
      IGaService gaService = Graphiti.getGaService();
      
      // Resize main shape rectangle and box shape
      Rectangle invisibleRectangle = (Rectangle) shape.getGraphicsAlgorithm();
      GraphicsAlgorithm box = (GraphicsAlgorithm) invisibleRectangle.eContents().get(0);
      GraphicsAlgorithm boxChild = (GraphicsAlgorithm) box.eContents().get(0);
      
      if(updateWidth) {
        Shape labelShape = shape.getChildren().get(0);
        GraphicsAlgorithm boxLabelShape = (GraphicsAlgorithm) boxChild.eContents().get(0);
        
        gaService.setWidth(invisibleRectangle, width);
        gaService.setWidth(box, width);
        gaService.setWidth(boxChild, width - 20);
        gaService.setWidth(labelShape.getGraphicsAlgorithm(), width - 20);
        gaService.setWidth(boxLabelShape, width - 40);
        gaService.setWidth(endMarker, width - 20);
      }
      
      if(updateHeight) {
        gaService.setHeight(invisibleRectangle, height);
        gaService.setHeight(box, height);
        gaService.setHeight(boxChild, height - 40);
        gaService.setLocation(endMarker, 0, height - 45);
      }
    }
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    AlfrescoReviewStepDefinition step = (AlfrescoReviewStepDefinition) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(step);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    
    // Check end-marker
    GraphicsAlgorithm endMarker = getEndProcessMarker(shape);
    if(endMarker != null) {
      return step.isEndProcessOnReject() != endMarker.getFilled();
    }
    return false;
  }
  
  protected GraphicsAlgorithm getEndProcessMarker(ContainerShape shape) {
    return (GraphicsAlgorithm) shape.getGraphicsAlgorithm().eContents().get(0).eContents().get(0).eContents().get(1);
  }
  
  protected String getLabelTextValue(AlfrescoReviewStepDefinition definition) {
    if(definition.getName() != null) {
      return definition.getName();
    } else {
      return "Nameless review";
    }
  }
}
