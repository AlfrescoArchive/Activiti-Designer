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
package com.alfresco.designer.gui.property;

import java.util.List;

import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.property.ActivitiPropertySection;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  private Combo formTypeCombo;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    List<String> formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT, ActivitiPlugin.getDefault());
    formTypeCombo = createCombobox(formTypes.toArray(new String[formTypes.size()]), 0);
    createLabel("Form key", formTypeCombo);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    StartEvent event = (StartEvent) businessObject;
    if (control == formTypeCombo) {
      return event.getFormKey();
    } 
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    StartEvent event = (StartEvent) businessObject;
    if (control == formTypeCombo) {
      event.setFormKey(formTypeCombo.getText());
    }
  }
}
