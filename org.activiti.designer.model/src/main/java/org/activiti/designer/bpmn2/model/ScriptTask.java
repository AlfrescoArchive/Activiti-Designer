package org.activiti.designer.bpmn2.model;

public class ScriptTask extends Task {

  protected String scriptFormat;
  protected String script;

  public String getScriptFormat() {
    return scriptFormat;
  }
  public void setScriptFormat(String scriptFormat) {
    this.scriptFormat = scriptFormat;
  }
  public String getScript() {
    return script;
  }
  public void setScript(String script) {
    this.script = script;
  }
}
