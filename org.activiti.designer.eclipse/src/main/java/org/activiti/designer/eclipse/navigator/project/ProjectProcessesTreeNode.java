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

import java.util.List;

import org.activiti.designer.eclipse.navigator.TreeNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;

/**
 * @author Tiese Barrell
 * 
 */
public class ProjectProcessesTreeNode extends AbstractProjectContainerTreeNode<TreeNode> {

  protected ProjectProcessesTreeNode(final IProject parent) {
    super(parent, null, "Project Processes Root Node");
  }

  @Override
  public Image getDisplayImage() {
    return null;
  }

  @Override
  protected void extractChildren() {
    addChildNode(new TreeNode() {

      @Override
      public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public Object getParent() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getName() {
        // TODO Auto-generated method stub
        return "TEST";
      }

      @Override
      public Image getDisplayImage() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public List<TreeNode> getChildren() {
        // TODO Auto-generated method stub
        return null;
      }
    });

  }
}
