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

import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.eclipse.navigator.TreeNode;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;

/**
 * @author Tiese Barrell
 */
public class PoolDiagramTreeNode extends AbstractDiagramTreeNode<Pool> {

  public PoolDiagramTreeNode(final Object parent, final Pool pool) {
    super(parent, pool, pool.getName());
  }

  @Override
  protected void extractChildren() {

    final String poolId = getModelObject().getId();

    final Bpmn2MemoryModel rootModel = findRootModel();

    processChildren(poolId, rootModel);

  }

  private void processChildren(String poolId, Bpmn2MemoryModel rootModel) {
    if (poolId != null && rootModel != null) {
      final Process foundProcess = rootModel.getProcess(poolId);
      addProcessChild(foundProcess);
    }
  }

  private void addProcessChild(final Process process) {
    if (process != null) {
      final TreeNode transparentTreeNode = DiagramTreeNodeFactory.createTransparentProcessNode(this, process);
      referenceChildNodesToOwnChildren(transparentTreeNode);
    }
  }

}
