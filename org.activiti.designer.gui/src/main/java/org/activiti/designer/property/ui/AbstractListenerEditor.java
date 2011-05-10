package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.activiti.designer.model.FieldExtensionModel;
import org.activiti.designer.util.BpmnBOUtil;
import org.eclipse.bpmn2.ActivitiListener;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
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
	
	public AbstractListenerEditor(String key, Composite parent) {
		
    super(key, "", new String[] {"Listener implementation", "Type", "Event", "Fields"},
    		new int[] {200, 150, 100, 300}, parent);
    this.parent = parent;
	}
	
	public void initialize(List<ActivitiListener> listenerList) {
	  removeTableItems();
		if(listenerList == null || listenerList.size() == 0) return;
		for (ActivitiListener listener : listenerList) {
			addTableItem(listener.getImplementation(), listener.getImplementationType(),
			        listener.getEvent(), listener.getFieldExtensions());
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
	
	protected void addTableItem(String implementation, String implementationType, 
	        String event, List<FieldExtension> fieldExtensions) {
	  
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, implementation);
      tableItem.setText(1, implementationType);
      if(isSequenceFlow && event == null) {
        event = "take";
      }
      tableItem.setText(2, event);
      String fieldString = "";
      if(fieldExtensions != null) {
        for (FieldExtension fieldExtension : fieldExtensions) {
          if(fieldString.length() > 0) {
            fieldString += "± ";
          }
          fieldString += fieldExtension.getFieldname() + ":" + fieldExtension.getExpression();
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
			
			return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
		} else {
			return null;
		}
	}
	
	@Override
  protected String[] getChangedInputObject(TableItem item) {
	  AbstractListenerDialog dialog = getDialog(parent.getShell(), getItems(), 
            item.getText(1), item.getText(0), item.getText(2), item.getText(3));
    dialog.open();
    if(dialog.eventName != null && dialog.eventName.length() > 0 &&
            dialog.implementation != null && dialog.implementation.length() > 0 &&
            dialog.implementationType != null && dialog.implementationType.length() > 0) {
      
      return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
    } else {
      return null;
    }
  }
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items);
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items, 
	        String implementationType, String implementation, String event, String fieldString);
	
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
	
	@Override
	protected void selectionChanged() {
		super.selectionChanged();
		saveExecutionListeners();
	}
	
	private void saveExecutionListeners() {
		if (pictogramElement != null) {
		  final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
		  if (bo == null) {
        return;
      }
		  final List<ActivitiListener> listenerList = BpmnBOUtil.getListeners(bo);
		  if(listenersChanged(listenerList, getItems()) == false) {
		    return;
		  }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					for (TableItem item : getItems()) {
						String implementation = item.getText(0);
						String implementationType = item.getText(1);
						String event = item.getText(2);
						String fields = item.getText(3);
						if(implementation != null && implementation.length() > 0 &&
						        event != null && event.length() > 0) {
							
						  ActivitiListener listener = listenerExists(listenerList, event, implementationType, implementation);
							if(listener != null) {
							  listener.setEvent(event);
							  listener.setImplementation(implementation);
							  listener.setImplementationType(implementationType);
							  setFieldsInListener(listener, fields);
							} else {
							  ActivitiListener newListener = Bpmn2Factory.eINSTANCE.createActivitiListener();
							  newListener.setEvent(event);
							  newListener.setImplementationType(implementationType);
							  newListener.setImplementation(implementation);
							  setFieldsInListener(newListener, fields);
							  BpmnBOUtil.addListener(bo, newListener);
							}
						}
					}
					removeListenersNotInList(getItems(), bo);
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private boolean listenersChanged(List<ActivitiListener> listenerList, TableItem[] items) {
	  boolean noListenersSaved = false;
	  boolean nothingInTable = false;
	  if(listenerList == null || listenerList.size() == 0) {
	    noListenersSaved = true;
	  }
	  if(items == null || items.length == 0) {
	    nothingInTable = true;
	  }
	  if(noListenersSaved && nothingInTable) {
	    return false;
	  } else if(noListenersSaved == false && nothingInTable == false) {
	    
	    for(ActivitiListener listener : listenerList) {
	      boolean found = false;
	      for (TableItem item : items) {
	        if(item.getText(2).equalsIgnoreCase(listener.getEvent()) &&
	                item.getText(1).equalsIgnoreCase(listener.getImplementationType()) &&
	                item.getText(0).equalsIgnoreCase(listener.getImplementation()) &&
	                fieldExtensionsChanged(listener.getFieldExtensions(), item.getText(3)) == false) {
	          
	          found = true;
	        }
        }
	      if(found == false) {
	        return true;
	      }
	    }
	    
	    for (TableItem item : items) {
	      boolean found = false;
	      for(ActivitiListener listener : listenerList) {
          if(item.getText(2).equalsIgnoreCase(listener.getEvent()) &&
                  item.getText(1).equalsIgnoreCase(listener.getImplementationType()) &&
                  item.getText(0).equalsIgnoreCase(listener.getImplementation()) &&
                  fieldExtensionsChanged(listener.getFieldExtensions(), item.getText(3)) == false) {
            
            found = true;
          }
	      }
	      if(found == false) {
	        if(found == false) {
	          return true;
	        }
          return true;
        }
      }
	    
	    return false;
	    
	  } else {
	    return true;
	  }
	}
	
	private boolean fieldExtensionsChanged(List<FieldExtension> fieldList, String fieldString) {
	  boolean noFieldExtensionsSaved = false;
    boolean nothingInTable = false;
    if(fieldList == null || fieldList.size() == 0) {
      noFieldExtensionsSaved = true;
    }
    if(fieldString == null || fieldString.length() == 0) {
      nothingInTable = true;
    }
    if(noFieldExtensionsSaved && nothingInTable) {
      return false;
    } else if(noFieldExtensionsSaved == false && nothingInTable == false) {
      List<FieldExtensionModel> fieldModelList = BpmnBOUtil.getFieldModelList(fieldString);
      for(FieldExtension fieldExtension : fieldList) {
        boolean found = false;
        for (FieldExtensionModel fieldExtensionModel : fieldModelList) {
          if(fieldExtensionModel.fieldName.equalsIgnoreCase(fieldExtension.getFieldname()) &&
                  fieldExtensionModel.expression.equalsIgnoreCase(fieldExtension.getExpression())) {
            
            found = true;
          }
        }
        if(found == false) {
          return true;
        }
      }
      
      for (FieldExtensionModel fieldExtensionModel : fieldModelList) {
        boolean found = false;
        for(FieldExtension fieldExtension : fieldList) {
          if(fieldExtensionModel.fieldName.equalsIgnoreCase(fieldExtension.getFieldname()) &&
                  fieldExtensionModel.expression.equalsIgnoreCase(fieldExtension.getExpression())) {
            
            found = true;
          }
        }
        if(found == false) {
          if(found == false) {
            return true;
          }
          return true;
        }
      }
      
      return false;
      
    } else {
      return true;
    }
	}
	
	private void setFieldsInListener(ActivitiListener listener, String fieldString) {
	  if(fieldString == null || fieldString.length() == 0) {
	    if(listener != null && listener.getFieldExtensions() != null && 
	            listener.getFieldExtensions().size() > 0) {
	      
  	    removeFieldExtensionsNotInList(listener.getFieldExtensions(), null);
	    }
	    return;
	  }
	  String[] fieldStringList = fieldString.split("±");
	  for (String field : fieldStringList) {
	    String[] fieldExtensionStringList = field.split(":");
	    FieldExtension fieldExtension = fieldExtensionExists(listener.getFieldExtensions(), fieldExtensionStringList[0]);
	    if(fieldExtension == null) {
	      fieldExtension = Bpmn2Factory.eINSTANCE.createFieldExtension();
	      listener.getFieldExtensions().add(fieldExtension);
	    }
	    fieldExtension.setFieldname(fieldExtensionStringList[0]);
	    String expression = null;
	    for(int i = 1; i < fieldExtensionStringList.length; i++) {
	      if(i == 1) {
	        expression = fieldExtensionStringList[i];
	      } else {
	        expression += ":" + fieldExtensionStringList[i];
	      }
	    }
	    fieldExtension.setExpression(expression);
    }
	  removeFieldExtensionsNotInList(listener.getFieldExtensions(), fieldStringList);
	}
	
	private FieldExtension fieldExtensionExists(List<FieldExtension> fieldList, String fieldname) {
	  if(fieldList == null) return null;
	  for(FieldExtension fieldExtension : fieldList) {
      if(fieldname.equalsIgnoreCase(fieldExtension.getFieldname())) {
        return fieldExtension;
      }
    }
    return null;
	}
	
	private void removeFieldExtensionsNotInList(List<FieldExtension> fieldList, String[] fieldStringList) {
	  Iterator<FieldExtension> entryIterator = fieldList.iterator();
    while(entryIterator.hasNext()) {
      FieldExtension fieldExtension = entryIterator.next();
      boolean found = false;
      if(fieldStringList != null && fieldStringList.length > 0) {
        for (String field : fieldStringList) {
          String[] fieldExtensionStringList = field.split(":");
          if(fieldExtensionStringList[0].equals(fieldExtension.getFieldname())) {
            found = true;
            break;
          }
        }
      }
      if(found == false) {
        diagram.eResource().getContents().remove(fieldExtension);
        entryIterator.remove();
      }
    }
	}
	
	private ActivitiListener listenerExists(List<ActivitiListener> listenerList, String event, String implementationType, String implementation) {
		if(listenerList == null) return null;
		for(ActivitiListener listener : listenerList) {
			if(event.equalsIgnoreCase(listener.getEvent()) &&
			        implementationType.equalsIgnoreCase(listener.getImplementationType()) &&
			        implementation.equalsIgnoreCase(listener.getImplementation())) {
			  
				return listener;
			}
		}
		return null;
	}
	
	private void removeListenersNotInList(TableItem[] items, Object bo) {
	  List<ActivitiListener> listenerList = BpmnBOUtil.getListeners(bo);
		List<ActivitiListener> toDeleteList = new ArrayList<ActivitiListener>();
		for (ActivitiListener listener : listenerList) {
			boolean found = false;
			for (TableItem item : items) {
				if(item.getText(0).equals(listener.getImplementation()) &&
				        item.getText(1).equals(listener.getImplementationType()) &&
				        item.getText(2).equals(listener.getEvent())) {
					found = true;
					break;
				}
			}
			if(found == false) {
			  toDeleteList.add(listener);
			}
		}
		for (ActivitiListener listener : toDeleteList) {
		  BpmnBOUtil.removeExecutionListener(bo, listener);
    }
	}
	
}
