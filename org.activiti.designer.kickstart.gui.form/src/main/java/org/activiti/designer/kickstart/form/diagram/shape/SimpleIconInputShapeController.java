/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.form.diagram.shape;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.designer.kickstart.form.util.FormComponentStyles;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
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
 * {@link FormPropertyDefinition} objects, represented as a simple field with an icon in it.
 *  
 * @author Frederik Heremans
 */
public abstract class SimpleIconInputShapeController extends AbstractBusinessObjectShapeController {
  
  public SimpleIconInputShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public final ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    FormPropertyDefinition definition = (FormPropertyDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = FormComponentStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    height = FormComponentStyles.DEFAULT_LABEL_HEIGHT + FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT;

    RoundedRectangle rectangle; 
    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, 0, 0, width, height);

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
    
    // Create and set visible rectangle inside invisible rectangle
    rectangle = gaService.createRoundedRectangle(invisibleRectangle, 2, 2);
    rectangle.setStyle(FormComponentStyles.getFixedElementStyle(diagram));
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
    rectangle.setLineStyle(LineStyle.DASH);
    gaService.setLocationAndSize(rectangle, 0, FormComponentStyles.DEFAULT_LABEL_HEIGHT, width, height - FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    
    // Add icon
    Image icon = gaService.createImage(rectangle, getIconKey(definition));
    
    int iconSize = 16;
    int iconOffset = (height - FormComponentStyles.DEFAULT_GROUP_LABEL_HEIGHT - iconSize) / 2 + 2;
    gaService.setLocationAndSize(icon, 2, iconOffset, iconSize, iconSize);
    
    getFeatureProvider().link(containerShape, new Object[] {definition});
    
    if(isQuickEditEnabled()) {
      // Allow quick-edit
      final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
      // set container shape for direct editing after object creation
      directEditingInfo.setMainPictogramElement(containerShape);
      // set shape and graphics algorithm where the editor for
      // direct editing shall be opened after object creation
      directEditingInfo.setPictogramElement(containerShape);
      directEditingInfo.setGraphicsAlgorithm(text);
      directEditingInfo.setActive(true);
    }
    return containerShape;
  }

  protected boolean isQuickEditEnabled() {
    return false;
  }

  /**
   * @param definition
   * @return key of the image to use to display as an icon in the input-field. The image is displayed
   * as a 16x16 image.
   */
  protected abstract String getIconKey(FormPropertyDefinition definition);

  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
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
}