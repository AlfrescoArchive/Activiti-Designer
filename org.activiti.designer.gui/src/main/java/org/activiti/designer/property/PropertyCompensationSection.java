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
package org.activiti.designer.property;

import org.activiti.bpmn.model.Activity;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyCompensationSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
  
  protected Button compensationButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    compensationButton = createCheckboxControl("Is for compensation?");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == compensationButton) {
      return activity.isForCompensation();
    } 
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Activity activity = (Activity) businessObject;
    if (control == compensationButton) {
      activity.setForCompensation(compensationButton.getSelection());
    }
  }
}
