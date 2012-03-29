package org.activiti.designer.eclipse.common;

/**
 * Constants for BPMN diagrams.
 * 
 * @author Tiese Barrell
 * @since 0.5.0
 * @version 2
 * 
 */
public final class ActivitiBPMNDiagramConstants {

  public static final String DIAGRAM_FOLDER = "src/main/resources/diagrams/";
  public static final String DIAGRAM_EXTENSION_RAW = "bpmn";
  public static final String DIAGRAM_EXTENSION = "." + DIAGRAM_EXTENSION_RAW;
  
  public static final String DIAGRAM_EDITOR_ID = "org.activiti.designer.editor.diagramEditor";

  public static final String BPMN_MARSHALLER_NAME = "Activiti Designer BPMN 2.0";
  public static final String BPMN_VALIDATOR_ID = "ActivitiDesignerBPMNValidator";
  public static final String BPMN_VALIDATOR_NAME = "Activiti Designer BPMN Validator";
  public static final String IMAGE_MARSHALLER_NAME = "Activiti Designer Image";

  public static final String ACTIVITI_GENERAL_MARKER_ID = "org.activiti.designer.eclipse.activitiGeneralMarker";

  private ActivitiBPMNDiagramConstants() {

  }

}
