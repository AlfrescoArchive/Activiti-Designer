package org.activiti.designer.property;

import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyIntermediateCatchMessageSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text messageText;

	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  messageText = createTextControl(false);
    createLabel("Message ref", messageText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == messageText) {
      if (event.getEventDefinitions().get(0) != null) {
        MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
        return convertMessageRef(messageDefinition.getMessageRef());
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == messageText) {
      MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
      messageDefinition.setMessageRef(messageText.getText());
    }
  }
}
