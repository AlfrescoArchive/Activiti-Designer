package org.activiti.designer.property.ui;

/**
 * @author Saeid Mirzaei
 */

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.Message;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class MessageEditor extends TableFieldEditor {
  
  protected Composite parent;
  public IDiagramEditor diagramEditor;
  public Diagram diagram;
	
  public MessageEditor(String key, Composite parent) {
    
    super(key, "", new String[] {"Id", "Name"},
        new int[] {200, 200}, parent);
    this.parent = parent;
  }

  public void initialize(List<Message> messageList) {
    removeTableItems();
    if(messageList == null || messageList.size() == 0) return;
    for (Message message : messageList) {
      addTableItem(message);
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
  
  protected void addTableItem(Message message) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, message.getId());
      tableItem.setText(1, message.getName());
    }
  }

  @Override
  protected String[] getNewInputObject() {
  	MessageDialog dialog = new MessageDialog(parent.getShell(), getItems());
    dialog.open();
    if(StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {
      return new String[] { dialog.id, dialog.name};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
  	MessageDialog dialog = new MessageDialog(parent.getShell(), getItems(), 
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
    saveMessages();
  }
  
  private void saveMessages() {
  	if(diagram == null) return;
    final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    if (model == null) {
      return;
    }
    
    TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    ActivitiUiUtil.runModelChange(new Runnable() {
      public void run() {
      	List<Message> newMessageList = new ArrayList<Message>();
        for (TableItem item : getItems()) {
          String id = item.getText(0);
          String name = item.getText(1);
          if(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(name)) {
            
          	Message newMessage = new Message();
          	newMessage.setId(id);
          	newMessage.setName(name);
          	newMessageList.add(newMessage);
          }
        }
        model.getMessages().clear();
        model.getMessages().addAll(newMessageList);
      }
    }, editingDomain, "Model Update");
  }
}
