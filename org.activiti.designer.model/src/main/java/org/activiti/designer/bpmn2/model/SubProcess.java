package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class SubProcess extends Activity {

  protected List<FlowElement> flowElements = new ArrayList<FlowElement>();
  protected List<Artifact> artifacts = new ArrayList<Artifact>();

  public List<FlowElement> getFlowElements() {
    return flowElements;
  }

  public void setFlowElements(List<FlowElement> flowElements) {
    this.flowElements = flowElements;
  }
  
  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = artifacts;
  }
}
