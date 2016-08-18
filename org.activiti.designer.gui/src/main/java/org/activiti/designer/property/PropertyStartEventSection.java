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

import org.activiti.bpmn.model.StartEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
	private Text initiatorText;
	private Text formKeyText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  initiatorText = createTextControl(false);
    createLabel("Initiator", initiatorText);
    formKeyText = createTextControl(false);
    createLabel("Form key", formKeyText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    StartEvent event = (StartEvent) businessObject;
    if (control == initiatorText) {
      return event.getInitiator();
      
    } else if (control == formKeyText) {
      return event.getFormKey();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    StartEvent event = (StartEvent) businessObject;
    if (control == initiatorText) {
      event.setInitiator(initiatorText.getText());
      
    } else if (control == formKeyText) {
      event.setFormKey(formKeyText.getText());
    }
  }
}