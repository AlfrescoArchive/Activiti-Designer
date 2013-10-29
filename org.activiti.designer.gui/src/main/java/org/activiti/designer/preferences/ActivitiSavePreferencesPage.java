package org.activiti.designer.preferences;

import java.util.Collection;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ActivitiSavePreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    getFieldEditorParent().setLayout(new FillLayout());

    addField(new BooleanFieldEditor(Preferences.SAVE_IMAGE.getPreferenceId(), "&Create process definition image when saving the diagram",
            getFieldEditorParent()));

    final Label exportMarshallersLabel = new Label(getFieldEditorParent(), SWT.HORIZONTAL);
    exportMarshallersLabel.setText("When saving diagrams, also save to the following formats:");

    final Collection<ExportMarshaller> marshallers = ExtensionPointUtil.getExportMarshallers();

    if (marshallers.size() > 0) {
      for (final ExportMarshaller exportMarshaller : marshallers) {
        addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(exportMarshaller), exportMarshaller.getFormatName(), getFieldEditorParent()));
      }
    }

  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Set preferences used while saving Activiti Diagrams");
    setTitle("Activiti Designer Save Preferences");
  }

}
