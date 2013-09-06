package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class FormPropertyGroupPropertySection extends AbstractKickstartFormComponentSection {
  
  // TODO: use kickstart-alfresco constants for this
  public static final String SINGLE_COLUMN_VALUE = "one-column";
  public static final String TWO_COLUMN_VALUE = "two-column";
  public static final String THREE_COLUMN_VALUE = "three-column";
  
  protected static final String SINGLE_COLUMN_MESSAGE = "Single column layout";
  protected static final String TWO_COLUMN_MESSAGE = "Two column layout";
  protected static final String THREE_COLUMN_MESSAGE = "Three column layout";
  
  protected static final String[] LAYOUT_VALUES = new String[] {SINGLE_COLUMN_MESSAGE, TWO_COLUMN_MESSAGE, THREE_COLUMN_MESSAGE};
  protected Text idControl;
  protected Text titleControl;
  protected Combo typeControl;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    titleControl = createTextControl(false);
    createLabel("Title", titleControl);
    
    idControl = createTextControl(false);
    createLabel("Group ID", idControl);
    
    typeControl = createCombobox(LAYOUT_VALUES, 0);
    createLabel("Type", typeControl);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    if(control == idControl) {
      return group.getId();
    } else if(control == titleControl) {
      return group.getTitle();
    } else if(control == typeControl) {
      return getTypeMessageForvalue(group.getType());
    }
    return null;
  }
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    if(control == idControl) {
      group.setId(idControl.getText());
    } else if(control == titleControl) {
      group.setTitle(titleControl.getText());
    } else if(control == typeControl) {
      group.setType(getTypeValueForMessage(typeControl.getText()));
    }
  }
  
  /**
   * @return the message to display for the given type.
   */
  protected String getTypeValueForMessage(String text) {
    if(TWO_COLUMN_MESSAGE.equals(text) ) {
      return TWO_COLUMN_VALUE;
    } else if(THREE_COLUMN_MESSAGE.equals(text)) {
      return THREE_COLUMN_VALUE;
    }
    return SINGLE_COLUMN_VALUE;
  }
  
  /**
   * @return the actual value to use for the group type, based on the selected
   * value.
   */
  protected String getTypeMessageForvalue(String type) {
    if(TWO_COLUMN_VALUE.equals(type) ) {
      return TWO_COLUMN_MESSAGE;
    } else if(THREE_COLUMN_VALUE.equals(type)) {
      return THREE_COLUMN_MESSAGE;
    }
    return SINGLE_COLUMN_MESSAGE;
  }
}
