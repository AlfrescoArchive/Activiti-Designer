package org.activiti.designer.kickstart.form.property;

import org.activiti.designer.kickstart.form.command.FormGroupPropertyDefinitionModelUpdater;
import org.activiti.designer.kickstart.form.command.KickstartModelUpdater;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class FormPropertyGroupPropertySection extends AbstractKickstartFormComponentSection {
  
  protected static final String[] LAYOUT_VALUES = new String[] {"Single column", "Two columns", "Three columns"};
  protected Text idControl;
  protected Text titleControl;
  protected Combo layoutControl;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    titleControl = createTextControl(false);
    createLabel("Title", titleControl);
    
    idControl = createTextControl(false);
    createLabel("Group ID", idControl);
    
    layoutControl = createCombobox(LAYOUT_VALUES, 0);
    createLabel("Layout", layoutControl);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FormPropertyGroup group = (FormPropertyGroup) businessObject;
    if(control == idControl) {
      return group.getId();
    } else if(control == titleControl) {
      return group.getTitle();
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
    }
  }
  
  @Override
  protected KickstartModelUpdater<?> getModelUpdater() {
    PictogramElement pictogramElement = getSelectedPictogramElement();
    FormPropertyGroup group = (FormPropertyGroup) getBusinessObject(pictogramElement);
        
    if(group != null) {
      return new FormGroupPropertyDefinitionModelUpdater(group, pictogramElement, getDiagramTypeProvider().getFeatureProvider());
    }
    return null;
  }
}
