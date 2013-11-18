package org.activiti.designer.property;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyBoundarySignalSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Combo cancelActivityCombo;
	private String[] cancelFormats = new String[] {"true", "false"};
	private Text signalText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    cancelActivityCombo = createCombobox(cancelFormats, 0);
    createLabel("Cancel activity", cancelActivityCombo);
    signalText = createTextControl(false);
    createLabel("Signal ref", signalText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      return String.valueOf(event.isCancelActivity());
      
    } else if (control == signalText) {
      if (event.getEventDefinitions().get(0) != null) {
        SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
        return signalDefinition.getSignalRef();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      event.setCancelActivity(Boolean.valueOf(cancelFormats[cancelActivityCombo.getSelectionIndex()]));
      
    } else if (control == signalText) {
      SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
      signalDefinition.setSignalRef(signalText.getText());
    }
  }
}
