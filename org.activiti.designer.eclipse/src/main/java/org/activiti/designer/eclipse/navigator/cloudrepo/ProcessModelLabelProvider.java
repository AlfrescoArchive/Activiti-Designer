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
package org.activiti.designer.eclipse.navigator.cloudrepo;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.fasterxml.jackson.databind.JsonNode;

public class ProcessModelLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	
	public ProcessModelLabelProvider() {
	}

	public String getText(Object element) {
	  if (element instanceof ActivitiCloudEditorRoot) {
	    return "Root";
	  } else {
	    return ((JsonNode) element).get("name").asText();
	  }
	}

	public String getDescription(Object element) {
		return getText(element);
	}

	public Image getImage(Object element) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

}
