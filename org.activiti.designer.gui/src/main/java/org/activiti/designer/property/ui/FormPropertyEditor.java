package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FormProperty;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class FormPropertyEditor extends TableFieldEditor {
  
  protected Composite parent;
  public PictogramElement pictogramElement;
  public IDiagramEditor diagramEditor;
  public Diagram diagram;
	
  public FormPropertyEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Id", "Name", "Type", "Value", "Required", "Readable", "Writeable"},
        new int[] {100, 100, 100, 200, 100, 100, 100}, parent);
    this.parent = parent;
  }

  public void initialize(List<FormProperty> formPropertyList) {
    removeTableItems();
    if(formPropertyList == null || formPropertyList.size() == 0) return;
    for (FormProperty formProperty : formPropertyList) {
      addTableItem(formProperty);
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
  
  protected void addTableItem(FormProperty formProperty) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, formProperty.getId());
      tableItem.setText(1, formProperty.getName());
      tableItem.setText(2, formProperty.getType());
      tableItem.setText(3, formProperty.getValue());
      tableItem.setText(4, "" + formProperty.isRequired());
      tableItem.setText(5, "" + formProperty.isReadable());
      tableItem.setText(6, "" + formProperty.isWriteable());
    }
  }

  @Override
  protected String[] getNewInputObject() {
    FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems());
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {
      return new String[] { dialog.id, dialog.name, dialog.type, dialog.value,
              dialog.required, dialog.readable, dialog.writeable};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
    FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems(), 
            item.getText(1), item.getText(0), item.getText(2), item.getText(3), item.getText(4),
            item.getText(5), item.getText(6));
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {      
      return new String[] { dialog.id, dialog.name, dialog.type, dialog.value, 
              dialog.required, dialog.readable, dialog.writeable };
    } else {
      return null;
    }
  }
  
  @Override
  protected void selectionChanged() {
    super.selectionChanged();
    saveFormProperties();
  }
  
  private List<FormProperty> getFormProperties(Object bo) {
    List<FormProperty> formPropertyList = null;
    if(bo instanceof UserTask) {
      formPropertyList = ((UserTask) bo).getFormProperties();
    } else if(bo instanceof StartEvent) {
      formPropertyList = ((StartEvent) bo).getFormProperties();
    }
    return formPropertyList;
  }
  
  private void saveFormProperties() {
    if (pictogramElement != null) {
      final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
      if (bo == null) {
        return;
      }
      final List<FormProperty> formPropertyList = getFormProperties(bo);
      if(formPropertyList == null) return;
      
      if(formPropertiesChanged(formPropertyList, getItems()) == false) {
        return;
      }
      
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {
        public void run() {
          for (TableItem item : getItems()) {
            String id = item.getText(0);
            String name = item.getText(1);
            String type = item.getText(2);
            String value = item.getText(3);
            String required = item.getText(4);
            String readable = item.getText(5);
            String writeable = item.getText(6);
            if(id != null && id.length() > 0) {
              
              FormProperty formProperty = formPropertyExists(formPropertyList, id);
              if(formProperty != null) {
                formProperty.setName(name);
                formProperty.setType(type);
                formProperty.setValue(value);
                if(required != null && required.length() > 0) {
                  formProperty.setRequired(Boolean.valueOf(required.toLowerCase()));
                }
                if(readable != null && readable.length() > 0) {
                  formProperty.setReadable(Boolean.valueOf(readable.toLowerCase()));
                }
                if(writeable != null && writeable.length() > 0) {
                  formProperty.setWriteable(Boolean.valueOf(writeable.toLowerCase()));
                }
              } else {
                FormProperty newFormProperty = Bpmn2Factory.eINSTANCE.createFormProperty();
                newFormProperty.setId(id);
                newFormProperty.setName(name);
                newFormProperty.setType(type);
                newFormProperty.setValue(value);
                if(required != null && required.length() > 0) {
                  newFormProperty.setRequired(Boolean.valueOf(required.toLowerCase()));
                }
                if(readable != null && readable.length() > 0) {
                  newFormProperty.setReadable(Boolean.valueOf(readable.toLowerCase()));
                }
                if(writeable != null && writeable.length() > 0) {
                  newFormProperty.setWriteable(Boolean.valueOf(writeable.toLowerCase()));
                }
                getFormProperties(bo).add(newFormProperty);
              }
            }
          }
          removeFormPropertiesNotInList(getItems(), bo);
        }
      }, editingDomain, "Model Update");
    }
  }
  
  private boolean formPropertiesChanged(List<FormProperty> formPropertyList, TableItem[] items) {
    boolean noPropertySaved = false;
    boolean nothingInTable = false;
    if(formPropertyList == null || formPropertyList.size() == 0) {
      noPropertySaved = true;
    }
    if(items == null || items.length == 0) {
      nothingInTable = true;
    }
    if(noPropertySaved && nothingInTable) {
      return false;
    } else if(noPropertySaved == false && nothingInTable == false) {
      
      for(FormProperty formProperty : formPropertyList) {
        boolean found = false;
        for (TableItem item : items) {
          if(item.getText(0).equalsIgnoreCase(formProperty.getId()) &&
                  item.getText(1).equalsIgnoreCase(formProperty.getName()) &&
                  item.getText(2).equalsIgnoreCase(formProperty.getType()) &&
                  item.getText(3).equalsIgnoreCase(formProperty.getValue()) &&
                  item.getText(4).equalsIgnoreCase("" + formProperty.isRequired()) &&
                  item.getText(5).equalsIgnoreCase("" + formProperty.isReadable()) &&
                  item.getText(6).equalsIgnoreCase("" + formProperty.isWriteable())) {
            
            found = true;
          }
        }
        if(found == false) {
          return true;
        }
      }
      
      for (TableItem item : items) {
        boolean found = false;
        for(FormProperty formProperty : formPropertyList) {
          if(item.getText(0).equalsIgnoreCase(formProperty.getId()) &&
                  item.getText(1).equalsIgnoreCase(formProperty.getName()) &&
                  item.getText(2).equalsIgnoreCase(formProperty.getType()) &&
                  item.getText(3).equalsIgnoreCase(formProperty.getValue()) &&
                  item.getText(4).equalsIgnoreCase("" + formProperty.isRequired()) &&
                  item.getText(5).equalsIgnoreCase("" + formProperty.isReadable()) &&
                  item.getText(6).equalsIgnoreCase("" + formProperty.isWriteable())) {
            
            found = true;
          }
        }
        if(found == false) {
          return true;
        }
      }
      
      return false;
      
    } else {
      return true;
    }
  }
 
  private FormProperty formPropertyExists(List<FormProperty> formPropertyList, String id) {
    if(formPropertyList == null) return null;
    for(FormProperty formProperty : formPropertyList) {
      if(id.equalsIgnoreCase(formProperty.getId())) {
        return formProperty;
      }
    }
    return null;
  }
  
  private void removeFormPropertiesNotInList(TableItem[] items, Object bo) {
    List<FormProperty> formPropertyList = getFormProperties(bo);
    List<FormProperty> toDeleteList = new ArrayList<FormProperty>();
    for (FormProperty formProperty : formPropertyList) {
      boolean found = false;
      for (TableItem item : items) {
        if(item.getText(0).equals(formProperty.getId())) {
          found = true;
          break;
        }
      }
      if(found == false) {
        toDeleteList.add(formProperty);
      }
    }
    for (FormProperty formProperty : toDeleteList) {
     getFormProperties(bo).remove(formProperty);
    }
  }
	
}
