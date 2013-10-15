package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class PropertyDefinitionPropertySection extends AbstractKickstartFormComponentSection {

  protected Text nameControl;
  protected Button mandatoryControl;
  protected Button writableControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    nameControl = createTextControl(false);
    createLabel("Property name", nameControl);

    mandatoryControl = createCheckboxControl("Mandatory");
    writableControl = createCheckboxControl("Editable");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FormPropertyDefinition propDef = (FormPropertyDefinition) businessObject;
    if (control == nameControl) {
      return propDef.getName();
    } else if (control == mandatoryControl) {
      return propDef.isMandatory();
    } else if (control == writableControl) {
      return propDef.isWritable();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FormPropertyDefinition propDef = (FormPropertyDefinition) businessObject;
    if (control == nameControl) {
      propDef.setName(nameControl.getText());
    } else if (control == mandatoryControl) {
      propDef.setMandatory(mandatoryControl.getSelection());
    } else if (control == writableControl) {
      propDef.setWritable(writableControl.getSelection());
    }
  }
}
