package org.activiti.designer.property;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyErrorEndEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text errorCodeText;

	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    errorCodeText = createTextControl(false);
    createLabel("Error code", errorCodeText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    EndEvent event = (EndEvent) businessObject;
    if (control == errorCodeText) {
      if (event.getEventDefinitions().get(0) != null) {
        ErrorEventDefinition errorDefinition = (ErrorEventDefinition) event.getEventDefinitions().get(0);
        return errorDefinition.getErrorCode();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    EndEvent event = (EndEvent) businessObject;
    if (control == errorCodeText) {
      ErrorEventDefinition errorDefinition = (ErrorEventDefinition) event.getEventDefinitions().get(0);
      errorDefinition.setErrorCode(errorCodeText.getText());
    }
  }
}
