/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
