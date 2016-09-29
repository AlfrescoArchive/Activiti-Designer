/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class MessageDefinitionEditor extends TableFieldEditor {

  public Diagram diagram;
  public TransactionalEditingDomain editingDomain;
	protected Composite parent;
	protected List<Message> messages;
	
	public MessageDefinitionEditor(String key, Composite parent) {

		super(key, "", new String[] {"Id", "Name"}, new int[] {150, 200, 200}, parent);
		this.parent = parent;
	}

	public void initialize(Collection<Message> messages) {
		removeTableItems();
		this.messages = new ArrayList<Message>(messages);
		if (messages == null || messages.size() == 0) return;
		for (Message message : messages) {
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
		if (table != null) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, message.getId() != null ? message.getId() : "");
			tableItem.setText(1, message.getName() != null ? message.getName() : "");
		}
	}

	@Override
	protected String[] getNewInputObject() {
	  MessageDefinitionDialog dialog = new MessageDefinitionDialog(parent.getShell(), getItems());
		dialog.open();
		if (StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
		  saveNewObject(dialog);
			return new String[] {dialog.id, dialog.name};
		} else {
			return null;
		}
	}

	@Override
	protected String[] getChangedInputObject(TableItem item) {
		int index = table.getSelectionIndex();
		
		MessageDefinitionDialog dialog = new MessageDefinitionDialog(parent.getShell(), getItems(), 
				messages.get(table.getSelectionIndex()));
		dialog.open();
		if (StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
		  saveChangedObject(dialog, index);
			return new String[] {dialog.id, dialog.name};
		} else {
			return null;
		}
	}

	@Override
	protected void removedItem(final int index) {
		if (index >= 0 && index < messages.size()) {
		  final Runnable runnable = new Runnable() {
	      public void run() {
	        messages.remove(index);
	        getBpmnModel().setMessages(messages);
	      }
	    };
	    runModelChange(runnable);
		  initialize(messages);
		}
	}

	protected void saveNewObject(final MessageDefinitionDialog dialog) {
		// verify that id is unique
		if (!isUnique(dialog.id)) {
			MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
			return;
		}

		// Perform the changes on the updatable BO instead of the original
		final Message newMessage = new Message();
		newMessage.setId(dialog.id);
		newMessage.setName(dialog.name);
		
		final Runnable runnable = new Runnable() {
      public void run() {
        messages.add(newMessage);
        getBpmnModel().addMessage(newMessage);
      }
    };
    runModelChange(runnable);
		initialize(messages);
	}

	protected void saveChangedObject(final MessageDefinitionDialog dialog, final int index) {
		final Message originalMessage = messages.get(index);

    // verify that id is unique
		if (!dialog.id.equals(originalMessage.getId()) && !isUnique(dialog.id)) {
			MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
			return;
		}

		// check to see if any values actually changed, or it is a no op
    final Message changedMessage = originalMessage.clone();
    changedMessage.setId(dialog.id);
    changedMessage.setName(dialog.name);

    if (!changedMessage.equals(originalMessage)) {
      final Runnable runnable = new Runnable() {
        public void run() {
          messages.set(index, changedMessage);
          getBpmnModel().setMessages(messages);
		  updateMessageCatchingEvents(originalMessage,changedMessage);
        }


	  };
      runModelChange(runnable);
      initialize(messages);
		}
	}

	private void updateMessageCatchingEvents(Message originalMessage, Message changedMessage) {
		BpmnModel model=getBpmnModel();
		for (Process process:model.getProcesses()) {
			for (FlowElement element:process.getFlowElements()) {
				if (element instanceof SubProcess) {
					updateMessageCatchingEventsInSubProcess((SubProcess)element,originalMessage,changedMessage);
				} else {
					updateMessageRef(originalMessage, changedMessage, element);
				}
			}
		}
	}

	private void updateMessageCatchingEventsInSubProcess(SubProcess subProcess, Message originalMessage, Message changedMessage) {
		for (FlowElement element: subProcess.getFlowElements()) {
			if (element instanceof SubProcess) {
				updateMessageCatchingEventsInSubProcess((SubProcess)element,originalMessage,changedMessage);
			} else {
				updateMessageRef(originalMessage, changedMessage, element);
			}
		}
	}


	private void updateMessageRef(Message originalMessage, Message changedMessage, FlowElement element) {
		if (element instanceof IntermediateCatchEvent) {
            IntermediateCatchEvent event=(IntermediateCatchEvent)element;
			if (event.getEventDefinitions().get(0) != null) {
				MessageEventDefinition eventDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
	            if (originalMessage.getId().equals(eventDefinition.getMessageRef())) {
					eventDefinition.setMessageRef(changedMessage.getId());
				}
			}
		}
	}

	@Override
	protected void upPressed() {
		final int index = table.getSelectionIndex();
		// Perform the changes on the updatable BO instead of the original
		final Runnable runnable = new Runnable() {
      public void run() {
        Message removedMessage = messages.remove(index);
        messages.add(index - 1, removedMessage);
        getBpmnModel().setMessages(messages);
      }
    };
    runModelChange(runnable);
		super.upPressed();
	}

	@Override
	protected void downPressed() {
		final int index = table.getSelectionIndex();
		// Perform the changes on the updatable BO instead of the original
		final Runnable runnable = new Runnable() {
      public void run() {
        Message removedMessage = messages.remove(index);
        messages.add(index + 1, removedMessage);
        getBpmnModel().setMessages(messages);
      }
    };
    runModelChange(runnable);
		super.downPressed();
	}

	@Override
	protected boolean isTableChangeEnabled() {
		return false;
	}
	
	protected void runModelChange(Runnable runnable) {
    ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
	}
	
	protected BpmnModel getBpmnModel() {
	  BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    BpmnModel model = memoryModel.getBpmnModel();
    return model;
	}
	
	protected boolean isUnique(String newId) {
		// verify that id is unique
		for (Message message : messages) {
			if (message.getId().equals(newId)) {
				return false;
			}
		}
		return true;
	}
}
