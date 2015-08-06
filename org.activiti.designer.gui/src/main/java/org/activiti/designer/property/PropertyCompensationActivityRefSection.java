package org.activiti.designer.property;

import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.Event;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyCompensationActivityRefSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text activityRefText;

	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  activityRefText = createTextControl(false);
    createLabel("Activity ref", activityRefText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == activityRefText) {
      if (event.getEventDefinitions().get(0) != null) {
        CompensateEventDefinition compensateDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
        return compensateDefinition.getActivityRef();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == activityRefText) {
      CompensateEventDefinition compensateDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
      compensateDefinition.setActivityRef(activityRefText.getText());
    }
  }
}
