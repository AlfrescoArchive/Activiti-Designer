package com.alfresco.designer.gui.property;

import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyAlfrescoStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  private CCombo formTypeCombo;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		formTypeCombo = factory.createCCombo(composite, SWT.NONE); //$NON-NLS-1$
		formTypeCombo.setItems(PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT));
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    formTypeCombo.setLayoutData(data);
    formTypeCombo.addFocusListener(listener);

    CLabel formKeyLabel = factory.createCLabel(composite, "Form key:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(formTypeCombo, -HSPACE);
    data.top = new FormAttachment(formTypeCombo, 0, SWT.TOP);
    formKeyLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			StartEvent startEvent = ((StartEvent) bo);
			if(startEvent.getFormKey() != null) {
				
			  formTypeCombo.removeFocusListener(listener);
				String condition = startEvent.getFormKey();
				formTypeCombo.setText(condition);
				formTypeCombo.addFocusListener(listener);
			} else {
			  formTypeCombo.setText("");
			}
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (bo instanceof StartEvent) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							if (bo instanceof StartEvent == false) {
								return;
							}
							StartEvent startEvent = (StartEvent) bo;
							String formKey = formTypeCombo.getText();
							if (formKey != null && formKey.length() > 0) {
							  startEvent.setFormKey(formKey);
								
							} else {
								startEvent.setFormKey("");
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};

}
