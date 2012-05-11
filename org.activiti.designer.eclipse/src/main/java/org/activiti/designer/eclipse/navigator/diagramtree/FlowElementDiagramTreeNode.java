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

import org.activiti.designer.bpmn2.model.FlowElement;

/**
 * @author Tiese Barrell
 */
public class FlowElementDiagramTreeNode extends AbstractDiagramTreeNode<FlowElement> {

  public FlowElementDiagramTreeNode(final Object parent, final FlowElement flowElement) {
    super(parent, flowElement, flowElement.getName());
  }

  @Override
  protected void extractChildren() {  
    //no-op
  }
  
}
