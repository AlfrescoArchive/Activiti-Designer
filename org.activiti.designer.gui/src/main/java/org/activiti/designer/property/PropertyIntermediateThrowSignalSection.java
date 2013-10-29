package org.activiti.designer.property;

import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyIntermediateThrowSignalSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text signalText;

	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    signalText = createTextControl(false);
    createLabel("Signal ref", signalText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == signalText) {
      if (event.getEventDefinitions().get(0) != null) {
        SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
        return signalDefinition.getSignalRef();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == signalText) {
      SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
      signalDefinition.setSignalRef(signalText.getText());
    }
  }
}
