package org.activiti.designer.property.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public class FormPropertyDialog extends Dialog implements ITabbedPropertyConstants {
	
	public String id;
	public String name;
	public String type;
	public String value;
	public String expression;
	public String variable;
	public String defaultExpression;
	public String datePattern;
	public String required;
	public String readable;
	public String writeable;
	public String formValues;
	
	protected String savedId;
	protected String savedName;
	protected String savedType;
	protected String savedValue;
	protected String savedExpression;
	protected String savedVariable;
	protected String savedDefaultExpression;
	protected String savedDatePattern;
	protected String savedRequired;
	protected String savedReadable;
	protected String savedWriteable;
	protected String savedFormValues;

	public FormPropertyDialog(Shell parent, TableItem[] fieldList) {
		// Pass the default styles here
		this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public FormPropertyDialog(Shell parent, TableItem[] fieldList, String savedId, 
	        String savedName, String savedType, String savedValue, 
	        String savedExpression, String savedVariable, String savedDefaultExpression, String savedDatePattern,
	        String savedRequired, String savedReadable, String savedWriteable, String savedFormValues) {
    // Pass the default styles here
    this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.savedId = savedId;
    this.savedName = savedName;
    this.savedType = savedType;
    this.savedValue = savedValue;
    this.savedExpression = savedExpression;
    this.savedVariable = savedVariable;
    this.savedDefaultExpression = savedDefaultExpression;
    this.savedDatePattern = savedDatePattern;
    this.savedRequired = savedRequired;
    this.savedReadable = savedReadable;
    this.savedWriteable = savedWriteable;
    this.savedFormValues = savedFormValues;
  }

	public FormPropertyDialog(Shell parent, TableItem[] fieldList, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Form property configuration");
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
		shell.setSize(700, 650);
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
	  
	  final Text idText = createText(savedId, shell, null);
    createLabel("Id", shell, idText);
    
    final Text nameText = createText(savedName, shell, idText);
    createLabel("Name", shell, nameText);
    
    final Text typeText = createText(savedType, shell, nameText);
    createLabel("Type", shell, typeText);
    
    final Text valueText = createText(savedValue, shell, typeText);
    createLabel("Value", shell, valueText);
    
    final Text expressionText = createText(savedExpression, shell, valueText);
    createLabel("Expression", shell, expressionText);
    
    final Text variableText = createText(savedVariable, shell, expressionText);
    createLabel("Variable", shell, variableText);
    
    final Text defaultText = createText(savedDefaultExpression, shell, variableText);
    createLabel("Default", shell, defaultText);
    
    final Text patternText = createText(savedDatePattern, shell, defaultText);
    createLabel("Date pattern", shell, patternText);
    
    final Combo readableDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
    readableDropDown.add("");
    readableDropDown.add("True");
    readableDropDown.add("False");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(patternText, VSPACE);
    readableDropDown.setLayoutData(data);
    if("true".equalsIgnoreCase(savedReadable)) {
      readableDropDown.select(1);
    } else if("false".equalsIgnoreCase(savedReadable)) {
      readableDropDown.select(2);
    } else {
    	readableDropDown.select(0);
    }
    
    createLabel("Readable", shell, readableDropDown);
    
    final Combo writeableDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
    writeableDropDown.add("");
    writeableDropDown.add("True");
    writeableDropDown.add("False");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(readableDropDown, VSPACE);
    writeableDropDown.setLayoutData(data);
    if("true".equalsIgnoreCase(savedWriteable)) {
      writeableDropDown.select(1);
    } else if("false".equalsIgnoreCase(savedWriteable)) {
      writeableDropDown.select(2);
    } else {
    	writeableDropDown.select(0);
    }
    
    createLabel("Writeable", shell, writeableDropDown);
    
    final Combo requiredDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
    requiredDropDown.add("");
    requiredDropDown.add("True");
    requiredDropDown.add("False");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(writeableDropDown, VSPACE);
    requiredDropDown.setLayoutData(data);
    if("true".equalsIgnoreCase(savedRequired)) {
      requiredDropDown.select(1);
    } else if("false".equalsIgnoreCase(savedRequired)) {
      requiredDropDown.select(2);
    } else {
    	requiredDropDown.select(0);
    }
    
    createLabel("Required", shell, requiredDropDown);
    
    Composite valuesComposite = new Composite(shell, SWT.WRAP);
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(requiredDropDown, VSPACE);
		valuesComposite.setLayoutData(data);
		GridLayout valuesLayout = new GridLayout();
		valuesLayout.marginTop = 0;
		valuesLayout.numColumns = 1;
		valuesComposite.setLayout(valuesLayout);
		final FormValueEditor formValueEditor = new FormValueEditor("formValueEditor", valuesComposite);
		formValueEditor.getLabelControl(valuesComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		formValueEditor.initialize(savedFormValues);
		
		createLabel("Form values", shell, valuesComposite);
    
    // Create the cancel button and add a handler
    // so that pressing it will set input to null
    Button cancel = new Button(shell, SWT.PUSH);
    cancel.setText("Cancel");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(valuesComposite, 20);
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
				type = typeText.getText();
				value = valueText.getText();
				expression = expressionText.getText();
				variable = variableText.getText();
				defaultExpression = defaultText.getText(); 
				datePattern = patternText.getText();
				readable = readableDropDown.getText();
				writeable = writeableDropDown.getText();
				required = requiredDropDown.getText();
				formValues = "";
				TableItem[] formValueItems = formValueEditor.getItems();
				if(formValueItems != null) {
					for(int i = 0; i < formValueItems.length; i++) {
						TableItem formValueItem = formValueItems[i];
						if(i > 0) {
							formValues += ";"; 
						}
						formValues += formValueItem.getText(0) + ":" + formValueItem.getText(1);
					}
				}
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
