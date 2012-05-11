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

package org.activiti.designer.eclipse.navigator.diagramtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.eclipse.swt.graphics.Image;

/**
 * @author Tiese Barrell
 * 
 */
public abstract class AbstractDiagramTreeNode<T extends Object> implements DiagramTreeNode {

  private static final String NO_NAME = "(no name)";

  private final List<DiagramTreeNode> children = new ArrayList<DiagramTreeNode>();

  private final String id;

  private final Object parent;
  private final T modelObject;
  private final String name;

  protected AbstractDiagramTreeNode(final Object parent, final T modelObject, final String name) {
    super();
    this.id = UUID.randomUUID().toString();
    this.parent = parent;
    this.modelObject = modelObject;
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

  protected void addChildNode(final DiagramTreeNode diagramTreeNode) {
    children.add(diagramTreeNode);
  }

  protected void referenceChildNodesToOwnChildren(final DiagramTreeNode transparentProcessNode) {
    for (final DiagramTreeNode diagramTreeNode : transparentProcessNode.getChildren()) {
      addChildNode(diagramTreeNode);
    }
  }

  protected final Bpmn2MemoryModel findRootModel() {
    Bpmn2MemoryModel result = null;

    if (hasRootModel()) {
      result = getRootModel();
    } else if (getParent() instanceof AbstractDiagramTreeNode) {
      result = ((AbstractDiagramTreeNode< ? >) getParent()).findRootModel();
    }

    return result;
  }

  protected boolean hasRootModel() {
    return false;
  }

  protected Bpmn2MemoryModel getRootModel() {
    return null;
  }

  protected abstract void extractChildren();

  @Override
  public List<DiagramTreeNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public Object getParent() {
    return parent;
  }

  protected DiagramTreeNode getParentNode() {

    DiagramTreeNode result = null;

    if (getParent() instanceof DiagramTreeNode) {
      result = (DiagramTreeNode) getParent();
    } else {
      throw new IllegalArgumentException("getParentNode was called on a node that doesn't have a parent that is a node");
    }
    return result;
  }

  protected T getModelObject() {
    return (T) modelObject;
  }

  @Override
  public String getName() {
    return name;
  }

  public boolean hasChildren() {
    return !getChildren().isEmpty();
  }

  public Image getDisplayImage() {
    return ExtensionPointUtil.getIconFromIconProviders(modelObject);
  }

  public String toString() {
    return getName();
  }

  public int hashCode() {
    return id.hashCode();
  }

  public boolean equals(Object obj) {
    return obj instanceof AbstractDiagramTreeNode && ((AbstractDiagramTreeNode< ? >) obj).id.equals(id);
  }

}
