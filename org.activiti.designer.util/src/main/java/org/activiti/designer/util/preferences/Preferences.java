/**
 * 
 */
package org.activiti.designer.util.preferences;

/**
 * Enumeration of preferences used in the designer.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public enum Preferences {

  ECLIPSE_ACTIVITI_JAR_LOCATION("org.activiti.designer.preferences.eclipse.activitiJarLocation"),
  ALFRESCO_ENABLE("com.alfresco.designer.preferences.enable"),
  ALFRESCO_FORMTYPES_STARTEVENT("com.alfresco.designer.preferences.formtypes.startevent"),
  ALFRESCO_FORMTYPES_USERTASK("com.alfresco.designer.preferences.formtypes.usertask"),
  EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS("org.activiti.designer.preferences.editor.addLabelsToNewSequenceFlows"), 
  EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS("org.activiti.designer.preferences.editor.addDefaultContentToDiagrams"), 
  SAVE_IMAGE("org.activiti.designer.preferences.save.imageFormat");

  private String preferenceId;

  private Preferences(final String preferenceId) {
    this.preferenceId = preferenceId;
  }

  public String getPreferenceId() {
    return preferenceId;
  }

}
