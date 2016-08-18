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
package org.activiti.designer.kickstart.process.dialog;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KickstartFormsTreeContentProvider implements ITreeContentProvider {

  private IProject project;

  public KickstartFormsTreeContentProvider(IProject project) {
    this.project = project;
  }

  public void dispose() {
    // No custom listeners added, nothing to dispose
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // Let the viewer handle this
  }

  public Object[] getElements(Object inputElement) {
    return getValidChildren(project);
  }

  public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof IContainer) {
      return getValidChildren((IContainer) parentElement);
    }
    return null;
  }

  public Object getParent(Object element) {
    if (element instanceof IResource) {
      return ((IResource) element).getParent();
    }
    return null;
  }

  public boolean hasChildren(Object element) {
    if (element instanceof IContainer) {
      try {
        for (IResource child : ((IContainer) element).members()) {
          if (isValid(child)) {
            return true;
          }
        }
      } catch (CoreException ce) {
        Logger.logError("Error while getting children for resource: " + element, ce);
      }
    }
    return false;
  }

  protected Object[] getValidChildren(IContainer container) {
    List<IResource> resources = new ArrayList<IResource>();
    try {
      for (IResource child : container.members()) {
        if (isValid(child)) {
          resources.add(child);
        }
      }
    } catch (CoreException ce) {
      Logger.logError("Error while getting children for resource: " + container, ce);
    }

    return resources.toArray();
  }

  protected boolean isValid(IResource resource) throws CoreException {
    if (resource instanceof IFile) {
      IFile file = (IFile) resource;
      if(!file.isSynchronized(0)) {
        // In case the resource has been updated, refresh it
        file.refreshLocal(0, null);
      }
      if (file.getContentDescription() != null
          && file.getContentDescription().getContentType() != null
          && KickstartFormMemoryModel.KICKSTART_FORM_CONTENT_TYPE.equals(file.getContentDescription()
              .getContentType().getId())) {
        return true;
      }
    } else if (resource instanceof IFolder) {
      return !resource.isDerived() && !resource.isHidden();
    }
    return false;
  }
}
