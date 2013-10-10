package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class DatePropertyDefinitionPropertySection extends AbstractKickstartFormComponentSection {

  protected Button showTimeControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    showTimeControl = createCheckboxControl("Show time");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    DatePropertyDefinition propDef = (DatePropertyDefinition) businessObject;
    if (control == showTimeControl) {
      return propDef.isShowTime();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    DatePropertyDefinition propDef = (DatePropertyDefinition) businessObject;
    if (control == showTimeControl) {
      propDef.setShowTime(showTimeControl.getSelection());
    }
  }
}
