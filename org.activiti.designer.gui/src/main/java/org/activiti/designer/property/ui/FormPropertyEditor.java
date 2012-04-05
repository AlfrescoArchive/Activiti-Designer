package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.FormValue;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
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
    
    super(key, "", new String[] {"Id", "Name", "Type", "Value", "Expression", "Variable", "Default", "Pattern", "Required", "Readable", "Writeable", "Form values"},
        new int[] {60, 100, 60, 80, 100, 80, 80, 60, 60, 60, 60, 120}, parent);
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
      tableItem.setText(1, formProperty.getName() != null ? formProperty.getName() : "");
      tableItem.setText(2, formProperty.getType() != null ? formProperty.getType() : "");
      tableItem.setText(3, formProperty.getValue() != null ? formProperty.getValue() : "");
      tableItem.setText(4, formProperty.getExpression() != null ? formProperty.getExpression() : "");
      tableItem.setText(5, formProperty.getVariable() != null ? formProperty.getVariable() : "");
      tableItem.setText(6, formProperty.getDefaultExpression() != null ? formProperty.getDefaultExpression() : "");
      tableItem.setText(7, formProperty.getDatePattern() != null ? formProperty.getDatePattern() : "");
      tableItem.setText(8, formProperty.getRequired() != null ? formProperty.getRequired().toString().toLowerCase() : "");
      tableItem.setText(9, formProperty.getReadable() != null ? formProperty.getReadable().toString().toLowerCase() : "");
      tableItem.setText(10, formProperty.getWriteable() != null ? formProperty.getWriteable().toString().toLowerCase() : "");
      
      String formValuesString = "";
      for(int i = 0; i < formProperty.getFormValues().size(); i++) {
      	FormValue formValue = formProperty.getFormValues().get(i);
      	if(i > 0) {
      		formValuesString += ";";
      	}
      	formValuesString += formValue.getId() + ":" + formValue.getName();
      }
      tableItem.setText(11, formValuesString);
    }
  }

  @Override
  protected String[] getNewInputObject() {
    FormPropertyDialog dialog = new FormPropertyDialog(parent.getShell(), getItems());
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {
      return new String[] { dialog.id, dialog.name, dialog.type, dialog.value,
      				dialog.expression, dialog.variable, dialog.defaultExpression, dialog.datePattern,
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
            item.getText(5), item.getText(6), item.getText(7), item.getText(8), item.getText(9), item.getText(10), item.getText(11));
    dialog.open();
    if(dialog.id != null && dialog.id.length() > 0) {      
      return new String[] {dialog.id, dialog.name, dialog.type, dialog.value,
      				dialog.expression, dialog.variable, dialog.defaultExpression, dialog.datePattern,
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
      
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {
        public void run() {
        	List<FormProperty> newFormList = new ArrayList<FormProperty>();
          for (TableItem item : getItems()) {
            String id = item.getText(0);
            String name = item.getText(1);
            String type = item.getText(2);
            String value = item.getText(3);
            String expression = item.getText(4);
            String variable = item.getText(5);
            String defaultExpression = item.getText(6);
            String datePattern = item.getText(7);
            String required = item.getText(8);
            String readable = item.getText(9);
            String writeable = item.getText(10);
            String formValues = item.getText(11);
            if(id != null && id.length() > 0) {
              
              FormProperty newFormProperty = new FormProperty();
              newFormProperty.setId(id);
              newFormProperty.setName(name);
              newFormProperty.setType(type);
              newFormProperty.setValue(value);
              newFormProperty.setExpression(expression);
              newFormProperty.setVariable(variable);
              newFormProperty.setDefaultExpression(defaultExpression);
              newFormProperty.setDatePattern(datePattern);
              if(StringUtils.isNotEmpty(required)) {
                newFormProperty.setRequired(Boolean.valueOf(required.toLowerCase()));
              } else {
              	newFormProperty.setRequired(null);
              }
              if(StringUtils.isNotEmpty(readable)) {
                newFormProperty.setReadable(Boolean.valueOf(readable.toLowerCase()));
              } else {
              	newFormProperty.setReadable(null);
              }
              if(StringUtils.isNotEmpty(writeable)) {
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
}
