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
package org.activiti.designer.kickstart.eclipse.sync;
import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author jbarrez
 */
public class SyncProcessWithRepositoryDelegate implements IObjectActionDelegate {
	
	private Shell shell;
	private IFile selectedFile;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			// Check if the selected process has a node id in the json
			SimpleWorkflowJsonConverter simpleWorkflowJsonConverter = new SimpleWorkflowJsonConverter();
			WorkflowDefinition workflowDefinition = simpleWorkflowJsonConverter.readWorkflowDefinition(selectedFile.getContents());
			String nodeId = (String) workflowDefinition.getParameters().get(SyncConstants.REPOSITORY_NODE_ID);
			
			if (nodeId == null) {
				
				// No current node found in process json, so allow to select a location 
				showLocationSelectionDialog();
				
			} else {
				
				// Check if the location still exists
				CmisObject cmisObject = CmisUtil.getCmisObject(nodeId);
				if (cmisObject != null && cmisObject instanceof Document) {
					final Document document = (Document) cmisObject;
					SyncUtil.startProcessSynchronizationBackgroundJob(shell, document, selectedFile.getName(), false, selectedFile);
				} else {
					showLocationSelectionDialog();
				}
				
			}
		} catch (Exception exception) {
			Logger.logError(exception);
		}
		
	}

	protected void showLocationSelectionDialog() {
	  WizardDialog dialog = new WizardDialog(shell, new SyncProcessWithRepositoryWizard(selectedFile));
	  dialog.open();
  }

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			selectedFile = (IFile) strucSelection.getFirstElement();
		}
	}
	

}
