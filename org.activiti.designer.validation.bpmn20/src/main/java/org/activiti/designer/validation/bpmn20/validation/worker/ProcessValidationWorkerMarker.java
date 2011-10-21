package org.activiti.designer.validation.bpmn20.validation.worker;

import org.activiti.designer.validation.bpmn20.validation.worker.impl.ValidationCode;

/**
 * Container object for markers created by {@link ProcessValidationWorker}s.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ProcessValidationWorkerMarker {

  private int severity;
  private String message;
  private String nodeId;
  private ValidationCode code;

  public ProcessValidationWorkerMarker(final int severity, final String message, final String nodeId, final ValidationCode code) {
    super();
    this.severity = severity;
    this.message = message;
    this.nodeId = nodeId;
    this.code = code;
  }

  public int getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  public String getNodeId() {
    return nodeId;
  }

  public ValidationCode getCode() {
    return code;
  }

}
