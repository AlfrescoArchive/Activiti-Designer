package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.AbstractNamedStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link StepDefinition}.
 * 
 * @author Frederik Hereman
 */
public class NamedStepDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Text nameControl;
  protected Text descriptionControl;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    nameControl = createTextControl(false);
    createLabel("Human step name", nameControl);
    descriptionControl = createTextControl(true);
    createLabel("Description", descriptionControl);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    AbstractNamedStepDefinition stepDefinition = (AbstractNamedStepDefinition) businessObject;
    if(control == nameControl) {
      return stepDefinition.getName() != null ? stepDefinition.getName() : "";
    } else if(control == descriptionControl) {
      return stepDefinition.getDescription() != null ? stepDefinition.getDescription() : "";
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    AbstractNamedStepDefinition stepDefinition = (AbstractNamedStepDefinition) businessObject;
    if(control == nameControl) {
      stepDefinition.setName(nameControl.getText());
    } else if(control == descriptionControl) {
      stepDefinition.setDescription(descriptionControl.getText());
    }
  }
  

}
