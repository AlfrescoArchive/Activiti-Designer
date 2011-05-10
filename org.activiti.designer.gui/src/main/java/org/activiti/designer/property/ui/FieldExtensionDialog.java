package org.activiti.designer.property.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FieldExtensionDialog extends Dialog {
	
	public String fieldNameInput;
	public String fieldValueInput;
	private String savedFieldName;
	private String savedFieldValue;
	private TableItem[] fieldList;

	public FieldExtensionDialog(Shell parent, TableItem[] fieldList) {
		// Pass the default styles here
		this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public FieldExtensionDialog(Shell parent, TableItem[] fieldList, String savedFieldName, String savedFieldValue) {
    this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.savedFieldName = savedFieldName;
    this.savedFieldValue = savedFieldValue;
  }

	public FieldExtensionDialog(Shell parent, TableItem[] fieldList, int style) {
		// Let users override the default styles
		super(parent, style);
		this.fieldList = fieldList;
		setText("Field extension");
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		shell.setSize(700, 300);
		Point location = getParent().getShell().getLocation();
		Point size = getParent().getShell().getSize();
		shell.setLocation((location.x + size.x - 500) / 2, (location.y + size.y - 300) / 2);
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
		shell.setLayout(new GridLayout(2, false));

		Label fieldLabel = new Label(shell, SWT.NONE);
		fieldLabel.setText("Field name");
		fieldLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Text fieldText = new Text(shell, SWT.BORDER);
		if(savedFieldName != null) {
		  fieldText.setText(savedFieldName);
		}
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		fieldText.setLayoutData(data);
		
		Label valueLabel = new Label(shell, SWT.NONE);
		valueLabel.setText("Expression");
		valueLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Text valueText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		valueText.setSize(500, 150);
		if(savedFieldValue != null) {
		  valueText.setText(savedFieldValue);
    }
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 500;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		valueText.setLayoutData(data);

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if(fieldText.getText() == null || fieldText.getText().length() == 0) {
					MessageDialog.openError(shell, "Validation error", "Field name must be filled.");
					return;
				}
				if(valueText.getText() == null || valueText.getText().length() == 0) {
					MessageDialog.openError(shell, "Validation error", "Expression must be filled.");
					return;
				}
				if(fieldList != null) {
					for (TableItem item : fieldList) {
					  if(savedFieldName != null && savedFieldName.equals(item.getText(0)) &&
					          savedFieldValue != null && savedFieldValue.equals(item.getText(1))) {
		          continue;
		        }
						if(fieldText.getText().equals(item.getText(0))) {
							MessageDialog.openError(shell, "Validation error", "Field name is already used.");
							return;
						}
					}
				}
				fieldNameInput = fieldText.getText();
				fieldValueInput = valueText.getText();
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fieldNameInput = null;
				fieldValueInput = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
	}
}
