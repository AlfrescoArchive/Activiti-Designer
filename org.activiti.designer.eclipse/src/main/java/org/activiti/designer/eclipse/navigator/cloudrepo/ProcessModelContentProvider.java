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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author jbarrez
 */
public class ProcessModelContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	
	public static JsonNode modelsNode;
	
	public ProcessModelContentProvider() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ActivitiCloudEditorRoot) {
		  System.out.println("modelsNode " + modelsNode);
			if (modelsNode == null) {
				initializeRootElements();
			}
			
			ArrayNode modelArrayNode =  (ArrayNode) modelsNode.get("data");
			Object[] objectArray = new Object[modelArrayNode.size()];
			for (int i = 0; i < modelArrayNode.size(); i++) {
			  JsonNode modelNode = modelArrayNode.get(i);
			  objectArray[i] = modelNode;
			}
			return objectArray;
			
		} else {
			return EMPTY_ARRAY;
		}
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return (element instanceof ActivitiCloudEditorRoot);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	  modelsNode = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	private void initializeRootElements() {
		modelsNode = ActivitiCloudEditorUtil.getProcessModels();
	}

}
