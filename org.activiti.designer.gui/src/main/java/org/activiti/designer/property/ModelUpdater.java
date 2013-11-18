package org.activiti.designer.property;

import org.activiti.designer.command.BpmnProcessModelUpdater;

public interface ModelUpdater {
  
  public BpmnProcessModelUpdater getProcessModelUpdater();
  
  public void executeModelUpdater();

}
