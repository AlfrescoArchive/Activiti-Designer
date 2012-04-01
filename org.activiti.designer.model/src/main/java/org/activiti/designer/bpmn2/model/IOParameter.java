package org.activiti.designer.bpmn2.model;

public class IOParameter extends BaseElement {

  protected String source;
  protected String sourceExpression;
  protected String target;
  protected String targetExpression;

  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getTarget() {
    return target;
  }
  public void setTarget(String target) {
    this.target = target;
  }
  public String getSourceExpression() {
    return sourceExpression;
  }
  public void setSourceExpression(String sourceExpression) {
    this.sourceExpression = sourceExpression;
  }
  public String getTargetExpression() {
    return targetExpression;
  }
  public void setTargetExpression(String targetExpression) {
    this.targetExpression = targetExpression;
  }
}
