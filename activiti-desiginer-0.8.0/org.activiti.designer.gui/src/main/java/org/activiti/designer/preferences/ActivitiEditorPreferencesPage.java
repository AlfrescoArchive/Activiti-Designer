package org.activiti.designer.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.preferences.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiEditorPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiEditorPreferencesPage() {
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(Preferences.EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS.getPreferenceId(),
				"&Automatically create a label when adding a new sequence flow", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS.getPreferenceId(),
				"&Create default diagram content when creating new diagrams and subprocesses", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Set preferences used while editing Activiti Diagrams");
		setTitle("Activiti Designer Editor Preferences");
	}
}
