package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.Signal;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class SignalEditor extends TableFieldEditor {
  
  protected Composite parent;
  public IDiagramEditor diagramEditor;
  public Diagram diagram;
	
  public SignalEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Id", "Name"},
        new int[] {200, 200}, parent);
    this.parent = parent;
  }

  public void initialize(List<Signal> signalList) {
    removeTableItems();
    if(signalList == null || signalList.size() == 0) return;
    for (Signal signal : signalList) {
      addTableItem(signal);
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
  
  protected void addTableItem(Signal signal) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, signal.getId());
      tableItem.setText(1, signal.getName());
    }
  }

  @Override
  protected String[] getNewInputObject() {
  	SignalDialog dialog = new SignalDialog(parent.getShell(), getItems());
    dialog.open();
    if(StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {
      return new String[] { dialog.id, dialog.name};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
  	SignalDialog dialog = new SignalDialog(parent.getShell(), getItems(), 
            item.getText(0), item.getText(1));
    dialog.open();
    if(StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {
      return new String[] { dialog.id, dialog.name};
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
    saveSignals();
  }
  
  private void saveSignals() {
  	if(diagram == null) return;
    final Process process = ModelHandler.getModel(EcoreUtil.getURI(diagram)).getProcess();
    if (process == null) {
      return;
    }
    
    TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    ActivitiUiUtil.runModelChange(new Runnable() {
      public void run() {
      	List<Signal> newSignalList = new ArrayList<Signal>();
        for (TableItem item : getItems()) {
          String id = item.getText(0);
          String name = item.getText(1);
          if(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(name)) {
            
          	Signal newSignal = new Signal();
          	newSignal.setId(id);
          	newSignal.setName(name);
          	newSignalList.add(newSignal);
          }
        }
        process.getSignals().clear();
        process.getSignals().addAll(newSignalList);
      }
    }, editingDomain, "Model Update");
  }
}
