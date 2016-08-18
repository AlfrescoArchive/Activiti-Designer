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
package org.activiti.designer.kickstart.process.diagram.shape;


import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Interface describing a controller capable of creating and updating
 * shapes that are related to a particular business-object. 
 * 
 * @author Frederik Heremans
 */
public interface BusinessObjectShapeController {

  /**
   * @return true, if this controller can create/update a shape for the
   * given business-object.
   */
  boolean canControlShapeFor(Object businessObject);

  /**
   * @return true, if the values in the given businessObject differ from the corresponding
   * values found in the given shape.
   */
  boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject);
  
  /**
   * @param businessObject object to create shape for
   * @param layoutParent parent to add new shape to
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   * @return new shape, representing the given business-object.
   */
  ContainerShape createShape(Object businessObject, ContainerShape layoutParent, int width, int height);
  
  /**
   * @param businessObject object to update shape for
   * @param shape shape representing the business object
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   */
  void updateShape(ContainerShape shape, Object businessObject, int width, int height);
  
  /**
   * Extract data from the given shape, hiding away any complexity related to child-shape iteration
   * and type-checking. Provided a clean way of exposing data that is present in a shape-tree, which is
   * specific to the implementation.
   * 
   * @param key data key, defined by the implementation.
   * @param shape shape to get the data from
   * @return the shape data value that can be extracted from the given shape for the given key. Returns
   * null if the data cannot be extracted or if the data is null.
   */
  Object extractShapeData(String key, Shape shape);
  
  /**
   * @param container
   * @return the graphics algorithm that should be used to position the default direct editor for
   * the given container shape.
   */
   GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container);
}
