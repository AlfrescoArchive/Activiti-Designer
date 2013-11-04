package org.activiti.designer.kickstart.form.diagram.shape;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link NumberPropertyDefinition}
 * objects.
 * 
 * @author Frederik Heremans
 */
public class NumberPropertyShapeController extends SimpleIconInputShapeController {

  public NumberPropertyShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof NumberPropertyDefinition;
  }

  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    NumberPropertyDefinition propDef = (NumberPropertyDefinition) businessObject;

    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(propDef);
    if (!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }

  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }
  
  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    NumberPropertyDefinition propDef = (NumberPropertyDefinition) businessObject;
    
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
  protected boolean isQuickEditEnabled() {
    // TODO Auto-generated method stub
    return super.isQuickEditEnabled();
  }

  @Override
  protected String getIconKey(FormPropertyDefinition definition) {
    return KickstartFormPluginImage.NEW_NUMBER_INPUT.getImageKey();
  }
}
