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
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class PropertyDefinitionPropertySection extends AbstractKickstartFormComponentSection {

  protected Text nameControl;
  protected Button mandatoryControl;
  protected Button writableControl;
  protected Button outputPropertyControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    nameControl = createTextControl(false);
    createLabel("Property name", nameControl);

    mandatoryControl = createCheckboxControl("Mandatory");
    writableControl = createCheckboxControl("Editable");
    outputPropertyControl = createCheckboxControl("Output property to workflow");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FormPropertyDefinition propDef = (FormPropertyDefinition) businessObject;
    if (control == nameControl) {
      return propDef.getName();
    } else if (control == mandatoryControl) {
      return propDef.isMandatory();
    } else if (control == writableControl) {
      return propDef.isWritable();
    } else if(control == outputPropertyControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, false);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FormPropertyDefinition propDef = (FormPropertyDefinition) businessObject;
    if (control == nameControl) {
      propDef.setName(nameControl.getText());
    } else if (control == mandatoryControl) {
      propDef.setMandatory(mandatoryControl.getSelection());
    } else if (control == writableControl) {
      propDef.setWritable(writableControl.getSelection());
    } else if(control == outputPropertyControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, outputPropertyControl.getSelection());
    }
  }
}
