package org.activiti.designer.kickstart.form.diagram.shape;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
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
 * {@link TextPropertyDefinition} objects.
 *  
 * @author Frederik Heremans
 */
public class TextPropertyShapeController extends AbstractBusinessObjectShapeController {
  
  public TextPropertyShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof TextPropertyDefinition;
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    TextPropertyDefinition definition = (TextPropertyDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = FormComponentStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    // Multi-line cannot be altered through properties
    if(definition.isMultiline()) {
      height = FormComponentStyles.DEFAULT_LABEL_HEIGHT + FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT * 2;
    } else {
      height = FormComponentStyles.DEFAULT_LABEL_HEIGHT + FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT;
    }

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(FormComponentStyles.getInputFieldStyle(diagram));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    gaService.setLocationAndSize(rectangle, 0, FormComponentStyles.DEFAULT_LABEL_HEIGHT, width, height - FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    
    // Add label
    final Shape shape = peCreateService.createShape(containerShape, false);
    final MultiText text = gaService.createDefaultMultiText(diagram, shape, getLabelTextValue(definition));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getFeatureProvider().getDiagramTypeProvider().getDiagram(), text.getFont().getName(), 11));
    }
    
    gaService.setLocationAndSize(text, 0, 0, width, FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width, FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    
    if(definition.isMultiline()) {
      Shape textareaDecorationShape = peCreateService.createShape(containerShape, false);
      RoundedRectangle polygon = gaService.createRoundedRectangle(textareaDecorationShape, FormComponentStyles.DEFAULT_SCROLLBAR_DECORATION_WIDTH, FormComponentStyles.DEFAULT_SCROLLBAR_DECORATION_WIDTH);
      gaService.setLocationAndSize(polygon, width - 2 - FormComponentStyles.DEFAULT_SCROLLBAR_DECORATION_WIDTH, FormComponentStyles.DEFAULT_LABEL_HEIGHT + 2, FormComponentStyles.DEFAULT_SCROLLBAR_DECORATION_WIDTH, height - FormComponentStyles.DEFAULT_LABEL_HEIGHT - 5);
      polygon.setBackground(FormComponentStyles.getFieldDecorationColor(diagram));
      polygon.setForeground(null);
      polygon.setLineVisible(false);
    }
    
    // Allow quick-edit
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(containerShape);
    directEditingInfo.setGraphicsAlgorithm(text);
    
    return containerShape;
  }

  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    TextPropertyDefinition propDef = (TextPropertyDefinition) businessObject;
    
    // Update the label
    MultiText labelText = findNameMultiText(shape);
    if(labelText != null) {
      labelText.setValue(getLabelTextValue(propDef));
    }
    
    // Check if width needs to be altered
    if(width != shape.getGraphicsAlgorithm().getWidth()) {
      // TODO: implement
    }
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    TextPropertyDefinition propDef = (TextPropertyDefinition) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(propDef);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
}
