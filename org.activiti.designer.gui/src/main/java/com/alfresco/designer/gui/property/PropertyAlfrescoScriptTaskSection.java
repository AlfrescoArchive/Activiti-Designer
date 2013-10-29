package com.alfresco.designer.gui.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.property.ActivitiPropertySection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyAlfrescoScriptTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text scriptText;
	private Text runAsText;
	private Text scriptProcessorText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    scriptText = createTextControl(true);
    createLabel("Script", scriptText);
    runAsText = createTextControl(false);
    createLabel("Run as", runAsText);
    scriptProcessorText = createTextControl(false);
    createLabel("Script processor", scriptProcessorText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ServiceTask scriptTask = (ServiceTask) businessObject;
    if (control == scriptText) {
      return getFieldString("script", scriptTask);
      
    } else if (control == runAsText) {
      return getFieldString("runAs", scriptTask);
      
    } else if (control == scriptProcessorText) {
      return getFieldString("scriptProcessor", scriptTask);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ServiceTask scriptTask = (ServiceTask) businessObject;
    if (control == scriptText) {
      setFieldString("script", scriptText.getText(), scriptTask);
      
    } else if (control == runAsText) {
      setFieldString("runAs", runAsText.getText(), scriptTask);
    
    } else if (control == scriptProcessorText) {
      setFieldString("scriptProcessor", scriptProcessorText.getText(), scriptTask);
    }
  }
}
