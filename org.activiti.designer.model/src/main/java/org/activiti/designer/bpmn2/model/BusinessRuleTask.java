package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class BusinessRuleTask extends Task {

  protected String resultVariableName;
  protected boolean exclude;
  protected List<String> ruleNames = new ArrayList<String>();
  protected List<String> inputVariables = new ArrayList<String>();

  public boolean isExclude() {
    return exclude;
  }
  public void setExclude(boolean exclude) {
    this.exclude = exclude;
  }
  public String getResultVariableName() {
    return resultVariableName;
  }
  public void setResultVariableName(String resultVariableName) {
    this.resultVariableName = resultVariableName;
  }
  public List<String> getRuleNames() {
    return ruleNames;
  }
  public void setRuleNames(List<String> ruleNames) {
    this.ruleNames = ruleNames;
  }
  public List<String> getInputVariables() {
    return inputVariables;
  }
  public void setInputVariables(List<String> inputVariables) {
    this.inputVariables = inputVariables;
  }
}
