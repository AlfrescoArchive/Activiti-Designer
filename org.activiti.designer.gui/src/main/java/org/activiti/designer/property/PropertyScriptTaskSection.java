package org.activiti.designer.property;

import java.util.Arrays;
import java.util.List;

import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.bpmn2.ScriptTask;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private CCombo scriptFormatCombo;
	private List<String> scriptFormats = Arrays.asList("javascript", "groovy");
	private Text scriptText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		scriptFormatCombo = factory.createCCombo(composite, SWT.NONE);
		scriptFormatCombo.setItems((String[]) scriptFormats.toArray());
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		scriptFormatCombo.setLayoutData(data);
		scriptFormatCombo.addFocusListener(listener);

		CLabel languageLabel = factory.createCLabel(composite, "Script Language:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(scriptFormatCombo, -HSPACE);
		data.top = new FormAttachment(scriptFormatCombo, 0, SWT.CENTER);
		languageLabel.setLayoutData(data);

		scriptText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(scriptFormatCombo, VSPACE);
		scriptText.setLayoutData(data);
		scriptText.addFocusListener(listener);

		CLabel scriptLabel = factory.createCLabel(composite, "Script:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(scriptText, -HSPACE);
		data.top = new FormAttachment(scriptText, 0, SWT.TOP);
		scriptLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		scriptFormatCombo.removeFocusListener(listener);

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			String scriptFormat = ((ScriptTask) bo).getScriptFormat();
			int scriptIndex = scriptFormats.indexOf(scriptFormat);
			scriptFormatCombo.select(scriptIndex == -1 ? 0 : scriptIndex);
			scriptFormatCombo.addFocusListener(listener);

			String script = ((ScriptTask) bo).getScript();
			scriptText.setText(script == null ? "" : script);
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (bo instanceof ScriptTask) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							String scriptFormat = scriptFormatCombo.getText();
							if (scriptFormat != null) {
								if (bo instanceof ScriptTask) {

									((ScriptTask) bo).setScriptFormat(scriptFormat);
								}
							}
							String script = scriptText.getText();
							if (script != null) {
								if (bo instanceof ScriptTask) {

									((ScriptTask) bo).setScript(script);
								}
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};
}
