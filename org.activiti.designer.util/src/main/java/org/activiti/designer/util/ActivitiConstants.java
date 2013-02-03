package org.activiti.designer.util;

/**
 * Constants for BPMN diagrams.
 *
 * @author Tiese Barrell
 * @since 0.5.0
 * @version 2
 *
 */
public final class ActivitiConstants {

  /** The project nature of Activiti projects */
  public static final String NATURE_ID = "org.activiti.designer.nature";

  /** A constant for an empty string */
  public static final String EMPTY_STRING = "";

  /** The extension of the temporary diagram file */
  public static final String DIAGRAM_FILE_EXTENSION_RAW = "bpmn2d";

  /** The extension of the data file */
  public static final String DATA_FILE_EXTENSION_RAW = "bpmn";

  public static final String DIAGRAM_FOLDER = "src/main/resources/diagrams/";
  public static final String DATA_FILE_EXTENSION = "." + DATA_FILE_EXTENSION_RAW;

  public static final String DIAGRAM_EDITOR_ID = "org.activiti.designer.editor.diagramEditor";

  public static final String BPMN_MARSHALLER_NAME = "Activiti Designer BPMN 2.0";
  public static final String BPMN_VALIDATOR_ID = "ActivitiDesignerBPMNValidator";
  public static final String BPMN_VALIDATOR_NAME = "Activiti Designer BPMN Validator";
  public static final String IMAGE_MARSHALLER_NAME = "Activiti Designer Image";

  public static final String ACTIVITI_GENERAL_MARKER_ID = "org.activiti.designer.eclipse.activitiGeneralMarker";

  /**
   * In case a model update occurs within the model, this text is shown in the Eclipse Activity
   * view
   */
  public static final String DEFAULT_MODEL_CHANGE_TEXT = "Activiti BPMN Model Update";

  private ActivitiConstants() {
    super();
  }

}
