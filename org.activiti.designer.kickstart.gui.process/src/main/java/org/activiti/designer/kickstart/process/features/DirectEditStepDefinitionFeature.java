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
package org.activiti.designer.kickstart.process.features;

import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.NamedStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;

/**
 * @author Tijs Rademakers
 */
public class DirectEditStepDefinitionFeature extends AbstractDirectEditingFeature implements ICustomUndoableFeature {

  protected KickstartProcessModelUpdater<?> updater;
  
  public DirectEditStepDefinitionFeature(IFeatureProvider fp) {
    super(fp);
  }

  public int getEditingType() {
    return TYPE_TEXT;
  }

  @Override
  public boolean canDirectEdit(IDirectEditingContext context) {
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (businessObject instanceof StepDefinition);
  }

  public String getInitialValue(IDirectEditingContext context) {
    // Return the current name of the EClass
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    if (businessObject instanceof NamedStepDefinition) {
      return ((NamedStepDefinition) businessObject).getName();
    }
    return null;
  }
  

  @Override
  public String checkValueValid(String value, IDirectEditingContext context) {
    if (value.length() < 1) {
      return "The label should be at least one character long";
    }
    return null;
  }

  public void setValue(String value, IDirectEditingContext context) {
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    updater = ((KickstartProcessFeatureProvider) getFeatureProvider()).getModelUpdaterFor(businessObject, context.getPictogramElement());
    if (updater != null) {
      // Set the new value in the updateable definition
      if(businessObject instanceof NamedStepDefinition) {
        ((NamedStepDefinition) updater.getUpdatableBusinessObject()).setName(value);
      }
      updater.doUpdate();
    }
  }

  @Override
  public void undo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    if (updater != null) {
      updater.doUndo(false);
    }
  }

  @Override
  public boolean canRedo(IContext context) {
    return true;
  }

  @Override
  public void redo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    if (updater != null) {
      updater.doUpdate(false);
    }
  }
}
