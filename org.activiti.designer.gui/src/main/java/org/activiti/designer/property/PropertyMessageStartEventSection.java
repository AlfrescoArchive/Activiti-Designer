package org.activiti.designer.property;

/**
 * @author Saeid Mirzaei
 */

import org.activiti.bpmn.model.Event;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyMessageStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  protected Combo messageCombo;
  protected String[] messageArray;
	
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    messageCombo = createCombobox(messageArray, 0);
    createLabel("Message ref", messageCombo);
  }
  
  @Override
  protected void populateControl(Control control, Object businessObject) {
    if (control == messageCombo) {
      MessagePropertyUtil.fillMessageCombo(messageCombo, selectionListener, getDiagram());
    }
    super.populateControl(control, businessObject);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == messageCombo) {
      return MessagePropertyUtil.getMessageValue(event, getDiagram());
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == messageCombo) {
      MessagePropertyUtil.storeMessageValue(messageCombo, event, getDiagram());
    }
  }
}
