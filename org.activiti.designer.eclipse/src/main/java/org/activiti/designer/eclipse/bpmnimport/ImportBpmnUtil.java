/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.eclipse.bpmnimport;

import java.io.IOException;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;


/**
 * @author Tijs Rademakers
 */
public class ImportBpmnUtil {
  
  public static ImportBpmnElementsCommand createDiagram(String processName, String bpmnFile, 
          IProject project, IContainer targetFolder) {
    
    // Get the default resource set to hold the new resource
    ResourceSet resourceSet = new ResourceSetImpl();
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
    if (editingDomain == null) {
      // Not yet existing, create one
      editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
    }
    
    // Create the data within a command and save (must not happen inside
    // the command since finishing the command will trigger setting the 
    // modification flag on the resource which will be used by the save
    // operation to determine which resources need to be saved)
    ImportBpmnElementsCommand operation = new ImportBpmnElementsCommand( 
            editingDomain, processName, bpmnFile, targetFolder);
    editingDomain.getCommandStack().execute(operation);
    try {
      operation.getCreatedResource().save(null);
    } catch (IOException e) {
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), e.getMessage(), e); //$NON-NLS-1$
      ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error Occured", e.getMessage(), status);
    }

    // Dispose the editing domain to eliminate memory leak
    editingDomain.dispose();
    return operation;
  }

}
