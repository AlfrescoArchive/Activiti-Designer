package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.property.ModelUpdater;
import org.activiti.designer.util.BpmnBOUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;


public abstract class AbstractListenerEditor extends TableFieldEditor {
	
  protected static final int EXECUTION_LISTENER = 1;
  protected static final int TASK_LISTENER = 2;
  
	protected Composite parent;
	protected int listenerType;
	protected ModelUpdater modelUpdater;
	
	public PictogramElement pictogramElement;
	public Diagram diagram;
	public boolean isSequenceFlow;
	private List<ActivitiListener> listenerList;
	
	public AbstractListenerEditor(String key, Composite parent, int listenerType, ModelUpdater modelUpdater) {
		
    super(key, "", new String[] {"Listener implementation", "Type", "Event", "Fields"},
    		new int[] {200, 150, 100, 300}, parent);
    this.parent = parent;
    this.listenerType = listenerType;
    this.modelUpdater = modelUpdater;
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
            fieldString += "|";
          }
          if (StringUtils.isNotEmpty(fieldExtension.getExpression())) {
            fieldString += fieldExtension.getFieldName() + ":" + fieldExtension.getExpression();
          } else {
            fieldString += fieldExtension.getFieldName() + ":" + fieldExtension.getStringValue();
          }
        }
      }
      tableItem.setText(3, fieldString);
    }
  }

	@Override
	protected String[] getNewInputObject() {
	  AbstractListenerDialog dialog = getDialog(parent.getShell(), getItems());
		dialog.open();
		if(StringUtils.isNotEmpty(dialog.eventName) && StringUtils.isNotEmpty(dialog.implementation)) {
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
    if(StringUtils.isNotEmpty(dialog.eventName) && StringUtils.isNotEmpty(dialog.implementation)) {
    	saveChangedObject(dialog, index);
      return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
    } else {
      return null;
    }
  }
	
	@Override
  protected void removedItem(int index) {
		if(index >= 0 && index < listenerList.size()) {
			saveRemovedObject(index);
		}
  }
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items);
	
	protected abstract AbstractListenerDialog getDialog(Shell shell, TableItem[] items, 
	        ActivitiListener listener);
	
	private String getFieldString(List<FieldExtension> fieldList) {
	  String fieldString = "";
    if(fieldList != null) {
      for (FieldExtension fieldExtension : fieldList) {
        if(fieldString.length() > 0) {
          fieldString += ", ";
        }
        if (StringUtils.isNotEmpty(fieldExtension.getExpression())) {
          fieldString += fieldExtension.getFieldName() + ":" + fieldExtension.getExpression();
        } else {
          fieldString += fieldExtension.getFieldName() + ":" + fieldExtension.getStringValue();
        }
      }
    }
    return fieldString;
	}
	
	private void saveNewObject(final AbstractListenerDialog dialog) {
		if (pictogramElement != null) {
      // Perform the changes on the updatable BO instead of the original
      Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
      ActivitiListener newListener = new ActivitiListener();
      newListener.setEvent(dialog.eventName);
      newListener.setImplementationType(dialog.implementationType);
      newListener.setImplementation(dialog.implementation);
      
      if(AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER.equalsIgnoreCase(dialog.implementation) ||
              AlfrescoScriptTask.ALFRESCO_SCRIPT_EXECUTION_LISTENER.equalsIgnoreCase(dialog.implementation)) {
        
        FieldExtension scriptExtension = new FieldExtension();
        scriptExtension.setFieldName("script");
        scriptExtension.setStringValue(dialog.script);
        newListener.getFieldExtensions().add(scriptExtension);
        
        FieldExtension runAsExtension = new FieldExtension();
        runAsExtension.setFieldName("runAs");
        runAsExtension.setStringValue(dialog.runAs);
        newListener.getFieldExtensions().add(runAsExtension);
        
        FieldExtension scriptProcessorExtension = new FieldExtension();
        scriptProcessorExtension.setFieldName("scriptProcessor");
        scriptProcessorExtension.setStringValue(dialog.scriptProcessor);
        newListener.getFieldExtensions().add(scriptProcessorExtension);
        
      } else {
        setFieldsInListener(newListener, dialog.fieldExtensionList);
      }
      
      if (listenerType == EXECUTION_LISTENER) {
        BpmnBOUtil.addExecutionListener(updatableBo, newListener, diagram);
      } else {
        ((UserTask) updatableBo).getTaskListeners().add(newListener);
      }
      modelUpdater.executeModelUpdater();
		}
	}
	
	private void saveChangedObject(final AbstractListenerDialog dialog, final int index) {
		if (pictogramElement != null) {
		  Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
			
		  ActivitiListener listener = listenerList.get(index);
			if (listener != null) {
			  listener.setEvent(dialog.eventName);
			  listener.setImplementation(dialog.implementation);
			  listener.setImplementationType(dialog.implementationType);
			  
			  if(AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER.equalsIgnoreCase(dialog.implementation) ||
                AlfrescoScriptTask.ALFRESCO_SCRIPT_EXECUTION_LISTENER.equalsIgnoreCase(dialog.implementation)) {
			    
			    List<FieldExtension> extensionList = listener.getFieldExtensions();
			    FieldExtension scriptExtension = null;
			    FieldExtension runAsExtension = null;
			    FieldExtension scriptProcessorExtension = null;
		      for (FieldExtension fieldExtension : extensionList) {
		        if ("script".equalsIgnoreCase(fieldExtension.getFieldName())) {
		          scriptExtension = fieldExtension;
		        } else if ("runAs".equalsIgnoreCase(fieldExtension.getFieldName())) {
              runAsExtension = fieldExtension;
            } else if ("scriptProcessor".equalsIgnoreCase(fieldExtension.getFieldName())) {
              scriptProcessorExtension = fieldExtension;
            }
		      }
		      
		      if (scriptExtension != null) {
		        scriptExtension.setStringValue(dialog.script);
		      } else {
		        scriptExtension = new FieldExtension();
		        scriptExtension.setFieldName("script");
		        scriptExtension.setStringValue(dialog.script);
		        listener.getFieldExtensions().add(scriptExtension);
		      }
		      
		      if (runAsExtension != null) {
		        runAsExtension.setStringValue(dialog.runAs);
          } else {
            runAsExtension = new FieldExtension();
            runAsExtension.setFieldName("runAs");
            runAsExtension.setStringValue(dialog.runAs);
            listener.getFieldExtensions().add(runAsExtension);
          }
		      
		      if (scriptProcessorExtension != null) {
		        scriptProcessorExtension.setStringValue(dialog.scriptProcessor);
          } else {
            scriptProcessorExtension = new FieldExtension();
            scriptProcessorExtension.setFieldName("scriptProcessor");
            scriptProcessorExtension.setStringValue(dialog.scriptProcessor);
            listener.getFieldExtensions().add(scriptProcessorExtension);
          }
			    
			  } else {
			    setFieldsInListener(listener, dialog.fieldExtensionList);
			  }
			  
			  if (listenerType == EXECUTION_LISTENER) {
			    BpmnBOUtil.setExecutionListener(updatableBo, listener, index, diagram);
			  } else {
			    ((UserTask) updatableBo).getTaskListeners().set(index, listener);
			  }
			  
			  modelUpdater.executeModelUpdater();
			}
		}
	}
	
	private void saveRemovedObject(int index) {
		if (pictogramElement != null) {
		  Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
		  
		  if (listenerType == EXECUTION_LISTENER) {
		    BpmnBOUtil.removeExecutionListener(updatableBo, index, diagram);
		  } else {
		    ((UserTask) updatableBo).getTaskListeners().remove(index);
		  }
		  
		  modelUpdater.executeModelUpdater();
		}
	}
	
	@Override
  protected void upPressed() {
	  final int index = table.getSelectionIndex();
	  Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
    List<ActivitiListener> boListeners = null;
    if (listenerType == EXECUTION_LISTENER) {
      boListeners = BpmnBOUtil.getExecutionListeners(updatableBo, diagram);
    } else {
      boListeners = ((UserTask) updatableBo).getTaskListeners();
    }
    ActivitiListener listener = boListeners.remove(index);
    boListeners.add(index - 1, listener);
    listenerList = boListeners;
    modelUpdater.executeModelUpdater();
    super.upPressed();
  }

  @Override
  protected void downPressed() {
    final int index = table.getSelectionIndex();
    Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
    List<ActivitiListener> boListeners = null;
    if (listenerType == EXECUTION_LISTENER) {
      boListeners = BpmnBOUtil.getExecutionListeners(updatableBo, diagram);
    } else {
      boListeners = ((UserTask) updatableBo).getTaskListeners();
    }
    ActivitiListener listener = boListeners.remove(index);
    boListeners.add(index + 1, listener);
    listenerList = boListeners;
    modelUpdater.executeModelUpdater();
    super.downPressed();
  }

  private void setFieldsInListener(ActivitiListener listener, List<FieldExtension> fieldList) {
	  if(listener != null) {
  		listener.getFieldExtensions().clear();
		  for (FieldExtension fieldModel : fieldList) {
		    FieldExtension fieldExtension = new FieldExtension();
		    listener.getFieldExtensions().add(fieldExtension);
		    fieldExtension.setFieldName(fieldModel.getFieldName());
		    fieldExtension.setStringValue(fieldModel.getStringValue());
		    fieldExtension.setExpression(fieldModel.getExpression());
	    }
	  }
	}
}
