package com.alfresco.designer.gui.property;

import org.activiti.designer.bpmn2.model.alfresco.AlfrescoScriptTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
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

public class PropertyAlfrescoScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text scriptText;
	private Text runAsText;
	private Text scriptProcessorText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		scriptText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		scriptText.setLayoutData(data);
		scriptText.addFocusListener(listener);

		createLabel(composite, "Script", scriptText, factory);

		runAsText = createText(composite, factory, scriptText);
		createLabel(composite, "Run as", runAsText, factory);
		
		scriptProcessorText = createText(composite, factory, runAsText);
    createLabel(composite, "Script processor", scriptProcessorText, factory);
	}

	@Override
	public void refresh() {
	  scriptText.removeFocusListener(listener);
	  runAsText.removeFocusListener(listener);
	  scriptProcessorText.removeFocusListener(listener);
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;
			
			AlfrescoScriptTask scriptTask = (AlfrescoScriptTask) bo;
			
			String script = scriptTask.getScript();
			scriptText.setText(script == null ? "" : script);
			runAsText.setText(scriptTask.getRunAs() == null ? "" : scriptTask.getRunAs());
			scriptProcessorText.setText(scriptTask.getScriptProcessor() == null ? "" : scriptTask.getScriptProcessor());
		}
		scriptText.addFocusListener(listener);
    runAsText.addFocusListener(listener);
    scriptProcessorText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (bo instanceof AlfrescoScriptTask) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							AlfrescoScriptTask scriptTask = (AlfrescoScriptTask) bo;
							if (scriptText.getText() != null) {
							  scriptTask.setScript(scriptText.getText());
							}
							if (runAsText.getText() != null) {
                scriptTask.setRunAs(runAsText.getText());
              }
							if (scriptProcessorText.getText() != null) {
                scriptTask.setScriptProcessor(scriptProcessorText.getText());
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
    data.top = new FormAttachment(top, VSPACE);
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
