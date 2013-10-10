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
package org.activiti.designer.kickstart.eclipse.navigator.handlers;

import java.util.List;

import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigator;
import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigatorSelectionHolder;
import org.activiti.designer.kickstart.eclipse.navigator.CmisUtil;
import org.activiti.designer.kickstart.eclipse.navigator.ContentProvider;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteHandler extends AbstractHandler {

//	@Override
//	public Object execute(ExecutionEvent event) throws ExecutionException {
//
//		List<CmisObject> selectedObjects = CmisNavigatorSelectionHolder.getInstance().getSelectedObjects();
//		
//		if (!selectedObjects.isEmpty()) {
//			
//			MessageDialog dialog = new MessageDialog(
//					HandlerUtil.getActiveShellChecked(event),
//					generateDialogTitle(selectedObjects),
//					null, // icon
//					generateDialogMessage(selectedObjects),
//					MessageDialog.WARNING,
//					new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
//
//			int result = dialog.open();
//			if (result == 0) { // 'YES'
//				CmisUtil.deleteCmisObjects(selectedObjects);
//				
//				final IWorkbenchPart wbp = HandlerUtil.getActivePart(event);
//				((CmisNavigator) wbp).getCommonViewer().getTree();
//			}
//		}
//
//		return this;
//	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		List<CmisObject> selectedObjects = CmisNavigatorSelectionHolder.getInstance().getSelectedObjects();
		
		if (!selectedObjects.isEmpty()) {
			
			MessageDialog dialog = new MessageDialog(
					HandlerUtil.getActiveShellChecked(event),
					generateDialogTitle(selectedObjects),
					null, // icon
					generateDialogMessage(selectedObjects),
					MessageDialog.WARNING,
					new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);

			int result = dialog.open();
			if (result == 0) { // 'YES'
				CmisUtil.deleteCmisObjects(selectedObjects);
				
				Tree tree = getTreeView(event);
				TreeItem[] treeItems = tree.getSelection();
				for (TreeItem treeItem : treeItems) {
					treeItem.dispose();
				}
				
			}
		}

		return this;
	}
	
	private Tree getTreeView(ExecutionEvent event) {
		IWorkbenchPart wbp = HandlerUtil.getActivePart(event);
		return ((CmisNavigator) wbp).getCommonViewer().getTree();
	}

	private String generateDialogTitle(List<CmisObject> selectedObjects) {
		String title = "";
		if (selectedObjects.size() == 1) {
			title = "Deleting " + selectedObjects.get(0).getName();
		} else {
			int folderCount = 0;
			int documentCount = 0;
			for (CmisObject cmisObject : selectedObjects) {
				if (cmisObject instanceof Folder) {
					folderCount++;
				} else if (cmisObject instanceof Document) {
					documentCount++;
				}
			}
			
			if (folderCount > 0 && documentCount > 0) {
				title = "Deleting " + folderCount + " folders and " + documentCount + " files";
			} else if (folderCount == 0 && documentCount > 0) {
				title = "Deleting " + documentCount + " files";
			} else if (folderCount > 0 && documentCount ==  0) {
				title = "Deleting " + folderCount + " folders";
			}
		}
		return title;
	}
	
	private String generateDialogMessage(List<CmisObject> selectedObjects) {
		StringBuilder messageBuilder = new StringBuilder("Are you sure you want to delete ");
		for (int i=0; i<selectedObjects.size(); i++) {
			messageBuilder.append(selectedObjects.get(i).getName());
			if (i == selectedObjects.size() - 2) {
				messageBuilder.append(" and ");
			}
			if (i != selectedObjects.size() - 1 && i != selectedObjects.size() - 2) {
				messageBuilder.append(", ");
			}
		}
		messageBuilder.append("?");
		return messageBuilder.toString();
	}

}
