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

import org.activiti.bpmn.model.FlowNode;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Adds the ability to modify Asynchronous and Exclusiveness of an activity to the "General"
 * property section.
 *
 * This will simply add two check boxes that define whether the activity is either asynchronous
 * and/or exclusive.
 */
public class PropertyAsyncSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
  
  private Button asyncButton;
  private Button exclusiveButton;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    asyncButton = createCheckboxControl("Asynchronous");
    exclusiveButton = createCheckboxControl("Exclusive");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    if (businessObject instanceof FlowNode) {
      FlowNode flowNode = (FlowNode) businessObject;
      exclusiveButton.setVisible(true);
      if (control == asyncButton) {
        return flowNode.isAsynchronous();
        
      } else if(control == exclusiveButton) {
        return !flowNode.isNotExclusive();
      }
    } 
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FlowNode flowNode = (FlowNode) businessObject;
    if (control == asyncButton) {
      flowNode.setAsynchronous(asyncButton.getSelection());
    } else if (control == exclusiveButton) {
      flowNode.setNotExclusive(!exclusiveButton.getSelection());
    }
  }
}
