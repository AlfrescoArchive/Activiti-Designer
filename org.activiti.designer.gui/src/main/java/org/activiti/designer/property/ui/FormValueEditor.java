package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.bpmn.model.FormValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class FormValueEditor extends TableFieldEditor {
  
  protected Composite parent;
  protected boolean enableTableChanges = true;
	
  public FormValueEditor(String key, Composite parent) {
    super(key, "", new String[] {"Id", "Name"},
        new int[] {200, 200}, parent);
    this.parent = parent;
  }

  public void initialize(List<FormValue> formValues) {
    removeTableItems();
    for (FormValue formValue : formValues) {
      addTableItem(formValue);
    }
  }
  
  @Override
  protected boolean isTableChangeEnabled() {
    return true;
  }

  @Override
  protected String createList(String[][] items) {
    return null;
  }

  @Override
  protected String[][] parseString(String string) {
    return null;
  }
  
  protected void addTableItem(FormValue formValue) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, formValue.getId());
      tableItem.setText(1, formValue.getName());
    }
  }

  @Override
  protected String[] getNewInputObject() {
  	FormValueDialog dialog = new FormValueDialog(parent.getShell(), getItems());
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {
      return new String[] { dialog.id, dialog.name};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
  	FormValueDialog dialog = new FormValueDialog(parent.getShell(), getItems(), 
            item.getText(0), item.getText(1));
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {      
      return new String[] { dialog.id, dialog.name};
    } else {
      return null;
    }
  }
  
  @Override
  protected void removedItem(int index) {
	  // TODO Auto-generated method stub 
  }
}
