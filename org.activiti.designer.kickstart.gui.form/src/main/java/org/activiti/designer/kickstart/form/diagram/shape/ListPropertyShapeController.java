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
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link ListPropertyDefinition} objects.
 *  
 * @author Frederik Heremans
 */
public class ListPropertyShapeController extends AbstractBusinessObjectShapeController {
  
  public ListPropertyShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof ListPropertyDefinition;
  }

  @Override
  public ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(layoutParent, true);
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    ListPropertyDefinition definition = (ListPropertyDefinition) businessObject;

    // If no size has been supplied, revert to the default sizes
    if(width < 0) {
      width = FormComponentStyles.DEFAULT_COMPONENT_WIDTH;
    }
    
    height = FormComponentStyles.DEFAULT_LABEL_HEIGHT + FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT;

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
      text.setFont(gaService.manageFont(diagram, text.getFont().getName(), 11));
    }
    
    gaService.setLocationAndSize(text, 0, 0, width, FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    gaService.setLocationAndSize(shape.getGraphicsAlgorithm(), 0, 0, width, FormComponentStyles.DEFAULT_LABEL_HEIGHT);
    
    // Add default-text
    Shape innerTextShape = peCreateService.createShape(containerShape, false);
    Text innerText = gaService.createPlainText(innerTextShape, getDefaultValue(definition));
    innerText.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    innerText.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
    innerText.setFilled(false);
    innerText.setLineWidth(0);
    gaService.setLocationAndSize(innerText, 3, FormComponentStyles.DEFAULT_LABEL_HEIGHT + 1, width - 4, height - FormComponentStyles.DEFAULT_LABEL_HEIGHT - 2);

    // Add combo decoration
    Shape decoration = peCreateService.createShape(containerShape, false);
    Rectangle decorationRectangle = gaService.createInvisibleRectangle(decoration);
    gaService.setLocationAndSize(decorationRectangle, width - 25, FormComponentStyles.DEFAULT_LABEL_HEIGHT, 30, FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT);
    
    Polygon seperator = gaService.createPolygon(decorationRectangle, new int[] {0, 0, 0, FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT});
    seperator.setLineWidth(0);
    seperator.setBackground(FormComponentStyles.getDefaultForegroundColor(diagram));
    
    
    int caretY = (FormComponentStyles.DEFAULT_COMPONENT_BOX_HEIGHT - 7) / 2;
    Polygon caret = gaService.createPolygon(decorationRectangle, new int[] {18, caretY, 8, caretY, 13, caretY + 7});
    caret.setLineVisible(false);
    caret.setForeground(FormComponentStyles.getDefaultForegroundColor(diagram));
    
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
    ListPropertyDefinition propDef = (ListPropertyDefinition) businessObject;

    // Update the label
    MultiText labelText = findNameMultiText(shape);
    if(labelText != null) {
      labelText.setValue(getLabelTextValue(propDef));
    }
    
    // Update default text
    Text defaultText = findFieldText(shape);
    if(defaultText != null) {
      defaultText.setValue(getDefaultValue(propDef));
    }
    
    // Check if width needs to be altered
    if(width > 0 && width != shape.getGraphicsAlgorithm().getWidth()) {
      int widthDiv = width - shape.getGraphicsAlgorithm().getWidth();
      
      // Check if width needs to be altered
      if(width != shape.getGraphicsAlgorithm().getWidth()) {
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
        
        // Resize default value shape
        Shape defaultShape = shape.getChildren().get(1);
        gaService.setWidth(defaultShape.getGraphicsAlgorithm(), width);
        
        // Also move the calendar decorations, relative to right corner
        for(int i = 2; i < shape.getChildren().size(); i++) {
          GraphicsAlgorithm decoration = shape.getChildren().get(i).getGraphicsAlgorithm();
          gaService.setLocation(decoration,  decoration.getX() + widthDiv, decoration.getY()); 
        }
      }
    }
  }

  protected String getDefaultValue(ListPropertyDefinition definition) {
    String value = null;
    if(definition.getEntries() != null && !definition.getEntries().isEmpty()) {
      value = definition.getEntries().get(0).getName();
    }
    
    if(value == null || value.isEmpty()) {
      value = "Select a value...";
    }
    return value;
  }
  
  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    ListPropertyDefinition propDef = (ListPropertyDefinition) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(propDef);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    
    // Check default value
    String currentDefault = (String) extractShapeData(DEFAULT_VALUE_DATA_KEY, shape);
    String newDefault = getDefaultValue(propDef);
    if(!StringUtils.equals(currentDefault, newDefault)) {
      return true;
    }
    return false;
  }
  
  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }

}
