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
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	
	private Session cmisSession;
	
	private List<CmisObject> rootElements;
	
	public ContentProvider() {
		System.out.println("Content provider wordt aangemaakt");
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Root) {
			
			if (rootElements == null) {
				initializeRootElements();
			}
			return rootElements.toArray();
			
		} else if (parentElement instanceof Folder) {
			
			Folder folder = (Folder) parentElement;
			ItemIterable<CmisObject> children = folder.getChildren();
			List<CmisObject> childCmisObjects = new ArrayList<CmisObject>();
			for (CmisObject childCmisObject : children) {
				childCmisObjects.add(childCmisObject);
			}
			return childCmisObjects.toArray();
			
		} else if (parentElement instanceof Document) {
			return EMPTY_ARRAY;
		} else {
			return EMPTY_ARRAY;
		}
	}

	public Object getParent(Object element) {
		String parentId = null;
		if (element instanceof Folder) {
			parentId = ((Folder) element).getParentId();
		} else if (element instanceof Document) {
			parentId = ((Document) element).getParents().get(0).getId();
		}
		
		if (parentId != null) {
			return cmisSession.getObject(parentId);
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return (element instanceof Root || element instanceof Folder);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		this.cmisSession = null;
//		this.parents = null;
		this.rootElements = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	private void initializeRootElements() {
		System.out.println("Initializing session");
		cmisSession = CmisUtil.createCmisSession("admin", "admin", "http://localhost:8280/alfresco/service/cmis");
		this.rootElements = CmisUtil.getRootElements();
	}

}
