package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.util.StepDefinitionStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.definition.AbstractNamedStepDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link AbstractNamedStepDefinition} objects, represented by a shape with a name and an icon (see {@link #getIcon()}.
 *  
 * @author Frederik Heremans
 */
public abstract class SimpleIconStepShapeController extends AbstractBusinessObjectShapeController {
  
  public SimpleIconStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    AbstractNamedStepDefinition definition = (AbstractNamedStepDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = StepDefinitionStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    height = StepDefinitionStyles.DEFAULT_LABEL_HEIGHT + StepDefinitionStyles.DEFAULT_COMPONENT_BOX_HEIGHT;

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, getLabelTextValue(definition));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getFeatureProvider().getDiagramTypeProvider().getDiagram(), text.getFont().getName(), 11));
    }
    gaService.setLocationAndSize(text, 0, 5, width, height - 5);
    
    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(StepDefinitionStyles.getStepDefinitionStyle(diagram));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);
    
    // Add icon
    Image icon = gaService.createImage(rectangle,getIcon().getImageKey());
    
    int iconSize = 16;
    int iconOffset = (height - iconSize) / 2 + 2;
    gaService.setLocationAndSize(icon, iconSize / 2, iconOffset, iconSize, iconSize);
    
    getFeatureProvider().link(containerShape, new Object[] {definition});
    
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
    AbstractNamedStepDefinition propDef = (AbstractNamedStepDefinition) businessObject;
    
    // Update the label
    MultiText labelText = findNameMultiText(shape);
    if(labelText != null) {
      labelText.setValue(getLabelTextValue(propDef));
    }
    
    // Check if width needs to be altered
    if(width > 0 && width != shape.getGraphicsAlgorithm().getWidth()) {
      IGaService gaService = Graphiti.getGaService();
      
      // Resize main shape rectangle
      Rectangle invisibleRectangle = (Rectangle) shape.getGraphicsAlgorithm();
      gaService.setWidth(invisibleRectangle, width);
      
      // Resize box shape (child of invisibleRectangle)
      GraphicsAlgorithm box = (GraphicsAlgorithm) invisibleRectangle.eContents().get(0);
      gaService.setWidth(box, width);
      
      // Resize label shape 
      Shape labelShape = shape.getChildren().get(0);
      gaService.setWidth(labelShape.getGraphicsAlgorithm(), width);
    }
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    AbstractNamedStepDefinition propDef = (AbstractNamedStepDefinition) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(propDef);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
  
  protected abstract KickstartProcessPluginImage getIcon();
}
