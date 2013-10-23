package org.activiti.designer.kickstart.process.property;

import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link WorkflowDefinition}.
 * 
 * @author Frederik Hereman
 */
public class ParallelStepDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Label infoControl;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    infoControl = createFullWidthLabel("A step that can contain multiple steps that are executed in parallel.");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    // No value stored in model
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // Nothing to store
  }
}
