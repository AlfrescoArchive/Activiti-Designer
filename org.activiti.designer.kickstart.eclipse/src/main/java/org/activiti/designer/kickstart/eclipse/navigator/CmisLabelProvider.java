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
package org.activiti.designer.kickstart.eclipse.navigator;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class CmisLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	
	public CmisLabelProvider() {
		System.out.println("Label provider wordt aangemaakt");
	}

	public String getText(Object element) {
		System.out.println("Getting name of " + element);
		if (element instanceof Folder) {
			return ((Folder) element).getName();
		} else if (element instanceof Document) {
			return ((Document) element).getName();
		}
		return null;
	}

	public String getDescription(Object element) {
		return getText(element);
	}

	public Image getImage(Object element) {
		if (element instanceof Folder) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof Document) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}

}
