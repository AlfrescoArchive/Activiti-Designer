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
public class PeopleSelectPropertySection extends AbstractKickstartFormComponentSection {

  protected Button editableControl;
  protected Button mandatoryControl;
  protected Button manyControl;
  protected Button outputPropertyControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    editableControl = createCheckboxControl("Editable");
    mandatoryControl = createCheckboxControl("Mandatory");
    createSeparator();
    manyControl = createCheckboxControl("Allow selecting multiple people");
    outputPropertyControl = createCheckboxControl("Output property to workflow");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == editableControl) {
      return propDef.isWritable();
    } else if(control == mandatoryControl) {
      return propDef.isMandatory();
    } else if(control == manyControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_REFERENCE_MANY, false);
    } else if(control == outputPropertyControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, false);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == editableControl) {
      propDef.setWritable(editableControl.getSelection());
    } else if(control == mandatoryControl) {
      propDef.setMandatory(mandatoryControl.getSelection());
    } else if(control == manyControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_REFERENCE_MANY, manyControl.getSelection());
    } else if(control == outputPropertyControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, outputPropertyControl.getSelection());
    }
  }
}
