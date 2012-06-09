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

package org.activiti.designer.eclipse.navigator.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.navigator.AbstractTreeContentProvider;
import org.activiti.designer.eclipse.navigator.TreeNode;
import org.eclipse.core.resources.IFile;
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
public class DiagramTreeContentProvider extends AbstractTreeContentProvider {

  public DiagramTreeContentProvider() {
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

  private boolean isDiagramFile(IFile modelFile) {
    return ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION_RAW.equals(modelFile.getFileExtension());
  }

  @Override
  protected boolean hasChildrenForElement(final Object element) {
    if (element instanceof IFile) {
      return isDiagramFile((IFile) element);
    }
    return false;
  }

  @Override
  protected Object[] getChildrenForElement(final Object parentElement) {
    Object[] result = null;

    if (parentElement instanceof IFile) {
      /* Possible model file */
      final IFile modelFile = (IFile) parentElement;
      if (isDiagramFile(modelFile)) {
        result = getDataArray(modelFile);
        if (result == null && !updateModel(modelFile).isEmpty()) {
          result = getDataArray(modelFile);
        }
      }
    }

    return result;

  }

  private Object[] getDataArray(final IFile modelFile) {
    Object[] result = null;
    final List<TreeNode> diagramTreeData = getCachedChildrenForResource(modelFile);
    if (diagramTreeData != null) {
      result = getChildrenAsArray(diagramTreeData.get(0));
    }
    return result;
  }

  @Override
  public boolean visit(final IResourceDelta delta) throws CoreException {
    IResource source = delta.getResource();
    switch (source.getType()) {
    case IResource.ROOT:
    case IResource.PROJECT:
    case IResource.FOLDER:
      return true;
    case IResource.FILE:
      final IFile file = (IFile) source;
      if (isDiagramFile(file)) {
        updateModel(file);
        new UIJob("Update Process Model in CommonViewer") { //$NON-NLS-1$

          public IStatus runInUIThread(IProgressMonitor monitor) {
            if (getStructuredViewer() != null && !getStructuredViewer().getControl().isDisposed())
              getStructuredViewer().refresh(file);
            return Status.OK_STATUS;
          }
        }.schedule();
      }
      return false;
    }
    return false;
  }

  /**
   * Load the model from the given file, if possible.
   * 
   * @param modelFile
   *          The IFile which contains the persisted model
   */
  private synchronized List<TreeNode> updateModel(IFile modelFile) {
    List<TreeNode> model = new ArrayList<TreeNode>();
    if (isDiagramFile(modelFile)) {
      if (modelFile.exists()) {

        model = new ArrayList<TreeNode>();

        model.add(DiagramTreeNodeFactory.createModelFileNode(modelFile));

        addModelToCache(modelFile, model);
        return model;

      } else {
        removeModelFromCache(modelFile);
      }
    }
    return model;
  }

}
