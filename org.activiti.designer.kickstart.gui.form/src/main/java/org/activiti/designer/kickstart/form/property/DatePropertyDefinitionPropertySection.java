package org.activiti.designer.kickstart.form.property;

import org.activiti.designer.kickstart.form.command.FormPropertyDefinitionModelUpdater;
import org.activiti.designer.kickstart.form.command.KickstartModelUpdater;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class DatePropertyDefinitionPropertySection extends AbstractKickstartFormComponentSection {

  protected Button showTimeControl;

  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    showTimeControl = createCheckboxControl("Show time");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    DatePropertyDefinition propDef = (DatePropertyDefinition) businessObject;
    if (control == showTimeControl) {
      return propDef.isShowTime();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    DatePropertyDefinition propDef = (DatePropertyDefinition) businessObject;
    if (control == showTimeControl) {
      propDef.setShowTime(showTimeControl.getSelection());
    }
  }

  @Override
  protected KickstartModelUpdater<?> getModelUpdater() {
    PictogramElement pictogramElement = getSelectedPictogramElement();
    FormPropertyDefinition propDef = (FormPropertyDefinition) getBusinessObject(pictogramElement);
        
    if(propDef != null) {
      return new FormPropertyDefinitionModelUpdater(propDef, pictogramElement, getDiagramTypeProvider().getFeatureProvider());
    }
    return null;
  }
}
