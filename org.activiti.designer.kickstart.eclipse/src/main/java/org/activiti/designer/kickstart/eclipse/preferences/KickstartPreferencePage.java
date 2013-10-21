package org.activiti.designer.kickstart.eclipse.preferences;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class KickstartPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public KickstartPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
	  getFieldEditorParent().setLayout(new FillLayout());

    addField(new StringFieldEditor(Preferences.CMIS_URL.getPreferenceId(), "&CMIS URL", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_USERNAME.getPreferenceId(), "&CMIS username", getFieldEditorParent()));
    addField(new StringFieldEditor(Preferences.CMIS_PASSWORD.getPreferenceId(), "&CMIS password", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = KickstartPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Kickstart settings");
    setTitle("Kickstart settings");
	}
}
