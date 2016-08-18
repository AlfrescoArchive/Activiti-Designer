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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;


/**
 * @author Joram Barrez
 */
public class CmisNavigatorSelectionHolder {
	
	private static volatile CmisNavigatorSelectionHolder INSTANCE = new CmisNavigatorSelectionHolder();
	
	private List<CmisObject> selectedObjects = Collections.synchronizedList(new ArrayList<CmisObject>());
	
	public static CmisNavigatorSelectionHolder getInstance() {
		return INSTANCE;
	}

	public List<CmisObject> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(List<CmisObject> selectedObjects) {
		this.selectedObjects.clear();
		this.selectedObjects.addAll(selectedObjects);
	}
	
}
