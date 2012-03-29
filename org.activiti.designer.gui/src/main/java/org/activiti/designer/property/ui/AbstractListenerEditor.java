package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.designer.bpmn2.model.ActivitiListener;
import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.model.FieldExtensionModel;
import org.activiti.designer.util.BpmnBOUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;


public abstract class AbstractListenerEditor extends TableFieldEditor {
	
	protected Composite parent;
	public PictogramElement pictogramElement;
	public IDiagramEditor diagramEditor;
	public Diagram diagram;
	public boolean isSequenceFlow;
	private List<ActivitiListener> listenerList;
	
	public AbstractListenerEditor(String key, Composite parent) {
		
    super(key, "", new String[] {"Listener implementation", "Type", "Event", "Fields"},
    		new int[] {200, 150, 100, 300}, parent);
    this.parent = parent;
	}
	
	public void initialize(List<ActivitiListener> listenerList) {
	  removeTableItems();
	  this.listenerList = listenerList;
		if(listenerList == null || listenerList.size() == 0) return;
		for (ActivitiListener listener : listenerList) {
			addTableItem(listener);
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
	
	protected void addTableItem(ActivitiListener listener) {
	  
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, listener.getImplementation());
      tableItem.setText(1, listener.getImplementationType());
      String event = listener.getEvent();
      if(isSequenceFlow && listener.getEvent() == null) {
        event = "take";
      }
      tableItem.setText(2, event);
      String fieldString = "";
      if(listener.getFieldExtensions() != null) {
        for (FieldExtension fieldExtension : listener.getFieldExtensions()) {
          if(fieldString.length() > 0) {
            fieldString += "± ";
          }
          fieldString += fieldExtension.getFieldName() + ":" + fieldExtension.getExpression();
        }
      }
      tableItem.setText(3, fieldString);
    }
  }

	@Override
	protected String[] getNewInputObject() {
	  AbstractListenerDialog dialog = getDialog(parent.getShell(), getItems());
		dialog.open();
		if(dialog.eventName != null && dialog.eventName.length() > 0 &&
		        dialog.implementation != null && dialog.implementation.length() > 0 &&
		        dialog.implementationType != null && dialog.implementationType.length() > 0) {
			
			saveNewObject(dialog);
			return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
		} else {
			return null;
		}
	}
	
	@Override
  protected String[] getChangedInputObject(TableItem item) {
		
		int index = table.getSelectionIndex();
	  AbstractListenerDialog dialog = getDialog(parent.getShell(), getItems(), 
	  		listenerList.get(table.getSelectionIndex()));
    dialog.open();
    if(dialog.eventName != null && dialog.eventName.length() > 0 &&
            dialog.implementation != null && dialog.implementation.length() > 0 &&
            dialog.implementationType != null && dialog.implementationType.length() > 0) {
      
    	saveChangedObject(dialog, index);
      return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
    } else {
      return null;
    }
  }
	
	@Override
  protected void removedItem(int index) {
		if(index >= 0 && index < listenerList.size()) {
			saveRemovedObject(listenerList.get(index));
		}
  }
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items);
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items, 
	        ActivitiListener listener);
	
	private String getFieldString(List<FieldExtensionModel> fieldList) {
	  String fieldString = "";
    if(fieldList != null) {
      for (FieldExtensionModel fieldExtension : fieldList) {
        if(fieldString.length() > 0) {
          fieldString += ", ";
        }
        fieldString += fieldExtension.fieldName + ":" + fieldExtension.expression;
      }
    }
    return fieldString;
	}
	
	private void saveNewObject(final AbstractListenerDialog dialog) {
		if (pictogramElement != null) {
		  final Object bo = BpmnBOUtil.getExecutionListenerBO(pictogramElement, diagram);
		  if (bo == null) {
        return;
      }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
				  ActivitiListener newListener = new ActivitiListener();
				  newListener.setEvent(dialog.eventName);
				  newListener.setImplementationType(dialog.implementationType);
				  newListener.setImplementation(dialog.implementation);
				  if("alfrescoScriptType".equalsIgnoreCase(dialog.implementationType)) {
				  	newListener.setRunAs(dialog.runAs);
				  	newListener.setScriptProcessor(dialog.scriptProcessor);
				  }
				  setFieldsInListener(newListener, dialog.fieldExtensionList);
				  BpmnBOUtil.addListener(bo, newListener);
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private void saveChangedObject(final AbstractListenerDialog dialog, final int index) {
		if (pictogramElement != null) {
		  final Object bo = BpmnBOUtil.getExecutionListenerBO(pictogramElement, diagram);
		  if (bo == null) {
        return;
      }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					ActivitiListener listener = listenerList.get(index);
					if(listener != null) {
					  listener.setEvent(dialog.eventName);
					  listener.setImplementation(dialog.implementation);
					  listener.setImplementationType(dialog.implementationType);
					  if("alfrescoScriptType".equalsIgnoreCase(listener.getImplementationType())) {
					  	listener.setRunAs(dialog.runAs);
					  	listener.setScriptProcessor(dialog.scriptProcessor);
					  }
					  setFieldsInListener(listener, dialog.fieldExtensionList);
					  BpmnBOUtil.setListener(bo, listener, index);
					}
					
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private void saveRemovedObject(final ActivitiListener listener) {
		if (pictogramElement != null) {
		  final Object bo = BpmnBOUtil.getExecutionListenerBO(pictogramElement, diagram);
		  if (bo == null) {
        return;
		  }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					BpmnBOUtil.removeListener(bo, listener);
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private void setFieldsInListener(ActivitiListener listener, List<FieldExtensionModel> fieldList) {
	  if(listener != null) {
  		listener.getFieldExtensions().clear();
		  for (FieldExtensionModel fieldModel : fieldList) {
		    FieldExtension fieldExtension = new FieldExtension();
		    listener.getFieldExtensions().add(fieldExtension);
		    fieldExtension.setFieldName(fieldModel.fieldName);
		    fieldExtension.setExpression(fieldModel.expression);
	    }
	  }
	}
}
