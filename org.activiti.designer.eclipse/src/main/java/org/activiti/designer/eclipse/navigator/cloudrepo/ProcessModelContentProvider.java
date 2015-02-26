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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author jbarrez
 */
public class ProcessModelContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	
	public static JsonNode modelsNode;
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ActivitiCloudEditorRoot) {
			if (modelsNode == null) {
			  try {
			    initializeRootElements();
			  } catch (final ActivitiCloudEditorException e) {
			    String detailMessage = null;
	        if (e.getExceptionNode() != null) {
	          detailMessage = e.getExceptionNode().get("message").asText();
	        } else {
	          detailMessage = e.getMessage();
	        }
	        // creating fake entry
	        ObjectMapper objectMapper = new ObjectMapper();
	        modelsNode = objectMapper.createObjectNode();
	        ArrayNode modelArrayNode = objectMapper.createArrayNode();
	        ((ObjectNode) modelsNode).put("data", modelArrayNode);
	        ObjectNode errorNode = objectMapper.createObjectNode();
	        modelArrayNode.add(errorNode);
	        errorNode.put("name", "Process models could not be retrieved: " + detailMessage);
			  }
			}
			
			if (modelsNode != null) {
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
