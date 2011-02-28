/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * The Class AbstractInputFieldWizardPage.
 */
public abstract class AbstractInputFieldWizardPage extends AbstractWizardPage {

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;

	// widgets
	/**
	 * The text field.
	 */
	Text textField;

	private Listener nameModifyListener = new Listener() {
		public void handleEvent(Event e) {
			boolean valid = validatePage();
			setPageComplete(valid);

		}
	};

	/**
	 * Instantiates a new abstract input field wizard page.
	 * 
	 * @param pageName
	 *            the page name
	 * @param title
	 *            the title
	 * @param titleImage
	 *            the title image
	 */
	public AbstractInputFieldWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * Instantiates a new abstract input field wizard page.
	 * 
	 * @param pageName
	 *            the page name
	 */
	protected AbstractInputFieldWizardPage(String pageName) {
		super(pageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.graphiti.examples.testview.wizard.AbstractWizardPage#
	 * createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());

		initializeDialogUnits(parent);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createWizardContents(composite);

		setPageComplete(validatePage());

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.testview.wizard.ITextProvider#getText()
	 */
	public String getText() {
		if (textField == null) {
			return getInitialTextFieldValue();
		}

		return getTextFieldValue();
	}

	/**
	 * Validate page.
	 * 
	 * @return true, if successful
	 */
	protected boolean validatePage() {
		String text = getTextFieldValue();
		if (text.equals("")) { //$NON-NLS-1$
			setErrorMessage(null);
			setMessage(getMessageText());
			return false;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = doWorkspaceValidation(workspace, text);
		if (!status.isOK()) {
			setErrorMessage(status.getMessage());
			return false;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/**
	 * Do workspace validation.
	 * 
	 * @param workspace
	 *            the workspace
	 * @param text
	 *            the text
	 * 
	 * @return the i status
	 */
	abstract IStatus doWorkspaceValidation(IWorkspace workspace, String text);

	/**
	 * Gets the initial text field value.
	 * 
	 * @return the initial text field value
	 */
	abstract String getInitialTextFieldValue();

	/**
	 * Gets the label text.
	 * 
	 * @return the label text
	 */
	abstract String getLabelText();

	/**
	 * Gets the message text.
	 * 
	 * @return the message text
	 */
	abstract String getMessageText();

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.graphiti.samples.testview.wizard.AbstractWizardPage#
	 * createWizardContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createWizardContents(Composite parent) {
		createProjectNameGroup(parent);
	}

	private final void createProjectNameGroup(Composite parent) {
		// project specification group
		Composite projectGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setText(getLabelText());
		projectLabel.setFont(parent.getFont());

		// new project name entry field
		textField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		textField.setLayoutData(data);
		textField.setFont(parent.getFont());

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (getInitialTextFieldValue() != null) {
			textField.setText(getInitialTextFieldValue());
		}
		textField.addListener(SWT.Modify, nameModifyListener);
	}

	private String getTextFieldValue() {
		if (textField == null) {
			return ""; //$NON-NLS-1$
		}

		return textField.getText().trim();
	}
}
