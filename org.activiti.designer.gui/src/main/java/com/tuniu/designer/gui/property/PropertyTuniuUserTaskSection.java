package com.tuniu.designer.gui.property;

import org.activiti.designer.property.ActivitiPropertySection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import com.tuniu.nfbird.bpm.model.WorkformTask;

public class PropertyTuniuUserTaskSection extends ActivitiPropertySection
		implements ITabbedPropertyConstants {

	private Text formKeyText;
	private Text urlText;
	private Text paramText;
	private Text assigneeText;

	@Override
	public void createFormControls(
			TabbedPropertySheetPage aTabbedPropertySheetPage) {

		formKeyText = createTextControl(false);
		createLabel("Form key", formKeyText);

		urlText = createTextControl(false);
		createLabel("url", urlText);
		
		paramText = createTextControl(false);
		createLabel("param", paramText);
		
		assigneeText = createTextControl(false);
		createLabel("assignee", assigneeText);
	}

	@Override
	protected Object getModelValueForControl(Control control,
			Object businessObject) {
		WorkformTask userTask = (WorkformTask) businessObject;

		userTask.getWorkformAttributes();

		if (control == formKeyText) {

			return userTask.getFormKey();

		} else if (control == urlText) {

			return userTask.getWorkformAttributes().get("url");

		}else if (control == paramText) {

			return userTask.getWorkformAttributes().get("param");

		}else if (control == assigneeText) {

			return userTask.getWorkformAttributes().get("assignee");

		}
		return null;
	}

	@Override
	protected void storeValueInModel(Control control, Object businessObject) {
		WorkformTask userTask = (WorkformTask) businessObject;
		if (control == formKeyText) {
			userTask.setFormKey(formKeyText.getText());

		}else if (control == urlText) {
			userTask.addWorkformAttribute("url", urlText.getText());
		}else if (control == paramText) {
			userTask.addWorkformAttribute("param", paramText.getText());
		}else if (control == assigneeText) {
			userTask.addWorkformAttribute("assignee", assigneeText.getText());
		}
	}
	
}
