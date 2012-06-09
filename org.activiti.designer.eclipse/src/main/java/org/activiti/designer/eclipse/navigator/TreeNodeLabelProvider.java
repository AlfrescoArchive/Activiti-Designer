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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

/**
 * @author Tiese Barrell
 */
public class TreeNodeLabelProvider implements ILabelProvider, IDescriptionProvider {

  @Override
  public void addListener(ILabelProviderListener arg0) {
  }

  @Override
  public void dispose() {
  }

  @Override
  public boolean isLabelProperty(Object arg0, String arg1) {
    return false;
  }

  @Override
  public void removeListener(ILabelProviderListener arg0) {
  }

  @Override
  public String getDescription(Object element) {
    return getText(element);
  }
  @Override
  public Image getImage(Object element) {
    if (element instanceof TreeNode) {
      return ((TreeNode) element).getDisplayImage();
    }
    return null;
  }

  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode) {
      final TreeNode treeNode = (TreeNode) element;
      return treeNode.getName();
    }
    return null;
  }

}
