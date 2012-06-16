package org.activiti.designer.eclipse.bpmn;

import org.activiti.designer.bpmn2.model.AssociationDirection;
import org.activiti.designer.bpmn2.model.Process;


public class AssociationModel {

  public String id;
  public AssociationDirection associationDirection;
  public String sourceRef;
  public String targetRef;
  public Process parentProcess;
}
