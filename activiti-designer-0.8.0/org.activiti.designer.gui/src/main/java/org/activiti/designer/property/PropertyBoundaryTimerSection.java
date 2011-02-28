package org.activiti.designer.property;

import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
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

public class PropertyBoundaryTimerSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text timeDurationText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		timeDurationText = factory.createText(composite, "", SWT.SINGLE); //$NON-NLS-1$
		data = new FormData(SWT.DEFAULT, 100);
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		timeDurationText.setLayoutData(data);
		timeDurationText.addFocusListener(listener);

		CLabel elementLabel = factory.createCLabel(composite, "Time duration:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(timeDurationText, -HSPACE);
		data.top = new FormAttachment(timeDurationText, 0, SWT.TOP);
		elementLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
	  timeDurationText.removeFocusListener(listener);

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;
			
			BoundaryEvent boundaryEvent = (BoundaryEvent) bo;
			String timeDuration = null;
			if(boundaryEvent.getEventDefinitions().get(0) != null) {
			  TimerEventDefinition timerDefinition = (TimerEventDefinition) boundaryEvent.getEventDefinitions().get(0);
        if(timerDefinition.getTimeDuration() != null) {
          timeDuration = ((FormalExpression) timerDefinition.getTimeDuration()).getBody();
        }
			}
			timeDurationText.setText(timeDuration == null ? "" : timeDuration);
		}
		timeDurationText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (bo instanceof BoundaryEvent) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							String timeDuration = timeDurationText.getText();
							if (timeDuration != null) {
							  BoundaryEvent boundaryEvent = (BoundaryEvent) bo;
							  TimerEventDefinition timerDefinition = (TimerEventDefinition) boundaryEvent.getEventDefinitions().get(0);
							  if(timerDefinition.getTimeDuration() == null) {
							    FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
							    timerDefinition.setTimeDuration(expression);
							  }
							  FormalExpression formalExpression = (FormalExpression) timerDefinition.getTimeDuration();
							  formalExpression.setBody(timeDuration);
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};
}
