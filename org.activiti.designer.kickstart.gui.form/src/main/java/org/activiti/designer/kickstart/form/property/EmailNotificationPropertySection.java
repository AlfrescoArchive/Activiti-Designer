package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class EmailNotificationPropertySection extends AbstractKickstartFormComponentSection {

  protected Button forceControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createFullWidthLabel("This field is shown as a checkbox and controls wether or not email-notifications are sent for all tasks in this workflow. In case the 'Force notification' is enabled, "
        + "no checkbox will be shown on the form and email-notifications are enabled by default. When used on a task-form, this field has no effect and will not be shown.");
    createSeparator();
    
    forceControl = createCheckboxControl("Force notification");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == forceControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_FORCE_NOTOFICATIONS, false);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == forceControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_FORCE_NOTOFICATIONS, forceControl.getSelection());
    }
  }
}
