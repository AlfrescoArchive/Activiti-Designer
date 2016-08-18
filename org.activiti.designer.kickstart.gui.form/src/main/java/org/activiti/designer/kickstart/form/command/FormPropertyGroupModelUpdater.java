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
package org.activiti.designer.kickstart.form.command;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FormPropertyGroupModelUpdater extends KickstartModelUpdater<FormPropertyGroup> {

  public FormPropertyGroupModelUpdater(FormPropertyGroup businessObject, PictogramElement pictogramElement,
      IFeatureProvider featureProvider) {
    super(businessObject, pictogramElement, featureProvider);
  }

  @Override
  protected FormPropertyGroup cloneBusinessObject(FormPropertyGroup businessObject) {
    // We use the same list of form-properties instead of cloning, as this 
    // updater doesn't impact children
    FormPropertyGroup newGroup = new FormPropertyGroup();
    newGroup.setFormPropertyDefinitions(businessObject.getFormPropertyDefinitions());
    newGroup.setId(businessObject.getId());
    newGroup.setTitle(businessObject.getTitle());
    newGroup.setType(businessObject.getType());
    return newGroup;
  }

  @Override
  protected void performUpdates(FormPropertyGroup valueObject, FormPropertyGroup targetObject) {
    targetObject.setId(valueObject.getId());
    targetObject.setTitle(valueObject.getTitle());
    
    boolean typeChanged = !StringUtils.equals(valueObject.getType(), targetObject.getType());
    targetObject.setType(valueObject.getType());
    
    if(typeChanged) {
      // Force relayout of the updated group
      PictogramElement element = ((KickstartFormFeatureProvider)featureProvider).getPictogramElementForBusinessObject(targetObject);
      if(element != null) {
        ((KickstartFormFeatureProvider)featureProvider).getFormLayouter().relayout((ContainerShape) element);
      }
    }
  }


}
