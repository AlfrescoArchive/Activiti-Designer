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
