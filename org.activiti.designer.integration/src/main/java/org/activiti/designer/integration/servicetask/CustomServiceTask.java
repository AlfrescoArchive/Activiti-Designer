/**
 * 
 */
package org.activiti.designer.integration.servicetask;

/**
 * Interface for customizations of the default ServiceTask from BPMN.
 * 
 * This interface should not be implemented directly by clients - an abstract
 * base class that implements this interface should be extended instead
 * 
 * @author Tiese Barrell
 * @version 1
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
   * Gets the name of the class to be used at runtime for this
   * {@link CustomServiceTask}. This class is determined by the task's
   * {@link org.activiti.designer.integration.servicetask.annotation.Runtime}
   * annotation
   * 
   * @return the canonical name of the runtime class
   */
  String getRuntimeClassname();

  /**
   * Gets the order index for this {@link CustomServiceTask} within it's
   * container drawer. Used to sort the tools in the Palette.
   * 
   * @return the order index
   */
  Integer getOrder();

}
