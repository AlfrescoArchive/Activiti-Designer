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

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;

/**
 * @author Tiese Barrell
 */
public class LaneDiagramTreeNode extends AbstractDiagramTreeNode<Lane> {

  public LaneDiagramTreeNode(final Object parent, final Lane lane) {
    super(parent, lane, lane.getName());
  }

  @Override
  protected void extractChildren() {

    final List<FlowElement> laneElements = extractLaneElements();

    for (final FlowElement flowElement : laneElements) {
      addChildNode(DiagramTreeNodeFactory.createFlowElementNode(this, flowElement));
    }
  }

  private List<FlowElement> extractLaneElements() {

    List<FlowElement> result = null;

    final Process parentProcess = findParentProcess();

    if (parentProcess != null) {
      result = extractLaneElements(parentProcess);
    } else {
      throw new IllegalArgumentException("extractLaneElements was called on a lane that has no process as a parent");
    }
    return result;
  }

  private Process findParentProcess() {
    Process result = null;

    final Pool parentPool = findParentPool();
    if (parentPool != null) {
      final String poolId = parentPool.getId();

      final Bpmn2MemoryModel bpmn2MemoryModel = findRootModel();

      result = bpmn2MemoryModel.getProcess(poolId);

    } else {
      throw new IllegalArgumentException("findParentPool was called on a lane that has no pool as a parent");
    }
    return result;
  }

  private Pool findParentPool() {
    Pool result = null;

    if (getParentNode() instanceof AbstractDiagramTreeNode) {
      final Object modelObject = ((AbstractDiagramTreeNode< ? >) getParentNode()).getModelObject();
      if (modelObject instanceof Pool) {
        result = (Pool) modelObject;
      }
    }
    return result;
  }

  private List<FlowElement> extractLaneElements(final Process parentProcess) {

    final List<FlowElement> result = new ArrayList<FlowElement>();

    final List<String> flowElementRefs = getModelObject().getFlowReferences();
    final List<FlowElement> parentProcessFlowElements = parentProcess.getFlowElements();
    for (final FlowElement flowElement : parentProcessFlowElements) {
      if (flowElementRefs.contains(flowElement.getId())) {
        result.add(flowElement);
      }
    }

    return result;
  }

}
