/**
 * 
 */
package org.activiti.designer.validation.bpmn20.bundle;

/**
 * Plugin constants for the org.activiti.designer.validation.bpmn20 bundle.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 5.6
 * 
 */
public final class PluginConstants {

  public static final String VALIDATOR_ID = "com.atosorigin.esuite.editor.process.validation.ESuiteProcessValidator";
  public static final String VALIDATOR_NAME = "E-Suite Process Validator";
  public static final String FORMAT_NAME = "E-Suite ZaakType jPDL";

  public static final String MARKER_MESSAGE_PATTERN = "[%s] %s";

  public static final int WORK_CLEAR_MARKERS = 5;
  public static final int WORK_EXTRACT_CONSTRUCTS = 20;

  public static final int WORK_USER_TASK = 10;
  public static final int WORK_SCRIPT_TASK = 10;
  public static final int WORK_SERVICE_TASK = 10;
  public static final int WORK_SEQUENCE_FLOW = 10;
  public static final int WORK_SUB_PROCESS = 10;

  public static final int WORK_TOTAL = WORK_CLEAR_MARKERS + WORK_EXTRACT_CONSTRUCTS + WORK_USER_TASK + WORK_SCRIPT_TASK + WORK_SERVICE_TASK
          + WORK_SEQUENCE_FLOW + WORK_SUB_PROCESS;

}
