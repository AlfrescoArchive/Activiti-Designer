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

package org.activiti.designer.eclipse.navigator.project;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiProjectNature;
import org.activiti.designer.eclipse.navigator.AbstractTreeContentProvider;
import org.activiti.designer.eclipse.navigator.TreeNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Tiese Barrell
 */
public class ProjectTreeContentProvider extends AbstractTreeContentProvider {

  public ProjectTreeContentProvider() {
    super();
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return getChildren(inputElement);
  }

  @Override
  public Object getParent(Object element) {
    if (element instanceof TreeNode) {
      TreeNode data = (TreeNode) element;
      return data.getParent();
    }
    return null;
  }

  @Override
  protected boolean hasChildrenForElement(final Object element) {
    if (element instanceof IProject) {
      return isActivitiProject((IProject) element);
    }
    return false;
  }

  @Override
  protected Object[] getChildrenForElement(final Object parentElement) {
    Object[] result = null;

    if (parentElement instanceof IProject) {
      final IProject project = (IProject) parentElement;
      if ((isActivitiProject(project))) {
        result = getDataArray(project);
        if (result == null && !updateModel(project).isEmpty()) {
          result = getDataArray(project);
        }
      }
    }

    return result;

  }

  private Object[] getDataArray(final IProject project) {
    Object[] result = null;
    final List<TreeNode> projectTreeData = getCachedChildrenForResource(project);
    if (projectTreeData != null) {
      result = getChildrenAsArray(projectTreeData.get(0));
    }
    return result;
  }

  @Override
  public boolean visit(final IResourceDelta delta) throws CoreException {
    IResource source = delta.getResource();
    switch (source.getType()) {
    case IResource.ROOT:
    case IResource.PROJECT:
      final IProject project = (IProject) source;
      if (isActivitiProject(project)) {
        updateModel(project);
        new UIJob("Update Project Model in CommonViewer") { //$NON-NLS-1$

          public IStatus runInUIThread(IProgressMonitor monitor) {
            if (getStructuredViewer() != null && !getStructuredViewer().getControl().isDisposed())
              getStructuredViewer().refresh(project);
            return Status.OK_STATUS;
          }
        }.schedule();
      }
      return false;
    case IResource.FOLDER:
      return true;
    case IResource.FILE:
    }
    return false;
  }

  private boolean isActivitiProject(final IProject project) {
    boolean result = false;
    try {
      result = project.hasNature(ActivitiProjectNature.NATURE_ID);
    } catch (CoreException e) {
      // no-op
    }
    return result;
  }

  /**
   * Load the model from the given project, if possible.
   * 
   * @param project
   *          The IProject to get the model for
   */
  private synchronized List<TreeNode> updateModel(IProject project) {
    List<TreeNode> model = new ArrayList<TreeNode>();
    if (isActivitiProject(project)) {
      if (project.exists()) {

        model = new ArrayList<TreeNode>();

        model.add(ProjectTreeNodeFactory.createProcessesNode(project));

        addModelToCache(project, model);
        return model;

      } else {
        removeModelFromCache(project);
      }
    }
    return model;
  }

}
