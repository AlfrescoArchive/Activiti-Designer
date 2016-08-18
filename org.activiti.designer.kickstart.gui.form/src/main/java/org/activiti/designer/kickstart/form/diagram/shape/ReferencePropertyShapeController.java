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

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link ReferencePropertyDefinition} objects, which represent a reference to an existing field.
 *  
 * @author Frederik Heremans
 */
public class ReferencePropertyShapeController extends SimpleIconInputShapeController {
  
  public ReferencePropertyShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    boolean valid =  businessObject instanceof ReferencePropertyDefinition;
    if(valid) {
      ReferencePropertyDefinition ref = (ReferencePropertyDefinition) businessObject;
      valid = AlfrescoConversionConstants.FORM_REFERENCE_FIELD.equals(ref.getType())
          || AlfrescoConversionConstants.CONTENT_TYPE_PEOPLE.equals(ref.getType())
          || AlfrescoConversionConstants.CONTENT_TYPE_GROUP.equals(ref.getType())
          || AlfrescoConversionConstants.CONTENT_TYPE_CONTENT.equals(ref.getType());
    }
    return valid;
  }

  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    return container.getChildren().get(0).getGraphicsAlgorithm();
  }

  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    FormPropertyDefinition propDef = (FormPropertyDefinition) businessObject;
    
    // Check label text
    String currentLabel = (String) extractShapeData(LABEL_DATA_KEY, shape);
    String newLabel = getLabelTextValue(propDef);
    if(!StringUtils.equals(currentLabel, newLabel)) {
      return true;
    }
    return false;
  }
  
  @Override
  public void updateShape(ContainerShape shape, Object businessObject, int width, int height) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    
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

  protected boolean isQuickEditEnabled() {
    return true;
  }

  @Override
  protected String getIconKey(FormPropertyDefinition definition) {
    ReferencePropertyDefinition ref = (ReferencePropertyDefinition) definition;
    if(AlfrescoConversionConstants.CONTENT_TYPE_PEOPLE.equals(ref.getType())) {
      return KickstartFormPluginImage.NEW_PEOPLE_SELECT.getImageKey();
    } else if(AlfrescoConversionConstants.CONTENT_TYPE_GROUP.equals(ref.getType())) {
      return KickstartFormPluginImage.NEW_GROUP_SELECT.getImageKey();
    } else if(AlfrescoConversionConstants.CONTENT_TYPE_CONTENT.equals(ref.getType())) {
      return KickstartFormPluginImage.NEW_PACKAGE_ITEMS.getImageKey();
    } else {
      return KickstartFormPluginImage.NEW_FIELD_REFERENCE.getImageKey();
    }
  }
}
