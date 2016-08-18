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
package org.activiti.designer.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class AbstractPropertyCustomTaskSection extends BaseActivitiPropertySection {

  public void runModelChange(final Runnable runnable) {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo instanceof ServiceTask || bo instanceof UserTask) {
        DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
        TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
        ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
      }
    }
  }

}