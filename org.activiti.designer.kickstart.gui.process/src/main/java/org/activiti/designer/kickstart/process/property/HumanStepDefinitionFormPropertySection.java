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
package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.util.KickstartProcessConstants;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link StepDefinition}.
 * 
 * @author Frederik Heremans
 */
public class HumanStepDefinitionFormPropertySection extends AbstractKickstartProcessPropertySection {

  protected FormReferenceViewer formReferenceViewer;

  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    formReferenceViewer = new FormReferenceViewer(formComposite, new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        String referencePath = formReferenceViewer.getReferencedFormPath();
        HumanStepDefinition workflowDefinition =  (HumanStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        workflowDefinition.getParameters().put(KickstartProcessConstants.PARAMETER_FORM_REFERENCE, referencePath);
        executeModelUpdater();
      }
    }, null);
    
    registerControl(formReferenceViewer.getComposite());
    createSeparator();
    
    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    formReferenceViewer.getComposite().setLayoutData(formData);
  }
  
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
    if(formReferenceViewer != null) {
      IProject project = null;
      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
      if (model != null && model.getModelFile() != null) {
        project = model.getModelFile().getProject();
      }
      formReferenceViewer.setProject(project);
    }
  }
  
  @Override
  protected void populateControl(Control control, Object businessObject) {
    if(control == formReferenceViewer.getComposite()) {
      HumanStepDefinition definition = (HumanStepDefinition) businessObject;
      formReferenceViewer.setReferencedFormPath((String) definition.getParameters()
          .get(KickstartProcessConstants.PARAMETER_FORM_REFERENCE));
    } else {
      super.populateControl(control, businessObject);
    }
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Not using standard control listeners, storing is done manually
  }
  

}
