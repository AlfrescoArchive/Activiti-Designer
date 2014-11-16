package org.activiti.designer.property;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyBoundaryCompensateSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	private Combo cancelActivityCombo;
	private String[] cancelFormats = new String[] { "true", "false" };
	private Text compensateText;

	@Override
	protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
		cancelActivityCombo = createCombobox(cancelFormats, 0);
		createLabel("Cancel activity", cancelActivityCombo);
		compensateText = createTextControl(false);
		createLabel("Compensate ref", compensateText);
	}

	@Override
	protected Object getModelValueForControl(Control control, Object businessObject) {
		BoundaryEvent event = (BoundaryEvent) businessObject;
		if (control == cancelActivityCombo) {
			return String.valueOf(event.isCancelActivity());

		} else if (control == compensateText) {
			if (event.getEventDefinitions().get(0) != null) {
				CompensateEventDefinition compensateEventDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
				return compensateEventDefinition.getActivityRef();
			}
		}
		return null;
	}

	@Override
	protected void storeValueInModel(Control control, Object businessObject) {
		BoundaryEvent event = (BoundaryEvent) businessObject;
		if (control == cancelActivityCombo) {
			event.setCancelActivity(Boolean.valueOf(cancelFormats[cancelActivityCombo.getSelectionIndex()]));

		} else if (control == compensateText) {
			CompensateEventDefinition compensateEventDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
			compensateEventDefinition.setActivityRef(compensateText.getText());
		}
	}

}
