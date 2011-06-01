package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.IOParameter;
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
              } else {
                IOParameter newParameter = Bpmn2Factory.eINSTANCE.createIOParameter();
                newParameter.setSource(source);
                newParameter.setTarget(target);
                parameterList.add(newParameter);
              }
            }
          }
          removeParametersNotInList(getItems(), (CallActivity) bo, isInputParameters);
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
      
      for(IOParameter parameter : parameterList) {
        boolean found = false;
        for (TableItem item : items) {
          if(item.getText(0).equalsIgnoreCase(parameter.getSource()) &&
                  item.getText(1).equalsIgnoreCase(parameter.getTarget())) {
            
            found = true;
          }
        }
        if(found == false) {
          return true;
        }
      }
      
      for (TableItem item : items) {
        boolean found = false;
        for(IOParameter parameter : parameterList) {
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
  
  private void removeParametersNotInList(TableItem[] items, CallActivity callActivity, boolean isInput) {
    List<IOParameter> toDeleteList = new ArrayList<IOParameter>();
    List<IOParameter> parameterList = null;
    if(isInput == true) {
      parameterList = callActivity.getInParameters();
    } else {
      parameterList = callActivity.getOutParameters();
    }
    for (IOParameter parameter : parameterList) {
      boolean found = false;
      for (TableItem item : items) {
        if(item.getText(0).equals(parameter.getSource()) &&
                item.getText(1).equals(parameter.getTarget())) {
          found = true;
          break;
        }
      }
      if(found == false) {
        toDeleteList.add(parameter);
      }
    }
    for (IOParameter parameter : toDeleteList) {
      if(isInput == true) {
        callActivity.getInParameters().remove(parameter);
      } else {
        callActivity.getOutParameters().remove(parameter);;
      }
    }
  }
	
}
