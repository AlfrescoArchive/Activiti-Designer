package org.activiti.designer.bpmn2.model;

public class Association extends Artifact {

  protected AssociationDirection associationDirection = AssociationDirection.NONE;

  protected BaseElement sourceRef;

  protected BaseElement targetRef;

  public AssociationDirection getAssociationDirection() {
    return associationDirection;
  }

  public void setAssociationDirection(AssociationDirection associationDirection) {
    this.associationDirection = associationDirection;
  }

  public BaseElement getSourceRef() {
    return sourceRef;
  }

  public void setSourceRef(BaseElement sourceRef) {
    this.sourceRef = sourceRef;
  }

  public BaseElement getTargetRef() {
    return targetRef;
  }

  public void setTargetRef(BaseElement targetRef) {
    this.targetRef = targetRef;
  }

}
