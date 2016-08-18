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
package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinitionContainer;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

/**
 * Base class for creating new form-properties. Subclasses should override {@link #createFormPropertyDefinition(ICreateContext)},
 * returning a {@link FormPropertyDefinition}. Adding the graphical representation and adding the property
 * to the model is handled by this class. 
 *  
 * @author Frederik Heremans
 */
public abstract class AbstractCreateFormPropertyFeature extends AbstractCreateFeature implements ICustomUndoableFeature {

  protected FormPropertyDefinition createdDefinition;
  protected FormPropertyDefinitionContainer definitionContainer;
  
  public AbstractCreateFormPropertyFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }
  
  public final Object[] create(ICreateContext context) {
    // Let subclass create a new property
    createdDefinition = createFormPropertyDefinition(context);
    
    // Add the new property-definition to the model
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized()) {
      Object businessObject = getFeatureProvider().getBusinessObjectForPictogramElement(context.getTargetContainer());
      if(businessObject instanceof FormPropertyDefinitionContainer) {
        definitionContainer = (FormPropertyDefinitionContainer) businessObject; 
        definitionContainer.addFormProperty(createdDefinition);
      }
    }
    
    // Add graphical information for the definition
    addGraphicalRepresentation(context, createdDefinition);
    return new Object[] { createdDefinition };
  }
  
  @Override
  public boolean canUndo(IContext context) {
    return createdDefinition != null && definitionContainer != null;
  }
  
  @Override
  public boolean canRedo(IContext context) {
    return createdDefinition != null && definitionContainer != null;
  }
  
  @Override
  public void undo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdDefinition != null) {
      definitionContainer.removeFormProperty(createdDefinition);
    }
  }
  
  @Override
  public void redo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdDefinition != null) {
      definitionContainer.addFormProperty(createdDefinition);
    }
  }
  
  protected abstract FormPropertyDefinition createFormPropertyDefinition(ICreateContext context);
}
