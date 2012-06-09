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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Tiese Barrell
 * 
 */
public abstract class AbstractTreeNode<T extends Object> implements TreeNode {

  private static final String NO_NAME = "(no name)";

  private final List<TreeNode> children = new ArrayList<TreeNode>();

  private final String id;

  private final Object parent;
  private final T treeObject;
  private final String name;

  protected AbstractTreeNode(final Object parent, final T treeObject, final String name) {
    super();
    this.id = UUID.randomUUID().toString();
    this.parent = parent;
    this.treeObject = treeObject;
    this.name = getName(name);

    extractChildren();
  }

  private final String getName(final String name) {
    String result = name;
    if (name == null || name.isEmpty()) {
      result = NO_NAME;
    }
    return result;
  }

  protected void addChildNode(final TreeNode treeNode) {
    children.add(treeNode);
  }

  protected void referenceChildNodesToOwnChildren(final TreeNode transparentTreeNode) {
    for (final TreeNode treeNode : transparentTreeNode.getChildren()) {
      addChildNode(treeNode);
    }
  }

  @Override
  public List<TreeNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public Object getParent() {
    return parent;
  }

  protected abstract void extractChildren();

  protected TreeNode getParentNode() {

    TreeNode result = null;

    if (getParent() instanceof TreeNode) {
      result = (TreeNode) getParent();
    } else {
      throw new IllegalArgumentException("getParentNode was called on a node that doesn't have a parent that is a treenode");
    }
    return result;
  }

  public T getModelObject() {
    return (T) treeObject;
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean hasChildren() {
    return !getChildren().isEmpty();
  }

  public String toString() {
    return getName();
  }

  public int hashCode() {
    return id.hashCode();
  }

  public boolean equals(Object obj) {
    return obj instanceof AbstractTreeNode && ((AbstractTreeNode< ? >) obj).id.equals(id);
  }

}
