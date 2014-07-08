package org.activiti.designer.eclipse.navigator.cloudrepo;

import com.fasterxml.jackson.databind.JsonNode;


public class ActivitiCloudEditorException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  protected JsonNode exceptionNode;
  
  public ActivitiCloudEditorException(JsonNode exceptionNode) {
    this.exceptionNode = exceptionNode;
  }
  
  public ActivitiCloudEditorException(String exceptionMessage) {
    super(exceptionMessage);
  }

  public JsonNode getExceptionNode() {
    return exceptionNode;
  }
}
