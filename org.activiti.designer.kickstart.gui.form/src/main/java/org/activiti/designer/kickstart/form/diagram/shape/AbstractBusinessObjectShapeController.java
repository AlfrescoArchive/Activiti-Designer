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
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Base class to use when creating {@link BusinessObjectShapeController}, exposes some
 * utility methods for locating child shapes. Also exposes {@link #LABEL_DATA_KEY} as
 * shape data, returning the first {@link MultiText} value found in the child hierarchy and
 *  {@link #DEFAULT_VALUE_DATA_KEY}, returning first {@link Text} value implementation.
 * 
 * @author Frederik Heremans
 */
public abstract class AbstractBusinessObjectShapeController implements BusinessObjectShapeController {

  public static final String LABEL_DATA_KEY = "label";
  public static final String DEFAULT_VALUE_DATA_KEY = "default";

  protected KickstartFormFeatureProvider featureProvider;

  public AbstractBusinessObjectShapeController(KickstartFormFeatureProvider featureProvider) {
    this.featureProvider = featureProvider;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>Supported keys: {@value #LABEL_DATA_KEY}, {@value #DEFAULT_VALUE_DATA_KEY}</p>
   */
  @Override
  public Object extractShapeData(String key, Shape shape) {
    Object data = null;
    
    if(LABEL_DATA_KEY.equals(key)) {
      // Extract the label-value from the Multi-text field
      MultiText labeltext = findNameMultiText(shape);
      if(labeltext != null) {
        data = labeltext.getValue();
      }
    } else if(DEFAULT_VALUE_DATA_KEY.equals(key)) {
      Text defaultText = findFieldText(shape);
      if(defaultText != null) {
        data = defaultText.getValue();
      }
    }
    return data;
  }
  
  protected KickstartFormFeatureProvider getFeatureProvider() {
    return featureProvider;
  }
  
  /**
   * @return the first {@link MultiText} that is encountered in the child-hierarchy
   * of the given shape. Returns null, if not found.
   */
  protected MultiText findNameMultiText(Shape shape) {
    if(shape.getGraphicsAlgorithm() instanceof MultiText) {
      return (MultiText) shape.getGraphicsAlgorithm();
    }
    
    MultiText foundtext = null;
    if(shape instanceof ContainerShape) {
      for(Shape child : ((ContainerShape) shape).getChildren()) {
        foundtext = findNameMultiText(child);
        if(foundtext != null) {
          return foundtext;
        }
      }
    }
    return null;
  }
  
  protected Text findFieldText(PictogramElement shape) {
    if(shape.getGraphicsAlgorithm() instanceof Text) {
      return (Text) shape.getGraphicsAlgorithm();
    }
    
    Text foundText = null;
    if(shape instanceof ContainerShape) {
      for(Shape child : ((ContainerShape) shape).getChildren()) {
        foundText = findFieldText(child);
        if(foundText != null) {
          return foundText;
        }
      }
    }
    return null;
  }
  
  protected String getLabelTextValue(FormPropertyDefinition definition) {
    String value =  definition.getName() != null ?  definition.getName() : "";
    if(definition.isMandatory()) {
      value = value + " *";
    }
    return value;
  }
  
  protected String getLabelTextValue(FormPropertyGroup group) {
    return group.getTitle() != null && !group.getTitle().isEmpty() ?  group.getTitle() : "";
  }
}
