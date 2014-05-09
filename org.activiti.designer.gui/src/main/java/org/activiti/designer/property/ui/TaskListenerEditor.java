package org.activiti.designer.property.ui;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.designer.property.ModelUpdater;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;



public class TaskListenerEditor extends AbstractListenerEditor {

  public TaskListenerEditor(String key, Composite parent, ModelUpdater modelUpdater) {
    super(key, parent, TASK_LISTENER, modelUpdater);
  }
  
  @Override
  protected boolean isTableChangeEnabled() {
    return false;
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items) {
    return new TaskListenerDialog(shell, items);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items, ActivitiListener savedListener) {
    return new TaskListenerDialog(shell, items, savedListener);
  }
}
