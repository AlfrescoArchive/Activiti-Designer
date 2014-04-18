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
  TUNIU_FORMTYPES_USERTASK("com.tuniu.nfbird.bpm.model.WorkformTask"),
  EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS("org.activiti.designer.preferences.editor.addLabelsToNewSequenceFlows"), 
  EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS("org.activiti.designer.preferences.editor.addDefaultContentToDiagrams"), 
  SAVE_TO_FORMAT("org.activiti.designer.preferences.save.saveToFormat"), 
  SAVE_IMAGE("org.activiti.designer.preferences.save.imageFormat"),
  SAVE_IMAGE_ADD_OVERLAY("org.activiti.designer.preferences.save.imageAddOverlay"),
  SAVE_IMAGE_ADD_OVERLAY_POSITION("org.activiti.designer.preferences.save.imageAddOverlayPosition"),
  SAVE_IMAGE_ADD_OVERLAY_FILENAME("org.activiti.designer.preferences.save.imageAddOverlayFilename"),
  SAVE_IMAGE_ADD_OVERLAY_KEY("org.activiti.designer.preferences.save.imageAddOverlayKey"),
  SAVE_IMAGE_ADD_OVERLAY_DATE("org.activiti.designer.preferences.save.imageAddOverlayDate"),
  SAVE_IMAGE_ADD_OVERLAY_NAMESPACE("org.activiti.designer.preferences.save.imageAddOverlayNamespace"),
  SAVE_IMAGE_ADD_OVERLAY_REVISION("org.activiti.designer.preferences.save.imageAddOverlayRevision"),
  SAVE_IMAGE_ADD_OVERLAY_TEXT_COLOR("org.activiti.designer.preferences.save.imageAddOverlayTextColor"),
  SAVE_IMAGE_ADD_OVERLAY_BORDER_COLOR("org.activiti.designer.preferences.save.imageAddOverlayBorderColor"),
  SAVE_IMAGE_ADD_OVERLAY_BACKGROUND_COLOR("org.activiti.designer.preferences.save.imageAddOverlayBackgroundColor"),
  
  CMIS_URL("org.activiti.designer.kickstart.preferences.cmis.url"),
  CMIS_USERNAME("org.activiti.designer.kickstart.preferences.cmis.username"),
  CMIS_PASSWORD("org.activiti.designer.kickstart.preferences.cmis.password"),
  
  PROCESS_TARGET_LOCATION_REPOSITORY("target-location-repository"),
  PROCESS_TARGET_LOCATION_SHARE("target-location-share"),
  PROCESS_EXPORT_TYPE("export-type"),
  CMIS_WORKFLOW_DEFINITION_PATH("cmis-workflow-definition-path"),
  CMIS_MODELS_PATH("cmis-models-path"),
  CMIS_MODELS_DELETE("cmis-models-delete"),
  CMIS_SHARE_CONFIG_PATH("cmis-share-config-path"),
  SHARE_RELOAD_URL("share-reload-url"),
  SHARE_ENABLED("share-enabled");

  public static final String PROCESS_EXPORT_TYPE_TARGET = "target";
  public static final String PROCESS_EXPORT_TYPE_FS = "fs";
  public static final String PROCESS_EXPORT_TYPE_CMIS = "cmis";
  
  private String preferenceId;

  private Preferences(final String preferenceId) {
    this.preferenceId = preferenceId;
  }

  public String getPreferenceId() {
    return preferenceId;
  }

}
