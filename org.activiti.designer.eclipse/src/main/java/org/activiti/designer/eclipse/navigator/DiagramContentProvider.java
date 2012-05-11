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

package org.activiti.designer.eclipse.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.navigator.diagramtree.DiagramTreeNode;
import org.activiti.designer.eclipse.navigator.diagramtree.DiagramTreeNodeFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Tiese Barrell
 */
public class DiagramContentProvider implements ITreeContentProvider, IResourceChangeListener, IResourceDeltaVisitor {

  private static final Object[] NO_CHILDREN = new Object[0];

  private final Map<IFile, List<DiagramTreeNode>> cachedModelMap = new HashMap<IFile, List<DiagramTreeNode>>();

  private StructuredViewer viewer;

  public DiagramContentProvider() {
    super();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
  }

  @Override
  public void dispose() {
    cachedModelMap.clear();
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
  }

  @Override
  public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
    if (oldInput != null && !oldInput.equals(newInput))
      cachedModelMap.clear();
    viewer = (StructuredViewer) aViewer;
  }

  @Override
  public Object[] getChildren(Object parentElement) {
    Object[] result = null;

    if (parentElement instanceof DiagramTreeNode) {
      final DiagramTreeNode diagramTreeData = (DiagramTreeNode) parentElement;
      result = diagramTreeData.hasChildren() ? getDataArray(diagramTreeData) : NO_CHILDREN;
    } else if (parentElement instanceof IFile) {
      /* Possible model file */
      final IFile modelFile = (IFile) parentElement;
      if (isDiagramFile(modelFile)) {
        result = getDataArray(modelFile);
        if (result == null && !updateModel(modelFile).isEmpty()) {
          result = getDataArray(modelFile);
        }
      }
    }

    return result != null ? result : NO_CHILDREN;
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return getChildren(inputElement);
  }

  @Override
  public Object getParent(Object element) {
    if (element instanceof DiagramTreeNode) {
      DiagramTreeNode data = (DiagramTreeNode) element;
      return data.getParent();
    }
    return null;
  }
  @Override
  public boolean hasChildren(Object element) {
    if (element instanceof DiagramTreeNode) {
      return ((DiagramTreeNode) element).hasChildren();
    } else if (element instanceof IFile) {
      return isDiagramFile((IFile) element);
    }
    return false;
  }

  /**
   * Load the model from the given file, if possible.
   * 
   * @param modelFile
   *          The IFile which contains the persisted model
   */
  private synchronized List<DiagramTreeNode> updateModel(IFile modelFile) {
    List<DiagramTreeNode> model = new ArrayList<DiagramTreeNode>();
    if (isDiagramFile(modelFile)) {
      if (modelFile.exists()) {

        model = new ArrayList<DiagramTreeNode>();

        model.add(DiagramTreeNodeFactory.createModelFileNode(modelFile));

        cachedModelMap.put(modelFile, model);
        return model;

      } else {
        cachedModelMap.remove(modelFile);
      }
    }
    return model;
  }

  private boolean isDiagramFile(IFile modelFile) {
    return ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION_RAW.equals(modelFile.getFileExtension());
  }

  private Object[] getDataArray(final IFile modelFile) {
    Object[] result = null;
    final List<DiagramTreeNode> diagramTreeData = cachedModelMap.get(modelFile);
    if (diagramTreeData != null) {
      result = getDataArray(diagramTreeData.get(0));
    }
    return result;
  }

  private Object[] getDataArray(final DiagramTreeNode diagramTreeData) {
    Object[] result = null;
    final List<DiagramTreeNode> children = diagramTreeData.getChildren();
    if (children.size() > 0) {
      result = children.toArray();
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
            if (viewer != null && !viewer.getControl().isDisposed())
              viewer.refresh(file);
            return Status.OK_STATUS;
          }
        }.schedule();
      }
      return false;
    }
    return false;
  }

  @Override
  public void resourceChanged(final IResourceChangeEvent event) {
    IResourceDelta delta = event.getDelta();
    try {
      delta.accept(this);
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }
}
