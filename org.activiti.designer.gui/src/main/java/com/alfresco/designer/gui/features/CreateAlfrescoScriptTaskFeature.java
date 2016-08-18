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
package com.alfresco.designer.gui.features;

import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.PluginImage;
import org.activiti.designer.features.AbstractCreateFastBPMNFeature;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAlfrescoScriptTaskFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "alfrescoScripttask";

  public CreateAlfrescoScriptTaskFeature(IFeatureProvider fp) {
    super(fp, "AlfrescoScriptTask", "Add Alfresco script task");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || 
            parentObject instanceof SubProcess || parentObject instanceof Lane);
  }

  @Override
  public Object[] create(ICreateContext context) {
    ServiceTask newScriptTask = new ServiceTask();
    newScriptTask.setImplementation(AlfrescoScriptTask.ALFRESCO_SCRIPT_DELEGATE);
    newScriptTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
    newScriptTask.setId(getNextId(newScriptTask));
    newScriptTask.setName("Alfresco Script Task");

    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).addFlowElement(newScriptTask);
      
    } else if (parentObject instanceof Lane) {
      final Lane lane = (Lane) parentObject;
      lane.getFlowReferences().add(newScriptTask.getId());
      lane.getParentProcess().addFlowElement(newScriptTask);
      
    } else {
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model.getBpmnModel().getMainProcess() == null) {
        model.addMainProcess();
      }
      model.getBpmnModel().getMainProcess().addFlowElement(newScriptTask);
    }

    addGraphicalContent(context, newScriptTask);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);

    return new Object[] { newScriptTask };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_SCRIPTTASK.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
