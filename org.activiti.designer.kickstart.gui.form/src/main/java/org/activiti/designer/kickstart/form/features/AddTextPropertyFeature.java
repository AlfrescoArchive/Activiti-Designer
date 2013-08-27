package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * @author Frederik Heremans
 */
public class AddTextPropertyFeature extends AbstractAddFormComponentFeature {

  protected static final int DEFAULT_COMPONENT_WIDTH = 500;
  protected static final int DEFAULT_LABEL_WIDTH = 150;
  
  public AddTextPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof TextPropertyDefinition;
  }
  
  @Override
  protected ContainerShape createContainerShape(Object newObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    TextPropertyDefinition definition = (TextPropertyDefinition) newObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = DEFAULT_COMPONENT_WIDTH;
    }
    if(height < 0) {
      height = 25;
    }

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(FormComponentStyles.getInputFieldStyle(getDiagram()));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, DEFAULT_LABEL_WIDTH, 0, width - DEFAULT_LABEL_WIDTH, height);
    
    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(getDiagram(), shape, definition.getName());
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 11));
    }
    
    gaService.setLocationAndSize(text, 0, 0, width - DEFAULT_LABEL_WIDTH, 25);
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width - DEFAULT_LABEL_WIDTH, 25);
    
    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(shape);
    directEditingInfo.setGraphicsAlgorithm(text);

    // Create link and wire it
    link(containerShape, newObject);
    return containerShape;
  }

}
