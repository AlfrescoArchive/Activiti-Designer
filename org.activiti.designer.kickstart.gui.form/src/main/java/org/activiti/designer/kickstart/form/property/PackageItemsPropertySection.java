package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class PackageItemsPropertySection extends AbstractKickstartFormComponentSection {

  protected Button allowAddingControl;
  protected Button allowRemovingControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createFullWidthLabel("This fields represent the package attached to the workflow, containing the contant "
        + "that is associated with the workflow. When used on a start-form, the user can select the initial content. When used "
        + "in a Human Step form, adding and removing items can be enabled/disabled.");
    createSeparator();

    allowAddingControl = createCheckboxControl("Allow adding content");
    allowRemovingControl = createCheckboxControl("Allow removing content");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == allowRemovingControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_PACKAGEITEMS_ALLOW_REMOVE);
    } else if (control == allowAddingControl) {
      return getBooleanParameter(propDef, AlfrescoConversionConstants.PARAMETER_PACKAGEITEMS_ALLOW_ADD);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ReferencePropertyDefinition propDef = (ReferencePropertyDefinition) businessObject;
    if (control == allowRemovingControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_PACKAGEITEMS_ALLOW_REMOVE,
          allowRemovingControl.getSelection());
    } else if (control == allowAddingControl) {
      propDef.getParameters().put(AlfrescoConversionConstants.PARAMETER_PACKAGEITEMS_ALLOW_ADD,
          allowAddingControl.getSelection());
    }
  }

  protected Boolean getBooleanParameter(ReferencePropertyDefinition propDef, String key) {
    Boolean result = Boolean.FALSE;
    if (propDef.getParameters() != null) {
      Object value = propDef.getParameters().get(key);
      if (value instanceof Boolean) {
        result = (Boolean) value;
      } else if (value != null) {
        result = Boolean.valueOf(value.toString());
      }
    }
    return result;
  }
}
