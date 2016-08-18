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
/**
 * 
 */
package org.activiti.designer.integration.servicetask;

import org.activiti.designer.integration.DiagramBaseShape;

/**
 * Interface for customizations of the default ServiceTask from BPMN.
 * 
 * This interface should not be implemented directly by clients - an abstract
 * base class that implements this interface should be extended instead
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * 
 */
public interface CustomServiceTask {

  public static final String MANIFEST_EXTENSION_NAME = "ActivitiDesigner-Extension-Name";

  /**
   * Gets the identifier for this custom service task. The qualified identifier
   * uniquely identifies this service task.
   * 
   * @return the identifier
   */
  String getId();

  /**
   * Gets a descriptive name for the service task. This name is presented to the
   * user in the designer.
   * 
   * @return the service task's name
   */
  String getName();

  /**
   * Gets a description for the service task. This name is presented to the user
   * in the designer.
   * 
   * @return the service task's description
   */
  String getDescription();

  /**
   * Determines to which palette drawer this service task contributes. Provide
   * an empty string to prevent this service task from showing up in any palette
   * drawer.
   * 
   * @return the name of the palette drawer
   */
  String contributeToPaletteDrawer();

  /**
   * Gets the path to the icon image file used for small icon display. Returns
   * null if this {@link CustomServiceTask} has no image of its own.
   * 
   * @return the path to the icon file or null if there is none
   */
  String getSmallIconPath();

  /**
   * Gets the path to the icon image file used for large icon display. Returns
   * null if this {@link CustomServiceTask} has no image of its own.
   * 
   * @return the path to the icon file or null if there is none
   */
  String getLargeIconPath();

  /**
   * Gets the path to the icon image file used for display in the shape on the
   * canvas. Returns null if this {@link CustomServiceTask} has no image of its
   * own.
   * 
   * @return the path to the icon file or null if there is none
   */
  String getShapeIconPath();

  /**
   * Gets the base shape for the diagram for this {@link CustomServiceTask}. The
   * base shape is the type of shape used to display a node in the diagram. For
   * ServiceTasks, this a rounded rectangle by default. Override this method and
   * return a different base shape to customize the base shape drawn for this
   * {@link CustomServiceTask}.
   * 
   * @return the base shape for the element in the diagram
   */
  DiagramBaseShape getDiagramBaseShape();

  /**
   * Gets the type of delegate defined by the {@link @Runtime} annotation.
   * 
   * @return the delegate type
   */
  DelegateType getDelegateType();

  /**
   * Gets the specification of the delegate to be used at runtime for this
   * {@link CustomServiceTask}. This specification is determined by the task's
   * {@link org.activiti.designer.integration.annotation.Runtime}
   * annotation. The type of the specification is provided by
   * {@link #getDelegateType()}
   * 
   * @see #getDelegateType()
   * 
   * @return the specification or an empty string if there is none
   */
  String getDelegateSpecification();

  /**
   * Gets the order index for this {@link CustomServiceTask} within it's
   * container drawer. Used to sort the tools in the Palette.
   * 
   * @return the order index
   */
  Integer getOrder();

}
