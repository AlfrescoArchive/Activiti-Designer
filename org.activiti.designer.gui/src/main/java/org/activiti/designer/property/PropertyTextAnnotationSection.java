package org.activiti.designer.property;

import org.activiti.bpmn.model.TextAnnotation;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyTextAnnotationSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text text;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    text = createTextControl(true);
    createLabel("Text", text);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    TextAnnotation textAnnotation = (TextAnnotation) businessObject;
    if (control == text) {
      return textAnnotation.getText();
    } 
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    TextAnnotation textAnnotation = (TextAnnotation) businessObject;
    if (control == text) {
      textAnnotation.setText(text.getText());
    }
  }
}