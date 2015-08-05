package org.activiti.designer.property;

import org.activiti.bpmn.model.Event;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertySignalStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  protected Combo signalCombo;
  protected String[] signalArray;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
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
    Event event = (Event) businessObject;
    if (control == signalCombo) {
      return SignalPropertyUtil.getSignalValue(event, getDiagram());
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    if (control == signalCombo) {
      SignalPropertyUtil.storeSignalValue(signalCombo, event, getDiagram());
    }
  }
}
