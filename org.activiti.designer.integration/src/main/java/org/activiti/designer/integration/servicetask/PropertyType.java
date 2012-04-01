/**
 * 
 */
package org.activiti.designer.integration.servicetask;

/**
 * @author Tiese Barrell
 * @since 0.6.0
 * @version 2
 * 
 */
public enum PropertyType {

  /**
   * Property type for properties to be displayed as a single line text input
   * field.
   */
  TEXT,

  /**
   * Property type for properties to be displayed as a multiline text input
   * field.
   */
  MULTILINE_TEXT,

  /**
   * Property type for properties to be displayed as a period picker.
   */
  PERIOD,

  /**
   * Property type for properties to be displayed as a date picker.
   */
  DATE_PICKER,

  /**
   * Property type for single-selection choice properties to be displayed as a
   * radio button group.
   */
  RADIO_CHOICE,

  /**
   * Property type for single-selection choice properties to be displayed as a
   * combobox.
   */
  COMBOBOX_CHOICE,

  /**
   * Property type for boolean properties to be displayed as a checkbox.
   */
  BOOLEAN_CHOICE;

}
