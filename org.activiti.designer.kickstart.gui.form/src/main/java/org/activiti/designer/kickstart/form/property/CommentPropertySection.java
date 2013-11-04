package org.activiti.designer.kickstart.form.property;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class CommentPropertySection extends AbstractKickstartFormComponentSection {


  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createFullWidthLabel("Field that represent the default task comment field. Can be edited and will be stored in the task comment when saved/completed.");
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
  }
}
