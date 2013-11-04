package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class GroupSelectPropertySection extends AbstractKickstartFormComponentSection {

  protected Button editableControl;
  protected Button mandatoryControl;
  protected Button manyControl;
  protected Button outputPropertyControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    editableControl = createCheckboxControl("Editable");
    mandatoryControl = createCheckboxControl("Mandatory");
    createSeparator();
    manyControl = createCheckboxControl("Allow selecting multiple groups");
    outputPropertyControl = createCheckboxControl("Output property to workflow");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == editableControl) {
      return propDef.isWritable();
    } else if(control == mandatoryControl) {
      return propDef.isMandatory();
    } else if(control == manyControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_REFERENCE_MANY, false);
    } else if(control == outputPropertyControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, false);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == editableControl) {
      propDef.setWritable(editableControl.getSelection());
    } else if(control == mandatoryControl) {
      propDef.setMandatory(mandatoryControl.getSelection());
    } else if(control == manyControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_REFERENCE_MANY, manyControl.getSelection());
    } else if(control == outputPropertyControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_ADD_PROPERTY_TO_OUTPUT, outputPropertyControl.getSelection());
    }
  }
}
