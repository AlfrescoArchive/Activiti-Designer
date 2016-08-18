/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.process.diagram;

import org.activiti.designer.kickstart.process.layout.KickstartProcessLayouter;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Interface representing a class capable of layouting process components.
 * 
 * @author Frederik Heremans
 * @author Tijs Rademakers
 */
public interface ProcessComponentLayout {

  /**
   * Shape was requested to move to the given location in the target container, and should be positioned
   * using this layout implementation.
   */
  void moveShape(KickstartProcessLayouter layouter, ContainerShape targetContainer, ContainerShape sourceContainer, Shape shape, int x, int y);
  
  /**
   * Request to re-layout all components in this container.
   */
  void relayout(KickstartProcessLayouter layouter, ContainerShape targetContainer);
}
