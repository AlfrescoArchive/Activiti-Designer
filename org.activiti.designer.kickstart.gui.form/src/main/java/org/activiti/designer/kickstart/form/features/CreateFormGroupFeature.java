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

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Frederik Heremans
 */
public class CreateFormGroupFeature extends AbstractCreateFeature implements ICustomUndoableFeature {

  protected FormPropertyGroup createdGroup;
  
  public CreateFormGroupFeature(IFeatureProvider fp) {
    super(fp, "Group", "Group of components");
  }
  
  @Override
  public boolean canCreate(ICreateContext context) {
    return context.getTargetContainer() instanceof Diagram;
  }
  
  public final Object[] create(ICreateContext context) {
    createdGroup = new FormPropertyGroup();
    createdGroup.setTitle("Group of properties");
    
    // Add the new group to the model
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized()) {
      model.getFormDefinition().addFormPropertyGroup(createdGroup);
    }
    
    // Add graphical information
    addGraphicalRepresentation(context, createdGroup);
    return new Object[] { createdGroup };
  }
  
  @Override
  public boolean canUndo(IContext context) {
    return createdGroup != null;
  }
  
  @Override
  public boolean canRedo(IContext context) {
    return createdGroup != null;
  }
  
  @Override
  public void undo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdGroup != null) {
      model.getFormDefinition().getFormGroups().remove(createdGroup);
    }
  }
  
  @Override
  public void redo(IContext context) {
    KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(getDiagram())));
    if (model != null && model.isInitialized() && createdGroup != null) {
      model.getFormDefinition().addFormPropertyGroup(createdGroup);
    }
  }
  
  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_GROUP.getImageKey();
  }
  
}