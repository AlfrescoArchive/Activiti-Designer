package org.activiti.designer.property.ui;

import org.activiti.bpmn.model.ValuedDataObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public class DataPropertyDialog extends Dialog implements ITabbedPropertyConstants {
	
	public String id;
	public String name;
	public String type;
	public String value;
	
	protected ValuedDataObject savedDataProperty;

	public DataPropertyDialog(Shell parent, TableItem[] fieldList) {
		// Pass the default styles here
		this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
	}
	
	public DataPropertyDialog(Shell parent, TableItem[] fieldList, ValuedDataObject savedDataProperty) {
    // Pass the default styles here
    this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    this.savedDataProperty = savedDataProperty;
  }

	public DataPropertyDialog(Shell parent, TableItem[] fieldList, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Data property configuration");
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		shell.setText(getText());
		shell.setSize(700, 300);
		Point location = getParent().getShell().getLocation();
		Point size = getParent().getShell().getSize();
		shell.setLocation((location.x + size.x - 300) / 2, (location.y + size.y - 150) / 2);
		createContents(shell);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);
		FormData data;


		final Text idText = createText(savedDataProperty != null ? savedDataProperty.getId() : "", shell, null);
		createLabel("Id", shell, idText);

		final Text nameText = createText(savedDataProperty != null ? savedDataProperty.getName() : "", shell, idText);
		createLabel("Name", shell, nameText);

		final Combo typeDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		typeDropDown.add("string");
		typeDropDown.add("boolean");
		typeDropDown.add("datetime");
		typeDropDown.add("double");
		typeDropDown.add("int");
		typeDropDown.add("long");
		typeDropDown.add("string");
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(50, 0);
		data.top = new FormAttachment(nameText, VSPACE);
		typeDropDown.setLayoutData(data);
		typeDropDown.select(0);
	    createLabel("Type", shell, typeDropDown);

		final Text valueText = createText(savedDataProperty != null ? savedDataProperty.getValue().toString() : "", shell, typeDropDown);
		createLabel("Value", shell, valueText);

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(50, 0);
		data.top = new FormAttachment(valueText, 20);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(cancel, -HSPACE);
		data.top = new FormAttachment(cancel, 0, SWT.TOP);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if(idText.getText() == null || idText.getText().length() == 0) {
					MessageDialog.openError(shell, "Validation error", "ID must be filled.");
					return;
				}
				id = idText.getText();
				name = nameText.getText();
				type = typeDropDown.getText();
				value = valueText.getText();
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}

	private Text createText(String saved, Shell shell, Control control) {
		Text textField = new Text(shell, SWT.BORDER);
		if(saved != null) {
			textField.setText(saved);
		}
		FormData data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(70, 0);
		if(control != null) {
			data.top = new FormAttachment(control, 10);
		} else {
			data.top = new FormAttachment(0, 10);
		}
		textField.setLayoutData(data);
		return textField;
	}

	private void createLabel(String text, Shell shell, Control control) {
		CLabel idLabel = new CLabel(shell, SWT.NONE);
		idLabel.setText(text);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(control, -HSPACE);
		data.top = new FormAttachment(control, 0, SWT.TOP);
		idLabel.setLayoutData(data);
		idLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}
}
