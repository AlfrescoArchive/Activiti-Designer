package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class FieldReferencePropertySection extends AbstractKickstartFormComponentSection {

  protected Text nameText;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createFullWidthLabel("Should not be used on start-forms. Reference to an existing property on the workflow or task "
        + "displayed as a read-only value. Make sure the property exists in the start-form or in any previous forms.");
    createSeparator();
    
    nameText = createTextControl(false);
    createLabel("Property name", nameText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == nameText) {
      return propDef.getName() != null ? propDef.getName() : "";
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == nameText) {
      propDef.setName(nameText.getText());
    }
  }
}
