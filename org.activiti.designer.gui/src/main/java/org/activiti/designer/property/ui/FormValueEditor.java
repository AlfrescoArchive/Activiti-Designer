package org.activiti.designer.property.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class FormValueEditor extends TableFieldEditor {
  
  protected Composite parent;
	
  public FormValueEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Id", "Name"},
        new int[] {200, 200}, parent);
    this.parent = parent;
  }

  public void initialize(String formValues) {
    removeTableItems();
    if(formValues == null || formValues.length() == 0) return;
    String[] formValueList = formValues.split(";");
    for (String formValue : formValueList) {
      addTableItem(formValue);
    }
  }

  @Override
  protected String createList(String[][] items) {
    return null;
  }

  @Override
  protected String[][] parseString(String string) {
    return null;
  }
  
  protected void addTableItem(String formValue) {
    
    if(table != null) {
    	String[] valueObject = formValue.split(":");
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, valueObject[0]);
      tableItem.setText(1, valueObject[1]);
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
