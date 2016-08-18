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
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link ReferencePropertyDefinition} objects, referencing package-items.
 * 
 * @author Frederik Heremans
 */
public class PackageItemsPropertyShapeController extends SimpleIconInputShapeController {

  public PackageItemsPropertyShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof ReferencePropertyDefinition && AlfrescoConversionConstants.FORM_REFERENCE_PACKAGE_ITEMS.equals(
        ((ReferencePropertyDefinition) businessObject).getType());
  }

  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    // No direct editing supported
    return null;
  }

  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    return false;
  }

  @Override
  protected String getIconKey(FormPropertyDefinition definition) {
    return KickstartFormPluginImage.NEW_PACKAGE_ITEMS.getImageKey();
  }
}
