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
package org.activiti.designer.kickstart.eclipse.navigator.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Inspired by
 * http://stackoverflow.com/questions/14332400/how-to-display-only-the
 * -contents-of-my-workspace-in-my-treeviewer
 * 
 * @author jbarrez
 */
public class FileTreeContentProvider implements ITreeContentProvider {

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getElements(Object inputElement) {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject projects = (IProject) parentElement;
			try {
				return onlyFolders(projects.members());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (parentElement instanceof IFolder) {
			IFolder ifolder = (IFolder) parentElement;
			try {
				return onlyFolders(ifolder.members());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IProject) {
			IProject projects = (IProject) element;
			return projects.getParent();
		}
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			return folder.getParent();
		}
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			return file.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {
			IProject projects = (IProject) element;
			try {
				return onlyFolders(projects.members()).length > 0;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			try {
				return onlyFolders(folder.members()).length > 0;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	protected Object[] onlyFolders(IResource[] resources) {
    	List<IResource> filteredResources = new ArrayList<IResource>();
    	for (IResource resource : resources) {
    		if (resource instanceof IProject || resource instanceof IFolder) {
    			filteredResources.add(resource);
    		}
    	}
    	return filteredResources.toArray();
    }
}