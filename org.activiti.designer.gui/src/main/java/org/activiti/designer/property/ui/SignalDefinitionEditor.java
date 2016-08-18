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

import java.util.List;

import org.activiti.bpmn.model.Signal;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class SignalDefinitionEditor extends TableFieldEditor {

  public Diagram diagram;
  public TransactionalEditingDomain editingDomain;
	protected Composite parent;
	protected List<Signal> signals;
	
	public SignalDefinitionEditor(String key, Composite parent) {

		super(key, "", new String[] {"Id", "Name", "Scope"}, new int[] {150, 200, 200}, parent);
		this.parent = parent;
	}

	public void initialize(List<Signal> signals) {
		removeTableItems();
		this.signals = signals;
		if (signals == null || signals.size() == 0) return;
		for (Signal signal : signals) {
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

		if (table != null) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, signal.getId() != null ? signal.getId() : "");
			tableItem.setText(1, signal.getName() != null ? signal.getName() : "");
			tableItem.setText(2, signal.getScope() != null ? signal.getScope() : "global");
		}
	}

	@Override
	protected String[] getNewInputObject() {
	  SignalDefinitionDialog dialog = new SignalDefinitionDialog(parent.getShell(), getItems());
		dialog.open();
		if (StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
		  saveNewObject(dialog);
			return new String[] {dialog.id, dialog.name, dialog.scope};
		} else {
			return null;
		}
	}

	@Override
	protected String[] getChangedInputObject(TableItem item) {
		int index = table.getSelectionIndex();
		
		SignalDefinitionDialog dialog = new SignalDefinitionDialog(parent.getShell(), getItems(), 
				signals.get(table.getSelectionIndex()));
		dialog.open();
		if (StringUtils.isNotEmpty(dialog.id) && StringUtils.isNotEmpty(dialog.name)) {      
		  saveChangedObject(dialog, index);
			return new String[] {dialog.id, dialog.name, dialog.scope};
		} else {
			return null;
		}
	}

	@Override
	protected void removedItem(final int index) {
		if (index >= 0 && index < signals.size()) {
		  final Runnable runnable = new Runnable() {
	      public void run() {
	        signals.remove(index);
	      }
	    };
	    runModelChange(runnable);
		  initialize(signals);
		}
	}

	protected void saveNewObject(final SignalDefinitionDialog dialog) {
		// verify that id is unique
		if (!isUnique(dialog.id)) {
			MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
			return;
		}

		// Perform the changes on the updatable BO instead of the original
		final Signal newSignal = new Signal();
		newSignal.setId(dialog.id);
		newSignal.setName(dialog.name);
		newSignal.setScope(dialog.scope);
		
		final Runnable runnable = new Runnable() {
      public void run() {
        signals.add(newSignal);
      }
    };
    runModelChange(runnable);
		initialize(signals);
	}

	protected void saveChangedObject(final SignalDefinitionDialog dialog, final int index) {
		Signal originalSignal = signals.get(index);

    // verify that id is unique
		if (!dialog.id.equals(originalSignal.getId()) && !isUnique(dialog.id)) {
			MessageDialog.openError(parent.getShell(), "Validation error", "ID must be unique.");
			return;
		}

		// check to see if any values actually changed, or it is a no op
    final Signal changedSignal = originalSignal.clone();
    changedSignal.setId(dialog.id);
    changedSignal.setName(dialog.name);
    changedSignal.setScope(dialog.scope);

    if (!changedSignal.equals(originalSignal)) {
      final Runnable runnable = new Runnable() {
        public void run() {
          signals.set(index, changedSignal);
        }
      };
      runModelChange(runnable);
      initialize(signals);
		}
	}

	@Override
	protected void upPressed() {
		final int index = table.getSelectionIndex();
		// Perform the changes on the updatable BO instead of the original
		final Runnable runnable = new Runnable() {
      public void run() {
        Signal removedSignal = signals.remove(index);
        signals.add(index - 1, removedSignal);
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
        Signal removedSignal = signals.remove(index);
        signals.add(index + 1, removedSignal);
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
	
	protected boolean isUnique(String newId) {
		// verify that id is unique
		for (Signal signal : signals) {
			if (signal.getId().equals(newId)) {
				return false;
			}
		}
		return true;
	}
}
