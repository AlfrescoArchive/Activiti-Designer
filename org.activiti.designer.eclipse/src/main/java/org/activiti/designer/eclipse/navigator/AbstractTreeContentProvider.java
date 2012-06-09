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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Tiese Barrell
 */
public abstract class AbstractTreeContentProvider implements ITreeContentProvider, IResourceChangeListener, IResourceDeltaVisitor {

  protected static final Object[] NO_CHILDREN = new Object[0];

  private final Map<IResource, List<TreeNode>> cachedModelMap = new HashMap<IResource, List<TreeNode>>();

  private StructuredViewer viewer;

  public AbstractTreeContentProvider() {
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
  public Object[] getChildren(final Object parentElement) {
    Object[] result = null;

    if (parentElement instanceof TreeNode) {
      final TreeNode treeNode = (TreeNode) parentElement;
      result = treeNode.hasChildren() ? getChildrenAsArray(treeNode) : NO_CHILDREN;
    } else {
      result = getChildrenForElement(parentElement);
    }

    return result != null ? result : NO_CHILDREN;
  }

  @Override
  public boolean hasChildren(Object element) {
    if (element instanceof TreeNode) {
      return ((TreeNode) element).hasChildren();
    } else {
      return hasChildrenForElement(element);
    }
  }

  protected final StructuredViewer getStructuredViewer() {
    return viewer;
  }

  protected final void addModelToCache(final IResource resource, final List<TreeNode> treeNodes) {
    cachedModelMap.put(resource, treeNodes);
  }

  protected final void removeModelFromCache(final IResource resource) {
    if (isModelCachedForResource(resource)) {
      cachedModelMap.remove(resource);
    }
  }

  protected final boolean isModelCachedForResource(final IResource resource) {
    return cachedModelMap.containsKey(resource);
  }

  protected final List<TreeNode> getCachedChildrenForResource(final IResource resource) {
    return cachedModelMap.get(resource);
  }

  /**
   * Invoked if the element is not a {@link TreeNode}. Subclasses must implement
   * this method to indicate whether they will provide children for elements
   * that are not {@link TreeNode}s.
   * 
   * @param element
   *          the element to determine children for
   * @return true if the element has children, false otherwise
   */
  protected abstract boolean hasChildrenForElement(final Object element);

  /**
   * Invoked if the element is not a {@link TreeNode}. Subclasses must implement
   * this method to provide children for elements that are not {@link TreeNode}
   * s.
   * 
   * @param element
   *          the element to determine children for
   * @return an array of child objects
   */
  protected abstract Object[] getChildrenForElement(final Object parentElement);

  protected Object[] getChildrenAsArray(final TreeNode treeNode) {
    Object[] result = null;
    final List<TreeNode> children = treeNode.getChildren();
    if (children.size() > 0) {
      result = children.toArray();
    }
    return result;
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
