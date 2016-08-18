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
package org.activiti.designer.controller;


import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

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
   * @param businessObject object to create shape for
   * @param layoutParent parent to add new shape to
   * @param width the preferred width, can be ignored by implementations.
   * @param height the preferred height, can be ignored by implementations.
   * @return new shape, representing the given business-object.
   */
  PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context);
}
