package org.activiti.designer.property.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;


public class ExecutionListenerEditor extends AbstractListenerEditor {
	
	public ExecutionListenerEditor(String key, Composite parent) {
    super(key, parent);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items) {
    return new ExecutionListenerDialog(shell, items, isSequenceFlow);
  }

  @Override
  protected AbstractListenerDialog getDialog(Shell shell, TableItem[] items, String implementationType, String implementation, String event, String fieldString) {
    return new ExecutionListenerDialog(shell, items, isSequenceFlow, implementationType, implementation, event, fieldString);
  }
	
}
