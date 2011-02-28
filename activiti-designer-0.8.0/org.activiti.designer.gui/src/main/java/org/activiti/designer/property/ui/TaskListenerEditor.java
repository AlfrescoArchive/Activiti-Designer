package org.activiti.designer.property.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;



public class TaskListenerEditor extends AbstractListenerEditor {

  public TaskListenerEditor(String key, Composite parent) {
    super(key, parent);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items) {
    return new TaskListenerDialog(shell, items);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items, String implementationType, String implementation, String event, String fieldString) {
    return new TaskListenerDialog(shell, items, implementationType, implementation, event, fieldString);
  }
	
}
