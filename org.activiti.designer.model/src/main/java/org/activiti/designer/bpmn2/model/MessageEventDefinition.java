package org.activiti.designer.bpmn2.model;
/**
 * @author Saeid Mirzaei
 */
public class MessageEventDefinition extends EventDefinition {

  protected String messageRef;
  
  public String getMessageRef() {
    return messageRef;
  }
  
  public void setMessageRef(String messageRef) {
    this.messageRef = messageRef;
  }
}
