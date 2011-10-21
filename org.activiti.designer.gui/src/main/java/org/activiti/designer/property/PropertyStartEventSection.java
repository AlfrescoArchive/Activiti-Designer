package org.activiti.designer.property;

import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
	private Text initiatorText;
	private Text formKeyText;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		
		initiatorText = createText(composite, factory, null);
		createLabel(composite, "Initiator", initiatorText, factory); //$NON-NLS-1$

		formKeyText = createText(composite, factory, initiatorText);
		createLabel(composite, "Form key:", formKeyText, factory); //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		initiatorText.removeFocusListener(listener);
		formKeyText.removeFocusListener(listener);
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			StartEvent startEvent = ((StartEvent) bo);
			
			if(startEvent.getInitiator() != null) {
				initiatorText.setText(startEvent.getInitiator());
			} else {
				initiatorText.setText("");
			}
			
			if(startEvent.getFormKey() != null) {
				
				String condition = startEvent.getFormKey();
				formKeyText.setText(condition);
			} else {
			  formKeyText.setText("");
			}
		}
		initiatorText.addFocusListener(listener);
		formKeyText.addFocusListener(listener);
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
							
							String initator = initiatorText.getText();
							if (initator != null && initator.length() > 0) {
							  startEvent.setInitiator(initator);
								
							} else {
								startEvent.setInitiator("");
							}
							
							String formKey = formKeyText.getText();
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
	
	private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
    text.addFocusListener(listener);
    return text;
  }
  
  private CLabel createLabel(Composite parent, String text, Control control, TabbedPropertySheetWidgetFactory factory) {
    CLabel label = factory.createCLabel(parent, text); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.TOP);
    label.setLayoutData(data);
    return label;
  }

}
