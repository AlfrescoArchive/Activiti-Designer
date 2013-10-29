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
package org.activiti.designer.kickstart.eclipse.navigator.listeners;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.eclipse.navigator.CmisNavigatorSelectionHolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;

/**
 * @author Joram Barrez
 */
public class CmisNavigatorSelectionChangedListener implements ISelectionChangedListener {
	
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection() instanceof TreeSelection) {
			TreeSelection selection = (TreeSelection) event.getSelection();
			Object[] selectedElements = selection.toArray();
			if (selectedElements != null && selectedElements.length > 0) {
				List<CmisObject> selectedCmisObjects = new ArrayList<CmisObject>();
				for (Object selectedElement : selectedElements) {
					if (selectedElement instanceof CmisObject) {
						selectedCmisObjects.add((CmisObject) selectedElement);
					}
				}
				CmisNavigatorSelectionHolder.getInstance().setSelectedObjects(selectedCmisObjects);
			}
		}
	}

}
