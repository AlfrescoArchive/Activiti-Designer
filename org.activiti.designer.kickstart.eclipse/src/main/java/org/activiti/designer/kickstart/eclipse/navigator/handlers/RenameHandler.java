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

import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigator;
import org.activiti.designer.kickstart.eclipse.navigator.dialog.RenameDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class RenameHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RenameDialog renameDialog = new RenameDialog(HandlerUtil.getActiveShellChecked(event));
		renameDialog.open();
		
		// This is executed after close
		IWorkbenchPart wbp = HandlerUtil.getActivePart(event);
		Tree tree = ((CmisNavigator) wbp).getCommonViewer().getTree();
		TreeItem[] selectedItems = tree.getSelection();
		if (selectedItems != null && selectedItems.length > 0) {
			TreeItem selectedItem = selectedItems[0]; // Can be only one for rename
			selectedItem.setText(renameDialog.getName());
		}
		
		return null;
	}

}
