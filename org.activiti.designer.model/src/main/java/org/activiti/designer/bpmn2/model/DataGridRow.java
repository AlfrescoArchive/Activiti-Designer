package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class DataGridRow {

  protected int index;
  protected List<DataGridField> fields = new ArrayList<DataGridField>();

  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
  public List<DataGridField> getFields() {
    return fields;
  }
  public void setFields(List<DataGridField> fields) {
    this.fields = fields;
  }
}
