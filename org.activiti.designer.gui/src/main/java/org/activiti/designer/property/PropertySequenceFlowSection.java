package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.SequenceFlow;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertySequenceFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
	private Text conditionExpressionText;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		conditionExpressionText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		conditionExpressionText.setLayoutData(data);
		conditionExpressionText.addFocusListener(listener);

		CLabel scriptLabel = factory.createCLabel(composite, "Condition:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(conditionExpressionText, -HSPACE);
		data.top = new FormAttachment(conditionExpressionText, 0, SWT.TOP);
		scriptLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			SequenceFlow sequenceFlow = ((SequenceFlow) bo);
			if(sequenceFlow.getConditionExpression() != null) {
				
				conditionExpressionText.removeFocusListener(listener);
				String condition = sequenceFlow.getConditionExpression();
				conditionExpressionText.setText(condition);
				conditionExpressionText.addFocusListener(listener);
			} else {
				conditionExpressionText.setText("");
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
				if (bo instanceof SequenceFlow) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							if (bo instanceof SequenceFlow == false) {
								return;
							}
							SequenceFlow sequenceFlow = (SequenceFlow) bo;
							String condition = conditionExpressionText.getText();
							if (condition != null && condition.length() > 0) {
								sequenceFlow.setConditionExpression(condition);
								
							} else {
								sequenceFlow.setConditionExpression(null);
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};

}
