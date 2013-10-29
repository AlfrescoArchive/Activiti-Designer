package org.activiti.designer.kickstart.form.property;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class WorkflowDescriptionPropertySection extends AbstractKickstartFormComponentSection {

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createFullWidthLabel("This field represent the workflow description. If used on a start-form, it allows the user to set the workflow message. "
        + "If used on a task, the workflow-description is shown read-only.");
    createSeparator();
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    // Nothing to return, this property is used as-is in conversion
    return null;
    
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Nothing to store, this property is used as-is in conversion
  }
}
