package org.activiti.designer.property;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyBoundaryMessageSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Combo cancelActivityCombo;
  private String[] cancelFormats = new String[] {"true", "false"};
  private Text messageText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    cancelActivityCombo = createCombobox(cancelFormats, 0);
    createLabel("Cancel activity", cancelActivityCombo);
    messageText = createTextControl(false);
    createLabel("Message ref", messageText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      return event.isCancelActivity();
      
    } else if (control == messageText) {
      if (event.getEventDefinitions().get(0) != null) {
        MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
        return messageDefinition.getMessageRef();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      event.setCancelActivity(Boolean.valueOf(cancelFormats[cancelActivityCombo.getSelectionIndex()]));
      
    } else if (control == messageText) {
      MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
      messageDefinition.setMessageRef(messageText.getText());
    }
  }
}
