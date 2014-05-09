package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiCloudEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiCloudEditorPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
	  getFieldEditorParent().setLayout(new FillLayout());

    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_URL.getPreferenceId(), "&Activiti cloud editor URL", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME.getPreferenceId(), "&Activiti cloud editor username", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD.getPreferenceId(), "&Activiti cloud editor password", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Activiti cloud editor settings");
    setTitle("Activiti cloud editor settings");
	}
}
