package org.activiti.designer.bpmn2.model;


public class TextAnnotation extends Artifact {
  
  protected String text;
  protected String textFormat;
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getTextFormat() {
    return textFormat;
  }
  
  public void setTextFormat(String textFormat) {
    this.textFormat = textFormat;
  }
}
