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

import org.activiti.designer.eclipse.navigator.AbstractTreeNode;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.eclipse.swt.graphics.Image;

/**
 * @author Tiese Barrell
 * 
 */
public abstract class AbstractDiagramTreeNode<T extends Object> extends AbstractTreeNode<T> {

  protected AbstractDiagramTreeNode(final Object parent, final T modelObject, final String name) {
    super(parent, modelObject, name);
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

  public Image getDisplayImage() {
    return ExtensionPointUtil.getIconFromIconProviders(getModelObject());
  }

}
