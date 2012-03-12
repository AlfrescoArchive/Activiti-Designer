package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.CallActivity;
import org.activiti.designer.bpmn2.model.IOParameter;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class IOParameterEditor extends TableFieldEditor {
  
  protected Composite parent;
  public PictogramElement pictogramElement;
  public IDiagramEditor diagramEditor;
  public Diagram diagram;
  public boolean isInputParameters = false;
	
  public IOParameterEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Source", "Target"},
        new int[] {300, 300}, parent);
    this.parent = parent;
  }

  public void initialize(List<IOParameter> parameterList) {
    removeTableItems();
    if(parameterList == null || parameterList.size() == 0) return;
    for (IOParameter parameter : parameterList) {
      addTableItem(parameter);
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
  
  protected void addTableItem(IOParameter parameter) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, parameter.getSource());
      tableItem.setText(1, parameter.getTarget());
    }
  }

  @Override
  protected String[] getNewInputObject() {
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems());
    dialog.open();
    if(dialog.source != null && dialog.source.length() > 0 &&
            dialog.target != null && dialog.target.length() > 0) {
      return new String[] { dialog.source, dialog.target};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems(), 
            item.getText(0), item.getText(1));
    dialog.open();
    if(dialog.source != null && dialog.source.length() > 0 &&
            dialog.target != null && dialog.target.length() > 0) {
      return new String[] { dialog.source, dialog.target};
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
  
  private void saveFormProperties() {
    if (pictogramElement != null) {
      final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
      if (bo == null) {
        return;
      }
      List<IOParameter> parameterList = null;
      if(isInputParameters == true) {
        parameterList = ((CallActivity) bo).getInParameters();
      } else {
        parameterList = ((CallActivity) bo).getOutParameters();
      }
      if(parametersChanged(parameterList, getItems()) == false) {
        return;
      }
      TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
      ActivitiUiUtil.runModelChange(new Runnable() {
        public void run() {
        	List<IOParameter> newParameterList = new ArrayList<IOParameter>();
        	
          List<IOParameter> parameterList = null;
          if(isInputParameters == true) {
            parameterList = ((CallActivity) bo).getInParameters();
          } else {
            parameterList = ((CallActivity) bo).getOutParameters();
          }
          for (TableItem item : getItems()) {
            String source = item.getText(0);
            String target = item.getText(1);
            if(source != null && source.length() > 0 &&
                    target != null && target.length() > 0) {
              
              IOParameter parameter = parameterExists(parameterList, source, target);
              if(parameter != null) {
                parameter.setSource(source);
                parameter.setTarget(target);
                newParameterList.add(parameter);
              } else {
                IOParameter newParameter = new IOParameter();
                newParameter.setSource(source);
                newParameter.setTarget(target);
                newParameterList.add(newParameter);
              }
            }
          }
          if(isInputParameters == true) {
            ((CallActivity) bo).getInParameters().clear();
            ((CallActivity) bo).getInParameters().addAll(newParameterList);
          } else {
            ((CallActivity) bo).getOutParameters().clear();
            ((CallActivity) bo).getOutParameters().addAll(newParameterList);
          }
        }
      }, editingDomain, "Model Update");
    }
  }
  
  private boolean parametersChanged(List<IOParameter> parameterList, TableItem[] items) {
    boolean noPropertySaved = false;
    boolean nothingInTable = false;
    if(parameterList == null || parameterList.size() == 0) {
      noPropertySaved = true;
    }
    if(items == null || items.length == 0) {
      nothingInTable = true;
    }
    if(noPropertySaved && nothingInTable) {
      return false;
    } else if(noPropertySaved == false && nothingInTable == false) {
      
    	for(int i = 0; i < parameterList.size(); i++) {
    		IOParameter parameter = parameterList.get(i);
      
        boolean found = false;
        if(items.length > i) {
        	TableItem item = items[i];
          if(item.getText(0).equalsIgnoreCase(parameter.getSource()) &&
                  item.getText(1).equalsIgnoreCase(parameter.getTarget())) {
            
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
        if(parameterList.size() > i) {
        	IOParameter parameter = parameterList.get(i);
      
          if(item.getText(0).equalsIgnoreCase(parameter.getSource()) &&
                  item.getText(1).equalsIgnoreCase(parameter.getTarget())) {
            
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
 
  private IOParameter parameterExists(List<IOParameter> parameterList, String source, String target) {
    if(parameterList == null) return null;
    for(IOParameter parameter : parameterList) {
      if(source.equalsIgnoreCase(parameter.getSource()) &&
              target.equalsIgnoreCase(parameter.getTarget())) {
        return parameter;
      }
    }
    return null;
  }
}
