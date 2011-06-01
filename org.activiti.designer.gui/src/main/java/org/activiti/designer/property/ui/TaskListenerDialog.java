package org.activiti.designer.property.ui;

import org.eclipse.bpmn2.ActivitiListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class TaskListenerDialog extends AbstractListenerDialog {

  public TaskListenerDialog(Shell parent, TableItem[] fieldList) {
    super(parent, fieldList);
  }

  public TaskListenerDialog(Shell parent, TableItem[] fieldList, ActivitiListener savedListener) {
    super(parent, fieldList, savedListener);
  }

  @Override
  protected String[] getEventList() {
    return new String[] {"create", "assignment", "complete"};
  }

  @Override
  protected String getDefaultEvent() {
    return "create";
  }
  
}
