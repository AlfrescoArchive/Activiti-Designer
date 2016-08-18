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

import org.activiti.bpmn.model.BoundaryEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyBoundarySignalSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	protected Combo cancelActivityCombo;
	protected String[] cancelFormats = new String[] {"true", "false"};
	protected Combo signalCombo;
	protected String[] signalArray;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    cancelActivityCombo = createCombobox(cancelFormats, 0);
    createLabel("Cancel activity", cancelActivityCombo);
    signalCombo = createCombobox(signalArray, 0);
    createLabel("Signal ref", signalCombo);
  }
	

  @Override
  protected void populateControl(Control control, Object businessObject) {
    if (control == signalCombo) {
      SignalPropertyUtil.fillSignalCombo(signalCombo, selectionListener, getDiagram());
    }
    super.populateControl(control, businessObject);
  }


  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      return String.valueOf(event.isCancelActivity());
      
    } else if (control == signalCombo) {
      return SignalPropertyUtil.getSignalValue(event, getDiagram(), getDiagramContainer());
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    BoundaryEvent event = (BoundaryEvent) businessObject;
    if (control == cancelActivityCombo) {
      event.setCancelActivity(Boolean.valueOf(cancelFormats[cancelActivityCombo.getSelectionIndex()]));
      
    } else if (control == signalCombo) {
      SignalPropertyUtil.storeSignalValue(signalCombo, event, getDiagram());
    }
  }
}
