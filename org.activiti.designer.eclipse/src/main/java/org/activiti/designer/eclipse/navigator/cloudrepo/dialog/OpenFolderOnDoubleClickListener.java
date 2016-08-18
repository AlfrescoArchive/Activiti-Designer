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
package org.activiti.designer.eclipse.navigator.cloudrepo.dialog;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class OpenFolderOnDoubleClickListener implements IDoubleClickListener {
	
	public void doubleClick(DoubleClickEvent event) {
    TreeViewer viewer = (TreeViewer) event.getViewer();
    IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection(); 
    Object selectedNode = thisSelection.getFirstElement(); 
    viewer.setExpandedState(selectedNode,
        !viewer.getExpandedState(selectedNode));
  }

}
