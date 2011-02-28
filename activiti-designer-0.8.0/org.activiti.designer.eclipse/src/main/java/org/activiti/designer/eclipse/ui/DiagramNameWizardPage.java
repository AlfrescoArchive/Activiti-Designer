package org.activiti.designer.eclipse.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.CheckBox;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The Class DiagramNameWizardPage.
 */
public class DiagramNameWizardPage extends AbstractInputFieldWizardPage {

	CheckBox createDefaultContent = new CheckBox();

	/**
	 * The Constructor.
	 * 
	 * @param pageName
	 *            the page name
	 * @param title
	 *            the title
	 * @param titleImage
	 *            the title image
	 */
	public DiagramNameWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * Instantiates a new diagram name wizard page.
	 * 
	 * @param pageName
	 *            the page name
	 */
	protected DiagramNameWizardPage(String pageName) {
		super(pageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.testview.wizard.AbstractInputFieldWizardPage
	 * #getInitialTextFieldValue()
	 */
	@Override
	String getInitialTextFieldValue() {
		return "my_bpmn2_diagram";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.testview.wizard.AbstractInputFieldWizardPage
	 * #getLabelText()
	 */
	@Override
	String getLabelText() {
		return "BPMN 2.0 diagram name";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.examples.testview.wizard.AbstractInputFieldWizardPage
	 * #getMessageText()
	 */
	@Override
	String getMessageText() {
		return "Please enter BPMN 2.0 diagram name";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.samples.testview.wizard.AbstractInputFieldWizardPage
	 * #doWorkspaceValidation(org.eclipse.core.resources.IWorkspace,
	 * java.lang.String)
	 */
	@Override
	IStatus doWorkspaceValidation(IWorkspace workspace, String text) {
		IStatus ret = workspace.validateName(text, IResource.FILE);
		return ret;
	}
}
