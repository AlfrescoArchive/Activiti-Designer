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

public class ActivitiImportPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ActivitiImportPreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    Group group = new Group(getFieldEditorParent(), SWT.BORDER);
    group.setText("BPMN DI");
    group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

    addField(new BooleanFieldEditor(Preferences.IMPORT_USE_BPMNDI.getPreferenceId(),
            "&Use BPMN DI information when importing diagrams", group));
  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Set preferences used while importing BPMN 2.0 XML files");
    setTitle("Activiti Designer Import Preferences");
  }

}
