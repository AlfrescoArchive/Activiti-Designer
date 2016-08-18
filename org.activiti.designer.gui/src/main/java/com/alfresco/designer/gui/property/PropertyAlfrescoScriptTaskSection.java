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

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.property.ActivitiPropertySection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text scriptText;
	private Text runAsText;
	private Text scriptProcessorText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    scriptText = createTextControl(true);
    createLabel("Script", scriptText);
    runAsText = createTextControl(false);
    createLabel("Run as", runAsText);
    scriptProcessorText = createTextControl(false);
    createLabel("Script processor", scriptProcessorText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ServiceTask scriptTask = (ServiceTask) businessObject;
    if (control == scriptText) {
      return getFieldString("script", scriptTask);
      
    } else if (control == runAsText) {
      return getFieldString("runAs", scriptTask);
      
    } else if (control == scriptProcessorText) {
      return getFieldString("scriptProcessor", scriptTask);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ServiceTask scriptTask = (ServiceTask) businessObject;
    if (control == scriptText) {
      setFieldString("script", scriptText.getText(), scriptTask);
      
    } else if (control == runAsText) {
      setFieldString("runAs", runAsText.getText(), scriptTask);
    
    } else if (control == scriptProcessorText) {
      setFieldString("scriptProcessor", scriptProcessorText.getText(), scriptTask);
    }
  }
}
