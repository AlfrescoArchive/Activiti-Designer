package org.activiti.designer.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ActivitiSavePreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    Group group = new Group(getFieldEditorParent(), SWT.BORDER);
    group.setText("Export marshallers");
    group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

    addField(new BooleanFieldEditor(Preferences.SAVE_IMAGE.getPreferenceId(),
            "&Create process definition image when saving the diagram", group));
  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Set preferences used while saving Activiti Diagrams");
    setTitle("Activiti Designer Save Preferences");
  }

}
