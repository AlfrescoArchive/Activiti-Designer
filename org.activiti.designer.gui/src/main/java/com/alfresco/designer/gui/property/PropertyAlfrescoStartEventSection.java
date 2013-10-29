package com.alfresco.designer.gui.property;

import java.util.List;

import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.property.ActivitiPropertySection;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  private Combo formTypeCombo;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    List<String> formTypes = PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT);
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
