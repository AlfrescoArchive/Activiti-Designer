package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class Event extends FlowNode {

  protected List<EventDefinition> eventDefinitions = new ArrayList<EventDefinition>();

  public List<EventDefinition> getEventDefinitions() {
    return eventDefinitions;
  }

  public void setEventDefinitions(List<EventDefinition> eventDefinitions) {
    this.eventDefinitions = eventDefinitions;
  }
}
