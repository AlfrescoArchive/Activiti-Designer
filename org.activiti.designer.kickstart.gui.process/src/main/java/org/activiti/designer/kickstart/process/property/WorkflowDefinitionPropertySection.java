package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link WorkflowDefinition}.
 * 
 * @author Frederik Hereman
 */
public class WorkflowDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Text nameControl;
  protected Text idControl;
  protected Text descriptionControl;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    nameControl = createTextControl(false);
    createLabel("Workflow name", nameControl);
    idControl = createTextControl(false);
    createLabel("Workflow id", idControl);
    descriptionControl = createTextControl(true);
    createLabel("Description", descriptionControl);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    WorkflowDefinition workflowDefinition = (WorkflowDefinition) businessObject;
    if(control == nameControl) {
      return workflowDefinition.getName() != null ? workflowDefinition.getName() : "";
    } else if(control == descriptionControl) {
      return workflowDefinition.getDescription() != null ? workflowDefinition.getDescription() : "";
    } else if(control == idControl) {
      return workflowDefinition.getId() != null ? workflowDefinition.getId() : "";
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    WorkflowDefinition workflowDefinition = (WorkflowDefinition) businessObject;
    if(control == nameControl) {
      workflowDefinition.setName(nameControl.getText());
    } else if(control == descriptionControl) {
      workflowDefinition.setDescription(descriptionControl.getText());
    } else if(control == idControl) {
      workflowDefinition.setId(idControl.getText());
    }
  }
}
