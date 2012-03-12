package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.FormValue;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class FormPropertyEditor extends TableFieldEditor {
  
  protected Composite parent;
  public PictogramElement pictogramElement;
  public IDiagramEditor diagramEditor;
  public Diagram diagram;
	
  public FormPropertyEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Id", "Name", "Type", "Value/Expression", "Required", "Readable", "Writeable", "Form values"},
        new int[] {100, 100, 100, 200, 100, 100, 100, 200}, parent);
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
      if(formProperty.getName() != null) {
      	tableItem.setText(1, formProperty.getName());
      } else {
      	tableItem.setText(1, "");
      }
      
      if(formProperty.getType() != null) {
      	tableItem.setText(2, formProperty.getType());
	    } else {
	    	tableItem.setText(2, "");
	    }
      
      if(formProperty.getValue() != null) {
      	tableItem.setText(3, formProperty.getValue());
      } else {
	    	tableItem.setText(3, "");
	    }
      
      if(formProperty.getRequired() != null) {
      	tableItem.setText(4, "" + formProperty.getRequired().toString().toLowerCase());
      } else {
      	tableItem.setText(4, "");
      }
      if(formProperty.getReadable() != null) {
      	tableItem.setText(5, "" + formProperty.getReadable().toString().toLowerCase());
      } else {
      	tableItem.setText(5, "");
      }
      if(formProperty.getWriteable() != null) {
      	tableItem.setText(6, "" + formProperty.getWriteable().toString().toLowerCase());
      } else {
      	tableItem.setText(6, "");
      }
      String formValuesString = "";
      for(int i = 0; i < formProperty.getFormValues().size(); i++) {
      	FormValue formValue = formProperty.getFormValues().get(i);
      	if(i > 0) {
      		formValuesString += ";";
      	}
      	formValuesString += formValue.getId() + ":" + formValue.getName();
      }
      tableItem.setText(7, formValuesString);
    }
  }

  @Override
  protected String[] getNewInputObject() {
    FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems());
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {
      return new String[] { dialog.id, dialog.name, dialog.type, dialog.value,
              dialog.required.toLowerCase(), dialog.readable.toLowerCase(), 
              dialog.writeable.toLowerCase(), dialog.formValues};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
    FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems(), 
    				item.getText(0), item.getText(1), item.getText(2), item.getText(3), item.getText(4),
            item.getText(5), item.getText(6), item.getText(7));
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {      
      return new String[] {dialog.id, dialog.name, dialog.type, dialog.value, 
              dialog.required.toLowerCase(), dialog.readable.toLowerCase(), 
              dialog.writeable.toLowerCase(), dialog.formValues};
    } else {
      return null;
    }
  }
  
  @Override
  protected void removedItem(int index) {
	  // TODO Auto-generated method stub 
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
  
  private void setFormProperties(Object bo, List<FormProperty> formPropertyList) {
    if(bo instanceof UserTask) {
      ((UserTask) bo).getFormProperties().clear();
      ((UserTask) bo).getFormProperties().addAll(formPropertyList);
    } else if(bo instanceof StartEvent) {
    	((StartEvent) bo).getFormProperties().clear();
      ((StartEvent) bo).getFormProperties().addAll(formPropertyList);
    }
  }
  
  private void saveFormProperties() {
    if (pictogramElement != null) {
      final Object bo = ModelHandler.getModel(EcoreUtil.getURI(diagram)).getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
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
        	List<FormProperty> newFormList = new ArrayList<FormProperty>();
          for (TableItem item : getItems()) {
            String id = item.getText(0);
            String name = item.getText(1);
            String type = item.getText(2);
            String value = item.getText(3);
            String required = item.getText(4);
            String readable = item.getText(5);
            String writeable = item.getText(6);
            String formValues = item.getText(7);
            if(id != null && id.length() > 0) {
              
              FormProperty newFormProperty = new FormProperty();
              newFormProperty.setId(id);
              newFormProperty.setName(name);
              newFormProperty.setType(type);
              newFormProperty.setValue(value);
              if(required != null && required.length() > 0) {
                newFormProperty.setRequired(Boolean.valueOf(required.toLowerCase()));
              } else {
              	newFormProperty.setRequired(null);
              }
              if(readable != null && readable.length() > 0) {
                newFormProperty.setReadable(Boolean.valueOf(readable.toLowerCase()));
              } else {
              	newFormProperty.setReadable(null);
              }
              if(writeable != null && writeable.length() > 0) {
                newFormProperty.setWriteable(Boolean.valueOf(writeable.toLowerCase()));
              } else {
              	newFormProperty.setWriteable(null);
              }
              
              List<FormValue> formValueList = new ArrayList<FormValue>();
              if(formValues != null && formValues.length() > 0) {
              	String[] formValueArray = formValues.split(";");
              	if(formValueArray != null) {
              		for(String formValue : formValueArray) {
              			FormValue formValueObj = new FormValue();
              			formValueObj.setId(formValue.substring(0, formValue.lastIndexOf(":")));
              			formValueObj.setName(formValue.substring(formValue.lastIndexOf(":") + 1));
              			formValueList.add(formValueObj);
              		}
              	}
              }
              newFormProperty.getFormValues().addAll(formValueList);
              
              newFormList.add(newFormProperty);
            }
          }
          setFormProperties(bo, newFormList);
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
      
      for(int i = 0; i < formPropertyList.size(); i++) {
      	FormProperty formProperty = formPropertyList.get(i);
        boolean found = false;
        if(items.length > i) {
        	TableItem item = items[i];
          if(item.getText(0).equalsIgnoreCase(formProperty.getId()) &&
                  item.getText(1).equalsIgnoreCase(formProperty.getName()) &&
                  item.getText(2).equalsIgnoreCase(formProperty.getType()) &&
                  item.getText(3).equalsIgnoreCase(formProperty.getValue()) &&
                  item.getText(4).equalsIgnoreCase("" + formProperty.getRequired()) &&
                  item.getText(5).equalsIgnoreCase("" + formProperty.getReadable()) &&
                  item.getText(6).equalsIgnoreCase("" + formProperty.getWriteable())) {
            
            found = true;
          }
        }
        if(found == false) {
          return true;
        }
      }
      
      for (int i = 0; i < items.length; i++) {
      	TableItem item = items[i];
        boolean found = false;
        if(formPropertyList.size() > i) {
        	FormProperty formProperty = formPropertyList.get(i);
        
	        if(item.getText(0).equalsIgnoreCase(formProperty.getId()) &&
			        item.getText(1).equalsIgnoreCase(formProperty.getName()) &&
			        item.getText(2).equalsIgnoreCase(formProperty.getType()) &&
			        item.getText(3).equalsIgnoreCase(formProperty.getValue()) &&
			        item.getText(4).equalsIgnoreCase("" + formProperty.getRequired()) &&
			        item.getText(5).equalsIgnoreCase("" + formProperty.getReadable()) &&
			        item.getText(6).equalsIgnoreCase("" + formProperty.getWriteable())) {
	            
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
}
