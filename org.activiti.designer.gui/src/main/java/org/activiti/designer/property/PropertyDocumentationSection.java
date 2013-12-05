package org.activiti.designer.property;

import org.activiti.bpmn.model.FlowElement;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyDocumentationSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text documentationText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    documentationText = createTextControl(true);
    createLabel("Documentation", documentationText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    FlowElement element = (FlowElement) businessObject;
    if(control == documentationText) {
      return element.getDocumentation();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    FlowElement element = (FlowElement) businessObject;
    if (control == documentationText) {
      element.setDocumentation(documentationText.getText());
    }
  }
}
