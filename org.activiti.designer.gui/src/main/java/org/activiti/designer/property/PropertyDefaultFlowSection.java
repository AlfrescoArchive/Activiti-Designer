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

import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDefaultFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Combo defaultCombo;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    defaultCombo = createCombobox(new String[]{}, 0);
    createLabel("Default flow", defaultCombo);
  }
  
  

  @Override
  public void refresh() {
    PictogramElement element = getSelectedPictogramElement();
    FlowNode flowNode = (FlowNode) getBusinessObject(element);
    List<SequenceFlow> flowList = flowNode.getOutgoingFlows();
    defaultCombo.removeAll();
    defaultCombo.add("");
    for (SequenceFlow flow : flowList) {
      defaultCombo.add(flow.getId());
    }
    
    super.refresh();
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    if (control == defaultCombo) {
      String defaultFlow = null;
      if (businessObject instanceof Activity) {
        defaultFlow = ((Activity) businessObject).getDefaultFlow();
      } else if (businessObject instanceof Gateway) {
        defaultFlow = ((Gateway) businessObject).getDefaultFlow();
      }
      return defaultFlow; 
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    if (control == defaultCombo) {
      if (businessObject instanceof Activity) {
        ((Activity) businessObject).setDefaultFlow(defaultCombo.getText());
      } else if (businessObject instanceof Gateway) {
        ((Gateway) businessObject).setDefaultFlow(defaultCombo.getText());
      }
    }
  }
}
