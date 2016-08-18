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

import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.Event;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyCompensationActivityRefSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text activityRefText;

	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  activityRefText = createTextControl(false);
    createLabel("Activity ref", activityRefText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == activityRefText) {
      if (event.getEventDefinitions().get(0) != null) {
        CompensateEventDefinition compensateDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
        return compensateDefinition.getActivityRef();
      }
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == activityRefText) {
      CompensateEventDefinition compensateDefinition = (CompensateEventDefinition) event.getEventDefinitions().get(0);
      compensateDefinition.setActivityRef(activityRefText.getText());
    }
  }
}
