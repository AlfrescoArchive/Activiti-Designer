package org.activiti.designer.bpmn2.model;

import java.util.ArrayList;
import java.util.List;

public class DataGrid implements ComplexDataType {

  protected List<DataGridRow> rows = new ArrayList<DataGridRow>();

  public List<DataGridRow> getRows() {
    return rows;
  }

  public void setRows(List<DataGridRow> rows) {
    this.rows = rows;
  }
}
